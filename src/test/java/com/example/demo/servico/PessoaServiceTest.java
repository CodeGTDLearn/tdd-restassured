package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepo;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.impl.PessoaServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.example.demo.databuilders.PessoaBuilder.pessoaComCpf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PessoaServiceTest {

    @MockBean
    private PessoaRepo repo;

    private PessoaService service;

    private Pessoa pessoa;

    @Before
    public void setUp() {
        service = new PessoaServiceImpl(repo);
        pessoa = pessoaComCpf().gerar();
    }

    @Test
    public void savingPersonInRepo() throws PessoaComCpfDuplicado {
        service.save(pessoa);
        verify(repo).save(pessoa);
    }

    @Test(expected = PessoaComCpfDuplicado.class)
    public void blockingSaveMethosInPeopleWithDuplicity() throws PessoaComCpfDuplicado {
        when(repo.findByCpf(pessoa.getCpf())).thenReturn(Optional.of(pessoa));
        service.save(pessoa);
    }
}