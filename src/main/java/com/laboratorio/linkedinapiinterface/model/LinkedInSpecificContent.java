package com.laboratorio.linkedinapiinterface.model;

import com.google.gson.annotations.SerializedName;
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
public class LinkedInSpecificContent {
    @SerializedName("com.linkedin.ugc.ShareContent")
    private LinkedInShareContent shareContent;
}