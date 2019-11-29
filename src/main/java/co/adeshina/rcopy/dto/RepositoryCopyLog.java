package co.adeshina.rcopy.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds data from the successful copy of the contents of a remote git repository.
 */
public class RepositoryCopyLog {

    private List<File> files;
    private LocalDateTime start;
    private LocalDateTime finish;
    private String repository;

    /**
     * Returns the number of minutes the copying process took.
     */
    public long getDuration() {
        return (start == null || finish == null) ? 0 : Duration.between(start, finish).toMinutes();
    }

    /**
     * Returns the number of files copied.
     */
    public int fileCount() {
        return files.size();
    }

    /**
     * Returns the datetime when the copying process started
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Sets the date and time when the copy process was initiated.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Returns the date and time when the copy process was completed.
     */
    public LocalDateTime getFinish() {
        return finish;
    }

    /**
     * Sets the date and time when the copy process was completed.
     */
    public void setFinish(LocalDateTime finish) {
        this.finish = finish;
    }

    /**
     * Set the metadata for the files copied.
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * Returns metadata for the files copied.
     */
    public List<File> getFiles() {
        return new ArrayList<>(files);
    }

    /**
     * Returns the repository from which files were copied.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Sets the repository from which files were copied.
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }
}
