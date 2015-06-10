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
import java.util.concurrent.TimeUnit;

public class core_transport_set_timeouts implements Command {

    public int execute(Meterpreter meterpreter, TLVPacket request, TLVPacket response) throws Exception {
        Integer retryTotal = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_RETRY_TOTAL, null);
        Integer retryWait = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_RETRY_WAIT, null);
        Integer sessionExpiry = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_SESSION_EXP, null);
        Integer commsTimeout = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_COMM_TIMEOUT, null);
        if (retryTotal != null) {
//            Payload.retry_total = TimeUnit.SECONDS.toMillis(retryTotal.intValue());
        }
        if (retryWait != null) {
//            Payload.retry_wait = TimeUnit.SECONDS.toMillis(retryWait.intValue());
        }
        return ERROR_SUCCESS;
    }
}
