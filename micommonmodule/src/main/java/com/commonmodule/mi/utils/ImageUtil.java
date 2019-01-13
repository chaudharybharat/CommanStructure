package com.commonmodule.mi.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by mind on 2/10/15.
 */
public class ImageUtil {

    private final static String TAG = ""+ImageUtil.class.getSimpleName().toString();

    private static final String ERROR_URI_NULL = "Uri cannot be null";

    /***
     * Scales the image depending upon the display density of the device. Maintains image aspect
     * ratio.
     *
     * When dealing with the bitmaps of bigger size, this method must be called from a non-UI
     * thread.
     * ***/
    public static Bitmap scaleDownBitmap(Context ctx, Bitmap source, int newHeight) {
        final float densityMultiplier = MiUtil.getDensityMultiplier(ctx);

        // Log.v( TAG, "#scaleDownBitmap Original w: " + source.getWidth() + " h: " +
        // source.getHeight() );

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * source.getWidth() / ((double) source.getHeight()));

        // Log.v( TAG, "#scaleDownBitmap Computed w: " + w + " h: " + h );

        Bitmap photo = Bitmap.createScaledBitmap(source, w, h, true);

        // Log.v( TAG, "#scaleDownBitmap Final w: " + w + " h: " + h );

        return photo;
    }

    /***
     * Scales the image independently of the screen density of the device. Maintains image aspect
     * ratio.
     *
     * When dealing with the bitmaps of bigger size, this method must be called from a non-UI
     * thread.
     * ***/
    public static Bitmap scaleBitmap(Context ctx, Bitmap source, int newHeight) {

        // Log.v( TAG, "#scaleDownBitmap Original w: " + source.getWidth() + " h: " +
        // source.getHeight() );

        int w = (int) (newHeight * source.getWidth() / ((double) source.getHeight()));

        // Log.v( TAG, "#scaleDownBitmap Computed w: " + w + " h: " + newHeight );

        Bitmap photo = Bitmap.createScaledBitmap(source, w, newHeight, true);

        // Log.v( TAG, "#scaleDownBitmap Final w: " + w + " h: " + newHeight );

        return photo;
    }

    /***
     * Scales the image independently of the screen density of the device. Maintains image aspect
     * ratio.
     *
     * @param uri
     *            Uri of the source bitmap
     ****/
    public static Bitmap scaleDownBitmap(Context ctx, Uri uri, int newHeight) throws FileNotFoundException, IOException {
        Bitmap original = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
        return scaleBitmap(ctx, original, newHeight);
    }

    /***
     * Scales the image independently of the screen density of the device. Maintains image aspect
     * ratio.
     *
     * @param uri
     *            Uri of the source bitmap
     ****/
    public static Uri scaleDownBitmapForUri(Context ctx, Uri uri, int newHeight) throws FileNotFoundException, IOException {

        if (uri == null)
            throw new NullPointerException(ERROR_URI_NULL);

        if (!isMediaContentUri(uri))
            return null;

        Bitmap original = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), uri);
        Bitmap bmp = scaleBitmap(ctx, original, newHeight);

        Uri destUri = null;
        String uriStr = ImageUtil.writeImageToMedia(ctx, bmp, "", "");

        if (uriStr != null) {
            destUri = Uri.parse(uriStr);
        }

        return destUri;
    }

    /***
     * Gets the orientation of the image pointed to by the parameter uri
     *
     * @return Image orientation value corresponding to <code>ExifInterface.ORIENTATION_*</code> <br/>
     *         Returns -1 if the row for the {@link android.net.Uri} is not found.
     ****/
    public static int getOrientation(Context context, Uri uri) {

        int invalidOrientation = -1;
        if (uri == null) {
            throw new NullPointerException(ERROR_URI_NULL);
        }

        if (!isMediaContentUri(uri)) {
            return invalidOrientation;
        }

        String filePath = ImageUtil.getImagePathForUri(context, uri);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = invalidOrientation;
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, invalidOrientation);
        }

        return orientation;
    }

    /***
     * @deprecated Use {@link MediaUtils#isMediaContentUri(android.net.Uri)} instead. <br/>
     *             Checks if the parameter {@link android.net.Uri} is a
     *             {@link android.provider.MediaStore.Audio.Media} content uri.
     ****/
    public static boolean isMediaContentUri(Uri uri) {
        if (!uri.toString().contains("content://media/")) {
//            LogUtil.w(TAG, "uri is not a media content");
            return false;
        } else {
            return true;
        }
    }


    /**
     * *
     * Inserts an image into {@link android.provider.MediaStore.Images.Media} content provider of the device.
     *
     * @return The media content Uri to the newly created image, or null if the image failed to be
     * stored for any reason.
     * **
     */
    public static String writeImageToMedia(Context ctx, Bitmap image, String title, String description) {
        // TODO: move to MediaUtils
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        return MediaStore.Images.Media.insertImage(ctx.getContentResolver(), image, title, description);
    }

    /**
     * @param mediaContentUri Media content Uri.
     *                        **
     * @deprecated Use {@link android.content.Context, android.net.Uri)}
     * <p/>
     * <br/>
     * <br/>
     * Get the file path from the Media Content Uri for video, audio or images.
     */
    public static String getImagePathForUri(Context context, Uri mediaContentUri) {

        Cursor cur = null;
        String path = null;

        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cur = context.getContentResolver().query(mediaContentUri, projection, null, null, null);

            if (cur != null && cur.getCount() != 0) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }

            // Log.v( TAG, "#getRealPathFromURI Path: " + path );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return path;
    }

}
