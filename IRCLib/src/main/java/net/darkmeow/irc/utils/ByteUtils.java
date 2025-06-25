package net.darkmeow.irc.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteUtils {

    public static byte[] concatByteArrays(byte[]... arrays) {
        ByteBuf buffer = Unpooled.buffer();
        for (byte[] arr : arrays) {
            buffer.writeBytes(arr);
        }
        byte[] result = new byte[buffer.readableBytes()];
        buffer.readBytes(result);
        return result;
    }

}
