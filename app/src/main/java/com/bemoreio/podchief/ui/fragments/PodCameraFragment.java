package com.bemoreio.podchief.ui.fragments;

import android.net.Uri;
import android.os.Bundle;

import com.bemoreio.podchief.utils.Global;
import com.commonsware.cwac.cam2.CameraFragment;

/**
 * Created by Cody on 9/11/15.
 */
public class PodCameraFragment extends CameraFragment {

    public static PodCameraFragment newPictureInstance(Uri output, boolean updateMediaStore) {
        PodCameraFragment f = new PodCameraFragment();
        Bundle args = new Bundle();
        args.putParcelable("output", output);
        args.putBoolean("updateMediaStore", updateMediaStore);
        args.putBoolean("isVideo", false);
        f.setArguments(args);
        return f;
    }

    @Override
    protected void performCameraAction() {

        int screenOrientation = getResources().getConfiguration().orientation;
        Global.getInstance().setScreenOrientation(screenOrientation);

        super.performCameraAction();
    }
}
