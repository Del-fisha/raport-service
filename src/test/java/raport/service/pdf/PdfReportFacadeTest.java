package raport.service.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import raport.model.DailyShiftRaportData;
import raport.model.RaportData;
import raport.service.compensatory_time.CompensatoryTimeService;
import raport.service.daily_shift.DailyShiftService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PdfReportFacadeTest {

    @TempDir
    Path tempDir;

    @Test
    void createOtgulPdf_generatesDocx_thenConverts_thenReturnsPdfBytes() throws Exception {
        CompensatoryTimeService compensatory = mock(CompensatoryTimeService.class);
        DailyShiftService dailyShift = mock(DailyShiftService.class);
        DocxToPdfConverter converter = mock(DocxToPdfConverter.class);

        Path docx = tempDir.resolve("report.docx");
        Path pdf = tempDir.resolve("report.pdf");
        Files.writeString(docx, "docx", StandardCharsets.UTF_8);
        Files.write(pdf, "pdf-bytes".getBytes(StandardCharsets.UTF_8));

        when(compensatory.generateAndSaveReport(any(RaportData.class))).thenReturn(docx.toString());

        PdfReportFacade facade = new PdfReportFacade(compensatory, dailyShift, converter);

        GeneratedFile out = facade.createOtgulPdf(new RaportData());

        verify(compensatory).generateAndSaveReport(any(RaportData.class));
        verifyNoInteractions(converter);

        assertThat(out.fileName()).isEqualTo("report.pdf");
        assertThat(new String(out.bytes(), StandardCharsets.UTF_8)).isEqualTo("pdf-bytes");
        verifyNoInteractions(dailyShift);
    }

    @Test
    void createSutkiPdf_generatesDocx_thenConverts_thenReturnsPdfBytes() throws Exception {
        CompensatoryTimeService compensatory = mock(CompensatoryTimeService.class);
        DailyShiftService dailyShift = mock(DailyShiftService.class);
        DocxToPdfConverter converter = mock(DocxToPdfConverter.class);

        Path docx = tempDir.resolve("sutki.docx");
        Path pdf = tempDir.resolve("sutki.pdf");
        Files.writeString(docx, "docx", StandardCharsets.UTF_8);
        Files.write(pdf, "pdf-bytes".getBytes(StandardCharsets.UTF_8));

        when(dailyShift.generateAndSaveReport(any(DailyShiftRaportData.class))).thenReturn(docx.toString());

        PdfReportFacade facade = new PdfReportFacade(compensatory, dailyShift, converter);

        GeneratedFile out = facade.createSutkiPdf(new DailyShiftRaportData());

        verify(dailyShift).generateAndSaveReport(any(DailyShiftRaportData.class));
        verifyNoInteractions(converter);

        assertThat(out.fileName()).isEqualTo("sutki.pdf");
        assertThat(new String(out.bytes(), StandardCharsets.UTF_8)).isEqualTo("pdf-bytes");
        verifyNoInteractions(compensatory);
    }
}

