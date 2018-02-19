package org.neo4j.graphalgo.core.utils.paged;

public final class DeltaEncoding {

    public static long vSize(long value) {
        if (value < 1L << 7) return 1L;
        if (value < 1L << 14) return 2L;
        if (value < 1L << 21) return 3L;
        if (value < 1L << 28) return 4L;
        if (value < 1L << 35) return 5L;
        if (value < 1L << 42) return 6L;
        if (value < 1L << 49) return 7L;
        if (value < 1L << 56) return 8L;
        return 9L;
    }

    public static int encodeInt(int value, byte[] array, int offset) {
        array[offset++] = (byte) (value >>> 24);
        array[offset++] = (byte) (value >>> 16);
        array[offset++] = (byte) (value >>> 8);
        array[offset++] = (byte) (value);
        return offset;
    }

    public static int encodeVLong(long value, byte[] array, int offset) {
        long i = value;
        while ((i & ~0x7FL) != 0L) {
            array[offset++] = (byte) ((i & 0x7FL) | 0x80L);
            i >>>= 7L;
        }
        array[offset++] = (byte) i;
        return offset;
    }

    public static int encodeVLongs(long[] values, int srcLength, byte[] array, int offset) {
        long i;
        int srcOffset = 0;
        while (srcOffset < srcLength) {
            i = values[srcOffset++];
            while ((i & ~0x7FL) != 0L) {
                array[offset++] = (byte) ((i & 0x7FL) | 0x80L);
                i >>>= 7L;
            }
            array[offset++] = (byte) i;
        }
        return offset;
    }

    public static int singedVSize(long value) {
        return (int) vSize((value >> 63) ^ (value << 1));
    }

    public static int encodeSignedVLong(long value, byte[] array, int offset) {
        long i = (value >> 63) ^ (value << 1);
        while ((i & ~0x7FL) != 0L) {
            array[offset++] = (byte) ((i & 0x7FL) | 0x80L);
            i >>>= 7L;
        }
        array[offset++] = (byte) i;
        return offset;
    }

    public static int decodeSignedVLong(byte[] array, int srcOffset, long[] into, int dstOffset) {
        byte b = array[srcOffset++];
        long i = (long) ((int) b & 0x7F);
        for (int shift = 7; ((int) b & 0x80) != 0; shift += 7) {
            b = array[srcOffset++];
            i |= ((long) b & 0x7FL) << shift;
        }
        into[dstOffset] = ((i >>> 1) ^ -(i & 1));
        return srcOffset;
    }
}
