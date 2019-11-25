package co.adeshina.rcopy.internal.service;

import co.adeshina.rcopy.internal.dto.FileContent;
import co.adeshina.rcopy.internal.dto.FileType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilesServiceTest {

    @Test
    public void testFileDownload() throws IOException {

        String downloadUrl = "/res/file.txt";
        String fileContents = "Hello World\n I have a dream\n Where is my cat\n Ok, bye!";

        MockResponse response = new MockResponse();
        response.addHeader("Content-Type", "text/plain; charset=utf-8");
        response.setBody(fileContents);

        MockWebServer server = new MockWebServer();
        server.enqueue(response);
        server.start();

        HttpUrl url = server.url(downloadUrl);

        FileContent fileData = new FilesService().download(url.toString());
        String responseContents = new String(fileData.getBytes(), fileData.getCharset());
        server.shutdown();

        assertEquals(responseContents, fileContents);
    }

    @Test
    public void testFileWrite() throws IOException {

        String writeTxt = "I have a small car\n Let's take it for a spin";

        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = writeTxt.getBytes();
        FileContent content = new FileContent(FileType.TEXT, bytes, charset);

        Path filePath = Files.createTempFile("rcopy_test", ".txt");

        FilesService service = new FilesService();
        service.write(filePath, content);

        bytes = Files.readAllBytes(filePath);
        String readTxt = new String(bytes, charset);

        assertEquals(writeTxt, readTxt);
    }

}