package raport.controller.compensatory_time;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raport.model.RaportData;
import raport.service.compensatory_time.CompensatoryTimeService;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class CompensatoryTimeController {

    private final CompensatoryTimeService generatorService;

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
}

