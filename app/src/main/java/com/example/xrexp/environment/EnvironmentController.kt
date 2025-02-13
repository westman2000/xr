package com.example.xrexp.environment

import android.util.Log
import androidx.concurrent.futures.await
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialEnvironment
import com.example.xrexp.Main3DActivity.Companion.GLB_FILE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EnvironmentController(private val xrSession: Session, private val coroutineScope: CoroutineScope) {

    private val assetCache: HashMap<String, Any> = HashMap()
    private var activeEnvironmentModelName: String? = null

    fun requestHomeSpaceMode() = xrSession.spatialEnvironment.requestHomeSpaceMode()

    fun requestFullSpaceMode() = xrSession.spatialEnvironment.requestFullSpaceMode()

    fun requestPassthrough() = xrSession.spatialEnvironment.setPassthroughOpacityPreference(1f)

    /**
     * Request the system load a custom Environment
     */
    fun requestCustomEnvironment(environmentModelName: String) {
        coroutineScope.launch {
            try {

                if (activeEnvironmentModelName == null ||
                    activeEnvironmentModelName != environmentModelName){

                    val environmentModel = assetCache[environmentModelName] as GltfModel

                    SpatialEnvironment.SpatialEnvironmentPreference(
                        skybox = null,
                        geometry = environmentModel).let {
                        xrSession.spatialEnvironment.setSpatialEnvironmentPreference(
                            it
                        )
                    }

                    activeEnvironmentModelName = environmentModelName
                }
                xrSession.spatialEnvironment.setPassthroughOpacityPreference(0f)

            } catch (e: Exception){
                Log.e(
                    "Hello Android XR",
                    "Failed to update Environment Preference for $environmentModelName: $e")
            }

        }
    }

    fun loadModelAsset(modelName: String) {
        coroutineScope.launch {
            //load the asset if it hasn't been loaded previously
            if (!assetCache.containsKey(modelName)){
                try {
                    val gltfModel = GltfModel.create(xrSession, GLB_FILE_NAME).await()

                    assetCache[modelName] = gltfModel

                }catch (e: Exception) {
                    Log.e(
                        "Hello Android XR",
                        "Failed to load model for $modelName: $e")
                }
            }
        }
    }
}
