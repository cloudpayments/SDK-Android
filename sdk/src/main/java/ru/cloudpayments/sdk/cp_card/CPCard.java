package ru.cloudpayments.sdk.cp_card;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CPCard {

    private String number;
    private String expDate;
    private String cvv;

    private static final String KEY_VERSION() {
        return "04";
    }

    private static final String PUBLIC_KEY() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArBZ1NNjvszen6BNWsgyDUJvDUZDtvR4jKNQtEwW1iW7hqJr0TdD8hgTxw3DfH+Hi/7ZjSNdH5EfChvgVW9wtTxrvUXCOyJndReq7qNMo94lHpoSIVW82dp4rcDB4kU+q+ekh5rj9Oj6EReCTuXr3foLLBVpH0/z1vtgcCfQzsLlGkSTwgLqASTUsuzfI8viVUbxE1a+600hN0uBh/CYKoMnCp/EhxV8g7eUmNsWjZyiUrV8AA/5DgZUCB+jqGQT/Dhc8e21tAkQ3qan/jQ5i/QYocA/4jW3WQAldMLj0PA36kINEbuDKq8qRh25v+k4qyjb7Xp4W2DywmNtG3Q20MQIDAQAB";
    }

    private CPCard() {
    }

    public CPCard(String number) throws IllegalArgumentException {

        if (!isValidNumber(number)) {
            throw new IllegalArgumentException("Card number is not correct.");
        }

        this.number = number;
    }

    public CPCard(String number, String expDate, String cvv) throws IllegalArgumentException {

        if (!isValidNumber(number)) {
            throw new IllegalArgumentException("Card number is not correct.");
        }

        if (!isValidExpDate(expDate)) {
            throw new IllegalArgumentException("Expiration date is not correct.");
        }

        this.number = number;
        this.expDate = expDate;
        this.cvv = cvv;
    }

    /**
     * @return Тип карты
     */
    public String getType() {
        return getType(number);
    }

    /**
     * @return Тип карты
     */
    private String getType(String number) {
        return CPCardType.toString(CPCardType.getType(number));
    }

    /**
     * Валидация номера карты
     * @return
     */
    public boolean isValidNumber() {
        return isValidNumber(number);
    }

    /**
     * Валидация номера карты
     * @return
     */
    public static boolean isValidNumber(String number) {
        boolean res = false;
        int sum = 0;
        int i;
        number = prepareCardNumber(number);
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.length() % 2 == 0) {
            for (i = 0; i < number.length(); i += 2) {
                int c = Integer.parseInt(number.substring(i, i + 1));
                c *= 2;
                if (c > 9) {
                    c -= 9;
                }
                sum += c;
                sum += Integer.parseInt(number.substring(i + 1, i + 2));
            }
        } else {
            for (i = 1; i < number.length(); i += 2) {
                int c = Integer.parseInt(number.substring(i, i + 1));
                c *= 2;
                if (c > 9) {
                    c -= 9;
                }
                sum += c;
                sum += Integer.parseInt(number.substring(i - 1, i));
            }
//			adding last character
            sum += Integer.parseInt(number.substring(i - 1, i));
        }
        //final check
        if (sum % 10 == 0) {
            res = true;
        }
        return res;
    }

    /**
     * Валидация даты
     * @return
     */
    public boolean isValidExpDate() {
        return isValidExpDate(expDate);
    }

    /**
     * Валидация даты
     * @return
     */
    public static boolean isValidExpDate(String expDate) {
        if (expDate.length() != 4) {
            return false;
        }

        DateFormat format = new SimpleDateFormat("MMyy", Locale.ENGLISH);
        format.setLenient(false);
        try {
            Date date = format.parse(expDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            date = calendar.getTime();

            Date currentDate = new Date();
            if (currentDate.before(date)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Генерим криптограму для карты
     * @param publicId
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public String cardCryptogram(String publicId) throws UnsupportedEncodingException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {
        return cardCryptogram(number, expDate, cvv, publicId);
    }

    /**
     * Генерим криптограму для карты
     * @param cardNumber
     * @param cardExp
     * @param cardCvv
     * @param publicId
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    private String cardCryptogram(String cardNumber, String cardExp, String cardCvv, String publicId) throws UnsupportedEncodingException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {

        cardNumber = prepareCardNumber(cardNumber);
        String shortNumber = cardNumber.substring(0, 6) + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        String exp = cardExp.substring(2, 4) + cardExp.substring(0, 2);
        String s = cardNumber + "@" + exp + "@" + cardCvv + "@" + publicId;
        byte[] bytes = s.getBytes("ASCII");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.ENCRYPT_MODE, getRSAKey(), random);
        byte[] crypto = cipher.doFinal(bytes);
        String crypto64 = "02" +
                shortNumber +
                exp + KEY_VERSION() +
                Base64.encodeToString(crypto, Base64.DEFAULT);
        String[] cr_array = crypto64.split("\n");
        crypto64 = "";
        for (int i = 0; i < cr_array.length; i++) {
            crypto64 += cr_array[i];
        }
        return crypto64;
    }

    /**
     * Генерим криптограму для CVV
     * @param cardCvv
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static String cardCryptogramForCVV(String cardCvv) throws UnsupportedEncodingException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {

        byte[] bytes = cardCvv.getBytes("ASCII");
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.ENCRYPT_MODE, getRSAKey(), random);
        byte[] crypto = cipher.doFinal(bytes);
        String crypto64 = "03" +
                KEY_VERSION() +
                Base64.encodeToString(crypto, Base64.DEFAULT);
        String[] cr_array = crypto64.split("\n");
        crypto64 = "";
        for (int i = 0; i < cr_array.length; i++) {
            crypto64 += cr_array[i];
        }
        return crypto64;
    }

    private static String prepareCardNumber(String cardNumber) {
        return cardNumber.replaceAll("\\s", "");
    }

    private static PublicKey getRSAKey() {
        try {
            byte[] keyBytes = Base64.decode(PUBLIC_KEY().getBytes("utf-8"), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf;
            kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
