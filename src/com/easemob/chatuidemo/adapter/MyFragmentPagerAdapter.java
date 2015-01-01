package com.easemob.chatuidemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	private Fragment[] fragments;
	public MyFragmentPagerAdapter(FragmentManager fm,Fragment[] fragments) {
		super(fm);
		this.fragments = fragments;
		
	}
	
	@Override
	public int getCount() {
		return fragments.length;
	}
	
	@Override
	public Fragment getItem(int position) {
		return fragments[position];
	}
	
	
}
