package raport.service.pdf;

import java.io.IOException;
import java.nio.file.Path;

public interface DocxToPdfConverter {
    Path convert(Path docxPath) throws IOException, InterruptedException;
}

