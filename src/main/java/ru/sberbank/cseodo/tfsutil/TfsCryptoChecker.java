package ru.sberbank.cseodo.tfsutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sberbank.cseodo.tfsutil.checker.SignChecker;

@SpringBootApplication
@EnableScheduling
public class TfsCryptoChecker implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(TfsCryptoChecker.class);

    @Autowired
    private SignChecker signChecker;

    public static void main(String[] args) {
        SpringApplication.run(TfsCryptoChecker.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {
        LOG.info("Чтение и загрузка свойств");
        signChecker.prepareCryptoLibrary();
        signChecker.signChecker();
    }
}
