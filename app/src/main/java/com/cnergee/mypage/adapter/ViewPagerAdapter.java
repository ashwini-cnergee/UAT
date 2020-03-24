package com.cnergee.mypage.adapter;



import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.cnergee.fragments.ExistingConnFragment;
import com.cnergee.fragments.NewConnFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	final int PAGE_COUNT = 2;
	Context ctx;

	public ViewPagerAdapter(FragmentManager fm,Context ctx) {
		super(fm);
		this.ctx=ctx;
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {

			// Open FragmentTab1.java
		case 0:
			ExistingConnFragment fragmenttab1= new ExistingConnFragment();
			return fragmenttab1;
			
		case 1:
		NewConnFragment newConnFragment= new NewConnFragment();
		return newConnFragment;

		
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

}
