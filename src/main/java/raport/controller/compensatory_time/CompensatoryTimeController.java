package raport.controller.compensatory_time;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raport.model.RaportData;
import raport.service.compensatory_time.CompensatoryTimeService;
import raport.service.pdf.GeneratedFile;
import raport.service.pdf.PdfReportFacade;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class CompensatoryTimeController {

    private final CompensatoryTimeService generatorService;
    private final PdfReportFacade pdfReportFacade;

    @PostMapping("/otgul")
    public ResponseEntity<String> createCompensatoryTimeReport(@RequestBody RaportData request) {
        try {
            String filePath = generatorService.generateAndSaveReport(request);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Рапорт ГУ МВД успешно сформирован. Путь к файлу: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка при генерации рапорта: " + e.getMessage());
        }
    }

    @PostMapping("/otgul/pdf")
    public ResponseEntity<byte[]> createCompensatoryTimeReportPdf(@RequestBody RaportData request) {
        try {
            GeneratedFile pdf = pdfReportFacade.createOtgulPdf(request);
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

