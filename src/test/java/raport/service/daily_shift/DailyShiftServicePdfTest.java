package raport.service.daily_shift;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import raport.model.DailyShiftRaportData;
import raport.service.DeclensionService;
import raport.service.pdf.DocxToPdfConverter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DailyShiftServicePdfTest {

    @TempDir
    Path tempDir;

    @Test
    void generateAndSaveReport_alsoConvertsToPdf() throws Exception {
        DeclensionService declension = mock(DeclensionService.class);
        RankPositionGenitiveService genitive = mock(RankPositionGenitiveService.class);
        DocxToPdfConverter converter = mock(DocxToPdfConverter.class);

        DailyShiftService service = new DailyShiftService(declension, genitive, converter) {
            @Override
            byte[] renderDocxBytes(Map<String, String> templateData) {
                return "docx-bytes".getBytes(StandardCharsets.UTF_8);
            }
        };

        DailyShiftRaportData data = new DailyShiftRaportData();
        var p = new raport.dto.PersonDto();
        p.setFirstName("Иван");
        p.setLastName("Петров");
        p.setPosition("pos");
        p.setRank("rank");
        data.setEmployee(p);
        data.setRecipient(p);
        data.setPetitioner(p);
        data.setNewTimeDate("08.05.2026");
        data.setFirstTimeDate("08.05.2026");
        data.setSecondTimeDate("08.05.2026");
        data.setReportDate("08.05.2026");

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

