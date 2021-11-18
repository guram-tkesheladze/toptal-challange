package org.datatower.toptal.challenge.controller;

import org.datatower.toptal.challenge.common.HealthDataException;
import org.datatower.toptal.challenge.controller.dtos.patient.GenderDto;
import org.datatower.toptal.challenge.controller.dtos.patient.PatientDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RequestMapping("patient")
public interface PatientController {

    @GetMapping("{id}")
    PatientDto getPatient(@Positive @PathVariable long id) throws HealthDataException;

    @GetMapping("query")
    Page<PatientDto> queryPatients(@RequestParam(required = false) String lastName,
                                   @RequestParam(required = false) GenderDto gender,
                                   @PositiveOrZero @RequestParam int page,
                                   @Positive @RequestParam int size);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PatientDto createPatient(@Valid @RequestBody PatientDto patient) throws HealthDataException;

    @DeleteMapping("{id}")
    void deletePatient(@Positive @PathVariable long id) throws HealthDataException;
}
