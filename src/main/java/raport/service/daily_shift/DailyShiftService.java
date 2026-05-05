package raport.service.daily_shift;

import com.deepoove.poi.XWPFTemplate;
import com.github.aleksandy.petrovich.Case;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import raport.model.DailyShiftRaportData;
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
public class DailyShiftService {

    private final DeclensionService declensionService;
    private final RankPositionGenitiveService genitiveService;

    public String generateAndSaveReport(DailyShiftRaportData data) throws IOException {
        Map<String, String> templateData = new HashMap<>();

        // кому рапорт (дательный падеж)
        String commanderRankUpper = upperFirst(data.getRecipient() == null ? null : data.getRecipient().getRank());
        templateData.put("commanderFullPost",
                declensionService.declineRankOrPosition(data.getRecipient() == null ? null : data.getRecipient().getPosition()));
        templateData.put("commanderRank", declensionService.declineRankOrPosition(commanderRankUpper));
        templateData.put("commanderFullName",
                data.getRecipient() == null ? "" : declensionService.getDeclinedShortName(data.getRecipient(), Case.DATIVE));

        // сотрудник (родительный падеж)
        String employeeRankUpper = upperFirst(data.getEmployee() == null ? null : data.getEmployee().getRank());
        templateData.put("employeeFullPost",
                genitiveService.toGenitive(data.getEmployee() == null ? null : data.getEmployee().getPosition()));
        templateData.put("employeeRank", genitiveService.toGenitive(employeeRankUpper));
        templateData.put("employeeFullName",
                data.getEmployee() == null ? "" : declensionService.getDeclinedShortName(data.getEmployee(), Case.GENITIVE));

        // даты
        templateData.put("firstTimeDate", nullToEmpty(data.getFirstTimeDate()));
        templateData.put("secondTimeDate", nullToEmpty(data.getSecondTimeDate()));
        templateData.put("newTimeDate", nullToEmpty(data.getNewTimeDate()));
        templateData.put("reportDate", nullToEmpty(data.getReportDate()));

        // от кого (именительный)
        if (data.getPetitioner() != null) {
            templateData.put("petitionerFullPost", nullToEmpty(data.getPetitioner().getPosition()));
            templateData.put("petitionerRank", nullToEmpty(upperFirst(data.getPetitioner().getRank())));
            templateData.put("petitionerFullName", declensionService.getDeclinedShortName(data.getPetitioner(), Case.NOMINATIVE));
        } else {
            templateData.put("petitionerFullPost", "");
            templateData.put("petitionerRank", "");
            templateData.put("petitionerFullName", "");
        }

        byte[] bytes;
        try (InputStream is = new ClassPathResource("templates/shift_rescheduling_template.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(templateData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            bytes = bos.toByteArray();
        }

        String fileName = String.format("Сутки (%s %s.) %s.docx",
                safe(data.getEmployee() == null ? null : data.getEmployee().getLastName(), "Сотрудник"),
                safeInitial(data.getEmployee() == null ? null : data.getEmployee().getFirstName()),
                nullToEmpty(data.getNewTimeDate()));

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

    private static String upperFirst(String s) {
        if (s == null || s.isBlank()) return "";
        String trimmed = s.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private static String safeInitial(String firstName) {
        if (firstName == null || firstName.isBlank()) return "";
        return (Character.toUpperCase(firstName.trim().charAt(0)) + ".");
    }
}

