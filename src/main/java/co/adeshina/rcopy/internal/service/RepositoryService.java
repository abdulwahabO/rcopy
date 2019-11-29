package co.adeshina.rcopy.internal.service;

import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.CopyConfig;
import co.adeshina.rcopy.internal.service.github.GithubRepositoryService;
import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.GitHostingService;

import co.adeshina.rcopy.internal.service.github.GithubRestClient;
import java.util.List;

/**
 * Base interface for services that interact with the Rest API of Git hosting services.
 */
public interface RepositoryService {

    /**
     * Returns a collection of {@link File} for all the files on the remote repository that can be copied.
     *
     * @return Metadata describing the files on the remote repository.
     * @throws RepositoryAccessException if for any reason attempts to access the repository's data fail.
     */
    List<File> files() throws RepositoryAccessException;

    /**
     * Returns a concrete implementation of this interface for the given hosting service.
     */
    static RepositoryService get(CopyConfig config) {

        RepositoryService service = null;

        if (config.hostingService().equals(GitHostingService.GITHUB)) {
            GithubRestClient restClient = new GithubRestClient(config.repository(), config.username(),
                    config.httpUserAgent(), config.ref());
            service = new GithubRepositoryService(config, restClient);
        }

        return service;
    }
}
