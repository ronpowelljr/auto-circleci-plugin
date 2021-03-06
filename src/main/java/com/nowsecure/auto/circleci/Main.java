package com.nowsecure.auto.circleci;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.nowsecure.auto.domain.NSAutoLogger;
import com.nowsecure.auto.domain.NSAutoParameters;
import com.nowsecure.auto.gateway.NSAutoGateway;
import com.nowsecure.auto.utils.IOHelper;
import com.nowsecure.auto.utils.IOHelperI;

/**
 * This class defines business logic for uploading mobile binary and retrieving
 * results and score. It would fail the job if score is below user-defined
 * threshold.
 * 
 * @author sbhatti
 *
 */
public class Main implements NSAutoParameters, NSAutoLogger {
    private static final int TIMEOUT = 60000;
    private static final String PLUGIN_NAME = "circleci-nowsecure-auto-security-test";
    private static final String DEFAULT_URL = "https://lab-api.nowsecure.com";
    private String apiUrl = DEFAULT_URL;
    private String group;
    private File file;
    private int waitMinutes;
    private boolean breakBuildOnScore;
    private int scoreThreshold;
    private String apiKey;
    private File artifactsDir;
    private String description;
    private String username;
    private String password;
    private boolean showStatusMessages;
    private String stopTestsForStatusMessage;

    private final IOHelperI helper = new IOHelper(PLUGIN_NAME, TIMEOUT);

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getArtifactsDir()
     */
    @Override
    public File getArtifactsDir() {
        return artifactsDir;
    }

    public void setArtifactsDir(File artifactsDir) {
        this.artifactsDir = artifactsDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getApiUrl()
     */
    @Override
    public String getApiUrl() {
        return apiUrl != null && apiUrl.length() > 0 ? apiUrl : DEFAULT_URL;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getGroup()
     */
    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getBinaryName()
     */
    @Override
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getWaitMinutes()
     */
    @Override
    public int getWaitMinutes() {
        return waitMinutes;
    }

    public void setWaitMinutes(int waitMinutes) {
        this.waitMinutes = waitMinutes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.nowsecure.auto.jenkins.plugin.NSAutoParameters#getScoreThreshold()
     */
    @Override
    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isShowStatusMessages() {
        return showStatusMessages;
    }

    public void setShowStatusMessages(boolean showStatusMessages) {
        this.showStatusMessages = showStatusMessages;
    }

    @Override
    public String getStopTestsForStatusMessage() {
        return stopTestsForStatusMessage;
    }

    public void setStopTestsForStatusMessage(String stopTestsForStatusMessage) {
        this.stopTestsForStatusMessage = stopTestsForStatusMessage;
    }

    public void execute() throws IOException {
        new NSAutoGateway(this, this, helper).execute();
    }

    @Override
    public String toString() {
        return "Main [apiUrl=" + apiUrl + ", group=" + group + ", file=" + file + ", waitMinutes=" + waitMinutes
               + ", breakBuildOnScore=" + breakBuildOnScore + ", scoreThreshold=" + scoreThreshold + ", artifactsDir="
               + artifactsDir + ", username=" + username + ", showStatusMessages=" + showStatusMessages
               + ", stopTestsForStatusMessage=" + stopTestsForStatusMessage + "]";
    }

    private static int parseInt(String name) {
        String value = System.getProperty(name, "").trim();
        if (value.length() == 0) {
            value = System.getenv(name);
            if (value == null) {
                return 0;
            }
            value = value.trim();
        }
        value = value.replaceAll("\\D+", "");
        if (value.length() == 0) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private static String getString(String name, String def) {
        String value = System.getProperty(name, "").trim();
        if (value.length() == 0) {
            value = System.getenv(name);
            if (value == null) {
                return def;
            }
            value = value.trim();
        }
        value = value.replace("<nil>", "");
        return value.length() == 0 ? def : value;
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.parseArgs(args);

        try {
            main.execute();
            System.exit(0);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    private void usage(String msg) {
        System.err.println(this);

        System.err.println(msg);
        System.err.println("Usage:\n");
        System.err
                .println(
                        "\tgradle run --args=\"--auto-url auto-url --auto-dir artifacts-dir --auto-token api-token --auto-group user-group"
                         + " --auto-username test-username --auto-password test-password --auto-show-status-messages true|false to show status-messages --auto-stop-tests-on-status status-message to stop tests"
                         + " --auto-file binary-file --auto-wait wait-for-completion-in-minutes --auto-score min-score-to-pass \"");
        System.err.println("\tOR");
        System.err
                .println(
                        "Usage: gradle run -Dauto.dir=artifacts-dir -Dauto.url=auto-url -Dauto.token=api-token -Dauto.file=mobile-binary-file"
                         + " -Dauto.username test-username -Dauto.password test-password -Dauto.show.status.messages true|false show status-messages -Dauto.stop.tests.on.status status-message to stop tests"
                         + " -Dauto.group=user-group -Dauto.file=binary-file -Dauto.wait=wait-for-completion-in-minutes -Dauto.score=min-score-to-pass");
        System.err.println("\tDefault url is " + DEFAULT_URL);
        System.err.println("\tDefault auto-wait is 0, which means just upload without waiting for results");
        System.err.println(
                "\tDefault auto-score is 0, which means build won't break, otherwise build will break if the app score is lower than this number");
        System.exit(1);
    }

    private static boolean isEmpty(String m) {
        return m == null || m.trim().length() == 0;
    }

    //
    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--auto-url".equals(args[i])) {
                this.apiUrl = args[i + 1].trim();
            } else if ("--auto-group".equals(args[i])) {
                this.apiUrl = args[i + 1].trim();
            } else if ("--auto-dir".equals(args[i])) {
                this.artifactsDir = new File(args[i + 1].trim());
            } else if ("--auto-file".equals(args[i])) {
                this.file = new File(args[i + 1].trim());
            } else if ("--auto-token".equals(args[i])) {
                this.apiKey = args[i + 1].trim();
            } else if ("--auto-wait".equals(args[i])) {
                this.waitMinutes = Integer.parseInt(args[i + 1].trim());
            } else if ("--auto-score".equals(args[i])) {
                this.scoreThreshold = Integer.parseInt(args[i + 1].trim());
            } else if ("--auto-username".equals(args[i])) {
                this.username = args[i + 1].trim();
            } else if ("--auto-password".equals(args[i])) {
                this.password = args[i + 1].trim();
            } else if ("--auto-show-status-messages".equals(args[i])) {
                this.showStatusMessages = Boolean.valueOf(args[i + 1].trim());
            } else if ("--auto-stop-tests-on-status".equals(args[i])) {
                this.stopTestsForStatusMessage = args[i + 1].trim();
            }
        }
        if (isEmpty(this.group)) {
            this.group = getString("auto.group", "");
        }
        if (isEmpty(this.apiUrl)) {
            this.apiUrl = getString("auto.url", DEFAULT_URL);
        }
        if (isEmpty(this.apiKey)) {
            this.apiKey = getString("auto.token", "");
            if (this.apiKey.length() == 0) {
                this.usage("auto-token is not defined");
            }
        }
        if (file == null) {
            String val = getString("auto.file", "");
            if (val.length() == 0) {
                this.usage("auto-file is not defined");
            }
            this.file = new File(val);
        }
        if (!file.exists()) {
            this.usage("auto-file doesn't exist, please specify full path");
        }

        if (artifactsDir == null) {
            String val = getString("auto.dir", "");
            if (val.length() == 0) {
                this.usage("auto-dir is not defined");
            }
            this.artifactsDir = new File(val);
        }
        if (!artifactsDir.exists()) {
            artifactsDir.mkdirs();
        }
        if (this.waitMinutes == 0) {
            this.waitMinutes = parseInt("auto.wait");
        }
        if (this.scoreThreshold == 0) {
            this.scoreThreshold = parseInt("auto.score");
        }
        if (isEmpty(this.username)) {
            this.username = getString("auto.username", "");
        }
        if (isEmpty(this.password)) {
            this.password = getString("auto.password", "");
        }
        if (!this.showStatusMessages) {
            this.showStatusMessages = getString("auto.show.status.messages", "").length() > 0;
        }
        if (isEmpty(this.stopTestsForStatusMessage)) {
            this.stopTestsForStatusMessage = getString("auto.stop.tests.on.status", "");
        }
    }

    @Override
    public void info(String msg) {
        System.out.println(new Date() + "@" + IOHelper.getLocalHost() + ":" + PLUGIN_NAME + " v" + IOHelper.getVersion()
                           + " " + msg);
    }

    @Override
    public void error(String msg) {
        System.err.println(new Date() + "@" + IOHelper.getLocalHost() + ":" + PLUGIN_NAME + " v" + IOHelper.getVersion()
                           + " " + msg);
    }

}
