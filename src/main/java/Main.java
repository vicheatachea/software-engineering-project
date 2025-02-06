import view.View;

/*
* Main initialises view which initialises controller which initialises model
* Two-way data binding is not supported due to this schema and should not be needed
* Creating everything here and passing it down does not seem to be possible
*/
public class Main {
    public static void main(String[] args) {
        View.launch(View.class);
    }
}
