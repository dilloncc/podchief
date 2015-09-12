package com.bemoreio.podchief.ui.activities;

import android.content.Context;

import com.bemoreio.podchief.ui.fragments.PodCameraFragment;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.ImageContext;

/**
 * Created by Cody on 9/11/15.
 */
public class PodCameraActivity extends CameraActivity {

    @Override
    protected PodCameraFragment buildFragment() {
        return PodCameraFragment.newPictureInstance(this.getOutputUri(), this.getIntent().getBooleanExtra("cwac_cam2_update_media_store", false));
    }

    @Override
    public void completeRequest(ImageContext imageContext, boolean isOK) {
        super.completeRequest(imageContext, isOK);
    }

    public static class IntentBuilder extends com.commonsware.cwac.cam2.AbstractCameraActivity.IntentBuilder {
        public IntentBuilder(Context ctxt) {
            super(ctxt, PodCameraActivity.class);
        }

        public PodCameraActivity.IntentBuilder skipConfirm() {
            this.result.putExtra("cwac_cam2_confirm", false);
            return this;
        }
    }
}
