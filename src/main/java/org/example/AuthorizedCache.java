package org.example;


import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import io.quarkus.scheduler.Scheduled;
import org.infinispan.client.hotrod.RemoteCache;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.client.hotrod.ProtocolVersion; // Per risolvere ProtocolVersion
import java.time.LocalDateTime;

@ApplicationScoped
public class AuthorizedCache {

    @ConfigProperty(name = "custom.dg.hosts")
    String hosts;

    @ConfigProperty(name = "custom.dg.trust-store")
    String trustStorePath;

    @ConfigProperty(name = "custom.dg.trust-store-password")
    String trustStorePassword;

    @ConfigProperty(name = "custom.dg.sni-host-name")
    String sniHostName;

    private RemoteCacheManager cacheManager;

    @PostConstruct
    void init() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        String[] hostPort = hosts.split(":");
        String host = hostPort[0];
        int port = Integer.parseInt(hostPort[1]);

        builder.addServer()
                .host(host)
                .port(port)
                .clientIntelligence(ClientIntelligence.BASIC)
                // Forziamo il protocollo Hot Rod alla versione 3.0 (standard per Infinispan 12)
                .version(ProtocolVersion.PROTOCOL_VERSION_30)
                .security().authentication()
                .username("mario")
                .password("!crQbYN>J)S(d]!v")
                .realm("default")
                .clientIntelligence(ClientIntelligence.BASIC); // <--- Questo impedisce al client di cercare IP interni di OpenShift

        builder.security().ssl()
                .enable()
                .trustStoreFileName(trustStorePath)
                .trustStorePassword(trustStorePassword.toCharArray())
                .trustStoreType("PKCS12")
                .sniHostName(sniHostName);

        this.cacheManager = new RemoteCacheManager(builder.build());
    }

    @Scheduled(every = "10s")
    void inserisciEntry() {
        RemoteCache<String, String> cache = cacheManager.getCache("cache-riservata");
        if (cache != null) {
            String key = "debug-key-" + System.currentTimeMillis();
            cache.put(key, "Debug OCP: " + LocalDateTime.now());
            System.out.println("Riproduzione errore - Entry inserita: " + key);
        }
    }
}