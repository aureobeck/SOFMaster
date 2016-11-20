/**
 * StackWrap4J - A Java wrapper for the Stack Exchange API.
 * 
 * Copyright (c) 2010 Bill Cruise and Justin Nelson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sf.stackwrap4j.stackauth.entities;

import java.util.ArrayList;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;

/**
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public class Site {
    private String name;
    private String apiEndpoint;
    private String logoUrl;
    private String iconUrl;
    private String siteUrl;
    private String description;

    private Styling styling;

    public Site(JSONObject jS) throws JSONException {
        name = jS.getString("name");
        logoUrl = jS.getString("logo_url");
        iconUrl = jS.getString("icon_url");
        siteUrl = jS.getString("site_url");
        description = jS.getString("description");

        styling = new Styling(jS.getJSONObject("styling"));

        PoliteJSONObject jSp = new PoliteJSONObject(jS);
        apiEndpoint = jSp.tryGetString("api_endpoint");
    }

    public String getName() {
        return name;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getDescription() {
        return description;
    }
    
    public Styling getStyling(){
        return styling;
    }

    public StackWrapper getStackWrapper(String key){
        return new StackWrapper(this.getApiEndpoint(), key);
    }
    
    public StackWrapper getStackWrapper(){
    	return getStackWrapper(null);
    }
    
    public static List<Site> fromJSONString(String jsonStr)
            throws JSONException {
        return fromJSONArray(new JSONArray(jsonStr));
    }

    public static List<Site> fromJSONArray(JSONArray arr) throws JSONException {
        List<Site> ret = new ArrayList<Site>();
        for (int i = 0; i < arr.length(); i++) {
            ret.add(new Site(arr.getJSONObject(i)));
        }
        return ret;
    }
}
