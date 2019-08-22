package com.mredrock.cyxbs.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.databinding.CourseFragmentNoCourseInviteBinding
import com.mredrock.cyxbs.course.viewmodels.NoCourseInviteViewModel

/**
 * Created by anriku on 2018/10/6.
 */

class NoCourseInviteFragment : Fragment() {

    private lateinit var mBinding: CourseFragmentNoCourseInviteBinding
    private lateinit var mNoCourseInviteViewModel: NoCourseInviteViewModel
    private var mNowWeek = 0

    companion object {
        const val NOW_WEEK = "nowWeek"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.course_fragment_no_course_invite, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity ?: return

        initFragment()
    }

    private fun initFragment() {
        mBinding.setLifecycleOwner(this)

        mNowWeek = arguments?.getInt(NOW_WEEK) ?: 0
        mNoCourseInviteViewModel = ViewModelProviders.of(activity!!).get(NoCourseInviteViewModel::class.java)
        mBinding.nowWeek = mNowWeek
        mBinding.noCourseInviteViewModel = mNoCourseInviteViewModel

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mNoCourseInviteViewModel.getCourses {
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

}