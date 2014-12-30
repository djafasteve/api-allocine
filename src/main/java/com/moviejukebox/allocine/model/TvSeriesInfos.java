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
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This is the TvSeries Search bean for the api.allocine.fr search
 *
 * @author Yves.Blusseau
 */
public class TvSeriesInfos extends AbstractBaseInfos {

    private static final long serialVersionUID = -4486388552369054158L;

    @JsonProperty("tvseries")
    private TvSeries tvSeries;

    public TvSeries getTvSeries() {
        return tvSeries;
    }

    public void setTvSeries(TvSeries tvSeries) {
        this.tvSeries = tvSeries;
    }

    public boolean isValid() {
        if (tvSeries == null) {
            return false;
        }
        return tvSeries.getCode() > 0;
    }

    public boolean isNotValid() {
        return !this.isValid();
    }

    public int getCode() {
        return this.getCode(tvSeries);
    }

    public String getTitle() {
        return this.getTitle(tvSeries);
    }

    public String getOriginalTitle() {
        return this.getOriginalTitle(tvSeries);
    }

    public int getYearStart() {
        if (tvSeries == null) {
            return 0;
        }
        return tvSeries.getYearStart();
    }

    public int getYearEnd() {
        if (tvSeries == null) {
            return 0;
        }
        return tvSeries.getYearEnd();
    }

    public String getReleaseDate() {
        return this.getReleaseDate(tvSeries);
    }
    
    public String getReleaseState() {
        return this.getReleaseState(tvSeries);
    }

    public String getSynopsis() {
        return this.getSynopsis(tvSeries);
    }

    public String getSynopsisShort() {
        return this.getSynopsisShort(tvSeries);
    }

    public int getUserRating() {
        return this.getUserRating(tvSeries);
    }

    public int getPressRating() {
        return this.getPressRating(tvSeries);
    }

    public Set<String> getGenres() {
        return this.getGenres(tvSeries);
    }

    public Set<String> getNationalities() {
        return this.getNationalities(tvSeries);
    }

    public String getOriginalChannel() {
        if (tvSeries == null) {
            return null;
        }
        if (tvSeries.getOriginalChannel() == null) {
            return null;
        }
        if (tvSeries.getOriginalChannel().getChannel() == null) {
            return null;
        }
        return tvSeries.getOriginalChannel().getChannel().getName();
    }

    public Set<MoviePerson> getActors() {
        return this.getActors(tvSeries);
    }

    public Set<MoviePerson> getDirectors() {
        return this.getDirectors(tvSeries);
    }

    public Set<MoviePerson> getWriters() {
        return this.getWriters(tvSeries);
    }
    
    public Set<MoviePerson> getCamera() {
        return this.getCamera(tvSeries);
    }

    public Set<MoviePerson> getProducers() {
        return this.getProducers(tvSeries);
    }
    
    public Set<String> getPosterUrls() {
        return this.getPosterUrls(tvSeries);
    }

    public int getSeasonCount() {
        if (tvSeries == null) {
            return 0;
        }
        return tvSeries.getSeasonCount();
    }

    public List<Season> getSeasonList() {
        if (tvSeries == null) {
            return Collections.emptyList();
        }
        return tvSeries.getSeasonList();
    }

    public final int getSeasonCode(int seasonNumber) {
        for (Season season : this.getSeasonList()) {
            if (season.getSeasonNumber() == seasonNumber) {
                return season.getCode();
            }
        }
        return -1;
    }
}
