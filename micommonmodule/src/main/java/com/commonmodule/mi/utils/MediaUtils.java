package com.commonmodule.mi.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;


public class MediaUtils {

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";

    private static final String TAG = MediaUtils.class.getSimpleName();

    /**
     * *
     * Get runtime duration of media such as audio or video in milliseconds
     * **
     */
    public static long getDuration(Context ctx, Uri mediaUri) {
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{Video.Media.DURATION}, null, null, null);
        long duration = -1;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    duration = cur.getLong(cur.getColumnIndex(Video.Media.DURATION));

                    if (duration == 0)
                        LogUtil.w(" The image size was found to be 0. Reason: UNKNOWN");

                }    // end while
            } else if (cur.getCount() == 0) {
                LogUtil.e(" cur size is 0. File may not exist");
            } else {
                LogUtil.e(" cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return duration;
    }

    /**
     * Checks if the parameter {@link android.net.Uri} is a Media content uri.
     * **
     */
    public static boolean isMediaContentUri(Uri uri) {
        if (!uri.toString().contains("content://media/")) {
            Log.w(TAG, "#isContentUri The uri is not a media content uri");
            return false;
        } else
            return true;
    }

    /**
     * Generates uri with "content://" scheme for a given image file.
     *
     * @param context
     * @param imageFile
     * @return Content uri for the parameter image file. Returns null if
     * the image file is not found.
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        return getImageContentUri(context, filePath);
    }

    /**
     * Generates uri with "content://" scheme for a given image file path.
     *
     * @param context
     * @param absoluteImageFilePath Absolute path of the image file on the disk
     * @return Content uri for the parameter image file. Returns null if
     * the image file is not found.
     */
    public static Uri getImageContentUri(Context context, String absoluteImageFilePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{absoluteImageFilePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            File imageFile = new File(absoluteImageFilePath);
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, absoluteImageFilePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the size of the media resource pointed to by the paramter mediaUri.
     * <p/>
     * Known bug: for unknown reason, the image size for some images was found to be 0
     *
     * @param mediaUri uri to the media resource. For e.g. content://media/external/images/media/45490 or
     *                 content://media/external/video/media/45490
     * @return Size in bytes, -1 if error
     * **
     */
    public static long getMediaSize(Context ctx, Uri mediaUri) {

        long size = -1;
        if (!MediaUtils.isMediaContentUri(mediaUri)) {
            LogUtil.i(" Not a valid content uri");
            return size;
        }

        String columnName = MediaStore.MediaColumns.DATA;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{columnName}, null, null, null);

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String path = cur.getString(cur.getColumnIndex(columnName));
                    File f = new File(path);

                    size = f.length();

                    if (size == 0) {
                        LogUtil.e(" The media size was found to be 0. Reason: UNKNOWN");
                    }
                } // end while
            } else if (cur.getCount() == 0) {
                LogUtil.e(" cur size is 0. File may not exist");
            } else {
                LogUtil.e(" cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return size;
    }

    /**
     * Gets media file name.
     */
    public static String getFileName(Context ctx, Uri mediaUri) {
        // TODO: move to MediaUtils
        String colName = MediaStore.MediaColumns.DISPLAY_NAME;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{colName}, null, null, null);
        String dispName = null;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    dispName = cur.getString(cur.getColumnIndex(colName));

                    if (TextUtils.isEmpty(colName)) {
                        LogUtil.w(" The file name is blank or null. Reason: UNKNOWN");
                    }

                } // end while
            } else if (cur != null && cur.getCount() == 0) {
                LogUtil.e("  File may not exist");
            } else {
                LogUtil.e(" cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return dispName;
    }

    /**
     *
     * Gets media type from the Uri
     *
     * @return "video", "audio", "image" Returns null otherwise.
     * **
     */
    public static String getType(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        String uriStr = uri.toString();

        if (uriStr.contains(TYPE_VIDEO)) {
            return TYPE_VIDEO;
        } else if (uriStr.contains(TYPE_AUDIO)) {
            return TYPE_AUDIO;
        } else if (uriStr.contains(TYPE_IMAGE)) {
            return TYPE_IMAGE;
        } else {
            return null;
        }
    }
}
