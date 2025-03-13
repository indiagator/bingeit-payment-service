package com.secor.paymentservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("api/v1")
public class MainRestController {

    private static final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    AuthService authService;

    @PostMapping("create/payment/{subid}")
    public ResponseEntity<String> createPayment(@PathVariable String subid,
                                                @RequestHeader("Authorization") String token) throws InterruptedException
    {
        Thread.sleep(10000);

        if(authService.validateToken(token))
        {
            Payment payment = new Payment();
            payment.setPaymentid(String.valueOf(new Random().nextInt()));
            payment.setSubid(subid);
            log.info("Payment created: {}", payment);
            return ResponseEntity.ok(payment.getPaymentid()); // FINAL RESPONSE
        }
        else
        {
            return ResponseEntity.status(401).body("Unauthorized");
        }


    }

}
