package raport.service.financial_assistance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raport.model.FinancialAssistanceData;
import raport.service.pdf.GeneratedFile;
import raport.service.pdf.PdfReportFacade;
import raport.service.pdf.PdfResponses;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class FinancialAssistanceController {

    private final FinancialAssistanceService generatorService;
    private final PdfReportFacade pdfReportFacade;

    @PostMapping("/financial-assistance")
    public ResponseEntity<String> create(@RequestBody FinancialAssistanceData request) {
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

    @PostMapping("/financial-assistance/pdf")
    public ResponseEntity<byte[]> createPdf(@RequestBody FinancialAssistanceData request) {
        try {
            GeneratedFile pdf = pdfReportFacade.createFinancialAssistancePdf(request);
            return PdfResponses.inline(pdf);
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
