package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.event.SlackStateChangeModel;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.manager.*;

import javax.swing.*;

public class FlowManager {

    public static void initFlowStatus() {
        boolean enabledFlow = FlowModeClient.isFlowModeOn();
        FileUtilManager.updateFlowChangeState(enabledFlow);
        updateFlowStateDisplay();
    }

    public static void toggleFlowMode(boolean automated) {
        if (!FileUtilManager.getFlowChangeState()) {
            enterFlowMode(automated);
        } else {
            exitFlowMode();
        }
    }

    public static void enterFlowMode(boolean automated) {

        boolean isRegistered = AccountManager.checkRegistration(false, null);
        if (!automated && !isRegistered) {
            // show the flow mode prompt
            AccountManager.showModalSignupPrompt("To use Flow Mode, please first sign up or login.", () -> { CodeTimeWindowFactory.refresh(true);});
            return;
        }

        boolean intellij_CtskipSlackConnect = FileUtilManager.getBooleanItem("intellij_CtskipSlackConnect");
        boolean workspaces = SlackManager.hasSlackWorkspaces();
        boolean isInFlow = FileUtilManager.getFlowChangeState();
        if (!automated && !workspaces && !intellij_CtskipSlackConnect && !isInFlow) {
            String msg = "Connect a Slack workspace to pause\nnotifications and update your status?";

            Object[] options = {"Connect", "Skip"};
            Icon icon = UtilManager.getResourceIcon("app-icon-blue.png", FlowManager.class.getClassLoader());

            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showOptionDialog(
                        null, msg, "Slack connect", JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);

                if (choice == 0) {
                    SlackStateChangeModel changeModel = new SlackStateChangeModel();
                    SlackManager.connectSlackWorkspace(() -> {
                        CodeTimeWindowFactory.refresh(true);
                    });
                } else {
                    FileUtilManager.setBooleanItem("intellij_CtskipSlackConnect", true);
                    FlowManager.enterFlowMode(automated);
                    FileUtilManager.updateFlowChangeState(true);
                }
            });
            return;
        } else if (!isInFlow) {
            // go ahead and make the api call to enter flow mode
            FlowModeClient.enterFlowMode(automated);
            FileUtilManager.updateFlowChangeState(true);
        }

        updateFlowStateDisplay();
    }

    public static void exitFlowMode() {
        boolean isInFlow = FileUtilManager.getFlowChangeState();
        if (isInFlow) {
            FlowModeClient.exitFlowMode();
            FileUtilManager.updateFlowChangeState(false);
        }

        updateFlowStateDisplay();
    }

    private static void updateFlowStateDisplay() {
        ApplicationManager.getApplication().invokeLater(() -> {
            // at least update the status bar
            AsyncManager.getInstance().executeOnceInSeconds(() -> {
                CodeTimeWindowFactory.refresh(false);
            }, 2);
            StatusBarManager.updateStatusBar(null);
        });
    }
}
