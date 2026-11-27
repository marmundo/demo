package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegistroRequestDTO;
import com.example.demo.dto.TokenResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")   // rotas públicas de entrada: liberadas no SecurityConfig, pois ninguém tem token ainda
public class AuthController {

    // Coordena todo o processo de autenticação (busca o usuário e confere a senha)
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    // Usado no cadastro para gerar o hash da senha
    private final PasswordEncoder passwordEncoder;
    // Responsável por criar o JWT depois que o login der certo
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dados) {
        // 1. Empacota e-mail e senha no formato que o Spring Security entende (ainda NÃO autenticado)
        var credenciais = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        // 2. Delega a verificação. Se a senha estiver errada, lança exceção e o fluxo para aqui (401)
        var autenticacao = authenticationManager.authenticate(credenciais);
        // 3. Chegou aqui, então as credenciais são válidas. getPrincipal() devolve o Usuario carregado do banco
        var token = tokenService.gerarToken((Usuario) autenticacao.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody RegistroRequestDTO dados) {
        // E-mail é o login, então não pode haver dois iguais: responde 409 (conflito)
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        // BCrypt é uma via de mão única: não dá para recuperar a senha original a partir do hash
        var senhaHash = passwordEncoder.encode(dados.senha());
        usuarioRepository.salvar(new Usuario(dados.nome(), dados.email(), senhaHash, dados.perfil()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
