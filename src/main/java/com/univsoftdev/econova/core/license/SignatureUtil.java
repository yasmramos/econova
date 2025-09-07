package com.univsoftdev.econova.core.license;

import java.security.*;
import java.util.Base64;

public class SignatureUtil {

    private static final String ALGORITHM = "SHA512withRSA";

    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    public static boolean verify(String data, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance(ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] decodedSignature = Base64.getDecoder().decode(signature);
        return sig.verify(decodedSignature);
    }
}
