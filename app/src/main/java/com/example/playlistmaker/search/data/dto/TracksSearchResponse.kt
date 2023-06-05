package com.example.playlistmaker.search.data.dto

// класс описывающий ответ сервера
// наследуется от Response и соответственно имеет так же resultCode
class TracksSearchResponse(
    val results: ArrayList<TrackDto>
    ) : Response()