package com.tuca.playlistmaker.data.network

import com.tuca.playlistmaker.data.dto.Response
import com.tuca.playlistmaker.data.dto.TrackSearchRequest

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