package com.laboratorio.linkedinapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.laboratorio.linkedinapiinterface.LinkedInStatusApi;
import com.laboratorio.linkedinapiinterface.exception.LinkedInApiException;
import com.laboratorio.linkedinapiinterface.model.LinkedInPostMessage;
import com.laboratorio.linkedinapiinterface.model.LinkedInRegisterUpload;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareCommentary;
import com.laboratorio.linkedinapiinterface.model.LinkedInShareContent;
import com.laboratorio.linkedinapiinterface.model.LinkedInSpecificContent;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInPostMessageResponse;
import com.laboratorio.linkedinapiinterface.model.response.LinkedInRegisterUploadResponse;
import com.laboratorio.linkedinapiinterface.utils.LinkedInApiConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 25/08/2024
 */
public class LinkedInStatusApiImpl implements LinkedInStatusApi {
    protected static final Logger log = LogManager.getLogger(LinkedInStatusApiImpl.class);
    private final String accessToken;
    private final String author;
    private final LinkedInApiConfig apiConfig;

    public LinkedInStatusApiImpl(String accessToken, String author) {
        this.accessToken = accessToken;
        this.author = author;
        this.apiConfig = LinkedInApiConfig.getInstance();
    }
    
    private void logException(Exception e) {
        log.error("Error: " + e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: " + e.getMessage());
        }
    }
    
    private LinkedInPostMessageResponse postStatus(LinkedInPostMessage request) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String urlBase = this.apiConfig.getProperty("url_base_linkedin");
        String endpoint = this.apiConfig.getProperty("endpoint_ugcPosts");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("ugcPosts_valor_ok"));
        
        try {
            // Se crea la request
            Gson gson = new Gson();
            String requestJson = gson.toJson(request);
            log.info("Request a enviar: " + requestJson);
            
            String url = urlBase + "/" + endpoint;
            WebTarget target = client.target(url);
            
            response = target.request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken)
                    .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON));
            
            String jsonStr = response.readEntity(String.class);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new LinkedInApiException(LinkedInStatusApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.info("Respuesta recibida: " + jsonStr);
            
            return gson.fromJson(jsonStr, LinkedInPostMessageResponse.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (LinkedInApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
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
        Client client = ClientBuilder.newClient();
        Response response = null;
        String urlBase = this.apiConfig.getProperty("url_base_linkedin");
        String endpoint = this.apiConfig.getProperty("endpoint_delete_post");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("delete_post_valor_ok"));
        
        try {
            String url = urlBase + "/" + endpoint + "/" + messageId;
            WebTarget target = client.target(url);
            
            response = target.request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken)
                    .delete();
            
            if (response.getStatus() != okStatus) {
                log.error(String.format("Se obtuvo el código de error %d", response.getStatus()));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new LinkedInApiException(LinkedInStatusApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            
            return true;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (LinkedInApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    @Override
    public LinkedInRegisterUploadResponse registerUpload() {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String urlBase = this.apiConfig.getProperty("url_base_linkedin");
        String endpoint = this.apiConfig.getProperty("endpoint_registerUpload");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("registerUpload_valor_ok"));
        
        try {
            // Se crea la request
            Gson gson = new Gson();
            LinkedInRegisterUpload request = new LinkedInRegisterUpload(this.author);
            String requestJson = gson.toJson(request);
            log.info("Request a enviar: " + requestJson);
            
            String url = urlBase + "/" + endpoint;
            WebTarget target = client.target(url)
                    .queryParam("action", "registerUpload");
            
            response = target.request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken)
                    .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON));
            
            String jsonStr = response.readEntity(String.class);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new LinkedInApiException(LinkedInStatusApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.info("Respuesta recibida: " + jsonStr);
            
            return gson.fromJson(jsonStr, LinkedInRegisterUploadResponse.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (LinkedInApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    @Override
    public boolean uploadImage(String url, String imagePath) throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = null;
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("uploadImage_valor_ok"));
        
        try {
            WebTarget target = client.target(url);
            
            File imageFile = new File(imagePath);
            InputStream fileStream = new FileInputStream(imageFile);
            
            response = target.request()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken)
                    .post(Entity.entity(fileStream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
            
            String jsonStr = response.readEntity(String.class);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new LinkedInApiException(LinkedInStatusApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.debug("Respuesta recibida: " + jsonStr);
            
            return true;
        } catch (JsonSyntaxException | FileNotFoundException e) {
            logException(e);
            throw  e;
        } catch (LinkedInApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
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