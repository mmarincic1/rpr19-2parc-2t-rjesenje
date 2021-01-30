package ba.unsa.etf.rpr;


import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerTest {
    Stage theStage;
    GradController ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        // Pored konstruktora sa dva parametra dodajemo i konstruktor sa tri parametra
        // kako bismo mogli proslijediti spisak gradova
        ctrl = new GradController(null, dao.drzave(), dao.gradovi());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();
        stage.toFront();
        theStage = stage;
    }

    @Test
    public void testPoljaPostoje(FxRobot robot) {
        ListView lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        assertNotNull(lv);
        ChoiceBox cb = robot.lookup("#choiceGrad").queryAs(ChoiceBox.class);
        assertNotNull(cb);
        Button btn = robot.lookup("#btnDodaj").queryAs(Button.class);
        assertNotNull(btn);
    }

    @Test
    public void testDodajPobratima(FxRobot robot) {
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodaj");
        ListView<Grad> lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        ObservableList<Grad> items = lv.getItems();
        assertEquals(1, items.size());
        assertEquals("Pariz", items.get(0).getNaziv());
    }

    @Test
    public void testDodajPobratimaGrad(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        // Grad pobratim: London
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodaj");

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad sarajevo = ctrl.getGrad();
        assertEquals(1, sarajevo.getPobratimi().size());
        assertEquals("London", sarajevo.getPobratimi().get(0).getNaziv());
    }

    @Test
    public void testDvaIstaPobratima(FxRobot robot) {
        robot.clickOn("#choiceGrad");
        robot.clickOn("Grac");
        robot.clickOn("#btnDodaj");

        robot.clickOn("#choiceGrad");
        robot.clickOn("Mančester");
        // Ponovo biramo Grac
        robot.clickOn("#choiceGrad");
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#btnDodaj");

        ListView<Grad> lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        ObservableList<Grad> items = lv.getItems();
        assertEquals(1, items.size());
        assertEquals("Grac", items.get(0).getNaziv());
    }

    @Test
    public void testPunoPobratima(FxRobot robot) {
        // Upisujemo grad
        robot.clickOn("#fieldNaziv");
        robot.write("Rejkjavik");
        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");
        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Grac");
        robot.clickOn("#btnDodaj");

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertEquals(4, rejkjavik.getPobratimi().size());
        assertEquals("London", rejkjavik.getPobratimi().get(0).getNaziv());
        assertEquals("Beč", rejkjavik.getPobratimi().get(1).getNaziv());
        assertEquals("Pariz", rejkjavik.getPobratimi().get(2).getNaziv());
        assertEquals("Grac", rejkjavik.getPobratimi().get(3).getNaziv());
    }
}