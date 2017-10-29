package com.psa.rxvolleylistener.generators

import io.kotlintest.properties.Gen
import org.json.JSONArray
import org.json.JSONObject

/**
 * This class generates JSON objects to be used as responses for
 * the conversations gateway.
 *
 * @author Pablo Sánchez Alonso
 * @since 1.0
 */
class ConversationsResponseGenerator : Gen<JSONArray> {
    override fun generate(): JSONArray {
        val count = Gen.choose(10, 100).generate()
        val result = JSONArray()
        (1..count).forEach {
            val conversation = JSONObject()
            conversation.put("title", Gen.string().generate())
            conversation.put("messages", Gen.choose(0L, 2000L).generate())
            result.put(conversation)
        }
        return  result
    }
}