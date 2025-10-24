package com.example.movie.config.pay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VnpayConfig {
    private String tmnCode;
    private String hashSecret;
    private String payUrl;
    private String returnUrl;
    private int expireMinutes = 15;

    // getters/setters
    public String getTmnCode(){return tmnCode;} public void setTmnCode(String v){tmnCode=v;}
    public String getHashSecret(){return hashSecret;} public void setHashSecret(String v){hashSecret=v;}
    public String getPayUrl(){return payUrl;} public void setPayUrl(String v){payUrl=v;}
    public String getReturnUrl(){return returnUrl;} public void setReturnUrl(String v){returnUrl=v;}
    public int getExpireMinutes(){return expireMinutes;} public void setExpireMinutes(int v){expireMinutes=v;}
}

