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

        String commanderRankRaw = data.getRecipient().getRank();
        String commanderRankUpper = (commanderRankRaw != null && !commanderRankRaw.isBlank())
                ? Character.toUpperCase(commanderRankRaw.charAt(0)) + commanderRankRaw.substring(1)
                : "";
        templateData.put("commanderFullPost", declensionService.declineRankOrPosition(data.getRecipient().getPosition()));
        templateData.put("commanderRank", declensionService.declineRankOrPosition(commanderRankUpper));
        templateData.put("commanderFullName", declensionService.getDeclinedShortName(data.getRecipient(), Case.DATIVE));

        templateData.put("compensatoryTimeDate", data.getDayOffDate());

        String employeeRankRaw = data.getEmployee().getRank();
        String employeeRankUpper = (employeeRankRaw != null && !employeeRankRaw.isBlank())
                ? Character.toUpperCase(employeeRankRaw.charAt(0)) + employeeRankRaw.substring(1)
                : "";
        templateData.put("employeeFullPost", data.getEmployee().getPosition());
        templateData.put("employeeRank", employeeRankUpper);
        templateData.put("employeeFullName", declensionService.getDeclinedShortName(data.getEmployee(), Case.NOMINATIVE));
        templateData.put("reportDate", data.getReportDate());
                if (data.getInterceder() != null) {
                    String intercederRankRaw = data.getInterceder().getRank();
                    String intercederRankUpper = (intercederRankRaw != null && !intercederRankRaw.isBlank())
                            ? Character.toUpperCase(intercederRankRaw.charAt(0)) + intercederRankRaw.substring(1)
                            : "";
            templateData.put("petitionerFullPost", data.getInterceder().getPosition());
            templateData.put("petitionerRank", intercederRankUpper);
            templateData.put("petitionerFullName", declensionService.getDeclinedShortName(data.getInterceder(), Case.NOMINATIVE));
        }

        byte[] bytes;
        try (InputStream is = new ClassPathResource("templates/compensatory_time_template.docx").getInputStream()) {
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