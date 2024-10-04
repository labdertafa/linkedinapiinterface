package com.laboratorio.linkedinapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.laboratorio.clientapilibrary.ApiClient;
import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.clientapilibrary.utils.ImageMetadata;
import com.laboratorio.clientapilibrary.utils.PostUtils;
import com.laboratorio.linkedinapiinterface.LinkedInStatusApi;
import com.laboratorio.linkedinapiinterface.model.LinkedInPostMessage;
import com.laboratorio.linkedinapiinterface.model.LinkedInRegisterUpload;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareCommentary;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareContent;
import com.laboratorio.linkedinapiinterface.model.LinkedInSpecificContent;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInPostMessageResponse;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInRegisterUploadResponse;
import com.laboratorio.linkedinapiinterface.utils.LinkedInApiConfig;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 24/08/2024
 * @updated 04/10/2024
 */
public class LinkedInStatusApiImpl implements LinkedInStatusApi {
    protected static final Logger log = LogManager.getLogger(LinkedInStatusApiImpl.class);
    private final ApiClient client;
    private final String accessToken;
    private final String author;
    private final LinkedInApiConfig apiConfig;
    private final String urlBase;
    private final Gson gson;

    public LinkedInStatusApiImpl(String accessToken, String author) {
        this.client = new ApiClient();
        this.accessToken = accessToken;
        this.author = author;
        this.apiConfig = LinkedInApiConfig.getInstance();
        this.urlBase = this.apiConfig.getProperty("url_base_linkedin");
        this.gson = new Gson();
    }
    
    private void logException(Exception e) {
        log.error("Error: " + e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: " + e.getCause().getMessage());
        }
    }
    
    private LinkedInPostMessageResponse postStatus(LinkedInPostMessage postMessage) {
        String endpoint = this.apiConfig.getProperty("endpoint_ugcPosts");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("ugcPosts_valor_ok"));
        
        try {
            // Se crea la request
            String requestJson = this.gson.toJson(postMessage);
            log.debug("Request a enviar: " + requestJson);
            
            String url = this.urlBase + "/" + endpoint;
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.POST, requestJson);
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), LinkedInPostMessageResponse.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (ApiClientException e) {
            throw  e;
        }
    }

    @Override
    public LinkedInPostMessageResponse postStatus(String text) {
        LinkedInShareContent shareContent = new LinkedInShareContent(new LinkedInShareCommentary(text));
        LinkedInSpecificContent specificContent = new LinkedInSpecificContent(shareContent);
        LinkedInPostMessage request = new LinkedInPostMessage(this.author, specificContent);
        
        return this.postStatus(request);
    }

    @Override
    public boolean deleteStatus(String messageId) {
        String endpoint = this.apiConfig.getProperty("endpoint_delete_post");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("delete_post_valor_ok"));
        
        try {
            String url = this.urlBase + "/" + endpoint + "/" + messageId;
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.DELETE);
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            this.client.executeApiRequest(request);
            
            return true;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (ApiClientException e) {
            throw  e;
        }
    }
    
    @Override
    public LinkedInRegisterUploadResponse registerUpload() {
        String endpoint = this.apiConfig.getProperty("endpoint_registerUpload");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("registerUpload_valor_ok"));
        
        try {
            // Se crea la request
            LinkedInRegisterUpload registerUpload = new LinkedInRegisterUpload(this.author);
            String requestJson = gson.toJson(registerUpload);
            log.debug("Request a enviar: " + requestJson);
            
            String url = this.urlBase + "/" + endpoint;
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.POST, requestJson);
            request.addApiPathParam("action", "registerUpload");
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);

            return this.gson.fromJson(response.getResponseStr(), LinkedInRegisterUploadResponse.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (ApiClientException e) {
            throw  e;
        }
    }
    
    @Override
    public boolean uploadImage(String url, String imagePath) throws Exception {
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("uploadImage_valor_ok"));
        
        try {
            File imageFile = new File(imagePath);
            ImageMetadata metadata = PostUtils.extractImageMetadata(imagePath);
            
            ApiRequest request = new ApiRequest(url, okStatus, ApiMethodType.POST, imageFile);
            request.addApiHeader("Content-Type", metadata.getMimeType());
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            this.client.executeApiRequest(request);

            return true;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (ApiClientException e) {
            throw  e;
        }
    }
    
    @Override
    public LinkedInPostMessageResponse postStatus(String text, String imagePath) throws Exception {
        // Se hace el registro del "upload" para obtener la URL para subir la imagen
        LinkedInRegisterUploadResponse registerUploadResponse;
        try {
            registerUploadResponse = this.registerUpload();
        } catch (Exception e) {
            log.error("Ha ocurrido un error mientras se registraba del upload de la imagen");
            throw  e;
        }
        
        // Se sube la imagen
        try {
            this.uploadImage(registerUploadResponse.getValue().getUploadMechanism().getMediaUploadHttpRequest().getUploadUrl(), imagePath);
        } catch (Exception e) {
            log.error("Ha ocurrido un error mientras se subía la imagen a postear");
            throw e;
        }
        
        // Se hace la petición de publicación del estado con la imagen
        LinkedInShareContent shareContent = new LinkedInShareContent(new LinkedInShareCommentary(text), registerUploadResponse.getValue().getAsset(), "Imagen de la punblicación", "Image");
        LinkedInSpecificContent specificContent = new LinkedInSpecificContent(shareContent);
        LinkedInPostMessage request = new LinkedInPostMessage(this.author, specificContent);
        
        return this.postStatus(request);
    }
}