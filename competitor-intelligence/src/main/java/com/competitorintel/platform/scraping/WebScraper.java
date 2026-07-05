package com.competitorintel.platform.scraping;

import com.competitorintel.platform.config.AppProperties;
import com.competitorintel.platform.domain.entity.IntelligenceSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebScraper {

    private final AppProperties  appProperties;
    private final SeleniumScraper seleniumScraper;

    public List<ScrapedContent> scrape(IntelligenceSource source) throws IOException {
        if (source.isRequiresJavascript()) return seleniumScraper.scrape(source);
        return jsoupScrape(source);
    }

    private List<ScrapedContent> jsoupScrape(IntelligenceSource source) throws IOException {
        AppProperties.Scraping cfg = appProperties.getScraping();
        Document doc = Jsoup.connect(source.getUrl())
                .userAgent(cfg.getUserAgent())
                .timeout(cfg.getTimeoutMs())
                .followRedirects(true)
                .get();

        List<ScrapedContent> results = new ArrayList<>();
        if (StringUtils.hasText(source.getCssSelector())) {
            for (Element el : doc.select(source.getCssSelector())) {
                ScrapedContent c = fromElement(el, source.getUrl());
                if (c != null) results.add(c);
            }
        } else {
            results.add(ScrapedContent.builder()
                    .title(cap(doc.title(), 900))
                    .url(source.getUrl())
                    .content(cap(doc.body() != null ? doc.body().text() : "", 50000))
                    .publishedAt(LocalDateTime.now())
                    .build());
        }
        log.debug("JSoup scraped {} items from {}", results.size(), source.getUrl());
        return results;
    }

    private ScrapedContent fromElement(Element el, String baseUrl) {
        Element headEl = el.select("h1,h2,h3,.title,.post-title").first();
        String title = headEl != null ? headEl.text() : el.text();
        if (!StringUtils.hasText(title)) return null;

        Element anchor = el.select("a[href]").first();
        String url = (anchor != null && StringUtils.hasText(anchor.absUrl("href")))
                ? anchor.absUrl("href") : baseUrl;

        Element img  = el.select("img[src]").first();
        Element auth = el.select(".author,[rel=author],.byline").first();

        return ScrapedContent.builder()
                .title(cap(title, 900))
                .url(url)
                .content(cap(el.text(), 50000))
                .imageUrl(img  != null ? img.absUrl("src") : null)
                .author(auth  != null ? auth.text()       : null)
                .publishedAt(LocalDateTime.now())
                .build();
    }

    private String cap(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
