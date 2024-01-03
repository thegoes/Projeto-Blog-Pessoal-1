package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private TemaRepository temaRepository;

    @GetMapping
    public ResponseEntity<List<Postagem>> getAll() {
        List<Postagem> postagens = postagemRepository.findAll();
        return ResponseEntity.ok(postagens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Postagem> getById(@PathVariable Long id) {
        Optional<Postagem> postagem = postagemRepository.findById(id);
        return postagem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
        List<Postagem> postagens = postagemRepository.findAllByTituloContainingIgnoreCase(titulo);
        return ResponseEntity.ok(postagens);
    }

    @PostMapping
    public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
        Postagem novaPostagem = postagemRepository.save(postagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPostagem);
    }

    @PutMapping
    public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
        Long temaId = postagem.getTema().getId();
        if (temaRepository.existsById(temaId)) {
            Optional<Postagem> optionalPostagem = postagemRepository.findById(postagem.getId());
            if (optionalPostagem.isPresent()) {
                Postagem postagemAtualizada = optionalPostagem.get();
                postagemAtualizada.setTitulo(postagem.getTitulo());
                postagemAtualizada.setTexto(postagem.getTexto());
                postagemAtualizada.setData(postagem.getData());
                postagemAtualizada.setTema(postagem.getTema());
                Postagem novaPostagem = postagemRepository.save(postagemAtualizada);
                return ResponseEntity.ok(novaPostagem);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Optional<Postagem> postagem = postagemRepository.findById(id);
        if (postagem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        postagemRepository.deleteById(id);
    }
}
