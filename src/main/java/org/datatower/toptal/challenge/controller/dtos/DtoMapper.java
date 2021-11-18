package org.datatower.toptal.challenge.controller.dtos;

import org.datatower.toptal.challenge.controller.dtos.patient.GenderDto;
import org.datatower.toptal.challenge.controller.dtos.patient.PatientDto;
import org.datatower.toptal.challenge.model.Gender;
import org.datatower.toptal.challenge.model.Patient;
import org.mapstruct.Mapper;

@Mapper
public interface DtoMapper {

    Patient patient(PatientDto patientDto);

    PatientDto patientDto(Patient patient);

    Gender gender(GenderDto genderDto);

    GenderDto genderDto(Gender gender);
}
