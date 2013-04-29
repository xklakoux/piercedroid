package com.xklakoux.piercedroid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class StartActivity extends Activity implements OnMenuItemClickListener{

	public static final String CODE = "code";
	ImageView ibAddPhoto;
	Bitmap photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);
		ibAddPhoto = (ImageView) findViewById(R.id.ibAddPhoto);
	}

	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
	    popup.setOnMenuItemClickListener(this);
		inflater.inflate(R.menu.actions_photo, popup.getMenu());
		popup.show();
	}

	@Override
	
	public boolean onMenuItemClick(MenuItem item) {
		Intent i = new Intent(StartActivity.this, PictureActivity.class);
		switch(item.getItemId()){
		case R.id.itemTakePhoto:
			i.putExtra(StartActivity.CODE, PictureActivity.PHOTO_CODE);
			startActivity(i);
			return true;
		case R.id.itemSelectPicture:
			i.putExtra(StartActivity.CODE, PictureActivity.IMAGE_CODE);
			startActivity(i);
			return true;
		}
		return false;
	}

}
