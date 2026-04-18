package com.bignerdranch.android.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bignerdranch.android.playlistmaker.R
import kotlin.math.min


class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var onClick: (() -> Unit)? = null
    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isPlaying = false

    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private val minViewSize = resources.getDimensionPixelSize(
        R.dimen.playPauseButtonSize
    )

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                imagePlayBitmap = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()
                imagePauseBitmap = getDrawable(R.styleable.PlaybackButtonView_imagePauseResId)?.toBitmap()

            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                isPressed = false

                if (event.x in 0f..width.toFloat() &&
                    event.y in 0f..height.toFloat()
                ) {
                    onClick?.invoke()
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = resolveSize(minViewSize, widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = if (isPlaying) imagePauseBitmap else imagePlayBitmap

        // for night theme
        paint.colorFilter = PorterDuffColorFilter(
            getTintColor(),
            PorterDuff.Mode.SRC_IN
        )

        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, paint)
        }
    }

    private fun getTintColor(): Int {
        return ContextCompat.getColor(context, R.color.play_pause_icon_tint)
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        invalidate()
    }
}