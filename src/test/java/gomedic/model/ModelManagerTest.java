package gomedic.model;

import static gomedic.testutil.TypicalPersons.MAIN_DOCTOR;
import static gomedic.testutil.TypicalPersons.MAIN_PATIENT;
import static gomedic.testutil.TypicalPersons.OTHER_DOCTOR;
import static gomedic.testutil.TypicalPersons.OTHER_PATIENT;
import static gomedic.testutil.TypicalUserProfile.OTHER_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gomedic.commons.core.GuiSettings;
import gomedic.model.commonfield.Id;
import gomedic.model.commonfield.exceptions.MaxAddressBookCapacityReached;
import gomedic.model.person.doctor.Doctor;
import gomedic.model.person.patient.Patient;
import gomedic.model.util.NameContainsKeywordsPredicate;
import gomedic.model.util.SampleDataUtil;
import gomedic.testutil.AddressBookBuilder;
import gomedic.testutil.Assert;
import gomedic.testutil.TypicalActivities;
import gomedic.testutil.TypicalPersons;
import gomedic.testutil.modelbuilder.DoctorBuilder;
import gomedic.testutil.modelbuilder.PatientBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        Assertions.assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookDataFileRootPath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookDataFileRootPath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        Assertions.assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.setAddressBookDataRootFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookDataRootFilePath(path);
        assertEquals(path, modelManager.getAddressBookDataRootFilePath());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder()
                .withActivity(TypicalActivities.MEETING)
                .withActivity(TypicalActivities.PAPER_REVIEW)
                .withDoctor(TypicalPersons.MAIN_DOCTOR)
                .withDoctor(TypicalPersons.OTHER_DOCTOR)
                .withPatient(TypicalPersons.MAIN_PATIENT)
                .withPatient(TypicalPersons.OTHER_PATIENT)
                .build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertEquals(modelManager, modelManagerCopy);

        // same object -> returns true
        assertEquals(modelManager, modelManager);

        // null -> returns false
        assertNotEquals(null, modelManager);

        // different types -> returns false
        assertNotEquals(5, modelManager);

        // different addressBook -> returns false
        assertNotEquals(modelManager, new ModelManager(differentAddressBook, userPrefs));

        // different filteredList -> returns false
        String[] keywords = "Invalid input".split("\\s+");
        modelManager.updateFilteredDoctorList(new NameContainsKeywordsPredicate<>(Arrays.asList(keywords)));
        assertNotEquals(modelManager, new ModelManager(addressBook, userPrefs));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredDoctorList(Model.PREDICATE_SHOW_ALL_ITEMS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookDataFileRootPath(Paths.get("differentFilePath"));
        assertNotEquals(modelManager, new ModelManager(addressBook, differentUserPrefs));
    }

    @Test
    public void hasDoctor_nullDoctor_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.hasDoctor(null));
    }

    @Test
    public void hasDoctor_doctorNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasDoctor(TypicalPersons.MAIN_DOCTOR));
    }

    @Test
    public void hasDoctor_doctorInAddressBook_returnsTrue() {
        modelManager.addDoctor(TypicalPersons.MAIN_DOCTOR);
        assertTrue(modelManager.hasDoctor(TypicalPersons.MAIN_DOCTOR));
    }

    @Test
    void hasNewDoctorId_emptyList_returnsTrue() {
        assertTrue(modelManager.hasNewDoctorId());
    }

    @Test
    void hasNewDoctorId_oneItemInList_returnsTrue() {
        modelManager.addDoctor(MAIN_DOCTOR);
        assertTrue(modelManager.hasNewDoctorId());
    }

    @Test
    void hasNewDoctorId_maxItemInList_returnsFalse() {
        for (int i = 1; i <= Id.MAXIMUM_ASSIGNABLE_IDS; i++) {
            Doctor toAdd = new DoctorBuilder().withId(i).build();
            modelManager.addDoctor(toAdd);
        }
        assertFalse(modelManager.hasNewDoctorId());
    }

    @Test
    void getNewDoctorId_emptyList_returns1() {
        assertEquals(1, modelManager.getNewDoctorId());
    }

    @Test
    void getNewDoctorId_twoItemList_returns3() {
        modelManager.addDoctor(MAIN_DOCTOR);
        modelManager.addDoctor(OTHER_DOCTOR);
        assertEquals(3, modelManager.getNewDoctorId());
    }

    @Test
    void getNewDoctorId_maxListSize_throwsMaxAddressBookCapacityReached() {
        for (int i = 1; i <= Id.MAXIMUM_ASSIGNABLE_IDS; i++) {
            Doctor toAdd = new DoctorBuilder().withId(i).build();
            modelManager.addDoctor(toAdd);
        }
        assertThrows(MaxAddressBookCapacityReached.class, modelManager::getNewDoctorId);
    }

    @Test
    public void getFilteredDoctorList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredDoctorList()
                .remove(0));
    }

    @Test
    public void hasPatient_nullPatient_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.hasPatient(null));
    }

    @Test
    public void hasPatient_patientNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPatient(TypicalPersons.MAIN_PATIENT));
    }

    @Test
    public void hasPatient_patientInAddressBook_returnsTrue() {
        modelManager.addPatient(TypicalPersons.MAIN_PATIENT);
        assertTrue(modelManager.hasPatient(TypicalPersons.MAIN_PATIENT));
    }

    @Test
    void hasNewPatientId_emptyList_returnsTrue() {
        assertTrue(modelManager.hasNewPatientId());
    }

    @Test
    void hasNewPatientId_oneItemInList_returnsTrue() {
        modelManager.addPatient(MAIN_PATIENT);
        assertTrue(modelManager.hasNewPatientId());
    }

    @Test
    void hasNewPatientId_maxItemInList_returnsFalse() {
        for (int i = 1; i <= Id.MAXIMUM_ASSIGNABLE_IDS; i++) {
            Patient toAdd = new PatientBuilder().withId(i).build();
            modelManager.addPatient(toAdd);
        }
        assertFalse(modelManager.hasNewPatientId());
    }

    @Test
    void getNewPatientId_emptyList_returns1() {
        assertEquals(1, modelManager.getNewPatientId());
    }

    @Test
    void getNewPatientId_twoItemList_returns3() {
        modelManager.addPatient(MAIN_PATIENT);
        modelManager.addPatient(OTHER_PATIENT);
        assertEquals(3, modelManager.getNewPatientId());
    }

    @Test
    void getNewPatientId_maxListSize_throwsMaxAddressBookCapacityReached() {
        for (int i = 1; i <= Id.MAXIMUM_ASSIGNABLE_IDS; i++) {
            Patient toAdd = new PatientBuilder().withId(i).build();
            modelManager.addPatient(toAdd);
        }
        assertThrows(MaxAddressBookCapacityReached.class, modelManager::getNewPatientId);
    }

    @Test
    public void getFilteredPatientList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPatientList()
            .remove(0));
    }

    @Test
    public void hasActivity_nullActivity_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> modelManager.hasActivity(null));
    }

    @Test
    public void hasActivity_activityNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasActivity(TypicalActivities.MEETING));
    }

    @Test
    public void hasActivity_activityInAddressBook_returnsTrue() {
        modelManager.addActivity(TypicalActivities.MEETING);
        assertTrue(modelManager.hasActivity(TypicalActivities.MEETING));
    }

    @Test
    public void getFilteredActivityList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredActivityListById()
                .remove(0));
    }

    @Test
    void getModelBeingShown_validInput_testPassed() {
        assertEquals(0, modelManager.getModelBeingShown().getValue());
    }

    @Test
    void setModelBeingShown_validInput_testPassed() {
        modelManager.setModelBeingShown(ModelItem.PATIENT);
        assertEquals(ModelItem.PATIENT.ordinal(), modelManager.getModelBeingShown().getValue());
    }

    @Test
    void setModelBeingShown_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setModelBeingShown(null));
    }

    @Test
    void getObservableUserProfile_defaultValue() {
        assertEquals(SampleDataUtil.getSampleUserProfile(), modelManager.getObservableUserProfile().getValue());
    }

    @Test
    void setUserProfile_validInput_testPassed() {
        modelManager.setUserProfile(OTHER_PROFILE);
        assertEquals(OTHER_PROFILE, modelManager.getObservableUserProfile().getValue());
    }

    @Test
    void setUserProfile_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserProfile(null));
    }
}
