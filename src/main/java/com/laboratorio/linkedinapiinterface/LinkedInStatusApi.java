package com.laboratorio.linkedinapiinterface;

import com.laboratorio.linkedinapiinterface.model.response.LinkedInPostMessageResponse;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInRegisterUploadResponse;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */
public interface LinkedInStatusApi {
    LinkedInPostMessageResponse postStatus(String text);
    boolean deleteStatus(String messageId);
    
    // Postear un status con imagen
    LinkedInRegisterUploadResponse registerUpload();
    boolean uploadImage(String url, String imagePath) throws Exception;
    LinkedInPostMessageResponse postStatus(String text, String imagePath) throws Exception;
}