package org.datatower.toptal.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PatientRepository extends JpaRepository<Patient, Long>,
                                           JpaSpecificationExecutor<Patient> {
}
