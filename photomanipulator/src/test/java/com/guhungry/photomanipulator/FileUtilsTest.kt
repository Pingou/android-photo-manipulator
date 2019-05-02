package com.guhungry.photomanipulator

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import com.guhungry.photomanipulator.helper.AndroidFile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import java.io.*

internal class FileUtilsTest {
    private var context: Context? = null

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        context = null
    }

    @Test
    fun `cachePath should throw error when all cache dir is null`() {
        context = mock(Context::class.java)

        val exception = assertThrows<IOException> { FileUtils.cachePath(context!!) }

        assertThat(exception, instanceOf(IOException::class.java))
        assertThat(exception.message, equalTo("No cache directory available"))
    }

    @Test
    fun `cachePath should externalCacheDir when internalCacheDir is null`() {
        val dir = mock(File::class.java)
        context = mock(Context::class.java)
        `when`(context!!.externalCacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should internalCacheDir when externalCacheDir is null`() {
        val dir = mock(File::class.java)
        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should externalCacheDir when external has more free space`() {
        val external = mock(File::class.java)
        `when`(external.freeSpace).thenReturn(55555)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(external))
    }

    @Test
    fun `cachePath should internalCacheDir when external has less free space`() {
        val external = mock(File::class.java)
        `when`(external.freeSpace).thenReturn(1111)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(internal))
    }

    @Test
    fun `cleanDirectory should delete file with starts with prefix`() {
        val directory = mock(File::class.java)
        val files = arrayOf(mock(File::class.java))
        `when`(directory.listFiles(any<FilenameFilter>())).thenReturn(files)

        FileUtils.cleanDirectory(directory, "DELETE ME")

        verify(files[0], times(1)).delete()
    }

    @Test
    fun `createTempFile should return temp file correctly`() {
        val helper = mock(AndroidFile::class.java)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)

        FileUtils.createTempFile(context!!, "PREFIX", MimeUtils.JPEG, helper)

        verify(helper, times(1)).createTempFile("PREFIX", ".jpg", internal)
    }

    @Test
    fun `saveImageFile should save correct data`() {
        val image = mock(Bitmap::class.java)
        val uri = mock(File::class.java)
        val output = mock(FileOutputStream::class.java)
        val helper = mock(AndroidFile::class.java)
        `when`(helper.makeFileOutputStream(uri)).thenReturn(output)

        FileUtils.saveImageFile(image, MimeUtils.JPEG, 32, uri, helper)

        verify(image, times(1)).compress(Bitmap.CompressFormat.JPEG, 32, output)
    }

    @Test
    fun `openBitmapInputStream when local file should open local stream`() {
        val contentResolver = mock(ContentResolver::class.java)
        `when`(contentResolver.openInputStream(any())).thenReturn(mock(InputStream::class.java))
        context = mock(Context::class.java)
        `when`(context!!.contentResolver).thenReturn(contentResolver)
        val factory = MockAndroidFactory()

        FileUtils.openBitmapInputStream(context!!, "file://local/path/for/sure", factory)
        FileUtils.openBitmapInputStream(context!!, "content://local/content/path/for/sure", factory)
        FileUtils.openBitmapInputStream(context!!, "android.resource://local/resource/path/for/sure", factory)

        verify(contentResolver, times(3)).openInputStream(any())
    }
}