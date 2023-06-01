package app.revanced.manager.compose.domain.repository

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.revanced.manager.compose.patcher.patch.PatchBundle
import app.revanced.manager.compose.util.combineFlowMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BundleRepository(private val sourceRepository: SourceRepository) {
    /**
     * A [Flow] that gives you all loaded [PatchBundle]s.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val bundles = sourceRepository.sources.flatMapLatest { sources ->
        sources.mapValues { (_, source) -> source.bundle }.combineFlowMap()
    }

    fun onAppStart(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            // TODO: this does not belong here.
            sourceRepository.loadSources()
        }
    }
}