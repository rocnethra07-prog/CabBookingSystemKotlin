package cab_booking.repository

interface RepositoryContract<T> {
    fun save(entity: T)
    fun findByKey(key: String) : T?
    fun deleteByKey(key: String)
    fun existsByKey(key: String) : Boolean
    fun findAll() : List<T>
}