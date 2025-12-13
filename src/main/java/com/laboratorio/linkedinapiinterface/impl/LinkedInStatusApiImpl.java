package com.laboratorio.linkedinapiinterface.impl;

import com.google.gson.Gson;
import com.laboratorio.clientapilibrary.ApiClient;
import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.clientapilibrary.utils.ImageMetadata;
import com.laboratorio.clientapilibrary.utils.PostUtils;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.linkedinapiinterface.LinkedInStatusApi;
import com.laboratorio.linkedinapiinterface.exception.LinkedInApiException;
import com.laboratorio.linkedinapiinterface.model.LinkedInPostMessage;
import com.laboratorio.linkedinapiinterface.model.LinkedInRegisterUpload;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareCommentary;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareContent;
import com.laboratorio.linkedinapiinterface.model.LinkedInSpecificContent;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInPostMessageResponse;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInRegisterUploadResponse;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.2
 * @created 24/08/2024
 * @updated 13/12/2025
 */
public class LinkedInStatusApiImpl implements LinkedInStatusApi {
    protected static final Logger log = LogManager.getLogger(LinkedInStatusApiImpl.class);
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
    private final ApiClient client;
    private final String accessToken;
    private final String author;
    private final ReaderConfig apiConfig;
    private final String urlBase;
    private final Gson gson;

    public LinkedInStatusApiImpl(String accessToken, String author) {
        this.accessToken = accessToken;
        this.author = author;
        this.apiConfig = new ReaderConfig("config//linkedin_api.properties");
        this.urlBase = this.apiConfig.getProperty("url_base_linkedin");
        this.gson = new Gson();
        String proxyHost = this.apiConfig.getProperty("linkedin_proxy_host");
        String proxyPortStr = this.apiConfig.getProperty("linkedin_proxy_port");
        String certificatePath = this.apiConfig.getProperty("linkedin_proxy_certificate");
        if (proxyHost != null && !proxyHost.isBlank() && proxyPortStr != null && !proxyPortStr.isBlank()
                && certificatePath != null && !certificatePath.isBlank()) {
            int proxyPort = Integer.parseInt(proxyPortStr);
            this.client = new ApiClient(proxyHost, proxyPort, certificatePath);
        } else {
            this.client = new ApiClient();
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
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), LinkedInPostMessageResponse.class);
        } catch (Exception e) {
            throw  new LinkedInApiException("Error posteando un estado en linkedIn", e);
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
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            this.client.executeApiRequest(request);
            
            return true;
        } catch (Exception e) {
            throw  new LinkedInApiException("Error eliminando un estado en linkedIn", e);
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
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            ApiResponse response = this.client.executeApiRequest(request);

            return this.gson.fromJson(response.getResponseStr(), LinkedInRegisterUploadResponse.class);
        } catch (ApiClientException e) {
            throw  new LinkedInApiException("Error registrando una subida de fichero en linkedIn", e);
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
            request.addApiHeader(AUTHORIZATION, BEARER + this.accessToken);
            
            this.client.executeApiRequest(request);

            return true;
        } catch (ApiClientException e) {
            throw  new LinkedInApiException("Error subiendo una imagen a linkedIn", e);
        }
    }
    
    @Override
    public LinkedInPostMessageResponse postStatus(String text, String imagePath) throws Exception {
        // Se hace el registro del "upload" para obtener la URL para subir la imagen
        LinkedInRegisterUploadResponse registerUploadResponse = this.registerUpload();
        
        // Se sube la imagen
        this.uploadImage(registerUploadResponse.getValue().getUploadMechanism().getMediaUploadHttpRequest().getUploadUrl(), imagePath);
        
        // Se hace la petición de publicación del estado con la imagen
        LinkedInShareContent shareContent = new LinkedInShareContent(new LinkedInShareCommentary(text), registerUploadResponse.getValue().getAsset(), "Imagen de la punblicación", "Image");
        LinkedInSpecificContent specificContent = new LinkedInSpecificContent(shareContent);
        LinkedInPostMessage request = new LinkedInPostMessage(this.author, specificContent);
        
        return this.postStatus(request);
    }
}