package com.team7.rupiapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.team7.rupiapp.client.data.SendWhatsappMessageData;

@FeignClient(name = "whatsapp-client", url = "https://api.izo.my.id/wahub")
public interface WhatsappClient {
    @PostMapping("/send-message")
    public void sendWhatsappMessage(@RequestBody SendWhatsappMessageData data);
}
