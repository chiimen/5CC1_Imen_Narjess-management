package tn.esprit.studentmanagement.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStudentService studentService;

    @Test
    void getAllStudents_retourneUneListe() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(studentService).getAllStudents();
    }

    @Test
    void getStudent_appelleLeServiceAvecId() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(new Student());

        mockMvc.perform(get("/students/getStudent/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(studentService).getStudentById(1L);
    }

    @Test
    void createStudent_transmetLeStudentAuService() throws Exception {
        when(studentService.saveStudent(any(Student.class))).thenReturn(new Student());

        mockMvc.perform(post("/students/createStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(studentService).saveStudent(any(Student.class));
    }

    @Test
    void updateStudent_transmetLeStudentAuService() throws Exception {
        when(studentService.saveStudent(any(Student.class))).thenReturn(new Student());

        mockMvc.perform(put("/students/updateStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(studentService).saveStudent(any(Student.class));
    }

    @Test
    void deleteStudent_appelleLeServiceAvecId() throws Exception {
        mockMvc.perform(delete("/students/deleteStudent/1"))
                .andExpect(status().isOk());

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void getStudentByEmail_transmetEmailAuService() throws Exception {
        when(studentService.getStudentByEmail("imen@example.com"))
                .thenReturn(new Student());

        mockMvc.perform(get("/students/getStudentByEmail/{email}", "imen@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(studentService).getStudentByEmail("imen@example.com");
    }
}
