package com.programyourhome.environment.mock;

import org.springframework.context.annotation.Configuration;

import com.programyourhome.pc.PcInstructor;

@Configuration
public class PcInstructorMock extends PyhMock {

    // @Bean
    public PcInstructor createPcInstructorMock() {
        return this.createMock(PcInstructor.class);
    }
}
