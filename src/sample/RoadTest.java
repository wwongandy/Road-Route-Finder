package sample;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Test for Road Class
 *
 * @author Daemon-Macklin
 *
 */

public class RoadTest {

    private Road road1;
    private Road roadInvalid;


    /**
     * Set up test fixtures
     *
     * Called before every test method
     */
    @Before
    public void setUp() {
        road1 = new Road("M", "R001", 98);
        roadInvalid = new Road("NotARoad", "R002THISPARTISINVALID", 999999999);
    }

    /**
     * Teardown test fixtures
     *
     * Called after each test method
     */
    @After
    public void tearDown() {

    }

    /**
     * Test the contsructor
     */
    @Test
    public void testConstructor() {
        assertNotNull(road1); //will test these in testGetters()
        assertEquals("R002", roadInvalid.getId());
        assertEquals("Unspecified", roadInvalid.getType());
    }

    /**
     * Test all getters using valid data
     */
    @Test
    public void testGetters() {
        assertEquals("R001", road1.getId());
        assertEquals("M", road1.getType());
        assertEquals(98, road1.getDistance());
    }

    /**
     * Test all setters for Road
     */
    @Test
    public void testSetters() {
        road1.setId("R003"); //Valid change
        assertEquals("R003", road1.getId());
        road1.setId("R00333333");
        assertEquals("R003", road1.getId());
    }

}