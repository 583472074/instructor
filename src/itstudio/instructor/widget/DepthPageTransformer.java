package itstudio.instructor.widget;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class DepthPageTransformer implements ViewPager.PageTransformer
{
	private static final float MIN_SCALE = 0.85f;
	private static final float MIN_ALPHA = 0.5f;

	@SuppressLint("NewApi")
	public void transformPage(View view, float position)
	{
		int pageWidth = view.getWidth();
		int pageHeight = view.getHeight();

		System.out.println(position+"position");
		Log.e("TAG", view + " , " + position + "");
        float ap=0;
		if (position < -1)
		{ // [-Infinity,-1)
			// This page is way off-screen to the left.
		    ap=0;

		} else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
		{ // [-1,1]
			// Modify the default slide transition to shrink the page as well
			float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			// Fade the page relative to its size.
			ap = MIN_ALPHA + (scaleFactor - MIN_SCALE)
                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA);

		}
		if(ap>0.4){
		    view.setAlpha(ap);
		    
		}else{
		    view.setAlpha(0.4f);
        }
	}
}