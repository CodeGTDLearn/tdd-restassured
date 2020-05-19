package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.exceptions.CpfDuplicadoException;
import com.example.demo.servico.exceptions.TelDuplicadoException;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;
import com.example.demo.servico.impl.PessoaServiceImpl;
import com.example.demo.servico.utils.CreateMessage;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.demo.databuilders.PessoaBuilder.pessoaComCpfeTel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource("classpath:application.properties")
@Sql(value = "/load-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DataJpaTest
@RunWith(SpringRunner.class)
public class PessoaServiceTest {

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
        FiltroPessoaCascade filtro = FiltroPessoaCascade.builder()
                .nome("xxx")
                .cpf("4444")
                .ddd("99")
                .phone("38416516").build();

        Pessoa personResult1 = new Pessoa();
        Pessoa personResult2 = new Pessoa();
        personResult1.setNome("paulo");
        personResult2.setNome("vinicius");

        when(repo.findByMultiFilterCascade(
                filtro.getNome(),
                filtro.getCpf(),
                filtro.getDdd(),
                filtro.getPhone())).thenReturn(Arrays.asList(personResult1, personResult2));

        List<Pessoa> pessoas =
                service.findByMultiFilterCascade(filtro);

        AssertionsForClassTypes.assertThat(pessoas.size()).isEqualTo(2);
    }

    @Test(expected = CpfDuplicadoException.class)
    public void blockSaveWithCpfDuplicity() throws CpfDuplicadoException, TelDuplicadoException {
        when(repo.findByCpf(pessoa.getCpf())).thenReturn(Optional.of(pessoa));
        service.save(pessoa);
    }

    @Test(expected = TelDuplicadoException.class)
    public void blockSaveWithTelDuplicity() throws TelDuplicadoException, CpfDuplicadoException {
        String ddd = pessoa.getTelefones().get(0).getDdd();
        String numero = pessoa.getTelefones().get(0).getNumero();
        when(repo.findByTelDdd(ddd, numero)).thenReturn(Optional.of(pessoa));
        service.save(pessoa);

    }

    //---Teste das EXCECOES:
    //      - As excecoes ECLODEM no service, portanto devem ser testadas no ServiceTest
    //---TESTANDO AS 03 MODALIDADES DE TESTE DE EXCECAO:
    //A) Expected
    @Test(expected = TelephoneNotFoundException.class)
    public void telephoneNotFoundException_ExpectedMethod() throws TelephoneNotFoundException {
        tel = pessoaComCpfeTel().gerar().getTelefones().get(0);
        service.findByTelephone(tel);
    }

    //B) Generic
    @Test
    public void telephoneNotFoundException_GenericMethod() throws Exception {
        tel = pessoaComCpfeTel().gerar().getTelefones().get(0);
        expExc.expect(TelephoneNotFoundException.class);
        expExc.expectMessage(CreateMessage.builder()
                .text1(tel.getDdd())
                .text2(tel.getNumero())
                .build()
                .telNotFound());
        service.findByTelephone(tel);
    }

    //C) Robust
    @Test
    public void telephoneNotFoundException_RobustMethod() {
        tel = pessoaComCpfeTel().gerar().getTelefones().get(0);
        try {
            service.findByTelephone(tel);
            fail();
        } catch (TelephoneNotFoundException erro) {

            //Assert.assertThat: org.junit.Assert;
            //IS: org.hamcrest.CoreMatchers;
            Assert.assertThat(erro.getMessage(), CoreMatchers.is(CreateMessage.builder()
                    .text1(tel.getDdd())
                    .text2(tel.getNumero())
                    .build()
                    .telNotFound()));
        }
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

    //TESTE DE EXCECAO: GenericMethod
    @Test
    public void cpfDuplicityException_GenericMethod() throws CpfDuplicadoException, TelDuplicadoException {
        when(repo.findByCpf(pessoa.getCpf())).thenReturn(Optional.of(pessoa));
        expExc.expect(CpfDuplicadoException.class);
        expExc.expectMessage(CreateMessage.builder()
                .text1(pessoa.getCpf())
                .build()
                .cpfDuplicity());

        service.save(pessoa);
    }

    //TESTE DE EXCECAO: GenericMethod
    @Test
    public void telDuplicityException_GenericMethod() throws CpfDuplicadoException, TelDuplicadoException {
        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();

        when(repo.findByTelDdd(ddd, numero)).thenReturn(Optional.of(pessoa));
        expExc.expect(TelDuplicadoException.class);
        expExc.expectMessage(CreateMessage.builder()
                .text1(ddd)
                .text2(numero)
                .build()
                .telDuplicity());

        service.save(pessoa);
    }

}