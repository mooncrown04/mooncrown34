package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.api.*
import java.net.URI

class M3UStreamProvider : MainAPI() {
    override var name = "M3U"
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/mooncrown34/master/m3u/resources/birlesik.m3u"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun load(url: String): LoadResponse {
        val channels = mutableListOf<LiveSearchResponse>()
        val response = app.get(mainUrl).text
        val lines = response.lines()

        var currentName: String? = null
        var currentLogo: String? = null

        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXTINF")) {
                val nameMatch = Regex("#EXTINF:-1.*?,(.*)").find(line)
                currentName = nameMatch?.groupValues?.get(1)?.trim() ?: "Bilinmeyen"

                val logoMatch = Regex("tvg-logo=\"(.*?)\"").find(line)
                currentLogo = logoMatch?.groupValues?.get(1)
            } else if (line.startsWith("http")) {
                val streamUrl = line.trim()
                currentName?.let { name ->
                    channels.add(
                        LiveSearchResponse(
                            name = name,
                            url = streamUrl,
                            apiName = this.name,
                            iconUrl = currentLogo,
                            source = this.name,
                            type = TvType.Live
                        )
                    )
                }
            }
        }

        return LiveStreamLoadResponse(
            name = "M3U Yayınları",
            dataUrl = url,
            streams = channels,
            type = TvType.Live
        )
    }
}
