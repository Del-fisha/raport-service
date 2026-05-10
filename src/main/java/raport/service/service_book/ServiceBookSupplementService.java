package raport.service.service_book;

import com.deepoove.poi.XWPFTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import raport.model.ServiceBookSupplementData;
import raport.service.pdf.DocxToPdfConverter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceBookSupplementService {

    private static final DateTimeFormatter CORE_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter SERVICE_BOOK_DATE = DateTimeFormatter.ofPattern("«dd» MM yyyy 'г.'");

    private final DocxToPdfConverter docxToPdfConverter;

    public String generateAndSaveReport(ServiceBookSupplementData data) throws IOException {
        Map<String, String> templateData = new HashMap<>();

        templateData.put("employeeFullPost", nullToEmpty(data.getEmployee() == null ? null : data.getEmployee().getPosition()));
        templateData.put("employeeRank", nullToEmpty(data.getEmployee() == null ? null : data.getEmployee().getRank()));
        templateData.put("employeeFullName", fullName(data.getEmployee()));

        templateData.put("petitionerFullPost", nullToEmpty(data.getPetitioner() == null ? null : data.getPetitioner().getPosition()));
        templateData.put("petitionerRank", nullToEmpty(data.getPetitioner() == null ? null : data.getPetitioner().getRank()));
        templateData.put("petitionerFullName", fullName(data.getPetitioner()));

        templateData.put("reportDate", formatReportDate(data.getReportDate()));

        byte[] bytes = renderDocxBytes(templateData);

        String fileName = String.format("Служебная книжка (%s %s.).docx",
                safe(data.getEmployee() == null ? null : data.getEmployee().getLastName(), "Сотрудник"),
                safeInitial(data.getEmployee() == null ? null : data.getEmployee().getFirstName()));

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

    byte[] renderDocxBytes(Map<String, String> templateData) throws IOException {
        try (InputStream is = new ClassPathResource("templates/service_book.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(templateData);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            return bos.toByteArray();
        }
    }

    private static String formatReportDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        try {
            LocalDate d = LocalDate.parse(raw.trim(), CORE_DATE);
            return d.format(SERVICE_BOOK_DATE);
        } catch (DateTimeParseException e) {
            // Если пришло не в ожидаемом формате — вставляем как есть.
            return raw;
        }
    }

    private static String fullName(raport.dto.PersonDto p) {
        if (p == null) return "";
        String ln = nullToEmpty(p.getLastName());
        String fn = nullToEmpty(p.getFirstName());
        String mn = nullToEmpty(p.getMiddleName());
        String combined = (ln + " " + fn + " " + mn).trim();
        return combined.replaceAll("\\s{2,}", " ");
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

