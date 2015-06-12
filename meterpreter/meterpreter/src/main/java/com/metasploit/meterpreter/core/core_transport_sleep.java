package com.metasploit.meterpreter.core;

import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.TLVType;
import com.metasploit.meterpreter.Utils;
import com.metasploit.meterpreter.command.Command;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class core_transport_sleep implements Command {

    public int execute(Meterpreter meterpreter, TLVPacket request, TLVPacket response) throws Exception {
        meterpreter.getTransport().transport_next_wait = request.getIntValue(TLVType.TLV_TYPE_TRANS_COMM_TIMEOUT);
        return ERROR_SHUTDOWN;
    }
}
