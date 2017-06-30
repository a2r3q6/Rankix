package com.shifz.rankix.utils;


import com.shifz.rankix.exceptions.RequestException;

/**
 * Created by shifar on 31/12/15.
 */
public class HeaderSecurity {

    public static final String KEY_AUTHORIZATION = "Authorization";
    private static final String REASON_API_KEY_MISSING = "API key is missing";
    private static final String REASON_INVALID_API_KEY = "Invalid API key";
    private final String authorization;
    private String frenemyId;

    public HeaderSecurity(final String authorization) throws RequestException {
        //Collecting header from passed request
        this.authorization = authorization;
    }


    public String getFrenemyId() {
        return this.frenemyId;
    }

    public String getFailureReason() {
        return this.authorization == null ? REASON_API_KEY_MISSING : REASON_INVALID_API_KEY;
    }

    public String getAuthorization() {
        return authorization;
    }
}
