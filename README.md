# RestApplication
The Rest application library allows you to run a Rest application from anywhere. It's lightweight and fast. The library is made to expose objects through HTTP by responding with JSON format.

## How to use it

```java
@Application
public class MyApp {

    @Mapping(path = "/root")
    public Result rest(
            @Param(name = "param1", required = true) String param1,
            @Param(name = "param2") int param2){
        Result r = new Result();
        r.setName(param1);
        r.setId(param2);
        return r;
    }

    public static void main(String[] args) {
        RestApplication app = new RestApplication(MyApp.class);
        app.setPort(8888);
        app.run();
    }
}
```
* @Application : enable the class to expose Rest calls [Required]
* @Mapping: expose this method to the rest api, with the specified path [Required]
* @Param: parameter for the url