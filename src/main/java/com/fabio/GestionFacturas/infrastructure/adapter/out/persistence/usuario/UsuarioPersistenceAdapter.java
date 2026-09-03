package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity entidad = UsuarioMapper.aEntidad(usuario);
        UsuarioJpaEntity guardada = usuarioJpaRepository.save(entidad);
        return UsuarioMapper.aDominio(guardada);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioJpaRepository.findById(id).map(UsuarioMapper::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(UsuarioMapper::aDominio);
    }
}
