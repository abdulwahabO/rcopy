package co.adeshina.rcopy.internal.service.github;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import co.adeshina.rcopy.GitHostingService;
import co.adeshina.rcopy.CopyConfig;
import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.exception.RepositoryAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

public class GithubRepositoryServiceTest {

    private GithubRestClient restClient = Mockito.mock(GithubRestClient.class);
    private String username = "adeshina";
    private String repo = "repo";

    private String fileContentUrl = "www.dwnload.com";
    private String fileName = "file.txt";
    private String repoFilePath = "src/java/file.txt";

    @BeforeEach
    public void setup() throws RepositoryAccessException {

        GithubRepositoryItem item = new GithubRepositoryItem();
        item.setName(fileName);
        item.setPath(repoFilePath);
        item.setType("file");
        item.setDownloadUrl(fileContentUrl);

        doReturn(Collections.singletonList(item)).when(restClient).repositoryContents(any());
    }

    @Test
    public void shouldMapRepoItemsToFile() throws IOException, RepositoryAccessException {

        Path targetDir = Files.createTempDirectory("rcopy_tst");
        CopyConfig config = new CopyConfig.Builder(username, repo, targetDir, GitHostingService.GITHUB)
                                          .excludePatterns(Collections.emptyList())
                                          .httpUserAgent("agent")
                                          .ref(null)
                                          .build();

        GithubRepositoryService service = new GithubRepositoryService(config, restClient);
        Optional<File> optionalFile = service.files().stream().findAny();

        if (!optionalFile.isPresent()) {
            fail("Github Service failed to return any File objects");
        }

        File file = optionalFile.get();

        String repoDirPath = repoFilePath.substring(0, repoFilePath.lastIndexOf("/"));

        assertEquals(fileContentUrl, file.getContentUrl());
        assertEquals(targetDir.resolve(repoDirPath), file.getDirectoryPath());
        assertEquals(fileName, file.getFileName());
    }
}
