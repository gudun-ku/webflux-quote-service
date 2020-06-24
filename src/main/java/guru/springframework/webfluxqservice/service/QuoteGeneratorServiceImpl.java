package guru.springframework.webfluxqservice.service;

import guru.springframework.webfluxqservice.model.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;


@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService {

    private final MathContext MATH_CONTEXT = new MathContext(2);
    private final Random random = new Random();
    private final List<Quote> prices = new ArrayList<>();

    public QuoteGeneratorServiceImpl() {
        prices.add(new Quote("AAPL", 160.16));
        prices.add(new Quote("MSFT", 77.74));
        prices.add(new Quote("GOOG", 847.24));
        prices.add(new Quote("ORCL", 49.51));
        prices.add(new Quote("IBM", 159.34));
        prices.add(new Quote("INTC", 39.29));
        prices.add(new Quote("RHT", 84.16));
        prices.add(new Quote("VMW", 92.21));
        prices.add(new Quote("GAS", 99.16));
        prices.add(new Quote("VOLV", 139.46));
    }

    @Override
    public Flux<Quote> fetchQuoteStream(Duration period) {

        // We use here Flux.generate to create quotes,
        // iterating on each stock starting at index 0
        return Flux.generate(() -> 0,
                (BiFunction<Integer, SynchronousSink<Quote>, Integer>) (index, sink) -> {
                    Quote updatedQuote = updateQuote(prices.get(index));
                    sink.next(updatedQuote);
                    return ++index % prices.size();
                })
                // We want to emit them with a specific period;
                // to do so, we zip Flux with a Flux.interval
                .zipWith(Flux.interval(period))
                .map(Tuple2::getT1)
                // Because values are generated in batches,
                // we need to set their timestamp after their creation!
                .map(quote -> {
                    quote.setInstant(Instant.now());
                    return quote;
                })
                .log("guru.springframework.service.QuoteGenerator");
    }

    private Quote updateQuote(Quote quote) {
        BigDecimal priceChange = quote.getPrice()
                .multiply(new BigDecimal(0.05 * random.nextDouble()), MATH_CONTEXT);
        return new Quote(quote.getTicker(), quote.getPrice().add(priceChange));
    }
}
