package com.team7.rupiapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.team7.rupiapp.client.data.CheckWhatsappNumberData;
import com.team7.rupiapp.client.data.SendWhatsappMessageData;

@FeignClient(name = "whatsapp-client", url = "https://api.izo.my.id/wahub")
public interface WhatsappClient {
    @PostMapping("/check-number")
    public ResponseEntity<Object> checkNumber(@RequestBody CheckWhatsappNumberData data);

    @PostMapping("/send-message")
    public ResponseEntity<Object> sendMessage(@RequestBody SendWhatsappMessageData data);
}
