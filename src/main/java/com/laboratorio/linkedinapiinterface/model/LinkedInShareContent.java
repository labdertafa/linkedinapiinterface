package com.laboratorio.linkedinapiinterface.model;

import java.util.ArrayList;
import java.util.List;
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
public class LinkedInShareContent {
    private LinkedInShareCommentary shareCommentary;
    private String shareMediaCategory;
    private List<LinkedInMedia> media;

    public LinkedInShareContent(LinkedInShareCommentary shareCommentary) {
        this.shareCommentary = shareCommentary;
        this.shareMediaCategory = "NONE";
    }   

    public LinkedInShareContent(LinkedInShareCommentary shareCommentary, String imageId, String imageDescription, String imageTitle) {
        this.shareCommentary = shareCommentary;
        this.shareMediaCategory = "IMAGE";
        this.media = new ArrayList<>();
        LinkedInMedia imageData = new LinkedInMedia(imageId, imageDescription, imageTitle);
        this.media.add(imageData);
    }
}