package com.psa.rxvolleylistener

import com.android.volley.Response.*
import com.android.volley.VolleyError
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.should
import io.kotlintest.properties.forAll
import io.kotlintest.specs.ShouldSpec
import org.json.JSONObject

class RxVolleyListenerSpec : ShouldSpec({
    should("be an error listener") {
        RxVolleyListener<Any>() should beInstanceOf(ErrorListener::class)
    }

    should("be a response listener") {
        RxVolleyListener<Any>() should beInstanceOf(Listener::class)
    }

    should("return the value for the response") {
        forAll {
            response : String ->
            val listener = RxVolleyListener<String>()
            val observer = listener.result.test()
            listener.onResponse(response)
            observer.assertValue(response)
            observer.assertComplete()
            true
        }
    }

    should("return the error") {
        val error = VolleyError()
        val listener = RxVolleyListener<JSONObject>()
        val observer = listener.result.test()
        listener.onErrorResponse(error)
        observer.assertError(error)
    }

    should("return an empty throwable when the error is null") {
        val listener = RxVolleyListener<String>()
        val observer = listener.result.test()
        listener.onErrorResponse(null)
        observer.assertError { it.message == "Request ended with null error" }
    }
})
