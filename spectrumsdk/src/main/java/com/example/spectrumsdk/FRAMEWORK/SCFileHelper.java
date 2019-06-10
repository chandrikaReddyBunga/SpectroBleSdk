package com.example.spectrumsdk.FRAMEWORK;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ADMIN on 14-05-2019.
 */

public class SCFileHelper {
    private static SCFileHelper ourInstance;
    public Context context;
    String FETCH_FILES_URL_STRING = "http://54.210.61.0:8096/spectrocare/sdkjsonfiles/getallstripes";
    private JSONObject filesObj;
    public FetchJsonInterface listener;

    public static SCFileHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new SCFileHelper();
        }
        return ourInstance;
    }

    public void fillContext(Context context1) {
        context = context1;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    public void getJsonFiles() {
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String response = makePostRequest(FETCH_FILES_URL_STRING);
                    Log.e("response", "calll" + response);
                    return "Success";
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return "";
                }
            }

        }.execute("");
    }

    private String makePostRequest(String stringUrl) throws IOException {

        JSONObject params = new JSONObject();
        try {
            params.put("username", "viswanath3344@gmail.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();

        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(params.toString());
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            try {
                JSONObject jsonObj = new JSONObject(String.valueOf(jsonString));
                filesObj = jsonObj;
                //check if listener is set or not.
                if (listener == null) {
                    //Fire proper event. bitmapList or error message will be sent to
                    //class which set listener.
                }else {
                    listener.onSuccessForLoadJson(filesObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onFailureForLoadJson(e.getMessage());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            listener.onFailureForLoadJson(ex.getMessage());
        }
        uc.disconnect();
        return jsonString.toString();
    }

    public String convertTimestampTodate(String stringData) {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds * 1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }

    public String loadDateFromTimeStamp(String timestamp) {
        String date = "";
        if (timestamp.contains(".")) {
            String timearray[] = timestamp.split("\\.");
            Log.e("timearray", "call" + timearray[0]);
            date = convertTimestampTodate(timearray[0]);
        } else {
            date = timestamp;
        }
        return date;
    }


    public static class FetchJsonLIstener {

        public void setMyCustomListener(FetchJsonInterface listener1) {
            SCFileHelper.getInstance().listener = listener1;
        }
        /*public void fetchJson() {
            SCFileHelper.getInstance().getJsonFiles();
        }*/
    }

    //In this interface, you can define messages, which will be send to owner.
    public interface FetchJsonInterface {
        //In this case we have two messages,
        //the first that is sent when the process is successful.
        void onSuccessForLoadJson(JSONObject bitmapList);
        //And The second message, when the process will fail.
        void onFailureForLoadJson(String error);

        void onSuccessForConfigureJson(JSONObject bitmapList);

        void onFailureForConfigureJson(String bitmapList);

    }

}
