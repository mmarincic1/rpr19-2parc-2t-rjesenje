package ba.unsa.etf.rpr;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IspitGradTest {
    @Test
    void testGrad() {
        Grad paris = new Grad(1, "Paris", 2206488, null);
        Grad marseille = new Grad(2, "Marseille", 3000000, null);
        paris.getPobratimi().add(marseille);

        ArrayList<Grad> pobratimi = paris.getPobratimi();
        assertEquals(1, pobratimi.size());
        assertEquals("Marseille", pobratimi.get(0).getNaziv());
    }

    @Test
    void testGradDefault() {
        Grad paris = new Grad(1, "Paris", 2206488, null);
        ArrayList<Grad> pobratimi = paris.getPobratimi();
        assertEquals(0, pobratimi.size());
    }
}