package com.syan1.pagingdemo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.uber.autodispose.lifecycle.CorrespondingEventsFunction
import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

open class BaseActivity : AppCompatActivity(), LifecycleScopeProvider<LifecycleEvent> {

    private val lifecycleSubject = BehaviorSubject.create<LifecycleEvent>()


    override fun lifecycle(): Observable<LifecycleEvent> {
        return lifecycleSubject
    }

    override fun correspondingEvents(): CorrespondingEventsFunction<LifecycleEvent> {
        return ProviderUtils.activityCorrespondingEvents()
    }

    override fun peekLifecycle(): LifecycleEvent? {
        return lifecycleSubject.value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(LifecycleEvent.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(LifecycleEvent.ON_START)
    }

    public override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(LifecycleEvent.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(LifecycleEvent.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(LifecycleEvent.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(LifecycleEvent.ON_DESTROY)
    }

    override fun finish() {
        peekLifecycle()?.apply {
            when (this) {
                LifecycleEvent.ON_CREATE, LifecycleEvent.ON_START, LifecycleEvent.ON_RESUME -> {
                    lifecycleSubject.onNext(LifecycleEvent.ON_PAUSE)
                    lifecycleSubject.onNext(LifecycleEvent.ON_STOP)
                    lifecycleSubject.onNext(LifecycleEvent.ON_DESTROY)
                }
                LifecycleEvent.ON_PAUSE -> {
                    lifecycleSubject.onNext(LifecycleEvent.ON_STOP)
                    lifecycleSubject.onNext(LifecycleEvent.ON_DESTROY)
                }
                LifecycleEvent.ON_STOP -> lifecycleSubject.onNext(LifecycleEvent.ON_DESTROY)
            }
        }
        super.finish()
    }
}