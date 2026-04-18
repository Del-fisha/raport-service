package raport.service.utils;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class ConfigService {

    @Value("${app.output_reports}")
    private String templatePath;

    @PreDestroy
    public void destroy() {
        Path path = Paths.get(templatePath);
        try (Stream<Path> walk = Files.list(path)) {
            walk.forEach(p -> p.toFile().delete());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
