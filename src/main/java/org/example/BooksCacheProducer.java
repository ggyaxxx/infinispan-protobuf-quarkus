package org.example;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.Collections;
import java.util.UUID;

@ApplicationScoped
@Startup
public class BooksCacheProducer {

    private final RemoteCacheManager cacheManager;
    private RemoteCache<String, Book> booksCache;

    public BooksCacheProducer(RemoteCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    //@Scheduled(every = "1s") //momentaneamente disabilitato
    void pushBook() {
        try {
            if (booksCache == null) {
                booksCache = cacheManager.getCache("books");
                if (booksCache == null) {
                    System.err.println("Cache 'books' does not exist yet in Data Grid!");
                    return;
                }
            }


            Author author = new Author("Isaac", "Asimov");

            Book book = new Book(
                    UUID.randomUUID().toString(),
                    "Foundation",
                    "Classic Sci-Fi Novel",
                    1951,
                    Collections.singletonList(author),
                    Language.ENGLISH
            );

            // Write to remote cache
            booksCache.put(book.getId(), book);

            System.out.println("Added book to cache: " + book.getId());

        } catch (Exception e) {
            System.err.println("Error writing to cache: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
