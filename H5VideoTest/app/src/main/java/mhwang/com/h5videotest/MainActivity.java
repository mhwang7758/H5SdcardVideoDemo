package mhwang.com.h5videotest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {
    public static final String ROOT_PATH = Environment
            .getExternalStorageDirectory() + File.separator + "mhwang";
    public static final String VIDEO_PATH = ROOT_PATH + File.separator + "video";

    /**
     *  该命令可以初始化http请求根目录
     */
    private static final String VIDEO_DIRECTORY_COMMAND = "busybox-smp httpd -p 127.0.0.1:8080 -h "+
            VIDEO_PATH;

    private void showLog(String msg){
        Log.d("MainActivity=>", msg);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView wv_test = findViewById(R.id.wv_test);

        // 适配6.0系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionGen.with(MainActivity.this)
                    .addRequestCode(100)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
            showLog("sdk more than 6.0");
        } else {
            // 创建APP错误日志文件夹
            initData();
            showLog("sdk less than 6.0");
        }

        loadWebView(wv_test);
        Log.d("onCreate=>", Environment.getExternalStorageDirectory().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    private void hasPermission() {
        initData();
        showLog("permission success");
    }

    @PermissionFail(requestCode = 100)
    private void requestPermissionFail() {
        Toast.makeText(this, "permission deny", Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        // 创建视频文件夹
        File file1 = new File(VIDEO_PATH);
        if (!file1.exists()){
            file1.mkdirs();
        }

        try {
            Runtime.getRuntime().exec(VIDEO_DIRECTORY_COMMAND);
            showLog("执行命令:"+VIDEO_DIRECTORY_COMMAND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadWebView(WebView webView){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/test.html");

    }
}
