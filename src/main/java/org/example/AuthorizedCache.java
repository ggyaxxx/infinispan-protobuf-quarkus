package org.example;

import jakarta.enterprise.context.ApplicationScoped;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import io.quarkus.scheduler.Scheduled;
import org.infinispan.client.hotrod.RemoteCache;

import java.time.LocalDateTime;


public class AuthorizedCache {



    @ApplicationScoped
    public class DataGridJob {

        private final RemoteCacheManager cacheManager;

        public DataGridJob() {
            // Configurazione programmatica delle credenziali
            ConfigurationBuilder builder = new ConfigurationBuilder();

            // Assumiamo che la connettività base (host/port) sia già gestita
            // o la aggiungiamo qui per sicurezza
            builder.addServer()
                    .host("datagrid-service-name")
                    .port(11222)
                    .security().authentication()
                    .username("il-tuo-username") // Qui userai i dati estratti dal Secret
                    .password("la-tua-password")
                    .realm("default")
                    .saslMechanism("DIGEST-MD5");

            this.cacheManager = new RemoteCacheManager(builder.build());
        }

        @Scheduled(every = "10s")
        void inserisciEntry() {
            RemoteCache<String, String> cache = cacheManager.getCache("cache-riservata");

            String key = "cron-job-" + System.currentTimeMillis();
            String value = "Check eseguito il: " + LocalDateTime.now();

            cache.put(key, value);

            System.out.println("Entry inserita: " + key);
        }
    }

}
