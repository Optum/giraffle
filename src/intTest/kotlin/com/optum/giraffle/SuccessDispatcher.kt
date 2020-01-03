package com.optum.giraffle

import com.optum.giraffle.data.GsqlTokenSerializable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class SuccessDispatcher() : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        val errorResponse = MockResponse().setResponseCode(404)

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val responseJson = GsqlTokenSerializable("REST-0000", 1580254963, false, "Generate new token successfully.", "o9fhgnc3dm9glac9e072uc6qhb0hibs6")
        val jsonAdapter: JsonAdapter<GsqlTokenSerializable> = moshi.adapter(GsqlTokenSerializable::class.java)

        val pathWithoutQueryParams = request.requestUrl?.encodedPath ?: return errorResponse

        when (pathWithoutQueryParams) {
            "/requesttoken" -> return MockResponse()
                .setResponseCode(200)
                .setBody(jsonAdapter.toJson(responseJson))
        }
        return errorResponse
    }
}
