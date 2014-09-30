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
 * ����������ӿؼ������У����������¼����ƶ��¼�������¼��Ľ���
 * 
 * @author wangzl_pc
 *
 */
public class Dragment extends FrameLayout {
	private static final String TAG = Dragment.class.getName();
	// ��ָ������
	private int touchX, touchY;
	// ��ǰ������View
	private View touchedView;
	// ��ȡƫ��λ��
	private int offsetX, offsetY;
	// ������ģʽ
	private int screenOrientation;
	// �����϶
	private int spaceLandscape = 0;
	// �����϶
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
	 * ��ȡ��Ļ����
	 * 
	 * @since if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
	 *        Log.i("info", "landscape"); // ����
	 * 
	 *        } else if (screenOrientation ==
	 *        Configuration.ORIENTATION_PORTRAIT) { Log.i("info", "portrait");
	 *        // ���� }
	 */
	public void init() {
		screenOrientation = this.getResources().getConfiguration().orientation;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ������Ļ�������ò��ֲ���
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
	 * �ƶ�����View
	 * 
	 * @param event
	 */
	private void move(MotionEvent event) {
		// ��������View���ƶ�
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
	 * �ƶ����view
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
	 * ��ȡ��ǰ������View
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
	 * �������л����������÷���Ĭ��Ϊ��ʼ״̬
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
