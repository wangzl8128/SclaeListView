package com.wangzl8128;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 * 书的容器，子控件的排列，负责缩放事件、移动事件、点击事件的接收
 * 
 * @author wangzl_pc
 *
 */
public class Dragment extends FrameLayout {
	private static final String TAG = Dragment.class.getName();
	// 手指触摸点
	private int touchX, touchY;
	// 当前触摸的View
	private View touchedView;
	// 获取偏移位置
	private int offsetX, offsetY;
	// 横竖屏模式
	private int screenOrientation;
	// 横向间隙
	private int spaceLandscape = 0;
	// 竖向间隙
	private int spacePortrait = 0;

	private View[] views = new View[3];

	private BaseAdapter mAdapter;

	public Dragment(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Dragment(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public Dragment(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取屏幕方向
	 * 
	 * @since if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
	 *        Log.i("info", "landscape"); // 横屏
	 * 
	 *        } else if (screenOrientation ==
	 *        Configuration.ORIENTATION_PORTRAIT) { Log.i("info", "portrait");
	 *        // 竖屏 }
	 */
	public void init() {
		screenOrientation = this.getResources().getConfiguration().orientation;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 根据屏幕方向设置布局参数
	 */
	private void setChildParams(boolean first) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child
					.getLayoutParams();
//			if (first) {
//				child.measure(View.MeasureSpec.UNSPECIFIED,
//						View.MeasureSpec.UNSPECIFIED);
//			}
			if (isPortrait()) {
				int width = child.getMeasuredWidth() + lp.leftMargin
						+ lp.rightMargin;
				lp.topMargin = 0;
				lp.leftMargin = 0;
				lp.leftMargin = i * width + i * spacePortrait;
				lp.rightMargin = i * width + i * spacePortrait + width;
			} else if (isLandscape()) {
				int height = child.getMeasuredHeight() + lp.topMargin
						+ lp.bottomMargin;
				lp.topMargin = 0;
				lp.leftMargin = 0;
				lp.topMargin = i * height + i * spacePortrait;
				lp.bottomMargin = i * height + i * spacePortrait + height;
			}
			child.setLayoutParams(lp);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			getTouchView();
			return true;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			move(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			return super.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 移动整个View
	 * 
	 * @param event
	 */
	private void move(MotionEvent event) {
		// 遍历所有View并移动
		int moveX = (int) event.getX();
		int moveY = (int) event.getY();
		offsetX = (int) (touchX - moveX);
		offsetY = (int) (touchY - moveY);
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			if (v != null) {
				int l = v.getLeft();
				int t = v.getTop();
				int r = v.getRight();
				int b = v.getBottom();
				l -= offsetX;
				r -= offsetX;
				t -= offsetY;
				b -= offsetY;
				Log.i(TAG, "l" + l);
				Log.i(TAG, "r" + r);
				Log.i(TAG, "offsetX" + offsetX);
				v.layout(l, t, r, b);
			}
		}
		touchX = moveX;
		touchY = moveY;
	}

	/**
	 * 移动点击view
	 * 
	 * @param event
	 */
	@Deprecated
	private void dragMove(MotionEvent event) {
		if (touchedView != null) {
			int moveX = (int) event.getX();
			int moveY = (int) event.getY();
			offsetX = (int) (touchX - moveX);
			offsetY = (int) (touchY - moveY);
			int l = touchedView.getLeft();
			int t = touchedView.getTop();
			int r = touchedView.getRight();
			int b = touchedView.getBottom();
			l -= offsetX;
			r -= offsetX;
			t -= offsetY;
			b -= offsetY;
			Log.i(TAG, "l" + l);
			Log.i(TAG, "r" + r);
			Log.i(TAG, "offsetX" + offsetX);
			touchedView.layout(l, t, r, b);
			touchX = moveX;
			touchY = moveY;
		}
	}

	/**
	 * 获取当前触摸的View
	 */
	private void getTouchView() {
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			int[] location = new int[2];
			v.getLocationOnScreen(location);
			int vx = location[0];
			int vy = location[1];
			if (touchX >= vx) {
				if (touchY > vy) {
					touchedView = v;
				}
			}
		}
	}

	/**
	 * 横竖屏切换需主动调用否则默认为初始状态
	 * 
	 * @param orientation
	 */
	public void setOnOrientationChange(int orientation) {
		screenOrientation = orientation;
		setChildParams(false);

	}

	private boolean isPortrait() {
		return screenOrientation == Configuration.ORIENTATION_PORTRAIT;
	}

	private boolean isLandscape() {
		return screenOrientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public void setAdapter(BaseAdapter adapter) {
//		if (adapter == null) {
//			Log.i(TAG, "Adapter must isn't null!");
//			throw new NullPointerException();
//		}
//		this.mAdapter = adapter;
//		for (int i = 0; i < views.length; i++) {
//			View view = views[i];
//			view = mAdapter.getView(i, view, this);
//			views[i] = view;
//			this.addView(view);
//		}
//		setChildParams(true);
//		requestLayout();
//		invalidate();
	}
}
