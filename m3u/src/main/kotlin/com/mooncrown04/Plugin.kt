package com.mooncrown04

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class M3UPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(M3UStreamProvider())
    }
}
