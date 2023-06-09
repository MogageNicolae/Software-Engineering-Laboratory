package bms.protocol;

import bms.domain.*;
import bms.services.IObserver;
import bms.services.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRpcProxy implements IService {
    private final String host;
    private final int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ClientRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.connection = null;
        qresponses = new LinkedBlockingQueue<>();
    }

    private Object loginAction(Request request, IObserver client) throws Exception {
        initializeConnection();
        sendRequest(request);
        Response response = readResponse();
        switch (response.type()) {
            case OK -> {
                this.client = client;
                return response.data();
            }
            case INEXISTENT -> {
                return null;
            }
            case ERROR -> {
                String error = response.data().toString();
                closeConnection();
                throw new Exception(error);
            }
        }
        return null;
    }

    @Override
    public Tester login(Tester person, IObserver client) throws Exception {
        Request request = new Request.Builder().type(RequestType.LOGIN_TESTER).data(person).build();
        return (Tester) loginAction(request, client);
    }

    @Override
    public Developer login(Developer person, IObserver client) throws Exception {
        Request request = new Request.Builder().type(RequestType.LOGIN_DEVELOPER).data(person).build();
        return (Developer) loginAction(request, client);
    }

    private void logoutAction(Request request) throws Exception {
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void logout(Tester person, IObserver client) throws Exception {
        Request request = new Request.Builder().type(RequestType.LOGOUT_TESTER).data(person).build();
        logoutAction(request);
    }

    @Override
    public void logout(Developer person, IObserver client) throws Exception {
        Request request = new Request.Builder().type(RequestType.LOGOUT_DEVELOPER).data(person).build();
        logoutAction(request);
    }

    @Override
    public void addBug(Bug bug) throws Exception {
        Request request = new Request.Builder().type(RequestType.ADD_BUG).data(bug).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new Exception(error);
        }
    }

    @Override
    public void solveBug(Bug bug) throws Exception {
        Request request = new Request.Builder().type(RequestType.SOLVE_BUG).data(bug).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            System.out.println(error);
        }
    }

    @Override
    public void removeBug(Bug bug) throws Exception {
        Request request = new Request.Builder().type(RequestType.REMOVE_BUG).data(bug).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            System.out.println(error);
        }
    }

    private Collection<Bug> getBugs(Request request) throws Exception {
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new Exception(error);
        }
        return (Collection<Bug>) response.data();
    }

    @Override
    public Collection<Bug> getUnsolvedBugs() throws Exception {
        return getBugs(new Request.Builder().type(RequestType.GET_UNSOLVED_BUGS).build());
    }

    @Override
    public Collection<Bug> getUnsolvedBugsByTester(int id) throws Exception {
        return getBugs(new Request.Builder().type(RequestType.GET_UNSOLVED_BUGS_BY_TESTER).data(id).build());
    }

    @Override
    public Collection<Bug> getAllBugs() throws Exception {
        return getBugs(new Request.Builder().type(RequestType.GET_BUGS).build());
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object " + e);
        }
    }

    private Response readResponse() throws Exception {
        Response response;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            throw new Exception("Error reading object " + e);
        }
        return response;
    }

    private void initializeConnection() {
        if (connection != null) {
            return;
        }
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            connection = null;
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.BUGS_LIST_CHANGED;
    }

    private void handleUpdate(Response response) throws Exception {
        if (response.type() == ResponseType.BUGS_LIST_CHANGED) {
            Collection<Bug> bugs = (Collection<Bug>) response.data();
            client.bugListChanged(bugs);
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
