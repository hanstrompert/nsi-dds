/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.nsi.dds.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.es.nsi.dds.config.http.HttpConfig;
import org.glassfish.jersey.client.ClientConfig;

/**
 *
 * @author hacksaw
 */
public class Main {
    private final static HttpConfig testServer = new HttpConfig("localhost", "9800", "net.es.nsi.dds.client");

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = RestClient.configureClient();
        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webGet = client.target("http://localhost:8401/dds");
        Response response = webGet.request(MediaType.APPLICATION_JSON).get();

        System.out.println("Get result " + response.getStatus());

        // Configure the local test client callback server.
        TestServer.INSTANCE.start(testServer);
    }
}
