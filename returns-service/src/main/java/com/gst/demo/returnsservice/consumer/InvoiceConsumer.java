
package com.gst.demo.returnsservice.consumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceConsumer {

    private final Counter returnsCounter;

    public InvoiceConsumer(MeterRegistry registry) {
        this.returnsCounter = Counter.builder("returns_processed_total").description("Total returns processed").register(registry);
    }

    @KafkaListener(topics = "invoices", groupId = "returns-group")
    public void listen(String message) throws InterruptedException {
        // simulate processing
        Thread.sleep(500);
        returnsCounter.increment();
        System.out.println("Processed invoice: " + message);
    }
}
