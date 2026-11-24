package raport.service.pdf;

import com.deepoove.poi.xwpf.NiceXWPFDocument;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public final class DocxPageMerger {

    private DocxPageMerger() {
    }

    public static byte[] concat(List<byte[]> pages) throws IOException {
        if (pages == null || pages.isEmpty()) {
            throw new IOException("Нет страниц для сборки");
        }
        if (pages.size() == 1) {
            return pages.get(0);
        }
        NiceXWPFDocument acc = null;
        try {
            acc = new NiceXWPFDocument(new ByteArrayInputStream(pages.get(0)));
            for (int i = 1; i < pages.size(); i++) {
                try (NiceXWPFDocument next = new NiceXWPFDocument(new ByteArrayInputStream(pages.get(i)))) {
                    XWPFParagraph breakParagraph = acc.createParagraph();
                    breakParagraph.createRun().addBreak(BreakType.PAGE);
                    acc = acc.merge(next);
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            acc.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Не удалось склеить страницы рапорта", e);
        } finally {
            if (acc != null) {
                acc.close();
            }
        }
    }
}
