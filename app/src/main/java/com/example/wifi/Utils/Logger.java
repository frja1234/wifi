package com.example.wifi.Utils;

import android.graphics.PointF;
import android.os.Environment;

import com.example.wifi.Model.wifi.WifiAp;
import com.example.wifi.Model.wifi.WifiSignal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Logger {
    private static String TAG = "Logger";

    private static String getRootDir() {
        return Environment.getExternalStorageDirectory() + "/wifiLocation";
    }

    public static void saveFingerprintData(String type, WifiSignal wifiSignal) {
        List<String> data = new LinkedList<>();

        for (WifiAp ap : wifiSignal.getAp()) {
            data.add(String.format(Locale.ENGLISH, "%s %s %s", ap.getWifiName(), ap.getWifiBssid(), ap.getWifiRssi()));
        }

        String firstLine = String.format(Locale.ENGLISH, "%.2f %.2f %d %s", wifiSignal.getWifiPoint().getWifiMap().getWifiMapX(), wifiSignal.getWifiPoint().getWifiMap().getWifiMapY(), data.size()
                ,getTimeStamp());

        String rootPath = getRootDir();
        String filePath = rootPath + "/" + type + getDateUnderLine() + ".txt";

        try {
            File dir = new File(filePath);
            dir.getParentFile().mkdirs();
            //not use "+"http://37533an013.wicp.vip
            //<<Effective Java>> 51
            StringBuilder sb = new StringBuilder();
            sb.append(firstLine).append("\r\n");

            for (String str : data) {
                sb.append(str).append("\r\n");
            }
            sb.append("\r\n");

            FileWriter fw = new FileWriter(filePath, true);
            try {
                fw.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PointF> getCollectedGrid(String type) {
        String rootPath = getRootDir();
        String filePath = rootPath + "/" + type + getDateUnderLine() + ".txt";

        List<PointF> result = new ArrayList<>();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        }

        try {
            String line = in.readLine();
            while (line != null) {
                if (!line.trim().equals("")) { //no blank line
                    if (!line.contains("|")) {
                        String[] attr = line.split(" ");
                        PointF p = new PointF(Float.valueOf(attr[0]), Float.valueOf(attr[1]));
                        result.add(p);
                    }
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //For create file name
    private static String getDateUnderLine() {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    private static String getTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

}
