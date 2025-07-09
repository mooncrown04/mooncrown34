  package com.mooncrown04

import com.lagradost.cloudstream3.*
import java.text.SimpleDateFormat
import java.util.*

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u" // kendi linkinle değiştir
    override var name = "M3U Yayın"
    override val supportedTypes = setOf(TvType.Live)
    override val hasMainPage = false

    override suspend fun load(url: String): LoadResponse {
        val channelName = "Canlı Kanal"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr", "TR"))
        val formattedDate = sdf.format(Date())

        return LiveStream(
            name = "$channelName ($formattedDate)",
            url = url,
            referer = mainUrl
        )
    }
}

