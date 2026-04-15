package org.example.goldbroker.service;

import org.example.goldbroker.dto.PriceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;

@Service
public class PriceService {

    // 1 troy ounce in grams
    private static final BigDecimal GRAMS_PER_TROY_OUNCE = new BigDecimal("31.1034768");

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String baseCurrency; // we set this to INR

    public PriceService(RestTemplate restTemplate,
            @Value("${goldbroker.api.base-url}") String baseUrl,
            @Value("${goldbroker.api.key}") String apiKey,
            @Value("${goldbroker.api.base-currency:INR}") String baseCurrency) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.baseCurrency = baseCurrency;
    }

    public PriceDto fetchLatestPrice(String metal) {
        // Map UI names to Metalprice symbols
        String symbol = switch (metal.toUpperCase()) {
            case "GOLD" -> "XAU";
            case "SILVER" -> "XAG";
            case "PLATINUM" -> "XPT";
            case "PALLADIUM" -> "XPD";
            default -> throw new IllegalArgumentException("Unsupported metal: " + metal);
        };

        // Example:
        // https://api.metalpriceapi.com/v1/latest?api_key=KEY&base=INR&currencies=XAU
        String url = String.format(
                "%s/latest?api_key=%s&base=%s&currencies=%s",
                baseUrl,
                apiKey,
                baseCurrency,
                symbol);

        MetalpriceLatestResponse response = restTemplate.getForObject(url, MetalpriceLatestResponse.class);

        if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
            throw new IllegalStateException("Failed to fetch price from MetalpriceAPI");
        }

        Map<String, BigDecimal> rates = response.getRates();
        if (rates == null) {
            throw new IllegalStateException("No rates returned from MetalpriceAPI");
        }

        // Prefer INRXAU / INRXAG / etc. if available
        String directKey = baseCurrency + symbol; // e.g. "INRXAU"
        BigDecimal pricePerOunceInInr;

        if (rates.containsKey(directKey)) {
            // Already INR per 1 ounce
            pricePerOunceInInr = rates.get(directKey);
        } else if (rates.containsKey(symbol)) {
            // Fallback: invert XAU to get INR per ounce
            BigDecimal rate = rates.get(symbol); // XAU per INR
            pricePerOunceInInr = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
        } else {
            throw new IllegalStateException("No usable rate for " + symbol);
        }

        // Convert per ounce → per gram
        BigDecimal pricePerGramInInr = pricePerOunceInInr
                .divide(GRAMS_PER_TROY_OUNCE, 2, RoundingMode.HALF_UP); // 2 decimal INR

        Instant ts = response.getTimestamp() > 0
                ? Instant.ofEpochSecond(response.getTimestamp())
                : Instant.now();

        // Our "unit" is now clearly 1 gram of metal, in INR
        return new PriceDto(metal.toUpperCase(), pricePerGramInInr, ts);
    }

    // DTO for MetalpriceAPI /latest JSON
    public static class MetalpriceLatestResponse {
        private Boolean success;
        private long timestamp;
        private String base;
        private Map<String, BigDecimal> rates;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public Map<String, BigDecimal> getRates() {
            return rates;
        }

        public void setRates(Map<String, BigDecimal> rates) {
            this.rates = rates;
        }
    }
}
