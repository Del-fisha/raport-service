package raport.service.shift_transfer;

import com.deepoove.poi.XWPFTemplate;
import com.github.aleksandy.petrovich.Case;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import raport.model.ShiftTransferData;
import raport.service.DeclensionService;
import raport.service.daily_shift.RankPositionGenitiveService;
import raport.service.pdf.DocxToPdfConverter;

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
public class ShiftTransferService {

    private final DeclensionService declensionService;
    private final RankPositionGenitiveService genitiveService;
    private final DocxToPdfConverter docxToPdfConverter;

    public String generateAndSaveReport(ShiftTransferData data) throws IOException {
        byte[] bytes = renderDocxBytes(templateData(data));

        String fileName = String.format("Замена смены (%s %s) %s.docx",
                safe(data.getEmployee() == null ? null : data.getEmployee().getLastName(), "Сотрудник"),
                safeInitial(data.getEmployee() == null ? null : data.getEmployee().getFirstName()),
                nullToEmpty(data.getNewTimeDate()));

        Path filePath = write(fileName, bytes);
        convert(filePath);
        return filePath.toAbsolutePath().toString();
    }

    byte[] renderDocxBytes(Map<String, String> templateData) throws IOException {
        try (InputStream is = new ClassPathResource("templates/shift_transfer.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(templateData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            return bos.toByteArray();
        }
    }

    private Map<String, String> templateData(ShiftTransferData data) {
        Map<String, String> templateData = new HashMap<>();

        templateData.put("commanderFullPost",
                declensionService.declineRankOrPosition(data.getRecipient() == null ? null : data.getRecipient().getPosition()));
        templateData.put("commanderRank",
                declensionService.declineRankOrPosition(data.getRecipient() == null ? null : data.getRecipient().getRank()));
        templateData.put("commanderFullName",
                data.getRecipient() == null ? "" : declensionService.getDeclinedShortName(data.getRecipient(), Case.DATIVE));

        // тот же падеж, что в рапорте на сутки: «график несения службы (кого?)»
        templateData.put("employeeFullPost",
                genitiveService.toGenitive(data.getEmployee() == null ? null : data.getEmployee().getPosition()));
        templateData.put("employeeRank",
                genitiveService.toGenitive(data.getEmployee() == null ? null : data.getEmployee().getRank()));
        templateData.put("employeeFullName",
                data.getEmployee() == null ? "" : declensionService.getDeclinedShortName(data.getEmployee(), Case.GENITIVE));

        templateData.put("oldTimeDate", nullToEmpty(data.getOldTimeDate()));
        templateData.put("newTimeDate", nullToEmpty(data.getNewTimeDate()));
        templateData.put("reportDate", nullToEmpty(data.getReportDate()));

        if (data.getPetitioner() != null) {
            templateData.put("petitionerFullPost", nullToEmpty(data.getPetitioner().getPosition()));
            templateData.put("petitionerRank", nullToEmpty(data.getPetitioner().getRank()));
            templateData.put("petitionerFullName", declensionService.getDeclinedShortName(data.getPetitioner(), Case.NOMINATIVE));
        } else {
            templateData.put("petitionerFullPost", "");
            templateData.put("petitionerRank", "");
            templateData.put("petitionerFullName", "");
        }
        return templateData;
    }

    private void convert(Path filePath) throws IOException {
        try {
            docxToPdfConverter.convert(filePath.toAbsolutePath());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while converting DOCX to PDF", e);
        }
    }

    private static Path write(String fileName, byte[] bytes) throws IOException {
        Path directory = Paths.get("output_reports");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = directory.resolve(fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(bytes);
        }
        return filePath;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private static String safeInitial(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            return "";
        }
        return Character.toUpperCase(firstName.trim().charAt(0)) + ".";
    }
}
