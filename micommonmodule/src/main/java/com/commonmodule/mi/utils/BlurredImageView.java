package com.commonmodule.mi.utils;

/**
 * Created by mayur on 11/2/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BlurredImageView extends ImageView {

    Context context;

    public BlurredImageView(Context context) {
	super(context);
	this.context = context;
    }

    public BlurredImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.context = context;
    }

    public BlurredImageView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
        try{
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap blurredBitmap;
            try {
                Bitmap bitmap = b.copy(Config.ARGB_8888, true);
                blurredBitmap = Blur.blurBitmap(bitmap, getContext());
            } catch (Exception e) {
                blurredBitmap = Blur.blurBitmap(b, getContext());
            }
            canvas.drawBitmap(scaleCenterCrop(blurredBitmap, canvas.getHeight(), canvas.getWidth()), 0,
                    0, null);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        try{
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            // Compute the scaling factors to fit the new height and width, respectively.
            // To cover the final image, the final scaling will be the bigger
            // of these two.
            float xScale = (float) newWidth / sourceWidth;
            float yScale = (float) newHeight / sourceHeight;
            float scale = Math.max(xScale, yScale);

            // Now get the size of the source bitmap when scaled
            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            // Let's find out the upper left coordinates if the scaled bitmap
            // should be centered in the new size give by the parameters
            float left = (newWidth - scaledWidth) / 2;
            float top = (newHeight - scaledHeight) / 2;

            // The target rectangle for the new, scaled version of the source bitmap will now
            // be
            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            // Finally, we create a new bitmap of the specified size and draw our new,
            // scaled bitmap onto it.
            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(source, null, targetRect, null);

            return dest;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }

}