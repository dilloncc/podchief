package com.bemoreio.podchief.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils
{
	private static final String LOG_TAG = ImageUtils.class.getName();

    public static final int MEDIA_TYPE_IMAGE_PNG = 1;
    public static final int MEDIA_TYPE_IMAGE_JPG = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;
    public static final int FILE_TYPE_PDF = 4;

	/**
	 * Convert a bitmap to a byte array
	 * @param bitmap
	 * @return
	 */
	public static byte[] toByteArray(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			return null;
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		return byteArray;
	}

    /**
     * Convert a file to a byte array
     * @param file
     * @return
     */
    public static byte[] toByteArray(File file)
    {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * Convert a file to a byte array
     * @param uri
     * @return
     */
    public static byte[] toByteArray(Uri uri)
    {
        return toByteArray(new File(uri.getPath()));
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputPdfFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PODChief");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE_PNG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        }
        else if (type == MEDIA_TYPE_IMAGE_JPG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        }
        else if (type == FILE_TYPE_PDF) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "POD_"+ timeStamp + ".pdf");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PODChief");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE_PNG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        }
        else if (type == MEDIA_TYPE_IMAGE_JPG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFileThumbnail(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PODChief");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE_PNG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + "_THUMB.png");
        }
        else if (type == MEDIA_TYPE_IMAGE_JPG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + "_THUMB.jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + "_THUMB.mp4");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    public static byte[] rotateAndScaleImage(String filePath, int targetWidth, int targetHeight)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        return toByteArray(bitmap);
    }

    public static void rotateAndScaleImage(File file, int targetWidth, int targetHeight)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        writeToFile(bitmap, file);
    }

    public static void rotateAndScaleImage(File inputFile, File outputFile, int targetWidth, int targetHeight)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath(), bmOptions);

        try {
            ExifInterface exif = new ExifInterface(inputFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        writeToFile(bitmap, outputFile);
    }

    public static void rotateAndScaleImage(File file)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        // Here previewRect is a rectangle which holds the camera's preview size,
// pictureRect and nativeResRect hold the camera's picture size and its
// native resolution, respectively.
        RectF previewRect = new RectF(0, 0, 480, 800),
                pictureRect = new RectF(0, 0, 1080, 1920),
                nativeResRect = new RectF(0, 0, 1952, 2592),
                resultRect = new RectF(0, 0, 480, 800);

        Matrix scaleMatrix = new Matrix();

// create a matrix which scales coordinates of preview size rectangle into the
// camera's native resolution.
        scaleMatrix.setRectToRect(previewRect, nativeResRect, Matrix.ScaleToFit.CENTER);

// map the result rectangle to the new coordinates
        scaleMatrix.mapRect(resultRect);

// create a matrix which scales coordinates of picture size rectangle into the
// camera's native resolution.
        scaleMatrix.setRectToRect(pictureRect, nativeResRect, Matrix.ScaleToFit.CENTER);

// invert it, so that we get the matrix which downscales the rectangle from
// the native resolution to the actual picture size
        scaleMatrix.invert(scaleMatrix);

// and map the result rectangle to the coordinates in the picture size rectangle
        scaleMatrix.mapRect(resultRect);

        try {
//            Rect dest = new Rect();
//            resultRect.roundOut(dest);
//
//            bitmap = BitmapRegionDecoder.newInstance(file.getAbsolutePath(), true).decodeRegion(dest, null);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

//        applyMatrix(bitmap, scaleMatrix);

        writeToFile(bitmap, file);
    }

    public static void rotateAndScaleImage(Bitmap bitmap, String filePath, int targetWidth, int targetHeight)
    {
        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        writeToFile(bitmap, new File(filePath));
    }

    public static Bitmap rotateAndScaleBitmap(File file, int targetWidth, int targetHeight)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        return bitmap;
    }

    public static int getRotation(String filePath) {

        int rotationDegrees;
        try {
            ExifInterface exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
                default:
                    rotationDegrees = 0;
                    break;
            }
        }
        catch (IOException e) {
            rotationDegrees = 0;
        }

        return rotationDegrees;
    }

    public static void rotateAndScaleImage(File file, int targetSize)
    {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = targetSize * bmOptions.outHeight / photoW;

        // Determine how much to scale down the image
        int scaleFactor = photoW / targetSize;//Math.min(photoW/targetSize, photoH/targetSize);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        writeToFile(bitmap, file);
    }

    public static void writeToFile(Bitmap bitmap, File file)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap bitmap, File file)
    {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        return bitmap;
    }

    public static Bitmap rotateImage(File file)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        try {

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        return bitmap;
    }

    public static Bitmap rotateImage(File file, int screenOrientation)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        try {

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            if (width > height && screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                orientation = ExifInterface.ORIENTATION_ROTATE_90;
            }

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    bitmap = rotateImage(bitmap, 0);
                    break;
            }
        }
        catch (IOException e) {

        }

        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Matrix getRotation(Matrix matrix, float angle)
    {
        matrix.postRotate(angle);
        return matrix;
    }

    public static Bitmap applyMatrix(String imagePath, Bitmap source, Matrix matrix) {
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static File saveImage(File file, byte[] data)
    {
        try {
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(data);
            fos.close();
        }
        catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return file;
    }

    /**
     * Copy the file to a temporary local file
     * @param context
     * @param resolver
     * @param uri
     * @return
     */
    public static File getFromMediaUriPfd(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            File outputFile = getOutputMediaFile(MEDIA_TYPE_IMAGE_JPG);

            output = new FileOutputStream(outputFile);

            int read = 0;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return outputFile;
        } catch (IOException ignored) {
            // nothing we can do
        } finally {
            if (input != null) try { input.close(); } catch(Exception ignored) {}
            if (output != null) try { output.close(); } catch(Exception ignored) {}
        }
        return null;
    }

//    public static void invalidateCache(Context context, String fileName) {
//        Picasso.with(context).invalidate(new File(fileName));
//    }
}
