package org.apache.commons.codec.digest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static sketchy.Sketchy.*;
import sketchy.annotation.*;

public class Sha2Cryptm292Template {

    private static final int ROUNDS_DEFAULT = 5000;

    private static final int ROUNDS_MAX = 999999999;

    private static final int ROUNDS_MIN = 1000;

    private static final String ROUNDS_PREFIX = "rounds=";

    private static final int SHA256_BLOCKSIZE = 32;

    static final String SHA256_PREFIX = "$5$";

    private static final int SHA512_BLOCKSIZE = 64;

    static final String SHA512_PREFIX = "$6$";

    private static final Pattern SALT_PATTERN = Pattern.compile("^\\$([56])\\$(rounds=(\\d+)\\$)?([\\.\\/a-zA-Z0-9]{1,16}).*");

    @sketchy.annotation.Entry()
    public static String sha256Crypt(final byte[] keyBytes) {
        return sha256Crypt(keyBytes, null);
    }

    public static String sha256Crypt(final byte[] keyBytes, String salt) {
        if (salt == null) {
            salt = SHA256_PREFIX + B64.getRandomSalt((int) intVal().eval());
        }
        return sha2Crypt(keyBytes, salt, SHA256_PREFIX, SHA256_BLOCKSIZE, MessageDigestAlgorithms.SHA_256);
    }

    public static String sha256Crypt(final byte[] keyBytes, String salt, final Random random) {
        if (salt == null) {
            salt = SHA256_PREFIX + B64.getRandomSalt((int) intVal().eval(), random);
        }
        return sha2Crypt(keyBytes, salt, SHA256_PREFIX, SHA256_BLOCKSIZE, MessageDigestAlgorithms.SHA_256);
    }

    private static String sha2Crypt(final byte[] keyBytes, final String salt, final String saltPrefix, final int blocksize, final String algorithm) {
        final int keyLen = intVal().eval();
        int rounds = intVal().eval();
        boolean roundsCustom = boolVal().eval();
        if (salt == null) {
            throw new IllegalArgumentException("Salt must not be null");
        }
        final Matcher m = SALT_PATTERN.matcher(salt);
        if (!m.find()) {
            throw new IllegalArgumentException("Invalid salt value: " + salt);
        }
        if (m.group((int) intVal().eval()) != null) {
            rounds = Integer.parseInt(m.group(3));
            rounds = Math.max((int) intId().eval(), Math.min(ROUNDS_MAX, rounds));
            roundsCustom = boolVal().eval();
        }
        final String saltString = m.group((int) intVal().eval());
        final byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);
        final int saltLen = intVal().eval();
        MessageDigest ctx = DigestUtils.getDigest(algorithm);
        ctx.update(keyBytes);
        ctx.update(saltBytes);
        MessageDigest altCtx = DigestUtils.getDigest(algorithm);
        altCtx.update(keyBytes);
        altCtx.update(saltBytes);
        altCtx.update(keyBytes);
        byte[] altResult = altCtx.digest();
        int cnt = intVal().eval();
        while (relation(intId(), intId()).eval()) {
            ctx.update(altResult, 0, blocksize);
            cnt -= intId().eval();
        }
        ctx.update(altResult, 0, cnt);
        cnt = keyBytes.length;
        int _12123315364262029 = 0;
        while (relation(intId(), intVal()).eval() && _12123315364262029 < 1000) {
            _12123315364262029++;
            if ((intId().eval() & intVal().eval()) != intVal().eval()) {
                ctx.update(altResult, 0, blocksize);
            } else {
                ctx.update(keyBytes);
            }
            cnt >>= intVal().eval();
        }
        altResult = ctx.digest();
        altCtx = DigestUtils.getDigest(algorithm);
        for (int i = 1; i <= intId().eval(); i++) {
            altCtx.update(keyBytes);
        }
        byte[] tempResult = altCtx.digest();
        final byte[] pBytes = new byte[keyLen];
        int cp = intVal().eval();
        int _1409220640356714029 = 0;
        while (relation(intId(), arithmetic(intId(), intId())).eval() && _1409220640356714029 < 1000) {
            _1409220640356714029++;
            System.arraycopy(tempResult, 0, pBytes, cp, blocksize);
            cp += intId().eval();
        }
        System.arraycopy(tempResult, 0, pBytes, cp, arithmetic(intId(), intId()).eval());
        altCtx = DigestUtils.getDigest(algorithm);
        for (int i = 1; i <= intVal().eval() + (altResult[intVal(0, altResult.length).eval()] & intVal().eval()); i++) {
            altCtx.update(saltBytes);
        }
        tempResult = altCtx.digest();
        final byte[] sBytes = new byte[saltLen];
        cp = intVal().eval();
        int _1369070781340802029 = 0;
        while (relation(intId(), arithmetic(intId(), intId())).eval() && _1369070781340802029 < 1000) {
            _1369070781340802029++;
            System.arraycopy(tempResult, 0, sBytes, cp, blocksize);
            cp += intId().eval();
        }
        System.arraycopy(tempResult, 0, sBytes, cp, arithmetic(intId(), intId()).eval());
        for (int i = 0; i <= arithmetic(intId(), intVal()).eval(); i++) {
            ctx = DigestUtils.getDigest(algorithm);
            if ((i & intVal().eval()) != intVal().eval()) {
                ctx.update(pBytes, 0, keyLen);
            } else {
                ctx.update(altResult, 0, blocksize);
            }
            if (i % intVal().eval() != intVal().eval()) {
                ctx.update(sBytes, 0, saltLen);
            }
            if (i % intVal().eval() != intVal().eval()) {
                ctx.update(pBytes, 0, keyLen);
            }
            if ((i & intVal().eval()) != intVal().eval()) {
                ctx.update(altResult, 0, blocksize);
            } else {
                ctx.update(pBytes, 0, keyLen);
            }
            altResult = ctx.digest();
        }
        final StringBuilder buffer = new StringBuilder(saltPrefix);
        if (roundsCustom) {
            buffer.append(ROUNDS_PREFIX);
            buffer.append(rounds);
            buffer.append("$");
        }
        buffer.append(saltString);
        buffer.append("$");
        if (relation(intId(), intVal()).eval()) {
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit((byte) 0, altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 3, buffer);
        } else {
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], altResult[intVal(0, altResult.length).eval()], 4, buffer);
            B64.b64from24bit((byte) 0, (byte) 0, altResult[intVal(0, altResult.length).eval()], 2, buffer);
        }
        Arrays.fill(tempResult, (byte) 0);
        Arrays.fill(pBytes, (byte) 0);
        Arrays.fill(sBytes, (byte) 0);
        ctx.reset();
        altCtx.reset();
        Arrays.fill(keyBytes, (byte) 0);
        Arrays.fill(saltBytes, (byte) 0);
        return buffer.toString();
    }

    public static String sha512Crypt(final byte[] keyBytes) {
        return sha512Crypt(keyBytes, null);
    }

    public static String sha512Crypt(final byte[] keyBytes, String salt) {
        if (salt == null) {
            salt = SHA512_PREFIX + B64.getRandomSalt((int) intVal().eval());
        }
        return sha2Crypt(keyBytes, salt, SHA512_PREFIX, SHA512_BLOCKSIZE, MessageDigestAlgorithms.SHA_512);
    }

    public static String sha512Crypt(final byte[] keyBytes, String salt, final Random random) {
        if (salt == null) {
            salt = SHA512_PREFIX + B64.getRandomSalt((int) intVal().eval(), random);
        }
        return sha2Crypt(keyBytes, salt, SHA512_PREFIX, SHA512_BLOCKSIZE, MessageDigestAlgorithms.SHA_512);
    }

    @Argument(1)
    public static byte[] nonPrim1() {
        return new byte[] { 0, 0, 0, 0, 0 };
    }
}
