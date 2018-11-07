package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.User;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


public class UserController extends Controller {

    private final UserService userService;
    private final HttpExecutionContext ec;

    @Inject
    public UserController(UserService userService, HttpExecutionContext ec) {
        this.userService = userService;
        this.ec = ec;
    }

    public CompletionStage<Result> getOneUser(Long id) {
        return userService.get(id).thenApplyAsync(user -> {
            return ok(getCustomJson(user));
        }, ec.current());
    }
    public CompletionStage<Result> getAllUsers() {
        return userService.getAll().thenApplyAsync(personStream -> {
            return ok(Json.toJson(personStream.collect(Collectors.toList())));
        }, ec.current());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addUser() {
        final JsonNode jsonRequest = request().body().asJson();
        final User userToAdd = Json.fromJson(jsonRequest, User.class);

        return userService.add(userToAdd).thenApplyAsync(user -> {
            return ok(Json.toJson(user));
        }, ec.current());
    }

    public CompletionStage<Result> changeUser(Long id) {
        final JsonNode jsonRequest = request().body().asJson();
        final User userToChange = Json.fromJson(jsonRequest, User.class);

        // Aus ID array buddies hinzufügen---------------------------------------------------------------------------

        userToChange.setId(id);
        return userService.change(userToChange).thenApplyAsync(user -> {
            return ok(Json.toJson(user));
        }, ec.current());

    }

    public CompletionStage<Result> deleteUser(Long id) {
        return userService.delete(id).thenApplyAsync(removed -> {
            return removed ? ok() : internalServerError();
        }, ec.current());
    }

    private JsonNode getCustomJson(User user){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = Json.toJson(user);
        List<User> buddies = user.getBuddies();
        ArrayList<Long> list = new ArrayList<>();
        for (User u : buddies) {
            list.add(u.getId());
        }
        ArrayNode buddieIds = mapper.valueToTree(list);
        ((ObjectNode) json).putArray("buddies").addAll(buddieIds);
        return json;
    }
}
