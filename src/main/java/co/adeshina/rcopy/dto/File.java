package co.adeshina.rcopy.dto;

import java.nio.file.Path;

/**
 * Metadata of a file copied from a remote repository to the local filesystem.
 */
public class File {

    private final Path path;
    private final String contentUrl;
    private final long size;

    /**
     * Constructor.
     *
     * @param contentUrl - Remote URL from which the file's contents can be downloaded.
     * @param path - Location of the file on local filesystem
     * @param size - The size of the file in bytes.
     */
    public File(String contentUrl, Path path, long size) {
        this.contentUrl = contentUrl;
        this.size = size;
        this.path = path;
    }

    /**
     * Returns a URL from which the contents of the file can be downloaded.
     */
    public String getContentUrl() {
        return contentUrl;
    }

    /**
     * Returns the size of the file in bytes.
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns a {@link Path} pointing to the location of the file.
     */
    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return String.format("%s | %d bytes", path.toString(), size);
    }
}
