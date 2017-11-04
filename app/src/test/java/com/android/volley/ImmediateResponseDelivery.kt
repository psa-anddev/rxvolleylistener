package com.android.volley

/**
 * Allows to remove the need for executors in Robolectric tests.
 *
 * @author Pablo Sánchez Alonso
 * @since 1.0
 */
class ImmediateResponseDelivery : ResponseDelivery {
    override fun postResponse(request: Request<*>, response: Response<*>) {
        postResponse(request, response, null)
    }

    @Suppress("UNCHECKED_CAST")
    override fun postResponse(request: Request<*>, response: Response<*>, runnable: Runnable?) {
        request.markDelivered()
        request.addMarker("post-response")
        if (request.isCanceled)
            request.finish("cancelled-at-delivery")
        else {
            if (response.isSuccess)
                (request as Request<Any?>).deliverResponse(response.result)
            else
                request.deliverError(response.error)

            if (response.intermediate)
                request.addMarker("intermediate-response")
            else
                request.finish("done")

            runnable?.run()
        }
    }

    override fun postError(request: Request<*>?, error: VolleyError?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}