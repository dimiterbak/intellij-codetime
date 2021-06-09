package com.software.codetime.toolwindows.dashboard;

import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.WebviewClosedConnection;
import com.software.codetime.toolwindows.WebviewOpenedConnection;
import com.software.codetime.toolwindows.WebviewResourceState;
import org.apache.commons.lang.StringUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import swdc.java.ops.manager.AppleScriptManager;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.http.ClientResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URL;

public class DashboardResourceHandler implements CefResourceHandler {

    public final static String config_settings_api = "/users/me/edit_preferences";
    public final static String dashboard_api = "/v1/plugin_dashboard";

    private WebviewResourceState state = new WebviewClosedConnection();

    private static String html_api = dashboard_api;

    public static void updateHtmlApi(String api) {
        html_api = api;
    }

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = cefRequest.getURL();
        if (StringUtils.isNotBlank(url)) {

            String pathToResource = url.replace("http://dashboard", "dashboard");
            URL resourceUrl = getClass().getClassLoader().getResource(pathToResource);

            // load the fetched html
            File f = new File(FileUtilManager.getCodeTimeDashboardHtmlFile());
            String api = html_api;
            if (html_api.equals(config_settings_api)) {
                // add the query string
                api += "?editor=intellij&isLightMode=" + !AppleScriptManager.isDarkMode();
                f = new File(FileUtilManager.getCodeTimeSettingsHtmlFile());
            }
            loadApiHtml(resourceUrl, f, api);

            cefCallback.Continue();
            return true;
        }
        return false;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        state.getResponseHeaders(cefResponse, responseLength, redirectUrl);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        return state.readResponse(dataOut, designedBytesToRead, bytesRead, callback);
    }

    @Override
    public void cancel() {
        state.close();
        state = new WebviewClosedConnection();
    }

    private void loadApiHtml(URL resourceUrl, File htmlFile, String api) {
        Writer writer = null;
        try {
            ClientResponse resp = OpsHttpClient.softwareGet(api, FileUtilManager.getItem("jwt"));
            String html = resp.getJsonObj().get("html").getAsString();

            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(htmlFile), StandardCharsets.UTF_8));
            writer.write(html);
        } catch (Exception e) {
            System.out.println("Dashboard write error: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Writer close error: " + e.getMessage());
                }
            }
        }

        try {
            resourceUrl = htmlFile.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            state = new WebviewOpenedConnection(resourceUrl.openConnection());
        } catch (Exception e) {
            //
        }
    }

}
