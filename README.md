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

# Templates

Template allows to render html pages with included parameters.

## How to use templates

Create template as html files placed in the source folder.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
</head>
<body>
  <h1>Welcome ##NAME##</h1>
  <p>This is a test</p>
</body>
</html>
```

Place this file in resources/html/home.html (the resources folder must be set as source folder type). You can chose a different name for the resources folder.

```java
@Mapping(path = "/home")
public Template home(@Param(name = "name", required = true) String name){
    Template tpl = new Template("/html/home.html");
    tpl.setParameter("##NAME##", name);
    return tpl;
}
```