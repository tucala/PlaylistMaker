package com.tuca.playlistmaker.search.data.network

import com.tuca.playlistmaker.search.data.dto.Response
import com.tuca.playlistmaker.search.data.dto.TrackResponse
import com.tuca.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(private val api: ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto !is TrackSearchRequest) {
            val errorResponse = Response()
            errorResponse.resultCode = 400
            return errorResponse
        }
        return try {
            val response = api.search(dto.expression).execute()
            val body = response.body() ?: Response()
            body.resultCode = response.code()
            body
        } catch (e: Exception) {
            val errorResponse = Response()
            errorResponse.resultCode = -1
            errorResponse
        } as Response
    }
}

