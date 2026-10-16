package com.smarthospital;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class HospitalConsoleRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (System.console() != null || (args != null && Arrays.asList(args).contains("--cli"))) {
            HospitalEngineDemo.runDemo();
        }
    }
}
