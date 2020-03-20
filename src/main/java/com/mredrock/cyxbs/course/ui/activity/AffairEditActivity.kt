package com.mredrock.cyxbs.course.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.TimeSelectedAdapter
import com.mredrock.cyxbs.course.adapters.WeekSelectedAdapter
import com.mredrock.cyxbs.course.adapters.YouMightAdapter
import com.mredrock.cyxbs.course.databinding.CourseActivityEditAffairBinding
import com.mredrock.cyxbs.course.ui.AffairTransitionAnimHelper
import com.mredrock.cyxbs.course.ui.fragment.RemindSelectDialogFragment
import com.mredrock.cyxbs.course.ui.fragment.TimeSelectDialogFragment
import com.mredrock.cyxbs.course.ui.fragment.WeekSelectDialogFragment
import com.mredrock.cyxbs.course.viewmodels.EditAffairViewModel
import kotlinx.android.synthetic.main.course_activity_edit_affair.*

/**
 * @author Jon
 * @date 2019/12/21 20:35
 * description：具有三个状态，添加标题，添加内容，最后修改，
 *              分别对应了Helper里面的几个切换方法
 */

class AffairEditActivity : BaseActivity() {

    override val isFragmentActivity: Boolean
        get() = true

    private lateinit var mBinding: CourseActivityEditAffairBinding

    //周数选择BottomSheetDialog
    val mWeekSelectDialogFragment: WeekSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        WeekSelectDialogFragment(this)
    }

    //时间选择BottomSheetDialog
    val mTimeSelectDialogFragment: TimeSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        TimeSelectDialogFragment(this)
    }

    //提醒选择BottomSheetDialog
    private val mRemindSelectDialogFragment: RemindSelectDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        RemindSelectDialogFragment(this)
    }

    private lateinit var affairTransitionAnimHelper:AffairTransitionAnimHelper

    lateinit var mEditAffairViewModel: EditAffairViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditAffairViewModel = ViewModelProviders.of(this).get(EditAffairViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.course_activity_edit_affair)
        mBinding.editAffairViewModel = mEditAffairViewModel
        mBinding.lifecycleOwner = this
        affairTransitionAnimHelper = AffairTransitionAnimHelper(this)
        initActivity()
    }

    private fun initActivity() {
        mEditAffairViewModel.initData(this)
        tv_week_select.adapter = WeekSelectedAdapter(mEditAffairViewModel.mPostWeeks, this)
        tv_time_select.adapter = TimeSelectedAdapter(mEditAffairViewModel.mPostClassAndDays, this)
        tv_remind_select.setOnClickListener {
            if (!mRemindSelectDialogFragment.isShowing) {
                mRemindSelectDialogFragment.show()
            }
        }
        course_next_step.setOnClickListener {
            forward()
        }
        et_title_content_input.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                return if (p1 == EditorInfo.IME_ACTION_NEXT) {
                    forward()
                    return true
                } else false
            }
        })
        mEditAffairViewModel.content.observe(this, Observer {
            et_title_content_input.setText(it)
            et_title_content_input.setSelection(it.length)
        })

        course_back.setOnClickListener { finish() }
        //必须在ViewModel的initData之后执行
        if (mEditAffairViewModel.passedAffairInfo != null) {
            affairTransitionAnimHelper.modifyPageLayout()
        }
        mEditAffairViewModel.titleCandidateList.observe(this, Observer {
            rv_you_might.adapter = YouMightAdapter(it, et_title_content_input)
        })
    }

    /**
     * 不断进行下一步，根据状态执行相应动画
     */
    private fun forward() {
        when (mEditAffairViewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> affairTransitionAnimHelper.addTitleNextMonitor()
            EditAffairViewModel.Status.ContentStatus -> affairTransitionAnimHelper.addContentNextMonitor()
            EditAffairViewModel.Status.AllDoneStatus -> postAffair()
        }
    }

    /**
     * 不断进行后退，根据状态执行相应动画，或者直接退出activity
     */
    override fun onBackPressed() {
        when (mEditAffairViewModel.status) {
            EditAffairViewModel.Status.TitleStatus -> super.onBackPressed()
            EditAffairViewModel.Status.ContentStatus -> affairTransitionAnimHelper.backAddTitleMonitor()
            //如果是修改事务，此时按返回键直接退出
            EditAffairViewModel.Status.AllDoneStatus -> {
                if (mEditAffairViewModel.passedAffairInfo != null) {
                    super.onBackPressed()
                } else {
                    affairTransitionAnimHelper.backAddContentMonitor()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        affairTransitionAnimHelper.unBindActivity()
    }

    /**
     * 最后一步上传事务
     */
    private fun postAffair() {
        when {
            TextUtils.isEmpty(mBinding.etTitle.text.trim()) -> {
                Toast.makeText(this, resources.getString(R.string.course_title_is_null),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.mPostWeeks.isEmpty() -> {
                Toast.makeText(this, resources.getString(R.string.course_week_is_not_select),
                        Toast.LENGTH_SHORT).show()
            }
            mEditAffairViewModel.mPostClassAndDays.isEmpty() -> {
                Toast.makeText(this, R.string.course_time_is_not_select, Toast.LENGTH_SHORT).show()
            }
            else -> {
                mEditAffairViewModel.postOrModifyAffair(this, mBinding.etTitle.text.toString(),
                        mBinding.etTitleContentInput.text.toString())
            }
        }
    }

    companion object {
        const val AFFAIR_INFO = "affairInfo"
        const val WEEK_NUM = "weekString"
        const val TIME_NUM = "timeNum"
    }
}
