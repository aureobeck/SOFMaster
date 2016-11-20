package net.sf.stackwrap4j.stackauth.entities;

import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;

public class Styling {

    private String linkColor;
    private String tagForegroundColor;
    private String tagBackgroundColor;
    
    protected Styling(JSONObject jS) throws JSONException{
        linkColor = jS.getString("link_color");
        tagForegroundColor = jS.getString("tag_foreground_color");
        tagBackgroundColor = jS.getString("tag_background_color");
    }
    
    public String getLinkColorString(){
        return linkColor;
    }
    
    public String getTagForegroundColorString(){
        return tagForegroundColor;
    }
    
    public String getTagBackgroundColorString(){
        return tagBackgroundColor;
    }
    
}
