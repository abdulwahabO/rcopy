package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.executor.RepositoryCopyConfig;
import co.adeshina.rcopy.internal.service.RepositoryService;
import co.adeshina.rcopy.dto.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public final class GithubRepositoryService implements RepositoryService {

    private Queue<String> directories = new LinkedList<>();
    private RepositoryCopyConfig config;
    private GithubRestClient restClient;

    public GithubRepositoryService(RepositoryCopyConfig config, GithubRestClient restClient) {
        this.config = config;
        this.restClient = restClient;
    }

    @Override
    public Set<File> files() throws RepositoryAccessException {

        List<File> filesAtRoot = processPath(null);
        Set<File> files = new HashSet<>(filesAtRoot);

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
        Path filePath = targetDirectory.resolve(Paths.get(item.getPath()));
        return new File(item.getDownloadUrl(), filePath, item.getSize());
    }

}
