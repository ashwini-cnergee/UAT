package com.cnergee.mypage.adapter;



import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.cnergee.fragments.DataUsageFragment;
import com.cnergee.fragments.RenewalHistoryFragments;

public class History_Data_PagerAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	final int PAGE_COUNT = 2;
	Context ctx;

	public History_Data_PagerAdapter(FragmentManager fm,Context ctx) {
		super(fm);
		this.ctx=ctx;
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {

			// Open FragmentTab1.java
		case 0:
			RenewalHistoryFragments fragmenttab1= new RenewalHistoryFragments();
			return fragmenttab1;
			
		case 1:
			DataUsageFragment dataUsageConnFragment= new DataUsageFragment();
			return dataUsageConnFragment;

		
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

}
