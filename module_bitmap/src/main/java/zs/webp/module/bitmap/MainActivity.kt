package zs.webp.module.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.system.Os.close
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bit1 = BitmapFactory.decodeResource(resources, R.drawable.img_withicc)
        val bit2 = BitmapFactory.decodeResource(resources, R.drawable.img_withouticc)

        findViewById<AppCompatImageView>(R.id.acIv1).setOnClickListener {
            Thread {
                writeMsg(bit1, "bit1")
            }.start()
        }

        findViewById<AppCompatImageView>(R.id.acIv2).setOnClickListener {
            Thread {
                writeMsg(bit2, "bit2")
            }.start()
        }
    }

    private fun writeMsg(bit: Bitmap, name: String) {
        val dir = File(externalCacheDir, "data_info")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val time = System.currentTimeMillis()

        val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E", Locale.CHINA)
        val date = Date(time)
        val result = simpleDateFormat.format(date)
        val file = File(dir, "${name}_$time.txt")
        try {
            PrintWriter(FileWriter(file)).apply {
                this.println("time: $result")
                load(bit, this)
                close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            println("完成！")
        }
    }

    private fun load(bit: Bitmap, printWriter: PrintWriter) {
        println("${bit.width}, ${bit.height}")
        val pixels = IntArray(bit.width * bit.height)
        bit.getPixels(pixels, 0, bit.width, 0, 0, bit.width, bit.height)
        for (clr in pixels) {
            val red = clr and 0x00ff0000 shr 16 //取高两位
            val green = clr and 0x0000ff00 shr 8 //取中两位
            val blue = clr and 0x000000ff //取低两位
            printWriter.println("rgb=${red.toBinaryString()}${green.toBinaryString()}${blue.toBinaryString()}")
        }
    }

    private fun Int.toBinaryString(): String =
        if (this == 1) "1" else (this / 2).toBinaryString() + this % 2
}