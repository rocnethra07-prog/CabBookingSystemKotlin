package cab_booking.repository

import cab_booking.exception.CabBookingException

abstract class InMemoryRepo<T> : Repository<T>{
    protected val storage = mutableMapOf<String, T>()

    protected abstract fun getKey(entity: T): String

    override fun save(entity: T) {
        val key = getKey(entity)
        if(key.isBlank()){
            throw CabBookingException("Key cannot be blank")
        }
        storage[key.trim()] = entity
    }

    override fun findAll(): List<T> {
        return storage.values.toList() //returns copy as immutable list
    }

    override fun existsByKey(key: String): Boolean {
        if(key.isBlank()){
            throw CabBookingException("Key cannot be blank")
        }
        return storage.containsKey(key.trim())
    }


    override fun findByKey(key: String): T {
        return storage[key.trim()] ?: throw CabBookingException("Record not found for key: $key")
    }

    override fun deleteByKey(key: String) {
        require(key.isBlank()){
            throw CabBookingException("Key cannot be blank")
        }
        if (storage.remove(key.trim()) == null) {
            throw CabBookingException("Record not found for key: $key")
        }
    }
}