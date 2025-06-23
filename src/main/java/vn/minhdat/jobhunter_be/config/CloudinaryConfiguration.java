package vn.minhdat.jobhunter_be.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfiguration {

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", "dfwttyfwk");
        config.put("api_key", "774352712225818");
        config.put("api_secret", "tctAzeBz8veomfqlcIXcOxRjA5I");

        return new Cloudinary(config);
    }
}
