package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.domain.*
import com.lagradost.cloudstream3.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class M3UStreamProvider : MainAPI() {
    override val name = "M3U Provider"

    override val mainUrl = "https://raw.githubusercontent.com/mooncrown04/mooncrown34/master/m3u/resources/birlesik.m3u"

    override val hasMainPage = false

    override suspend fun getVideoList(): List<VideoInfo> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoInfo>()

        val response = app.get(mainUrl)
        val body = response.text

        val lines = body.lines()
        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXTINF")) {
                val title = line.substringAfter(",").trim()
                if (i + 1 < lines.size) {
                    val url = lines[i + 1].trim()
                    if (url.isNotEmpty() && !url.startsWith("#")) {
                        videoList.add(VideoInfo(title, url))
                    }
                }
            }
        }
        videoList
    }

    override suspend fun load(url: String): VideoLoadResponse {
        return VideoLoadResponse(url)
    }
}
