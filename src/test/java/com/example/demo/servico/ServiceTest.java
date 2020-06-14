package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;
import com.example.demo.servico.impl.PessoaServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpfeTel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@Sql(value = "/data-mass-load.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/data-mass-clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
@RunWith(SpringRunner.class)
public class ServiceTest {

    /*
    ATENCAO!!!!
    O 'USO MOCKADO'(ANOTACAO MOCKBEAN) DE UMA CLASSE/INTERFACE/INJECAO,
    PROPAGARA O 'TEOR' DESSE MOCK-OBJECT
    P/ TDS OS OBJETOS DO MESMO TIPO, DENTRO DO TESTE.
    DIFERENTES OBJETOS/INJECOES, RECEBERAM OBRIGATORIAMENTE CONTEUDO DO MOCK-OBJECT,
    AINDA QUE SEJAM INSTANCIAS DIFERENTES!
     */
    @MockBean
    private PessoaRepoInt repo;

    private PessoaServiceInt service;

    @Rule
    public ExpectedException expExc = ExpectedException.none();

    private Pessoa pessoa;
    private Telefone tel;

    @Before
    public void setUp() {
        service = new PessoaServiceImpl(repo);
        pessoa = pessoaComCpfeTel().gerar();
    }

    @Test
    public void multiFilterTest() {
        FiltroPessoaCascade filtro = FiltroPessoaCascade.builder().build();

        Pessoa personResult1 = pessoaComCpfeTel().gerar();
        Pessoa personResult2 = pessoaComCpfeTel().gerar();

        when(repo.findByMultiFilterCascade(
                filtro.getNome(),
                filtro.getCpf(),
                filtro.getDdd(),
                filtro.getPhone())).thenReturn(Arrays.asList(personResult1, personResult2));

        List<Pessoa> pessoas = service.findByMultiFilterCascade(filtro);

        AssertionsForClassTypes.assertThat(pessoas.size()).isEqualTo(2);
        assertThat(personResult1.getNome()).isEqualTo(pessoas.get(0).getNome());
        assertThat(personResult2.getNome()).isEqualTo(pessoas.get(1).getNome());
    }

    @Test
    public void findPersonByTelephone() throws TelephoneNotFoundException {
        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();

        when(repo.findByTelDdd(ddd, numero)).thenReturn(Optional.of(pessoa));

        Pessoa person = service.findByTelephone(pessoa.getTelefones().get(0));

        verify(repo).findByTelDdd(ddd, numero);

        //Library: org.assertj.core.api.Assertions.assertThat;
        assertThat(person).isNotNull();
        assertThat(person.getNome()).isEqualTo(person.getNome());
        assertThat(person.getCpf()).isEqualTo(person.getCpf());
        assertThat(person.getTelefones().get(0).getNumero()).isEqualTo(numero);

    }

    @Test
    public void findAllMockmvc() {
        List<Pessoa> list = service.findAllMockmvc();
        AssertionsForClassTypes.assertThat(list.size()).isEqualTo(list.size());
    }

    @Test
    public void findByTelDdd() {
        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();

        when(repo.findByTelDdd(ddd, numero)).thenReturn(Optional.of(pessoa));

        Pessoa person = service.findByTelDdd(ddd, numero).get();

        verify(repo).findByTelDdd(ddd, numero);

        //Library: org.assertj.core.api.Assertions.assertThat;
        assertThat(person).isNotNull();
        assertThat(person.getNome()).isEqualTo(person.getNome());
        assertThat(person.getCpf()).isEqualTo(person.getCpf());
        assertThat(person.getTelefones().get(0).getNumero()).isEqualTo(numero);
    }
}