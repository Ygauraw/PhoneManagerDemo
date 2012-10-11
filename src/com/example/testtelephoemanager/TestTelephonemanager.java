package com.example.testtelephoemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.InputBinding;
import android.widget.TextView;

public class TestTelephonemanager extends Activity {
    private static final String TAG = "TestTelephonemanager";
    private String IMSI;// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
    private String CMCC = "中国移动";
    private String CHINA_UNICOM = "中国联通";
    private String CHINA_TELECOM = "中国电信";
    private TextView tv;
    private ActivityManager activityManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tes_telephonemanager);
        tv = (TextView) findViewById(R.id.tv);
        String[] memory = getTotalMemory();
        tv.setText("设备信息：\n" + getTelephoneManager() + "\n屏幕参数：\n" + getWidthAndHeight() + "\n内存信息：\n" + fetchMemoryInfo() + "\nCPU 信息：\n"
                + fetchCPUInfo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tes_telephonemanager, menu);
        return true;
    }

    private String getTelephoneManager() {
        StringBuffer result = new StringBuffer();
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        this.IMSI = manager.getSubscriberId();
        result.append("\nIMEI:" + manager.getDeviceId());
        result.append("\nIMSI：" + this.IMSI);
        result.append("\n手机号:" + (manager.getLine1Number() == null ? "未知" : manager.getLine1Number()));
        String networkType;
        switch (manager.getNetworkType()) {
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            networkType = "未知";
            break;
        case TelephonyManager.NETWORK_TYPE_GPRS:
            networkType = "GPRS";
            break;
        case TelephonyManager.NETWORK_TYPE_EDGE:
            networkType = "EDGE";
            break;
        case TelephonyManager.NETWORK_TYPE_CDMA:
            networkType = "CDMA";
            break;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
            networkType = "HSDPA";
            break;
        case TelephonyManager.NETWORK_TYPE_UMTS:
            networkType = "UMTS";
            break;
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
            networkType = "EVDO";
            break;
        default:
            networkType = "其他";
            break;
        }
        result.append("\n网络链接：" + networkType);
        if (this.IMSI.startsWith("46000") || this.IMSI.startsWith("46002")) {
            result.append("\n运营商：" + this.CMCC);
        } else if (this.IMSI.startsWith("46001")) {
            result.append("\n运营商：" + this.CHINA_UNICOM);
        } else if (this.IMSI.startsWith("46003")) {
            result.append("\n运营商：" + this.CHINA_TELECOM);
        }
        String mType = android.os.Build.MODEL;
        result.append("\n手机型号:" + mType);
        return result.toString();

    }

    private String fetchCPUInfo() {
        String result = "";
        try {
            Process process = new ProcessBuilder().command("/system/bin/cat", "/proc/cpuinfo").redirectErrorStream(true).start();
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result += new String(re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * fetch file /proc/meminfo which stored memory info
     * 
     * @return
     */
    private String fetchMemoryInfo() {
        String result = "";
        try {
            Process process = new ProcessBuilder().command("/system/bin/cat", "/proc/meminfo").redirectErrorStream(true).start();
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result += new String(re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String[] getTotalMemory() {
        String[] result = { "", "" };
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);
        long mTotalMem = 0;
        long mAvailMem = mi.availMem;
        String string;
        String[] arrayofStrings;
        try {
            FileReader localfilereader = new FileReader("/proc/meminfo");
            BufferedReader localBufferedReader = new BufferedReader(localfilereader);
            string = localBufferedReader.readLine();
            arrayofStrings = string.split("\\s+");// \\s 空白匹配，MemTotal: 635684
                                                  // kB----》[MemTotal:, 635684,
                                                  // kB]
            mTotalMem = Integer.valueOf(arrayofStrings[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result[0] = Formatter.formatFileSize(this, mTotalMem);
        result[1] = Formatter.formatFileSize(this, mAvailMem);
        Log.i(TAG, "memory info:" + result[0] + "used:" + result[1]);
        return result;
    }

    private String getWidthAndHeight() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int density = metrics.densityDpi;
        Log.i(TAG, "width pix=" + width + ",height pix=" + height + ",density dpi=" + density);
        return "width pix=" + width + ",height pix=" + height + ",density dpi=" + density;
    }
}
