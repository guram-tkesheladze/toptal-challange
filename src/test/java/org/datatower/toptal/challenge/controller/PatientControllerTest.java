package org.datatower.toptal.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.datatower.toptal.challenge.common.ExceptionKeys;
import org.datatower.toptal.challenge.controller.dtos.error.ErrorInfo;
import org.datatower.toptal.challenge.controller.dtos.patient.GenderDto;
import org.datatower.toptal.challenge.controller.dtos.patient.PatientDto;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PatientControllerTest {

    private static final Random rand = new Random(System.currentTimeMillis());

    private static final ObjectMapper om = new ObjectMapper();

    static {
        om.findAndRegisterModules();
    }

    private static final List<PatientDto> patients = List.of(
            new PatientDto(null, "Guram", "Tkesheladze", GenderDto.MALE, LocalDate.of(1999, 3, 5)),
            new PatientDto(null, "Scarlett", "Johansson", GenderDto.FEMALE, LocalDate.of(1984, 11, 22)),
            new PatientDto(null, "Chris", "Evans", GenderDto.MALE, LocalDate.of(1981, 6, 13)),
            new PatientDto(null, "Elizabeth", "Olsen", GenderDto.FEMALE, LocalDate.of(1989, 2, 16)),
            new PatientDto(null, "Chris", "Hemsworth", GenderDto.MALE, LocalDate.of(1983, 8, 11)),
            new PatientDto(null, "Gwyneth", "Paltrow", GenderDto.MALE, LocalDate.of(1972, 9, 27))
    );

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetPatient() throws Exception {
        mockMvc.perform(getPatientRequest(1L))
                .andDo(print())
                .andExpect(status().isNotFound());

        PatientDto toCreate = getPatient(0);
        PatientDto created = om.readValue(createPatient(toCreate)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(), PatientDto.class);

        PatientDto result = om.readValue(mockMvc.perform(getPatientRequest(created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), PatientDto.class);

        assertTrue(new ReflectionEquals(result, "id").matches(toCreate));
    }

    @Test
    void testCreatePatient_created() throws Exception {
        PatientDto toCreate = getPatient(0);
        createPatient(toCreate)
                .andExpect(status().isCreated());
    }

    @Test
    void testCreatePatient_nonNullId() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setId(12L);
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals("id", errorInfos.get(0).getField());
    }

    @Test
    void testCreatePatient_firstNameNull() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setFirstName(null);
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals("firstName", errorInfos.get(0).getField());
    }

    @Test
    void testCreatePatient_lastNameNull() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setLastName(null);
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals("lastName", errorInfos.get(0).getField());
    }

    @Test
    void testCreatePatient_genderNull() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setGender(null);
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals("gender", errorInfos.get(0).getField());
    }

    @Test
    void testCreatePatient_birthdayNull() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setBirthday(null);
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals("birthday", errorInfos.get(0).getField());
    }

    @Test
    void testCreatePatient_nonLegalAge() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setBirthday(LocalDate.of(2010, 1, 1));
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals(ExceptionKeys.PatientIsNotLegalAge, errorInfos.get(0).getMessage());
    }

    @Test
    void testCreatePatient_birthdayInFuture() throws Exception {
        PatientDto toCreate = getPatient(0);
        toCreate.setBirthday(LocalDate.of(2010, 1, 1));
        List<ErrorInfo> errorInfos = readErrors(createPatient(toCreate)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
        assertEquals(1, errorInfos.size());
        assertEquals(ExceptionKeys.PatientIsNotLegalAge, errorInfos.get(0).getMessage());
    }

    @Test
    void testDeletePatient_success() throws Exception {
        List<Long> patientIds = createAllPatients();
        for (Long patientId : patientIds) {
            deletePatient(patientId)
                    .andExpect(status().isOk());
        }
    }

    @Test
    void testDeletePatient_error() throws Exception {
        long lowerBound = createAllPatients().stream().mapToLong(l -> l).max().orElse(0);
        for (int i = 0; i < 5; i++) {
            long randomId = nextLong(lowerBound);
            deletePatient(randomId)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void testQueryPatients() throws Exception {
        createAllPatients();

        queryPatients(null, null, 0, Integer.MAX_VALUE)
                .andExpect(jsonPath("$.content.length()", equalTo(patients.size())));

        queryPatients("Tkeshe", null, 0, Integer.MAX_VALUE)
                .andExpect(jsonPath("$.content.length()", equalTo(1)));

        queryPatients("Tkeshe", null, 1, 1)
                .andExpect(jsonPath("$.content.length()", equalTo(0)));

        queryPatients("Tkeshe", GenderDto.FEMALE, 0, Integer.MAX_VALUE)
                .andExpect(jsonPath("$.content.length()", equalTo(0)));
    }

    private List<Long> createAllPatients() throws Exception {
        List<Long> createdIds = new ArrayList<>();
        for (PatientDto patient : patients) {
            createdIds.add(createPatientAndReturnId(patient));
        }
        return createdIds;
    }

    private long createPatientAndReturnId(PatientDto toCreate) throws Exception {
        return om.readValue(createPatient(toCreate).andReturn().getResponse().getContentAsString(), PatientDto.class).getId();
    }

    private ResultActions queryPatients(String lastName, GenderDto gender, int page, int size) throws Exception {
        MockHttpServletRequestBuilder request = get("/patient/query")
                .queryParam("lastName", lastName)
                .queryParam("gender", Optional.ofNullable(gender).map(GenderDto::name).orElse(null))
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size));
        return mockMvc.perform(request)
                .andDo(print());
    }

    private ResultActions createPatient(PatientDto toCreate) throws Exception {
        return mockMvc.perform(registerPatientRequest(toCreate))
                .andDo(print());
    }

    private ResultActions deletePatient(long patientId) throws Exception {
        return mockMvc.perform(delete(String.format("/patient/%d", patientId)))
                .andDo(print());
    }

    private MockHttpServletRequestBuilder getPatientRequest(long patientId) {
        return get(String.format("/patient/%d", patientId));
    }

    private MockHttpServletRequestBuilder registerPatientRequest(PatientDto patient) throws Exception {
        return post("/patient")
                .contentType("application/json")
                .content(om.writeValueAsString(patient));
    }

    private List<ErrorInfo> readErrors(String content) throws JsonProcessingException {
        return om.readValue(content, new TypeReference<>() {
        });
    }

    private PatientDto getPatient(int index) {
        PatientDto patient = patients.get(index);
        return new PatientDto(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getGender(), patient.getBirthday());
    }

    private long nextLong(long lowerBound) {
        long random = rand.nextInt();
        while (random <= lowerBound) {
            random = rand.nextInt();
        }
        return random;
    }

}