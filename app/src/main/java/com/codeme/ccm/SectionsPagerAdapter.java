package com.codeme.ccm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fg = null;
    private ArrayList<String> titleList = null;
    private List<String> mTagList = null;
    private FragmentManager fm;

    SectionsPagerAdapter(FragmentManager fm, List<Fragment> fg, ArrayList<String> titleList) {
        super(fm);
        this.fm = fm;
        this.fg = fg;
        this.titleList = titleList;
        this.mTagList = new ArrayList<>();
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        return fg.get(arg0);
    }

    @Override
    public int getCount() {
        return fg.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return titleList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String name = makeFragmentName(container.getId(), (int) getItemId(position));
        if(mTagList.size()==0||!mTagList.toString().contains(name)){
            mTagList.add(position, name);
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }

    // FragmentPageAdapter源码里给 Fragment 生成标签的方法
    private String makeFragmentName(int viewId, int index) {
        Log.e("instantiateItem: ", viewId + ":" + index + "****" +mTagList.size());
        return "android:switcher:" + viewId + ":" + index;
    }

    public void update(int position){
        try {
        Fragment fragment = fm.findFragmentByTag(mTagList.get(position));
            switch (position){
                case 0:
//                    ((fragment0)fragment).update();break;
                case 2:
//                    ((fragment2)fragment).update();break;
            }
        }
        catch (Exception e){
            Log.e("****", e.getMessage());
        }

    }

}