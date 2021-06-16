// Обработка файлов ЭЦП из TFS
package ru.sberbank.cseodo.tfsutil.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.infocrypt.icbicrypttools.*;
import ru.sberbank.cseodo.tfsutil.config.ParamConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SignChecker {
    private static final Logger LOG = LoggerFactory.getLogger(ParamConfig.class);

    private ICBicryptTools cryptoTool;
    private PublicKeyBase pkb, admPkb;

    @Autowired
    private ParamConfig paramConfig;

    // Подготовить криптографическую библиотеку
    public void prepareCryptoLibrary() {
        String osName = System.getProperty("os.name");
        LOG.info(String.format("Операционная система: %s", osName));

        cryptoTool = new ICBicryptTools();
        try {
            cryptoTool.installFiles(paramConfig.getBICRYPT_SPECIFIC_LIBRARY());
            // Системные библиотеки
            if(osName.contains("Windows")) {
                System.loadLibrary("cms80_64");
                System.loadLibrary("asn1pars_64");
                System.loadLibrary("cryptox509_64");
                System.loadLibrary("bicr5_64");
                System.loadLibrary("Grn64");
            }
            // Базы открытых ключей
            pkb = new PublicKeyBase(paramConfig.getPUBLIC_KEYS());
            admPkb = new PublicKeyBase(paramConfig.getADM_PUBLIC_KEYS());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Сканирование каталога TFS
    @Scheduled(initialDelayString = "#{T(java.lang.Long).valueOf(paramConfig.getDELAY_BEFORE_SCHEDULING())}",
            fixedDelayString = "#{T(java.lang.Long).valueOf(paramConfig.getDELAY_BETWEEN_SCANNING())}")
    public void signChecker() {
        LOG.info(String.format("Сканирование входного каталога TFS: %s", paramConfig.getTFS_INCOME_DIRECTORY()));
        // Получить список файлов
        try {
            List<File> filesInFolder = Files.walk(Paths.get(paramConfig.getTFS_INCOME_DIRECTORY()))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            // Перебрать список файлов и выделить только файлы открепленной ЭЦП
            for(int i = 0; i < filesInFolder.size(); i++) {
                String fname = filesInFolder.get(i).getName();
                if(fname.endsWith("sig")) {
                    checkSign(readFile(fname));
                    // TODO: перенос файлов в рабочий каталог WORK_DIRECTORY (для дальнейшей обработки)
                }
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    // Проверка ЭЦП
    private void checkSign(byte[] data) {
        LOG.info("Проверка ЭЦП...");
        try {
            BicryptSignParser bicryptSignParser = new BicryptSignParser(data);
            BicryptSign bicryptSign = bicryptSignParser.getBicryptSign();
            LOG.info(String.format("Идентификатор ЭЦП Bicrypt: %s", bicryptSign.getBicryptIdentifier()));

            boolean result = cryptoTool.check(data, bicryptSign, this.pkb, this.admPkb);
            LOG.info("Результат проверки ЭЦП: " + result);
            LOG.info("-----------------------------------------------------------");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Прочесть файл в массив байтов
    private byte[] readFile(String fname) {
        String path = paramConfig.getTFS_INCOME_DIRECTORY() + fname;
        LOG.info(String.format("Открытие файла ЭЦП: %s", path));

        File file = new File(path);
        byte[] result = new byte[(int)file.length()];

        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            int totalBytesRead = 0;
            while (totalBytesRead < result.length) {
                int bytesRemaining = result.length - totalBytesRead;
                int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                if (bytesRead > 0)
                    totalBytesRead += bytesRead;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }
}
