package raport.service.pdf;

import org.springframework.stereotype.Service;
import raport.model.DailyShiftRaportData;
import raport.model.RaportData;
import raport.model.ServiceBookSupplementData;
import raport.service.compensatory_time.CompensatoryTimeService;
import raport.service.daily_shift.DailyShiftService;
import raport.service.service_book.ServiceBookSupplementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PdfReportFacade {

    private final CompensatoryTimeService compensatoryTimeService;
    private final DailyShiftService dailyShiftService;
    private final ServiceBookSupplementService serviceBookSupplementService;
    private final DocxToPdfConverter docxToPdfConverter;

    public PdfReportFacade(
            CompensatoryTimeService compensatoryTimeService,
            DailyShiftService dailyShiftService,
            ServiceBookSupplementService serviceBookSupplementService,
            DocxToPdfConverter docxToPdfConverter
    ) {
        this.compensatoryTimeService = compensatoryTimeService;
        this.dailyShiftService = dailyShiftService;
        this.serviceBookSupplementService = serviceBookSupplementService;
        this.docxToPdfConverter = docxToPdfConverter;
    }

    public GeneratedFile createOtgulPdf(RaportData data) throws IOException, InterruptedException {
        Path docxPath = Path.of(compensatoryTimeService.generateAndSaveReport(data));
        Path pdfPath = replaceExtension(docxPath, ".pdf");
        if (!Files.exists(pdfPath)) {
            pdfPath = docxToPdfConverter.convert(docxPath);
        }
        byte[] bytes = Files.readAllBytes(pdfPath);
        String fileName = replaceExtension(docxPath.getFileName().toString(), ".pdf");
        return new GeneratedFile(fileName, bytes);
    }

    public GeneratedFile createSutkiPdf(DailyShiftRaportData data) throws IOException, InterruptedException {
        Path docxPath = Path.of(dailyShiftService.generateAndSaveReport(data));
        Path pdfPath = replaceExtension(docxPath, ".pdf");
        if (!Files.exists(pdfPath)) {
            pdfPath = docxToPdfConverter.convert(docxPath);
        }
        byte[] bytes = Files.readAllBytes(pdfPath);
        String fileName = replaceExtension(docxPath.getFileName().toString(), ".pdf");
        return new GeneratedFile(fileName, bytes);
    }

    public GeneratedFile createServiceBookSupplementPdf(ServiceBookSupplementData data) throws IOException, InterruptedException {
        Path docxPath = Path.of(serviceBookSupplementService.generateAndSaveReport(data));
        Path pdfPath = replaceExtension(docxPath, ".pdf");
        if (!Files.exists(pdfPath)) {
            pdfPath = docxToPdfConverter.convert(docxPath);
        }
        byte[] bytes = Files.readAllBytes(pdfPath);
        String fileName = replaceExtension(docxPath.getFileName().toString(), ".pdf");
        return new GeneratedFile(fileName, bytes);
    }

    private static Path replaceExtension(Path p, String newExtWithDot) {
        String fileName = p.getFileName().toString();
        int idx = fileName.lastIndexOf('.');
        String base = idx >= 0 ? fileName.substring(0, idx) : fileName;
        return p.getParent().resolve(base + newExtWithDot);
    }

    private static String replaceExtension(String fileName, String newExtWithDot) {
        int idx = fileName.lastIndexOf('.');
        String base = idx >= 0 ? fileName.substring(0, idx) : fileName;
        return base + newExtWithDot;
    }
}

