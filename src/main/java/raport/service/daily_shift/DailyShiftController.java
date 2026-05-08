package raport.service.daily_shift;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raport.model.DailyShiftRaportData;
import raport.service.pdf.GeneratedFile;
import raport.service.pdf.PdfReportFacade;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class DailyShiftController {

    private final DailyShiftService generatorService;
    private final PdfReportFacade pdfReportFacade;

    @PostMapping("/sutki")
    public ResponseEntity<String> createDailyShiftReport(@RequestBody DailyShiftRaportData request) {
        try {
            String filePath = generatorService.generateAndSaveReport(request);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Рапорт успешно сформирован. Путь к файлу: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка при генерации рапорта: " + e.getMessage());
        }
    }

    @PostMapping("/sutki/pdf")
    public ResponseEntity<byte[]> createDailyShiftReportPdf(@RequestBody DailyShiftRaportData request) {
        try {
            GeneratedFile pdf = pdfReportFacade.createSutkiPdf(request);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdf.fileName() + "\"")
                    .body(pdf.bytes());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Ошибка при генерации рапорта: " + e.getMessage()).getBytes());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Ошибка при генерации рапорта: interrupted".getBytes());
        }
    }
}

