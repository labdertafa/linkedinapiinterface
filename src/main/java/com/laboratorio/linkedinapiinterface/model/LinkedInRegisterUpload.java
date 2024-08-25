package com.laboratorio.linkedinapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LinkedInRegisterUpload {
    private LinkedInRegisterUploadRequest registerUploadRequest;

    public LinkedInRegisterUpload(String owner) {
        this.registerUploadRequest = new LinkedInRegisterUploadRequest(owner);
    }
}