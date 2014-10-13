package nl.ewjmulder.programyourhome.server;

import nl.ewjmulder.programyourhome.ComponentScanBase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
public class ProgramYourHomeServer {

    public static void main(final String[] args) {
        SpringApplication.run(ProgramYourHomeServer.class, args);
    }

}
