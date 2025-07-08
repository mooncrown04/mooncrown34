package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.extractors.*
import com.lagradost.cloudstream3.utils.*
import java.net.URI

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u"
    override var name = "M3U"
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun load(apiName: String, dataUrl: String): Boolean {
        val res = app.get(dataUrl).text
        val lines = res.split("#EXTINF")
        for (line in lines.drop(1)) {
            val url = line.substringAfter("\n").substringBefore("\n").trim()
            val name = line.substringBefore("\n").substringAfter(",").trim()

            val logo = Regex("tvg-logo=\"(.*?)\"").find(line)?.groupValues?.getOrNull(1)
            val group = Regex("group-title=\"(.*?)\"").find(line)?.groupValues?.getOrNull(1)
            val referer = getRefererFromUrl(url)

            callback(
                LiveSearchResponse(
                    name = name,
                    url = fixUrl(url),
                    apiName = this.name,
                    type = TvType.Live,
                    iconUrl = logo,
                    referer = referer,
                    quality = getQualityFromName(name),
                    headers = mapOf("User-Agent" to USER_AGENT)
                )
            )
        }
        return true
    }

    private fun getRefererFromUrl(url: String): String? {
        return try {
            URI(url).host?.let { "https://$it" }
        } catch (e: Exception) {
            null
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
                name = "M3U Link"
                source = "m3u"
                url = data
                isM3u8 = true
                quality = Qualities.Unknown.value
                headers = mapOf("User-Agent" to USER_AGENT)
            }
        )
        return true
    }

    companion object {
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
    }
}
