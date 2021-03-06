package hr.excilys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

import hr.excilys.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserService userService;
	
	@Autowired
	public WebSecurityConfig(UserService userService) {

		this.userService = userService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/addComputer", "/editComputer").hasAnyRole("ADMIN", "USER");
		http.authorizeRequests().antMatchers("/delete").hasRole("ADMIN");
		http.authorizeRequests().antMatchers("/api/*").hasAnyRole("ADMIN", "USER").and().authorizeRequests()
				.antMatchers("/api/delete").hasRole("ADMIN");
		http.formLogin().loginPage("/login").failureUrl("/login?error=true");
		http.logout().logoutUrl("/logout").logoutSuccessUrl("/dashboard").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").clearAuthentication(true);
		http.exceptionHandling(e -> e.authenticationEntryPoint(digestEntryPoint())).addFilter(digestAuthFilter(digestEntryPoint(), userService));

		http.cors().and().csrf().disable();
	}

	@Autowired
	public void userConfigurationGlobal(AuthenticationManagerBuilder authentificationManagerBuilder) throws Exception {

		authentificationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public DigestAuthenticationEntryPoint digestEntryPoint() {

		DigestAuthenticationEntryPoint entryPoint = new DigestAuthenticationEntryPoint();
		entryPoint.setRealmName("ENTRYPOINT CDB");
		entryPoint.setKey("keyCDB++");
		entryPoint.setNonceValiditySeconds(1);

		return entryPoint;
	}

	@Bean
	public DigestAuthenticationFilter digestAuthFilter(DigestAuthenticationEntryPoint entryPoint,
			UserDetailsService userDetailsService) {

		DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
		filter.setUserDetailsService(userDetailsService);
		filter.setAuthenticationEntryPoint(entryPoint);

		return filter;
	}
}
