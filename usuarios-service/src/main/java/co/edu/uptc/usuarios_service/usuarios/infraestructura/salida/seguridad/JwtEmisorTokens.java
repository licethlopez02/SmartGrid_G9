package co.edu.uptc.usuarios_service.usuarios.infraestructura.salida.seguridad;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import co.edu.uptc.usuarios_service.usuarios.aplicacion.EmisorTokens;
import co.edu.uptc.usuarios_service.usuarios.aplicacion.TokenAcceso;
import co.edu.uptc.usuarios_service.usuarios.dominio.TipoRol;
import co.edu.uptc.usuarios_service.usuarios.dominio.Usuario;

@Component
public class JwtEmisorTokens implements EmisorTokens {
    private final JwtEncoder encoder;
    private final long expiracionSegundos;

    public JwtEmisorTokens(JwtEncoder encoder, @Value("${jwt.expiracion-minutos}") long minutos) {
        this.encoder = encoder;
        this.expiracionSegundos = minutos * 60;
    }

    @Override
    public TokenAcceso emitir(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("usuarios-service")
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(expiracionSegundos))
                .claim("correo", usuario.getCorreo())
                .claim("roles", usuario.getRoles().stream().map(TipoRol::nombre).toList())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        return new TokenAcceso(token, expiracionSegundos);
    }
}