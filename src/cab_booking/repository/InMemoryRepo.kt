package cab_booking.repository

abstract class InMemoryRepo<T> : Repository<T>{
    protected val storage = mutableMapOf<String, T>()

    protected abstract fun getKey(entity: T): String

    open override fun save(entity: T) {
        val key = getKey(entity)
        require(key.isNotBlank()){"Key cannot be blank"}
        storage[key.trim()] = entity
    }

    override fun findAll(): List<T> {
        return storage.values.toList()
    }

    override fun existsByKey(key: String): Boolean {
        require(key.isNotBlank()){"Key cannot be blank"}
        return storage.containsKey(key.trim())
    }

    override fun findByKey(key: String): T {
        return storage[key.trim()] ?: throw IllegalArgumentException("Record not found for key : $key")
    }

    override fun deleteByKey(key: String) {
        require(key.isNotBlank()){"Key cannot be blank"}
        if (storage.remove(key.trim()) == null) {
            throw IllegalArgumentException("Record not found for key: $key")
        }
    }
}