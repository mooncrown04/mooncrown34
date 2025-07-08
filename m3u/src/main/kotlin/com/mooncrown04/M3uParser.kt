package com.mooncrown04

data class M3uEntry(
    val name: String?,
    val url: String?,
    val group: String?,
    val logo: String?
)

fun parseM3u(m3uContent: String): List<M3uEntry> {
    val entries = mutableListOf<M3uEntry>()
    val lines = m3uContent.lines()
    var currentName: String? = null
    var currentGroup: String? = null
    var currentLogo: String? = null

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.startsWith("#EXTINF:")) {
            val nameMatch = Regex(",(.*)$").find(trimmed)
            currentName = nameMatch?.groups?.get(1)?.value ?: ""

            val groupMatch = Regex("group-title=\"([^\"]+)\"").find(trimmed)
            currentGroup = groupMatch?.groups?.get(1)?.value

            val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(trimmed)
            currentLogo = logoMatch?.groups?.get(1)?.value
        } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            // URL satırı
            entries.add(M3uEntry(currentName, trimmed, currentGroup, currentLogo))
            currentName = null
            currentGroup = null
            currentLogo = null
        }
    }
    return entries
}
