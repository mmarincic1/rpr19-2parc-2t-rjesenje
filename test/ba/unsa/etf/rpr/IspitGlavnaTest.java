package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Test dodavanja pobratima kroz glavnu formu
@ExtendWith(ApplicationExtension.class)
public class IspitGlavnaTest {
    Stage theStage;
    GlavnaController ctrl;

    @Start
    public void start (Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"));
        ctrl = new GlavnaController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Gradovi svijeta");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(false);
        stage.show();

        stage.toFront();

        theStage = stage;
    }

    @Test
    public void testDodajGrad(FxRobot robot) {
        ctrl.resetujBazu();

        // Otvaranje forme za dodavanje
        robot.clickOn("#btnDodajGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Ovo moramo jer robot klika po glavnoj formi umjesto po ovoj
        Platform.runLater(() -> theStage.hide());

        // Postoji li fieldNaziv
        robot.clickOn("#fieldNaziv");
        robot.write("Sarajevo");

        robot.clickOn("#fieldBrojStanovnika");
        robot.write("350000");

        robot.clickOn("#choiceDrzava");
        robot.clickOn("Francuska");

        // Gradovi pobratimi: London i Manchester
        robot.clickOn("#choiceGrad");
        robot.clickOn("London");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Mančester");
        robot.clickOn("#btnDodaj");

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Da li je Sarajevo dodano u bazu?
        GeografijaDAO dao = GeografijaDAO.getInstance();
        assertEquals(6, dao.gradovi().size());

        Grad sarajevo = null;
        for(Grad grad : dao.gradovi())
            if (grad.getNaziv().equals("Sarajevo"))
                sarajevo = grad;
        assertNotNull(sarajevo);

        assertEquals(350000, sarajevo.getBrojStanovnika());
        assertEquals("Francuska", sarajevo.getDrzava().getNaziv());
        assertEquals(2, sarajevo.getPobratimi().size());

        int pronadjeni = 0;
        for(Grad grad : sarajevo.getPobratimi()) {
            if (grad.getNaziv().equals("London")) pronadjeni++;
            if (grad.getNaziv().equals("Mančester")) pronadjeni++;
        }
        assertEquals(2, pronadjeni);

        // Ponovo prikazujemo glavnu formu
        Platform.runLater(() -> theStage.show());
    }

    @Test
    public void testIzmijeniGrad(FxRobot robot) {
        // Sličan test samo sa različitim glavnim i najvećim gradom
        ctrl.resetujBazu();
        robot.lookup("#tableViewGradovi").tryQuery().isPresent();

        robot.clickOn("Beč");
        // Otvaranje forme za dodavanje
        robot.clickOn("#btnIzmijeniGrad");

        // Čekamo da dijalog postane vidljiv
        robot.lookup("#fieldNaziv").tryQuery().isPresent();

        // Ovo moramo jer robot klika po glavnoj formi umjesto po ovoj
        Platform.runLater(() -> theStage.hide());


        // Gradovi pobratimi: Pariz i Grac
        robot.clickOn("#choiceGrad");
        robot.clickOn("Pariz");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Grac");
        robot.clickOn("#btnDodaj");

        // Dodajemo i Beč ali to ne bi smjelo uspjeti
        // robot.clickOn("Beč") će kliknuti na fieldNaziv pa to izbjegavamo
        robot.clickOn("#fieldNaziv");
        robot.write("X");
        robot.clickOn("#choiceGrad");
        robot.clickOn("Beč");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#fieldNaziv");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);

        // Klik na dugme Ok
        robot.clickOn("#btnOk");

        // Tražimo Beč u bazi
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad bech = dao.nadjiGrad("Beč");

        assertEquals(2, bech.getPobratimi().size());
        int pronadjeni = 0;
        for(Grad grad : bech.getPobratimi()) {
            if (grad.getNaziv().equals("Pariz")) pronadjeni++;
            if (grad.getNaziv().equals("Grac")) pronadjeni++;
        }
        assertEquals(2, pronadjeni);

        Platform.runLater(() -> theStage.show());
    }


}
