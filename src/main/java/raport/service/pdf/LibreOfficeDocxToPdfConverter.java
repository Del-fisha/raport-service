package raport.service.pdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibreOfficeDocxToPdfConverter implements DocxToPdfConverter {

    private final String sofficeCommand;
    private final Duration timeout;

    public LibreOfficeDocxToPdfConverter(
            @Value("${app.libreoffice.soffice_command:soffice}") String sofficeCommand,
            @Value("${app.libreoffice.timeout_seconds:30}") long timeoutSeconds
    ) {
        this.sofficeCommand = sofficeCommand;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    @Override
    public Path convert(Path docxPath) throws IOException, InterruptedException {
        if (docxPath == null) {
            throw new IllegalArgumentException("docxPath is null");
        }
        if (!Files.exists(docxPath)) {
            throw new IOException("DOCX not found: " + docxPath);
        }

        Path outDir = docxPath.toAbsolutePath().getParent();
        if (outDir == null) {
            throw new IOException("DOCX has no parent dir: " + docxPath);
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(sofficeCommand);
        cmd.add("--headless");
        cmd.add("--nologo");
        cmd.add("--nofirststartwizard");
        cmd.add("--norestore");
        cmd.add("--convert-to");
        cmd.add("pdf");
        cmd.add("--outdir");
        cmd.add(outDir.toString());
        cmd.add(docxPath.toAbsolutePath().toString());

        Process process = new ProcessBuilder(cmd)
                .redirectErrorStream(true)
                .start();

        boolean finished = process.waitFor(timeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException("LibreOffice conversion timed out after " + timeout.getSeconds() + "s");
        }

        int exit = process.exitValue();
        if (exit != 0) {
            String output = new String(process.getInputStream().readAllBytes());
            throw new IOException("LibreOffice conversion failed. exit=" + exit + ", output=" + output);
        }

        Path pdfPath = replaceExtension(docxPath, ".pdf");
        if (!Files.exists(pdfPath)) {
            // LibreOffice sometimes normalizes extension casing; try a fallback by scanning exact expected name.
            throw new IOException("PDF not created at expected path: " + pdfPath);
        }
        return pdfPath;
    }

    private static Path replaceExtension(Path p, String newExtWithDot) {
        String fileName = p.getFileName().toString();
        int idx = fileName.lastIndexOf('.');
        String base = idx >= 0 ? fileName.substring(0, idx) : fileName;
        return p.getParent().resolve(base + newExtWithDot);
    }
}

