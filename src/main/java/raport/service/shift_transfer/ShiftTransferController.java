package raport.service.shift_transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raport.model.ShiftTransferData;
import raport.service.pdf.GeneratedFile;
import raport.service.pdf.PdfReportFacade;
import raport.service.pdf.PdfResponses;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ShiftTransferController {

    private final ShiftTransferService generatorService;
    private final PdfReportFacade pdfReportFacade;

    @PostMapping("/shift-transfer")
    public ResponseEntity<String> create(@RequestBody ShiftTransferData request) {
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

    @PostMapping("/shift-transfer/pdf")
    public ResponseEntity<byte[]> createPdf(@RequestBody ShiftTransferData request) {
        try {
            GeneratedFile pdf = pdfReportFacade.createShiftTransferPdf(request);
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
