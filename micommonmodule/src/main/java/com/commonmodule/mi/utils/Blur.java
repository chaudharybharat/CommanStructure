package com.commonmodule.mi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;


/**
 * Created by akash on 10/2/15.
 */
public class Blur {

    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {

	//Let's create an empty bitmap with the same size of the bitmap we want to blur

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
	Bitmap outBitmap = Bitmap
		.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

	//Instantiate a new Renderscript
	RenderScript rs = RenderScript.create(context);

	//Create an Intrinsic Blur Script using the Renderscript
	ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
			Element.U8_4(rs));

	//Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
	Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
	Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

	//Set the radius of the blur
	blurScript.setRadius(22.f);

	//Perform the Renderscript
	blurScript.setInput(allIn);
	blurScript.forEach(allOut);

	//Copy the final bitmap created by the out Allocation to the outBitmap
	allOut.copyTo(outBitmap);

	//recycle the original bitmap
	bitmap.recycle();

	//After finishing everything, we destroy the Renderscript.
	rs.destroy();

	return outBitmap;
    }

}
