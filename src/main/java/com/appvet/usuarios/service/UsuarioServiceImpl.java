package com.appvet.usuarios.service;

import com.appvet.usuarios.model.Usuario;
import com.appvet.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(String id) {
        log.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        return usuarioRepository.findByEmail(email);
    }
    
    @Override
    public Usuario registrar(Usuario usuario) {
        log.info("Registrando usuario: {}", usuario.getEmail());
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + usuario.getEmail());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Override
    public Usuario actualizar(String id, Usuario usuarioActualizado) {
        log.info("Actualizando usuario con ID: {}", id);
        
        return usuarioRepository.findById(id)
            .map(usuarioExistente -> {
                usuarioExistente.setNombre(usuarioActualizado.getNombre());
                usuarioExistente.setRol(usuarioActualizado.getRol());
                usuarioExistente.setFotoPerfilUri(usuarioActualizado.getFotoPerfilUri());
                
                // Solo actualizar password si se proporciona uno nuevo
                if (usuarioActualizado.getPassword() != null && 
                    !usuarioActualizado.getPassword().isEmpty()) {
                    usuarioExistente.setPassword(usuarioActualizado.getPassword());
                }
                
                return usuarioRepository.save(usuarioExistente);
            })
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }
    
    @Override
    public void eliminar(String id) {
        log.info("Eliminando usuario con ID: {}", id);
        
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        usuarioRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> login(String email, String password) {
        log.info("Intento de login para email: {}", email);
        return usuarioRepository.findByEmailAndPassword(email, password);
    }
}