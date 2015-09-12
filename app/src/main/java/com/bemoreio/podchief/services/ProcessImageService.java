package com.bemoreio.podchief.services;

import android.graphics.Bitmap;

import com.bemoreio.podchief.utils.ImageUtils;
import com.joanzapata.android.asyncservice.api.annotation.AsyncService;

import java.io.File;

/**
 * Created by Cody on 9/4/15.
 */
@AsyncService
public class ProcessImageService {

    public String run(Bitmap bitmap, String imagePath, int width, int height) {
        ImageUtils.rotateAndScaleImage(bitmap, imagePath, width, height);

        return imagePath;
    }

    public String run(String imagePath, int width, int height) {
        ImageUtils.rotateAndScaleImage(new File(imagePath), width, height);

        return imagePath;
    }

    public String run(String inputFile, String outputFile, int width, int height) {
        ImageUtils.rotateAndScaleImage(new File(inputFile), new File(outputFile), width, height);

        return outputFile;
    }

    public String run(String imagePath) {
        ImageUtils.rotateAndScaleImage(new File(imagePath));

        return imagePath;
    }

    public Bitmap process(Bitmap bitmap, String imagePath) {
        return ImageUtils.rotateImage(bitmap, new File(imagePath));
    }

    public Bitmap process(String imagePath) {
        return ImageUtils.rotateImage(new File(imagePath));
    }

    public Bitmap process(String imagePath, int screenOrientation) {
        return ImageUtils.rotateImage(new File(imagePath), screenOrientation);
    }
}
