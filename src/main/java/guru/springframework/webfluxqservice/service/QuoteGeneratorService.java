package guru.springframework.webfluxqservice.service;

import guru.springframework.webfluxqservice.model.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

public interface QuoteGeneratorService {
    Flux<Quote> fetchQuoteStream(Duration period);
}
