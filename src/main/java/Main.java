public class Main {

    public static void main(String[] args) {

        PageActions pageActions = new PageActions();

        for (int i=0; i < 6; i++) {
            pageActions.enterWord();
        }

        pageActions.wait(20000);

        pageActions.closeDown();

    }
}