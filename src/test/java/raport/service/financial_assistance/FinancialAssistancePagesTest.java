package raport.service.financial_assistance;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import raport.dto.PersonDto;
import raport.model.FinancialAssistanceData;
import raport.service.DeclensionService;
import raport.service.pdf.DocxToPdfConverter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FinancialAssistancePagesTest {

    @Test
    void eachEmployeeIsOnItsOwnPage() throws Exception {
        DeclensionService declension = mock(DeclensionService.class);
        when(declension.declineRankOrPosition(any())).thenAnswer(invocation -> {
            String value = invocation.getArgument(0);
            return value == null ? "" : value;
        });
        when(declension.getDeclinedShortName(any(), any())).thenAnswer(invocation -> {
            PersonDto person = invocation.getArgument(0);
            return person.getLastName();
        });

        FinancialAssistanceService service = new FinancialAssistanceService(declension, mock(DocxToPdfConverter.class));

        FinancialAssistanceData data = new FinancialAssistanceData();
        data.setYear("2026");
        data.setReportDate("01.03.2026");
        data.setRecipient(person("Платов", "Начальник", "полковник"));
        data.setEmployees(List.of(
                person("Первов", "Полицейский", "сержант"),
                person("Второв", "Полицейский", "сержант")
        ));

        byte[] docx = service.renderMerged(data);
        String xml = documentXml(docx);
        assertThat(xml).contains("Первов");
        assertThat(xml).contains("Второв");
        assertThat(xml).contains("w:type=\"page\"");

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docx))) {
            String text = document.getParagraphs().stream()
                    .map(p -> p.getText())
                    .reduce("", (a, b) -> a + "\n" + b);
            assertThat(text).contains("Первов");
            assertThat(text).contains("Второв");
            assertThat(text).contains("2026");
        }
    }

    private static PersonDto person(String lastName, String position, String rank) {
        PersonDto person = new PersonDto();
        person.setLastName(lastName);
        person.setFirstName("Иван");
        person.setMiddleName("Иванович");
        person.setPosition(position);
        person.setRank(rank);
        person.setGender("MALE");
        return person;
    }

    private static String documentXml(byte[] docx) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(docx))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    return new String(zip.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        throw new IllegalStateException("document.xml missing");
    }
}
