package bms.protocol;

import bms.domain.*;
import bms.services.IObserver;
import bms.services.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;

public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private final IService server;
    private final Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcReflectionWorker(IService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private static final Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    //  private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public void bugListChanged(Collection<Bug> bugs) throws Exception {
        try {
            sendResponse(new Response.Builder().type(ResponseType.BUGS_LIST_CHANGED).data(bugs).build());
        } catch (IOException e) {
            throw new Exception("sending error: " + e);
        }
    }

    private Response handleADD_BUG(Request request) {
        System.out.println("Add bug request ..." + request.type());
        System.out.println("Received request: " + request.data().toString());
        Bug bug = (Bug) request.data();
        try {
            server.addBug(bug);
            return okResponse;
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleSOLVE_BUG(Request request) {
        System.out.println("Solve bug request ..." + request.type());
        System.out.println("Received request: " + request.data().toString());
        Bug bug = (Bug) request.data();
        try {
            server.solveBug(bug);
            return okResponse;
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleREMOVE_BUG(Request request) {
        System.out.println("Remove bug request ..." + request.type());
        System.out.println("Received request: " + request.data().toString());
        Bug bug = (Bug) request.data();
        try {
            server.removeBug(bug);
            return okResponse;
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_UNSOLVED_BUGS(Request request) {
        System.out.println("Get unsolved bugs request ... " + request.type());
        try {
            return new Response.Builder().type(ResponseType.GET_UNSOLVED_BUGS).data(server.getUnsolvedBugs()).build();
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_UNSOLVED_BUGS_BY_TESTER(Request request) {
        System.out.println("Get unsolved bugs request by user ..." + request.type());
        try {
            return new Response.Builder().type(ResponseType.GET_UNSOLVED_BUGS_BY_TESTER).
                    data(server.getUnsolvedBugsByTester((int) request.data())).build();
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_BUGS(Request request) {
        System.out.println("Get gus request ... " + request.type());
        try {
            return new Response.Builder().type(ResponseType.GET_BUGS).data(server.getAllBugs()).build();
        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGIN_DEVELOPER(Request request) {
        System.out.println("Login request ..." + request.type());
        System.out.println("Received request: " + request.data().toString());
        Developer developer = (Developer) request.data();
        try {
            Developer found = server.login(developer, this);
            if (found == null) {
                return new Response.Builder().type(ResponseType.INEXISTENT).build();
            }
            return new Response.Builder().type(ResponseType.OK).data(found).build();
        } catch (Exception e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGIN_TESTER(Request request) {
        System.out.println("Login request ..." + request.type());
        System.out.println("Received request: " + request.data().toString());
        Tester tester = (Tester) request.data();
        try {
            Tester found = server.login(tester, this);
            if (found == null) {
                return new Response.Builder().type(ResponseType.INEXISTENT).build();
            }
            return new Response.Builder().type(ResponseType.OK).data(found).build();
        } catch (Exception e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT_DEVELOPER(Request request) {
        System.out.println("Logout request...");
        Developer developer = (Developer) request.data();
        try {
            server.logout(developer, this);
            connected = false;
            return okResponse;

        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT_TESTER(Request request) {
        System.out.println("Logout request...");
        Tester tester = (Tester) request.data();
        try {
            server.logout(tester, this);
            connected = false;
            return okResponse;

        } catch (Exception e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        output.writeObject(response);
        output.flush();
    }
}
