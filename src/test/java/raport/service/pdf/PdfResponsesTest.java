package raport.service.pdf;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class PdfResponsesTest {

    @Test
    void cyrillicFileNameStaysAsciiInHeader() {
        String header = PdfResponses.inline(new GeneratedFile("Сутки (Иванов И.).pdf", new byte[]{1}))
                .getHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION);

        assertThat(header).isNotBlank();
        assertThat(header.chars().allMatch(ch -> ch <= 127)).isTrue();
        assertThat(header).contains("filename*=");
    }
}
