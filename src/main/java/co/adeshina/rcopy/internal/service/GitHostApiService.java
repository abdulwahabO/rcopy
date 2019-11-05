package co.adeshina.rcopy.internal.service;

import co.adeshina.rcopy.exception.RepositoryAccessException;
import co.adeshina.rcopy.executor.RepositoryCopyConfig;
import co.adeshina.rcopy.internal.service.github.GithubApiService;
import co.adeshina.rcopy.dto.File;
import co.adeshina.rcopy.executor.GitHostingService;

import java.util.Set;

/**
 * Base interface for services that interact with the Rest API of Git hosting providers.
 */
public interface GitHostApiService {

    /**
     * Returns a set of {@link File} for all the files on the remote repository that can be copied.
     *
     * @param copyConfig Configuration for repository copy process.
     * @return Metadata describing the files on the remote repository.
     * @throws RepositoryAccessException if for any reason attempts to access the repository data fail.
     */
    Set<File> files(RepositoryCopyConfig copyConfig) throws RepositoryAccessException;

    /**
     * Returns a concrete implementation of this interface for the given hosting service.
     */
    static GitHostApiService get(GitHostingService service) {

        GitHostApiService apiServic = null;

        if (service.equals(GitHostingService.GITHUB)) {
            apiServic = new GithubApiService();
        }

        return apiServic;
    }
}
