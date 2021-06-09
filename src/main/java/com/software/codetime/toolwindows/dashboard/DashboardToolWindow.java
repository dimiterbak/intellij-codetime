package com.software.codetime.toolwindows.dashboard;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.jcef.JBCefBrowser;
import com.software.codetime.toolwindows.codetime.CodeTimeDisplayHandler;
import org.cef.CefApp;

import javax.swing.*;

public class DashboardToolWindow {

    private JBCefBrowser browser;

    public DashboardToolWindow(ToolWindow toolWindow) {
        browser = new JBCefBrowser();
        browser.getJBCefClient().addDisplayHandler(new SettingsDisplayHandler(), browser.getCefBrowser());
        registerAppSchemeHandler();
        browser.loadURL("http://dashboard/loading.html");
    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory("http", "dashboard", new DashboardSchemeHandlerFactory());
    }

    public JComponent getContent() {
        return browser.getComponent();
    }

    public void refresh() {
        browser.loadURL("http://dashboard/index.html");
    }
}
