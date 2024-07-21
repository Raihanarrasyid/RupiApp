package com.team7.rupiapp.client.data;

import lombok.Data;

@Data
public class SendWhatsappMessageData {
    private String authkey;
    private String from;
    private String to;
    private String message;
}
