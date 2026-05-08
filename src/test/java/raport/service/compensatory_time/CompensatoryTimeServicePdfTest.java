package raport.service.compensatory_time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import raport.model.RaportData;
import raport.service.DeclensionService;
import raport.service.pdf.DocxToPdfConverter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CompensatoryTimeServicePdfTest {

    @TempDir
    Path tempDir;

    @Test
    void generateAndSaveReport_alsoConvertsToPdf() throws Exception {
        DeclensionService declension = mock(DeclensionService.class);
        DocxToPdfConverter converter = mock(DocxToPdfConverter.class);

        CompensatoryTimeService service = new CompensatoryTimeService(declension, converter) {
            @Override
            byte[] renderDocxBytes(Map<String, String> templateData) {
                return "docx-bytes".getBytes(StandardCharsets.UTF_8);
            }
        };

        RaportData data = new RaportData();
        // keep minimal NPE risk by stubbing DTO fields usage via mocks? We bypass renderDocxBytes, but
        // method still reads some data fields for fileName and templateData. We'll provide simple stubs.
        var p = new raport.dto.PersonDto();
        p.setFirstName("Иван");
        p.setLastName("Петров");
        p.setPosition("pos");
        p.setRank("rank");
        data.setEmployee(p);
        data.setRecipient(p);
        data.setInterceder(p);
        data.setDayOffDate("08.05.2026");
        data.setReportDate("08.05.2026");

        // force output_reports to temp dir by running in temp as cwd substitute is hard; instead just assert convert called with produced path
        // We can detect created file by scanning directory name used by service.
        // Service uses Paths.get("output_reports") relative to cwd; create that inside temp dir and switch user.dir.
        String prev = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            String docxPathStr = service.generateAndSaveReport(data);
            Path docxPath = Path.of(docxPathStr);
            assertThat(Files.exists(docxPath)).isTrue();
            verify(converter).convert(docxPath);
        } finally {
            System.setProperty("user.dir", prev);
        }
    }
}

