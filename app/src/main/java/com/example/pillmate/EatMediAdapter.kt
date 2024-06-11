package com.example.pillmate

import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EatMediAdapter(
    private val steps: List<EatMedi>,  // 단계 리스트를 저장
    private val buttonClickListener: (Int) -> Unit,  // 버튼 클릭 리스너
    private val skipClickListener: (Int) -> Unit  // 건너뛰기 버튼 클릭 리스너
) : RecyclerView.Adapter<EatMediAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.bind(step, position)  // 단계 데이터를 뷰 홀더에 바인딩

        // 2단계에서 사진이 있을 경우 이미지 설정
        if (position == 1 && !step.photoPath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(step.photoPath)
            holder.medicheckImage?.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int = steps.size  // 단계 리스트 크기 반환

    override fun getItemViewType(position: Int): Int = steps[position].layoutRes  // 각 단계의 레이아웃 리소스 ID 반환

    inner class StepViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val rootLayout: View = itemView.findViewById(R.id.root_layout)
        private val stepContent: View = itemView.findViewById(R.id.step_content1) ?: itemView.findViewById(R.id.step_content2) ?: itemView.findViewById(R.id.step_content3) ?: itemView.findViewById(R.id.step_content4)
        private val stepButton: Button = itemView.findViewById(R.id.step_button1) ?: itemView.findViewById(R.id.step_button2) ?: itemView.findViewById(R.id.step_button3) ?: itemView.findViewById(R.id.step_button4)
        private val stepCheck: ImageView? = itemView.findViewById(R.id.step_check)
        private val stepText: TextView? = itemView.findViewById(R.id.step1) ?: itemView.findViewById(R.id.step2) ?: itemView.findViewById(R.id.step3) ?: itemView.findViewById(R.id.step4)
        private val stepTitle: TextView? = itemView.findViewById(R.id.step_title1) ?: itemView.findViewById(R.id.step_title2) ?: itemView.findViewById(R.id.step_title3) ?: itemView.findViewById(R.id.step_title4)
        private val passButton: TextView? = itemView.findViewById(R.id.pass1)  // Pass 버튼은 첫 번째 단계에만 존재
        private val stepDescription: TextView? = itemView.findViewById(R.id.step1_description) ?:itemView.findViewById(R.id.step4_description)
        val medicheckImage: ImageView? = itemView.findViewById(R.id.medicheck_image)  // 2단계 이미지 뷰

        fun bind(step: EatMedi, position: Int) {
            // 단계 내용 가시성 설정
            stepContent.visibility = if (step.isVisible) View.VISIBLE else View.GONE
            // root 레이아웃 배경 설정
            rootLayout.setBackgroundResource(if (step.isVisible || step.isCompleted) R.drawable.button_stroke_blue else R.drawable.button_gray)

            // 단계 버튼 클릭 리스너 설정
            stepButton.setOnClickListener {
                buttonClickListener(position)
            }

            // 첫 번째 단계의 Pass 버튼 클릭 리스너 설정
            if (position == 0) {
                passButton?.setOnClickListener {
                    skipClickListener(position)
                }
                passButton?.visibility = View.VISIBLE
            } else {
                passButton?.visibility = View.GONE
            }

            // 단계 텍스트 및 체크 마크 가시성 설정
            if (step.isVisible) {
                stepCheck?.visibility = View.GONE
                stepText?.visibility = View.VISIBLE
                stepTitle?.visibility = View.VISIBLE
            } else {
                if (position < getCurrentStepPosition()) {
                    // 현재 단계 전
                    stepCheck?.visibility = View.VISIBLE
                    stepText?.visibility = View.GONE
                    stepTitle?.visibility = View.VISIBLE
                } else {
                    // 현재 단계 후
                    stepCheck?.visibility = View.GONE
                    stepText?.visibility = View.VISIBLE
                    stepTitle?.visibility = View.VISIBLE
                }
            }
            // 2단계의 medicheck_image 설정
            if (position == 1 && step.photoPath != null) {
                val bitmap = BitmapFactory.decodeFile(step.photoPath)
                medicheckImage?.setImageBitmap(bitmap)
            }

            // step1_description 텍스트 스타일 설정
            if (position == 0) {
                val spannable = SpannableStringBuilder(stepDescription?.text)
                val start = spannable.indexOf("당뇨약 파스타정")
                val end = start + "당뇨약 파스타정".length

                if (start >= 0) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#1E54DF")),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    stepDescription?.text = spannable
                }
            }
            if (position == 3) { // 4번째 단계인 경우
                val spannable = SpannableStringBuilder(stepDescription?.text)
                val start = spannable.indexOf("오늘 오후 7시 30분")
                val end = start + "오늘 오후 7시 30분\n당뇨약 '파스타정'".length

                if (start >= 0) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#1E54DF")),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    stepDescription?.text = spannable
                }
            }

        }
        private fun getCurrentStepPosition(): Int {
            for (i in steps.indices) {
                if (!steps[i].isCompleted) return i
            }
            return steps.size
        }
    }
}
