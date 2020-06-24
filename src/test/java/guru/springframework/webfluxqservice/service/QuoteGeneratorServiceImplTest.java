package guru.springframework.webfluxqservice.service;

import guru.springframework.webfluxqservice.model.Quote;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@SpringBootTest
public class QuoteGeneratorServiceImplTest {

    @Autowired
    QuoteGeneratorServiceImpl quoteGeneratorService;

    @BeforeAll
    public static void setUp() throws Exception {

    }

    @Test
    public void fetchQuoteStream() throws Exception {

        // get Flux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200L));

        quoteFlux.take(10)
                .subscribe(System.out::println);
    }

    @Test
    public void fetchQuoteStreamCountDown() throws Exception {

        // get Flux of quotes
        Flux<Quote> quoteFlux = quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200L));

        // subscriber lambda
        Consumer<Quote> println = System.out::println;

        // error handler
        Consumer<Throwable> errorHandler = e -> System.out.println("!!! Some error occured !!!");

        // all done runnable
        // firstly, set CountdownLatch to 1
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runnable allDone = () -> countDownLatch.countDown();

        quoteFlux.take(10)
                .subscribe(println, errorHandler, allDone);

        countDownLatch.await();
    }
}
