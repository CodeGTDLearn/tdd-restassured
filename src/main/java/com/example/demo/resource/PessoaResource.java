package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.exceptions.CpfDuplicadoException;
import com.example.demo.servico.exceptions.PessoaNaoEncontradaException;
import com.example.demo.servico.exceptions.TelDuplicadoException;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;
import com.example.demo.servico.utils.ErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

    @Autowired
    private PessoaServiceInt service;

    @GetMapping("/{ddd}/{numero}")
    public ResponseEntity<Pessoa> findByTel(@PathVariable("ddd") String ddd,
                                            @PathVariable("numero") String numero)
            throws TelephoneNotFoundException {
        final Telefone telefone = new Telefone();
        telefone.setDdd(ddd);
        telefone.setNumero(numero);

        final Pessoa pessoa = service.findByTelephone(telefone);

        //OK: org.springframework.http.HttpStatus.*;
        return new ResponseEntity<>(pessoa, OK);

    }

    @PostMapping
    public ResponseEntity<Pessoa> save(@RequestBody Pessoa pessoa, HttpServletResponse response) throws TelDuplicadoException, CpfDuplicadoException {

        Pessoa pessoaToBeSaved = service.save(pessoa);

        final String ddd = pessoaToBeSaved.getTelefones().get(0).getDdd();
        final String numero = pessoaToBeSaved.getTelefones().get(0).getNumero();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{ddd}/{numero}")
                .buildAndExpand(ddd, numero).toUri();

        response.setHeader("Location", uri.toASCIIString());

        return new ResponseEntity<>(pessoaToBeSaved, CREATED);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> put(@RequestBody Pessoa pessoa,
                                          @PathVariable("codigo") String codigo) throws PessoaNaoEncontradaException {
        Pessoa pessoaPut = service.put(pessoa, codigo);
        return new ResponseEntity<>(pessoaPut, OK);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<Pessoa>> findByMultiFilterCascade(@RequestBody FiltroPessoaCascade filtro){
        final List<Pessoa> listPessoas = service.findByMultiFilterCascade(filtro);
        return new ResponseEntity<>(listPessoas, OK);
    }

    @GetMapping
    List<Pessoa> findAllMockmvc (){
        return service.findAllMockmvc();
    }

    @ExceptionHandler({TelephoneNotFoundException.class})
    public ResponseEntity<ErrorException> handleTelNotFoundException(TelephoneNotFoundException exceptFromService) {

        //NOT_FOUND: org.springframework.http.HttpStatus.*;
        return new ResponseEntity<>(new ErrorException(exceptFromService.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler({CpfDuplicadoException.class})
    public ResponseEntity<ErrorException> handleCpfDuplicadoException(CpfDuplicadoException exceptFromService) {

        //BAD_REQUEST: org.springframework.http.HttpStatus.*;
        return new ResponseEntity<>(new ErrorException(exceptFromService.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler({TelDuplicadoException.class})
    public ResponseEntity<ErrorException> handleTelDuplicadoException(TelDuplicadoException exceptFromService) {

        //BAD_REQUEST: org.springframework.http.HttpStatus.*;
        return new ResponseEntity<>(new ErrorException(exceptFromService.getMessage()), BAD_REQUEST);
    }
}
