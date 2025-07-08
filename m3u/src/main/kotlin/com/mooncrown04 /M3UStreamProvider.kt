package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class M3UStreamProvider : MainAPI() {
    override var name = "M3U Stream"
    override var mainUrl = "https://example.com"
    override val supportedTypes = setOf(TvType.Live)
    override var lang = "tr"
    override val hasMainPage = true

    private val m3uUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u"

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val content = app.get(m3uUrl).text
        val channels = parseM3U(content)
        return newHomePageResponse(
            listOf(HomePageList("M3U Playlist", channels, isHorizontalImages = true)),
            hasNext = false
        )
    }

    private fun parseM3U(text: String): List<LiveSearchResponse> {
        val lines = text.split("\n")
        val result = mutableListOf<LiveSearchResponse>()

        var name: String? = null
        var logo: String? = null
        var group: String? = null

        for (i in lines.indices) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF")) {
                name = Regex("tvg-name=\"([^\"]+)\"").find(line)?.groupValues?.getOrNull(1)
                    ?: line.substringAfter(",").trim()
                logo = Regex("tvg-logo=\"([^\"]+)\"").find(line)?.groupValues?.getOrNull(1)
                group = Regex("group-title=\"([^\"]+)\"").find(line)?.groupValues?.getOrNull(1)
            } else if (line.startsWith("http")) {
                val url = line
                val finalName = name ?: "No Name"
                val finalLogo = logo ?: ""
                val finalGroup = group ?: "M3U"

                result.add(
                    newLiveSearchResponse(finalName, url, TvType.Live, fix = false) {
                        this.posterUrl = finalLogo
                        this.apiName = finalGroup
                    }
                )
            }
        }

        return result
    }

    override suspend fun load(url: String): LoadResponse {
        return newLiveStreamLoadResponse(
            name = url.substringAfterLast("/").substringBefore("."),
            url = url,
            streamUrl = url
        ) {
            posterUrl = null
            this.tags = listOf("M3U")
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(
            ExtractorLink(
                name,
                name,
                data,
                referer = null,
                quality = Qualities.Unknown,
                type = ExtractorLinkType.M3U8
            )
        )
        return true
    }
}
