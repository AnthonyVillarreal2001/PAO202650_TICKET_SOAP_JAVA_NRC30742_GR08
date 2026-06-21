package ec.edu.monster.cliweb.soap;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SoapHelper {

    private static final String SERVER_URL = "http://localhost:8080/WS_EurekaBank_Server/";

    public static String callSoap(String serviceName, String action, String envelope) throws Exception {
        URL url = new URL(SERVER_URL + serviceName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        connection.setRequestProperty("SOAPAction", action);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = envelope.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300) ? connection.getInputStream() : connection.getErrorStream();

        if (is == null) {
            return "";
        }

        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
