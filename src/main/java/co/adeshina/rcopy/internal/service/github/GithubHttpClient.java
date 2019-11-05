package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class GithubHttpClient {

    private static OkHttpClient client = new OkHttpClient();
    private static String CONTENTS_API_URL_FORMAT = "https://api.github.com/repos/%s/%s/contents/";
    private static final GithubHttpClient INSTANCE = new GithubHttpClient();

    private GithubHttpClient() {
    }

    public static GithubHttpClient getInstance() {
        return INSTANCE;
    }

    String repositoryContents(String repository, String username, String path, String ref, String userAgent) throws
            RepositoryAccessException {

        String url = String.format(CONTENTS_API_URL_FORMAT, username, repository);
        url += path == null ? "" : path;
        url += ref == null ? "" : "?ref=" + ref;

        Request request = new Request.Builder().addHeader("User-Agent", userAgent).url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RepositoryAccessException("Failed to get contents of: " + url, e);
        }
    }

}
