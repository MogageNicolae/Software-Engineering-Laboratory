package bms.server;

import bms.persistence.developers.DeveloperRepository;
import bms.persistence.developers.IDeveloperRepository;
import bms.persistence.testers.ITesterRepository;
import bms.persistence.testers.TesterRepository;
import bms.server.implementation.ServiceImpl;
import bms.services.IService;
import bms.utils.AbstractServer;
import bms.utils.RpcConcurrentServer;
import bms.utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private final static int defaultPort = 55555;

    public static void main(String[] args) {

        IDeveloperRepository developerRepository = new DeveloperRepository();
        ITesterRepository testerRepository = new TesterRepository();

        IService service = new ServiceImpl(testerRepository, developerRepository);

        AbstractServer server = new RpcConcurrentServer(defaultPort, service);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }

}
