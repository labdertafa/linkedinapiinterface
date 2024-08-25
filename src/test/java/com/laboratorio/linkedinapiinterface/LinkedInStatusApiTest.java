package com.laboratorio.linkedinapiinterface;

import com.laboratorio.linkedinapiinterface.impl.LinkedInStatusApiImpl;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInPostMessageResponse;
import com.laboratorio.linkedinapiinterface.utils.LinkedInApiConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LinkedInStatusApiTest {
    private LinkedInStatusApi statusApi;
    private static String messageId;
    
    @BeforeEach
    public void initTest() {
        LinkedInApiConfig config = LinkedInApiConfig.getInstance();
        String accessToken = config.getProperty("access_token_linkedin");
        String author = config.getProperty("urn_author_linkedin");
        
        this.statusApi = new LinkedInStatusApiImpl(accessToken, author);
    }
    
    @Test @Order(1)
    public void postStatus() {
        String texto = "Este es un mensaje de prueba enviado por pruebas unitarias JUNI5 el " + LocalDateTime.now().toString();
        
        LinkedInPostMessageResponse response = this.statusApi.postStatus(texto);
        messageId = response.getId();
        
        assertTrue(response.getId().length() > 0);
    }
    
    @Test @Order(2)
    public void deleteMessage() {
        boolean result = this.statusApi.deleteStatus(messageId);
        
        assertTrue(result);
    }
    
    @Test @Order(3)
    public void postStatusWithImage() throws Exception {
        String texto = "Este es un mensaje de prueba enviado por pruebas unitarias JUNI5 el " + LocalDateTime.now().toString();
        String filePath = "C:\\Users\\rafa\\Pictures\\Tutoriales\\laboratorio-2024.jpg";
        
        LinkedInPostMessageResponse response = this.statusApi.postStatus(texto, filePath);
        messageId = response.getId();
        
        assertTrue(response.getId().length() > 0);
    }
    
    @Test @Order(4)
    public void deleteMessageWithPhoto() {
        boolean result = this.statusApi.deleteStatus(messageId);
        
        assertTrue(result);
    }
}