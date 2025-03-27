package com.secor.paymentservice;

import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@RestController
@RequestMapping("api/v1")
public class MainRestController {

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    AuthService authService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    @Qualifier("web-client-sub-service")
    WebClient webSubClient;

    @PostMapping("create/payment/{subid}")
    public ResponseEntity<String> createPayment(@PathVariable String subid,
                                                @RequestHeader("Authorization") String token) throws InterruptedException
    {
       // Thread.sleep(10000);

        //throw new RuntimeException("Payment Service is down");

        if(authService.validateToken(token))
        {
            Payment payment = new Payment();
            payment.setPaymentid(String.valueOf(new Random().nextInt()));
            payment.setSubid(subid);
            payment.setStatus("UNPAID");
            log.info("Payment created: {}", payment);
            paymentRepository.save(payment);
            return ResponseEntity.ok(payment.getPaymentid()); // FINAL RESPONSE
        }
        else
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }


    }

    @PostMapping("make/payment/{paymentid}")
    public ResponseEntity makePayment(@PathVariable("paymentid") String paymentid)
    {
        log.info("Making payment for paymentid: {}", paymentid);
        paymentRepository.updateStatusByPaymentid("PAID",paymentid);
        log.info("Payment made for paymentid: {}", paymentid);

        //forward request to subscription service
        String subResponse =  webSubClient.post()
                .uri("/update/subs/{subid}", paymentRepository.findById(paymentid).get().getSubid())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("Response from subscription service: {}", subResponse);

        return ResponseEntity.ok(paymentRepository.findById(paymentid));
    }

    @GetMapping("get/payment/{subid}")
    public ResponseEntity<?> getPayment(@PathVariable("subid") String subid)
    {

        Payment payment = paymentRepository.findBySubidIgnoreCase(subid);
        if(payment != null)
        {
            return ResponseEntity.ok(payment);
        }
        else
        {
            return ResponseEntity.status(404).body("Payment not found");
        }

    }

}
