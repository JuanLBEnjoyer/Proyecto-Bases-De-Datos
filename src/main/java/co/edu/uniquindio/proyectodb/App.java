package co.edu.uniquindio.proyectodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del Torneo de Fútbol expuesta como API REST de Spring Boot.
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
        System.out.println("¡Servidor del Torneo de Fútbol corriendo en http://localhost:8080!");
    }
}
