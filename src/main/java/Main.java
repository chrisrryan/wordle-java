public class Main {

    public static void main(String[] args) {

        PageActions pageActions = new PageActions();

        boolean done = false;
        while (!done) {
            done = pageActions.enterWord();
        }

        pageActions.wait(300_000);

        pageActions.closeDown();

    }
}