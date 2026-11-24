package raport.service.shift_transfer;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import raport.service.DeclensionService;
import raport.service.daily_shift.RankPositionGenitiveService;
import raport.service.pdf.DocxToPdfConverter;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShiftTransferRenderTest {

    @Test
    void templateReceivesReplacementDates() throws Exception {
        ShiftTransferService service = new ShiftTransferService(
                mock(DeclensionService.class),
                new RankPositionGenitiveService(),
                mock(DocxToPdfConverter.class)
        );
        Map<String, String> data = new HashMap<>();
        data.put("commanderFullPost", "Начальнику");
        data.put("commanderRank", "полковнику");
        data.put("commanderFullName", "М.В. Платову");
        data.put("employeeFullPost", "полицейского");
        data.put("employeeRank", "сержанта");
        data.put("employeeFullName", "И.И. Тестова");
        data.put("oldTimeDate", "11.04.2026");
        data.put("newTimeDate", "12.04.2026");
        data.put("petitionerFullPost", "Командир");
        data.put("petitionerRank", "майор");
        data.put("petitionerFullName", "П.П. Командиров");
        data.put("reportDate", "10.04.2026");

        byte[] docx = service.renderDocxBytes(data);
        String text;
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docx))) {
            StringBuilder sb = new StringBuilder();
            document.getParagraphs().forEach(p -> sb.append(p.getText()).append('\n'));
            document.getTables().forEach(t -> t.getRows().forEach(r -> r.getTableCells().forEach(c ->
                    c.getParagraphs().forEach(p -> sb.append(p.getText()).append('\n')))));
            text = sb.toString();
        }
        assertThat(text).contains("И.И. Тестова");
        assertThat(text).contains("11.04.2026");
        assertThat(text).contains("12.04.2026");
        assertThat(text).doesNotContain("{{");
    }
}
