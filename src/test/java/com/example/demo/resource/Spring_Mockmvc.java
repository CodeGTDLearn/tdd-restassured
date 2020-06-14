package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.servico.PessoaServiceInt;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.var;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpfeTel;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class Spring_Mockmvc extends GlobalTestConfig {

    @Autowired
    PessoaServiceInt service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findAllMockmvcRequestAnswer() throws Exception {
        final ResultActions perform = mockMvc.perform(get("/pessoas"));
        perform.andExpect(status().isOk());
        perform.andExpect(status().is(200))
                .andDo(print());
    }

    @Test
    public void findAllMockmvcListSize() throws Exception {
        List<Pessoa> listPessoas = service.findAllMockmvc();
        var perform = mockMvc.perform(get("/pessoas"));
        perform.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        perform.andExpect(jsonPath("$" ,hasSize(listPessoas.size())))
                .andDo(print());
    }

    @Test
    public void findAllMockmvcCheckContent() throws Exception {
        var listPessoas = service.findAllMockmvc();

        String nome1 = listPessoas.get(0).getNome();
        String nome2 = listPessoas.get(1).getNome();

        mockMvc.perform(get("/pessoas"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value(nome1))
                .andExpect(jsonPath("$[1].nome").value(nome2))
                .andDo(print());
    }

    @Test
    public void postMockMvc() throws Exception {
        Pessoa person = pessoaComCpfeTel().gerar();

        final String nome = person.getNome();
        final String ddd = person.getTelefones().get(0).getDdd();

        mockMvc.perform(post("/pessoas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.telefones[0].ddd").value(ddd))
                .andDo(print());
    }

    @Test
    public void findByDddAndTel() throws Exception {
        var listPessoas = service.findAllMockmvc();

        final String ddd = listPessoas.get(0).getTelefones().get(0).getDdd();
        final String tel = listPessoas.get(0).getTelefones().get(0).getNumero();

        Pessoa person = service.findByTelDdd(ddd ,tel).get();

        final String nome = person.getNome();

        mockMvc.perform(get("/pessoas/{ddd}/{numero}" ,ddd ,tel))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.telefones[0].ddd").value(ddd))
                .andDo(print());
    }
}

