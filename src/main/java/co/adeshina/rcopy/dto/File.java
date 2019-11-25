package co.adeshina.rcopy.dto;

import java.nio.file.Path;

/**
 * Metadata of a file copied from a remote repository to the local filesystem.
 */
public class File {

    private final String fileName;
    private final Path directoryPath;
    private final String contentUrl;
    private final long size;

    /**
     * Instantiates a {@link File}.
     *
     * @param contentUrl - Remote URL from which the file's contents can be downloaded.
     * @param directoryPath - Location of the file on local filesystem
     * @param size - The size of the file in bytes.
     */
    public File(String contentUrl, Path directoryPath, long size, String fileName) {
        this.contentUrl = contentUrl;
        this.size = size;
        this.fileName = fileName;
        this.directoryPath = directoryPath;
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
     * Returns a {@link Path} to the directory holding this file. This combined with <code>filename</code> gives the
     * absolute path to the file on the local filesystem.
     */
    public Path getDirectoryPath() {
        return directoryPath;
    }

    /**
     *
     */
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return String.format("%s | %d bytes", directoryPath.toString(), size);
    }
}
