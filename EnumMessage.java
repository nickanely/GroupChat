package fop.w11pchat;

import java.io.Serializable;

enum MessType {
    MESSAGE, WHOIS, LOGOUT, PINGU
}
public class EnumMessage  implements Serializable {
    private MessType type;
    private String msg;

    public EnumMessage(MessType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public MessType getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
