package com.xklakoux.piercedroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PictureActivity extends Activity {

	Bitmap photo;
	ImageView ivPicture;
	RelativeLayout rlPicture;

	private int mShortAnimationDuration;

	public static final int IMAGE_CODE = 1;
	public static final int PHOTO_CODE = 2;

	private ImageView ivBall;
	private ImageView ivRing;
	private ImageView ivBarbell;
	private ImageView ivCaptive;
	private ImageView ivSpike;
	private ImageView ivPlug;

	private ImageView ivTrash;

	private List<ImageView> system;
	private List<ImageView> custom = new ArrayList<ImageView>();

	int tempW;
	int tempH;

	private int ids = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_layout);

		ivPicture = (ImageView) findViewById(R.id.ivPicture);
		rlPicture = (RelativeLayout) findViewById(R.id.rlPicture);
		Intent i = new Intent();
		switch (getIntent().getIntExtra(StartActivity.CODE, 0)) {
		case PHOTO_CODE:
			i.setAction("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(i, PHOTO_CODE);
			break;
		case IMAGE_CODE:
			i.setType("image/*");
			i.setAction(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(i, IMAGE_CODE);
			break;
		}
		findViews();
		ImageView[] list = { ivBall, ivRing, ivBarbell, ivCaptive, ivSpike,
				ivPlug };
		List<ImageView> system = Arrays.asList(list);
		for (ImageView iv : system) {
			iv.setOnTouchListener(new MyTouchListener());
			iv.setFocusable(false);
		}
		rlPicture.setOnDragListener(new MyDragListener());
		ivTrash.setOnDragListener(new MyDragTrashListener());
		mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_mediumAnimTime);
		ivTrash.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			try {
				// We need to recyle unused bitmaps
				if (photo != null) {
					photo.recycle();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2; // 1/2 of the original image
				// options.inTargetDensity = 1;
				// options.inDensity = 1;

				InputStream stream = getContentResolver().openInputStream(
						data.getData());
				photo = BitmapFactory.decodeStream(stream, null, options);

				tempW = photo.getWidth();
				tempH = photo.getHeight();
				stream.close();

				if (tempW > tempH) {
					Matrix mtx = new Matrix();
					mtx.postRotate(270);
					photo = Bitmap.createBitmap(photo, 0, 0, tempW, tempH, mtx,
							true);
				}
				ivPicture.setImageBitmap(photo);
				for (ImageView iv : custom) {
					rlPicture.removeView(iv);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.picture_action_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent();
		switch (item.getItemId()) {
		case R.id.menu_retake:
			i.setAction("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(i, PHOTO_CODE);
			break;
		case R.id.menu_save:
			savePicture();
			break;
		case R.id.menu_share:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
						view);
				view.startDrag(data, shadowBuilder, view, 0);
				view.setVisibility(View.INVISIBLE);
				return true;
			} else {
				return false;
			}
		}
	}

	class MyDragListener implements OnDragListener {
		// Drawable enterShape = getResources().getDrawable(
		// R.drawable.ic_jewellery_test1);
		// Drawable normalShape = getResources().getDrawable(
		// R.drawable.ic_jewellery_test1);

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				fadeIn();
				// Do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				break;
			case DragEvent.ACTION_DROP:

				ImageView iv;
				RelativeLayout container = (RelativeLayout) v;
				ImageView view = (ImageView) event.getLocalState();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						new ViewGroup.MarginLayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
				params.setMargins((int) event.getX() - view.getWidth() / 2,
						(int) event.getY() - view.getHeight() / 2, 0, 0);

				if (!custom.contains(view)) {
					iv = (ImageView) LayoutInflater.from(getBaseContext())
							.inflate(R.layout.jewellery, null);
					iv.setId(ids++);
					iv.setOnTouchListener(new MyTouchListener());
					custom.add(iv);

				} else {
					iv = view;
					container.removeView(iv);
				}
				container.addView(iv, params);
				iv.setImageDrawable(view.getDrawable());
				iv.setFocusableInTouchMode(true);
				iv.requestFocus();
				view.setVisibility(View.VISIBLE);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				fadeOut();
				// v.setBackgroundDrawable(normalShape);
			default:
				break;
			}
			return true;
		}
	}

	class MyDragTrashListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				v.setBackgroundColor(getResources().getColor(
						R.color.translucent_red));
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				v.setBackgroundColor(getResources().getColor(
						R.color.translucent_black));
				break;
			case DragEvent.ACTION_DROP:
				//
				ImageView iv;
				ImageView view = (ImageView) event.getLocalState();
				if (!custom.contains(view)) {

				} else {
					iv = view;
					rlPicture.removeView(iv);
					custom.remove(iv);
				}

				v.setBackgroundColor(getResources().getColor(
						R.color.translucent_black));

				break;
			case DragEvent.ACTION_DRAG_ENDED:
			default:
				break;
			}
			return true;
		}
	}

	private void findViews() {
		ivBall = (ImageView) findViewById(R.id.ivBall);
		ivRing = (ImageView) findViewById(R.id.ivRing);
		ivBarbell = (ImageView) findViewById(R.id.ivBarbell);
		ivCaptive = (ImageView) findViewById(R.id.ivCaptive);
		ivSpike = (ImageView) findViewById(R.id.ivSpike);
		ivPlug = (ImageView) findViewById(R.id.ivPlug);

		ivTrash = (ImageView) findViewById(R.id.ivTrash);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	private void setOnClick(View v) {
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void fadeIn() {

		ivTrash.setAlpha(0f);
		ivTrash.setVisibility(View.VISIBLE);
		Log.d("fadeIn", "works" + ivTrash.getVisibility());
		ivTrash.animate().alpha(1f).setDuration(mShortAnimationDuration);
	}

	private void fadeOut() {

		ivTrash.animate().alpha(0f).setDuration(mShortAnimationDuration);
		ivTrash.setVisibility(View.GONE);
		Log.d("fadeOut", "works");
	}

	private void savePicture() {
		Bitmap output;
		ivPicture.buildDrawingCache();
		output = ivPicture.getDrawingCache();
		Canvas c = new Canvas(output);
		Bitmap frame;
		for (ImageView imageView : custom) {
			// Log.d("id",""+imageView.getId());
			// Bitmap frame = BitmapFactory.decodeResource(getResources(),
			// imageView.getDrawable());
			// BitmapDrawable drawable = (BitmapDrawable)
			// imageView.getDrawable();
			// Bitmap frame = drawable.getBitmap();
			imageView.buildDrawingCache(true);
			frame = imageView.getDrawingCache(true);
			// frame = Bitmap.createScaledBitmap(frame, tempW, tempW,
			// false);
			LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
			Log.d("params", lp.leftMargin + " " + lp.topMargin);
			c.drawBitmap(frame, lp.leftMargin, lp.topMargin, null);
			imageView.destroyDrawingCache();
			// frame.recycle();

		}
		try {

			String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";
			OutputStream os = null;
			String photo_path = "";
			String path = Environment.getExternalStorageDirectory().getPath();
			File photoDirectory = new File(path + "/Piercedroid/");
			photo_path = photoDirectory.getPath();
			photo_path += "/" + tmpImg;
			photoDirectory.mkdirs();
			File outputFile = new File(photoDirectory, tmpImg);
			os = new FileOutputStream(outputFile);

			output.compress(CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
