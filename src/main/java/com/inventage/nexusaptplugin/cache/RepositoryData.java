package com.inventage.nexusaptplugin.cache;

import org.apache.maven.index.ArtifactInfoFilter;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.context.IndexingContext;

public class RepositoryData {
    private final String repositoryId;

    private final IndexingContext indexingContext;

    private final ArtifactInfoFilter artifactInfoFilter;

    private final Indexer indexer;

    public RepositoryData(String repositoryId, IndexingContext indexingContext,
                          ArtifactInfoFilter artifactInfoFilter, Indexer indexer) {
        this.repositoryId = repositoryId;
        this.indexingContext = indexingContext;
        this.artifactInfoFilter = artifactInfoFilter;
        this.indexer = indexer;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public IndexingContext getIndexingContext() {
        return indexingContext;
    }

    public ArtifactInfoFilter getArtifactInfoFilter() {
        return artifactInfoFilter;
    }

    public Indexer getIndexer() {
        return indexer;
    }
}
