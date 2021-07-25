package model

data class          Field(val row: Int, val column: Int) {
    private val neighbors = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var marked: Boolean = false
    var opened: Boolean = false
    var mined: Boolean = false

    val unmarked: Boolean get() = !marked
    val closed: Boolean get() = !opened
    val safe: Boolean get() = !mined
    val goalAchieved: Boolean get() = safe && opened || mined && marked
    val numberOfNeighborsMined: Int get() = neighbors.filter { it.mined }.size
    val safeNeighborhood: Boolean
        get() = neighbors.map { it.safe }.reduce { resultado, seguro -> resultado && seguro }

    fun addNeighbor(neighbor: Field) {
        neighbors.add(neighbor)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if (closed) {
            opened = true
            if (mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLOSION) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPENING) }
                neighbors.filter { it.closed && it.safe && it.safeNeighborhood }.forEach { it.open() }
            }
        }
    }

    fun changeMarking() {
        if (closed) {
            marked = !marked
            val event = if (marked) FieldEvent.MARKED else FieldEvent.UNMARKED
            callbacks.forEach { it(this, event) }
        }
    }

    fun setupMine() {
        mined = true
    }

    fun restart() {
        opened = false
        mined = false
        marked = false
        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}
