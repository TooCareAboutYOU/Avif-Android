package cn.android.avif

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import zs.android.avif.R
import zs.android.avif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var openDocument: ActivityResultLauncher<Array<String>>
    private val imageList: ArrayList<Bitmap> by lazy { arrayListOf() }
    private var groupName = "Kodak24"


    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("avif")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        supportActionBar?.title = groupName

        intentActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    //获取返回的结果
                }
                openDocument.launch(
                    arrayOf("text/plain")
                )
            }
        //用于请求一组权限
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!! && it[Manifest.permission.READ_EXTERNAL_STORAGE]!!) {
                    Toast.makeText(this, "解码开始！", Toast.LENGTH_LONG).show()
//                    PrintLogUtils.init(this)

                    val list= Uri2PathUtil.getFileNameFromAssets(this, groupName)

                    for (index in 1..10) {
                        checkAvif("kodim01.avif")
//                        list.forEach {fileName->
//                            checkAvif(fileName)
//                        }
                    }
                    Toast.makeText(this, "解码完成!，请打开文档：AvifLog.txt", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "权限未通过!", Toast.LENGTH_LONG).show();
                }
            }

        val stringBuilder = StringBuilder()
        //提示用户选择文档（可以选择一个），分别返回它们的Uri
        openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                val path = Uri2PathUtil.getRealPathFromUri(this, it)
                Log.i("print_logs", "openDocument $path")
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

    private fun checkAvif(imageName: String) {
        //验证图片是不是AVif格式
        Log.i("print_logs", imageName)
        Uri2PathUtil.readFileFromAssets2(this, groupName, imageName)?.let { avif ->
//            val result = AvifDecoder.isAvifImage(avif)
//
//            if (result) {
//                //获取AVif图片信息
//                val info = AvifDecoder.Info()
//                AvifDecoder.getInfo(avif, avif.remaining(), info)
//                //展示AVif图片
//                val bm = Bitmap.createBitmap(info.width, info.height, Bitmap.Config.ARGB_8888)
//                val bimp = AvifDecoder.decode(avif, avif.remaining(), bm)
//            } else {
//                Toast.makeText(this, "图片异常！", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_avif, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ClassB_4K -> {
                Log.i("print_logs", "onOptionsItemSelected: ClassB_4K")
                groupName = "ClassB_4K"
            }
            R.id.item_ClassC_2K -> {
                Log.i("print_logs", "onOptionsItemSelected: ClassC_2K")
                groupName = "ClassC_2K"
            }
            R.id.item_Kodak24 -> {
                Log.i("print_logs", "onOptionsItemSelected: Kodak24")
                groupName = "Kodak24"
            }
            else -> {
                Log.i("print_logs", "onOptionsItemSelected Default: Kodak24")
                groupName = "Kodak24"
            }
        }
        supportActionBar?.title = groupName

        imageList.clear()
//        PrintLogUtils.getInstance().resetLog()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
//        PrintLogUtils.getInstance().resetLog()
    }
}