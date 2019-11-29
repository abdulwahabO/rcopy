package co.adeshina.rcopy.internal.service;

import co.adeshina.rcopy.internal.dto.FileContent;
import co.adeshina.rcopy.internal.dto.FileType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FilesService {

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Takes a URL from which the contents of a file can be downloaded. Returns a {@link FileContent} which holds the
     * contents of the file.
     */
    public FileContent download(String url) throws IOException {

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            ResponseBody body = response.body();

            if (response.isSuccessful() && body != null) {

                MediaType contentType = body.contentType();

                FileType type = contentType.type().equals("text") ? FileType.TEXT : FileType.BINARY;
                Charset charset = contentType.charset();
                byte[] bytes = body.bytes();

                return new FileContent(type, bytes, charset);
            } else {
                throw new IOException();
            }

        }
    }

    /**
     * Writes a file to the filesystem using the given path and file contents.
     */
    public void write(Path filePath, FileContent content) throws IOException {
        Charset charset = content.getCharset();
        byte[] bytes = content.getBytes();

        switch (content.getType()) {
            case TEXT:
                String text = new String(bytes, charset);
                try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) {
                    writer.write(text, 0, text.length());
                }
                break;

            case BINARY:
                Files.write(filePath, bytes);
                break;
        }
    }
}
