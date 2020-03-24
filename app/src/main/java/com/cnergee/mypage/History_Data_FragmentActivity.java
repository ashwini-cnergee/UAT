package com.cnergee.mypage;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar.Tab;


import com.cnergee.mypage.adapter.History_Data_PagerAdapter;
import com.cnergee.mypage.utils.Utils;

import cnergee.myapp.uat.R;

public class History_Data_FragmentActivity extends FragmentActivity{
	
	ViewPager mPager;
	Tab tab;
	public static String check="";
	 private FragmentTabHost mTabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_data_fragment);
		Utils.log("Class Name","is:"+this.getClass().getSimpleName());
		mPager = (ViewPager) findViewById(R.id.pager);
		FragmentManager fm = getSupportFragmentManager();

		
		History_Data_PagerAdapter history_Data_PagerAdapter= new History_Data_PagerAdapter(fm, History_Data_FragmentActivity.this);
		
		mPager.setAdapter(history_Data_PagerAdapter);
		
		
	}
	
	
}
