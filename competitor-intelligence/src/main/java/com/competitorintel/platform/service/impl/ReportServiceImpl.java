package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.entity.Report;
import com.competitorintel.platform.domain.repository.CompetitorRepository;
import com.competitorintel.platform.domain.repository.IntelligenceEventRepository;
import com.competitorintel.platform.domain.repository.ReportRepository;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.dto.response.ReportResponse;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.ReportMapper;
import com.competitorintel.platform.service.ReportService;
import com.opencsv.CSVWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final IntelligenceEventRepository eventRepository;
    private final CompetitorRepository        competitorRepository;
    private final ReportRepository            reportRepository;
    private final ReportMapper                reportMapper;

    @Override
    @Transactional
    public void exportEventsCsv(Long competitorId, int daysBack, String username,
                                  HttpServletResponse response) throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(daysBack);
        List<IntelligenceEvent> events = loadEvents(competitorId, from);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"events-" + LocalDateTime.now().toLocalDate() + ".csv\"");

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(new String[]{"ID","Competitor","Title","Category","Sentiment",
                    "Relevance","Importance","Published","URL","AI Summary"});
            for (IntelligenceEvent e : events) {
                writer.writeNext(new String[]{
                        str(e.getId()),
                        e.getCompetitor() != null ? e.getCompetitor().getName() : "",
                        e.getTitle(),
                        str(e.getCategory()),
                        str(e.getSentiment()),
                        str(e.getRelevanceScore()),
                        str(e.getImportanceScore()),
                        e.getPublishedAt() != null ? e.getPublishedAt().format(FMT) : "",
                        e.getUrl() != null ? e.getUrl() : "",
                        e.getAiSummary() != null ? e.getAiSummary() : ""
                });
            }
        }
        saveReportRecord("Events Export CSV", competitorId, "EVENTS", "CSV", username, events.size(), from);
    }

    @Override
    @Transactional
    public void exportEventsPdf(Long competitorId, int daysBack, String username,
                                  HttpServletResponse response) throws Exception {
        LocalDateTime from = LocalDateTime.now().minusDays(daysBack);
        List<IntelligenceEvent> events = loadEvents(competitorId, from);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"events-" + LocalDateTime.now().toLocalDate() + ".pdf\"");

        PdfWriter   pdfWriter = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDoc    = new PdfDocument(pdfWriter);
        Document    document  = new Document(pdfDoc);

        document.add(new Paragraph("Competitor Intelligence Report")
                .setFontSize(18).setBold());
        document.add(new Paragraph("Generated: " + LocalDateTime.now().format(FMT))
                .setFontSize(10));
        document.add(new Paragraph("Period: last " + daysBack + " days | Events: " + events.size())
                .setFontSize(10));

        Table table = new Table(new float[]{2, 4, 2, 2, 2});
        table.addHeaderCell("Competitor");
        table.addHeaderCell("Title");
        table.addHeaderCell("Category");
        table.addHeaderCell("Sentiment");
        table.addHeaderCell("Published");

        for (IntelligenceEvent e : events) {
            table.addCell(e.getCompetitor() != null ? e.getCompetitor().getName() : "");
            table.addCell(e.getTitle() != null ? cap(e.getTitle(), 80) : "");
            table.addCell(str(e.getCategory()));
            table.addCell(str(e.getSentiment()));
            table.addCell(e.getPublishedAt() != null ? e.getPublishedAt().format(FMT) : "");
        }
        document.add(table);
        document.close();

        saveReportRecord("Events Export PDF", competitorId, "EVENTS", "PDF", username, events.size(), from);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getMyReports(String username, Pageable pageable) {
        return PageResponse.of(reportRepository
                .findByCreatedByOrderByCreatedAtDesc(username, pageable)
                .map(reportMapper::toResponse));
    }

    @Override
    @Transactional
    public void deleteReport(Long id, String username) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", id));
        reportRepository.delete(r);
    }

    // ------------------------------------------------------------------ //

    private List<IntelligenceEvent> loadEvents(Long competitorId, LocalDateTime from) {
        if (competitorId != null) {
            return eventRepository.findByCompetitorIdAndDateRange(
                    competitorId, from, LocalDateTime.now());
        }
        return eventRepository.findAll().stream()
                .filter(e -> e.getPublishedAt() != null && e.getPublishedAt().isAfter(from))
                .toList();
    }

    private void saveReportRecord(String name, Long competitorId, String type, String format,
                                    String username, int rows, LocalDateTime from) {
        Report r = new Report();
        r.setName(name);
        r.setReportType(type);
        r.setFormat(format);
        r.setRowCount(rows);
        r.setDateFrom(from);
        r.setDateTo(LocalDateTime.now());
        r.setGeneratedAt(LocalDateTime.now());
        if (competitorId != null) {
            competitorRepository.findById(competitorId).ifPresent(r::setCompetitor);
        }
        reportRepository.save(r);
    }

    private String str(Object o)  { return o != null ? o.toString() : ""; }
    private String cap(String s, int max) {
        return (s == null) ? "" : (s.length() > max ? s.substring(0, max) + "…" : s);
    }
}
