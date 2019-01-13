package com.commonmodule.mi.utils;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.UUID;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mind on 6/10/15.
 */
public class LoadImage {

    public static void loadRoundedImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView) {
        //loadRoundedImageFromPath(mContext, mImagePath, imageView, 0);
        try {
            Glide.with(mContext).load(new File(mImagePath))
                    .bitmapTransform(new BlurTransformation(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void loadRoundedImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView, final int imageResouces) {
        if (ValidationUtil.isValidString(mImagePath)) {
            if (imageResouces != 0) {
                Glide.with(mContext).load(Uri.fromFile(new File(mImagePath)))
                        .placeholder(imageResouces)
                        .into(imageView);
            } else {
                Glide.with(mContext).load(new File(mImagePath))
                        .bitmapTransform(new RoundedCornersTransformation(mContext, 5, 10))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
        }
    }


    public static void loadCircularImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView) {
        loadCircularImageFromPath(mContext, mImagePath, imageView, 0);

    }


    public static void loadCircularImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView, final int imageResouces) {
        if (ValidationUtil.isValidString(mImagePath)) {
            if (imageResouces != 0) {
                Glide.with(mContext).load(Uri.fromFile(new File(mImagePath)))
                        .placeholder(imageResouces)
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(imageView);
            } else {
                Glide.with(mContext).load(new File(mImagePath))
                        .centerCrop()
                        .signature(new StringSignature(UUID.randomUUID().toString()))
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(imageView);
            }
        }
    }


    public static void loadImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView) {
        loadImageFromPath(mContext, mImagePath, imageView, 0);

    }


    public static void loadImageFromPath(final Context mContext, final String mImagePath, final ImageView imageView, final int imageResouces) {
        if (ValidationUtil.isValidString(mImagePath)) {
            if (imageResouces != 0) {
                Glide.with(mContext).load(Uri.fromFile(new File(mImagePath)))
                        .placeholder(imageResouces)
                        .into(imageView);
            } else {
                Glide.with(mContext).load(new File(mImagePath))
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
    public static void loadImageFromPath_new(Fragment fragment, final Context mContext, final File imgfile, final ImageView imageView) {

        if(imgfile!=null) {
            //Glide.get(mContext).clearDiskCache();


            Glide.with(fragment)
                    .load(Uri.fromFile(imgfile))
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(imageView);

        }


    }


    public static void getImageSize(final ImageView mImageView, final ImageSizeListener imageSizeListener) {
        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                // Remove after the first run so it doesn't fire forever
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                final int finalHeight = mImageView.getMeasuredHeight();
                final int finalWidth = mImageView.getMeasuredWidth();

                imageSizeListener.loadWithResize(finalHeight, finalWidth);
//                loadImageUrlResize(mContext, mImageUrl, imageView,imageResources,generateThumb, finalHeight, finalWidth );

                return true;
            }
        });
    }

    public interface ImageSizeListener {
        void loadWithResize(int height, int width);
    }

    public static int getdeviceWidth(Activity activity){
        Display mDisplay = activity.getWindowManager().getDefaultDisplay();
        return mDisplay.getWidth();
        //.override(getdeviceWidth(mContext), mImageView.getHeight())
    }
    public static void loadImage(final Activity mContext, final ImageView mImageView, final String mImageUrl, final int imageResources,final ProgressBar pb_loader) {
        //mImageView.setImageBitmap(null);
        pb_loader.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(mImageUrl)
                //.override(getdeviceWidth(mContext), mImageView.getHeight())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(imageResources)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }

    public static void loadImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources,final ProgressBar pb_loader) {
        //mImageView.setImageBitmap(null);
        pb_loader.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(mImageUrl)

                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(imageResources)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }



   /* public static void loadImageLarge(final Context mContext, final TouchImageView iv_profile_fullscreen, final String mImageUrl, final int imageResources,final ProgressBar pb_loader) {
        pb_loader.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(mImageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(imageResources)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_profile_fullscreen);


    }*/


    public static void loadBlurImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources) {
        try {

            Glide.with(mContext)
                    .load(mImageUrl)
                    .centerCrop()
//                        .override(width, height)
                    .placeholder(imageResources)
                    .bitmapTransform(new BlurTransformation(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);


            //  getImageSize(mImageView, imageSizeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void loadCircleImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources,final ProgressBar pb_loader) {
        pb_loader.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(mImageUrl)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        pb_loader.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(imageResources)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }

    public static void loadCircleBlurImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources) {

        Glide.with(mContext)
                .load(mImageUrl)
                .centerCrop()
//                        .override(width, height)
                .placeholder(imageResources)
                .bitmapTransform(new BlurTransformation(mContext), new CropCircleTransformation(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }

    public static void loadRoundedImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources) {

        Glide.with(mContext)
                .load(mImageUrl)
                .centerCrop()
                        //.override(width, height)
                .placeholder(imageResources)
                .bitmapTransform(new RoundedCornersTransformation(mContext, 100, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }

    public static void loadRoundedBlurImage(final Context mContext, final ImageView mImageView, final String mImageUrl, final int imageResources) {

        Glide.with(mContext)
                .load(mImageUrl)
                .centerCrop()

//                        .override(width, height)
                .placeholder(imageResources)
                .bitmapTransform(new BlurTransformation(mContext), new RoundedCornersTransformation(mContext, 15, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


    }





}
