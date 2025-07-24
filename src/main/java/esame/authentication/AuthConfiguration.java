package esame.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static esame.model.Credentials.ADMIN_ROLE;
import static esame.model.Credentials.DEFAULT_ROLE;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
				.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
				// DISABILITA CSRF e CORS (valuta se ti servono davvero)
				.csrf().disable().cors().disable()

				.authorizeHttpRequests()

				// 1) risorse pubbliche (home, login, register, static)
				.requestMatchers(HttpMethod.GET, "/", "/index", "/login", "/register", "/css/**", "/images/**",
						"/favicon.ico","/ricercaHome","/showAllLibri","/showAllAutori")
				.permitAll()

				// 2) GET consultazione libri, immagini, autori (pubblico)
				.requestMatchers(HttpMethod.GET, "/libro/**", "/libro/immagine/**", "/autore/**","/admin/**","/ricercaHome").permitAll()
				.requestMatchers(HttpMethod.POST, "/libro/**", "/libro/immagine/**", "/autore/**","/admin/**","/ricercaHome","/register").permitAll()

				// 3) area “post‐login” – solo loggati (ROLE_USER e ROLE_ADMIN)
				.requestMatchers(HttpMethod.GET, "/success", "/profile","/admin/**","/libro/**", "/libro/immagine/**").authenticated()

				// 4) amministrazione – solo ADMIN
				.requestMatchers("/admin/**","/autore/**","/libro/**").hasAuthority(ADMIN_ROLE)

				// 5) qualunque altra richiesta richiede autenticazione
				.anyRequest().authenticated()

				.and().formLogin().loginPage("/login").permitAll()
				// redirect DOPO login: vai su /success (che abbiamo aperto a chi è autenticato)
				.defaultSuccessUrl("/success", true).failureUrl("/login?error=true")

				.and().logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").permitAll();

		return http.build();
	}
}
