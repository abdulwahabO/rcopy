package co.adeshina.rcopy.executor;

import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.dto.RepositoryCopyLog;
import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.exception.RepositoryCopyException;
import co.adeshina.rcopy.internal.service.FilesDownloadService;
import co.adeshina.rcopy.internal.service.GitHostApiService;
import co.adeshina.rcopy.internal.dto.FileContents;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

public final class RepositoryCopyExecutor {

    private static final String EXCEPTION_MSG_FORMAT = "Failed to copy contents of repository %s/%s on %s";

    private final RepositoryCopyConfig copyConfig;
    private final GitHostApiService gitHostApiService;
    private final FilesDownloadService filesDownloadService;

    RepositoryCopyExecutor(RepositoryCopyConfig copyConfig, GitHostApiService gitHostApiService, FilesDownloadService filesDownloadService) {
        this.copyConfig = copyConfig;
        this.gitHostApiService = gitHostApiService;
        this.filesDownloadService = filesDownloadService;
    }

    public RepositoryCopyExecutor get(RepositoryCopyConfig copyConfig) {
        GitHostApiService apiService = GitHostApiService.get(copyConfig.hostingService());
        return new RepositoryCopyExecutor(copyConfig, apiService, FilesDownloadService.getInstance());
    }

    public RepositoryCopyLog execute() throws RepositoryCopyException {

        LocalDateTime start = LocalDateTime.now();
        String user = copyConfig.username();
        String repo = copyConfig.repository();
        GitHostingService service = copyConfig.hostingService();

        Set<File> files;

        try {
            files = gitHostApiService.files(copyConfig);
            for (File file : files) {
                processFile(file);
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

    private void processFile(File file) throws IOException {

        FileContents contents = filesDownloadService.download(file.getContentUrl());
        byte[] fileBytes = contents.getBytes();
        Path filePath = file.getPath();

        switch (contents.getType()) {
            case TEXT:
                Charset charset = contents.getCharset();
                String text = new String(fileBytes, charset);
                try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) {
                    writer.write(text, 0, text.length());
                }
                break;

            case BINARY:
                Files.write(filePath, fileBytes);
                break;
        }
    }
}
