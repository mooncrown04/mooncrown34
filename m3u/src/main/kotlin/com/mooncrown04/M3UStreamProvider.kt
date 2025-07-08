package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u"
    override var name = "M3U"
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun load(url: String): HomePageResponse {
        val response = app.get(url).text
        val lines = response.split("#EXTINF").drop(1)

        val liveList = lines.mapNotNull { line ->
            val streamUrl = line.substringAfter("\n").substringBefore("\n").trim()
            val title = line.substringBefore("\n").substringAfter(",").trim()

            val logo = Regex("tvg-logo=\"(.*?)\"").find(line)?.groupValues?.getOrNull(1)
            val group = Regex("group-title=\"(.*?)\"").find(line)?.groupValues?.getOrNull(1) ?: "M3U"

            newTvSeriesSearchResponse(title, streamUrl, TvType.Live) {
                this.posterUrl = logo
            }
        }

        return HomePageResponse(
            listOf(HomePageList(name = "M3U Channels", list = liveList))
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback(
            ExtractorLink(
                name = "M3U",
                source = "M3U",
                url = data,
                referer = getRefererFromUrl(data),
                quality = Qualities.Unknown.value,
                isM3u8 = true,
                headers = mapOf("User-Agent" to USER_AGENT)
            )
        )
        return true
    }

    private fun getRefererFromUrl(url: String): String? {
        return try {
            java.net.URI(url).host?.let { "https://$it" }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
    }
}
