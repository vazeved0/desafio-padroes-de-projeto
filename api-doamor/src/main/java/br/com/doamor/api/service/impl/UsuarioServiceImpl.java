package br.com.doamor.api.service.impl;

import br.com.doamor.api.model.Endereco;
import br.com.doamor.api.model.Usuario;
import br.com.doamor.api.repository.EnderecoRepository;
import br.com.doamor.api.repository.UsuarioRepository;
import br.com.doamor.api.service.UsuarioService;
import br.com.doamor.api.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // Singleton: Injetar os componentes do Spring com @Autowired.
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;


    @Override
    public Iterable<Usuario> buscarTodos() {

        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.get();
    }

    @Override
    public void inserir(Usuario usuario) {
        // Verifica se o endereço do cliente ja existe(Pelo CEP)
        salvarUsuarioComCep(usuario);

    }



    @Override
    public void atualizar(Long id, Usuario usuario) {
        Optional<Usuario>usuarioBD = usuarioRepository.findById(id);
        if(usuarioBD.isPresent()) {
            // Verifica se o endereço do cliente ja existe(Pelo CEP)
            salvarUsuarioComCep(usuario);
        }

    }

    @Override
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);

    }

    private Endereco salvarUsuarioComCep(Usuario usuario) {
        String cep = usuario.getEndereco().getCep();
        Long idEndereco = usuario.getEndereco().getId();
        if(idEndereco != null && idEndereco != 0){
            Optional<Endereco> enderecoOptional = enderecoRepository.findById(idEndereco);
            Endereco endereco = enderecoOptional.get();
            if(enderecoOptional.isPresent()){
                if (endereco.getCep() != usuario.getEndereco().getCep()){
                    Endereco novoCep = viaCepService.consultaCep(cep);
                    endereco.setUf(novoCep.getUf());
                    endereco.setCep(usuario.getEndereco().getCep());
                    endereco.setLocalidade(novoCep.getLocalidade());
                    endereco.setLogradouro(novoCep.getLogradouro());
                    endereco.setBairro(novoCep.getBairro());
                    endereco.setComplemento(novoCep.getComplemento());
                    endereco.setNumero(usuario.getEndereco().getNumero());
                    enderecoRepository.save(endereco);
                    usuario.setEndereco(endereco);
                    usuarioRepository.save(usuario);
                    return endereco;
                }else {
                    endereco.setNumero(usuario.getEndereco().getNumero());
                    enderecoRepository.save(endereco);
                    usuario.setEndereco(endereco);
                    usuarioRepository.save(usuario);
                    return endereco;
                }

            }
        }

        try {
            // Se o endereço não existir, vai buscar no ViaCEP, salvar na tabela e retornar novo endereço
            Endereco novoEndereco = viaCepService.consultaCep(cep);
            // Insere o número passado pelo usuário
            novoEndereco.setNumero(usuario.getEndereco().getNumero());
            enderecoRepository.save(novoEndereco);
            // Insere o usuário, vinculando o endereço (novo ou existente)
            usuario.setEndereco(novoEndereco);
            usuarioRepository.save(usuario);
            return novoEndereco;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço no ViaCEP: " + e.getMessage());
        }
    }
}
