package com.algonquinlive.corm0096.doorsopenottawa.HTTPManagement;

import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manage HTTP connections.
 *
 * Supported methods:
 * + getData() :: String
 *
 * @author David Gassner
 *
 * Modfied by Gerald.Hurdle@AlgonquinCollege.com: Enumerated type
 */

public class HttpManager {

    /**
     * Return the HTTP response from uri
     *
     * @param p RequestPackage
     * @return String the response; null when exception
     */
    public static String getData(RequestPackage p) {

        BufferedReader reader = null;
        String uri = p.getUri();

        byte[] loginBytes = ("corm0096:password").getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        if (p.getMethod() == HttpMethod.GET) {
            uri += "?" + p.getEncodedParams();
        }

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod().toString());
            con.addRequestProperty("Authorization", loginBuilder.toString());

            JSONObject json = new JSONObject(p.getParams());
            String params = json.toString();

            if (p.getMethod() == HttpMethod.POST || p.getMethod() == HttpMethod.PUT) {
                con.addRequestProperty("Accept", "application/json");
                con.addRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(params);
                writer.flush();
            }

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}