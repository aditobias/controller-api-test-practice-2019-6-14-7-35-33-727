package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {

    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    TodoRepository todoRepository;

    @Test
    void should_return_equivalent_todo_list_when_given_sample_todo_list() throws Exception {
        //Given
        Todo todo = new Todo("Test", false);
        List<Todo> todoList = new ArrayList<>();
        todoList.add(todo);
        when(todoRepository.getAll()).thenReturn(todoList);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[0].title", is("Test")));
    }

    @Test
    void should_return_specific_todo_when_specific_id_is_given() throws Exception {
        //Given
        Todo todo = new Todo(1, "TestingSearch", false, 1);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        //when
        ResultActions result = mvc.perform(get("/todos/{todo-id}", 1L));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    void should_return_created_when_posting_new_todo() throws Exception {
        //Given
        List<Todo> todoList = new ArrayList<>();
        Todo todo = new Todo("test", false);

        when(todoRepository.getAll()).thenReturn(todoList);
        //when
        ResultActions result = mvc.perform(post("/todos")
                        .content(asJsonString(todo))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated());
    }

    @Test
    void should_return_OK_when_user_is_deleted() throws Exception {
        //Given
        Todo todo = new Todo(1, "TestingSearch", false, 1);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        //when
        ResultActions result = mvc.perform(delete("/todos/{todo-id}", 1L))  ;

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void should_return_NOT_FOUND_when_user_is_invalid_or_not_existing() throws Exception {
        //Given

        //when
        ResultActions result = mvc.perform(delete("/todos/{todo-id}", 1L))  ;

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_OK_and_updated_todo_when_given_correct_id_and_updated_todo() throws Exception {
        //Given
        Todo todo = new Todo(1, "TestingSearch", false, 1);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        //when
        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(todo)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void should_return_not_found_if_old_todo_is_not_found() throws Exception {
        //Given
        Todo todo = new Todo(1, "TestingSearch", false, 1);

        //when
        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(todo)));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void should_return_bad_request_if_new_todo_is_not_given() throws Exception {
        //Given
        Todo todo = new Todo(1, "TestingSearch", false, 1);

        //when
        ResultActions result = mvc.perform(patch("/todos/{todo-id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest());
    }



    public static String asJsonString(final Todo obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}