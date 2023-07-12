package fr.triedge.rest.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class RestMethodHandler implements HttpHandler {

    private Method method;
    private Class<?> appClass;

    public RestMethodHandler(Method method, Class<?> appClass){
        this.method = method;
        this.appClass = appClass;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HashMap<String, String> params = Utils.parseParameters(exchange.getRequestURI().getQuery());
        try {
            handleResponse(exchange, params);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleResponse(HttpExchange httpExchange, HashMap<String, String> params) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException, InstantiationException {
        String json = "";
        Object obj = appClass.getDeclaredConstructor().newInstance();
        int paramCount = method.getParameterCount();
        Object result = null;
        if (paramCount == 0){
            result = method.invoke(obj);
        }else{
            Parameter[] ps = method.getParameters();
            Object[] paramResult = new Object[ps.length];
            for (int i = 0; i < ps.length; i++) {
                Parameter p = ps[i];
                if (p!= null && p.isAnnotationPresent(Param.class)){
                    Param ano = p.getAnnotation(Param.class);
                    if (ano.required() && !params.containsKey(ano.name())){
                        throw new RuntimeException("Parameter "+ano.name()+" is required in method "+ method.getName());
                    }
                    Class<?> type = p.getType();
                    type = Utils.toWrapper(type);
                    if (params.containsKey(ano.name())){
                        paramResult[i] = Utils.toObject(type, params.get(ano.name()));
                    }else{
                        paramResult[i] = null;
                    }
                }else{
                    paramResult[i] = null;
                }
            }
            result = method.invoke(obj, paramResult);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        json = gson.toJson(result);

        OutputStream outputStream = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(200, json.length());
        outputStream.write(json.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
