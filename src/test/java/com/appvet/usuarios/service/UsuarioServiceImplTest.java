package com.appvet.usuarios.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.appvet.usuarios.model.Usuario;
import com.appvet.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para UsuarioServiceImpl")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario(
            "1", 
            "Juan Pérez", 
            "juan@test.com", 
            "123456", 
            "Cliente"
        );
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    void debeObtenerTodosLosUsuarios() {
        // Given - Preparar datos
        List<Usuario> usuarios = Arrays.asList(
            usuarioTest,
            new Usuario("2", "María López", "maria@test.com", "123456", "Cliente")
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When - Ejecutar método
        List<Usuario> resultado = usuarioService.obtenerTodos();

        // Then - Verificar resultados
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener usuario por ID cuando existe")
    void debeObtenerUsuarioPorIdCuandoExiste() {
        // Given
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioTest));

        // When
        Optional<Usuario> resultado = usuarioService.obtenerPorId("1");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Juan Pérez", resultado.get().getNombre());
        assertEquals("juan@test.com", resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando usuario no existe")
    void debeRetornarOptionalVacioCuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = usuarioService.obtenerPorId("999");

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Debe registrar usuario cuando email no existe")
    void debeRegistrarUsuarioCuandoEmailNoExiste() {
        // Given
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        // When
        Usuario resultado = usuarioService.registrar(usuarioTest);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@test.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).existsByEmail("juan@test.com");
        verify(usuarioRepository, times(1)).save(usuarioTest);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando email ya existe")
    void debeLanzarExcepcionCuandoEmailYaExiste() {
        // Given
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrar(usuarioTest);
        });

        assertTrue(exception.getMessage().contains("El email ya está registrado"));
        verify(usuarioRepository, times(1)).existsByEmail("juan@test.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar usuario cuando existe")
    void debeActualizarUsuarioCuandoExiste() {
        // Given
        Usuario usuarioActualizado = new Usuario(
            "1", 
            "Juan Pérez Actualizado", 
            "juan@test.com", 
            "123456", 
            "Admin"
        );
        
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioTest));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        // When
        Usuario resultado = usuarioService.actualizar("1", usuarioActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Pérez Actualizado", resultado.getNombre());
        assertEquals("Admin", resultado.getRol());
        verify(usuarioRepository, times(1)).findById("1");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar usuario que no existe")
    void debeLanzarExcepcionAlActualizarUsuarioQueNoExiste() {
        // Given
        when(usuarioRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizar("999", usuarioTest);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).findById("999");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar usuario cuando existe")
    void debeEliminarUsuarioCuandoExiste() {
        // Given
        when(usuarioRepository.existsById("1")).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById("1");

        // When
        assertDoesNotThrow(() -> usuarioService.eliminar("1"));

        // Then
        verify(usuarioRepository, times(1)).existsById("1");
        verify(usuarioRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar usuario que no existe")
    void debeLanzarExcepcionAlEliminarUsuarioQueNoExiste() {
        // Given
        when(usuarioRepository.existsById("999")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminar("999");
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).existsById("999");
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe realizar login exitoso con credenciales correctas")
    void debeRealizarLoginExitosoConCredencialesCorrectas() {
        // Given
        when(usuarioRepository.findByEmailAndPassword("juan@test.com", "123456"))
            .thenReturn(Optional.of(usuarioTest));

        // When
        Optional<Usuario> resultado = usuarioService.login("juan@test.com", "123456");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Juan Pérez", resultado.get().getNombre());
        verify(usuarioRepository, times(1))
            .findByEmailAndPassword("juan@test.com", "123456");
    }

    @Test
    @DisplayName("Debe fallar login con credenciales incorrectas")
    void debeFallarLoginConCredencialesIncorrectas() {
        // Given
        when(usuarioRepository.findByEmailAndPassword("juan@test.com", "wrong"))
            .thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = usuarioService.login("juan@test.com", "wrong");

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1))
            .findByEmailAndPassword("juan@test.com", "wrong");
    }
}