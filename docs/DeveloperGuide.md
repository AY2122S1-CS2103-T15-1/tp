---
layout: page
title: Developer Guide
---

## **Introduction**

![logo](images/logo.png)

GoMedic is a **cross-platform desktop application written in Java and designed for doctors and medical residents to
manage contacts and patient details**. We aim for GoMedic to be used by someone who can type fast and take advantage of the
optimized features for Command Line Interface.

--------------------------------------------------------------------------------------------------------------------

## **Table of Contents**
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**
* Project bootstrapped from: [SE-EDU Address Book 3](https://se-education.org/addressbook-level3/)
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson)
  , [JUnit5](https://github.com/junit-team/junit5), [iTextPdf](https://itextpdf.com/en)
* The feature `TableView` mainly inspired by [this `TableView` article](http://tutorials.jenkov.com/javafx/tableview.html).

--------------------------------------------------------------------------------------------------------------------

## **About the Diagrams**
<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for of all diagrams in this developer guide 
should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in
the [diagrams](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML
Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit
diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** has two classes
called [`Main`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/Main.java)
and [`MainApp`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/MainApp.java). It
is responsible for,

* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues
the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding
  API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using
the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component
through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the
implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified
in [`Ui.java`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of many Ui components e.g.`CommandBox`, `ResultDisplay`, `SideWindow`
, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures
the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that
are in the `src/main/resources/view` folder. For example, the layout of
the [`MainWindow`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/ui/MainWindow.java)
is specified
in [`MainWindow.fxml`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* listens for changes to `Model` activities, doctors and patients data so that the respective Ui table can be shown and can be updated with the modified data.
* depends on some classes in the `Model` component, as it displays `Doctor`, `Profile`, `Patient` and `Activities` object residing in the `Model`.

The original Figma design for the `UI` component can be found [here](https://www.figma.com/file/zqo6peKfu0Wxeay679eVq9/cs2103t-tp?node-id=0%3A1)

To display the correct table (i.e. `ActivityTable`, `PatientTable`, or `DoctorTable`) or `PatientView` page to be shown in the `MainWindow`. 
The `MainWindow` object also listens to the `ModelBeingShown` stored in the `Model` component. 

The following *Sequence Diagram* illustrates how the `UI` component interacted with other components to show 
the correct model to the user after the user enters `list t/patient` command.  

![UiPatient](images/ui/UpdateModel.png)

<div markdown="block" class="alert alert-info">
:information_source: 
**Note:** GoMedic implements Observer Pattern using `ObservableList` and `ObservableValue` provided by the `JavaFX` framework to update the Ui
</div>

### Logic component

**API** : [`Logic.java`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it uses the `AddressBookParser` class to parse the user command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `AddPatientCommand`
   which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to add a patient).
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

The Sequence Diagram below illustrates the interactions within the `Logic` component for the
`execute("delete t/patient P001")` API call. `Logic` component will parse the command and create the respective `Command`
object. In this case, `DeletePatientCommand` object is created.

![Interactions Inside the Logic Component for the `delete t/patient P001` Command Creation](images/DeletePatientCreation.png)

After the `LogicManager` receives the new `DeletePatientCommand` object,
1. The `DeletePatientCommand` would call the appropriate method from the `Model` to delete the specified `Patient`.

![Interactions Inside the Logic Component for the `delete t/patient P001` Command Execution](images/DeletePatientExecution.png)

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:

* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a
  placeholder for the specific command name e.g., `AddPatientParser`) which uses the other classes shown above to parse
  the user command and create a `XYZCommand` object (e.g., `AddPatientCommand`) which the `AddressBookParser` returns back as
  a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddPatientParser`, `DeletePatientParser`, ...) inherit from the `Parser`
  interface so that they can be treated similarly where possible e.g, during testing.

### Model component

**API** : [`Model.java`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/model/Model.java)

![ModelClassDiagram](images/ModelClassDiagram.png)

The `Model` component,

* stores address book data, which consists of `Doctor`, `Patient`, `Activity` objects and one `UserProfile` object.

* `Doctor` and `Patient` Objects are each contained within their own `UniquePersonList<Doctor>` or 
`UniquePersonList<Patient>` object respectively while `Activity` objects are contained within a `UniqueActivityList` object.

* stores the currently 'selected' `Doctor` or `Patient` objects (e.g., results of a search query) as a separate _filtered_ list which
is exposed to outsiders as an unmodifiable `ObservableList<Doctor>` or `ObservableList<Patient>`.

* stores the currently 'selected' `Activity` objects (e.g., results of a search query) as a separate _filtered_ list which
is exposed to outsiders as an unmodifiable `ObservableList<Activity>`. `Activity`
objects can be filtered by its internal id or by its starting time.

*  `ObservableList<T>` objects are objects that can be 'observed'. e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.

* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as
  a `ReadOnlyUserPref` objects.

* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they
  should make sense on their own without depending on other components)

### Storage component

**API** : [`Storage.java`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/storage/Storage.java)

![Storage Component Class Diagram](images/StorageClassDiagram.png)

The `Storage` component handles the storing and loading of the data that GoMedic requires in order to function. 
These include user-created data, such as the patients, activities, doctors, and the user profile that is stored in GoMedic, 
as well as data related to the preferences and settings in GoMedic.

The `Storage` component makes use of the `Jackson` library to parse the data from GoMedic into a `JSON` format, and vice-versa.

* To load data from the user's hard disk, it parses the data stored in human-readable `json` files that reside in the `[JAR file location]/data/` directory. 
This directory is automatically created by GoMedic if it doesn't already exist (usually occurs during the first launch).
* To store data from GoMedic into these files, it parses important attributes of these data (e.g. for a doctor, important information 
would include the doctor's name, phone number and department) into a pre-defined `JSON` format. For doctors, this is defined 
in [`JsonAdaptedDoctor.java`](https://github.com/AY2122S1-CS2103T-T15-1/tp/tree/master/src/main/java/gomedic/storage/JsonAdaptedDoctor.java).


In general, the role of the `Storage` component is to:
* Save both user-created data (which is abstracted as the **AddressBook**) and user preference data in json format, and read them back into corresponding
  objects.
* Make use of its inheritance from both `AddressBookStorage` and `UserPrefStorage` to play the role either one, depending on
  context, and the necessary functions required of it. 

**Note:** To implement the `Storage` architecture, dependencies on some classes in the `Model` component, 
such as `Patient`, `Doctor`, `Activity` and `UserProfile` are required.
This is because the `Storage` component's job is to save/retrieve objects that belong to the `Model`, hence it would need
to access objects from these classes in order to acquire the necessary attributes from these objects to be stored. However,
note that we do not show these dependencies in the diagram above, in order to preserve the high-level design representation of
the `Storage` architecture, and to reduce unnecessary information overload.

### Common classes

Classes used by multiple components are in the `gomedic.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Command History feature

Command history feature helps the user keep track of all commands inputted in the current session and allows navigation
back and forth between all the commands entered in the instance so far.

Given below is an activity diagram showing the event flow when the user executes a key press:

![CommandHistoryActivityDiagram](images/CommandHistoryActivityDiagram.png)

### Suggestions feature

The suggestions feature is facilitated by `Messages`. It is a class consisting of static immutable string messages for 
various fixed error outputs for GoMedic. It also implements the following operations:

* `Messages#getSuggestions(String command)` — Returns suggested commands within GoMedic based on the incorrect command
input.

Given below is a sequence diagram when a user provides an erroneous input, "adl t/patent".

![SuggestionsSequenceDiagram](images/SuggestionsSequenceDiagram.png)

After GoMedic parses the invalid command,

1. Each erroneous command is split into its type, which is the first word of the command, and the target, which is the 
rest of the command, if it exists.
2. The method in the `Messages` class does the appropriate function call(s) suitable for the nature of the erroneous command input
given by the alternate paths in the diagram. 
3. The outputs of the function calls are then compiled and then returned to the `AddressBookParser` to be thrown as an
exception with `reply` as the error message to the user.

**Note:**

* `generateTypeSuggestions` and `generateTargetSuggestions` are private methods only accessed from within getSuggestions and nowhere else.

* The suggestions are generated according to how close the erroneous commands are to the existing commands using the
Levenshtein Distance metric and then ranked. The final output is an intersection of the suggestions generated from the 
two suggestion functions mentioned above.

### Generating Medical Referral Feature 

This feature allows GoMedic users to generate medical referral for a uniquely identified patient identified by his/her `PatientId` to other
doctor that is already saved in the GoMedic application and would be uniquely identified by his/her 
`DoctorId`.

This feature can be accessed using `referral` command which has parameters of `ti/Title`, `di/DoctorId`, `pi/PatiendId` and optional 
description which can be added using `d/Description` flag. 

_This feature uses **iText Java Pdf writer library** to generate the medical referral document._  

**Workflow**

In general, the following *Activity Diagram* summarizes the workflow of this command :

![workflow](images/referral/ReferralCommandWorkflow.png)

For illustration purposes, suppose the user enters the command:

`referral ti/Referral of Patient A di/D001 pi/P001 d/He is having internal bleeding, need urgent attention.`

<div markdown="span" class="alert alert-info">:information_source:
**Note:** the doctor id and patient id does not need to conform the `DXXX` and `PXXX` format in this case. However, should the supplied ids are invalid,
GoMedic would be unable to find the doctor and the patient, and would show the feedback patient/doctor not found message to the user.
</div>

Once the user enter the command is entered, the following **Sequence Diagram** below shows how the components specified in the [architecture](#architecture) interact with each other creates the new `ReferralCommand` object

![ReferralCommandCreation](images/referral/ReferralCommandCreation.png)

After the `LogicManager` receives the new `ReferralCommand` object, 

1. The `ReferralCommand` then would call the appropriate methods from the `Model` to obtain the `UserProfile`, DoctorList` and `PatientList`
2. Based on the illustration, `ReferralCommand` then would filter and check for the existence of patient whose id is `P001` and doctor whose id is `D001`.

Where the specific methods are shown in the sequence diagram shown below : 

![ReferralCommandCreation](images/referral/ReferralCommandExecution.png)

When the data is ready, the `ReferralCommand` object would call the `iTextPdf` library *APIs* that enable it to create a new Pdf document as shown 
in the sequence diagram below : 

![ReferralCommandWrite](images/referral/ReferralCommandWriteFile.png)

Finally, the pdf object is written into the `data/` folder whose filename is the same of that of the `title` (i.e. _title_.pdf). 
For this illustration, the file then would be `Referral of Patient A.pdf`.

### Customizing user's personal profile in GoMedic
This feature allows the user to customize his or her profile details on GoMedic. These details can then be used to 
sign off the referrals generated by the `referral` command. 

This feature can be accessed using the `profile` command. The user can customize these details through the parameters:
* `n/NAME` : The name of the user
* `p/POSITION`: The position held by the user
* `de/DEPARTMENT`: The department that the user works in
* `o/ORGANIZATION`: The organization that the user works in

Given below is the sequence diagram when a user provides an example of a valid `profile` command 
(`profile n/Jon Snow p/Senior Consultant de/Department of Neurology o/National University Hospital`)
to update his or her profile in GoMedic.

<div markdown="span" class="alert alert-info">:information_source:
**Note:** The example below does not include the details of the creation of a `ProfileCommand` object, as the
implementation is similar to that of the example covered in the `ReferralCommand` above.
</div>

![ProfileSequenceDiagram](images/ProfileSequenceDiagram.png)

As seen in the diagram above, after the `LogicManager` receives the `ProfileCommand` object,

1. The `LogicManager` will call the `execute` method of `ProfileCommand`, passing in the `Model` as a parameter to the method.
2. The `ProfileCommand` will then create a new `UserProfile` object based on the details specified by the user in the command.
3. Then, `ProfileCommand` will call `Model#setUserProfile(updatedProfile)` to replace the existing user profile with the new one.
4. The `ProfileCommand` will create a `CommandResult` and return it to `LogicManager`.
5. `LogicManager` then calls `Model#getAddressBook()` to get the newly updated address book in the model.
6. Finally, `LogicManager` calls `Storage#saveAddressBook(addressBook)` to update the new user profile in the storage and returns
the `CommandResult` to be displayed to the user.

### View feature
This feature allows user to view patient's details as PatientTable does not show complete details of the patients.

This feature can be accessed using the `view` command, which currently only support the viewing of patient which follows
the following format `view t/patient PATIENT_ID` where `PATIENT_ID` is a valid id of a patient.

Given below is an activity diagram showing the event flow when the user wants to view a patient's details:

![ViewPatientActivityDiagram](images/ViewPatientActivityDiagram.png)

<div markdown="span" class="alert alert-info">:information_source:
**Note:** The example below does not include the details of the creation of a `ViewPatientCommand` object, as the
implementation is similar to that of the example covered in the `ReferralCommand` above.
</div>

![ViewPatientCommandExecution](images/ViewPatientExecution.png)

After the `LogicManager` receives the new `ViewPatientCommand` object,
1. The `ViewPatientCommand` would call the appropriate method from the `Model` to obtain the `Patient`'s specific details
   to be viewed.
2. Only then a `CommandResult` object will be returned.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of patients and colleagues
* is a very busy man with lots of appointments and activities
* prefer desktop apps over other types
* can type fast and prefer CLI-formatted commands
* prefers typing to mouse interactions and is reasonably comfortable using CLI apps
* often forgets about his patient details and his schedule

**Value proposition**:

* manage patients contacts faster than a typical mouse/GUI driven app
* able to manage other doctors' details
* able to store large amount of patients' medical data
* able to retrieve the detailed information of a patient very quickly
* able to find certain particular patients, activities, or doctors
* able to display upcoming activities and appointments to the user
* easy to use and would give suggestion on the closest command whenever typo is made

### User stories

**Priorities:**
* **High (must have)** - `* * *`
* **Medium (nice to have)** - `* *`
* **Low (unlikely to have)** - `*`

#### [EPIC] Basic CRUD Functionality for patients and doctors

| Priority | As a …​                                 | I want to …​                             | So that I can…​                                                        |
| -------- | ------------------------------------------ | --------------------------------------------| ---------------------------------------------------------------------- |
| `* * *`  | user                                       | add a new patient detail                    | retrieve and update them later                                                                        |
| `* * *`  | doctor                                     | add a new colleague detail                  | remember their contact number and office numbers
| `* * *`  | user                                       | delete an existing patient / doctor details | remove entries that I no longer need
| `* * *`  | user                                       | update my patient details                   | change the details without deleting and adding the info again
| `* * *`  | doctor                                     | update my colleague details                 | change the details without deleting and adding the info again
| `* * *`  | user                                       | view all my patient details in a list       | know my entire list of patients at a glance
| `* * *`  | user                                       | view all my colleague details in a list     | know my entire list of colleague at a glance

#### [EPIC] Scheduling

| Priority | As a …​                                 | I want to …​                                            | So that I can…​                                                        |
| -------- | ------------------------------------------ | -----------------------------------------------------------| ---------------------------------------------------------------------- |
| `* * *`  | busy user                                  | add a new appointment with one of my patient               | so that I can remember my appointments with them and be reminded of them in the future
| `* * *`  | busy user                                  | add new activities such as meeting with colleagues         |  so that I can remember my schedules today with and be reminded of them in the future
| `* * *`  | user                                       | delete existing appointments with my patients              | remove appointments that are no longer happening                |
| `* * *`  | user                                       | delete any existing activity                               | remove activities that are no longer happening and free my schedules up                                                 |
| `* *  `  | organized user                             | list all my future appointments with a certain patient     | plan my schedules and track the appointments                                                 |
| `* * *`  | organized user                             | list all my future activities                              | know my schedules and plan future activities accordingly                           |
| `*    `  | busy user                                  | be reminded of my patients' appointment 15 minutes before the schedule             | prepare myself for the appointment                         |
| `*    `  | busy user                                  | be reminded of my daily schedule when the app is started / at the start of the day |   know what I will be doing for the day and plan ahead                          |
| `* *  `  | forgetful user                             | search for specific activities and appointments within a specific time frame       | plan ahead and focus on those time slots only                         |
| `*    `  | organized user                             | change the reminder settings (minutes)                     | tailor it according to my preference                         |

#### [EPIC] Information Retrieval and Organization

| Priority | As a …​                                 | I want to …​                                                        | So that I can…​                                                        |
| -------- | ------------------------------------------ | -----------------------------------------------------------------------| --------------------------------------------------------------------------|
| `*    `  | experienced user                           | search for activities based on its title and description               | retrieve certain grouped activities very fast such as meetings and visitations
| `* * *`  | busy user                                  | search for patients whose details contain a user-specified substring   | retrieve certain patients that I don't really remember which fields where the details are stored at
| `* * *`  | busy user                                  | search for doctors whose details contain a user-specified substring    | retrieve my colleague details without any need to remember which fields the data are stored at

#### [EPIC] Misc Helpful Features

| Priority | As a …​                                 | I want to …​                                                        | So that I can…​                                                        |
| -------- | ------------------------------------------ | -----------------------------------------------------------------------| --------------------------------------------------------------------------|
| `* * *`  | new and forgetful user                     | pull up a list of commands                                             | pick the right commands quickly
| `* * *`  | new user                                   | sample entries in the app                                              | know how the app would look like when I would populate it with my data
| `* * *`  | new user                                   | have suggestions on typo that I made on commands                       | learn from my mistakes and correct it quickly
| `* *`    | fickle user                                | have the app accept multiple fixed ways to write dates and times       | do not need to remember the correct format all the time

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `GoMedic` and the **Actor** is the `user`, unless specified otherwise)

**Use Case: [UC1] - Adding a new patient record**

**MSS**

1. User requests to add a new patient record.
2. GoMedic shows confirmation about the new patient record being added, and displays the patient's full details.

   Use case ends.

**Extensions**

* 1a. Incomplete patient details are given by users

    * 1a1. GoMedic shows a feedback to the user about the missing data.

      Use Case ends.
    
* 2a. Wrong patient details (not following the given constraints)
    
    * 2a1. GoMedic shows a feedback to the user about the violation.
        
      Use Case ends.

**Use Case: [UC2] - Delete an existing patient record**

**MSS**

1. User requests to list all patients.
2. GoMedic shows a list of patients.
3. User requests to delete a specific patient in the list.
4. GoMedic deletes the patient.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. GoMedic shows a feedback to the user about invalid index.

      Use case ends.

**Use Case: [UC3] - Command Suggestions**

**MSS**

1. User types in a certain command such as creating <u>new patient record (UC1)</u> and <u>deleting an existing patient
   record (UC2)</u> with typo.
2. GoMedic shows a list of suggested commands.
3. User retypes the command and requests GoMedic to perform certain action.
4. GoMedic performs the specified action.

   Use case ends.

**Extensions**

* 1a. Command is valid.

  Use case ends.

* 2a. User decides not to retype the commands.

  Use case ends.

* 3a. User input an invalid command.

  Use case resumes at step 1.

**Use Case: [UC4] - Adding a new appointment record**

**MSS**

1. User requests to add a new appointment record.
2. GoMedic shows confirmation about the new appointment record being added, and displays details of the appointment and
which patient it is scheduled with.

   Use case ends.

**Extensions**

* 1a. Incomplete appointment details are given by users

  * 1a1. GoMedic shows a feedback to the user about the missing data.

    Use Case ends.
* 1b. Patient which does not currently exist in the system is given.
  * 1b1. GoMedic shows a feedback to the user about invalid patient
  
    Use Case ends.

**Use Case: [UC5] - Searching for specific records based on a specific field**

**MSS**
1. User requests to search within either the Patient, Doctor, or Activity category, specifying a substring 
   and a field to which that substring should be matched to. 
   
2. GoMedic shows a response message with the number of matches that have been found, and
displays the matching records.
   
    Use case ends.
   
**Extensions**
* 1a. GoMedic gives feedback to user that no matches are found if there are no matching
entries corresponding to the user's input.
  
  Return.
  
* 1b GoMedic displays an error when user input is in incorrect format.
    Return.

**Use Case: [UC6] - View an existing patient record**

**MSS**

1. User requests to list all patients.
2. GoMedic shows a list of patients.
3. User requests to view a specific patient in the list.
4. GoMedic shows the patient's details

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. GoMedic shows a feedback to the user about invalid index.

      Use case ends.

**Use Case: [UC7] - Clear all doctor records in GoMedic**

**MSS**

1. User requests to list all doctors.
2. GoMedic shows a list of doctors.
3. User requests to clear all records of the doctors.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

**Use Case: [UC8] - Navigate to the past command**

**MSS**

1. User wants to get the previous command he typed.
2. GoMedic shows the previous command the user typed.

   Use case ends.

**Extensions**

* 2a. There is no previous command.

  Use case ends.
  
**Use Case: [UC9] - Changing the user's profile**

**MSS**

1. User requests to change his/her profile on GoMedic
2. GoMedic shows confirmation that the user's profile has been updated, and displays the new profile

   Use case ends.

**Extensions**

* 1a. The details supplied by the user is incomplete.

  * 1a1. GoMedic shows a feedback to the user about the necessary details that need to be supplied.
  
    Use Case ends.

* 1b. An incorrect detail, that does not conform to the constraints imposed by GoMedic, is supplied by the user.

    * 1b1. GoMedic shows a feedback to the user about the detail of the constraint that is violated. 

      Use case ends.
    
*{More to be added}*

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. Should be able to be run without any installation required as long as the user has Java `11` installed.
3. Should be able to hold up to 1000 patients and colleagues without a noticeable delay (less than 2 seconds) in
   performance for typical usage.
4. Should be able to hold up to 200 future activities and future appointments, and be retrieved without a noticeable
   delay (less than 2 seconds) for typical searches.
5. Should be only used by a single user and do not require other users to make changes to the app such as making
   appointment or sharing activities.
6. The data should not be stored using a _DBMS_.
7. The data should be stored _locally_ and should be in a human _editable_ and easily modified text file.
8. The project is expected to adhere to a _biweekly version release_ using breadth-first incremental technique.
9. Should be less than **100 MB** in size for the software, and less than **15 MB** per file for each document.
10. The developer guide and user guide must be pdf-friendly (meaning no embedded video, animations, embedded PowerPoint,
    etc.).
11. Should be delivered to the user using a single JAR file.
12. Graphical User Interface (GUI) should work reasonable well for standard screen resolution of 1920 x 1080 and higher
    with screen scales 100% and 125%, and also usable for screen resolutions 1280 x 720 and higher with screen scales
    150%.
13. Should be written mainly using Object-oriented paradigm.
14. The app is not required to be able to interact with external pieces of hardware such as printer.
15. The data stored within the app should be encrypted for security purposes (to prevent the raw data being read by
    external parties).
16. The app is mainly used for users based in Singapore, and therefore some local terms are tolerable, and the app is
    not expected to operate in other languages except English.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X.
* **DBMS** : Database Management System such as MySQL, Oracle, PSQL, MongoDB, etc.
* **JAR** : Java Archive file format, which is typically used to aggregate many Java class files and associated metadata
  into one file for distribution.
* **Typical usage/searches** : Finding by keyword, name, medical histories, and any combination of the field manually.
* **Object-Oriented Paradigm** : programming paradigm that organizes software design around objects rather than
  functions and logic. For complete list of Features that OO design should have,
  please [visit this wikipedia page](https://en.wikipedia.org/wiki/Object-oriented_programming)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

    1. Download the jar file and copy into an empty folder

    2. Double-click the jar file. If you are unable to do so, you might need to run `java -jar gomedic.jar` from the terminal where the `gomedic.jar` file is located. Expected: Shows the GUI with a set of sample contacts. The window size may not be
       optimum.

2. Saving window preferences

    1. Resize the window to an optimum size. Move the window to a different location. Close the window.

    1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Adding a record in GoMedic

1. Add a new activity by supplying all necessary parameters. Do the test cases sequentially to ensure correct id number is created.

    1. **Prerequisite**: Ensure you activities data are empty by using `clear t/activity` command and check it again using `list t/activity` command. The table should show "no activities to be displayed".

    2. Test case: `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Activity 1 d/Discussing the future of CS2103T-T15 Group!`<br>
       Expected: New activity whose id `A001` is created, confirmation is shown in feedback box, and the activity table is shown.

    3. Test case: `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Activity 2`<br>
       Expected: Conflicting activity error is shown.

    4. Test case: `add t/activity s/15/09/2023 14:00 e/15/09/2023 15:00 ti/Activity 3`<br>
       Expected: New activity whose id `A002` is created with empty description. 
   
    5. Test case: `add t/activity s/15-09-2024 14:00 e/15-09-2024 15:00 ti/Activity 4`<br>
       Expected: New activity whose id `A003` is created with empty description despite different datetime format supplied.

    6. Test case: `add t/activity s/15-09-2025 15:00 e/15-09-2025 14:00 ti/Activity 5`<br>
       Expected: Error message start time must be strictly less than end time is shown in the feedback box.
   
    7. Other incorrect `add t/activity` commands to try: `add t/activities` with invalid parameters, etc <br>
       Expected: Error message shown in the feedback box.

2. Add a new patient by supplying all necessary parameters. Do the test cases sequentially to ensure correct id number is created.

    1. **Prerequisite**: Ensure you patients data are empty by using `clear t/patient` command and check it again using `list t/patient` command. The table should show "no patients to be displayed".

    2. Test case: `add t/patient n/John Smith p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes`<br>
       Expected: New patient whose id `P001` is created, confirmation is shown in feedback box, and the patient table is shown.

    3. Test case: `add t/patient n/John Snow p/12312312 a/51 b/B+ g/M h/173 w/65 m/heart failure`<br>
       Expected: New patient whose id `P002` is created.

    4. Test case: `add t/patient n/Tim Burton p/33334444 a/50 b/O- g/M h/173 w/65`<br>
       Expected: Error message "blood type should only contain A+, A-, B+, B-, AB+, AB-, O+, or O-, and it should not be blank. All non capital letters will be capitalized" will be shown in the feedback box.

    5. Test case: `add t/patient n/Cedric Tom p/11112222 a/23 b/O+ g/M h/800 w/65`<br>
       Expected: Error message height should be integer between 1 and 300 inclusive is shown in the feedback box.

    6. Other incorrect `add t/patient` commands to try: `add t/patients` with invalid parameters, etc. <br>
       Expected: Error message shown in the feedback box.

3. Add a new doctor by supplying all necessary parameters. Do the test cases sequentially to ensure correct id number is created.

    1. **Prerequisites**: Ensure the doctors' data are empty by using `clear t/doctor` command and check it again using `list t/doctor` command. The table should show "no doctors to be displayed".

    2. Test case: `add t/doctor n/John Smith p/98765432 de/Cardiology`<br>
       Expected: New doctor whose id `D001` is created, confirmation is shown in feedback box, and the doctor table is shown.

    4. Test case: `add t/doctor n/Tim Burton p/93561345`<br>
       Expected: Error message of invalid command format is shown in the feedback box, as a department was not specified.

    5. Test case: `add t/doctor n/Cedric Tom p/11112222333 de/Cardiology`<br>
       Expected: Error message that phone number should be 8 digits long is shown in the feedback box.

    6. Other incorrect `add t/doctor` commands to try: `add t/doctors`, invalid parameters, `...` <br>
       Expected: Error message shown in the feedback box.
   
### Deleting a record in GoMedic

1. Deleting an activity while all activities are being shown

    1. **Prerequisite**: List all activities using the `list t/activity` command. 
       Ensure at least 1 activity with id `A001` is there, otherwise please use `add t/activity` command to add a new activity. 
       Multiple activities will be displayed in a table sorted by its id.

    2. Test case: `delete t/activity A001`<br>
       Expected: Activity whose id `A001`. Details of the deleted contact shown in the feedback box. 

    3. Test case: `delete t/activity A001`<br>
       Expected: No activity is deleted. Error details shown in the feedback box. 

    4. Other incorrect `delete t/activity` commands to try: `delete t/activity`, `delete t/activities`, `delete t/activity x` (where x is an invalid id), etc <br>
       Expected: Error message shown in the feedback box.

2. Deleting a patient while all patients are being shown

    1. **Prerequisite**: List all patients using the `list t/patient` command.
       Ensure at least 1 patient with id `P001` is there, otherwise please use `add t/patient` command to add a new patient.
       Multiple patients will be displayed in a table sorted by its id.

    2. Test case: `delete t/patient P001`<br>
       Expected: Patient with id `P001` is deleted. Details of the deleted patient shown in the feedback box.

    3. Test case: `delete t/patient P001`<br>
       Expected: No patient is deleted. Error details shown in the feedback box.

    4. **Prerequisite**: Clear the entire GoMedic using `clear` command.
       Add 1 new patient by running `add t/patient n/John Smith p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes` and
       1 new appointment by running `add t/appointment i/P001 s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Appointment with P001 d/Follow-up from tuesday's appointment.`

    5. Test case: `delete t/patient P001`<br>
       Expected: Patient with id `P001` is deleted. Details of the deleted patient shown in the feedback box. Appointment related to the patient will be deleted as well.

    6. Other incorrect delete patient commands to try: `delete t/patient`, `delete t/patients`, `delete t/patient x` (where x is an invalid id), etc <br>
       Expected: Error message shown in the feedback box.

3. Deleting a doctor while all doctors are being shown
   
    1. **Prerequisites**: List all doctors using the `list t/doctor` command.
       Ensure at least 1 doctor with id `D001` is there, otherwise please use `add t/doctor` command to add a new doctor.
       Multiple doctors will be displayed in a table sorted by its id.

    2. Test case: `delete t/doctor D001`<br>
       Expected: Doctor with id `D001` is deleted. Details of the deleted doctor are shown in the feedback box.

    3. Test case: `delete t/doctor D001`<br>
       Expected: No doctor is deleted. Error details shown in the feedback box.

    4. Other incorrect delete doctor commands to try: `delete t/doctor`, `delete t/doctors`, `delete t/doctor x` (where x is an invalid id), `...` <br>
       Expected: Error message shown in the feedback box.
       
### Editing a record in GoMedic

1. Editing an existing activity

    1. **Prerequisite**: Clear the entire activity using `clear t/activity` command. 
   Add a new activity using `add t/activity` command to ensure at least 1 activity with id `A001` is there. Check that it exists using `list t/activity`. Please do the test sequentially. 

    2. Test case: `edit t/activity i/A001 ti/Another new title`<br>
       Expected: Activity whose id `A001` has its title changed to "Another new title"

    3. Test case: `edit t/activity i/A001 s/17/10/2021 14:00 e/17/10/2021 15:00`<br>
       Expected: Activity whose id `A001` has its start time changed to "17-10-2021 14:00" and end time to "17-10-2021 15:00"

    4. Test case: `edit t/activity i/A001 s/17/10/2021 18:00 e/17/10/2021 15:00`<br>
       Expected: Error message shows start time must be before end time. 

    5. Test case: Add another activity using `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Meeting with Mr. Y` and then run 
   `edit t/activity i/A001 s/15/09/2022 14:00 e/15/09/2022 15:00`<br>
       Expected: Error message shows the activity's timing is conflicting with another activity.

    6. Other incorrect `edit t/activity` commands to try: `edit t/activity i/a001 pi/p001` (cannot change patient id), `edit t/activities`, `edit t/activity` (no parameters), etc. <br>
      Expected: Error message shown in the feedback box.
       
2. Editing an existing doctor

    1. **Prerequisite**: Clear all existing doctors in GoMedic using `clear t/doctor` command. 
   Add a new doctor using `add t/doctor` command. Ensure that a doctor with id `D001` exists by executing `list t/doctor`. 
   Conduct the following tests in sequential order. 

    2. Test case: `edit t/doctor i/D001 n/Jon Snow`<br>
       Expected: Doctor whose id is `D001` has his/her name changed to "Jon Snow".

    3. Test case: `edit t/doctor i/D001 n/Jon Low p/98765432`<br>
       Expected: Doctor whose id is `D001` has his/her name changed to "Jon Low", and phone number changed to "98765432".

    4. Test case: `edit t/doctor i/D001 n/Jon Low p/9191`<br>
       Expected: Feedback box displays constraint violation error message, indicating that the phone number has to be entirely numeric and exactly 8 digits.

    6. Other incorrect `edit t/doctor` commands to try: `edit t/doctor` (no parameters supplied), `edit t/doctor n/` (no value supplied for `NAME` parameter), etc <br>
      Expected: Feedback box displays error message indicating an invalid command / invalid command format / parameter constraints violations.

3. Editing an existing patient

    1. **Prerequisite**: Clear the entire patient using `clear t/patient` command.
       Add a new patient using `add t/patient` command to ensure at least 1 patient with id `P001` is there. Check that it exists using `list t/patient`. Please do the test sequentially.

    2. Test case: `edit t/patient i/P001 n/Tom tom`<br>
       Expected: Patient whose id `P001` has his/her name changed to "Tom tom"

    3. Test case: `edit t/patient i/P001 h/165 w/76`<br>
       Expected: Patient whose id `P001` has his/her height changed to "165" and weight changed to "76"

    4. Test case: `edit t/patient i/P001 p/12345678 b/O-`<br>
       Expected: Patient whose id `P001` has his/her phone number changed to "12345678" and blood type to "O-"

    5. Test case: `edit t/patient i/P001 a/77 g/O`<br>
       Expected: Patient whose id `P001` has his/her age changed to "77" and gender changed to "O"

    6. Test case: `edit t/patient i/P001 b/C+`<br>
       Expected: Error message "blood type should only contain A+, A-, B+, B-, AB+, AB-, O+, or O-, and it should not be blank. All non capital letters will be capitalized" will be shown in the feedback box.

    7. Other incorrect delete patient commands to try: `delete t/patients`, `edit t/patient` (no parameters), etc <br>
       Expected: Error message shown in the feedback box.

### Changing the user profile

1. Changing the user profile shown on the left side window       
    
    1. Test case: `profile n/Jon Snow p/Consultant de/Department of Cardiology o/National University Hospital`
        Expected: The feedback box displays the confirmation of the change of user profile, 
        and GoMedic updates the left side window with the corresponding information.
       
    2. Test case: `profile n/Bernice Yu p/Associate Professor de/Department of Radiology`
        Expected: The feedback box displays an error message stating that an invalid command format has been detected. 
        This corresponds to the fact that the user has not supplied his/her `ORGANIZATION` in the command. 
       
    3. Test case: `profile n/Bernice Yu p/Associate. Professor de/Department of Radiology o/Tan Tock Seng Hospital`
        Expected: The feedback box displays an error message stating that the `POSITION` parameter should only contain alphanumeric characters and spaces. 
        This corresponds to the fact that the user included an illegal character `.` in the `POSITION` parameter of the command. 

    4. Other incorrect `profile` commands to try: Commands that resemble the command in test case 1, but include 
        the illegal character `.` in its `NAME`, `DEPARTMENT` or `ORGANIZATION` parameters.
        Expected: The feedback box displays an error message stating that the constraints for those parameters have been 
        violated, similar to that in test case 3.

### Creating A Referral

1. Creating a referral using the template available. 

    1. **Prerequisite**: Check that you have `[JAR Location]/data` folder, it should be created after you run **GoMedic** for the first time. 
   Clear the entire patient and activity using `clear t/patient` and `clear t/doctor` respectively. Run the following commands to add 1 patient and doctor using
   `add t/patient n/John Doe p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes` and `add t/doctor n/John Smith p/98765432 de/Cardiology` respectively.<br> Check that patient whose id `P001` and doctor whose id `D001` exists
   using `list t/patient` and `list t/doctor` respectively. Also use this default profile by inputting this command `profile n/John Smith p/Senior Resident de/Cardiology o/NUH`.
   
    2. Test case: `referral ti/Referral di/D001 pi/P001 d/It looks like there may be a small tear in his aorta.`<br>
       Expected: A new referral called `Referral.pdf` is created in the `data` folder. The file should look like the following image but the date should be the date where you run the referral command.<br>
    ![referral](images/referral.png)

    3. Other incorrect `referral` commands to try: `referral ti/test di/d002 pi/p003` (non-existent doctor and patient id), etc <br>
        Expected: Error message shown in the feedback box.

### Viewing a patient

1. Viewing an existing patient

    1. **Prerequisite**: Clear the entire patient using `clear t/patient` command.
       Add a new patient using `add t/patient` command to ensure only 1 patient exist with id `P001` is there. Check that it exists using `list t/patient`. Please do the test sequentially.

    2. Test case: `view t/patient P001`<br>
       Expected: Patient whose id `P001` has its details shown in GoMedic application

    3. Test case: `view t/patient P002`<br>
       Expected: Error message the patient id doesn't exist in the list will be shown in the feedback box
       
    4. Other incorrect view patient commands to try: `view t/patients`, `view t/patient` (no parameters), etc. <br>
       Expected: Error message shown in the feedback box.

### Listing records in GoMedic

1. Listing activities in GoMedic

    1. **Prerequisite**: Clear all existing activities in GoMedic using `clear t/activity` command.
       Add 2 new activities by running `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/team meeting d/CS2103t group discussion` and
       `add t/activity s/14/09/2022 11:00 e/14/09/2022 12:00 ti/Lunch with CEO d/Lunch to discuss promotion` in order.
       Conduct the following tests in sequential order.

    2. Test case: `list t/activity`<br>
       Expected: GoMedic shows a table with 2 activities, one with id `A001` and another with id `A002`.

    3. Test case: `list t/activity s/START`<br>
       Expected: GoMedic shows both activities, but the activity with id `A002` is shown before the activity with id `A001`.

    4. Test case: `list t/activity s/START p/ALL`<br>
       Expected: GoMedic shows both activities, but the activity with id `A002` is shown before the activity with id `A001`.

    5. Test case: `list t/activity p/HEHE`<br>
       Expected: The feedback box displays an error message about the parameter supplied.

    6. Other valid `list t/activity` commands to try: First, add an activity whose date for the `START_TIME` and `END_TIME`
       corresponds to date that the user tests the `list t/activity` command. Then, run `list t/activity s/ID p/TODAY`. <br>
       Expected: GoMedic shows a table of activities that includes the activity mentioned above.

    7. Other invalid `list t/activity` commands to try: `list t/activites`, `list t/activity s/HOHO` (invalid parameter supplied), etc <br>
       Expected: Feedback box displays error message indicating an invalid command / parameter constraints violations.

2. Listing doctors in GoMedic

    1. **Prerequisite**: Clear all existing doctors in GoMedic using `clear t/doctor` command.
       Add 2 new doctors by running `add t/doctor n/John Smith p/98765432 de/Cardiology` and
       `add t/doctor n/Tom Hill p/12345678 de/Radiology` in order.
       Conduct the following tests in sequential order.

    2. Test case: `list t/doctor`<br>
       Expected: GoMedic shows a table with 2 doctors, one with id `D001` and another with id `D002`.

    3. Test case: `list t/doctor extra parameters supplied here`<br>
       Expected: GoMedic shows a table with 2 doctors, one with id `D001` and another with id `D002`, as it ignores the
       extra parameters supplied.

    4. Other invalid `list t/doctor` commands to try: `list t/doctors` <br>
       Expected: Feedback box displays error message indicating an invalid command.

3. Listing patients in GoMedic

    1. **Prerequisite**: Clear all existing patients in GoMedic using `clear t/patient` command.
       Add 2 new patients by running `add t/patient n/John Smith p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes` and
       `add t/patient n/Joan Lim p/12345678 a/30 b/A- g/F h/165 w/45 m/high blood pressure` in order.
       Conduct the following tests in sequential order.

    2. Test case: `list t/patient`<br>
       Expected: GoMedic shows a table with 2 patients, one with id `P001` and another with id `P002`.

    3. Test case: `list t/patient extra parameters supplied here`<br>
       Expected: GoMedic shows a table with 2 patients, one with id `P001` and another with id `P002`, as it ignores the
       extra parameters supplied.

    4. Other invalid `list t/patient` commands to try: `list t/patients` <br>
       Expected: Feedback box displays error message indicating an invalid command.

### Clearing records in GoMedic

1. Clearing activity records in GoMedic

    1. **Prerequisite** (**run before every test case**): Clear the entire activity using `clear t/activity` command.
       Add 2 new activities by running `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Meeting with Mr. Y d/Discussing the future of CS2103T-T15 Group!` and
       `add t/activity s/16/09/2022 14:00 e/16/09/2022 15:00 ti/Meeting with Mr. X d/Discussing the features of CS2103T-T15 Project!` in order.
       Conduct the following tests in sequential order.

    2. Test case: `clear t/activity`<br>
       Expected: GoMedic shows an empty activity table

    3. Test case: `clear t/activity extra parameters supplied here`<br>
       Expected: GoMedic shows an empty activity table, as it ignores the
       extra parameters supplied.

    4. Other invalid `clear t/activity` commands to try: `clear t/activities` <br>
       Expected: Feedback box displays error message indicating an invalid command.

2. Clearing doctor records in GoMedic

    1. **Prerequisite** (**run before every test case**): Clear the entire doctor using `clear t/doctor` command.
       Add 2 new doctors by running `add t/doctor n/John Smith p/98765432 de/Cardiology` and
       `add t/doctor n/Tommy Tom p/12312312 de/Skin` in order.
       Conduct the following tests in sequential order.

    2. Test case: `clear t/doctor`<br>
       Expected: GoMedic shows an empty doctor table

    3. Test case: `clear t/doctor extra parameters supplied here`<br>
       Expected: GoMedic shows an empty doctor table, as it ignores the
       extra parameters supplied.

    4. Other invalid `clear t/doctor` commands to try: `clear t/doctors` <br>
       Expected: Feedback box displays error message indicating an invalid command.

3. Clearing patient records in GoMedic
   
    1. **Prerequisite** (**run before every test case**): Clear the entire patient using `clear t/patient` command.
       Add 2 new patients by running `add t/patient n/John Smith p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes` and
       `add t/patient n/Joan Lim p/12345678 a/30 b/A- g/F h/165 w/45 m/high blood pressure` in order.
       
       (**run before test case 4**):
       Add 1 new appointment by running `add t/appointment i/P001 s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Appointment with P001 d/Follow-up from tuesday's appointment.`
       Conduct the following tests in sequential order.

    2. Test case: `clear t/patient`<br>
       Expected: GoMedic shows an empty patient table

    3. Test case: `clear t/patient extra parameters supplied here`<br>
       Expected: GoMedic shows an empty patient table, as it ignores the
       extra parameters supplied.
       
    4. Test case: `clear t/patient`<br>
       Expected: GoMedic shows an empty patient table and delete all appointments as all the patients are deleted

    5. Other invalid `clear t/patient` commands to try: `clear t/patients` <br>
       Expected: Feedback box displays error message indicating an invalid command.

4. Clearing all records in GoMedic

    1. **Prerequisites** (**run before every test case**): Clear the entire GoMedic using `clear` command.
       Add 1 new activity by running `add t/activity s/15/09/2022 14:00 e/15/09/2022 15:00 ti/Meeting with Mr. Y d/Discussing the future of CS2103T-T15 Group!`,
       1 new doctor by running `add t/doctor n/John Smith p/98765432 de/Cardiology`, and
       1 new patient by running `add t/patient n/John Smith p/98765432 a/45 b/AB+ g/M h/175 w/70 m/heart failure m/diabetes`
       Conduct the following tests in sequential order.

    2. Test case: `clear`<br>
       Expected: GoMedic clears all records for activities, doctors, and patients

    3. Test case: `clear extra parameters supplied here`<br>
       Expected: GoMedic clears all records for activities, doctors, and patients, as it ignores the
       extra parameters supplied.
       
### Finding a patient, doctor or activity

1. Searching for a doctor or a patient
    1. **Prerequisite**: List the patients, doctors, or activities based on which one you wish to see, using the `list` command.
    e.g. `list t/doctor` or `list t/patient` or `list t/activity`.
       
    2. Test case: e.g. `find t/patient n/Joe`
        Expected: All patients whose names contain the substring "Joe" (case-insensitive) will be displayed.
       
    3. Test case: e.g. `find t/activity ti/Meeting`
        Expected: All activities whose title or description contains the substring "Meeting" (case-insensitive) will be displayed. 
       
    4. Other incorrect find commands to try: `find t\patient Joe` 
        Expected: Error message as a flag is not specified prior to the keyword.

### Displaying suggestions for commands that are misspelled

1. Displaying suggestions for commands that are misspelt and in invalid format

   1. Test case: e.g. `find pateint`
      Expected: `find t/patient`, `find t/doctor`, `find t/activity` should all be listed in the feedback box.
   
   2. Test case: e.g. `clap`
      Expected: `clear t/patient`, `clear t/doctor`, `clear t/activity`, `clear` should all be listed in the feedback box.
   
   3. For really badly spelt commands, such as this test case: e.g. `asdahsdhajshd`
      Expected: Feedback box should only say, `Sorry, asdahsdhajshd is an invalid command.`

2. Displaying suggestions for commands that are misspelt but in valid format

   1. Test case: e.g. `find t/pateint`
      Expected: only `find t/patient` should be listed in the feedback box.
   
   2. Test case: e.g. `add t/appent`
      Expected: `add t/patient`, `add t/appointment` should both be listed in the feedback box.

   3. Test case: e.g. `add t/apsda`
      Expected: Feedback box should only say, `Sorry, add t/apsda is an invalid command.`

### Navigating between all commands input in the current session

1. Going back to previously typed commands

   1. **Prerequisite** (follow the steps accordingly before going ahead with the tests in this section): 
      1. Open up GoMedic
      2. Clear GoMedic with the `clear` command
      3. Input `add t/doctor n/John Smith p/98765432 de/Cardiology` and enter
      4. Input `add t/doctor n/John Wayne p/11111111 de/OB` and enter
      5. Proceed to conduct the following tests in sequential order
   
   2. Test case: `Up` arrow key is pressed once
      Expected: `add t/doctor n/John Wayne p/11111111 de/OB` should show up in the input box.
   
   3. Test case: `Up` arrow key is pressed twice
      Expected: `clear` should show up in the input box.
   
   4. Test case: `Up` arrow key is pressed once
      Expected: `clear` should still be in the input box.

2. Going forward to more recent commands

      1. **Note:**
         1. The current state before any tests are done in this section should be carried over from the previous section.
         2. The following tests should be done in order
      
      2. Test case: `Down` arrow key is pressed once
         Expected: `add t/doctor n/John Smith p/98765432 de/Cardiology` should show up in the input box.
   
      3. Test case: `Down` arrow key is pressed twice
         Expected: The input box should be cleared.
   
## **Appendix: Effort**

**Overview**

Overall, this project is a moderately challenging application. Most of the features here are `CRUD` features, but a lot of efforts need to be put to replicate `CRUD` for an extra model in the application as we add some specific characteristics to each model. While the original AB3 only deals with 
one entity type which is `Person`, we modify the `Person` to be a generic class and add three models called `Activity`, `Patient` and `Doctor`. 

Our app is more complex in a sense we need to deal
with three entities at once and manage the interactions between them such as creating appointments, viewing patients, creating referral, etc. Also, we need to maintain the information of which 
model is being displayed and switching the "page" of these three models depending on the commands called. Hence, a lot of `ObservableValue<T>` from `JavaFX` library is used to so that the `Ui` can monitor
the model of interest currently.

Not only that, there is an extra model called `UserProfile` to personalize the application as it will display the user identity in te sidebar, and the `UserProfile` is also used for creating a referral. 

**Some noteworthy efforts:**

1. To implement the responsive table view, we need to mainly refer to  [this `TableView` article](http://tutorials.jenkov.com/javafx/tableview.html). 
We need to learn about `tableCellFactory` also to change the height dynamically based on the length of the data inside the cell.
2. The implementation of `CRUD` methods of `Activity`, `Doctor` and `Patient` mainly refers from AB3 `Person` and their commands. However, we create all our fields ourselves and test them. For `Time` field, it is mainly a wrapper over `LocalDateTime` class provided by Java. 
3. We overhaul the entire `Ui` based on the Figma, therefore we also create a new side window, and modifies the `CSS` moderately. We also discard the `personView` and `personCard` as they are no longer used. 
4. We allow users to add `Appointment`, an extension of `Activity` which stores related patient in the appointment.
5. We create a new `Ui` for viewing patient details which will show all the patient's appointments and medical conditions.
6. Quality of life improvements such as a more extensive help page, ability to see suggestions for misspelt commands and
ability to navigate between all inputted commands in the current session.

*{...more to be added}*
