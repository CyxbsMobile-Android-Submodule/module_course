package com.mredrock.cyxbs.course.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.adapters.ScheduleDetailViewAdapter
import com.mredrock.cyxbs.course.component.ScheduleDetailView
import com.mredrock.cyxbs.course.network.Course

/**
 * This class is used as Dialog wrapper class, this class use singleton pattern to let there is only
 * one Dialog can access.
 *
 * Created by anriku on 2018/8/23.
 */
@SuppressLint("InflateParams")
class ScheduleDetailBottomSheetDialogHelper(val context:Context) {

    private lateinit var mScheduleDetailViewAdapter: ScheduleDetailViewAdapter

    fun showDialog(schedules: MutableList<Course>) {
        val dialog = RedRockBottomSheetDialog(context).apply {
            val layoutInflater = LayoutInflater.from(context)
            val dialogView = layoutInflater.inflate(R.layout.course_dialog_schedule_detail, null)
            setContentView(dialogView)
        }
        mScheduleDetailViewAdapter = ScheduleDetailViewAdapter(dialog, schedules)
        val mScheduleDetailView = dialog.findViewById<ScheduleDetailView>(R.id.schedule_detail_view)
        mScheduleDetailView?.scheduleDetailViewAdapter = mScheduleDetailViewAdapter
        dialog.show()
    }
}