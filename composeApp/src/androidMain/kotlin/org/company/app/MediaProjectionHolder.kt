package org.company.app

import android.media.projection.MediaProjection

object MediaProjectionHolder {
    // This variable should be set once the user has granted permission.
    var mediaProjection: MediaProjection? = null
}
