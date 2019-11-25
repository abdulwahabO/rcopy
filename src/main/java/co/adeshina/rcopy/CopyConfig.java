package co.adeshina.rcopy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Configuration for the copy process.
 */
public class CopyConfig {

    private String username;
    private String repository;
    private String ref;
    private Path targetDirectory;
    private GitHostingService gitService;
    private List<Pattern> exclusions;
    private String httpUserAgent;

    private CopyConfig() {

    }

    /**
     * Returns username of the remote Git repository owner.
     */
    public String username() {
        return username;
    }

    /**
     * The remote git repository from which to copy files.
     */
    public String repository() {
        return repository;
    }

    /**
     * The name of a branch or tag on the repository. Will be used to copy files if set,
     * otherwise the default branch is used.
     */
    public String ref() {
        return ref;
    }

    /**
     * The Git hosting provider on which the repository is hosted.
     */
    public GitHostingService hostingService() {
        return gitService;
    }

    /**
     * A list of compiled regex patterns matching paths of files that should be ignored during copy.
     */
    public List<Pattern> exclusions() {
        return new ArrayList<>(exclusions);
    }

    /**
     * A path pointing to the directory into which the files copied from the remote repository should reside.
     */
    public Path targetDirectory() {
        return targetDirectory;
    }

    /**
     * User-Agent string used for HTTP requests.
     */
    public String httpUserAgent() {
        return httpUserAgent;
    }

    /**
     * Config Builder.
     */
    public static class Builder {

        private String username;
        private String repository;
        private String ref;
        private Path targetDir;
        private GitHostingService gitService;
        private List<Pattern> exclusions;
        private String httpUserAgent = "RCopy-library";

        /**
         * Constructs a config builder. Takes non-optional config options as arguments.
         *
         * @param username The username of the remote Git repository owner.
         * @param repository The name of the repository to copy files from.
         * @param targetDir Path to a local directory to which the contents of the repository will be copied.
         * @param gitService The service on which the remote Git repository is hosted.
         */
        public Builder(String username, String repository, Path targetDir, GitHostingService gitService) {
            this.username = username;
            this.repository = repository;
            this.targetDir = targetDir;
            this.gitService = gitService;
        }

        /**
         * The name of a branch or tag on the repository. If this is not set, files will be copied from the
         * repository's default branch.
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * Takes a list of compiled regex patterns. Any files whose paths match one this patterns won't be copied.
         */
        public Builder excludePatterns(List<Pattern> exclusions) {
            this.exclusions = exclusions;
            return this;
        }

        /**
         * Takes a string to use as User-Agent header for HTTP requests. A simple default will be selected if this is
         * not set.
         */
        public Builder httpUserAgent(String userAgent) {
            this.httpUserAgent = userAgent;
            return this;
        }

        /**
         * Builds a configuration for the copy process.
         */
        public CopyConfig build() {
            CopyConfig config = new CopyConfig();
            config.exclusions = this.exclusions;
            config.gitService = this.gitService;
            config.ref = this.ref;
            config.repository = this.repository;
            config.username = this.username;
            config.targetDirectory = this.targetDir;
            config.httpUserAgent = this.httpUserAgent;
            return config;
        }
    }
}
