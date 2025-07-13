package com.mooncrown04

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class M3UStreamProvider : MainAPI() {
    override var mainUrl = "https://raw.githubusercontent.com/Zerk1903/zerkfilm/refs/heads/main/Filmler.m3u"
    override var name = "M3U IPTV"
    override val supportedTypes = setOf(TvType.Live)

    override suspend fun load(): LoadResponse {
        return LiveStreamLoadResponse(
            name = "TR Yayın",
            url = "https://raw.githubusercontent.com/Zerk1903/zerkfilm/refs/heads/main/Filmler.m3u",
            referer = null,
            quality = Qualities.Unknown
        )
    }
}
