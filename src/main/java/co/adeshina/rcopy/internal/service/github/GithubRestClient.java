package co.adeshina.rcopy.internal.service.github;

import co.adeshina.rcopy.exception.RepositoryAccessException;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GithubRestClient {

    // todo: change this during testing.
    public String GITHUB_API_BASE_URL = "https://api.github.com";
    private static String REPOSITORY_CONTENTS_URL_FORMAT = "/repos/%s/%s/contents/";

    private OkHttpClient client = new OkHttpClient();
    private Type collectionType = new TypeToken<ArrayList<GithubRepositoryItem>>(){}.getType();
    private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();

    private String repository;
    private String username;
    private String httpUserAgent;
    private String ref;

    public GithubRestClient(String repository, String username, String httpUserAgent, String ref) {
        this.httpUserAgent = httpUserAgent;
        this.repository = repository;
        this.username = username;
        this.ref = ref;
    }

    List<GithubRepositoryItem> repositoryContents(String path) throws RepositoryAccessException {

        String url = String.format(GITHUB_API_BASE_URL + REPOSITORY_CONTENTS_URL_FORMAT, username, repository);
        url += path == null ? "" : path;
        url += ref == null ? "" : "?ref=" + ref;

        Request request = new Request.Builder().addHeader("User-Agent", httpUserAgent).url(url).build();

        try (Response response = client.newCall(request).execute()) {

            ResponseBody body = response.body();
            if (response.isSuccessful() && body != null) {
                String json = body.string();
                return gson.fromJson(json, collectionType);
            } else {
                throw new RepositoryAccessException("Failed Http request for " + url);
            }

        } catch (IOException e) {
            throw new RepositoryAccessException("Failed to get contents of: " + url, e);
        }
    }
}
