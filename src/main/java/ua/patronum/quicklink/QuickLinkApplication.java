package ua.patronum.quicklink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import ua.patronum.quicklink.restapi.url.CacheConfig;


@SpringBootApplication
@EnableCaching
@Import(CacheConfig.class)
public class QuickLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickLinkApplication.class, args);
    }

}
