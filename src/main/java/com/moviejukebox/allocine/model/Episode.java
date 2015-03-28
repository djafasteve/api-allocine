/*
 *      Copyright (c) 2004-2015 YAMJ Members
 *      http://code.google.com/p/moviejukebox/people/list
 *
 *      This file is part of the Yet Another Movie Jukebox (YAMJ).
 *
 *      The YAMJ is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      YAMJ is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with the YAMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 *      Web: http://code.google.com/p/moviejukebox/
 *
 */
package com.moviejukebox.allocine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;

@JsonRootName("episode")
public class Episode extends AbstractBaseMapping {

    private static final long serialVersionUID = 7597888938988246976L;

    @JsonProperty("originalBroadcastDate")
    private String originalBroadcastDate;
    @JsonProperty("episodeNumberSeries")
    private int episodeNumberSeries;
    @JsonProperty("episodeNumberSeason")
    private int episodeNumberSeason;
    @JsonProperty("parentSeries")
    private CodeName parentSeries;
    @JsonProperty("parentSeason")
    private CodeName parentSeason;
    @JsonProperty("picture")
    private Picture picture;
    @JsonProperty("trailer")
    private Trailer trailer;
    @JsonProperty("trailerEmbed")
    private String trailerEmbed;
    @JsonProperty("broadcast")
    private List<Broadcast> broadcast;
    @JsonProperty("link")
    private List<Link> link;

    public String getOriginalBroadcastDate() {
        return originalBroadcastDate;
    }

    public void setOriginalBroadcastDate(String originalBroadcastDate) {
        this.originalBroadcastDate = originalBroadcastDate;
    }

    public int getEpisodeNumberSeries() {
        return episodeNumberSeries;
    }

    public void setEpisodeNumberSeries(int episodeNumberSeries) {
        this.episodeNumberSeries = episodeNumberSeries;
    }

    public int getEpisodeNumberSeason() {
        return episodeNumberSeason;
    }

    public void setEpisodeNumberSeason(int episodeNumberSeason) {
        this.episodeNumberSeason = episodeNumberSeason;
    }

    public CodeName getParentSeries() {
        return parentSeries;
    }

    public void setParentSeries(CodeName parentSeries) {
        this.parentSeries = parentSeries;
    }

    public CodeName getParentSeason() {
        return parentSeason;
    }

    public void setParentSeason(CodeName parentSeason) {
        this.parentSeason = parentSeason;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public String getTrailerEmbed() {
        return trailerEmbed;
    }

    public void setTrailerEmbed(String trailerEmbed) {
        this.trailerEmbed = trailerEmbed;
    }

    public List<Broadcast> getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(List<Broadcast> broadcast) {
        this.broadcast = broadcast;
    }

    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }
}
