// Обработка файлов TFS
package ru.sberbank.cseodo.tfsutil.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.infocrypt.icbicrypttools.BicryptSign;
import ru.infocrypt.icbicrypttools.BicryptSignParser;
import ru.infocrypt.icbicrypttools.ICBicryptTools;
import ru.infocrypt.icbicrypttools.PublicKeyBase;
import ru.sberbank.cseodo.tfsutil.config.ParamConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SignChecker {
    private static final Logger LOG = LoggerFactory.getLogger(ParamConfig.class);
    private ICBicryptTools cryptoTool;

    @Autowired
    private ParamConfig paramConfig;

    // Подготовить криптографическую библиотеку
    public void prepareCryptoLibrary() {
        cryptoTool = new ICBicryptTools();
        try {
            cryptoTool.installFiles("C:/Users/18741878/Keys/"); //paramConfig.getBICRYPT_SPECIFIC_LIBRARY());
//            cryptoTool.setPrndPath("C:/Users/18741878/Keys/prnd.db3");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Сканирование каталога TFS
    @Scheduled(initialDelayString = "#{T(java.lang.Long).valueOf(paramConfig.getDELAY_BEFORE_SCHEDULING())}",
            fixedDelayString = "#{T(java.lang.Long).valueOf(paramConfig.getDELAY_BETWEEN_SCANNING())}")
    public void signChecker() {
        LOG.info(String.format("Сканируем входной каталог TFS: %s", paramConfig.getTFS_INCOME_DIRECTORY()));
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
                    // Прочесть файл в массив байтов
                    byte[] data = readFile(fname);
                    // Проверить его ЭЦП
                    checkSign(cryptoTool, paramConfig.getSIGN_TYPE(), data);
                }
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    // Проверка ЭЦП
    private void checkSign(ICBicryptTools cryptoTool, String signType, byte[] data) {
        LOG.info("Проверка ЭЦП...");
        try {
//            if(signType.equalsIgnoreCase("A")) {
                BicryptSignParser bicryptSignParser = new BicryptSignParser(data);
                byte[] pureData = bicryptSignParser.getData();
                if(pureData == null) {
                    pureData = new byte[0];
                }
                BicryptSign bicryptSign = bicryptSignParser.getBicryptSign();
                LOG.info(String.format("Идентификатор ЭЦП Bicrypt: %s", bicryptSign.getBicryptIdentifier()));
                // Проверка ЭЦП по публичным ключам
                System.loadLibrary("cms80");
                PublicKeyBase pkb = new PublicKeyBase("C:\\Users\\18741878\\Keys\\testsbtj_d.015");
                PublicKeyBase admPkb = new PublicKeyBase(paramConfig.getADM_PUBLIC_KEY_DIRECTORY() + "00CA00CA.017");

                boolean result = cryptoTool.check(data, bicryptSign, pkb, admPkb);
                System.out.println(">>>>>>>>>>>> " + result);
                pkb.close();
                admPkb.close();
//            }
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
