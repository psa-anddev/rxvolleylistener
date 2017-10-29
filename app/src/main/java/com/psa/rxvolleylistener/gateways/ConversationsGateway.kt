package com.psa.rxvolleylistener.gateways

import com.psa.rxvolleylistener.entities.Conversation
import io.reactivex.Observable

/**
 * This gateway returns the conversations.
 *
 * @author Pablo Sánchez Alonso
 * @since 1.0
 */
interface ConversationsGateway {
    fun findAll() : Observable<Conversation>
}