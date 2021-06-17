package com.jansir.kglide.load.data

import androidx.core.util.Preconditions
import java.util.*

class DataRewinderRegistry {
    private val rewinders =
        HashMap<Class<*>, DataRewinder.Factory<*>>()

    @Synchronized
    fun register(factory: DataRewinder.Factory<*>) {
        rewinders[factory.getDataClass()] = factory
    }

    @Synchronized
    fun <T : Any> build(data: T): DataRewinder<T> {

        var result =
            rewinders[data.javaClass] as DataRewinder.Factory<T>?
        if (result == null) {
            for (registeredFactory in rewinders.values) {
                if (registeredFactory.getDataClass().isAssignableFrom(data.javaClass)) {
                    result = registeredFactory as DataRewinder.Factory<T>
                    break
                }
            }
        }
//        if (result == null) {
//            result = DataRewinderRegistry.DEFAULT_FACTORY as DataRewinder.Factory<T>?
//        }
        return result!!.build(data)
    }
}