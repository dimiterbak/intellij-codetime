package com.software.codetime.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.apache.commons.lang.StringUtils;
import swdc.java.ops.manager.ConfigManager;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.UtilManager;

public class SettingsMenuAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                UtilManager.launchUrl(ConfigManager.app_url + "/preferences");
            });
        });
    }

    @Override
    public void update(AnActionEvent event) {
        String email = FileUtilManager.getItem("name");
        boolean isLoggedIn = StringUtils.isNotBlank(email);
        event.getPresentation().setVisible(isLoggedIn);
        event.getPresentation().setEnabled(true);
    }
}
