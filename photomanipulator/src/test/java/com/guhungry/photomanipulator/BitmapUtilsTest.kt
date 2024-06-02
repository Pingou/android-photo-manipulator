package com.guhungry.photomanipulator

import android.graphics.*
import com.guhungry.photomanipulator.factory.AndroidFactory
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.*

internal class BitmapUtilsTest {
    @Test
    fun `printText should draw correctly without alignment and thickness`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 99f
            y = 74f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "Text no Thickness", location, 555, 45f, factory = factory)

        verify(paint, times(1)).setColor(555)
        verify(paint, times(1)).setTextSize(45f)
        verify(paint, times(1)).setTextAlign(Paint.Align.LEFT)
        verify(canvas, times(1)).drawText("Text no Thickness", 99f, 96.5f, paint)
        verify(paint, never()).setStrokeWidth(anyFloat())
        verify(paint, never()).setTypeface(any())
        verify(paint, never()).setStyle(any())
    }

    @Test
    fun `printText should draw correctly with all values`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 23f
            y = 14f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        val font = mock(Typeface::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "Text all Values", location, 432, 74f, font, Paint.Align.CENTER, 4f, 45f, factory)

        verify(paint, times(1)).setColor(432)
        verify(paint, times(1)).setTextSize(74f)
        verify(paint, times(1)).setTextAlign(Paint.Align.CENTER)
        verify(paint, times(1)).setStyle(Paint.Style.STROKE)
        verify(paint, times(1)).setTypeface(font)
        verify(paint, times(1)).setStrokeWidth(4f)
        verify(canvas, times(1)).drawText("Text all Values", 23f, 51f, paint)
        verify(canvas, times(1)).rotate(-45f, 23f, 51f)
    }

    @Test
    fun `printText when rotation null should rotate with 0`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 69f
            y = 55f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        val font = mock(Typeface::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "rotation null", location, 772, 84f, font, Paint.Align.RIGHT, 0f, null, factory)

        verify(paint, times(1)).setColor(772)
        verify(paint, times(1)).setTextSize(84f)
        verify(paint, times(1)).setTextAlign(Paint.Align.RIGHT)
        verify(paint, times(1)).setTypeface(font)
        verify(canvas, times(1)).drawText("rotation null", 69f, 97f, paint)
        verify(canvas, times(1)).rotate(-0f, 69f, 97f)
    }

    @Test
    fun `overlay should draw image correctly`() {
        val background = mock(Bitmap::class.java)
        val overlay = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 75f
            y = 95f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.overlay(background, overlay, location, factory)

        verify(paint, times(1)).setXfermode(any<PorterDuffXfermode>())
        verify(canvas, times(1)).drawBitmap(overlay, 75f, 95f, paint)
    }

    @Test
    fun `flip when FlipMode None do nothing`() {
        val background = mock(Bitmap::class.java)

        val actual = BitmapUtils.flip(background, FlipMode.None)

        assertThat(actual, sameInstance(background))
    }

    @Test
    fun `rotate when RotationMode None do nothing`() {
        val background = mock(Bitmap::class.java)

        val actual = BitmapUtils.rotate(background, RotationMode.None)

        assertThat(actual, sameInstance(background))
    }
}