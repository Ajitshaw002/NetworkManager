package com.network.networkmanager


import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class HttpTest {

    @Mock
    lateinit var connection: HttpURLConnection

    @Mock
    lateinit var responseListener: ResponseListener

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testGetRequest() {
        val responseBody = "{\"key\": \"value\"}"
        val inputStream: InputStream = ByteArrayInputStream(responseBody.toByteArray())
        `when`(connection.responseCode).thenReturn(HttpURLConnection.HTTP_OK)
        `when`(connection.inputStream).thenReturn(inputStream)
        `when`(connection.errorStream).thenReturn(null)

        `when`(connection.requestMethod).thenReturn(Http.GET)
        `when`(connection.url).thenReturn(URL("http://example.com"))

        val http = Http.RequestCall(Http.GET)
            .url("http://example.com")
            .makeRequest(responseListener)

        //set the response
        http.sendResponse(Http.Response(responseBody.toByteArray()),null)
        //compare the response
        assertEquals(JSONObject(responseBody).toString(), http.getResponse().toString())
    }
}