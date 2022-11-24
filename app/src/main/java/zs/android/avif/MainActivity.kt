package zs.android.avif

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.aomedia.avif.android.AvifDecoder
import org.aomedia.avif.android.PrintLogUtils
import zs.android.avif.databinding.ActivityMainBinding
import java.io.File
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var openDocument: ActivityResultLauncher<Array<String>>
    private lateinit var takePicturePreview: ActivityResultLauncher<Void?>
    private val imageList: ArrayList<Bitmap> by lazy { arrayListOf() }
    private var groupName = "480P"


    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("avif")
        }

        class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView = itemView.findViewById<AppCompatImageView>(R.id.acIv)
        }
    }

    private lateinit var componIcon1:ComponentName
    private lateinit var componIcon2:ComponentName
    private lateinit var componDefault:ComponentName

    private fun loadLaunchIcon(){
        componIcon1= ComponentName(this,"$packageName.icon1")
        componIcon2= ComponentName(this,"$packageName.icon2")
        componDefault= ComponentName(this,"$packageName.MainActivity")
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.title = groupName

        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = imageAdapter
        }


        intentActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    //获取返回的结果
                }
//                Log.i("print_logs", "MainActivity::onCreate: intentActivityResultLauncher")
                openDocument.launch(
                    arrayOf("text/plain")
                )
            }

        //拍照
        takePicturePreview =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                showBitmap(it)
            }

        //用于请求一组权限
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!!) {
//                    Log.i("print_logs", "requestMultiplePermissions 同意：READ_EXTERNAL_STORAGE")

                } else {
//                    Log.e("print_logs", "requestMultiplePermissions 拒绝：READ_EXTERNAL_STORAGE")
                }

                if (it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!! && it[Manifest.permission.READ_EXTERNAL_STORAGE]!!) {
                    Toast.makeText(this, "解码开始！", Toast.LENGTH_LONG).show();

                    for (index in 1..10) {
                        for (i in 1..10) {
                            Log.i("print_logs", "MainActivity::onCreate: 内侧：$i")

                            checkAvif("${index}.avif", groupName)
                        }
                    }
                    Toast.makeText(this, "解码完成!，请打开文档：AvifLog.txt", Toast.LENGTH_LONG).show();

                } else {
                }

//                if (it[Manifest.permission.CAMERA]!!) {
//                    Log.i("print_logs", "requestMultiplePermissions 同意：CAMERA")
////                    takePicturePreview.launch(null)
//                } else {
//                    Log.e("print_logs", "requestMultiplePermissions 拒绝：CAMERA")
//                }
            }

        val stringBuilder = StringBuilder()
        //提示用户选择文档（可以选择一个），分别返回它们的Uri
        openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                val path = Uri2PathUtil.getRealPathFromUri(this, it)
                Log.i("print_logs", "openDocument $path")
//                val localBitmap = BitmapFactory.decodeFile(path)
//                showBitmap(localBitmap)
//                val intent = FileUtils.openFile(this, path)
//                startActivity(intent)

                if (path != null) {
                    if (path.contains("AvifLog.txt")) {
                        val result = FileUtils.getFileContent(path)
                        stringBuilder.append("$groupName：").append("\n")
                        stringBuilder.append(result).append("\n")
                        mBinding.acTvInfo.text = stringBuilder.toString()
                    }
                }
            }
        }
        mBinding.acTvSelectFile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    requestAllFilesPermission()
                } else {
                    openDocument.launch(
                        arrayOf("text/plain") //"image/*",
                    )
                }
            } else {
                openDocument.launch(
                    arrayOf("text/plain")
                )
            }
        }

        mBinding.acTvAssets.setOnClickListener {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    /**
     * 打开允许访问所有文件权限
     */
    private lateinit var intentActivityResultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestAllFilesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        intentActivityResultLauncher.launch(intent)
    }

    private fun checkAvif(imageName: String, groupPath: String? = null) {
        PrintLogUtils.init(this)
        //验证图片是不是AVif格式
        Uri2PathUtil.readFileFromAssets2(this, groupPath, imageName)?.let { avif ->
            val result = AvifDecoder.isAvifImage(avif)

            if (result) {
                //获取AVif图片信息
                val info = AvifDecoder.Info()
                val resultInfo = AvifDecoder.getInfo(avif, avif.remaining(), info)
                //展示AVif图片
                val bm = Bitmap.createBitmap(info.width, info.height, Bitmap.Config.ARGB_8888)
                val bimp = AvifDecoder.decode(avif, avif.remaining(), bm)
                if (bimp) {
                    showBitmap(bm)
                }
            } else {
                Toast.makeText(this, "图片异常！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun showBitmap(bitmap: Bitmap) {
//        imageList.add(bitmap)
//        imageAdapter.notifyItemInserted(imageList.size - 1)
//        mBinding.recyclerView.scrollToPosition(imageList.size - 1)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_avif, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_480P -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected: 480P")
                groupName = "480P"
            }
            R.id.item_540P -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected: 540P")
                groupName = "540P"
            }
            R.id.item_720P -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected: 720P")
                groupName = "720P"
            }
            R.id.item_1080P -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected: 1080P")
                groupName = "1080P"
            }
            R.id.item_4k -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected: 4K")
                groupName = "4K"
            }
            else -> {
                Log.i("print_logs", "MainActivity::onOptionsItemSelected Default: 480P")
                groupName = "480P"
            }
        }
        supportActionBar?.title = groupName

        imageList.clear()
        imageAdapter.notifyDataSetChanged()
        PrintLogUtils.getInstance().resetLog()
        return super.onOptionsItemSelected(item)
    }


    private val imageAdapter = object : RecyclerView.Adapter<ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view =
                LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_image, null, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.imageView.setImageBitmap(imageList[position])
        }

        override fun getItemCount(): Int = imageList.size
    }


    override fun onDestroy() {
        super.onDestroy()
        PrintLogUtils.getInstance().resetLog()
    }
}