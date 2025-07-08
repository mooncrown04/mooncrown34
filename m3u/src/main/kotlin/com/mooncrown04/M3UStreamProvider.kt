package com.mooncrown04

import com.lagradost.cloudstream3.*
import java.text.SimpleDateFormat
import java.util.*
import com.mooncrown04.parseM3u

class M3UStreamProvider : MainAPI() {
    override var name = "M3UStream"
    override var mainUrl = "https://raw.githubusercontent.com/mooncrown04/m3u/refs/heads/main/birlesik.m3u"
    override var supportedTypes = setOf(TvType.Live)

    private fun isNew(date: Long): Boolean {
        val now = System.currentTimeMillis()
        val diff = now - date
        return diff <= 7 * 24 * 60 * 60 * 1000 // 1 hafta
    }

    override suspend fun load(): LoadResponse {
        val m3uData = app.get(mainUrl).text
        val parsed = parseM3u(m3uData)
        val channels = ArrayList<LiveStream>()

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))

        for (entry in parsed) {
            val name = entry.name ?: continue
            val url = entry.url ?: continue
            val addedAt = System.currentTimeMillis()

            val groupRaw = entry.group ?: ""
            val sourceTag = getSourceName(url)
            val isNewTag = isNew(addedAt)

            val groupTitle = if (isNewTag) "[YENİ] [$sourceTag]" else {
                if (groupRaw.isNotBlank()) "$groupRaw [$sourceTag]" else sourceTag
            }

            val fullName = if (isNewTag) "$name (${dateFormat.format(Date(addedAt))})" else "$name [${dateFormat.format(Date(addedAt))}]"

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

        return LiveStreamLoadResponse(name, mainUrl, channels)
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
