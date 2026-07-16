package cab_booking.repository

// implementation of repository
// Generic in-memory repository implementation using a MutableMap.
abstract class InMemoryRepo<T> : RepositoryContract<T>{
    protected val storage = mutableMapOf<String, T>()

    protected abstract fun getKey(entity: T): String

    override fun save(entity: T) {
        val key = getKey(entity)
        storage[validateKey(key)] = entity
    }

    override fun findAll(): List<T> {
        return storage.values.toList() //returns copy as immutable list
    }

    override fun existsByKey(key: String): Boolean {
        return storage.containsKey(validateKey(key))
    }

    override fun findByKey(key: String): T?{
        return storage[validateKey(key)]
    }

    override fun deleteByKey(key: String) {
        storage.remove(validateKey(key))
    }

    private fun validateKey(key: String): String {
        require(key.isNotBlank()) { "Key cannot be blank." }
        return key.trim()
    }
}