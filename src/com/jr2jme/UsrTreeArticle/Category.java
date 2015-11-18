package com.jr2jme.UsrTreeArticle;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by K.H on 2015/11/06.
 */
public class Category implements Serializable{
    Set<String> belowcat;//カテゴリに含まれるカテゴリ
    Set<String> abovecat;//カテゴリが属しているカテゴリ

    public Category(Set<String> above,Set<String>below){//(カテゴリが属しているカテゴリ,カテゴリに含まれるカテゴリ)
        belowcat=below;
        abovecat=above;
    }

    public void setAbovecat(Set<String> abovecat) {
        this.abovecat = abovecat;
    }

    public Set<String> getAbovecat() {
        return abovecat;
    }

    public Set<String> getBelowcat() {
        return belowcat;
    }
}
