package com.drinkmall.payment;

import org.springframework.http.HttpHeaders;

public interface PayCallbackVerifier {
    void verify(String body, HttpHeaders headers);
}
