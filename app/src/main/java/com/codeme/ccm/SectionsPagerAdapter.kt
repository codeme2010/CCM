package com.codeme.ccm

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.ViewGroup
import java.util.*

class SectionsPagerAdapter(private val fm: FragmentManager, private val fg: List<Fragment>,
                           private val titleList: ArrayList<String>) : FragmentPagerAdapter(fm) {
    private val mTagList: MutableList<String>? = ArrayList()

    override fun getItem(arg0: Int) = fg[arg0]

    override fun getCount() = fg.size

    override fun getPageTitle(position: Int) = titleList[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mTagList!!.size == 0) {
            for (i in 0..1) {
                mTagList.add(i, "android:switcher:" + container.id + ":" + i)
            }
        }
        return super.instantiateItem(container, position)
    }

    fun update(position: Int) {
        try {
            val fragment = fm.findFragmentByTag(mTagList!![position])
            when (position) {
                0 -> (fragment as fragment0).update()
                1 -> (fragment as fragment1).update()
            }
        } catch (e: Exception) {
            Log.e("****", e.message)
        }

    }

}