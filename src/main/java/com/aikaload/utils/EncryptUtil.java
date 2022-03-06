package com.aikaload.utils;

import java.util.Base64;

public class EncryptUtil {
    public static String encrypt(String strToEncrypt, String secret)
    {
        try {

            String newString = strToEncrypt+secret;
            String enc = Base64.getEncoder().withoutPadding().encodeToString(newString.getBytes());
            return reverse(enc);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return null;
    }


    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            strToDecrypt = reverse(strToDecrypt);
            byte[] dec = Base64.getDecoder().decode(strToDecrypt);
            String textdec = new String(dec);
            if (!textdec.contains(secret)) {
                System.out.println("Could not decrypt");
                return  null;
            }
            return textdec.replace(secret,"");
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String reverse(String input)
    {
        // convert String to character array
        // by using toCharArray
        char[] try1 = input.toCharArray();
        StringBuilder revString = new StringBuilder();
        for (int i = try1.length-1; i>=0; i--){
            revString.append(try1[i]);
        }
        return revString.toString();
    }



}
