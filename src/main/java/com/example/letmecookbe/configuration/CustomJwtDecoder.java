package com.example.letmecookbe.configuration;

import com.example.letmecookbe.dto.request.IntrospectRequest;
import com.example.letmecookbe.entity.Account;
import com.example.letmecookbe.enums.AccountStatus;
import com.example.letmecookbe.repository.AccountRepository;
import com.example.letmecookbe.service.AuthService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            var response = authService.introspect(IntrospectRequest.builder()
                    .token(token)
                    .build());

            if (!response.isValid())
                throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        Jwt jwt = nimbusJwtDecoder.decode(token);

        // Lấy email từ claim "sub" (đồng bộ với AuthService)
        String email = jwt.getClaim("sub").toString();
        Account account = accountRepository.findAccountByEmail(email)
                .orElseThrow(() -> new JwtException("Account not found"));

        if (account.getStatus() == AccountStatus.BANNED) {
            if (account.getBanEndDate() != null) {
                if (account.getBanEndDate().isBefore(LocalDateTime.now())) {
                    account.setStatus(AccountStatus.ACTIVE);
                    account.setBanEndDate(null);
                    accountRepository.save(account);
                } else {
                    long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), account.getBanEndDate());
                    throw new JwtException("Tài khoản bị ban " + daysRemaining + " ngày");
                }
            } else {
                // If banEndDate is null, treat as not banned
                account.setStatus(AccountStatus.ACTIVE);
                accountRepository.save(account);
            }
        }
        return jwt;
    }
}