package com.psa.rxvolleylistener

import com.android.volley.Response.*
import com.android.volley.VolleyError
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * This is a listener to turn Volley Requests into an Rx Single.
 *
 * @author Pablo Sánchez Alonso
 * @since 1.0
 */
class RxVolleyListener<T> : Listener<T>, ErrorListener {
    private val resultSubject : Subject<T> = PublishSubject.create()
    val result : Single<T> = resultSubject.firstOrError()

    override fun onResponse(response: T) {
        resultSubject.onNext(response)
    }

    override fun onErrorResponse(error: VolleyError?) {
        resultSubject.onError(error?:Throwable("Request ended with null error"))
    }
}