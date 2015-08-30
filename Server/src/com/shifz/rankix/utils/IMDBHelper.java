package com.shifz.rankix.utils;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class IMDBHelper {

    private static final java.lang.String DELIM_RATING = "<div class=\"titlePageSprite star-box-giga-star\"> ";
    private static final java.lang.String DELIM_RATING_2 = " </div>";
    private String rating;

    public IMDBHelper(String response){
        final String[] exp1 = response.split(DELIM_RATING);
        if(exp1.length==2){
            rating = exp1[1].split(DELIM_RATING_2)[0];
        }
    }

    public boolean hasRating(){
        return  rating!=null;
    }

    public String getRating(){
        return rating;
    }
}
