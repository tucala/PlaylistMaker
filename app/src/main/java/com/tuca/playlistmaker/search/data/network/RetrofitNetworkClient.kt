package com.tuca.playlistmaker.search.data.network

import com.tuca.playlistmaker.search.data.dto.Response
import com.tuca.playlistmaker.search.data.dto.TrackResponse
import com.tuca.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val api: ITunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TrackSearchRequest) {
            val errorResponse = Response()
            errorResponse.resultCode = 400
            return errorResponse
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = api.search(dto.expression)
                val body = response.body() ?: Response()
                body.resultCode = response.code()
                body
            } catch (e: Exception) {
                android.util.Log.e("RetrofitNetworkClient", "doRequest exception: ${e.message}", e)
                val errorResponse = Response()
                errorResponse.resultCode = -1
                errorResponse
            }
        }
    }
}

