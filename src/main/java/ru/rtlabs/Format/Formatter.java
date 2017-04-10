package ru.rtlabs.Format;


public class Formatter {

public static String format(String clinic, String action){

    return "               <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                                 xmlns:tfom=\"https://bur.cdmarf.ru/dss/services/tfoms\">\n" +
            "                  <soapenv:Header/>\n" +
            "                  <soapenv:Body>\n" +
            "                     <tfom:"+ action+">\n" +
            "                        <tfom:clinicID>"+ clinic +"</tfom:clinicID>\n" +
            "                     </tfom:"+ action+">\n" +
            "                  </soapenv:Body>\n" +
            "               </soapenv:Envelope>";
}

}


