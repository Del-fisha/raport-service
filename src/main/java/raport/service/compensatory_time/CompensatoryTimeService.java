package raport.service.compensatory_time;

import com.deepoove.poi.XWPFTemplate;
import com.github.aleksandy.petrovich.Case;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import raport.model.RaportData;
import raport.service.DeclensionService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompensatoryTimeService {
    private final DeclensionService declensionService;

    public String generateAndSaveReport(RaportData data) throws IOException {
        Map<String, String> templateData = new HashMap<>();

        templateData.put("commanderFullPost", declensionService.declineRankOrPosition(data.getRecipient().getPosition()));
        templateData.put("commanderRank", declensionService.declineRankOrPosition(data.getRecipient().getRank()));
        templateData.put("commanderFullName", declensionService.getDeclinedShortName(data.getRecipient(), Case.DATIVE));

        templateData.put("compensatoryTimeDate", data.getDayOffDate());

        templateData.put("employeeFullPost", data.getEmployee().getPosition());
        templateData.put("employeeRank", data.getEmployee().getRank());
        templateData.put("employeeFullName", declensionService.getDeclinedShortName(data.getEmployee(), Case.NOMINATIVE));
        templateData.put("reportDate", data.getReportDate());

        if (data.getInterceder() != null) {
            templateData.put("petitionerFullPost", declensionService.declineRankOrPosition(data.getInterceder().getPosition()));
            templateData.put("petitionerRank", declensionService.declineRankOrPosition(data.getInterceder().getRank()));
            templateData.put("petitionerFullName", declensionService.getDeclinedShortName(data.getInterceder(), Case.NOMINATIVE));
        }

        byte[] bytes;
        try (InputStream is = new ClassPathResource("templates/compensatory_time_template_2.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(templateData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            bytes = bos.toByteArray();
        }

        String fileName = "Отгул_" + data.getEmployee().getLastName() + "_" + System.currentTimeMillis() + ".docx";
        Path directory = Paths.get("output_reports");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = directory.resolve(fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(bytes);
        }

        return filePath.toAbsolutePath().toString();
    }
}
