package com.example.spinwheel

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

private const val COMPLETE_ANGLE = 360

class SpinWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ViewGroup(context, attrs, defStyleAttr) {

    var paint: Paint? = null
    var radius: Float? = null
    var angleEnclosedBySector: Int = 0
    private var mContext: Context? = null
    private var mAttrs: AttributeSet? = null
    private var mDefStyleAttr: Int? = 0
    private var circleViews: ArrayList<CircleView?> = arrayListOf()
    private var sectors: List<SectorModel>? = null

    var paddingLeftInDp: Int = 0
    var paddingTopInDp: Int = 0
    var paddingRightInDp: Int = 0
    var paddingBottomInDp: Int = 0
    var isInit: Boolean = false

    init {
        paint = Paint()
        paint?.color = Color.parseColor("#00880D")
        paint?.style = FILL

        setBackgroundColor(Color.TRANSPARENT)

        mContext = context
        mAttrs = attrs
        mDefStyleAttr = defStyleAttr

        init(
            listOf(
                SectorModel(Color.WHITE),
                SectorModel(Color.BLACK),
                SectorModel(Color.WHITE),
                SectorModel(Color.BLACK),
                SectorModel(Color.WHITE),
                SectorModel(Color.BLACK),
                SectorModel(Color.WHITE),
                SectorModel(Color.BLACK),
//                SectorModel(Color.RED),
//                SectorModel(Color.BLUE),
//                SectorModel(Color.MAGENTA),
//                SectorModel(Color.CYAN),
//                SectorModel(Color.YELLOW),
//                SectorModel(Color.GRAY),
            )
        )
    }

    fun init(
        sectors: List<SectorModel>,
        paddingLeftInDp: Int = 16,
        paddingTopInDp: Int = 16,
        paddingRightInDp: Int = 16,
        paddingBottomInDp: Int = 16,
        ) {
        this.sectors = sectors
        angleEnclosedBySector = COMPLETE_ANGLE/8

        this.paddingLeftInDp = paddingLeftInDp
        this.paddingTopInDp = paddingTopInDp
        this.paddingRightInDp = paddingRightInDp
        this.paddingBottomInDp = paddingBottomInDp
    }

    fun spin() {
        val objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 6 * 360f)
        objectAnimator.duration = 6 * 1000L
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.repeatCount = 0
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        objectAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (radius == null || paint == null) return
        canvas.drawCircle(width / 2f, height / 2f, radius!!, paint!!)
        //setPadding(paddingLeftInDp, paddingTopInDp, paddingRightInDp, paddingBottomInDp)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childWidth: Int = childView.getMeasuredWidth()
            val childHeight: Int = childView.getMeasuredHeight()
            val left = 0
            val top = 0

            childView.layout(left, top, left + childWidth, top + childHeight)

            circleViews[i]?.rotation?.let {
                childView.pivotX = radius!!
                childView.pivotY = radius!!
                childView.rotation = it
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, width)
        radius = width/2.0f

        if (!isInit && sectors != null && sectors!!.isNotEmpty()) {

            isInit = true

            for (i in 0 until sectors!!.size) {

                val circleView = CircleView(mContext!!, mAttrs!!, mDefStyleAttr!!).also {

                    it.init(
                        22.5f + i * 45f,
                        i,
                        sectors!![i].color,
                        angleEnclosedBySector,
                        radius!!,
                        paddingLeftInDp,
                        paddingTopInDp,
                        paddingRightInDp,
                        paddingBottomInDp
                    )

                    addView(
                        it,
                        LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.MATCH_PARENT
                        )
                    )
                }

                circleViews.add(circleView)
                circleView.measure(width, height)
            }
        }
    }
}
