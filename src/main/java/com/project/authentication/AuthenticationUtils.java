package com.project.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Date;

/*
    Our simple static class that demonstrates how to create and decode JWTs.
 */
public class AuthenticationUtils {
    private static String SECRET_KEY;

    //Sample method to construct a JWT
    public static String createJWT(String id, String issuer, String subject, long ttlMillis)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //Get the private Key
        KeyStore keystore=KeyStore.getInstance("JKS");
        BASE64Encoder encoder=new BASE64Encoder();
        keystore.load(new FileInputStream("keypair.jks"), "password".toCharArray());
        KeyPair keyPair = getPrivateKey(keystore, "teiid", "password".toCharArray());
        PrivateKey privateKey=keyPair.getPrivate();
        String SECRET_KEY=encoder.encode(privateKey.getEncoded());
        System.out.println(SECRET_KEY);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static boolean checkTokenIntegrity(String token, int tokenType) {
        switch(tokenType) {
            case 0: //RestToken
                try { return AuthenticationUtils.verifyServerToken(token); } catch (JwtException e) { return false; }

            case 1: //Google Token
                try { return AuthenticationUtils.verifyGoogleToken(token); } catch ( IllegalArgumentException |
                        GeneralSecurityException | IOException e) { return false; }

            case 2: //Facebook Token
                try { return AuthenticationUtils.veifyFacebookToken(token); } catch (IOException | JSONException e) { return false; }
        }

        return false;
    }

    private static boolean veifyFacebookToken(String token) throws IOException, JSONException {
        String FACEBOOK_APP_ID = "246982563186236";
        URL url = null;
        HttpURLConnection con = null;

        url = new URL("https://graph.facebook.com/" + FACEBOOK_APP_ID + " ?access_token=" + token);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
            JSONObject obj = new JSONObject(response.toString());
            return (obj.getString("id").equals(FACEBOOK_APP_ID)) ? true : false;
        }

        return false;
    }

    private static boolean verifyServerToken(String jwt) {
        Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();

        return true;
    }

    private static boolean verifyGoogleToken(String token) throws GeneralSecurityException, IOException, IllegalArgumentException {
        String aud = "744778791116-5mm7c9an5oe38kh86qi36imgem919gq1.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(aud))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        return (idToken != null) ? true : false;
    }

    public static KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
        try {
            Key key=keystore.getKey(alias,password);
            if(key instanceof PrivateKey) {
                Certificate cert= keystore.getCertificate(alias);
                PublicKey publicKey=cert.getPublicKey();
                return new KeyPair(publicKey,(PrivateKey)key);
            }
        } catch (UnrecoverableKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        }
        return null;
    }
}