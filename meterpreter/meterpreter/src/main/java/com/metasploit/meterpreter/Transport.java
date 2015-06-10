package com.metasploit.meterpreter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Transport {

    private List transportList = new LinkedList();

    public static class TransportObject {
        public String url;
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
//        tcp://192.168.43.203:4444
//        http://test.com/4444
    }

    public void changeTransport(TLVPacket request) {
        TransportObject transportObject = new TransportObject();
//        transportObject.url =
        transportList.add(transportObject);
    }

    public void addPayloadTransport(String[] parameters) {
        for (int i=0;i<parameters.length;i++) {
            String parameter = parameters[i];
            System.err.println("para" + i + "=" + parameter);
            if (parameter.startsWith("tcp://") || parameter.startsWith("http://") || parameter.startsWith("https://")) {
                TransportObject transportObject = new TransportObject();
                transportObject.url = parameter;
                transportList.add(transportObject);
            }
        }
    }

}
