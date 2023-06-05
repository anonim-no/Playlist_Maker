package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.Response

// Интерфейс предполагает, что некоторому сетевому клиенту будут переданы параметры запроса
// в виде экземпляра класса из пакета dto
// после чего клиент должен вернуть какой-то ответ.
interface NetworkClient {
    fun doRequest(dto: Any): Response
}