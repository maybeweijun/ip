package maybeweijun.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        // Make the avatar circular
        applyCircularClip(displayPicture);
    }

    /**
     * Applies a circular clip to the given ImageView so it renders as a circle.
     * The circle auto-adjusts to the ImageView's fit dimensions.
     *
     * @param iv the ImageView to clip
     */
    private void applyCircularClip(ImageView iv) {
        Circle clip = new Circle();
        clip.radiusProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(iv.getFitWidth(), iv.getFitHeight()) / 2.0,
                iv.fitWidthProperty(), iv.fitHeightProperty()
        ));
        clip.centerXProperty().bind(Bindings.createDoubleBinding(
                () -> iv.getFitWidth() / 2.0, iv.fitWidthProperty()
        ));
        clip.centerYProperty().bind(Bindings.createDoubleBinding(
                () -> iv.getFitHeight() / 2.0, iv.fitHeightProperty()
        ));
        iv.setClip(clip);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getDukeDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
