package org.generation.minhaLojaDeGames.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.generation.minhaLojaDeGames.model.Usuario;
import org.generation.minhaLojaDeGames.model.UserLogin;
import org.generation.minhaLojaDeGames.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		Optional<Usuario> usuarioExistente = repository.findByEmail(usuario.getEmail());
		if (usuarioExistente.isPresent()) {
			return Optional.empty();
		} else {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String senhaCriptografada = encoder.encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
			return Optional.ofNullable(repository.save(usuario));
		}
	}

	public Optional<UserLogin> logarUsuario(Optional<UserLogin> usuarioLogin) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuarioPresente = repository.findByEmail(usuarioLogin.get().getEmail());

		if (usuarioPresente.isPresent()) {
			if (encoder.matches(usuarioLogin.get().getSenha(), usuarioPresente.get().getSenha())) {
				String auth = usuarioLogin.get().getEmail() + ":" + usuarioLogin.get().getSenha();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);

				usuarioLogin.get().setToken(authHeader);
				usuarioLogin.get().setNome(usuarioPresente.get().getNome());
				usuarioLogin.get().setSenha(usuarioPresente.get().getSenha());

				return usuarioLogin;
			}
		}
		return null;
	}
}
