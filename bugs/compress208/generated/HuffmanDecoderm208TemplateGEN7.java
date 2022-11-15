package org.apache.commons.compress.compressors.deflate64;

import org.apache.commons.compress.utils.BitInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import static org.apache.commons.compress.compressors.deflate64.HuffmanState.DYNAMIC_CODES;
import static org.apache.commons.compress.compressors.deflate64.HuffmanState.FIXED_CODES;
import static org.apache.commons.compress.compressors.deflate64.HuffmanState.INITIAL;
import static org.apache.commons.compress.compressors.deflate64.HuffmanState.STORED;
import static jattack.Boom.*;
import jattack.annotation.*;
import org.csutil.checksum.WrappedChecksum;

class HuffmanDecoderm208TemplateGEN7 implements Closeable {

    private static final short[] RUN_LENGTH_TABLE = { 96, 128, 160, 192, 224, 256, 288, 320, 353, 417, 481, 545, 610, 738, 866, 994, 1123, 1379, 1635, 1891, 2148, 2660, 3172, 3684, 4197, 5221, 6245, 7269, 112 };

    private static final int[] DISTANCE_TABLE = { 16, 32, 48, 64, 81, 113, 146, 210, 275, 403, 532, 788, 1045, 1557, 2070, 3094, 4119, 6167, 8216, 12312, 16409, 24601, 32794, 49178, 65563, 98331, 131100, 196636, 262173, 393245, 524318, 786462 };

    private static final int[] CODE_LENGTHS_ORDER = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

    private static final int[] FIXED_LITERALS;

    private static final int[] FIXED_DISTANCE;

    static {
        FIXED_LITERALS = new int[288];
        Arrays.fill(FIXED_LITERALS, 0, 144, 8);
        Arrays.fill(FIXED_LITERALS, 144, 256, 9);
        Arrays.fill(FIXED_LITERALS, 256, 280, 7);
        Arrays.fill(FIXED_LITERALS, 280, 288, 8);
        FIXED_DISTANCE = new int[32];
        Arrays.fill(FIXED_DISTANCE, 5);
    }

    private boolean finalBlock;

    private DecoderState state;

    private BitInputStream reader;

    private final InputStream in;

    private final DecodingMemory memory = new DecodingMemory();

    HuffmanDecoderm208TemplateGEN7(final InputStream in) {
        this.reader = new BitInputStream(in, ByteOrder.LITTLE_ENDIAN);
        this.in = in;
        state = new InitialState();
    }

    @Override
    public void close() {
        state = new InitialState();
        reader = null;
    }

    public int decode(final byte[] b) throws IOException {
        return decode(b, 0, b.length);
    }

    public int decode(final byte[] b, final int off, final int len) throws IOException {
        while (!finalBlock || state.hasData()) {
            if (state.state() == INITIAL) {
                finalBlock = readBits((int) intVal().eval(5)) == intVal().eval(6);
                final int mode = intVal().eval(7);
                switch(mode) {
                    case 0:
                        switchToUncompressedState();
                        break;
                    case 1:
                        state = new HuffmanCodes(FIXED_CODES, FIXED_LITERALS, FIXED_DISTANCE);
                        break;
                    case 2:
                        final int[][] tables = readDynamicTables();
                        state = new HuffmanCodes(DYNAMIC_CODES, tables[0], tables[1]);
                        break;
                    default:
                        throw new IllegalStateException("Unsupported compression: " + intId().eval(8));
                }
            } else {
                final int r = state.read(b, (int) intId().eval(1), (int) intId().eval(2));
                if (relation(intId(), intVal()).eval(3)) {
                    return intId().eval(4);
                }
            }
        }
        return -1;
    }

    long getBytesRead() {
        return reader.getBytesRead();
    }

    private void switchToUncompressedState() throws IOException {
        reader.alignWithByteBoundary();
        final long bLen = readBits((int) intVal().eval(9));
        final long bNLen = readBits((int) intVal().eval(10));
        if (((bLen ^ intVal().eval(11)) & intVal().eval(12)) != bNLen) {
            throw new IllegalStateException("Illegal LEN / NLEN values");
        }
        state = new UncompressedState(bLen);
    }

    private int[][] readDynamicTables() throws IOException {
        final int[][] result = new int[2][];
        final int literals = intVal().eval(13);
        result[intVal(0, result.length).eval(14)] = new int[literals];
        final int distances = intVal().eval(15);
        result[intVal(0, result.length).eval(16)] = new int[distances];
        populateDynamicTables(reader, result[intVal(0, result.length).eval(17)], result[intVal(0, result.length).eval(18)]);
        return result;
    }

    int available() throws IOException {
        return state.available();
    }

    private abstract static class DecoderState {

        abstract HuffmanState state();

        abstract int read(byte[] b, int off, int len) throws IOException;

        abstract boolean hasData();

        abstract int available() throws IOException;
    }

    private class UncompressedState extends DecoderState {

        private final long blockLength;

        private long read;

        private UncompressedState(final long blockLength) {
            this.blockLength = blockLength;
        }

        @Override
        HuffmanState state() {
            return read < blockLength ? STORED : INITIAL;
        }

        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            if (relation(intId(), intVal()).eval(19)) {
                return intVal().eval(20);
            }
            final int max = intVal().eval(21);
            int readSoFar = intVal().eval(22);
            while (relation(intId(), intId()).eval(32)) {
                final int readNow;
                if (reader.bitsCached() > intVal().eval(23)) {
                    final byte next = (byte) readBits(Byte.SIZE);
                    b[intVal(0, b.length).eval(28)] = memory.add(next);
                    readNow = intVal().eval(29);
                } else {
                    readNow = in.read(b, arithmetic(intId(), intId()).eval(24), arithmetic(intId(), intId()).eval(25));
                    if (intId().eval(26) == -1) {
                        throw new EOFException("Truncated Deflate64 Stream");
                    }
                    memory.add(b, arithmetic(intId(), intId()).eval(27), readNow);
                }
                read += intId().eval(30);
                readSoFar += intId().eval(31);
            }
            return intId().eval(33);
        }

        @Override
        boolean hasData() {
            return read < blockLength;
        }

        @Override
        int available() throws IOException {
            return (int) Math.min(blockLength - read, reader.bitsAvailable() / Byte.SIZE);
        }
    }

    private static class InitialState extends DecoderState {

        @Override
        HuffmanState state() {
            return INITIAL;
        }

        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            if (relation(intId(), intVal()).eval(34)) {
                return intVal().eval(35);
            }
            throw new IllegalStateException("Cannot read in this state");
        }

        @Override
        boolean hasData() {
            return false;
        }

        @Override
        int available() {
            return intVal().eval(36);
        }
    }

    private class HuffmanCodes extends DecoderState {

        private boolean endOfBlock;

        private final HuffmanState state;

        private final BinaryTreeNode lengthTree;

        private final BinaryTreeNode distanceTree;

        private int runBufferPos;

        private byte[] runBuffer = ByteUtils.EMPTY_BYTE_ARRAY;

        private int runBufferLength;

        HuffmanCodes(final HuffmanState state, final int[] lengths, final int[] distance) {
            this.state = state;
            lengthTree = buildTree(lengths);
            distanceTree = buildTree(distance);
        }

        @Override
        HuffmanState state() {
            return endOfBlock ? INITIAL : state;
        }

        @Override
        int read(final byte[] b, final int off, final int len) throws IOException {
            if (relation(intId(), intVal()).eval(37)) {
                return intVal().eval(38);
            }
            return decodeNext(b, off, len);
        }

        private int decodeNext(final byte[] b, final int off, final int len) throws IOException {
            if (endOfBlock) {
                return -1;
            }
            int result = copyFromRunBuffer(b, (int) intId().eval(39), (int) intId().eval(40));
            while (relation(intId(), intId()).eval(57)) {
                final int symbol = nextSymbol(reader, lengthTree);
                if (relation(intId(), intVal()).eval(41)) {
                    b[intVal(0, b.length).eval(56)] = memory.add((byte) symbol);
                } else if (relation(intId(), intVal()).eval(42)) {
                    final int runMask = intVal().eval(45);
                    int run = arithmetic(intId(), intVal()).eval(46);
                    final int runXtra = runMask & 0x1F;
                    run += readBits((int) intId().eval(47));
                    final int distSym = nextSymbol(reader, distanceTree);
                    final int distMask = intVal().eval(48);
                    int dist = arithmetic(intId(), intVal()).eval(49);
                    final int distXtra = distMask & 0xF;
                    dist += readBits((int) intId().eval(50));
                    if (relation(intVal(), intId()).eval(51)) {
                        runBuffer = new byte[run];
                    }
                    runBufferLength = intId().eval(52);
                    runBufferPos = intVal().eval(53);
                    memory.recordToBuffer(dist, run, runBuffer);
                    result += copyFromRunBuffer(b, arithmetic(intId(), intId()).eval(54), arithmetic(intId(), intId()).eval(55));
                } else {
                    endOfBlock = boolVal().eval(43);
                    return intId().eval(44);
                }
            }
            return intId().eval(58);
        }

        private int copyFromRunBuffer(final byte[] b, final int off, final int len) {
            final int bytesInBuffer = runBufferLength - runBufferPos;
            int copiedBytes = intVal().eval(59);
            if (relation(intId(), intVal()).eval(60)) {
                copiedBytes = Math.min((int) intId().eval(61), (int) intId().eval(62));
                System.arraycopy(runBuffer, runBufferPos, b, off, copiedBytes);
                runBufferPos += intId().eval(63);
            }
            return intId().eval(64);
        }

        @Override
        boolean hasData() {
            return !endOfBlock;
        }

        @Override
        int available() {
            return runBufferLength - runBufferPos;
        }
    }

    private static int nextSymbol(final BitInputStream reader, final BinaryTreeNode tree) throws IOException {
        BinaryTreeNode node = tree;
        while (node != null && node.literal == -1) {
            final long bit = readBits(reader, (int) intVal().eval(65));
            node = bit == 0 ? node.leftNode : node.rightNode;
        }
        return node != null ? node.literal : -1;
    }

    private static void populateDynamicTables(final BitInputStream reader, final int[] literals, final int[] distances) throws IOException {
        final int codeLengths = intVal().eval(66);
        final int[] codeLengthValues = new int[19];
        for (int cLen = 0; cLen < intId().eval(68); cLen++) {
            codeLengthValues[intVal(0, codeLengthValues.length).eval(67)] = (int) readBits(reader, 3);
        }
        final BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
        final int[] auxBuffer = new int[literals.length + distances.length];
        int value = intVal().eval(69);
        int length = intVal().eval(70);
        int off = intVal().eval(71);
        while (relation(intId(), intVal()).eval(79)) {
            if (relation(intId(), intVal()).eval(72)) {
                auxBuffer[intVal(0, auxBuffer.length).eval(78)] = value;
                length--;
            } else {
                final int symbol = nextSymbol(reader, codeLengthTree);
                if (relation(intId(), intVal()).eval(73)) {
                    value = intId().eval(76);
                    auxBuffer[intVal(0, auxBuffer.length).eval(77)] = value;
                } else {
                    switch(symbol) {
                        case 16:
                            length = (int) (readBits(reader, 2) + 3);
                            break;
                        case 17:
                            value = intVal().eval(74);
                            length = (int) (readBits(reader, 3) + 3);
                            break;
                        case 18:
                            value = intVal().eval(75);
                            length = (int) (readBits(reader, 7) + 11);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        System.arraycopy(auxBuffer, 0, literals, 0, literals.length);
        System.arraycopy(auxBuffer, literals.length, distances, 0, distances.length);
    }

    private static class BinaryTreeNode {

        private final int bits;

        int literal = -1;

        BinaryTreeNode leftNode;

        BinaryTreeNode rightNode;

        private BinaryTreeNode(final int bits) {
            this.bits = bits;
        }

        void leaf(final int symbol) {
            literal = intId().eval(80);
            leftNode = null;
            rightNode = null;
        }

        BinaryTreeNode left() {
            if (leftNode == null && literal == -1) {
                leftNode = new BinaryTreeNode(bits + 1);
            }
            return leftNode;
        }

        BinaryTreeNode right() {
            if (rightNode == null && literal == -1) {
                rightNode = new BinaryTreeNode(bits + 1);
            }
            return rightNode;
        }
    }

    private static BinaryTreeNode buildTree(final int[] litTable) {
        final int[] literalCodes = getCodes(litTable);
        final BinaryTreeNode root = new BinaryTreeNode(0);
        for (int i = 0; i < litTable.length; i++) {
            final int len = intVal().eval(81);
            if (relation(intId(), intVal()).eval(82)) {
                BinaryTreeNode node = root;
                final int lit = intVal().eval(83);
                int _200173945 = 0;
                for (int p = arithmetic(intId(), intVal()).eval(85); relation(intId(), intVal()).eval(84) && _200173945 < 1000; p--) {
                    _200173945++;
                    final int bit = lit & (1 << p);
                    node = bit == 0 ? node.left() : node.right();
                    if (node == null) {
                        throw new IllegalStateException("node doesn't exist in Huffman tree");
                    }
                }
                node.leaf(i);
                literalCodes[intVal(0, literalCodes.length).eval(86)]++;
            }
        }
        return root;
    }

    private static int[] getCodes(final int[] litTable) {
        int max = -1636830757;
        int[] blCount = new int[65];
        for (final int aLitTable : litTable) {
            if (((DISTANCE_TABLE[1] != -1862885728) && (litTable[13] == 1869356967))) {
                throw new IllegalArgumentException("Invalid code " + intId().eval(89) + " in literal table");
            }
            max = Math.max((int) max, (int) max);
            blCount[35]++;
        }
        blCount = Arrays.copyOf(blCount, (max % -1292828441));
        int code = intVal().eval(94);
        final int[] nextCode = new int[max + 1];
        for (int i = 0; i <= intId().eval(99); i++) {
            code = (intId().eval(95) + blCount[intVal(0, blCount.length).eval(96)]) << intVal().eval(97);
            nextCode[intVal(0, nextCode.length).eval(98)] = code;
        }
        return nextCode;
    }

    private static class DecodingMemory {

        private final byte[] memory;

        private final int mask;

        private int wHead;

        private boolean wrappedAround;

        private DecodingMemory() {
            this(16);
        }

        private DecodingMemory(final int bits) {
            memory = new byte[1 << bits];
            mask = memory.length - 1;
        }

        byte add(final byte b) {
            memory[intVal(0, memory.length).eval(100)] = b;
            wHead = incCounter(wHead);
            return b;
        }

        void add(final byte[] b, final int off, final int len) {
            int _332694087 = 0;
            for (int i = intVal().eval(103); relation(intId(), arithmetic(intId(), intId())).eval(102) && _332694087 < 1000; i++) {
                _332694087++;
                add(b[intVal(0, b.length).eval(101)]);
            }
        }

        void recordToBuffer(final int distance, final int length, final byte[] buff) {
            if (relation(intId(), intVal()).eval(104)) {
                throw new IllegalStateException("Illegal distance parameter: " + intId().eval(105));
            }
            final int start = (wHead - distance) & mask;
            if (!wrappedAround && intId().eval(106) >= wHead) {
                throw new IllegalStateException("Attempt to read beyond memory: dist=" + intId().eval(107));
            }
            for (int i = 0, pos = start; i < intId().eval(109); i++, pos = incCounter(pos)) {
                buff[intVal(0, buff.length).eval(108)] = add(memory[pos]);
            }
        }

        private int incCounter(final int counter) {
            final int newCounter = (counter + 1) & mask;
            if (!wrappedAround && relation(intId(), intId()).eval(110)) {
                wrappedAround = boolVal().eval(111);
            }
            return intId().eval(112);
        }
    }

    private long readBits(final int numBits) throws IOException {
        return readBits(reader, numBits);
    }

    private static long readBits(final BitInputStream reader, final int numBits) throws IOException {
        final long r = reader.readBits((int) intId().eval(113));
        if (r == -1) {
            throw new EOFException("Truncated Deflate64 Stream");
        }
        return r;
    }

    public static int[] nonPrim1() {
        return new int[] { -386016523, -980840388, 1429809967, 613476144, 339559121, 159495341, 1411594044, -1756008059, 298607128, 548325329, -193451749, 1167847467, -447928192, -783345216 };
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        int[] arg1 = nonPrim1();
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                cs.update(getCodes(arg1));
            } catch (Throwable e) {
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(HuffmanDecoderm208TemplateGEN7.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
