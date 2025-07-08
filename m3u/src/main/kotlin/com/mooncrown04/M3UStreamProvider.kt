package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils

class M3UStreamProvider : MainAPI() {
    override var name = "M3U"
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/mooncrown34/master/m3u/resources/birlesik.m3u"
    override val hasMainPage = false
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun load(url: String): LoadResponse {
        val response = app.get(mainUrl).text
        val lines = response.lines()

        val episodes = ArrayList<Episode>()

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
                episodes.add(
                    Episode(
                        name = currentName,
                        url = streamUrl,
                        posterUrl = currentLogo
                    )
                )
            }
        }

        return TvSeriesLoadResponse(
            name = "M3U Yayınları",
            url = url,
            apiName = name,
            type = TvType.Live,
            episodes = episodes,
            posterUrl = null
        )
    }
}
