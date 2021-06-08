package com.software.codetime.toolwindows.codetime.html;

import com.intellij.openapi.wm.StatusBar;
import com.software.codetime.managers.StatusBarManager;
import org.apache.commons.lang3.StringUtils;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.SlackManager;
import swdc.java.ops.model.Integration;
import swdc.java.ops.model.Org;
import swdc.java.ops.model.Team;

import java.util.ArrayList;
import java.util.List;

public class DomBuilder {

    public static String getMainHtml() {
        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                getMetaDataHeader() +
                "  <body>\n" +
                getFlowModeComponent() +
                getStatsComponent() +
                getAccountComponent() +
                getTeamComponent() +
                getJsDependencies() +
                "  </body>\n" +
                "</html>";
    }

    private static String getFlowModeComponent() {
        String flowModeLabel = "Enter Flow Mode";
        String flowModeIcon = getFlowModeOffFaIcon();
        if (FlowModeClient.isFlowModeOn()) {
            flowModeLabel = "Exit Flow Mode";
            flowModeIcon = getFlowModeOnFaIcon();
        }
        return "<div class=\"card pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Flow Mode</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">Block out distractions</p>\n" +
                "    <div class=\"top-right\">\n" +
                "      <button type=\"button\" class=\"icon-button\" data-dismiss=\"modal\" aria-label=\"Settings\" onclick=\"onCmdClick('configure')\">\n" +
                "        <span aria-hidden=\"true\">\n" +
                getSettingsSvg() +
                "        </span>\n" +
                "      </button>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <div class=\"d-grid gap-2 col-8 mx-auto\">\n" +
                "    <button type=\"button\" class=\"btn btn-primary\" onclick=\"onCmdClick('toggle_flow')\">\n" +
                flowModeIcon + "\n" +
                flowModeLabel + "\n" +
                "    </button>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getStatsComponent() {
        return "<div class=\"card pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Stats</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">Data in your editor</p>\n" +
                "  </div>\n" +
                getStatsListItems() +
                "</div>\n";
    }

    private static String getAccountComponent() {
        String email = FileUtilManager.getItem("name");
        return "<div class=\"card pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Account</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">" + email + "</p>\n" +
                "  </div>\n" +
                getAccountListItems() +
                "</div>\n";
    }

    private static String getTeamComponent() {
        return "<div class=\"card pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Teams</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">View your team dashboard</p>\n" +
                "  </div>\n" +
                getTeamListItems() +
                "</div>\n";
    }

    private static String getGlobalStyle() {
        return "  <style type=\"text/css\">\n" +
                "    body { line-height: 1; font-size: .9rem; }\n" +
                "    .card { border-radius: 0 }\n" +
                "    .list-group-item { border: 0 none; }\n" +
                "    .accordion-item { border: 0 none; background-color: \"transparent\" }\n" +
                "    .accordion-button { font-size: inherit; }\n" +
                "    .accordion-body { padding: 1px }\n" +
                "    .card > .list-group { border-style: none; }\n" +
                "    button:focus, button:active { outline: none; border-style: none; }\n" +
                "    .cursor-pointer { cursor: pointer; }\n" +
                "    .top-right { position: absolute; top: 18px; right: 16px }\n" +
                "    .icon-button { padding: 0; background-color: \"transparent\"; border: 0; -webkit-appearance: none; cursor: pointer;}\n" +
                "    .icon-button:hover { background-color: rgba(black, 0.4);}\n" +
                "  </style>\n";
    }

    private static String getJsDependencies() {
        return "    <!-- Popper.js then Bootstrap JS -->\n" +
                "    <script src=\"https://kit.fontawesome.com/ef435e26ef.js\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\" integrity=\"sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.min.js\" integrity=\"sha384-Atwg2Pkwv9vp0ygtn1JAojH0nYbwNJLPhwyoVbhoPwBhjQPR5VtM2+xf0Uwh9KtT\" crossorigin=\"anonymous\"></script>\n" +
                customJavascript();
    }

    private static String customJavascript() {
        return "      <script>\n" +
                "       const windowFeatures = \"menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes\";\n" +
                "       function teamClickHandler(org_name, team_id) {\n" +
                "         console.log(JSON.stringify({cmd: 'launch_team', org_name, team_id}));\n" +
                "       }\n" +
                "       function workspaceClickHandler(id) {\n" +
                "         console.log(JSON.stringify({cmd: 'edit_workspace', id}));\n" +
                "       }\n" +
                "       function onCmdClick(cmd) {\n" +
                "         console.log(JSON.stringify({cmd}));\n" +
                "       }\n" +
                "     </script>\n";
    }

    private static String getMetaDataHeader() {
        return "<head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x\" crossorigin=\"anonymous\">\n" +
                "\n" +
                DomBuilder.getGlobalStyle() +
                "    <title>Code Time</title>\n" +
                "  </head>\n";
    }

    private static String getTeamButtonListItem(String label, String org_name, long team_id) {
        return "     <button id=\"`team_${team_id}`\" type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\" onclick=\"teamClickHandler('" + org_name + "', " + team_id + ")\">\n" +
                "        <span style=\"padding-right: 4px\">\n" +
                getTeamSvg() +
                "        </span>\n" +
                label +
                "    </button>\n";
    }

    private static String getWorkspaceButtonListItem(String team_name, String team_domain, long id) {
        return "     <button id=\"`team_${team_id}`\" type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\" onclick=\"workspaceClickHandler(" + id + ")\">\n" +
                "        <span style=\"padding-right: 4px\">\n" +
                getTeamSvg() +
                "        </span>\n" +
                team_name +
                "    </button>\n";
    }

    private static String getCommandButtonItem(String svg, String label, String cmd) {
        return "     <button type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\" onclick=\"onCmdClick('" + cmd + "')\">\n" +
                "        <span style=\"padding-right: 4px\">\n" +
                svg +
                "        </span>\n" +
                label +
                "    </button>\n";
    }

    private static String getCollapseButtonItem(String svg, String label, String accordionChildrenItems) {
        return "<div class=\"accordion accordion-flush\" id=\"workspaceItems\">\n" +
                "  <div class=\"accordion-item\">\n" +
                "     <button type=\"button\" class=\"accordion-button collapsed shadow-none text-nowrap p-2\" data-bs-toggle=\"collapse\" data-bs-target=\"#workspacesBody\" aria-expanded=\"false\" aria-controls=\"workspacesBody\">\n" +
                "        <span style=\"padding-right: 4px\">\n" +
                svg +
                "        </span>\n" +
                label +
                "    </button>\n" +
                "    <div id=\"workspacesBody\" class=\"accordion-collapse collapse\" aria-labelledby=\"headingOne\" data-bs-parent=\"#workspaceItems\">\n" +
                "      <div class=\"accordion-body\">\n" +
                accordionChildrenItems +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getButtonListItemContainer(String listItems) {
        return "<div class=\"list-group mx-2 mt-0\">\n" +
                listItems +
                "</div>\n";
    }

    private static List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        List<Org> orgs = OpsHttpClient.getOrganizations(FileUtilManager.getItem("jwt"));
        if (orgs != null && orgs.size() > 0) {
            for (Org org : orgs) {
                if (org.teams != null && org.teams.size() > 0) {
                    for (Team team : org.teams) {
                        if (StringUtils.isBlank(team.org_name)) {
                            team.org_name = org.name;
                        }
                        teams.add(team);
                    }
                }
            }
        }
        return teams;
    }

    private static String getTeamListItems() {
        List<Team> teams = getTeams();
        StringBuilder sb = new StringBuilder();
        for (Team team : teams) {
            sb.append(getTeamButtonListItem(team.name, team.org_name, team.id));
        }
        return getButtonListItemContainer(sb.toString());
    }

    private static String getWorkspaceListItems() {
        List<Integration> workspaces = SlackManager.getSlackWorkspaces();

        StringBuilder sb = new StringBuilder();
        for (Integration workspace : workspaces) {
            sb.append(getWorkspaceButtonListItem(workspace.team_name, workspace.team_domain, workspace.id));
        }
        sb.append(getCommandButtonItem(getPlusCircleFaIcon(), "Add workspace", "add_workspace"));
        return getButtonListItemContainer(sb.toString());
    }

    private static String getAccountListItems() {
        String toggleStatusLabel = StatusBarManager.showingStatusText() ? "Hide Code Time status" : "Show Code Time status";

        StringBuilder sb = new StringBuilder();
        sb.append(getCommandButtonItem(getPawSvg(), "Switch account", "switch_account"));
        sb.append(getCommandButtonItem(getSettingsSvg(), "Configure settings", "configure"));
        sb.append(getCommandButtonItem(getReadmeSvg(), "Documentation", "readme"));
        sb.append(getCommandButtonItem(getMessageSvg(), "Submit an issue", "submit_issue"));
        sb.append(getCommandButtonItem(getVisibleSvg(), toggleStatusLabel, "toggle_status"));
        sb.append(getCollapseButtonItem(getSlackSvg(), "Workspaces", getWorkspaceListItems()));
        return getButtonListItemContainer(sb.toString());
    }

    private static String getStatsListItems() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCommandButtonItem(getDashboardSvg(), "Dashboard", "dashboard"));
        sb.append(getCommandButtonItem(getPawSvg(), "More data at Software.com", "web_dashboard"));
        return getButtonListItemContainer(sb.toString());
    }

    private static String getPawSvg() {
        return "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M11.6786 13.1612C10.5602 14.2796 8.74693 14.2796 7.62855 13.1612C6.51017 12.0428 6.51015 10.2296 7.62853 9.11118C8.74691 7.9928 10.5602 7.9928 11.6785 9.11118C12.7969 10.2296 12.7969 12.0428 11.6786 13.1612Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M3.57869 9.1112C2.46031 10.2296 2.46031 12.0428 3.57869 13.1612C4.69707 14.2796 6.51017 14.2796 7.62855 13.1612C8.74693 12.0428 8.74691 10.2296 7.62853 9.11118C6.51015 7.9928 4.69707 7.99282 3.57869 9.1112Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M7.62855 13.1612C6.51017 14.2796 4.69707 14.2796 3.57869 13.1612C2.46031 12.0428 2.46031 10.2296 3.57869 9.1112C4.69707 7.99282 6.51015 7.9928 7.62853 9.11118C8.74691 10.2296 8.74693 12.0428 7.62855 13.1612Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.60356 7.0862C6.72194 5.96782 8.53519 5.96782 9.65357 7.0862C10.772 8.20458 10.7719 10.0178 9.65356 11.1362C8.53518 12.2546 6.72208 12.2546 5.6037 11.1362C4.48532 10.0178 4.48517 8.20458 5.60356 7.0862Z\" fill=\"#00B4EE\"/>\n" +
                "<path d=\"M9.65356 11.1362L7.62855 13.1612L5.6037 11.1362L7.62853 9.11118L9.65356 11.1362Z\" fill=\"#00B4EE\"/>\n" +
                "<path d=\"M11.6785 9.11118L9.65356 11.1362L7.62853 9.11118L9.65357 7.0862L11.6785 9.11118Z\" fill=\"#00B4EE\"/>\n" +
                "<path d=\"M7.62853 9.11118L5.6037 11.1362L3.57869 9.1112L5.60356 7.0862L7.62853 9.11118Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M11.6786 13.1612C12.7969 12.0428 12.7969 10.2296 11.6785 9.11118C10.5602 7.9928 8.74691 7.9928 7.62853 9.11118C6.51015 10.2296 6.51017 12.0428 7.62855 13.1612C8.74693 14.2796 10.5602 14.2796 11.6786 13.1612Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M5.63255 5.23731C6.53786 5.23731 7.27177 4.51261 7.27177 3.61865C7.27177 2.7247 6.53786 2 5.63255 2C4.72723 2 3.99333 2.7247 3.99333 3.61865C3.99333 4.51261 4.72723 5.23731 5.63255 5.23731Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M2.63922 7.9116C3.54454 7.9116 4.27844 7.1869 4.27844 6.29295C4.27844 5.39899 3.54454 4.6743 2.63922 4.6743C1.7339 4.6743 1 5.39899 1 6.29295C1 7.1869 1.7339 7.9116 2.63922 7.9116Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M9.62351 5.23731C10.5288 5.23731 11.2627 4.51261 11.2627 3.61865C11.2627 2.7247 10.5288 2 9.62351 2C8.7182 2 7.98429 2.7247 7.98429 3.61865C7.98429 4.51261 8.7182 5.23731 9.62351 5.23731Z\" fill=\"#00B4EE\"/>\n" +
                "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M12.6168 7.9116C13.5222 7.9116 14.2561 7.1869 14.2561 6.29295C14.2561 5.39899 13.5222 4.6743 12.6168 4.6743C11.7115 4.6743 10.9776 5.39899 10.9776 6.29295C10.9776 7.1869 11.7115 7.9116 12.6168 7.9116Z\" fill=\"#00B4EE\"/>\n" +
                "</svg>";
    }

    private static String getSettingsSvg() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" viewBox=\"0 0 48 48\">\n" +
                "    <path d=\"M0 0h48v48h-48z\" fill=\"none\"/>\n" +
                "    <path d=\"M38.86 25.95c.08-.64.14-1.29.14-1.95s-.06-1.31-.14-1.95l4.23-3.31c.38-.3.49-.84.24-1.28l-4-6.93c-.25-.43-.77-.61-1.22-.43l-4.98 2.01c-1.03-.79-2.16-1.46-3.38-1.97l-.75-5.3c-.09-.47-.5-.84-1-.84h-8c-.5 0-.91.37-.99.84l-.75 5.3c-1.22.51-2.35 1.17-3.38 1.97l-4.98-2.01c-.45-.17-.97 0-1.22.43l-4 6.93c-.25.43-.14.97.24 1.28l4.22 3.31c-.08.64-.14 1.29-.14 1.95s.06 1.31.14 1.95l-4.22 3.31c-.38.3-.49.84-.24 1.28l4 6.93c.25.43.77.61 1.22.43l4.98-2.01c1.03.79 2.16 1.46 3.38 1.97l.75 5.3c.08.47.49.84.99.84h8c.5 0 .91-.37.99-.84l.75-5.3c1.22-.51 2.35-1.17 3.38-1.97l4.98 2.01c.45.17.97 0 1.22-.43l4-6.93c.25-.43.14-.97-.24-1.28l-4.22-3.31zm-14.86 5.05c-3.87 0-7-3.13-7-7s3.13-7 7-7 7 3.13 7 7-3.13 7-7 7z\" fill=\"#00B4EE\"/>\n" +
                "</svg>";
    }

    private static String getReadmeSvg() {
        return "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<path d=\"M10.75 7.8125V8.46875C10.75 8.62344 10.6234 8.75 10.4688 8.75H6.53125C6.37656 8.75 6.25 8.62344 6.25 8.46875V7.8125C6.25 7.65781 6.37656 7.53125 6.53125 7.53125H10.4688C10.6234 7.53125 10.75 7.65781 10.75 7.8125ZM10.4688 9.5H6.53125C6.37656 9.5 6.25 9.62656 6.25 9.78125V10.4375C6.25 10.5922 6.37656 10.7188 6.53125 10.7188H10.4688C10.6234 10.7188 10.75 10.5922 10.75 10.4375V9.78125C10.75 9.62656 10.6234 9.5 10.4688 9.5ZM13 5.09141V12.875C13 13.4961 12.4961 14 11.875 14H5.125C4.50391 14 4 13.4961 4 12.875V3.125C4 2.50391 4.50391 2 5.125 2H9.90859C10.2063 2 10.4922 2.11953 10.7031 2.33047L12.6695 4.29688C12.8805 4.50547 13 4.79375 13 5.09141ZM10 3.21641V5H11.7836L10 3.21641V3.21641ZM11.875 12.875V6.125H9.4375C9.12578 6.125 8.875 5.87422 8.875 5.5625V3.125H5.125V12.875H11.875Z\" fill=\"#00B4EE\"/>\n" +
                "</svg>\n";
    }

    private static String getMessageSvg() {
        return "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<path d=\"M8.00119 3C4.68819 3 2.00029 4.93987 2.00029 7.33398C2.00029 8.36122 2.49412 9.30302 3.31924 10.0469C3.13796 10.5324 2.85875 10.7282 2.54621 10.9449C2.31492 11.1054 1.87527 11.3554 2.03363 11.8451C2.1399 12.166 2.46911 12.3597 2.8275 12.3306C4.02976 12.2347 5.14451 11.9305 6.09882 11.445C6.69683 11.5888 7.3365 11.668 7.9991 11.668C11.3142 11.668 14 9.73017 14 7.33398C14.0021 4.93987 11.3163 3 8.00119 3ZM8.00119 10.6678C7.324 10.6678 6.63848 10.5761 5.9738 10.3761C5.30078 10.772 4.33397 11.3346 3.16713 11.5013C3.8339 11.0012 4.35064 10.2177 4.44232 9.65933C3.64845 9.11758 3.00044 8.30704 3.00044 7.33398C3.00044 5.85668 4.79863 4.00015 8.00119 4.00015C11.2037 4.00015 13.0019 5.85668 13.0019 7.33398C13.0019 8.81337 11.2037 10.6678 8.00119 10.6678ZM6.66765 7.33398C6.66765 7.88615 6.21967 8.33413 5.66751 8.33413C5.11534 8.33413 4.66736 7.88615 4.66736 7.33398C4.66736 6.78181 5.11534 6.33383 5.66751 6.33383C6.21967 6.33383 6.66765 6.78181 6.66765 7.33398ZM9.00134 7.33398C9.00134 7.88615 8.55335 8.33413 8.00119 8.33413C7.44902 8.33413 7.00104 7.88615 7.00104 7.33398C7.00104 6.78181 7.44902 6.33383 8.00119 6.33383C8.55335 6.33383 9.00134 6.78181 9.00134 7.33398ZM11.335 7.33398C11.335 7.88615 10.887 8.33413 10.3349 8.33413C9.7827 8.33413 9.33472 7.88615 9.33472 7.33398C9.33472 6.78181 9.7827 6.33383 10.3349 6.33383C10.887 6.33383 11.335 6.78181 11.335 7.33398Z\" fill=\"#00B4EE\"/>\n" +
                "</svg>\n";
    }

    private static String getVisibleSvg() {
        return "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<path d=\"M7.97278 11.1663L8.65151 12.136C8.43494 12.1564 8.21754 12.1666 8.00001 12.1667C5.50397 12.1667 3.31316 10.8344 2.13847 8.84101C2.04781 8.68718 2 8.51188 2 8.33332C2 8.15476 2.04781 7.97945 2.13847 7.82562C2.63895 6.97633 3.32416 6.24697 4.13897 5.68943L5.32807 7.38816C5.22368 7.68374 5.16668 8.00181 5.16668 8.33333C5.16668 9.89033 6.41949 11.1517 7.97278 11.1663V11.1663ZM13.8615 8.84101C13.2018 9.96053 12.221 10.8714 11.0454 11.4551L11.0456 11.4554L11.9096 12.6887C12.0679 12.915 12.0129 13.2266 11.7867 13.385L11.5135 13.5762C11.2873 13.7346 10.9756 13.6796 10.8173 13.4533L4.09043 3.97791C3.93209 3.75166 3.98709 3.44 4.21334 3.28166L4.48647 3.09041C4.71272 2.93208 5.02438 2.98708 5.18272 3.21333L6.24601 4.72618C6.80584 4.57866 7.39386 4.5 8.00001 4.5C10.496 4.5 12.6869 5.83227 13.8615 7.82564C13.9522 7.97947 14 8.15477 14 8.33333C14 8.51188 13.9522 8.68718 13.8615 8.84101ZM10.8333 8.33333C10.8333 6.7672 9.56588 5.49999 8.00001 5.49999C7.62628 5.49999 7.26967 5.57235 6.94315 5.70358L7.34522 6.26729C7.88415 6.09706 8.49197 6.13889 9.02022 6.42166H9.01959C8.52688 6.42166 8.12751 6.82104 8.12751 7.31374C8.12751 7.80595 8.5264 8.20583 9.01959 8.20583C9.5123 8.20583 9.91167 7.80645 9.91167 7.31374V7.31312C10.2963 8.03166 10.2515 8.93633 9.7398 9.62458V9.62478L10.1422 10.1886C10.5728 9.69166 10.8333 9.0432 10.8333 8.33333ZM7.45863 10.4318L5.84343 8.12437C5.73897 9.21576 6.45413 10.1734 7.45863 10.4318Z\" fill=\"#00B4EE\"/>\n" +
                "</svg>\n";
    }

    private static String getDashboardSvg() {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" class=\"bi bi-bar-chart-line-fill\" viewBox=\"0 0 16 16\">\n" +
                "  <path d=\"M11 2a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v12h.5a.5.5 0 0 1 0 1H.5a.5.5 0 0 1 0-1H1v-3a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3h1V7a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v7h1V2z\" fill=\"#00B4EE\"/>\n" +
                "</svg>\n";
    }

    private static String getTeamSvg() {
        return "<svg width=\"16\" height=\"16\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 640 512\">\n" +
                "<path d=\"M192 256c61.9 0 112-50.1 112-112S253.9 32 192 32 80 82.1 80 144s50.1 112 112 112zm76.8 32h-8.3c-20.8 10-43.9 16-68.5 16s-47.6-6-68.5-16h-8.3C51.6 288 0 339.6 0 403.2V432c0 26.5 21.5 48 48 48h288c26.5 0 48-21.5 48-48v-28.8c0-63.6-51.6-115.2-115.2-115.2zM480 256c53 0 96-43 96-96s-43-96-96-96-96 43-96 96 43 96 96 96zm48 32h-3.8c-13.9 4.8-28.6 8-44.2 8s-30.3-3.2-44.2-8H432c-20.4 0-39.2 5.9-55.7 15.4 24.4 26.3 39.7 61.2 39.7 99.8v38.4c0 2.2-.5 4.3-.6 6.4H592c26.5 0 48-21.5 48-48 0-61.9-50.1-112-112-112z\" fill=\"#00B4EE\"/>\n" +
                "</svg>\n";
    }

    private static String getFlowModeOnFaIcon() {
        return "<i class=\"fas fa-circle\"></i>\n";
    }

    private static String getFlowModeOffFaIcon() {
        return "<i class=\"far fa-circle\"></i>\n";
    }

    private static String getPlusCircleFaIcon() {
        return "<i class=\"fas fa-plus-circle\" style=\"color: #00B4EE\"></i>\n";
    }

    private static String getSlackSvg() {
        return "<svg aria-hidden=\"true\" focusable=\"false\" data-prefix=\"fab\" data-icon=\"slack\" class=\"svg-inline--fa fa-slack fa-w-14\" role=\"img\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 448 512\" width=\"16\" height=\"16\">\n" +
                "<path fill=\"#00B4EE\" d=\"M94.12 315.1c0 25.9-21.16 47.06-47.06 47.06S0 341 0 315.1c0-25.9 21.16-47.06 47.06-47.06h47.06v47.06zm23.72 0c0-25.9 21.16-47.06 47.06-47.06s47.06 21.16 47.06 47.06v117.84c0 25.9-21.16 47.06-47.06 47.06s-47.06-21.16-47.06-47.06V315.1zm47.06-188.98c-25.9 0-47.06-21.16-47.06-47.06S139 32 164.9 32s47.06 21.16 47.06 47.06v47.06H164.9zm0 23.72c25.9 0 47.06 21.16 47.06 47.06s-21.16 47.06-47.06 47.06H47.06C21.16 243.96 0 222.8 0 196.9s21.16-47.06 47.06-47.06H164.9zm188.98 47.06c0-25.9 21.16-47.06 47.06-47.06 25.9 0 47.06 21.16 47.06 47.06s-21.16 47.06-47.06 47.06h-47.06V196.9zm-23.72 0c0 25.9-21.16 47.06-47.06 47.06-25.9 0-47.06-21.16-47.06-47.06V79.06c0-25.9 21.16-47.06 47.06-47.06 25.9 0 47.06 21.16 47.06 47.06V196.9zM283.1 385.88c25.9 0 47.06 21.16 47.06 47.06 0 25.9-21.16 47.06-47.06 47.06-25.9 0-47.06-21.16-47.06-47.06v-47.06h47.06zm0-23.72c-25.9 0-47.06-21.16-47.06-47.06 0-25.9 21.16-47.06 47.06-47.06h117.84c25.9 0 47.06 21.16 47.06 47.06 0 25.9-21.16 47.06-47.06 47.06H283.1z\"></path>\n" +
                "</svg>\n";
    }
}
