package ec.edu.monster.controller;

import ec.edu.monster.util.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class SoapClient {

    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parse("text/xml; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * Realiza una llamada SOAP al servicio web de conversión de unidades
     * @param methodName Nombre del método SOAP a invocar
     * @param paramName Nombre del parámetro
     * @param value Valor a convertir
     * @return Resultado de la conversión
     * @throws Exception Si hay algún error en la comunicación
     */
    public static float callConversion(String methodName, String paramName, float value) throws Exception {
        // Construir el envelope SOAP manualmente
        String soapEnvelope = buildSoapEnvelope(methodName, paramName, value);
        
        // Crear el request body
        RequestBody body = RequestBody.create(soapEnvelope, SOAP_MEDIA_TYPE);
        
        // Construir el request HTTP
        // Nota: El WSDL especifica soapAction="" (vacío), no se incluye namespace
        Request request = new Request.Builder()
                .url(Constants.URL)
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("SOAPAction", "\"\"")  // SOAPAction vacío según el WSDL
                .post(body)
                .build();
        
        // Ejecutar la petición
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Error en la respuesta del servidor: " + response.code());
            }
            
            String responseBody = response.body().string();
            return parseResponse(responseBody, methodName);
        }
    }

    /**
     * Construye el envelope SOAP para la petición según el formato document/literal del WSDL
     * El formato debe coincidir con el que espera el servicio JAX-WS
     */
    private static String buildSoapEnvelope(String methodName, String paramName, float value) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<S:Header/>" +
                "<S:Body>" +
                "<ns2:" + methodName + " xmlns:ns2=\"" + Constants.NAMESPACE + "\">" +
                "<" + paramName + ">" + value + "</" + paramName + ">" +
                "</ns2:" + methodName + ">" +
                "</S:Body>" +
                "</S:Envelope>";
    }

    /**
     * Extrae el resultado numérico de la respuesta SOAP
     * El servicio JAX-WS devuelve el resultado en un tag <return>
     */
    private static float parseResponse(String responseBody, String methodName) throws Exception {
        try {
            // El servicio JAX-WS devuelve la respuesta en formato:
            // <methodNameResponse><return>valor</return></methodNameResponse>
            String resultTag = "return";
            
            // Buscar el valor entre las etiquetas <return>
            int startIndex = responseBody.indexOf("<" + resultTag + ">");
            int endIndex = responseBody.indexOf("</" + resultTag + ">");
            
            // Si no encuentra <return>, intentar con otras variantes comunes
            if (startIndex == -1 || endIndex == -1) {
                // Probar con ns2:return o similar
                resultTag = ":return";
                startIndex = responseBody.indexOf("<" + resultTag + ">");
                if (startIndex != -1) {
                    endIndex = responseBody.indexOf("</" + resultTag + ">");
                }
            }
            
            // Si aún no encuentra, probar con methodNameResult
            if (startIndex == -1 || endIndex == -1) {
                resultTag = methodName + "Result";
                startIndex = responseBody.indexOf("<" + resultTag + ">");
                endIndex = responseBody.indexOf("</" + resultTag + ">");
            }
            
            if (startIndex != -1 && endIndex != -1) {
                // Extraer solo el nombre del tag sin el prefijo del namespace
                String openingTag = responseBody.substring(startIndex, responseBody.indexOf(">", startIndex) + 1);
                int valueStart = startIndex + openingTag.length();
                String value = responseBody.substring(valueStart, endIndex).trim();
                return Float.parseFloat(value);
            }
            
            throw new Exception("No se pudo parsear la respuesta. Response: " + responseBody);
        } catch (NumberFormatException e) {
            throw new Exception("Error al convertir el resultado a número: " + e.getMessage());
        }
    }
}