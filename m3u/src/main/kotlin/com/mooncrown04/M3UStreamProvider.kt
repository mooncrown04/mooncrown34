package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import java.net.URI

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u"
    override var name = "M3U"
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val response = app.get(mainUrl).text
        val lines = response.split("#EXTINF").drop(1)

        val items = lines.mapNotNull { line ->
            val streamUrl = line.substringAfter("\n").substringBefore("\n").trim()
            val title = line.substringBefore("\n").substringAfter(",").trim()
            val logo = Regex("""tvg-logo="(.*?)"""").find(line)?.groupValues?.getOrNull(1)

            newTvSeriesSearchResponse(title, streamUrl, TvType.Live) {
                posterUrl = logo
            }
        }

        return HomePageResponse(listOf(HomePageList("M3U Channels", items)))
    }

    override suspend fun load(url: String): LoadResponse {
        return newTvSeriesLoadResponse(name = url, url = url, type = TvType.Live) {
            episodes = listOf(
                Episode(
                    data = url,
                    name = "Canlı Yayın"
                )
            )
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback(
            newExtractorLink {
                this.source = "M3U"
                this.name = "Canlı"
                this.url = data
                this.referer = getRefererFromUrl(data) ?: ""
                this.quality = Qualities.Unknown.value
                this.isM3u8 = true
                this.headers = mapOf("User-Agent" to USER_AGENT)
            }
        )
        return true
    }

    private fun getRefererFromUrl(url: String): String? {
        return try {
            URI(url).host?.let { "https://$it" }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
    }
}
