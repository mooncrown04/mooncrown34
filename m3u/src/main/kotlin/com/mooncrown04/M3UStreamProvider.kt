package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import java.net.URI

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://example.com" // Ana URL, gerektiğinde değiştirilebilir
    override var name = "M3U Stream"
    override val hasMainPage = false
    override val hasQuickSearch = false
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(TvType.Live)

    private val m3uUrl = "https://raw.githubusercontent.com/mooncrown04/mooncrown34/master/birlesik.m3u"

    override suspend fun load(url: String): LoadResponse {
        return LiveStreamLoadResponse(
            name = url,
            url = url,
            streamUrl = url,
            referer = null
        )
    }

    override suspend fun getMainPage(): HomePageResponse {
        return HomePageResponse(emptyList())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val playlist = app.get(m3uUrl).text
        val result = mutableListOf<SearchResponse>()

        val lines = playlist.lines()
        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null

        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXTINF")) {
                val nameRegex = Regex(",(.*)")
                val logoRegex = Regex("""tvg-logo="(.*?)"""")
                val groupRegex = Regex("""group-title="(.*?)"""")

                nameRegex.find(line)?.groupValues?.getOrNull(1)?.let { currentName = it }
                logoRegex.find(line)?.groupValues?.getOrNull(1)?.let { currentLogo = it }
                groupRegex.find(line)?.groupValues?.getOrNull(1)?.let { currentGroup = it }

                val nextLine = lines.getOrNull(i + 1)
                if (!nextLine.isNullOrBlank() && nextLine.startsWith("http")) {
                    val streamUrl = nextLine.trim()
                    if (currentName?.contains(query, true) == true) {
                        result.add(
                            LiveSearchResponse(
                                name = currentName ?: streamUrl,
                                url = streamUrl,
                                apiName = this.name,
                                logo = currentLogo,
                                tvType = TvType.Live
                            )
                        )
                    }
                }
            }
        }

        return result
    }
}
