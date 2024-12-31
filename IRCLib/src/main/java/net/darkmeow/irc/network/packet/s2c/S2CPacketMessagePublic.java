package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;

public class S2CPacketMessagePublic implements S2CPacket {

    public String name;
    
    public UserInfoData info;

    public String message;

    public S2CPacketMessagePublic(String name, UserInfoData info, String message) {
        this.name = name;
        this.info = info;
        this.message = message;
    }

}
