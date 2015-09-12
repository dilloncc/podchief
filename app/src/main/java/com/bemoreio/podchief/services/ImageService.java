package com.bemoreio.podchief.services;

import android.graphics.Bitmap;

import com.joanzapata.android.asyncservice.api.annotation.AsyncService;
import com.scanlibrary.Utils;

import java.io.File;

/**
 * Created by Cody on 9/9/15.
 */
@AsyncService
public class ImageService {
    public File writeToFile(Bitmap bitmap) {

        File file = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE_PNG);
        Utils.writeToFile(bitmap, file);

        bitmap.recycle();

        return file;
    }
}
