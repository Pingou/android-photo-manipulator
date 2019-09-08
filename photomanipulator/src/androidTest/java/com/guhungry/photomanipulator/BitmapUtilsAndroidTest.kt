package com.guhungry.photomanipulator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BitmapUtilsAndroidTest {
    private var background: Bitmap? = null
    private var overlay: Bitmap? = null

    @After
    fun tearDown() {
        background?.recycle()
        background = null
        overlay?.recycle()
        overlay = null
    }

    @Test
    fun overlay_should_overlay_image_at_correct_location() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        overlay = TestHelper.drawableBitmap(R.drawable.overlay, options)

        BitmapUtils.overlay(background!!, overlay!!, PointF(75f, 145f))

        assertThat(background!!.colorSpace, equalTo(overlay!!.colorSpace))
        assertThat(background!!.getPixel(75 + 96, 145 + 70), equalTo(overlay!!.getPixel(96, 70)))
    }

    @Test
    fun readImageDimensions_should_return_correct_dimension() {
        assertReadImageDimensions(R.drawable.background, 800, 530)
        assertReadImageDimensions(R.drawable.overlay, 200, 141)
    }

    private fun assertReadImageDimensions(@DrawableRes res: Int, width: Int, height: Int) {
        val file = TestHelper.drawableUri(res)
        FileUtils.openBitmapInputStream(TestHelper.context(), file).use {
            val size = BitmapUtils.readImageDimensions(it)
            assertThat(size, equalTo(CGSize(width, height)))
        }
    }
}