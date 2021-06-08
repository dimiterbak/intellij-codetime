package com.software.codetime.managers;

import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.event.SlackStateChangeModel;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.*;

import javax.swing.*;

public class FlowManager {
    public static boolean enabledFlow = false;

    public static void toggleFlowMode(boolean automated) {
        if (!enabledFlow) {
            enterFlowMode(automated);
        } else {
            exitFlowMode();
        }
    }

    public static void enterFlowMode(boolean automated) {
        boolean isRegistered = AccountManager.checkRegistration(false, null);
        if (!isRegistered) {
            // show the flow mode prompt
            AccountManager.showModalSignupPrompt("To use Flow Mode, please first sign up or login.", () -> { CodeTimeWindowFactory.refresh(true);});
            return;
        }

        boolean intellij_CtskipSlackConnect = FileUtilManager.getBooleanItem("intellij_CtskipSlackConnect");
        boolean workspaces = SlackManager.hasSlackWorkspaces();
        if (!workspaces && !intellij_CtskipSlackConnect) {
            String msg = "Connect a Slack workspace to pause notifications and update your status?";

            Object[] options = {"Connect", "Skip"};
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showOptionDialog(
                        null, msg, "Slack connect", JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    SlackStateChangeModel changeModel = new SlackStateChangeModel();
                    SlackManager.connectSlackWorkspace(() -> {
                        CodeTimeWindowFactory.refresh(true);
                    });
                } else {
                    FileUtilManager.setBooleanItem("intellij_CtskipSlackConnect", true);
                    FlowManager.enterFlowMode(automated);
                }
            });
            return;
        }

        FlowModeClient.enterFlowMode(automated);

        FlowMode flowMode = UtilManager.gson.fromJson(FileUtilManager.getItem("flowMode"), FlowMode.class);

        if (flowMode.editor.intellij.screenMode.contains("Full Screen")) {
            ScreenManager.enterFullScreen();
        } else {
            ScreenManager.exitFullScreen();
        }

        SlackManager.clearSlackCache();

        CodeTimeWindowFactory.refresh(false);

        enabledFlow = true;
    }

    public static void exitFlowMode() {
        FlowModeClient.exitFlowMode();

        ScreenManager.exitFullScreen();

        SlackManager.clearSlackCache();

        CodeTimeWindowFactory.refresh(false);

        enabledFlow = false;
    }

    public static boolean isInFlowMode() {
        if (!enabledFlow) {
            return false;
        }

        return FlowModeClient.isFlowModeOn();
    }

    public static boolean isScreenStateInFlow() {
        ConfigSettings settings = ConfigManager.getConfigSettings();
        boolean screenInFlowState = false;
        if (settings.screenMode.contains("Full Screen") && ScreenManager.isFullScreen()) {
            screenInFlowState = true;
        } else if (settings.screenMode.contains("None") && !ScreenManager.isFullScreen()) {
            screenInFlowState = true;
        }

        return screenInFlowState;
    }
}
