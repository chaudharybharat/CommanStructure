package com.commonmodule.mi.utils;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class ImageUtils {

    public static void loadImage(final Context mContext, final String imagePath, final SimpleDraweeView simpleDraweeView)
    {
        loadImage(mContext, imagePath, simpleDraweeView, 1);
    }

    private static ImageRequest request;
    private static PipelineDraweeController controller;
    private static int imageSize = 200;

    public static void loadImage(final Context mContext, final String imagePath, final SimpleDraweeView simpleDraweeView, int viewAspectRation)
    {

        if (viewAspectRation == 0)
        {
            viewAspectRation = 1;
        }

       /* final String tempPath = imagePath.replace(MyPreferences.getBaseUrl(),"");

        if (Validation.isRequiredField(tempPath) && !tempPath.equalsIgnoreCase("null") && URLUtil.isValidUrl(imagePath)) {*/

            Uri uri = Uri.parse(imagePath);
            imageSize = (getMaxWidth(mContext) / viewAspectRation);

//            Methods.syso("MAXWIDTH====imageSize=="+imageSize);//1080

            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(imageSize, imageSize))
                    .build();
            controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);




        /*ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(mBaselineJpegView.getController())
                .setImageRequest(request)
                .build();
        mBaselineJpegView.setController(controller);*/

      /*  }
        else {

           *//* Methods.syso("MAXWIDTH====imageSize==" + imageSize);//1080

            request = ImageRequestBuilder.newBuilderWithResourceId(R.mipmap.ic_launcher)
                    .build();


            controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);*//*


            Uri uri = Uri.parse(MyPreferences.getBaseUrl()+"assets/images/staticimages/nophoto.png");
            imageSize = (getMaxWidth(mContext) / viewAspectRation);

//            Methods.syso("MAXWIDTH====imageSize=="+imageSize);//1080

            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(imageSize, imageSize))
                    .build();
            controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);
        }*/
    }
    private static int maxWidth = 0;
    public static int getMaxWidth(Context context) {
        if (maxWidth != 0) {
//            Methods.syso("MAXWIDTH===="+maxWidth);//1080
            return maxWidth;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        maxWidth = width;
        return maxWidth;
    }

}