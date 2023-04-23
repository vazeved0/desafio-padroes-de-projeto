package br.com.doamor.api.service;

import br.com.doamor.api.model.Usuario;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;

public interface UsuarioService {
    Iterable<Usuario> buscarTodos();
    Usuario buscarPorId(Long id);


    void inserir(Usuario usuario);
    void atualizar(Long id, Usuario usuario);

    void deletar(Long id);
}
