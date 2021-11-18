package org.datatower.toptal.challenge.service;

import org.datatower.toptal.challenge.common.HealthDataException;
import org.datatower.toptal.challenge.model.Gender;
import org.datatower.toptal.challenge.model.Patient;
import org.springframework.data.domain.Page;

public interface PatientService {

    Patient findPatientById(long id) throws HealthDataException;

    Page<Patient> queryPatients(String lastName, Gender gender, int page, int size);

    Patient createPatient(Patient patient) throws HealthDataException;

    void deletePatientById(long id) throws HealthDataException;
}
