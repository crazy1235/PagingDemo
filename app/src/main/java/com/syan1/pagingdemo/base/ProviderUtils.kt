package com.syan1.pagingdemo.base

import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleEndedException

object ProviderUtils {

    fun activityCorrespondingEvents(): CorrespondingEventsFunction<LifecycleEvent> {
        return CorrespondingEventsFunction { event: LifecycleEvent? ->
            when (event) {
                LifecycleEvent.ON_CREATE -> LifecycleEvent.ON_DESTROY
                LifecycleEvent.ON_START -> LifecycleEvent.ON_STOP
                LifecycleEvent.ON_RESUME -> LifecycleEvent.ON_PAUSE
                LifecycleEvent.ON_PAUSE -> LifecycleEvent.ON_STOP
                LifecycleEvent.ON_STOP -> LifecycleEvent.ON_DESTROY
                else -> throw LifecycleEndedException("Cannot bind outside lifecycle.")
            }
        }
    }

}