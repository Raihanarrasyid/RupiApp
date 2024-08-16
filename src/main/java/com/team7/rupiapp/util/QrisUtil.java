package com.team7.rupiapp.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static class CPM {
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

    @Data
    @NoArgsConstructor
    public static class MPM {
        private String payloadFormatIndicator;
        private String pointOfInitiationMethod;
        private Map<String, MerchantAccountInformation> merchantAccountInformationMap = new HashMap<>();
        private String merchantCategoryCode;
        private String transactionCurrency;
        private String transactionAmount;
        private String countryCode;
        private String merchantName;
        private String merchantCity;
        private String postalCode;
        private AdditionalDataFieldTemplate additionalDataFieldTemplate;

        public void setPayloadFormatIndicator(String payloadFormatIndicator) {
            this.payloadFormatIndicator = payloadFormatIndicator;
        }

        public void setPointOfInitiationMethod(String pointOfInitiationMethod) {
            this.pointOfInitiationMethod = pointOfInitiationMethod;
        }

        public void addMerchantAccountInformation(String id, MerchantAccountInformation info) {
            merchantAccountInformationMap.put(id, info);
        }

        public void setMerchantCategoryCode(String merchantCategoryCode) {
            this.merchantCategoryCode = merchantCategoryCode;
        }

        public void setTransactionCurrency(String transactionCurrency) {
            this.transactionCurrency = transactionCurrency;
        }

        public void setTransactionAmount(String transactionAmount) {
            this.transactionAmount = transactionAmount;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        public void setMerchantCity(String merchantCity) {
            this.merchantCity = merchantCity;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public void setAdditionalDataFieldTemplate(AdditionalDataFieldTemplate additionalDataFieldTemplate) {
            this.additionalDataFieldTemplate = additionalDataFieldTemplate;
        }

        public static String encode(MPM mpm) {
            StringBuilder sb = new StringBuilder();
            sb.append("00").append(padLeft(mpm.payloadFormatIndicator.length(), 2)).append(mpm.payloadFormatIndicator);

            if (mpm.pointOfInitiationMethod != null) {
                sb.append("01").append(padLeft(mpm.pointOfInitiationMethod.length(), 2))
                        .append(mpm.pointOfInitiationMethod);
            }

            for (Map.Entry<String, MerchantAccountInformation> entry : mpm.merchantAccountInformationMap.entrySet()) {
                String info = entry.getValue().toString();
                sb.append(entry.getKey()).append(padLeft(info.length(), 2)).append(info);
            }

            sb.append("52").append(padLeft(mpm.merchantCategoryCode.length(), 2)).append(mpm.merchantCategoryCode);
            sb.append("53").append(padLeft(mpm.transactionCurrency.length(), 2)).append(mpm.transactionCurrency);
            if (mpm.transactionAmount != null) {
                sb.append("54").append(padLeft(mpm.transactionAmount.length(), 2)).append(mpm.transactionAmount);
            }
            sb.append("58").append(padLeft(mpm.countryCode.length(), 2)).append(mpm.countryCode);
            sb.append("59").append(padLeft(mpm.merchantName.length(), 2)).append(mpm.merchantName);
            sb.append("60").append(padLeft(mpm.merchantCity.length(), 2)).append(mpm.merchantCity);

            if (mpm.postalCode != null) {
                sb.append("61").append(padLeft(mpm.postalCode.length(), 2)).append(mpm.postalCode);
            }

            if (mpm.additionalDataFieldTemplate != null) {
                String additionalData = mpm.additionalDataFieldTemplate.toString();
                sb.append("62").append(padLeft(additionalData.length(), 2)).append(additionalData);
            }

            sb.append("63").append("04").append("CEAB");

            return sb.toString();
        }

        private static String padLeft(int length, int padLength) {
            StringBuilder result = new StringBuilder(String.valueOf(length));
            while (result.length() < padLength) {
                result.insert(0, "0");
            }
            return result.toString();
        }
    }

    @Data
    @NoArgsConstructor
    public static class MerchantAccountInformation {
        private String globallyUniqueIdentifier;
        private Map<String, String> paymentNetworkSpecific = new HashMap<>();

        public void setGloballyUniqueIdentifier(String globallyUniqueIdentifier) {
            this.globallyUniqueIdentifier = globallyUniqueIdentifier;
        }

        public void addPaymentNetworkSpecific(String key, String value) {
            paymentNetworkSpecific.put(key, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("00").append(padLeft(globallyUniqueIdentifier.length(), 2)).append(globallyUniqueIdentifier);
            for (Map.Entry<String, String> entry : paymentNetworkSpecific.entrySet()) {
                sb.append(entry.getKey()).append(padLeft(entry.getValue().length(), 2)).append(entry.getValue());
            }
            return sb.toString();
        }

        private static String padLeft(int length, int padLength) {
            String result = String.valueOf(length);
            while (result.length() < padLength) {
                result = "0" + result;
            }
            return result;
        }
    }

    @Data
    @NoArgsConstructor
    public static class AdditionalDataFieldTemplate {
        private String referenceLabel;
        private String terminalLabel;

        public void setReferenceLabel(String referenceLabel) {
            this.referenceLabel = referenceLabel;
        }

        public void setTerminalLabel(String terminalLabel) {
            this.terminalLabel = terminalLabel;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (referenceLabel != null) {
                sb.append("05").append(padLeft(referenceLabel.length(), 2)).append(referenceLabel);
            }
            if (terminalLabel != null) {
                sb.append("07").append(padLeft(terminalLabel.length(), 2)).append(terminalLabel);
            }
            return sb.toString();
        }

        private static String padLeft(int length, int padLength) {
            String result = String.valueOf(length);
            while (result.length() < padLength) {
                result = "0" + result;
            }
            return result;
        }
    }
}
