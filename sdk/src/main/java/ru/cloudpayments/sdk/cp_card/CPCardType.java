package ru.cloudpayments.sdk.cp_card;

public class CPCardType {

    public final static int UNKNOWN = -1;
    public final static int VISA = 0;
    public final static int MASTER_CARD = 1;
    public final static int MAESTRO = 2;
    public final static int MIR = 3;
    public final static int JCB = 4;

    public static String toString(int value) {
        switch (value) {
            case VISA:
                return "Visa";
            case MASTER_CARD:
                return "MasterCard";
            case MAESTRO:
                return "Maestro";
            case MIR:
                return "MIR";
            case JCB:
                return "JCB";
            default:
                return "Unknown";
        }
    }

    public static int fromString(String value) {
        if ("visa".equals(value.toLowerCase())) {
            return VISA;
        } else if ("mastercard".equals(value.toLowerCase())) {
            return MASTER_CARD;
        } else if ("maestro".equals(value.toLowerCase())) {
            return MAESTRO;
        } else if ("mir".equals(value.toLowerCase())) {
            return MIR;
        } else if ("jcb".equals(value.toLowerCase())) {
            return JCB;
        } else {
            return UNKNOWN;
        }
    }

    public static int getType(String creditCardNumberPart) {

        if (creditCardNumberPart == null || creditCardNumberPart.isEmpty())
            return UNKNOWN;

        int first = Integer.valueOf(creditCardNumberPart.substring(0, 1));

        if (first == 4)
            return VISA;

        if (first == 6)
            return MAESTRO;

        if (creditCardNumberPart.length() < 2)
            return UNKNOWN;

        int firstTwo = Integer.valueOf(creditCardNumberPart.substring(0, 2));

        if (firstTwo == 35)
            return JCB;

        if (firstTwo == 50 || (firstTwo >= 56 && firstTwo <= 58))
            return MAESTRO;

        if (firstTwo >= 51 && firstTwo <= 55)
            return MASTER_CARD;

        if (creditCardNumberPart.length() < 4)
            return UNKNOWN;

        int firstFour = Integer.valueOf(creditCardNumberPart.substring(0, 4));

        if (firstFour >= 2200 && firstFour <= 2204)
            return MIR;

        if (firstFour >= 2221 && firstFour <= 2720)
            return MASTER_CARD;

        return UNKNOWN;
    }
}