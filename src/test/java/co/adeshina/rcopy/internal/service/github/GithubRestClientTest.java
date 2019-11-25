package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GithubRestClientTest {

    @Test
    public void shouldDeserializeRepositoryItemFromApiResponse() throws RepositoryAccessException, IOException {

        String jsonResponse = "[\n"
                + "  {\n"
                + "    \"name\": \"me.txt\",\n"
                + "    \"path\": \"src/test/java/me.txt\",\n"
                + "    \"size\": 4,\n"
                + "    \"url\": \"https://github.com\",\n"
                + "    \"html_url\": \"https://github.com/\",\n"
                + "    \"git_url\": \"https://api.github.com\",\n"
                + "    \"download_url\": \"https://github.com\",\n"
                + "    \"type\": \"file\"\n"
                + "  }\n"
                + "]";

        MockResponse response = new MockResponse();
        response.addHeader("Content-Type", "application/json; charset=utf-8");
        response.setBody(jsonResponse);

        String username = "user";
        String repo = "repo";
        String userAgent = "user-agent";

        MockWebServer server = new MockWebServer();
        server.enqueue(response);
        server.start();
        server.url(String.format("/repos/%s/%s/contents/", username, repo));

        GithubRestClient client = new GithubRestClient(repo, username, userAgent, null);
        GithubRestClient.GITHUB_BASE_URL = "http://localhost:" + server.getPort();
        GithubRepositoryItem item = client.repositoryContents(null).get(0);
        server.shutdown();

        assertEquals("me.txt", item.getName());
        assertEquals("https://github.com", item.getDownloadUrl());
    }

}
