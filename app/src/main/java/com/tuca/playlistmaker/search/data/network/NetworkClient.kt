package com.tuca.playlistmaker.search.data.network

import com.tuca.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}

