package com.aewinformatica.seguranca.repository.helper.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aewinformatica.seguranca.model.Usuario;
import com.aewinformatica.seguranca.repository.filter.UsuarioFilter;

public interface UsuariosQueries {

   public Optional<Usuario> porEmailEAtivo(String email);
	
	public List<String> permissoes(Usuario usuario);
	
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable);
	
	public Optional<Usuario> buscarComGrupos(Long codigo);
}
