package com.ling.framework.security.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.KeyUtil;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@UtilityClass
public class RSAUtils {
    public static RSAKey getRsaKey() {

        RSAPrivateKey privateKey = (RSAPrivateKey) KeyUtil.generatePrivateKey("RSA",
                Base64.decode(readPrivateKeyFromPem()));
        RSAPublicKey publicKey = getRSAPublicKey();

        return new RSAKey.Builder(publicKey).privateKey(privateKey)
                .keyID("GqWlisRUSw0R7emWghZmk76CWqcefP12")
                .build();
    }

    public static RSAPublicKey getRSAPublicKey() {
        return  (RSAPublicKey) KeyUtil.generatePublicKey("RSA", Base64.decode(readPublicKeyFromPem()));
    }

    public static String readPublicKeyFromPem() {
        return readKeyFromPem("public.pem");
    }

    public static String readPrivateKeyFromPem() {
        return readKeyFromPem("private.pem");
    }

    private static String readKeyFromPem(String resourcePath) {
        try (InputStream is = RSAUtils.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-----BEGIN") || line.startsWith("-----END")) {
                    continue;
                }
                sb.append(line.trim());
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PEM file: " + resourcePath, e);
        }
    }
}
