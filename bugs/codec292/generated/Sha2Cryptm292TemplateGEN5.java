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
import org.csutil.checksum.WrappedChecksum;

public class Sha2Cryptm292TemplateGEN5 {

    private static final int ROUNDS_DEFAULT = 5000;

    private static final int ROUNDS_MAX = 999999999;

    private static final int ROUNDS_MIN = 1000;

    private static final String ROUNDS_PREFIX = "rounds=";

    private static final int SHA256_BLOCKSIZE = 32;

    static final String SHA256_PREFIX = "$5$";

    private static final int SHA512_BLOCKSIZE = 64;

    static final String SHA512_PREFIX = "$6$";

    private static final Pattern SALT_PATTERN = Pattern.compile("^\\$([56])\\$(rounds=(\\d+)\\$)?([\\.\\/a-zA-Z0-9]{1,16}).*");

    public static String sha256Crypt(final byte[] keyBytes) {
        return sha256Crypt(keyBytes, null);
    }

    public static String sha256Crypt(final byte[] keyBytes, String salt) {
        if (salt == null) {
            salt = SHA256_PREFIX + B64.getRandomSalt((int) -1901199686);
        }
        return sha2Crypt(keyBytes, salt, SHA256_PREFIX, SHA256_BLOCKSIZE, MessageDigestAlgorithms.SHA_256);
    }

    public static String sha256Crypt(final byte[] keyBytes, String salt, final Random random) {
        if (salt == null) {
            salt = SHA256_PREFIX + B64.getRandomSalt((int) intVal().eval(2), random);
        }
        return sha2Crypt(keyBytes, salt, SHA256_PREFIX, SHA256_BLOCKSIZE, MessageDigestAlgorithms.SHA_256);
    }

    private static String sha2Crypt(final byte[] keyBytes, final String salt, final String saltPrefix, final int blocksize, final String algorithm) {
        final int keyLen = intVal().eval(3);
        int rounds = intVal().eval(4);
        boolean roundsCustom = boolVal().eval(5);
        if (salt == null) {
            throw new IllegalArgumentException("Salt must not be null");
        }
        final Matcher m = SALT_PATTERN.matcher(salt);
        if (!m.find()) {
            throw new IllegalArgumentException("Invalid salt value: " + salt);
        }
        if (m.group((int) intVal().eval(6)) != null) {
            rounds = Integer.parseInt(m.group(3));
            rounds = Math.max((int) intId().eval(7), Math.min(ROUNDS_MAX, rounds));
            roundsCustom = boolVal().eval(8);
        }
        final String saltString = m.group((int) intVal().eval(9));
        final byte[] saltBytes = saltString.getBytes(StandardCharsets.UTF_8);
        final int saltLen = intVal().eval(10);
        MessageDigest ctx = DigestUtils.getDigest(algorithm);
        ctx.update(keyBytes);
        ctx.update(saltBytes);
        MessageDigest altCtx = DigestUtils.getDigest(algorithm);
        altCtx.update(keyBytes);
        altCtx.update(saltBytes);
        altCtx.update(keyBytes);
        byte[] altResult = altCtx.digest();
        int cnt = intVal().eval(11);
        while (relation(intId(), intId()).eval(13)) {
            ctx.update(altResult, 0, blocksize);
            cnt -= intId().eval(12);
        }
        ctx.update(altResult, 0, cnt);
        cnt = keyBytes.length;
        int _12123315364262029 = 0;
        while (relation(intId(), intVal()).eval(18) && _12123315364262029 < 1000) {
            _12123315364262029++;
            if ((intId().eval(14) & intVal().eval(15)) != intVal().eval(16)) {
                ctx.update(altResult, 0, blocksize);
            } else {
                ctx.update(keyBytes);
            }
            cnt >>= intVal().eval(17);
        }
        altResult = ctx.digest();
        altCtx = DigestUtils.getDigest(algorithm);
        for (int i = 1; i <= intId().eval(19); i++) {
            altCtx.update(keyBytes);
        }
        byte[] tempResult = altCtx.digest();
        final byte[] pBytes = new byte[keyLen];
        int cp = intVal().eval(20);
        int _1409220640356714029 = 0;
        while (relation(intId(), arithmetic(intId(), intId())).eval(22) && _1409220640356714029 < 1000) {
            _1409220640356714029++;
            System.arraycopy(tempResult, 0, pBytes, cp, blocksize);
            cp += intId().eval(21);
        }
        System.arraycopy(tempResult, 0, pBytes, cp, arithmetic(intId(), intId()).eval(23));
        altCtx = DigestUtils.getDigest(algorithm);
        for (int i = 1; i <= intVal().eval(24) + (altResult[intVal(0, altResult.length).eval(25)] & intVal().eval(26)); i++) {
            altCtx.update(saltBytes);
        }
        tempResult = altCtx.digest();
        final byte[] sBytes = new byte[saltLen];
        cp = intVal().eval(27);
        int _1369070781340802029 = 0;
        while (relation(intId(), arithmetic(intId(), intId())).eval(29) && _1369070781340802029 < 1000) {
            _1369070781340802029++;
            System.arraycopy(tempResult, 0, sBytes, cp, blocksize);
            cp += intId().eval(28);
        }
        System.arraycopy(tempResult, 0, sBytes, cp, arithmetic(intId(), intId()).eval(30));
        for (int i = 0; i <= arithmetic(intId(), intVal()).eval(39); i++) {
            ctx = DigestUtils.getDigest(algorithm);
            if ((i & intVal().eval(31)) != intVal().eval(32)) {
                ctx.update(pBytes, 0, keyLen);
            } else {
                ctx.update(altResult, 0, blocksize);
            }
            if (i % intVal().eval(33) != intVal().eval(34)) {
                ctx.update(sBytes, 0, saltLen);
            }
            if (i % intVal().eval(35) != intVal().eval(36)) {
                ctx.update(pBytes, 0, keyLen);
            }
            if ((i & intVal().eval(37)) != intVal().eval(38)) {
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
        if (relation(intId(), intVal()).eval(40)) {
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(105)], altResult[intVal(0, altResult.length).eval(106)], altResult[intVal(0, altResult.length).eval(107)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(108)], altResult[intVal(0, altResult.length).eval(109)], altResult[intVal(0, altResult.length).eval(110)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(111)], altResult[intVal(0, altResult.length).eval(112)], altResult[intVal(0, altResult.length).eval(113)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(114)], altResult[intVal(0, altResult.length).eval(115)], altResult[intVal(0, altResult.length).eval(116)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(117)], altResult[intVal(0, altResult.length).eval(118)], altResult[intVal(0, altResult.length).eval(119)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(120)], altResult[intVal(0, altResult.length).eval(121)], altResult[intVal(0, altResult.length).eval(122)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(123)], altResult[intVal(0, altResult.length).eval(124)], altResult[intVal(0, altResult.length).eval(125)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(126)], altResult[intVal(0, altResult.length).eval(127)], altResult[intVal(0, altResult.length).eval(128)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(129)], altResult[intVal(0, altResult.length).eval(130)], altResult[intVal(0, altResult.length).eval(131)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(132)], altResult[intVal(0, altResult.length).eval(133)], altResult[intVal(0, altResult.length).eval(134)], 4, buffer);
            B64.b64from24bit((byte) 0, altResult[intVal(0, altResult.length).eval(135)], altResult[intVal(0, altResult.length).eval(136)], 3, buffer);
        } else {
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(41)], altResult[intVal(0, altResult.length).eval(42)], altResult[intVal(0, altResult.length).eval(43)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(44)], altResult[intVal(0, altResult.length).eval(45)], altResult[intVal(0, altResult.length).eval(46)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(47)], altResult[intVal(0, altResult.length).eval(48)], altResult[intVal(0, altResult.length).eval(49)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(50)], altResult[intVal(0, altResult.length).eval(51)], altResult[intVal(0, altResult.length).eval(52)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(53)], altResult[intVal(0, altResult.length).eval(54)], altResult[intVal(0, altResult.length).eval(55)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(56)], altResult[intVal(0, altResult.length).eval(57)], altResult[intVal(0, altResult.length).eval(58)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(59)], altResult[intVal(0, altResult.length).eval(60)], altResult[intVal(0, altResult.length).eval(61)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(62)], altResult[intVal(0, altResult.length).eval(63)], altResult[intVal(0, altResult.length).eval(64)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(65)], altResult[intVal(0, altResult.length).eval(66)], altResult[intVal(0, altResult.length).eval(67)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(68)], altResult[intVal(0, altResult.length).eval(69)], altResult[intVal(0, altResult.length).eval(70)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(71)], altResult[intVal(0, altResult.length).eval(72)], altResult[intVal(0, altResult.length).eval(73)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(74)], altResult[intVal(0, altResult.length).eval(75)], altResult[intVal(0, altResult.length).eval(76)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(77)], altResult[intVal(0, altResult.length).eval(78)], altResult[intVal(0, altResult.length).eval(79)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(80)], altResult[intVal(0, altResult.length).eval(81)], altResult[intVal(0, altResult.length).eval(82)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(83)], altResult[intVal(0, altResult.length).eval(84)], altResult[intVal(0, altResult.length).eval(85)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(86)], altResult[intVal(0, altResult.length).eval(87)], altResult[intVal(0, altResult.length).eval(88)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(89)], altResult[intVal(0, altResult.length).eval(90)], altResult[intVal(0, altResult.length).eval(91)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(92)], altResult[intVal(0, altResult.length).eval(93)], altResult[intVal(0, altResult.length).eval(94)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(95)], altResult[intVal(0, altResult.length).eval(96)], altResult[intVal(0, altResult.length).eval(97)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(98)], altResult[intVal(0, altResult.length).eval(99)], altResult[intVal(0, altResult.length).eval(100)], 4, buffer);
            B64.b64from24bit(altResult[intVal(0, altResult.length).eval(101)], altResult[intVal(0, altResult.length).eval(102)], altResult[intVal(0, altResult.length).eval(103)], 4, buffer);
            B64.b64from24bit((byte) 0, (byte) 0, altResult[intVal(0, altResult.length).eval(104)], 2, buffer);
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
            salt = SHA512_PREFIX + B64.getRandomSalt((int) intVal().eval(137));
        }
        return sha2Crypt(keyBytes, salt, SHA512_PREFIX, SHA512_BLOCKSIZE, MessageDigestAlgorithms.SHA_512);
    }

    public static String sha512Crypt(final byte[] keyBytes, String salt, final Random random) {
        if (salt == null) {
            salt = SHA512_PREFIX + B64.getRandomSalt((int) intVal().eval(138), random);
        }
        return sha2Crypt(keyBytes, salt, SHA512_PREFIX, SHA512_BLOCKSIZE, MessageDigestAlgorithms.SHA_512);
    }

    public static byte[] nonPrim1() {
        return new byte[] { 0, 0, 0, 0, 0 };
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                byte[] arg1 = nonPrim1();
                cs.update(sha256Crypt(arg1));
            } catch (Throwable e) {
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(Sha2Cryptm292TemplateGEN5.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
