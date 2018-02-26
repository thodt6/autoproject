/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public class HttpClient {

    private String urlBase;
    private String[] params;
    private String[] values;
    private boolean getMethod = true;
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HttpClient.class.getName());

    public HttpClient() {

    }

    public HttpClient(String url) {
        this.urlBase = url;
    }

    /**
     * @return the urlBase
     */
    public String getUrlBase() {
        return urlBase;
    }

    /**
     * @param urlBase the urlBase to set
     */
    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    /**
     * @return the params
     */
    public String[] getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(String[] params) {

        this.params = params;
    }

    /**
     * @return the values
     */
    public String[] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
        this.values = values;
    }

    /**
     * @return the getMethod
     */
    public boolean isGetMethod() {
        return getMethod;
    }

    /**
     * @param getMethod the getMethod to set
     */
    public void setGetMethod(boolean getMethod) {
        this.getMethod = getMethod;
    }

    public String getDataByGetMethod(String[] params, String[] values) {
        this.params = params;
        this.values = values;
        HttpURLConnection httpConn = null;
//      UrlBuilder builder=UrlBuilder.fromString(this.getUrlBase());
        URL url;
        String mURL;
        if (params == null || params.length == 0) {
            mURL = this.getUrlBase();
        } else {
            if (!this.getUrlBase().contains("?")) {
                mURL = this.getUrlBase() + "?";
            } else {
                mURL = this.getUrlBase() + "&";
            }
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals("")) {
                    try {
                        mURL = mURL + params[i] + "=" + URLEncoder.encode(values[i], "UTF-8") + "&";
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (mURL.endsWith("&")) {
            mURL = mURL.substring(0, mURL.length() - 1);
        }
        //mURL=mURL.replaceAll(" ","%20");
//      for(int i=0;i<params.length;i++){
//         builder.addParameter(params[i],values[i]); 
//      }
//      String mURL=builder.toString();
        log.info(mURL);
        InputStream is = null;
        try {
            url = new URL(mURL);
            // Open an HTTP Connection object
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("Accept-Charset", "UTF-8");
            httpConn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            httpConn.setRequestMethod("GET");
            httpConn.setDoOutput(true);
            int respCode = httpConn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                StringBuilder strB = new StringBuilder();
                String str;
                while (null != (str = input.readLine())) {
                    strB.append(str).append("\r\n");
                }
                return strB.toString();
            } else {
                //log.info("Error in opening HTTP Connection. Error#" + respCode);
                return "";
            }
        } catch (IOException ex) {
            return "";
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (IOException ex) {
                return "";
            }
        }
    }

    public String getDataByPostMethod(String[] params, String[] values) {
        this.params = params;
        this.values = values;
        HttpURLConnection httpConn = null;
        URL url;
        String parameters = "";
        for (int i = 0; i < params.length; i++) {
            parameters = parameters + "&" + params[i] + "=" + values[i];
        }
        log.info(parameters);
        parameters = parameters.replaceAll(" ", "%20");
        InputStream is = null;
        OutputStream os = null;
        try {
            //Create connection
            url = new URL(this.getUrlBase());
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            httpConn.setRequestProperty("Content-Language", "en-US");
            httpConn.setUseCaches(false);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            //Send request
            os = httpConn.getOutputStream();
            os.write(parameters.getBytes());
            os.flush();
            os.close();
            //Get Response
            StringBuilder sb = new StringBuilder();
            is = httpConn.getInputStream();
            int chr;
            while ((chr = is.read()) != -1) {
                sb.append((char) chr);
            }
            //log.info(new String(sb.toString().getBytes(),"UTF-8"));
            is.close();
            return new String(sb.toString().getBytes(), "UTF-8");
        } catch (IOException e) {
            log.info(e);
            return "";
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }

}
