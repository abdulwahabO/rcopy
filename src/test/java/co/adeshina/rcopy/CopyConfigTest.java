package co.adeshina.rcopy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CopyConfigTest {

    private static final String TARGET_DIR_PATH = "/usr/etc";
    private String user = "test-user";
    private String repo = "test-repo";
    private Path path = Paths.get(TARGET_DIR_PATH);
    private GitHostingService hostingService = GitHostingService.GITHUB;

    @Test
    public void builderSetsConfigProperties() {
        CopyConfig config = new CopyConfig.Builder(user, repo, path, hostingService)
                                                              .httpUserAgent("Agent-String")
                                                              .excludePatterns(Collections.emptyList())
                                                              .build();

        assertEquals(user, config.username());
        assertEquals(repo, config.repository());
        assertEquals(Paths.get(TARGET_DIR_PATH), config.targetDirectory());
    }
}
