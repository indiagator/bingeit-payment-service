package com.secor.paymentservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @Column(name = "paymentid", nullable = false, length = 50)
    private String paymentid;

    @Column(name = "subid", length = 50)
    private String subid;

    @Column(name = "status", length = 50)
    private String status;

}