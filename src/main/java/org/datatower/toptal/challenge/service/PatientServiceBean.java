package org.datatower.toptal.challenge.service;

import org.apache.commons.lang3.StringUtils;
import org.datatower.toptal.challenge.common.ExceptionKeys;
import org.datatower.toptal.challenge.common.HealthDataException;
import org.datatower.toptal.challenge.model.Gender;
import org.datatower.toptal.challenge.model.Patient;
import org.datatower.toptal.challenge.model.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;

@Service
public class PatientServiceBean implements PatientService {

    private static final int PATIENT_LEGAL_AGE = 18;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Patient findPatientById(long id) throws HealthDataException {
        return patientRepository.findById(id)
                .orElseThrow(() -> HealthDataException.notFound(ExceptionKeys.PatientNotFound));
    }

    @Override
    public Page<Patient> queryPatients(String lastName, Gender gender, int page, int size) {
        Specification<Patient> query = ((root, q, cb) -> cb.conjunction());
        if (StringUtils.isNotBlank(lastName)) {
            Specification<Patient> lastNameQuery = (((root, q, cb) -> cb.like(root.get("lastName"), "%" + lastName + "%")));
            query = query.and(lastNameQuery);
        }
        if (gender != null) {
            Specification<Patient> genderQuery = ((root, q, cb) -> cb.equal(root.get("gender"), gender));
            query = query.and(genderQuery);
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastName"));
        return patientRepository.findAll(query, pageRequest);
    }

    @Override
    @Transactional
    public Patient createPatient(Patient patient) throws HealthDataException {
        validateBirthDate(patient);
        return patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void deletePatientById(long id) throws HealthDataException {
        if (!patientRepository.existsById(id)) {
            throw HealthDataException.badRequest(ExceptionKeys.PatientNotFound);
        }
        patientRepository.deleteById(id);
    }

    private void validateBirthDate(Patient patient) throws HealthDataException {
        LocalDate now = LocalDate.now();
        if (now.isBefore(patient.getBirthday())) {
            throw HealthDataException.badRequest(ExceptionKeys.PatientBirthDateShouldNotBeInFuture);
        }
        int patientAge = Period.between(patient.getBirthday(), now).getYears();
        if (patientAge < PATIENT_LEGAL_AGE) {
            throw HealthDataException.badRequest(ExceptionKeys.PatientIsNotLegalAge);
        }
    }
}
