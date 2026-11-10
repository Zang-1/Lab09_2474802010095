package vn.edu.vlu.lab10

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * The app's Glide module. KSP (Glide annotation processor) generates GeneratedAppGlideModule from it,
 * so Glide starts with the app's configuration instead of logging "Failed to find GeneratedAppGlideModule".
 * Defaults are kept; settings such as the memory or disk cache size would go in applyOptions().
 */
@GlideModule
class LabGlideModule : AppGlideModule()
