package com.airppt.airppt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airppt.airppt.R;
import com.airppt.airppt.util.DPIUtil;

public class PhotoGridItem extends RelativeLayout implements Checkable {
	private Context mContext;
	private boolean mCheck;
	private ImageView mImageView;
	private ImageView mSelect;
	
	public PhotoGridItem(Context context) {
		this(context, null, 0, 0);
	}

	public PhotoGridItem(Context context, float wh) {
		this(context, null, 0, wh);
	}
	
	public PhotoGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

	public PhotoGridItem(Context context, AttributeSet attrs, int defStyle, float wh) {
		super(context, attrs, defStyle);
		mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.item_all_photo_show, this);
		mImageView = (ImageView)findViewById(R.id.photo_img_view);
		mSelect = (ImageView)findViewById(R.id.photo_select);
		int imgW = (int)((DPIUtil.screen_width  - DPIUtil.dip2px(context, 32) * 3f - 20) / 3f);
		int imgH = (int) ((DPIUtil.screen_width  - DPIUtil.dip2px(context, 32) * 3f - 20) / 3f / wh);
		mImageView.getLayoutParams().width = imgW ;
		mImageView.getLayoutParams().height =  imgH ;
	}
	@Override
	public void setChecked(boolean checked) {
//		mCheck = checked;
//		mSelect.setImageDrawable(checked ? getResources().getDrawable(R.mipmap.cb_on) : getResources().getDrawable(R.mipmap.cb_normal));
	}
	
	@Override
	public boolean isChecked() {
		return mCheck;
	}

	@Override
	public void toggle() {  
		setChecked(!mCheck);
	}
	
	public void setImgResID(int id){
		if(mImageView != null){
			mImageView.setBackgroundResource(id);
		}
	}
	
	public void SetBitmap(Bitmap bit){
		if(mImageView != null){
			mImageView.setImageBitmap(bit);
		}
	}
	
	public ImageView getImageView(){
		return mImageView;
	}
	
	
}
