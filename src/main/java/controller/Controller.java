package controller;

import model.Model;

/*
* The controller should not manipulate the view directly to maintain separation of concerns
* The view should be able to call the controller to manipulate the model and retrieve data
*/
public class Controller {
    private final Model model;

    public Controller() {
        this.model = new Model();
    }
}
