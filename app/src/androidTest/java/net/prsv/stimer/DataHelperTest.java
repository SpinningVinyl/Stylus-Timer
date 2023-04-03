package net.prsv.stimer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DataHelperTest {

    private Stylus stylus1, stylus2, stylus3;
    private StylusProfile profile1, profile2, profile3;

    private DataHelper helper;

    @Before
    public void setUp() {
        helper = new DataHelper(DataHelper.TEST_DB_FILE);
        profile1 = new StylusProfile(14, "Spherical", 300);
        profile2 = new StylusProfile(15, "Elliptical", 450);
        profile3 = new StylusProfile(16, "Shibata", 500);
        helper.insertProfile(profile1);
        helper.insertProfile(profile2);
        helper.insertProfile(profile3);
        stylus1 = new Stylus(0, "Test stylus 1", 15, 1.75, 0);
        stylus2 = new Stylus(0, "Test stylus 2", 14, 2.0, 1500);
        stylus3 = new Stylus(0, "Test stylus 3", 16, 1.25, 0);
        stylus1.setId(helper.insertStylus(stylus1));
        stylus2.setId(helper.insertStylus(stylus2));
        stylus3.setId(helper.insertStylus(stylus3));
    }

    @After
    public void tearDown() {
        helper.deleteAllStyli();
        helper.deleteAllProfiles();
        helper.close();

        profile1 = null;
        profile2 = null;
        profile3 = null;

        stylus1 = null;
        stylus2 = null;
        stylus3 = null;
    }

    @Test
    public void getStylus() {
        assertEquals(stylus1, helper.getStylus(stylus1.getId()));
        assertEquals(stylus2, helper.getStylus(stylus2.getId()));
        assertEquals(stylus3, helper.getStylus(stylus3.getId()));
    }

    @Test
    public void getAllStyli() {
        ArrayList<Stylus> styli = helper.getAllStyli();
        assertEquals(styli.size(), 3);
        assertTrue(styli.contains(stylus1));
        assertTrue(styli.contains(stylus2));
        assertTrue(styli.contains(stylus3));
    }

    @Test
    public void getAllProfiles() {
        ArrayList<StylusProfile> profiles = helper.getAllProfiles();
        assertEquals(profiles.size(), 3);
        assertTrue(profiles.contains(profile1));
        assertTrue(profiles.contains(profile2));
        assertTrue(profiles.contains(profile3));
    }

    @Test
    public void getProfile() {
        assertEquals(profile1, helper.getProfile(profile1.getId()));
        assertEquals(profile2, helper.getProfile(profile2.getId()));
        assertEquals(profile3, helper.getProfile(profile3.getId()));
    }

    @Test
    public void updateStylus() {
        stylus1.setHours(500);
        stylus1.setCustomThreshold(999);
        stylus1.setTrackingForce(5.55);
        stylus1.setName("New name for stylus 1");
        helper.updateStylus(stylus1);
        assertEquals(stylus1, helper.getStylus(stylus1.getId()));
    }

    @Test
    public void deleteStylus() {
        helper.deleteStylus(stylus1.getId());
        helper.deleteStylus(stylus2.getId());
        helper.deleteStylus(stylus3.getId());
        assertNull(helper.getStylus(stylus1.getId()));
        assertNull(helper.getStylus(stylus2.getId()));
        assertNull(helper.getStylus(stylus3.getId()));
    }

    @Test
    public void deleteProfile() {
        helper.deleteProfile(profile1.getId());
        helper.deleteProfile(profile2.getId());
        helper.deleteProfile(profile3.getId());
        assertNull(helper.getProfile(profile1.getId()));
        assertNull(helper.getProfile(profile2.getId()));
        assertNull(helper.getProfile(profile3.getId()));
    }

}