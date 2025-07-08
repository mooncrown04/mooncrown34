package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.parseM3u
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class M3UStreamProvider : MainAPI() {
    override var name = "M3UStream"
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u" // 👉 burayı kendi URL’inle değiştir
    override var supportedTypes = setOf(TvType.Live)

    // 1 hafta içinde eklenenlere [YENİ] etiketi ver
    private fun isNew(date: Long): Boolean {
        val now = System.currentTimeMillis()
        val diff = now - date
        return diff <= 7 * 24 * 60 * 60 * 1000 // 1 hafta
    }

    override suspend fun load(): LoadResponse {
        val m3uUrl = mainUrl
        val channels = ArrayList<LiveStream>()
        val m3uData = app.get(m3uUrl).text

        val parsed = parseM3u(m3uData)

        for (entry in parsed) {
            val name = entry.name ?: continue
            val url = entry.url ?: continue

            val addedAt = System.currentTimeMillis()
            val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
                .format(Date(addedAt))

            val groupRaw = entry.group ?: ""
            val sourceTag = getSourceName(url)
            val isNewTag = isNew(addedAt)

            val groupTitle = if (isNewTag) "[YENİ] [$sourceTag]" else {
                if (groupRaw.isNotBlank()) "$groupRaw [$sourceTag]" else sourceTag
            }

            val fullName = if (isNewTag) "$name ($dateStr)" else "$name [$dateStr]"

            val logo = entry.logo?.replace(",", "%2C") ?: ""

            channels.add(
                LiveStream(
                    name = fullName,
                    url = url,
                    icon = logo,
                    referer = null,
                    headers = null,
                    group = groupTitle
                )
            )
        }

        return LiveStreamLoadResponse(
            name = this.name,
            dataUrl = m3uUrl,
            streams = channels
        )
    }

    private fun getSourceName(url: String): String {
        return try {
            val host = Regex("https?://([^/]+)/").find(url)?.groupValues?.get(1) ?: url
            host.replace("www.", "")
        } catch (e: Exception) {
            "Kaynak"
        }
    }
}
