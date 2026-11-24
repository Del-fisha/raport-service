package raport.service.pdf;

import org.springframework.stereotype.Service;
import raport.model.DailyShiftRaportData;
import raport.model.FinancialAssistanceData;
import raport.model.RaportData;
import raport.model.ServiceBookSupplementData;
import raport.model.ShiftTransferData;
import raport.service.compensatory_time.CompensatoryTimeService;
import raport.service.daily_shift.DailyShiftService;
import raport.service.financial_assistance.FinancialAssistanceService;
import raport.service.service_book.ServiceBookSupplementService;
import raport.service.shift_transfer.ShiftTransferService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PdfReportFacade {

    private final CompensatoryTimeService compensatoryTimeService;
    private final DailyShiftService dailyShiftService;
    private final ServiceBookSupplementService serviceBookSupplementService;
    private final ShiftTransferService shiftTransferService;
    private final FinancialAssistanceService financialAssistanceService;
    private final DocxToPdfConverter docxToPdfConverter;

    public PdfReportFacade(
            CompensatoryTimeService compensatoryTimeService,
            DailyShiftService dailyShiftService,
            ServiceBookSupplementService serviceBookSupplementService,
            ShiftTransferService shiftTransferService,
            FinancialAssistanceService financialAssistanceService,
            DocxToPdfConverter docxToPdfConverter
    ) {
        this.compensatoryTimeService = compensatoryTimeService;
        this.dailyShiftService = dailyShiftService;
        this.serviceBookSupplementService = serviceBookSupplementService;
        this.shiftTransferService = shiftTransferService;
        this.financialAssistanceService = financialAssistanceService;
        this.docxToPdfConverter = docxToPdfConverter;
    }

    public GeneratedFile createOtgulPdf(RaportData data) throws IOException, InterruptedException {
        return readPdf(Path.of(compensatoryTimeService.generateAndSaveReport(data)));
    }

    public GeneratedFile createSutkiPdf(DailyShiftRaportData data) throws IOException, InterruptedException {
        return readPdf(Path.of(dailyShiftService.generateAndSaveReport(data)));
    }

    private GeneratedFile readPdf(Path docxPath) throws IOException, InterruptedException {
        Path pdfPath = replaceExtension(docxPath, ".pdf");
        if (!Files.exists(pdfPath)) {
            pdfPath = docxToPdfConverter.convert(docxPath);
        }
        byte[] bytes = Files.readAllBytes(pdfPath);
        String fileName = replaceExtension(docxPath.getFileName().toString(), ".pdf");
        return new GeneratedFile(fileName, bytes);
    }

    public GeneratedFile createShiftTransferPdf(ShiftTransferData data) throws IOException, InterruptedException {
        return readPdf(Path.of(shiftTransferService.generateAndSaveReport(data)));
    }

    public GeneratedFile createFinancialAssistancePdf(FinancialAssistanceData data) throws IOException, InterruptedException {
        return readPdf(Path.of(financialAssistanceService.generateAndSaveReport(data)));
    }

    public GeneratedFile createServiceBookSupplementPdf(ServiceBookSupplementData data) throws IOException, InterruptedException {
        return readPdf(Path.of(serviceBookSupplementService.generateAndSaveReport(data)));
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

