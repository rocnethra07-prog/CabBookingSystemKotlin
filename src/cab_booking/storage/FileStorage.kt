package cab_booking.storage

interface FileStorage<T> {
    fun save(items: List<T>)
    fun load(): List<T>
}
