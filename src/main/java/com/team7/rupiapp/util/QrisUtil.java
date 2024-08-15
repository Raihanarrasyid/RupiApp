package com.team7.rupiapp.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

public class QrisUtil {
    private QrisUtil() {
    }

    @Data
    @NoArgsConstructor
    public static class BERTLV {
        private String dataApplicationDefinitionFileName;
        private String dataApplicationLabel;
        private String dataTrack2EquivalentData;
        private String dataApplicationPAN;
        private String dataCardholderName;
        private String dataLanguagePreference;
        private String dataIssuerURL;
        private String dataApplicationVersionNumber;
        private String dataIssuerApplicationData;
        private String dataTokenRequestorID;
        private String dataPaymentAccountReference;
        private String dataLast4DigitsOfPAN;
        private String dataApplicationCryptogram;
        private String dataApplicationTransactionCounter;
        private String dataUnpredictableNumber;
        private String dataTransactionId;
    }

    @Data
    @NoArgsConstructor
    public static class ApplicationTemplate {
        private BERTLV bertlv = new BERTLV();
        private List<ApplicationSpecificTransparentTemplate> applicationSpecificTransparentTemplates = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class CommonDataTemplate {
        private BERTLV bertlv = new BERTLV();
        private List<CommonDataTransparentTemplate> commonDataTransparentTemplates = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class CommonDataTransparentTemplate {
        private BERTLV bertlv = new BERTLV();
    }

    @Data
    @NoArgsConstructor
    public static class ApplicationSpecificTransparentTemplate {
        private BERTLV bertlv = new BERTLV();
    }

    @Data
    @NoArgsConstructor
    public static class QRIS {
        private String dataPayloadFormatIndicator;
        private List<ApplicationTemplate> applicationTemplates = new ArrayList<>();
        private List<CommonDataTemplate> commonDataTemplates = new ArrayList<>();

        public String generatePayload() {
            StringBuilder s = new StringBuilder();
            if (dataPayloadFormatIndicator != null && !dataPayloadFormatIndicator.isEmpty()) {
                s.append(format("85", toHex(dataPayloadFormatIndicator)));
            } else {
                throw new IllegalArgumentException("Payload Format Indicator is required");
            }

            for (ApplicationTemplate t : applicationTemplates) {
                StringBuilder template = new StringBuilder(formattingTemplate(t.getBertlv()));
                for (ApplicationSpecificTransparentTemplate tt : t.getApplicationSpecificTransparentTemplates()) {
                    String ttemplate = formattingTemplate(tt.getBertlv());
                    template.append(format("63", ttemplate));
                }
                s.append(format("61", template.toString()));
            }
            for (CommonDataTemplate t : commonDataTemplates) {
                StringBuilder template = new StringBuilder(formattingTemplate(t.getBertlv()));
                for (CommonDataTransparentTemplate tt : t.getCommonDataTransparentTemplates()) {
                    String ttemplate = formattingTemplate(tt.getBertlv());
                    template.append(format("64", ttemplate));
                }
                s.append(format("62", template.toString()));
            }

            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }

            byte[] decoded = data;
            return Base64.getEncoder().encodeToString(decoded);
        }

        private String format(String id, String value) {
            int length = value.length() / 2;
            String lengthStr = String.format("%02X", length);
            return id + lengthStr + value;
        }

        private String toHex(String input) {
            return String.format("%X", new BigInteger(1, input.getBytes(StandardCharsets.UTF_8)));
        }

        private String formattingTemplate(BERTLV tlv) {
            StringBuilder template = new StringBuilder();
            
            appendIfNotNull(template, "4F", tlv.getDataApplicationDefinitionFileName());
            appendIfNotNull(template, "50", tlv.getDataApplicationLabel());
            appendIfNotNull(template, "57", tlv.getDataTrack2EquivalentData());
            appendIfNotNull(template, "5A", tlv.getDataApplicationPAN());
            appendIfNotNull(template, "5F20", tlv.getDataCardholderName());
            appendIfNotNull(template, "5F2D", tlv.getDataLanguagePreference());
            appendIfNotNull(template, "5F50", tlv.getDataIssuerURL());
            appendIfNotNull(template, "9F08", tlv.getDataApplicationVersionNumber());
            appendIfNotNull(template, "9F10", tlv.getDataIssuerApplicationData());
            appendIfNotNull(template, "9F19", tlv.getDataTokenRequestorID());
            appendIfNotNull(template, "9F24", tlv.getDataPaymentAccountReference());
            appendIfNotNull(template, "9F25", tlv.getDataLast4DigitsOfPAN());
            appendIfNotNull(template, "9F26", tlv.getDataApplicationCryptogram());
            appendIfNotNull(template, "9F36", tlv.getDataApplicationTransactionCounter());
            appendIfNotNull(template, "9F37", tlv.getDataUnpredictableNumber());
            appendIfNotNull(template, "9F4E", tlv.getDataTransactionId());
        
            return template.toString();
        }
        
        private void appendIfNotNull(StringBuilder sb, String id, String value) {
            if (value != null) {
                sb.append(format(id, toHex(value)));
            }
        }
    }
}