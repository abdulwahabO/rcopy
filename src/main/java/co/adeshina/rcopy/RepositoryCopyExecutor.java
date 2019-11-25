package co.adeshina.rcopy;

import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.dto.RepositoryCopyLog;
import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.exception.RepositoryCopyException;
import co.adeshina.rcopy.internal.service.FilesService;
import co.adeshina.rcopy.internal.service.RepositoryService;
import co.adeshina.rcopy.internal.dto.FileContent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * todo: Javadoc main entry point....
 */
public final class RepositoryCopyExecutor {

    private static final String EXCEPTION_MSG_FORMAT = "Failed to copy contents of repository %s/%s from %s";

    private final CopyConfig copyConfig;
    private final RepositoryService repositoryService;
    private final FilesService filesService;

    private RepositoryCopyExecutor(CopyConfig copyConfig, RepositoryService repositoryService, FilesService filesService) {
        this.copyConfig = copyConfig;
        this.repositoryService = repositoryService;
        this.filesService = filesService;
    }

    /**
     *
     * @param copyConfig
     */
    public static RepositoryCopyExecutor get(CopyConfig copyConfig) {
        RepositoryService apiService = RepositoryService.get(copyConfig);
        FilesService filesService = new FilesService();
        return new RepositoryCopyExecutor(copyConfig, apiService, filesService);
    }

    public RepositoryCopyLog execute() throws RepositoryCopyException {

        LocalDateTime start = LocalDateTime.now();
        String user = copyConfig.username();
        String repo = copyConfig.repository();
        GitHostingService service = copyConfig.hostingService();

        List<File> files;

        try {

            files = repositoryService.files();

            for (File file : files) {

                FileContent contents = filesService.download(file.getContentUrl());
                Path dirPath = file.getDirectoryPath();
                dirPath = Files.createDirectories(dirPath);

                Path filePath = dirPath.resolve(file.getFileName());
                filesService.write(filePath, contents);
            }

        } catch (RepositoryAccessException | IOException e) {
            throw new RepositoryCopyException(String.format(EXCEPTION_MSG_FORMAT, user, repo, service.toString()), e);
        }

        RepositoryCopyLog log = new RepositoryCopyLog();
        log.setFiles(new ArrayList<>(files));
        log.setFinish(LocalDateTime.now());
        log.setStart(start);
        log.setRepository(repo);

        return log;
    }
}
