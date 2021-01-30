package ba.unsa.etf.rpr;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Testovi vezani za editovanje postojećeg grada
@ExtendWith(ApplicationExtension.class)
public class IspitGradControllerSet {
    Stage theStage;
    GradController ctrl;
    ArrayList<Grad> gradovi;

    @Start
    public void start(Stage stage) throws Exception {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();

        GeografijaDAO dao = GeografijaDAO.getInstance();
        Drzava francuska = dao.nadjiDrzavu("Francuska");
        Grad bech = dao.nadjiGrad("Beč");

        Grad rejkjavik = new Grad(12345, "Rejkjavik", 350000, francuska);
        rejkjavik.getPobratimi().add(bech);

        gradovi = dao.gradovi();
        // Dodaćemo isti grad u gradove zbog testPobratimSamogSebe
        gradovi.add(rejkjavik);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"));
        ctrl = new GradController(rejkjavik, dao.drzave(), gradovi);
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
    public void testIspravneVrijednosti(FxRobot robot) {
        ListView<Grad> lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        assertNotNull(lv);
        ObservableList<Grad> items = lv.getItems();
        assertEquals(1, items.size());
        assertEquals("Beč", items.get(0).getNaziv());
    }

    @Test
    public void testDodavanjePobratima(FxRobot robot) {
        // Grad pobratim: London
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodaj");

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertEquals(2, rejkjavik.getPobratimi().size());
        assertEquals("London", rejkjavik.getPobratimi().get(1).getNaziv());
    }

    @Test
    public void testDvaIstaPobratima(FxRobot robot) {
        // Beč je već dodat u konstruktoru, zato se ovdje neće desiti ništa
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#choiceGrad");
        // Biramo Beč
        robot.press(KeyCode.DOWN).release(KeyCode.DOWN);
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
        robot.clickOn("#btnDodaj");

        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodaj");

        ListView<Grad> lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        ObservableList<Grad> items = lv.getItems();
        // Beč dodat u konstruktoru i novi Pariz
        assertEquals(2, items.size());
        assertEquals("Beč", items.get(0).getNaziv());
        assertEquals("Pariz", items.get(1).getNaziv());

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertEquals(2, rejkjavik.getPobratimi().size());
        assertEquals("Beč", rejkjavik.getPobratimi().get(0).getNaziv());
        assertEquals("Pariz", rejkjavik.getPobratimi().get(1).getNaziv());
    }

    @Test
    public void testPobratimSamogSebe(FxRobot robot) {
        // Grad ne može biti pobratim samog sebe, zato se ovdje neće desiti ništa
        // robot.clickOn("Rejkjavik") će kliknuti na fieldNaziv pa to izbjegavamo
        robot.clickOn("#fieldNaziv");
        robot.write("X");

        robot.clickOn("#choiceGrad");
        robot.clickOn("Rejkjavik");
        robot.clickOn("#btnDodaj");

        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);

        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodaj");

        ListView<Grad> lv = robot.lookup("#listViewPobratimi").queryAs(ListView.class);
        ObservableList<Grad> items = lv.getItems();
        // Beč dodat u konstruktoru i novi London
        assertEquals(2, items.size());
        assertEquals("Beč", items.get(0).getNaziv());
        assertEquals("London", items.get(1).getNaziv());

        // Klik na Ok
        robot.clickOn("#btnOk");

        Grad rejkjavik = ctrl.getGrad();
        assertEquals(2, rejkjavik.getPobratimi().size());
        assertEquals("Beč", rejkjavik.getPobratimi().get(0).getNaziv());
        assertEquals("London", rejkjavik.getPobratimi().get(1).getNaziv());
    }
}
