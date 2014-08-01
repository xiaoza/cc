package cn.nstreet.baijie.ui;

import com.nstreet.beijie.R;

import cn.nstreet.baijie.AppContext;
import cn.nstreet.baijie.vendor.slidingmenu.SlidingActivityBase;
import cn.nstreet.baijie.vendor.slidingmenu.SlidingActivityHelper;
import cn.nstreet.baijie.vendor.slidingmenu.SlidingMenu;
import cn.nstreet.baijie.vendor.slidingmenu.SlidingMenu.CanvasTransformer;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaiJSm extends ActionBarActivity implements SlidingActivityBase {
	
	protected AppContext appContext = (AppContext)getApplication();
	
	private static Interpolator interp = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}		
	};

	private SlidingActivityHelper mHelper;
	private CanvasTransformer mTransformer = new CanvasTransformer() {
		@Override
		public void transformCanvas(Canvas canvas, float percentOpen) {
//			canvas.translate(0, canvas.getHeight()*(1-interp.getInterpolation(percentOpen)));
			float scale = (float) (percentOpen*0.25 + 0.75);
			canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
		}			
	};
	
	/**SlidingMenu***************************/
	private SlidingMenu slidingMenu;
	private LinearLayout smOrder;
	private LinearLayout smSetting;
	
	private ImageButton smUserface;
	private TextView smUsername;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		
		//设置是否能够使用ActionBar来滑动
		setSlidingActionBarEnabled(true);
		//初始化侧滑界面
		initSlidingMenu();
	}
	
	private void initSlidingMenu() {
		setBehindContentView(R.layout.behind_slidingmenu);
		slidingMenu = getSlidingMenu();
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindScrollScale(0.0f);
		slidingMenu.setBehindCanvasTransformer(mTransformer);
		if(slidingMenu != null){
			//初始化侧滑界面的选项列表
			initChooseListView();
			initUser();
		}
	}
	
	private void initChooseListView() {
		smOrder = (LinearLayout) findViewById(R.id.slidingmenu_order);
		smSetting = (LinearLayout) findViewById(R.id.slidingmenu_setting);
		
		smOrder.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				smOrder.setBackgroundResource(R.drawable.slidemenu_choose);
				smSetting.setBackgroundResource(R.color.slidemenubackcolor);
				Intent intent = new Intent(BaiJSm.this, Order.class);
				startActivity(intent);
			}

		});
		smSetting.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				smOrder.setBackgroundResource(R.color.slidemenubackcolor);
				smSetting.setBackgroundResource(R.drawable.slidemenu_choose);
				slidingMenu.showContent();
			}

		});

		smOrder.setBackgroundResource(R.drawable.slidemenu_choose);
		smSetting.setBackgroundResource(R.color.slidemenubackcolor);
	}
	
	private void initUser(){
		
		smUserface = (ImageButton)findViewById(R.id.slidingmenu_userface);
		smUsername = (TextView)findViewById(R.id.slidingmenu_usertitle);
		
		//TODO 给商家logo和商家名称赋值
		
		smUserface.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				slidingMenu.showContent();
			}	
		});
		smUsername.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				slidingMenu.showContent();
			}
		});		
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null) {
			return v;
		}
		return mHelper.findViewById(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void setBehindContentView(View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	public void toggle() {
		mHelper.toggle();
	}

	public void showContent() {
		mHelper.showContent();
	}

	public void showMenu() {
		mHelper.showMenu();
	}

	public void showSecondaryMenu() {
		mHelper.showSecondaryMenu();
	}

	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b) return b;
		return super.onKeyUp(keyCode, event);
	}
}
