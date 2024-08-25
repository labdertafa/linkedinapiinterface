package com.laboratorio.linkedinapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */

@Getter @Setter @AllArgsConstructor
public class LinkedInMedia {
    private String status;
    private LinkedInDescription description;
    private String media;
    private LinkedInDescription title;

    public LinkedInMedia(String imageId, String imageDescription, String imageTitle) {
        this.status = "READY";
        this.description = new LinkedInDescription(imageDescription);
        this.media = imageId;
        this.title = new LinkedInDescription(imageTitle);
    }
}