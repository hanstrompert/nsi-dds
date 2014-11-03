package net.es.nsi.dds.client;


import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.es.nsi.dds.config.http.HttpConfig;
import net.es.nsi.dds.api.jaxb.NotificationListType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import net.es.nsi.dds.jersey.JsonMoxyConfigurationContextResolver;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public enum TestServer {
    INSTANCE;

    private static HttpServer server = null;
    private static ConcurrentLinkedQueue<NotificationListType> notificationQueue = new ConcurrentLinkedQueue<>();

    public boolean pushDdsNotification(NotificationListType notify) {
        return notificationQueue.add(notify);
    }

    public NotificationListType popDdsNotification() {
        return notificationQueue.remove();
    }

    public NotificationListType peekDdsNotification() {
        return notificationQueue.peek();
    }

    public NotificationListType pollDdsNotification() {
        return notificationQueue.poll();
    }

    public void start(HttpConfig config) throws IllegalStateException {
        synchronized(this) {
            if (server == null) {
                server = GrizzlyHttpServerFactory.createHttpServer(URI.create(config.getUrl()), getConfig(config.getPackageName()));
            }
            else {
                System.err.println("start: server is already started.");
            }
        }
    }

    public void shutdown() throws IllegalStateException {
        System.out.println("TestServer: shutting down.");

        synchronized(this) {
            if (server != null) {
                server.shutdownNow();
                server = null;
                System.out.println("TestServer: shut down complete.");
            }
            else {
                System.out.println("TestServer: shut down failed.");
            }
        }
    }

    public boolean isStarted() {
        synchronized(this) {
            if (server != null) {
                return server.isStarted();
            }
            else {
                return false;
            }
        }
    }

    public ResourceConfig getConfig(String packageName) throws IllegalStateException {

        return new ResourceConfig()
                .packages(packageName)
                .register(DdsNotificationCallback.class) // Remove this if packages gets fixed.
                .register(new MoxyXmlFeature())
                .register(new MoxyJsonFeature())
                .register(new LoggingFilter(java.util.logging.Logger.getGlobal(), true))
                .registerInstances(new JsonMoxyConfigurationContextResolver());
    }
}