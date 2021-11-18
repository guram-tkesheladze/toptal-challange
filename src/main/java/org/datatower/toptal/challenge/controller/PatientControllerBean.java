package org.datatower.toptal.challenge.controller;

import org.datatower.toptal.challenge.common.HealthDataException;
import org.datatower.toptal.challenge.controller.dtos.DtoMapper;
import org.datatower.toptal.challenge.controller.dtos.patient.GenderDto;
import org.datatower.toptal.challenge.controller.dtos.patient.PatientDto;
import org.datatower.toptal.challenge.model.Patient;
import org.datatower.toptal.challenge.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientControllerBean implements PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DtoMapper mapper;

    @Override
    public PatientDto getPatient(long id) throws HealthDataException {
        Patient patient = patientService.findPatientById(id);
        return mapper.patientDto(patient);
    }

    @Override
    public Page<PatientDto> queryPatients(String lastName, GenderDto gender, int page, int size) {
        return patientService.queryPatients(lastName, mapper.gender(gender), page, size)
                .map(mapper::patientDto);
    }

    @Override
    public PatientDto createPatient(PatientDto patient) throws HealthDataException {
        Patient created = patientService.createPatient(mapper.patient(patient));
        return mapper.patientDto(created);
    }

    @Override
    public void deletePatient(long id) throws HealthDataException {
        patientService.deletePatientById(id);
    }
}
