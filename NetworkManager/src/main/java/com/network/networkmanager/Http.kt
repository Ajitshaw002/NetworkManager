package com.network.networkmanager

import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

/* Http is the Builder design class which is used for to handle all network related calls
* The Http object provides methods for making HTTP requests (GET, POST, DELETE, PUT).
* The RequestCall class is used to configure the HTTP request, including the URL, request method, headers, and request body.
* The makeRequest method initiates the HTTP request asynchronously using a separate thread.
* The RequestTask class represents the task of making an HTTP request. It's responsible for opening a connection, sending the request, and handling the response.
* The Response class encapsulates the response data and provides methods for converting it to a JSON object.*/

object Http {

    const val GET = "GET"
    const val POST = "POST"
    const val DELETE = "DELETE"
    const val PUT = "PUT"

    class RequestCall(internal val method: String) {
        internal val header: MutableMap<String, String> = HashMap()
        internal var url: String? = null
        internal var body: ByteArray? = null
         var response: JSONObject? =null
        var jsonObjReqListener: ResponseListener? = null
        private var threadController: ThreadController = ThreadController()

        fun url(url: String?): RequestCall {
            this.url = url
            return this
        }

        fun body(bodyJson: JSONObject?): RequestCall {
            val textBody = bodyJson?.toString()
            body = textBody?.toByteArray(charset(UTF_8.toString()))
            this.header["Content-Type"] = "application/json"
            return this
        }

        fun header(header: Map<String, String>?): RequestCall {
            if (header.isNullOrEmpty()) return this
            this.header.putAll(header)
            return this
        }

        fun makeRequest(responseListener: ResponseListener?): RequestCall {
            this.jsonObjReqListener = responseListener
            threadController.execute(RequestTask(this))
            return this
        }

        internal fun sendResponse(resp: Response?, e: Exception?) {
            if (jsonObjReqListener != null) {
                if (e != null) jsonObjReqListener?.onFailure(e)
                else jsonObjReqListener?.onResponse(resp?.asJSONObject())
                response = resp?.asJSONObject()
            }
        }
        internal fun getResponse(): JSONObject? {
            return response
        }
    }

    internal class RequestTask(private val req: RequestCall) : Runnable {
        override fun run() {
            try {
                val conn = request()
                val parsedResponse = parseResponse(conn)
                req.sendResponse(parsedResponse, null)
            } catch (e: IOException) {
                req.sendResponse(null, e)
            }
        }

        @Throws(IOException::class)
        private fun request(): HttpURLConnection {
            val url = URL(req.url)
            val conn = url.openConnection() as HttpURLConnection
            val method = req.method
            conn.requestMethod = method
            for ((key, value) in req.header) {
                conn.setRequestProperty(key, value)
            }
            if (req.body != null) {
                val outputStream = conn.outputStream
                outputStream.write(req.body)
            }
            conn.connect()
            return conn
        }

        @Throws(IOException::class)
        private fun parseResponse(conn: HttpURLConnection): Response {
            try {
                val bos = ByteArrayOutputStream()
                val status = conn.responseCode
                val validStatus = status in 200..299
                val inpStream = if (validStatus) conn.inputStream else conn.errorStream
                var read: Int
                var totalRead = 0
                val buf = ByteArray(8192)
                while (inpStream.read(buf).also { read = it } != -1) {
                    bos.write(buf, 0, read)
                    totalRead += read
                }
                return Response(bos.toByteArray())
            } finally {
                conn.disconnect()
            }
        }
    }

    class Response(private val data: ByteArray) {
        @Throws(JSONException::class)
        fun asJSONObject(): JSONObject {
            val str = String(data, StandardCharsets.UTF_8)
            return if (str.isEmpty()) JSONObject() else JSONObject(str)
        }
    }
}