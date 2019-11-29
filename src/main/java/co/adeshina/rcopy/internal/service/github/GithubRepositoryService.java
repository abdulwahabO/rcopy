package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.CopyConfig;
import co.adeshina.rcopy.internal.service.RepositoryService;
import co.adeshina.rcopy.dto.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import java.util.stream.Collectors;

/**
 * Implementation of {@link RepositoryService} for Github. For internal use only.
 */
public final class GithubRepositoryService implements RepositoryService {

    private Queue<String> directories = new LinkedList<>();
    private CopyConfig config;
    private GithubRestClient restClient;

    public GithubRepositoryService(CopyConfig config, GithubRestClient restClient) {
        this.config = config;
        this.restClient = restClient;
    }

    @Override
    public List<File> files() throws RepositoryAccessException {

        List<File> filesAtRoot = processPath(null);
        List<File> files = new ArrayList<>(filesAtRoot);

        while (!directories.isEmpty()) {
            String directoryPath = directories.poll();
            List<File> dirFiles = processPath(directoryPath);
            files.addAll(dirFiles);
        }

        return files;
    }

    private boolean notExcluded(GithubRepositoryItem item) {
        return config.exclusions().stream().noneMatch(pattern -> pattern.matcher(item.getPath()).matches());
    }

    private List<File> processPath(String dirPath) throws RepositoryAccessException {

        List<GithubRepositoryItem> items = restClient.repositoryContents(dirPath);

        items.stream()
             .filter(this::notExcluded)
             .filter(item -> item.getType().equals("dir"))
             .forEach(dir -> directories.add(dir.getPath()));

        return items.stream()
                    .filter(this::notExcluded)
                    .filter(item -> item.getType().equals("file"))
                    .map(item -> makeFile(item, config.targetDirectory()))
                    .collect(Collectors.toList());
    }

    private File makeFile(GithubRepositoryItem item, Path targetDirectory) {

        String path = item.getPath();
        path = path.substring(0, path.lastIndexOf("/"));
        Path filePath = targetDirectory.resolve(Paths.get(path));

        return new File(item.getDownloadUrl(), filePath, item.getSize(), item.getName());
    }

}
