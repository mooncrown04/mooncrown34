  package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.mainapi.*
import java.text.SimpleDateFormat
import java.util.*

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/Zerk1903/zerkfilm/refs/heads/main/Filmler.m3u" // kendi linkinle değiştir
    override var name = "M3U Stream"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Live)

    private val m3uUrl = "https://ornek-adres.com/playlist.m3u"

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(m3uUrl).text
        val channels = mutableListOf<LiveStream>()

        val regex = Regex("#EXTINF:-?1 .*?,(.*?)\\n(.*?)\\n")
        for (match in regex.findAll(response)) {
            val title = match.groupValues[1].trim()
            val streamUrl = match.groupValues[2].trim()
            channels.add(LiveStream(title, streamUrl))
        }

        return LiveStreamLoadResponse(
            name = "M3U Canlı Yayınlar",
            url = url,
            apiName = this.name,
            streams = channels
        )
    }
}
