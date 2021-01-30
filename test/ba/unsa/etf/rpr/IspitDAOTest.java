package ba.unsa.etf.rpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IspitDAOTest {

    @BeforeEach
    void regenerisiBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
    }

    @Test
    void testPobratimiPrazni() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad graz = dao.nadjiGrad("Grac");
        Grad pariz = dao.nadjiGrad("Pariz");
        assertEquals(0, graz.getPobratimi().size());
        assertEquals(0, pariz.getPobratimi().size());
    }

    @Test
    void testDodajPobratima() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad graz = dao.nadjiGrad("Grac");
        Grad pariz = dao.nadjiGrad("Pariz");
        pariz.getPobratimi().add(graz);
        dao.izmijeniGrad(pariz);

        Grad pariz2 = dao.nadjiGrad("Pariz");
        assertEquals(1, pariz2.getPobratimi().size());
        assertEquals("Grac", pariz2.getPobratimi().get(0).getNaziv());
    }

    @Test
    void testUzajamno() {
        // Ako je London pobratim Beča, onda je Beč pobratim Londona
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        london.getPobratimi().add(bech);
        dao.izmijeniGrad(london);

        Grad london2 = dao.nadjiGrad("London");
        Grad bech2 = dao.nadjiGrad("Beč");
        assertEquals(1, london2.getPobratimi().size());
        assertEquals("Beč", london2.getPobratimi().get(0).getNaziv());
        assertEquals(1, bech2.getPobratimi().size());
        assertEquals("London", bech2.getPobratimi().get(0).getNaziv());
    }

    @Test
    void testMnogoPobratima() {
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        Grad pariz = dao.nadjiGrad("Pariz");
        Grad manchester = dao.nadjiGrad("Mančester");
        pariz.getPobratimi().add(bech);
        pariz.getPobratimi().add(london);
        pariz.getPobratimi().add(manchester);
        dao.izmijeniGrad(pariz);

        Grad pariz2 = dao.nadjiGrad("Pariz");
        Grad bech2 = dao.nadjiGrad("Beč");
        assertEquals(3, pariz2.getPobratimi().size());
        assertEquals(1, bech2.getPobratimi().size());
        assertEquals("Pariz", bech2.getPobratimi().get(0).getNaziv());
    }

    @Test
    void testDodavanjeGrada() {
        GeografijaDAO dao = GeografijaDAO.getInstance();

        // Najprije dodajemo drzavu
        Grad london = dao.nadjiGrad("London");
        Drzava bih = new Drzava(0, "Bosna i Hercegovina", london);
        dao.dodajDrzavu(bih);

        Drzava bih2 = dao.nadjiDrzavu("Bosna i Hercegovina");
        Grad sarajevo = new Grad(0, "Sarajevo", 500000, bih);
        sarajevo.getPobratimi().add(london);
        Grad pariz = dao.nadjiGrad("Pariz");
        sarajevo.getPobratimi().add(pariz);
        dao.dodajGrad(sarajevo);

        Grad sarajevo2 = dao.nadjiGrad("Sarajevo");
        Grad london2 = dao.nadjiGrad("London");
        Grad pariz2 = dao.nadjiGrad("Pariz");
        assertEquals(2, sarajevo2.getPobratimi().size());

        boolean ok = false;
        Grad g1 = sarajevo2.getPobratimi().get(0);
        Grad g2 = sarajevo2.getPobratimi().get(1);
        if (g1.getNaziv().equals("London") && g2.getNaziv().equals("Pariz")) ok = true;
        if (g2.getNaziv().equals("London") && g1.getNaziv().equals("Pariz")) ok = true;
        assertTrue(ok);

        assertEquals(1, london2.getPobratimi().size());
        assertEquals("Sarajevo", london2.getPobratimi().get(0).getNaziv());
        assertEquals(1, pariz2.getPobratimi().size());
        assertEquals("Sarajevo", pariz2.getPobratimi().get(0).getNaziv());
    }

    @Test
    void testBrisanjePobratima() {
        // Najprije London i Beč su pobratimi (ovo je testirano u testUzajamno)
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad london = dao.nadjiGrad("London");
        Grad bech = dao.nadjiGrad("Beč");
        london.getPobratimi().add(bech);
        dao.izmijeniGrad(london);

        // Sada Beč više neće biti pobratim Londona
        Grad bech3 = dao.nadjiGrad("Beč");
        bech3.setPobratimi(new ArrayList<>()); // prazna lista
        dao.izmijeniGrad(bech3);

        // Ni jedan ni drugi nemaju pobratima
        Grad london2 = dao.nadjiGrad("London");
        Grad bech2 = dao.nadjiGrad("Beč");
        assertEquals(0, london2.getPobratimi().size());
        assertEquals(0, bech2.getPobratimi().size());
    }


    @Test
    void testBrisanjeGrada() {
        // Najprije Pariz i Grac su pobratimi (ovo je testirano u testDodajPobratima)
        GeografijaDAO dao = GeografijaDAO.getInstance();
        Grad graz = dao.nadjiGrad("Grac");
        Grad pariz = dao.nadjiGrad("Pariz");
        pariz.getPobratimi().add(graz);
        dao.izmijeniGrad(pariz);

        // Beč i Manchester su pobratimi
        Grad bech = dao.nadjiGrad("Beč");
        Grad manchester = dao.nadjiGrad("Mančester");
        bech.getPobratimi().add(manchester);
        dao.izmijeniGrad(bech);

        // Brišemo Grac i Beč
        Grad graz2 = dao.nadjiGrad("Grac");
        dao.obrisiGrad(graz2);
        Grad bech2 = dao.nadjiGrad("Beč");
        dao.obrisiGrad(bech2);

        // Pariz i Manchester više nemaju pobratima
        Grad pariz2 = dao.nadjiGrad("Pariz");
        Grad manchester2 = dao.nadjiGrad("Mančester");
        assertEquals(0, pariz2.getPobratimi().size());
        assertEquals(0, manchester2.getPobratimi().size());
    }
}
