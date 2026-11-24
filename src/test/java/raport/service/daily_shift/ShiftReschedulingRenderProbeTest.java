package raport.service.daily_shift;

import com.deepoove.poi.XWPFTemplate;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ShiftReschedulingRenderProbeTest {

    @Test
    void templateRendersTheReportInsteadOfAFragment() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("commanderFullPost", "Начальнику");
        data.put("commanderRank", "полковнику");
        data.put("commanderFullName", "М.В. Платову");
        data.put("employeeFullPost", "полицейского");
        data.put("employeeRank", "сержанта");
        data.put("employeeFullName", "И.И. Тестова");
        data.put("firstTimeDate", "01.02.2026");
        data.put("secondTimeDate", "02.02.2026");
        data.put("newTimeDate", "03.02.2026");
        data.put("petitionerFullPost", "Командир");
        data.put("petitionerRank", "майор");
        data.put("petitionerFullName", "П.П. Командиров");
        data.put("reportDate", "04.02.2026");

        byte[] bytes;
        try (InputStream is = new ClassPathResource("templates/shift_rescheduling_template.docx").getInputStream()) {
            XWPFTemplate template = XWPFTemplate.compile(is).render(data);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            template.close();
            bytes = bos.toByteArray();
        }

        String text;
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                sb.append(paragraph.getText()).append('\n');
            }
            doc.getTables().forEach(table -> table.getRows().forEach(row -> row.getTableCells().forEach(cell ->
                    cell.getParagraphs().forEach(paragraph -> sb.append(paragraph.getText()).append('\n')))));
            text = sb.toString();
        }

        assertThat(text).contains("Р А П О Р Т");
        assertThat(text).contains("И.И. Тестова");
        assertThat(text).contains("01.02.2026");
        assertThat(text).contains("03.02.2026");
        assertThat(text.trim()).isNotEqualTo("15");
    }
}
