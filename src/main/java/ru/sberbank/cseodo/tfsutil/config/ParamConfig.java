// Загрузить свойства для работы криптографической библиотеки BicryptTools
package ru.sberbank.cseodo.tfsutil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:tfs.properties")
public class ParamConfig {
    @Value("${TFS_INCOME_DIRECTORY}")
    private String TFS_INCOME_DIRECTORY;
    public void setTFS_INCOME_DIRECTORY(String TFS_INCOME_DIRECTORY) {
        this.TFS_INCOME_DIRECTORY = TFS_INCOME_DIRECTORY;
    }
    public String getTFS_INCOME_DIRECTORY() {
        return TFS_INCOME_DIRECTORY;
    }

    @Value("${WORK_DIRECTORY}")
    private String WORK_DIRECTORY;
    public void setWORK_DIRECTORY(String WORK_DIRECTORY) {
        this.WORK_DIRECTORY = WORK_DIRECTORY;
    }
    public String getWORK_DIRECTORY() {
        return WORK_DIRECTORY;
    }

    @Value("${BICRYPT_SPECIFIC_LIBRARY}")
    private String BICRYPT_SPECIFIC_LIBRARY;
    public void setBICRYPT_SPECIFIC_LIBRARY(String BICRYPT_SPECIFIC_LIBRARY) {
        this.BICRYPT_SPECIFIC_LIBRARY = BICRYPT_SPECIFIC_LIBRARY;
    }
    public String getBICRYPT_SPECIFIC_LIBRARY() {
        return BICRYPT_SPECIFIC_LIBRARY;
    }

    @Value("${BICRYPT_JAR_LIBRARY}")
    private String BICRYPT_JAR_LIBRARY;
    public void setBICRYPT_JAR_LIBRARY(String BICRYPT_JAR_LIBRARY) {
        this.BICRYPT_JAR_LIBRARY = BICRYPT_JAR_LIBRARY;
    }
    public String getBICRYPT_JAR_LIBRARY() {
        return BICRYPT_JAR_LIBRARY;
    }

    @Value("${PUBLIC_KEYS_DIRECTORY}")
    private String PUBLIC_KEYS_DIRECTORY;
    public void setPUBLIC_KEYS_DIRECTORY(String PUBLIC_KEYS_DIRECTORY) {
        this.PUBLIC_KEYS_DIRECTORY = PUBLIC_KEYS_DIRECTORY;
    }
    public String getPUBLIC_KEYS_DIRECTORY() {
        return PUBLIC_KEYS_DIRECTORY;
    }

    @Value("${ADM_PUBLIC_KEY_DIRECTORY}")
    private String ADM_PUBLIC_KEY_DIRECTORY;
    public void setADM_PUBLIC_KEY_DIRECTORY(String ADM_PUBLIC_KEY_DIRECTORY) {
        this.ADM_PUBLIC_KEY_DIRECTORY = ADM_PUBLIC_KEY_DIRECTORY;
    }
    public String getADM_PUBLIC_KEY_DIRECTORY() {
        return ADM_PUBLIC_KEY_DIRECTORY;
    }

    @Value("${SIGN_TYPE}")
    private String SIGN_TYPE;
    public void setSIGN_TYPE(String SIGN_TYPE) {
        this.SIGN_TYPE = SIGN_TYPE;
    }
    public String getSIGN_TYPE() {
        return SIGN_TYPE;
    }

    @Value("${DELAY_BEFORE_SCHEDULING}")
    private long DELAY_BEFORE_SCHEDULING;
    public void setDELAY_BEFORE_SCHEDULING(long DELAY_BEFORE_SCHEDULING) {
        this.DELAY_BEFORE_SCHEDULING = DELAY_BEFORE_SCHEDULING;
    }
    public long getDELAY_BEFORE_SCHEDULING() {
        return DELAY_BEFORE_SCHEDULING;
    }

    @Value("${DELAY_BETWEEN_SCANNING}")
    private long DELAY_BETWEEN_SCANNING;
    public void setDELAY_BETWEEN_SCANNING(long DELAY_BETWEEN_SCANNING) {
        this.DELAY_BETWEEN_SCANNING = DELAY_BETWEEN_SCANNING;
    }
    public long getDELAY_BETWEEN_SCANNING() {
        return DELAY_BETWEEN_SCANNING;
    }
}
