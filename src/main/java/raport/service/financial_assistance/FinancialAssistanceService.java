package raport.service.financial_assistance;

import com.deepoove.poi.XWPFTemplate;
import com.github.aleksandy.petrovich.Case;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import raport.dto.PersonDto;
import raport.model.FinancialAssistanceData;
import raport.service.DeclensionService;
import raport.service.pdf.DocxPageMerger;
import raport.service.pdf.DocxToPdfConverter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinancialAssistanceService {

    private final DeclensionService declensionService;
    private final DocxToPdfConverter docxToPdfConverter;

    public String generateAndSaveReport(FinancialAssistanceData data) throws IOException {
        byte[] bytes = renderMerged(data);

        String fileName = String.format("Матпомощь (%d) %s.docx",
                data.getEmployees() == null ? 0 : data.getEmployees().size(),
                data.getReportDate() == null ? "" : data.getReportDate());

        Path directory = Paths.get("output_reports");
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path filePath = directory.resolve(fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(bytes);
        }

        try {
            docxToPdfConverter.convert(filePath.toAbsolutePath());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while converting DOCX to PDF", e);
        }
        return filePath.toAbsolutePath().toString();
    }

    byte[] renderMerged(FinancialAssistanceData data) throws IOException {
        if (data.getEmployees() == null || data.getEmployees().isEmpty()) {
            throw new IOException("Не выбраны сотрудники");
        }
        List<byte[]> pages = new ArrayList<>();
        for (PersonDto employee : data.getEmployees()) {
            pages.add(renderDocxBytes(templateData(data, employee)));
        }
        return DocxPageMerger.concat(pages);
    }

    byte[] renderDocxBytes(Map<String, String> templateData) throws IOException {
        try (InputStream is = new ClassPathResource("templates/financial_assistance.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(templateData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            return bos.toByteArray();
        }
    }

    private Map<String, String> templateData(FinancialAssistanceData data, PersonDto employee) {
        Map<String, String> templateData = new HashMap<>();
        templateData.put("commanderFullPost",
                declensionService.declineRankOrPosition(data.getRecipient() == null ? null : data.getRecipient().getPosition()));
        templateData.put("commanderRank",
                declensionService.declineRankOrPosition(data.getRecipient() == null ? null : data.getRecipient().getRank()));
        templateData.put("commanderFullName",
                data.getRecipient() == null ? "" : declensionService.getDeclinedShortName(data.getRecipient(), Case.DATIVE));

        templateData.put("year", data.getYear() == null ? "" : data.getYear());
        templateData.put("reportDate", data.getReportDate() == null ? "" : data.getReportDate());

        templateData.put("employeeFullPost", employee == null || employee.getPosition() == null ? "" : employee.getPosition());
        templateData.put("employeeRank", employee == null || employee.getRank() == null ? "" : employee.getRank());
        templateData.put("employeeFullName",
                employee == null ? "" : declensionService.getDeclinedShortName(employee, Case.NOMINATIVE));
        return templateData;
    }
}
