package co.adeshina.rcopy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.dto.RepositoryCopyLog;
import co.adeshina.rcopy.exception.RepositoryCopyException;
import co.adeshina.rcopy.internal.service.github.GithubRestClient;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.Test;

class RepositoryCopyExecutorIT {

    // todo: Final Steps
    // 1. Write this class.
    // 2. Write Javadoc for all classes.
    // 3. Write README for project.
    // 4. Do final push to Github.
    // 5. Start plans for Http server using Netty + Gradle.

    // todo: Should this be an integration test?? Yes because collaborators are no mocked.

    private String username = "adeshina";
    private String repository = "rcopy";
    private GitHostingService hostingService = GitHostingService.GITHUB;

    @Test
    public void shouldCreateFilesFromApiResponse() throws IOException, RepositoryCopyException {

        Path targetDir = Files.createTempDirectory("rcopy_test");

        CopyConfig copyConfig = new CopyConfig.Builder(username, repository, targetDir, hostingService)
                                              .excludePatterns(Collections.emptyList())
                                              .httpUserAgent("user-agent")
                                              .build();

        RepositoryCopyExecutor executor = RepositoryCopyExecutor.get(copyConfig);

        MockWebServer server = new MockWebServer();
        setupMockHttpServer(server);

        RepositoryCopyLog log = executor.execute();
        server.shutdown();

        List<File> files = log.getFiles();

        assertEquals(2, files.size());
        assertTrue(files.stream().anyMatch(file -> file.getFileName().equals("octokat.java")));
        assertTrue(files.stream().anyMatch(file -> file.getFileName().equals("octokit.rb")));

        files.forEach(file -> {
            Path absolutePath = file.getDirectoryPath().resolve(file.getFileName());
            assertTrue(Files.exists(absolutePath));
        });
    }

    private void setupMockHttpServer(MockWebServer server) throws IOException {

        MockResponse githubContentsApiResponse = new MockResponse();
        githubContentsApiResponse.addHeader("Content-Type", "application/json; charset=utf-8");

        server.start();
        GithubRestClient.GITHUB_BASE_URL = "http://localhost:" + server.getPort();
        server.url(String.format("/repos/%s/%s/contents/", username, repository));

        String rbFileUrl = server.url("/octokit.rb").toString();
        String javaFileUrl = server.url("/octokat.java").toString();

        Gson gson = new Gson();

        String json = "[\n"
                + "  {\n"
                + "    \"type\": \"file\",\n"
                + "    \"size\": 625,\n"
                + "    \"name\": \"octokit.rb\",\n"
                + "    \"path\": \"lib/octokit.rb\",\n"
                + "    \"sha\" : \"d93424rh3u4yy3rhr7y734\",\n"
                + "    \"download_url\": " +  gson.toJson(rbFileUrl) + " \n"
                + "  },\n"

                + "  {\n"
                + "    \"type\": \"file\",\n"
                + "    \"size\": 32,\n"
                + "    \"name\": \"octokat.java\",\n"
                + "    \"path\": \"lib/octokat.java\",\n"
                + "    \"sha\" : \"32n34iu4929ohd2oue823\",\n"
                + "    \"download_url\": "+ gson.toJson(javaFileUrl) +" \n"
                + "  }\n"
                + "]";

        githubContentsApiResponse.setBody(json);

        MockResponse rubyFileResp = new MockResponse();
        rubyFileResp.addHeader("Content-Type", "text/plain; charset=utf-8");
        rubyFileResp.setBody("This is a Ruby file");

        MockResponse javaFileResp = new MockResponse();
        javaFileResp.addHeader("Content-Type", "text/plain; charset=utf-8");
        javaFileResp.setBody("This is a Java file");

        server.enqueue(githubContentsApiResponse);
        server.enqueue(rubyFileResp);
        server.enqueue(javaFileResp);
    }
}
