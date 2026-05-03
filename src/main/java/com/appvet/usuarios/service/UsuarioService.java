package com.appvet.usuarios.service;

import com.appvet.usuarios.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> obtenerTodos();
    Optional<Usuario> obtenerPorId(String id);
    Optional<Usuario> obtenerPorEmail(String email);
    Usuario registrar(Usuario usuario);
    Usuario actualizar(String id, Usuario usuario);
    void eliminar(String id);
    boolean existeEmail(String email);
    Optional<Usuario> login(String email, String password);
}