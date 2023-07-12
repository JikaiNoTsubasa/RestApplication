package fr.triedge.rest.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RestApplication {
    private Class<?> mainApplication;
    private List<Method> methods;
    private int port = 8888;
    private int threadPool = 10;

    public RestApplication(Class<?> mainApp){
        this.mainApplication = mainApp;
    }

    public void run(){
        checkApplicationAnnotation(mainApplication);
        methods = getAnnotatedMethods(mainApplication);
        System.out.println("Registered "+methods.size()+" methods");
        try{
            HttpServer http = HttpServer.create(new InetSocketAddress("localhost", getPort()), 0);
            System.out.println("Server created on port: "+getPort());
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(getThreadPool());
            System.out.println("Number of threads: "+getThreadPool());

            for (Method m : methods){
                Mapping annon = m.getAnnotation(Mapping.class);
                String path = annon.path();
                if (!path.startsWith("/"))
                    path = "/"+path;
                http.createContext(path, new RestMethodHandler(m, mainApplication));
            }
            http.setExecutor(threadPoolExecutor);
            http.start();
            System.out.println("Server started");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void checkApplicationAnnotation(Class<?> cl){
        if (Objects.isNull(cl)){
            throw new RuntimeException("Provided application class is null");
        }
        if (!cl.isAnnotationPresent(Application.class)){
            throw new RuntimeException("Provided class("+cl.getSimpleName()+") isn't annotated with @Application");
        }
    }

    private List<Method> getAnnotatedMethods(Class<?> cl){
        List<Method> mets = new ArrayList<>();
        for (Method m : cl.getDeclaredMethods()){
            if (m.isAnnotationPresent(Mapping.class)){
                mets.add(m);
            }
        }
        return mets;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(int threadPool) {
        this.threadPool = threadPool;
    }
}
