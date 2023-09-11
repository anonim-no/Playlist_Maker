package com.example.playlistmaker.search.data.dto

// это модель, которую мы будем использовать при получении данных с сервера
// вместо модели, живущего в Domain-слое в которой содержится вся информация для отображения
class TrackDto(
    val trackId: Int, // уникальный идентификатор трека
    val trackName: String, // Название композиции
    val artistName: String?, // Имя исполнителя
    val trackTimeMillis: Long?, // Продолжительность трека
    val artworkUrl60: String?, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String?, // Год релиза трека
    val primaryGenreName: String?, // Жанр трека
    val country: String?, // Страна исполнителя
    val previewUrl: String?, // Ссылка на отрывок
)