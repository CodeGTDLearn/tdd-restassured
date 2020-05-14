package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.PessoaRepo;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.exceptions.PessoaComTelDuplicado;
import com.example.demo.servico.exceptions.TelephoneNotFound;
import com.example.demo.servico.impl.PessoaServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.example.demo.databuilders.PessoaBuilder.pessoaComCpfeTel;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class PessoaServiceTest {

    @MockBean
    private PessoaRepo repo;

    private PessoaServiceInt service;

    private Pessoa pessoa;

    private Telefone tel;

    @Before
    public void setUp() {
        service = new PessoaServiceImpl(repo);
        pessoa = pessoaComCpfeTel().gerar();
    }

    @Test
    public void savingPersonInRepo() throws PessoaComCpfDuplicado, PessoaComTelDuplicado {
        service.save(pessoa);
        verify(repo).save(pessoa);
    }

    @Test(expected = PessoaComCpfDuplicado.class)
    public void blockingSaveMethodInPeopleWithCpfDuplicity() throws PessoaComCpfDuplicado, PessoaComTelDuplicado {
        when(repo.findByCpf(pessoa.getCpf())).thenReturn(Optional.of(pessoa));
        service.save(pessoa);
    }

    @Test(expected = PessoaComTelDuplicado.class)
    public void blockingSaveMethodInPeopleWithTelDuplicity() throws PessoaComTelDuplicado, PessoaComCpfDuplicado {
        String ddd = pessoa.getTelefones().get(0).getDdd();
        String numero = pessoa.getTelefones().get(0).getNumero();
        when(repo.findByTel(ddd, numero)).thenReturn(Optional.of(pessoa));
        service.save(pessoa);

    }

    @Test
    public void findPersonByTelephone() throws TelephoneNotFound {
        Pessoa person = service.findByTelephone(pessoaComCpfeTel().gerar().getTelefones().get(0));
    }
}