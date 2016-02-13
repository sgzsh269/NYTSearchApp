
package com.sagarnileshshah.nytsearchapp.models.gson;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private Meta meta;
    private List<Doc> docs = new ArrayList<Doc>();

    /**
     * @return The meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * @param meta The meta
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * @return The docs
     */
    public List<Doc> getDocs() {
        return docs;
    }

    /**
     * @param docs The docs
     */
    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }


}
