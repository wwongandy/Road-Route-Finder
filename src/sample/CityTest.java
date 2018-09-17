package sample;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Test for City Class
 *
 * @author Daemon-Macklin
 *
 */
public class CityTest {

    private City city1;
    private City cityInvalid;

    /**
     * Set up test fixtures
     *
     * Called before every test method
     */
    @Before
    public void setUp() {
        city1 = new City("City", "C001", "Waterford");
        cityInvalid = new City("NotACity", "C002THISPARTISINVALID", "This String is used to test name sizes..EVERYTHING HERE SHOULD BE GONE");
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
        assertNotNull(city1); //will test these in testGetters()
        assertEquals("C002", cityInvalid.getId());
        assertEquals("Unspecified", cityInvalid.getType());
    }

    /**
     * Test all getters using valid data
     */
    @Test
    public void testGetters() {
        assertEquals("C001", city1.getId());
        assertEquals("City", city1.getType());
        assertEquals("Waterford", city1.getName());
    }

    /**
     * Test all setters for City
     */
    @Test
    public void testSetters() {
        city1.setId("C003"); //Valid change
        assertEquals("C003", city1.getId());
        city1.setId("C00333333");
        assertEquals("C003", city1.getId());
    }

}