package com.example.loginspring.Config;

import com.example.loginspring.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //Necesario para evitar que la seguridad se aplique a los resources
    //Como los css, imagenes y javascripts
    String[] resources = new String[]{
            "/include/**","/css/**","/icons/**","/img/**","/js/**","/layer/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //le decimos a Http que vamos a configurar los request(solicitudes)
                .authorizeRequests()
                //todos los que tengan el permit.all despues, pueden acceder a las url, o lo que este anteriormente
                .antMatchers(resources).permitAll()
                //en la "/" raiz o en el "index" cualquiera pueda entrar
                .antMatchers("/","/index").permitAll()
                //todos lo que empiece por admin, tiene que tener el rol de admin
                .antMatchers("/admin*").access("hasRole('ADMIN')")
                .antMatchers("/user*").access("hasRole('USER') or hasRole('ROLE_ADMIN')")
                //cualquier otra request que no sea user o admin tiene que estar autenticada
                    .anyRequest().authenticated()
                //con el ".and()" le decimos que queremos configurar el formulario de login
                    .and()
                //
                .formLogin()
                //siempre nos llevara a la URL de ("/login")
                    .loginPage("/login")
                    .permitAll()
                //si se logea exitosamente nos lleva a menu
                    .defaultSuccessUrl("/menu")
                //si no se logea exitosamente nos lleva a login nuevamente
                    .failureUrl("/login?error=true")
                //nombre del parametro del formulario
                    .usernameParameter("username")
                //nombre para el parametro contraseña del formulario
                    .passwordParameter("password")

                    .and()
                .logout()
                //cualquiera se puede desloguear
                    .permitAll()
                // y hacias donde nos llevara al desloguearse
                //hacia el parametro /login?logout
                    .logoutSuccessUrl("/login?logout");
    }


    //Crea el encriptador de contraseñas

    //metodo constructor



    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}



