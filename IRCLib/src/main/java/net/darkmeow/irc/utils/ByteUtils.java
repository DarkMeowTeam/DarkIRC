package net.darkmeow.irc.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.function.Consumer;

public class ByteUtils {

    public static byte[] concatByteArrays(byte[]... arrays) {
        return buildByteArray(buffer -> {
            for (byte[] arr : arrays) {
                buffer.writeBytes(arr);
            }
        });
    }

    public static byte[] buildByteArray(Consumer<ByteBuf> writer) {
        ByteBuf buffer = Unpooled.buffer();
        writer.accept(buffer);
        byte[] result = new byte[buffer.readableBytes()];
        buffer.readBytes(result);
        return result;
    }

}
