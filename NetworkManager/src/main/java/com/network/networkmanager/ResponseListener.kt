package com.network.networkmanager

import org.json.JSONObject

interface ResponseListener {
    fun onResponse(res: JSONObject?)
    fun onFailure(e: Exception?)
}