package com.devjchankchan.encryptedfilereader;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final String ERROR_TEXT = "(・A・)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        writeFile();
        TextView textView = (TextView) findViewById(R.id.textview);
        try {
//            textView.setText(loadText("encrypted.txt"));
            textView.setText(decrypt(loadText("encrypted.txt"), getKey()));
        } catch (Exception e) {

        }
    }

    private void writeFile() {
        try {
            FileWriter writer = new FileWriter(new File(Environment.getExternalStorageDirectory().toString(), "encrypted.txt"));
            writer.write(encrypt(loadText("test.txt"), getKey()));
            writer.close();
        } catch (Exception e) {

        }
    }

    private String getKey() {
        // "ABCDEFGHIJKLMNOP"を返却します
        byte val[] = new byte[16];
        int value = 65;
        for (int i = 0; i < 16; i++) {
            val[i] = Byte.valueOf(String.valueOf(value + i));
        }

        String ret = new String(val);
        return ret;
    }

    private String getEncryptionType() {
        StringBuilder sb = new StringBuilder();
        String string = ERROR_TEXT.replace("・", "");
        string = string.replace("(", "");
        string = string.replace(")", "");
        sb.append(string);
        byte val[] = new byte[1];
        val[0] = 0x53;
        sb.append("E");
        sb.append(new String(val));

        String ret = sb.toString();
        return ret;
    }

    private String loadText(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            return ERROR_TEXT;
        }
    }

    private String encrypt(String originalString, String secretKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] originalBytes = originalString.getBytes();
        byte[] secretKeyBytes = secretKey.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, getEncryptionType());
        Cipher cipher = Cipher.getInstance(getEncryptionType());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptBytes = cipher.doFinal(originalBytes);
        byte[] encryptBytesBase64 = Base64.encode(encryptBytes, Base64.DEFAULT);
//        byte[] encryptBytesBase64 = Base64.encodeBase64(encryptBytes, false);

        return new String(encryptBytesBase64);
    }

    private String decrypt(String encryptBytesBase64String, String secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        byte[] encryptBytes = Base64.decode(encryptBytesBase64String, Base64.DEFAULT);
//        byte[] encryptBytes = Base64.decodeBase64(encryptBytesBase64String);
        byte[] secretKeyBytes = secretKey.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, getEncryptionType());
        Cipher cipher = Cipher.getInstance(getEncryptionType());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] originalBytes = cipher.doFinal(encryptBytes);

        return new String(originalBytes);
    }
}
