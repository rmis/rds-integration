package ru.rtlabs.Service;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import ru.rtlabs.Format.Formatter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

public class Service {
    private static final Logger LOG = Logger.getLogger(Service.class);
    private String ur;
    private String queue;
    public void postSend(String clinicId, String soapAction, Channel channel) throws IOException {
        String content = Formatter.format(clinicId, soapAction);
        System.out.println(content);
           URL url = new URL(ur);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("SOAPAction", soapAction);
            conn.setRequestProperty("Host", "127.0.0.1:8090");
            conn.setRequestProperty("User-Agent", "HttpClient/0.0.0.1 (java 1.8) home");
            conn.setDoOutput(true);
            OutputStream reqStream = conn.getOutputStream();
            reqStream.write(content.getBytes());
            LOG.info("\nPOST\n" +
                    ur+"\n" +
                    "Content-Type: " + "text/xml;charset=UTF-8" + "\n" +
               "SOAPAction: " + soapAction + "\n" +
               "Host: " + "127.0.0.1:8090" + "\n" +
               "User-Agent: " + "Apache-HttpClient/4.1.1 (java 1.5)" + "\n" +
                    "Payload: \n" + content);
            String res = null;
            InputStreamReader isr = null;
           if(conn.getResponseCode() == 500){
                isr = new InputStreamReader(conn.getErrorStream(), "UTF-8");
                LOG.warn("Response code:  " + conn.getResponseCode() + " from " + ur);
           }else if ("gzip".equals(conn.getContentEncoding())){
                isr = new InputStreamReader(new GZIPInputStream(conn.getInputStream()));
            }else {
                isr = new InputStreamReader(conn.getInputStream());
            }
            BufferedReader bfr = new BufferedReader(isr);
            StringBuilder sbf = new StringBuilder();
            int ch = bfr.read();
            while (ch != -1) {
                sbf.append((char) ch);
                ch = bfr.read();
            }
            res = sbf.toString();
            LOG.info("\nResponse code: " + conn.getResponseCode() + "\n" +
                    "Ответ :\n" + res);
        try {
            responseParse(res, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void responseParse(String response, Channel channel) throws Exception {
        Document doc = XmlUtil.fromXML(response);
        SoapParser parser = new SoapParser(doc);
        SoapMessage soap = parser.parse();
        String str = XmlUtil.toXML(soap.getDocument(), false);
        System.out.println(str);
        StringBuilder builder = new StringBuilder();
        String[] array = str.split("\\n");
        ArrayList<String>  d = new ArrayList<>();
        Collections.addAll(d, array);
        for (int i = 3; i < d.size() - 1; i++) {
            if (!d.contains("XMLSchema-instance")){
                builder.append(d.get(i));
            }
        }//http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"/>
        String x = builder.toString().replace(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"", "");
        messageSend(x, channel);
    }
    private void messageSend(String message, Channel channel){
        try {
            if (!message.isEmpty()){
                channel.basicPublish("", queue, null, message.getBytes());
                LOG.info(" [x] Sent '" + message + "'");
            }
        } catch (IOException e) {
            LOG.error("Ошибка коннекта", e);
        }
    }

    public String getUr() {
        return ur;
    }

    public void setUr(String ur) {
        this.ur = ur;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
