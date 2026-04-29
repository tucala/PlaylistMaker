package com.tuca.playlistmaker.data.network

import com.tuca.playlistmaker.data.dto.Response
interface NetworkClient {
    fun doRequest(dto: Any): Response
}