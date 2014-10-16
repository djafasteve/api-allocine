/*
 *      Copyright (c) 2004-2014 YAMJ Members
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
package com.moviejukebox.allocine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviejukebox.allocine.model.EpisodeInfos;
import com.moviejukebox.allocine.model.MovieInfos;
import com.moviejukebox.allocine.model.PersonInfos;
import com.moviejukebox.allocine.model.Search;
import com.moviejukebox.allocine.model.TvSeasonInfos;
import com.moviejukebox.allocine.model.TvSeriesInfos;
import com.moviejukebox.allocine.tools.ApiUrl;
import com.moviejukebox.allocine.tools.WebBrowser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamj.api.common.http.CommonHttpClient;
import org.yamj.api.common.http.UserAgentSelector;

/**
 * Abstract implementation for Allocine API; common methods for XML and JSON.
 *
 * @author modmax
 */
public class AllocineApi {

    private static final Logger LOG = LoggerFactory.getLogger(AllocineApi.class);

    // Constants
    private static final String ERROR_FAILED_TO_CONVERT_URL = "Failed to convert URL";
    private static final String LITERAL_LARGE = "large";
    private static final String LITERAL_SYNOPSIS = "synopsis,synopsisshort";
    // Methods
    private static final String METHOD_SEARCH = "search";
    private static final String METHOD_MOVIE = "movie";
    private static final String METHOD_TVSERIES = "tvseries";
    private static final String METHOD_SEASON = "season";
    private static final String METHOD_EPISODE = "episode";
    private static final String METHOD_PERSON = "person";
    // Filters
    private static final String FILTER_MOVIE = "movie";
    private static final String FILTER_TVSERIES = "tvseries";
    private static final String FILTER_PERSON = "person";
    // Parameters
    private static final String PARAM_PROFILE = "profile";
    private static final String PARAM_MEDIAFMT = "mediafmt";
    private static final String PARAM_FILTER = "filter";
    private static final String PARAM_FORMAT = "format";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_STRIPTAGS = "striptags";
    private static final String PARAM_FORMAT_VALUE = "json";

    private final ApiUrl apiUrl;
    private final CommonHttpClient httpClient;
    private ObjectMapper mapper;
    private Charset charset;

    /**
     * Create the API
     *
     * @param partnerKey The partner key for Allocine
     * @param secretKey The secret key for Allocine
     */
    public AllocineApi(String partnerKey, String secretKey) {
        this(partnerKey, secretKey, null);
    }

    /**
     * Create the API
     *
     * @param partnerKey The partner key for Allocine
     * @param secretKey The secret key for Allocine
     * @param httpClient the http client to use instead internal web browser
     */
    public AllocineApi(String partnerKey, String secretKey, CommonHttpClient httpClient) {
        this.apiUrl = new ApiUrl(partnerKey, secretKey);
        this.httpClient = httpClient;
        this.mapper = new ObjectMapper();
        this.charset = Charset.forName("UTF-8");
    }

    public final void setProxy(Proxy proxy, String username, String password) {
        if (httpClient == null) {
            WebBrowser.setProxy(proxy);
            WebBrowser.setProxyPassword(username, password);
        }
    }

    public final void setProxy(String host, int port, String username, String password) {
        if (httpClient == null) {
            WebBrowser.setProxyHost(host);
            WebBrowser.setProxyPort(port);
            WebBrowser.setProxyPassword(username, password);
        }
    }

    private <T> T readJsonObject(URL url, Class<T> object) throws AllocineException {
        if (httpClient == null) {
            URLConnection connection = null;
            InputStream inputStream = null;
            try {
                connection = WebBrowser.openProxiedConnection(url);
                inputStream = connection.getInputStream();
                return mapper.readValue(inputStream, object);
            } catch (IOException ex) {
                throw new AllocineException(AllocineException.AllocineExceptionType.UNKNOWN_CAUSE, "Failed to read JSON object", url, ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception ex) {
                        LOG.trace("Failed to close input stream", ex);
                    }
                }

                if (connection != null && (connection instanceof HttpURLConnection)) {
                    try {
                        ((HttpURLConnection) connection).disconnect();
                    } catch (Exception ex) {
                        LOG.trace("Failed to close connection", ex);
                    }
                }
            }
        } else {
            try {
                HttpGet httpGet = new HttpGet(url.toURI());
                httpGet.addHeader("accept", "application/json");
                httpGet.setHeader(HTTP.USER_AGENT, UserAgentSelector.randomUserAgent());
                return mapper.readValue(this.httpClient.requestContent(httpGet, charset), object);
            } catch (IOException ex) {
                throw new AllocineException(AllocineException.AllocineExceptionType.MAPPING_FAILED, "Failed to convert JSON object", url, ex);
            } catch (URISyntaxException ex) {
                throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, "Failed to convert JSON object", url, ex);
            }
        }
    }

    public Search searchMovies(String query) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("q", query);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_FILTER, FILTER_MOVIE);
        params.put(PARAM_STRIPTAGS, LITERAL_SYNOPSIS);
        String url = apiUrl.generateUrl(METHOD_SEARCH, params);

        Search search;
        try {
            search = this.readJsonObject(new URL(url), Search.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }
        return search;
    }

    public Search searchTvSeries(String query) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("q", query);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_FILTER, FILTER_TVSERIES);
        String url = apiUrl.generateUrl(METHOD_SEARCH, params);

        Search search;
        try {
            search = this.readJsonObject(new URL(url), Search.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }

        return search;
    }

    public Search searchPersons(String query) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("q", query);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_FILTER, FILTER_PERSON);
        String url = apiUrl.generateUrl(METHOD_SEARCH, params);

        Search search;
        try {
            search = this.readJsonObject(new URL(url), Search.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }

        return search;
    }

    public MovieInfos getMovieInfos(String allocineId) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PARAM_CODE, allocineId);
        params.put(PARAM_PROFILE, LITERAL_LARGE);
        params.put(PARAM_FILTER, FILTER_MOVIE);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        String url = apiUrl.generateUrl(METHOD_MOVIE, params);

        MovieInfos movieInfos;
        try {
            movieInfos = this.readJsonObject(new URL(url), MovieInfos.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }

        return movieInfos;
    }

    public TvSeriesInfos getTvSeriesInfos(String allocineId) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PARAM_PROFILE, LITERAL_LARGE);
        params.put(PARAM_MEDIAFMT, "mp4-lc");
        params.put(PARAM_FILTER, FILTER_MOVIE);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_CODE, allocineId);
        params.put(PARAM_STRIPTAGS, LITERAL_SYNOPSIS);
        String url = apiUrl.generateUrl(METHOD_TVSERIES, params);

        TvSeriesInfos tvSeriesInfo;
        try {
            tvSeriesInfo = this.readJsonObject(new URL(url), TvSeriesInfos.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }

        return tvSeriesInfo;
    }

    public TvSeasonInfos getTvSeasonInfos(Integer seasonCode) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PARAM_PROFILE, LITERAL_LARGE);
        params.put(PARAM_MEDIAFMT, "mp4-lc");
        params.put(PARAM_FILTER, FILTER_MOVIE);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_CODE, String.valueOf(seasonCode));
        params.put(PARAM_STRIPTAGS, LITERAL_SYNOPSIS);
        String url = apiUrl.generateUrl(METHOD_SEASON, params);

        TvSeasonInfos tvSeasonInfos;
        try {
            tvSeasonInfos = this.readJsonObject(new URL(url), TvSeasonInfos.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }

        return tvSeasonInfos;
    }

    public PersonInfos getPersonInfos(String allocineId) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PARAM_PROFILE, LITERAL_LARGE);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_CODE, allocineId);
        params.put(PARAM_STRIPTAGS, "biography,biographyshort");
        String url = apiUrl.generateUrl(METHOD_PERSON, params);

        PersonInfos personInfos;
        try {
            personInfos = this.readJsonObject(new URL(url), PersonInfos.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }
        return personInfos;
    }

    public EpisodeInfos getEpisodeInfos(String allocineId) throws AllocineException {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PARAM_PROFILE, LITERAL_LARGE);
        params.put(PARAM_FORMAT, PARAM_FORMAT_VALUE);
        params.put(PARAM_CODE, allocineId);
        params.put(PARAM_STRIPTAGS, LITERAL_SYNOPSIS);
        String url = apiUrl.generateUrl(METHOD_EPISODE, params);

        EpisodeInfos episodeInfos;
        try {
            episodeInfos = this.readJsonObject(new URL(url), EpisodeInfos.class);
        } catch (MalformedURLException ex) {
            throw new AllocineException(AllocineException.AllocineExceptionType.INVALID_URL, ERROR_FAILED_TO_CONVERT_URL, url, ex);
        }
        return episodeInfos;
    }
}