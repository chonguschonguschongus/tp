package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * A list of Patients that enforces uniqueness between its elements and does not allow nulls.
 * A Patient is considered unique by comparing using {@code Person#isSamePerson(Person)}. As such, adding and updating of
 * Patients uses Person#isSamePerson(Person) for equality so as to ensure that the person being added or updated is
 * unique in terms of identity in the UniquePatientList. However, the removal of a Patient uses Person#equals(Object) so
 * as to ensure that the Patient with exactly the same fields will be removed.
 * <p>
 * Supports a minimal set of list operations.
 *
 * @see Person#isSamePerson(Person)
 */
public class UniquePatientList extends UniqueObjectList<Patient> {

    protected final ObservableList<Patient> internalList = FXCollections.observableArrayList();
    protected final ObservableList<Patient> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    public void setPersons(UniquePatientList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Returns true if the list contains an equivalent Patient as the given argument.
     */
    @Override
    public boolean contains(Patient toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSamePerson);
    }
    /**
     * Returns true if the list contains an equivalent ic as the given argument.
     */
    public boolean containsIc(Ic nric) {
        requireNonNull(nric);
        return internalList.stream().anyMatch(patient -> patient.getIc().equals(nric));
    }

    /**
     * Adds a Patient to the list.
     * The Patient must not already exist in the list.
     */
    @Override
    public void add(Patient toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicatePersonException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the Patient {@code target} in the list with {@code editedPatient}.
     * {@code target} must exist in the list.
     * The Patient identity of {@code editedPatient} must not be the same as another existing Patient in the list.
     */
    @Override
    public void setObject(Patient target, Patient editedPatient) {
        requireAllNonNull(target, editedPatient);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new PersonNotFoundException();
        }

        if (!target.isSamePerson(editedPatient) && contains(editedPatient)) {
            throw new DuplicatePersonException();
        }

        internalList.set(index, editedPatient);
    }

    /**
     * Removes the equivalent Patient from the list.
     * The Patient must exist in the list.
     */
    @Override
    public void remove(Patient toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new PersonNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code Patients}.
     * {@code Patients} must not contain duplicate Patients.
     */
    @Override
    public void setObjects(List<Patient> patients) {
        requireAllNonNull(patients);
        if (!objectsAreUnique(patients)) {
            throw new DuplicatePersonException();
        }

        internalList.setAll(patients);
    }

    @Override
    public ObservableList<Patient> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns true if {@code Patients} contains only unique Patients.
     */
    @Override
    protected boolean objectsAreUnique(List<Patient> patients) {
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = i + 1; j < patients.size(); j++) {
                if (patients.get(i).isSamePerson(patients.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
