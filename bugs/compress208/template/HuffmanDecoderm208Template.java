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

class HuffmanDecoderm208Template implements Closeable {

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

    HuffmanDecoderm208Template(final InputStream in) {
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
                finalBlock = readBits((int) intVal().eval()) == intVal().eval();
                final int mode = intVal().eval();
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
                        throw new IllegalStateException("Unsupported compression: " + intId().eval());
                }
            } else {
                final int r = state.read(b, (int) intId().eval(), (int) intId().eval());
                if (relation(intId(), intVal()).eval()) {
                    return intId().eval();
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
        final long bLen = readBits((int) intVal().eval());
        final long bNLen = readBits((int) intVal().eval());
        if (((bLen ^ intVal().eval()) & intVal().eval()) != bNLen) {
            throw new IllegalStateException("Illegal LEN / NLEN values");
        }
        state = new UncompressedState(bLen);
    }

    private int[][] readDynamicTables() throws IOException {
        final int[][] result = new int[2][];
        final int literals = intVal().eval();
        result[intVal(0, result.length).eval()] = new int[literals];
        final int distances = intVal().eval();
        result[intVal(0, result.length).eval()] = new int[distances];
        populateDynamicTables(reader, result[intVal(0, result.length).eval()], result[intVal(0, result.length).eval()]);
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
            if (relation(intId(), intVal()).eval()) {
                return intVal().eval();
            }
            final int max = intVal().eval();
            int readSoFar = intVal().eval();
            while (relation(intId(), intId()).eval()) {
                final int readNow;
                if (reader.bitsCached() > intVal().eval()) {
                    final byte next = (byte) readBits(Byte.SIZE);
                    b[intVal(0, b.length).eval()] = memory.add(next);
                    readNow = intVal().eval();
                } else {
                    readNow = in.read(b, arithmetic(intId(), intId()).eval(), arithmetic(intId(), intId()).eval());
                    if (intId().eval() == -1) {
                        throw new EOFException("Truncated Deflate64 Stream");
                    }
                    memory.add(b, arithmetic(intId(), intId()).eval(), readNow);
                }
                read += intId().eval();
                readSoFar += intId().eval();
            }
            return intId().eval();
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
            if (relation(intId(), intVal()).eval()) {
                return intVal().eval();
            }
            throw new IllegalStateException("Cannot read in this state");
        }

        @Override
        boolean hasData() {
            return false;
        }

        @Override
        int available() {
            return intVal().eval();
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
            if (relation(intId(), intVal()).eval()) {
                return intVal().eval();
            }
            return decodeNext(b, off, len);
        }

        private int decodeNext(final byte[] b, final int off, final int len) throws IOException {
            if (endOfBlock) {
                return -1;
            }
            int result = copyFromRunBuffer(b, (int) intId().eval(), (int) intId().eval());
            while (relation(intId(), intId()).eval()) {
                final int symbol = nextSymbol(reader, lengthTree);
                if (relation(intId(), intVal()).eval()) {
                    b[intVal(0, b.length).eval()] = memory.add((byte) symbol);
                } else if (relation(intId(), intVal()).eval()) {
                    final int runMask = intVal().eval();
                    int run = arithmetic(intId(), intVal()).eval();
                    final int runXtra = runMask & 0x1F;
                    run += readBits((int) intId().eval());
                    final int distSym = nextSymbol(reader, distanceTree);
                    final int distMask = intVal().eval();
                    int dist = arithmetic(intId(), intVal()).eval();
                    final int distXtra = distMask & 0xF;
                    dist += readBits((int) intId().eval());
                    if (relation(intVal(), intId()).eval()) {
                        runBuffer = new byte[run];
                    }
                    runBufferLength = intId().eval();
                    runBufferPos = intVal().eval();
                    memory.recordToBuffer(dist, run, runBuffer);
                    result += copyFromRunBuffer(b, arithmetic(intId(), intId()).eval(), arithmetic(intId(), intId()).eval());
                } else {
                    endOfBlock = boolVal().eval();
                    return intId().eval();
                }
            }
            return intId().eval();
        }

        private int copyFromRunBuffer(final byte[] b, final int off, final int len) {
            final int bytesInBuffer = runBufferLength - runBufferPos;
            int copiedBytes = intVal().eval();
            if (relation(intId(), intVal()).eval()) {
                copiedBytes = Math.min((int) intId().eval(), (int) intId().eval());
                System.arraycopy(runBuffer, runBufferPos, b, off, copiedBytes);
                runBufferPos += intId().eval();
            }
            return intId().eval();
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
            final long bit = readBits(reader, (int) intVal().eval());
            node = bit == 0 ? node.leftNode : node.rightNode;
        }
        return node != null ? node.literal : -1;
    }

    private static void populateDynamicTables(final BitInputStream reader, final int[] literals, final int[] distances) throws IOException {
        final int codeLengths = intVal().eval();
        final int[] codeLengthValues = new int[19];
        for (int cLen = 0; cLen < intId().eval(); cLen++) {
            codeLengthValues[intVal(0, codeLengthValues.length).eval()] = (int) readBits(reader, 3);
        }
        final BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
        final int[] auxBuffer = new int[literals.length + distances.length];
        int value = intVal().eval();
        int length = intVal().eval();
        int off = intVal().eval();
        while (relation(intId(), intVal()).eval()) {
            if (relation(intId(), intVal()).eval()) {
                auxBuffer[intVal(0, auxBuffer.length).eval()] = value;
                length--;
            } else {
                final int symbol = nextSymbol(reader, codeLengthTree);
                if (relation(intId(), intVal()).eval()) {
                    value = intId().eval();
                    auxBuffer[intVal(0, auxBuffer.length).eval()] = value;
                } else {
                    switch(symbol) {
                        case 16:
                            length = (int) (readBits(reader, 2) + 3);
                            break;
                        case 17:
                            value = intVal().eval();
                            length = (int) (readBits(reader, 3) + 3);
                            break;
                        case 18:
                            value = intVal().eval();
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
            literal = intId().eval();
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
            final int len = intVal().eval();
            if (relation(intId(), intVal()).eval()) {
                BinaryTreeNode node = root;
                final int lit = intVal().eval();
                int _200173945 = 0;
                for (int p = arithmetic(intId(), intVal()).eval(); relation(intId(), intVal()).eval() && _200173945 < 1000; p--) {
                    _200173945++;
                    final int bit = lit & (1 << p);
                    node = bit == 0 ? node.left() : node.right();
                    if (node == null) {
                        throw new IllegalStateException("node doesn't exist in Huffman tree");
                    }
                }
                node.leaf(i);
                literalCodes[intVal(0, literalCodes.length).eval()]++;
            }
        }
        return root;
    }

    @jattack.annotation.Entry()
    private static int[] getCodes(final int[] litTable) {
        int max = intVal().eval();
        int[] blCount = new int[65];
        for (final int aLitTable : litTable) {
            if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
                throw new IllegalArgumentException("Invalid code " + intId().eval() + " in literal table");
            }
            max = Math.max((int) intId().eval(), (int) intId().eval());
            blCount[intVal(0, blCount.length).eval()]++;
        }
        blCount = Arrays.copyOf(blCount, arithmetic(intId(), intVal()).eval());
        int code = intVal().eval();
        final int[] nextCode = new int[max + 1];
        for (int i = 0; i <= intId().eval(); i++) {
            code = (intId().eval() + blCount[intVal(0, blCount.length).eval()]) << intVal().eval();
            nextCode[intVal(0, nextCode.length).eval()] = code;
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
            memory[intVal(0, memory.length).eval()] = b;
            wHead = incCounter(wHead);
            return b;
        }

        void add(final byte[] b, final int off, final int len) {
            int _332694087 = 0;
            for (int i = intVal().eval(); relation(intId(), arithmetic(intId(), intId())).eval() && _332694087 < 1000; i++) {
                _332694087++;
                add(b[intVal(0, b.length).eval()]);
            }
        }

        void recordToBuffer(final int distance, final int length, final byte[] buff) {
            if (relation(intId(), intVal()).eval()) {
                throw new IllegalStateException("Illegal distance parameter: " + intId().eval());
            }
            final int start = (wHead - distance) & mask;
            if (!wrappedAround && intId().eval() >= wHead) {
                throw new IllegalStateException("Attempt to read beyond memory: dist=" + intId().eval());
            }
            for (int i = 0, pos = start; i < intId().eval(); i++, pos = incCounter(pos)) {
                buff[intVal(0, buff.length).eval()] = add(memory[pos]);
            }
        }

        private int incCounter(final int counter) {
            final int newCounter = (counter + 1) & mask;
            if (!wrappedAround && relation(intId(), intId()).eval()) {
                wrappedAround = boolVal().eval();
            }
            return intId().eval();
        }
    }

    private long readBits(final int numBits) throws IOException {
        return readBits(reader, numBits);
    }

    private static long readBits(final BitInputStream reader, final int numBits) throws IOException {
        final long r = reader.readBits((int) intId().eval());
        if (r == -1) {
            throw new EOFException("Truncated Deflate64 Stream");
        }
        return r;
    }

    @Argument(1)
    public static int[] nonPrim1() {
        return intArrVal(0, 15).eval();
    }
}
