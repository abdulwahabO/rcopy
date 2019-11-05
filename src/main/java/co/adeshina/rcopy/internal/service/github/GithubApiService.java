package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.executor.RepositoryCopyConfig;
import co.adeshina.rcopy.internal.service.GitHostApiService;
import co.adeshina.rcopy.dto.File;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GithubApiService implements GitHostApiService {

    private Type collectionType = new TypeToken<ArrayList<RepositoryItem>>(){}.getType();
    private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
    private Queue<String> directories;

    private String repository;
    private String username;
    private String ref;
    private List<Pattern> exclusions;
    private Path targetDirectory;
    private String userAgent;

    private final GithubHttpClient githubHttpClient;

    GithubApiService(GithubHttpClient githubHttpClient) {
        this.githubHttpClient = githubHttpClient;
    }

    public GithubApiService() {
        this(GithubHttpClient.getInstance());
    }

    @Override
    public Set<File> files(RepositoryCopyConfig copyConfig) throws RepositoryAccessException {

        repository = copyConfig.repository();
        username = copyConfig.username();
        exclusions = copyConfig.exclusions();
        ref = copyConfig.ref();
        targetDirectory = copyConfig.targetDirectory();
        userAgent = copyConfig.httpUserAgent();

        directories = new LinkedList<>();

        List<File> filesAtRoot = processPath(null);
        Set<File> files = new HashSet<>(filesAtRoot);

        while (!directories.isEmpty()) {
            String directoryPath = directories.poll();
            List<File> dirFiles = processPath(directoryPath);
            files.addAll(dirFiles);
        }

        return files;
    }

    private boolean notExcluded(RepositoryItem item) {
        return exclusions.stream().noneMatch(pattern -> pattern.matcher(item.path).matches());
    }

    private List<File> processPath(String dirPath) throws RepositoryAccessException {

        String json = githubHttpClient.repositoryContents(repository, username, dirPath, ref, userAgent);
        List<RepositoryItem> items = gson.fromJson(json, collectionType);

        items.stream()
             .filter(this::notExcluded)
             .filter(item -> item.type.equals("dir"))
             .forEach(dir -> directories.add(dir.path));

        return items.stream()
                    .filter(this::notExcluded)
                    .filter(item -> item.type.equals("file"))
                    .map(item -> makeFile(item, targetDirectory))
                    .collect(Collectors.toList());
    }

    private File makeFile(RepositoryItem item, Path targetDirectory) {
        Path filePath = targetDirectory.resolve(Paths.get(item.path));
        return new File(item.downloadUrl, filePath, item.size);
    }

    // Model for Github repo contents API response body
    private static class RepositoryItem {
        long size;
        String path;
        String type;
        String downloadUrl;
    }
}
