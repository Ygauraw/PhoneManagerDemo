package com.example.testtelephoemanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputBinding;
import android.widget.TextView;

public class TestTelephonemanager extends Activity {
    private static final String TAG = "TestTelephonemanager";
    private String IMSI;// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
    private String CMCC = "中国移动";
    private String CHINA_UNICOM = "中国联通";
    private String CHINA_TELECOM = "中国电信";
    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tes_telephonemanager);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("运营商：\n"+getTelephoneManager()+"\nCPU 信息：\n"+fetchCPUInfo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tes_telephonemanager, menu);
        return true;
    }

    private String getTelephoneManager() {
        String value = null;
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        this.IMSI = manager.getSubscriberId();
        Log.i(TAG, "current phone server ID:" + this.IMSI);
        Log.i(TAG, "cell location:" + manager.getCellLocation());
        Log.i(TAG, "line 1 number:" + manager.getLine1Number());
        Log.i(TAG, "network type:" + manager.getNetworkType());
        Log.i(TAG, "sim serial number:" + manager.getSimSerialNumber());
        Log.i(TAG, "neighboring cell info:" + manager.getNeighboringCellInfo());
        if (this.IMSI.startsWith("46000") || this.IMSI.startsWith("46002")) {
            value = this.CMCC;
        } else if (this.IMSI.startsWith("46001")) {
            value = this.CHINA_UNICOM;
        } else if (this.IMSI.startsWith("46003")) {
            value = this.CHINA_TELECOM;
        }
        return value;

    }

    public static String fetchCPUInfo() {
        //StringBuffer result = new StringBuffer();
        String reslut="";
        try {
            Process process = new ProcessBuilder().command("/system/bin/cat", "/proc/cpuinfo").redirectErrorStream(true).start();
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                //result.append(new String(re));
                reslut+=new String(re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reslut;

    }
}
