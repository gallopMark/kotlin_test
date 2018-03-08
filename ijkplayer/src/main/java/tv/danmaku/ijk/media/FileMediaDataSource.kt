package tv.danmaku.ijk.media

import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class FileMediaDataSource() : IMediaDataSource {
    private lateinit var mFile: RandomAccessFile
    private var mFileSize: Long = 0

    @Throws(IOException::class)
    constructor(file: File) : this() {
        mFile = RandomAccessFile(file, "r")
        mFileSize = mFile.length()
    }

    @Throws(IOException::class)
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (mFile.filePointer != position)
            mFile.seek(position)
        return if (size == 0) 0 else mFile.read(buffer, 0, size)
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        return mFileSize
    }

    @Throws(IOException::class)
    override fun close() {
        mFileSize = 0
        mFile.close()
    }
}