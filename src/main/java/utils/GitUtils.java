package utils;

import java.io.File;
import java.io.IOException;

public final class GitUtils {

    private GitUtils() {
    }

    public static String getBranchName() {

        try {

            Process process = new ProcessBuilder(
                    "git",
                    "rev-parse",
                    "--abbrev-ref",
                    "HEAD")
                    .directory(new File(System.getProperty("user.dir")))
                    .redirectErrorStream(true)
                    .start();

            byte[] output = process.getInputStream().readAllBytes();
            process.waitFor();

            return new String(output).trim();

        } catch (IOException | InterruptedException e) {

            return "Unknown";
        }
    }
    
    public static String getCommitId() {

        try {

            Process process = new ProcessBuilder(
                    "git",
                    "rev-parse",
                    "--short",
                    "HEAD")
                    .directory(new File(System.getProperty("user.dir")))
                    .redirectErrorStream(true)
                    .start();

            byte[] output = process.getInputStream().readAllBytes();
            process.waitFor();

            return new String(output).trim();

        } catch (IOException | InterruptedException e) {

            return "Unknown";
        }
    }
}