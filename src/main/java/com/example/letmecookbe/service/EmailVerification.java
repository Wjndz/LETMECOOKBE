package com.example.letmecookbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerification {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    // Map lưu tạm code theo email (production nên lưu DB + TTL)
    Map<String, String> codes = new ConcurrentHashMap<>();

    public void sendCode(String toEmail) {
        String email = toEmail.trim();              // ✂ trim email
        String code  = String.format("%06d", new Random().nextInt(1_000_000));
        codes.put(email, code);

        log.debug("Generated verification code [{}] for email [{}]", code, email);
        log.debug("Current codes map: {}", codes);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(email);
        msg.setSubject("LetMeCook – Mã xác thực");
        msg.setText("Mã xác thực của bạn là: " + code + " (hết hạn trong 5 phút)");
        mailSender.send(msg);
    }

    public boolean verifyCode(String rawEmail, String rawCode) {
        String email = rawEmail.trim();             // ✂ trim email
        String code  = rawCode  == null ? "" : rawCode.trim();  // ✂ trim code
        String stored = codes.get(email);
        log.debug("Verifying code [{}] for email [{}], stored [{}]", code, email, stored);
        if (stored != null && stored.equals(code)) {
            codes.remove(email);
            return true;
        }
        return false;
    }
}
