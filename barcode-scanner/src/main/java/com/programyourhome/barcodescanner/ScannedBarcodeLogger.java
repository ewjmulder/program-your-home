package com.programyourhome.barcodescanner;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;

@Component
public class ScannedBarcodeLogger {

    private final Set<MetaBarcodeScannedEvent> metaBarcodeEvents;
    private final Set<ProductBarcodeScannedEvent> productBarcodeEvents;

    public ScannedBarcodeLogger() {
        this.metaBarcodeEvents = new HashSet<>();
        this.productBarcodeEvents = new HashSet<>();
    }

    @EventListener(MetaBarcodeScannedEvent.class)
    public void logMetaEvent(final MetaBarcodeScannedEvent event) {
        this.metaBarcodeEvents.add(event);
    }

    @EventListener(ProductBarcodeScannedEvent.class)
    public void logBarcodeEvent(final ProductBarcodeScannedEvent event) throws URISyntaxException {
        this.productBarcodeEvents.add(event);
    }

    public Set<MetaBarcodeScannedEvent> getMetaBarcodeEvents() {
        return this.metaBarcodeEvents;
    }

    public Set<ProductBarcodeScannedEvent> getProductBarcodeEvents() {
        return this.productBarcodeEvents;
    }

    public List<String> getLogLines() {
        final SortedSet<ApplicationEvent> allEvents =
                new TreeSet<>((event1, event2) -> new Long(event1.getTimestamp()).compareTo(event2.getTimestamp()));
        allEvents.addAll(this.metaBarcodeEvents);
        allEvents.addAll(this.productBarcodeEvents);
        return allEvents.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
