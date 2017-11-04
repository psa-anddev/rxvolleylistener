package com.psa.rxvolleylistener.gateways

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.psa.rxvolleylistener.RxVolleyListener
import com.psa.rxvolleylistener.entities.Conversation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

/**
 * This is the implementation of the conversations gateway
 * using Volley.
 *
 * @author Pablo Sánchez Alonso
 * @since 1.0
 */
class VolleyConversationsGateway(private val queue: RequestQueue,
                                 private val baseUrl: String)
    : ConversationsGateway {

    override fun findAll(): Observable<Conversation> {

        return Single.fromCallable {
            val listener = RxVolleyListener<JSONArray>()
            queue.add(JsonArrayRequest(Request.Method.GET, baseUrl,
                    JSONArray(), listener, listener))
            listener
        }.subscribeOn(Schedulers.io())
                .flatMap { it.result }
                .flatMap { response ->
                    Single.fromCallable {
                        val list = mutableListOf<JSONObject>()
                        (0 until response.length()).forEach { list += response.getJSONObject(it) }
                        list as List<JSONObject>
                    }
                }
                .flatMapObservable { Observable.fromIterable(it) }
                .map { Conversation(it.getString("title"), it.getLong("messages")) }
    }

}