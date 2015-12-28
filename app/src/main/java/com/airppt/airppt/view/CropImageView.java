package com.airppt.airppt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.airppt.airppt.R;
import com.airppt.airppt.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * 底图缩放，浮层不变
 * @author yanglonghui
 *
 */
public class CropImageView extends View {
	
	//单点触摸的时候
	private float oldX=0;
	private float oldY=0;
	
	//多点触摸的时候
	private float oldx_0=0;
	private float oldy_0=0;
	
	private float oldx_1=0;
	private float oldy_1=0;
	
	//状态
	private final int STATUS_Touch_SINGLE=1;//单点
	private final int STATUS_TOUCH_MULTI_START=2;//多点开始
	private final int STATUS_TOUCH_MULTI_TOUCHING=3;//多点拖拽中
	
	private int mStatus=STATUS_Touch_SINGLE;
	
	//默认的裁剪图片宽度与高度
	private final int defaultCropWidth=300;
	private final int defaultCropHeight=300;
	private int cropWidth=defaultCropWidth;
	private int cropHeight=defaultCropHeight;
	
	protected float oriRationWH=0;//原始宽高比率
	protected final float maxZoomOut=5.0f;//最大扩大到多少倍
	protected final float minZoomIn=0.333333f;//最小缩小到多少倍
	
	protected Drawable mDrawable;//原图
	protected FloatDrawable mFloatDrawable;//浮层
	protected Rect mDrawableSrc = new Rect();
	protected Rect mDrawableDst = new Rect();
	protected Rect mDrawableFloat = new Rect();//浮层选择框，就是头像选择框
	protected boolean isFrist=true;
	
	protected Context mContext;
    
	public CropImageView(Context context) {
		super(context);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
			
	}
	
	private void init(Context context)
	{
		this.mContext=context;
		try {  
            if(android.os.Build.VERSION.SDK_INT>=11)  
            {  
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		mFloatDrawable=new FloatDrawable(context);//头像选择框
	}

	public void setDrawable(Drawable mDrawable,int cropWidth,int cropHeight, boolean isFirst)
	{
		this.mDrawable=mDrawable;
		this.cropWidth=cropWidth;
		this.cropHeight=cropHeight;
		this.isFrist=isFirst;
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getPointerCount()>1)
		{
			if(mStatus==STATUS_Touch_SINGLE)
			{
				mStatus=STATUS_TOUCH_MULTI_START;
				
				oldx_0=event.getX(0);
				oldy_0=event.getY(0);
				
				oldx_1=event.getX(1);
				oldy_1=event.getY(1);
			}
			else if(mStatus==STATUS_TOUCH_MULTI_START)
			{
				mStatus=STATUS_TOUCH_MULTI_TOUCHING;
			}
		}
		else
		{
			if(mStatus==STATUS_TOUCH_MULTI_START||mStatus==STATUS_TOUCH_MULTI_TOUCHING)
			{
				oldx_0=0;
				oldy_0=0;
				
				oldx_1=0;
				oldy_1=0;
				
				oldX=event.getX();
				oldY=event.getY();
			}
			
			mStatus=STATUS_Touch_SINGLE;
		}
		
//Log.v("count currentTouch"+currentTouch, "-------");	

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
//Log.v("count ACTION_DOWN", "-------");
				oldX = event.getX();
				oldY = event.getY();
	            break;
	            
			case MotionEvent.ACTION_UP:
//Log.v("count ACTION_UP", "-------");
				checkBounds();
	            break;
	            
			case MotionEvent.ACTION_POINTER_1_DOWN:
//Log.v("count ACTION_POINTER_1_DOWN", "-------");
				break;
				
			case MotionEvent.ACTION_POINTER_UP:
//Log.v("count ACTION_POINTER_UP", "-------");
				break;
	            
			case MotionEvent.ACTION_MOVE:
//Log.v("count ACTION_MOVE", "-------");
				if(mStatus==STATUS_TOUCH_MULTI_TOUCHING)
				{
					float newx_0=event.getX(0);
					float newy_0=event.getY(0);
					
					float newx_1=event.getX(1);
					float newy_1=event.getY(1);
					
					float oldWidth=Math.abs(oldx_1-oldx_0);
					float oldHeight=Math.abs(oldy_1-oldy_0);
					
					float newWidth=Math.abs(newx_1-newx_0);
					float newHeight=Math.abs(newy_1-newy_0);
					
					boolean isDependHeight=Math.abs(newHeight-oldHeight)>Math.abs(newWidth-oldWidth);
					
			        float ration=isDependHeight?((float)newHeight/(float)oldHeight):((float)newWidth/(float)oldWidth);
			        int centerX=mDrawableDst.centerX();
			        int centerY=mDrawableDst.centerY();
			        int _newWidth=(int) (mDrawableDst.width()*ration);
			        int _newHeight=(int) ((float)_newWidth/oriRationWH);
			        
			        float tmpZoomRation=(float)_newWidth/(float)mDrawableSrc.width();
			        if(tmpZoomRation>=maxZoomOut)
			        {
			        	_newWidth=(int) (maxZoomOut*mDrawableSrc.width());
			        	_newHeight=(int) ((float)_newWidth/oriRationWH);
			        }
			        else if(tmpZoomRation<=minZoomIn)
			        {
			        	_newWidth=(int) (minZoomIn*mDrawableSrc.width());
			        	_newHeight=(int) ((float)_newWidth/oriRationWH);
			        }
			        
			        mDrawableDst.set(centerX-_newWidth/2, centerY-_newHeight/2, centerX+_newWidth/2, centerY+_newHeight/2);
			        invalidate();
			        
					oldx_0=newx_0;
					oldy_0=newy_0;
					
					oldx_1=newx_1;
					oldy_1=newy_1;
				}
				else if(mStatus==STATUS_Touch_SINGLE)
				{
					int dx=(int)(event.getX()-oldX);
					int dy=(int)(event.getY()-oldY);
					
					oldX=event.getX();
					oldY=event.getY();
					
					if(!(dx==0&&dy==0))
					{
						mDrawableDst.offset((int)dx, (int)dy);
						invalidate();
					}
				}
	            break;
		}
		
//		Log.v("event.getAction()："+event.getAction()+"count："+event.getPointerCount(), "-------getX:"+event.getX()+"--------getY:"+event.getY());
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }
        
        configureBounds();

		mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
	}
	
	
	protected void configureBounds()
	{
		if(isFrist)
		{
			oriRationWH=((float)mDrawable.getIntrinsicWidth())/((float)mDrawable.getIntrinsicHeight());
			
			final float scale = mContext.getResources().getDisplayMetrics().density;
//			int w=Math.min(getWidth(), (int)(mDrawable.getIntrinsicWidth()*scale+0.5f));
//			int h=(int) (w/oriRationWH);

			int w,h;

			float cropWH = ((float)cropWidth) / ((float)cropHeight);
			if (cropWH < oriRationWH) {
				h = (int) (cropHeight * scale+0.5f);
				w = (int) (oriRationWH * h);
			} else {
				w = (int) (cropWidth * scale+0.5f);
				h = (int) (w/oriRationWH);
			}
			
			int left = (getWidth()-w)/2;
			int top = (getHeight()-h)/2;
			int right=left+w;
			int bottom=top+h;

			
			mDrawableSrc.set(left,top,right,bottom);
			mDrawableDst.set(mDrawableSrc);
			
			int floatWidth=dipTopx(mContext, cropWidth);
			int floatHeight=dipTopx(mContext, cropHeight);
			
			if(floatWidth>getWidth())
			{
				floatWidth=getWidth();
				floatHeight=cropHeight*floatWidth/cropWidth;
			}
			
			if(floatHeight>getHeight())
			{
				floatHeight=getHeight();
				floatWidth=cropWidth*floatHeight/cropHeight;
			}
			
			int floatLeft=(getWidth()-floatWidth)/2;
			int floatTop = (getHeight()-floatHeight)/2;
			mDrawableFloat.set(floatLeft, floatTop,floatLeft+floatWidth, floatTop+floatHeight);
			
	        isFrist=false;
		} else {

		}
        
		mDrawable.setBounds(mDrawableDst);
		mFloatDrawable.setBounds(mDrawableFloat);
	}
	
	protected void checkBounds()
	{
		int newLeft = mDrawableDst.left;
		int newTop = mDrawableDst.top;
		
		boolean isChange=false;
		if(mDrawableDst.left<-mDrawableDst.width())
		{
			newLeft=-mDrawableDst.width();
			isChange=true;
		}
		
		if(mDrawableDst.top<-mDrawableDst.height())
		{
			newTop=-mDrawableDst.height();
			isChange=true;
		}
		
		if(mDrawableDst.left>getWidth())
		{
			newLeft=getWidth();
			isChange=true;
		}
		
		if(mDrawableDst.top>getHeight())
		{
			newTop=getHeight();
			isChange=true;
		}
		
		mDrawableDst.offsetTo(newLeft, newTop);
		if(isChange)
		{
			invalidate();
		}
	}
	
	public Boolean getCropImage(String path)
	{
		Log.e("width", getWidth() + " " + getHeight() + " :" + mDrawableSrc.width() + " " + mDrawableDst.width());
		Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(tmpBitmap);
		canvas.drawColor(Color.WHITE);
		mDrawable.draw(canvas);

		Matrix matrix=new Matrix();
		float scale=(float)(mDrawableSrc.width())/(float)(mDrawableDst.width());
//		float scale=(float)(1500)/(float)(mDrawableDst.width());
		matrix.postScale(scale, scale);

	    Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.width(), mDrawableFloat.height(), null, true);
	    tmpBitmap.recycle();
	    tmpBitmap=null;

//	    Bitmap newRet=Bitmap.createScaledBitmap(ret, cropWidth, cropHeight, false);
//    	ret.recycle();
//    	ret=newRet;


		//		Log.e("size", tmpBitmap.getByteCount() + "");
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		int  options =  100 ;
		ret.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length/1024 > 100) {
			options -= 10;
			baos.reset();
			ret.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
//		byte[] bts = baos.toByteArray();
//		Bitmap bitmap = BitmapFactory.decodeByteArray(bts, 0, bts.length);
//		ByteArrayInputStream isBm =  new ByteArrayInputStream(baos.toByteArray());
//		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		ret.recycle();
//		isBm.reset();
		try {
			if (FileUtil.createFile(path) != null) {
				FileOutputStream outputStream = new FileOutputStream(path);
				baos.writeTo(outputStream);
				outputStream.flush();
				outputStream.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
    public int dipTopx(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
       return (int) (dpValue * scale + 0.5f);  
    }

	/**
	 * 获取当前操作图片
	 * @return
	 */
	public Bitmap getCurrentImage() {
		BitmapDrawable bd = (BitmapDrawable) mDrawable;
		return bd.getBitmap();
	}
}
