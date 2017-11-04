package com.psa.rxvolleylistener.gateways

import com.android.volley.ImmediateResponseDelivery
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import com.psa.rxvolleylistener.entities.Conversation
import com.psa.rxvolleylistener.generators.ConversationsResponseGenerator
import com.psa.rxvolleylistener.sample.BuildConfig
import io.kotlintest.properties.forAll
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.httpclient.FakeHttp

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        sdk = intArrayOf(26))
class VolleyConversationsGatewayTest {
    companion object {
        @JvmField val server = MockWebServer()
        @JvmField val ioScheduler = TestScheduler()

        @BeforeClass @JvmStatic
        fun setupClass() {
            RxJavaPlugins.setIoSchedulerHandler { ioScheduler }
            server.start()
        }

        @AfterClass @JvmStatic
        fun tearDownClass() {
            server.shutdown()
            RxJavaPlugins.reset()
        }
    }

    private val queue  = RequestQueue(NoCache(),
            BasicNetwork(HurlStack()),
            4,
            ImmediateResponseDelivery())

    private val gateway : ConversationsGateway =
            VolleyConversationsGateway(queue,
                    server.url("/").toString())

    @Before
    fun setUp() {
        FakeHttp.getFakeHttpLayer().interceptHttpRequests(false)
        queue.start()
    }

    @Test
    fun `should return conversations from server`() {
        forAll(ConversationsResponseGenerator()) {
            response ->
            server.enqueue(MockResponse().setBody(response.toString()))
            val expectedConversations by lazy {
                val conversations = mutableListOf<Conversation>()
                (0 until response.length()).forEach {
                    val conversationObject = response.getJSONObject(it)
                    val conversation = Conversation(conversationObject.getString("title"),
                            conversationObject.getLong("messages"))
                    conversations += conversation
                }
                conversations
            }

            val observer = gateway.findAll().test()
            ioScheduler.triggerActions()
            server.takeRequest()
            observer.awaitTerminalEvent()
            observer.assertValues(*expectedConversations.toTypedArray())
            true
        }
    }
}