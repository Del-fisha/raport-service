package raport.service.pdf;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

public final class PdfResponses {

    private PdfResponses() {
    }

    /**
     * Tomcat 11 кодирует заголовки в ISO-8859-1. Кириллица в {@code filename}
     * роняет ответ на середине тела, и в браузере вместо рапорта открывается обломок.
     */
    public static ResponseEntity<byte[]> inline(GeneratedFile pdf) {
        String name = pdf.fileName() == null || pdf.fileName().isBlank() ? "report.pdf" : pdf.fileName();
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(name, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(pdf.bytes());
    }
}
