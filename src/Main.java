import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("=== DEBUG: Application starting ===");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BankingSystem.fxml"));
            
            // Load the FXML first
            Parent root = loader.load();
            
            // Get the controller and check if it exists
            MainController controller = loader.getController();
            System.out.println("=== DEBUG: Controller loaded: " + (controller != null));
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Banking System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("=== DEBUG: Application started ===");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== DEBUG: Main method starting ===");
        launch(args);
    }
}