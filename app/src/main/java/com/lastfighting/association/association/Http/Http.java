package com.lastfighting.association.association.Http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * Created by cz542 on 2016/9/22.
 */

public class Http {
    static String sessionid = null;

    public static String Get(String Url, RetData retData) {
        String result = null;
        URL url = null;
        StringBuffer sbContent = new StringBuffer();
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(Url);
            connection = (HttpURLConnection) url.openConnection();
            if(sessionid != null)
                connection.setRequestProperty("Cookie", sessionid);

            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();

            retData.retcode = connection.getResponseCode();
            Map<String, List<String>> map = connection.getHeaderFields();
            List<String> cookieval = map.get("Set-Cookie");
            if(cookieval != null) {
                sessionid = cookieval.get(0);
            }

        }catch(SocketTimeoutException e) {
            Log.e("connErr", "potalTestGet: failed to connect");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    public static StringBuffer Post(String Url, String PostData, RetData retData) {
        URL url = null;
        StringBuffer sbContent = new StringBuffer();
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(Url);
            connection = (HttpURLConnection) url.openConnection();
            if(sessionid != null)
                connection.setRequestProperty("Cookie", sessionid);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            DataOutputStream dop = new DataOutputStream(
                    connection.getOutputStream());
            if(PostData != null)
                dop.writeBytes(PostData);
            dop.flush();
            dop.close();

            retData.retcode = connection.getResponseCode();
            Map<String, List<String>> map = connection.getHeaderFields();
            List<String> cookieval = map.get("Set-Cookie");
            if(cookieval != null) {
                sessionid = cookieval.get(0);
            }

            String inputLine = null;
            BufferedReader result = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            while ((inputLine = result.readLine()) != null) {
                sbContent.append(inputLine);
            }
            result.close();
        }catch(SocketTimeoutException e) {
            Log.e("connErr", "potalTestGet: failed to connect");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return sbContent;
    }
}
