package nearbygems;

import com.google.gson.Gson;
import spark.ModelAndView;
import spark.debug.DebugScreen;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Starter {

  private static final Gson gson = new Gson();

  public static String render(Map<String, Object> model, String templatePath) {
    return new MustacheTemplateEngine().render(new ModelAndView(model, templatePath));
  }

  public static void main(String[] args) {

    port(8080);

    staticFiles.location("/public");

    DebugScreen.enableDebugScreen();

    get("/", (req, res) -> "Hello World");
    post("/:name", (req, res) -> {

      var name = req.params("name");
      var mood = gson.fromJson(req.body(), MoodHolder.class);

      var map = new HashMap<String, String>();

      map.put("name", name);
      map.put("device", req.queryParams("device"));
      map.put("user-age", req.queryMap().get("user").get("age").value());
      map.put("user-gender", req.queryMap().get("user").get("gender").value());
      map.put("mood", mood.getMood());

      return map;

    }, new JsonTransformer());

    path("/temp", () -> {

      get("/", (request, response) -> {
        var map = new HashMap<String, Object>();
        map.put("userName", "World");
        return render(map, "index.html");
      });

      get("/everybody", (request, response) -> {
        var map = new HashMap<String, Object>();
        map.put("userName", "everybody!!");
        return render(map, "index.html");
      });

      get("/:name", (request, response) -> {
        var map = new HashMap<String, Object>();
        map.put("userName", request.params("name"));
        return render(map, "index.html");
      });

    });

    after((request, response) -> {
      response.header("Content-Encoding", "gzip");
    });

    notFound((req, res) -> {
      res.type("application/json");
      return gson.toJson(Collections.singletonMap("error", "нет таких"));
    });
  }
}
