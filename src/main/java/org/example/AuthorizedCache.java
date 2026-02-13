package org.example;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import io.quarkus.scheduler.Scheduled;
import org.infinispan.client.hotrod.RemoteCache;

import java.time.LocalDateTime;

@ApplicationScoped

public class AuthorizedCache {
    // Iniettiamo le properties dal file
    @ConfigProperty(name = "quarkus.infinispan-client.hosts")
    String hosts;

    @ConfigProperty(name = "quarkus.infinispan-client.trust-store")
    String trustStorePath;

    @ConfigProperty(name = "quarkus.infinispan-client.trust-store-password")
    String trustStorePassword;

    @ConfigProperty(name = "quarkus.infinispan-client.sni-host-name")
    String sniHostName;

    private  RemoteCacheManager cacheManager;


    @PostConstruct
    void init() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // Estraiamo host e porta dalla stringa (es: host:443)
        String[] hostPort = hosts.split(":");
        String host = hostPort[0];
        int port = Integer.parseInt(hostPort[1]);

        builder.addServer()
                .host(host)
                .port(port)
                .clientIntelligence(ClientIntelligence.BASIC)
                .security().authentication()
                .username("mario") // Qui usa i valori del Secret
                .password("!crQbYN>J)S(d]!v")
                .realm("default")
                .saslMechanism("SCRAM-SHA-512"); // Aggiornato secondo il tuo properties

        // Configurazione SSL/TLS
        builder.security().ssl()
                .enable()
                .trustStoreFileName(trustStorePath)
                .trustStorePassword(trustStorePassword.toCharArray())
                .trustStoreType("PKCS12")
                .sniHostName(sniHostName)
                .hostnameValidation(false); // Corrisponde a ssl-host-name-validation=false

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

