[![Build Status](https://travis-ci.com/abdulwahabO/rcopy.svg?branch=master)](https://travis-ci.com/abdulwahabO/rcopy)

### About

A Java library for copying the contents of remote git repository to a specified directory on the local filesystem.
Files and directories within the repository can excluded from copying by providing compiled regular expressions
 patterns.

### Usage 

This is a Maven project that has to be built locally. I plan to publish it to Github's package repository in
the near future. Use `mvn clean install` to run all tests and install a JAR of the library in your local Maven
 repository. Then add RCopy as a dependency in your project's POM.

```xml
    <dependency>
        <groupId>co.adeshina</groupId>
        <artifactId>rcopy</artifactId>
        <version>{version}</version>
    </dependency>
```

To copy the contents of repository: Create a `CopyConfig`, use it to instantiate a `RepositoryCopyExecutor` and
 execute it.

```java
public class Copy {
    public void start() throws RepositoryCopyException {
        
        // Directory into which to copy the repos contents.
        Path targetDir = Files.createDirectory("rcopy_dump");
                
        CopyConfig copyConfig = new CopyConfig.Builder(username, repository, targetDir, GitHostingService.GITHUB)
                                              .excludePatterns(Collections.emptyList()) // No file/directories excluded
                                              .httpUserAgent("user-agent") // HTTP User-Agent string
                                              .build();
            
        // Create executor
        RepositoryCopyExecutor executor = RepositoryCopyExecutor.get(copyConfig);
            
        // Blocks until all eligible files are copied or an exception is thrown
        RepositoryCopyLog log = executor.execute();
    }        
}   
```

### Motivation

This is the first step towards implementing a side project idea that's been in my head: A simple HTTP server that
 serves files copied from a remote git repository. I may never get to build that server but writing this library has
  been educative and fun.
  
### Tech

* JDK 1.8
* JUnit 5 and Mockito 2
* GSON
* Okhttp3 [MockWebServer]()
