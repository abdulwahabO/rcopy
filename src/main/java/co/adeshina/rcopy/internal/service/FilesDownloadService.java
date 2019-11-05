package co.adeshina.rcopy.internal.service;

import co.adeshina.rcopy.internal.dto.FileContents;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class FilesDownloadService {

    private OkHttpClient client = new OkHttpClient();
    private static final FilesDownloadService INSTANCE = new FilesDownloadService();

    private FilesDownloadService() {
    }

    public static FilesDownloadService getInstance() {
        return INSTANCE;
    }

    /**
     * Takes a URL from which the contents of a file can be download. Returns a {@link FileContents}.
     *
     * @throws IOException for any network I/O errors.
     */
    public FileContents download(String url) throws IOException {

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            ResponseBody responseBody = response.body();
            MediaType contentType = responseBody.contentType();

            FileContents.Type type = contentType.type().equals("text") ? FileContents.Type.TEXT : FileContents.Type.BINARY;

            Charset charset = contentType.charset();
            byte[] bytes = responseBody.bytes();

            return new FileContents(type, bytes, charset);
        }
    }
}
