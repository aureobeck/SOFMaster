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

package net.sf.stackwrap4j.entities;

import java.io.IOException;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.datastructures.CommentsFromPostList;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.CommentQuery;

/**
 * Super class for Question and Answer. Contains common methods and members.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 * 
 */
public abstract class MajorPost extends Post {

    /**
     * The universal version identifier for a Serializable class.
     */
    private static final long serialVersionUID = -1356543893716595603L;
    
    /** Timestamp when the post was locked. */
	private long lockedDate;
	
	/** Timestamp when the post was last edited. */
    private long lastEditDate;
    
    /** Timestamp from the last activity on the post. */
    private long lastActivityDate;

    /** True if the post is community wiki, false otherwise. */
    private boolean isCommunityOwned;

    /** Number of upvotes on this post. */
    private int upVoteCount;
    
    /** Number of downvotes on this post. */
    private int downVoteCount;

    /** Title of this post. */
    private String title;
    
    /** Number of times this post has been viewed. */
    private int viewCount;

    /** List of comments left on this post. */
    protected List<Comment> comments;

    /** The URL to fetch comments on this post. */
    protected String commentsUrl;

    /**
     * Creates a MajorPost from a JSON object.
     * @param jP the JSON object representing a post.
     * @param originator the StackExchange instance that created this.
     * @throws JSONException if the original JSON string was poorly formatted, or if an invalid parameter is requested.
     */
    MajorPost(final JSONObject jP, final StackWrapper originator) throws JSONException {
        super(jP, originator);
        PoliteJSONObject jPp = new PoliteJSONObject(jP);
        lockedDate = jPp.tryGetLong("locked_date", -1);
        lastEditDate = jPp.tryGetLong("last_edit_date", -1);
        lastActivityDate = jPp.tryGetLong("last_activity_date", -1);

        JSONArray jarr = jPp.tryGetJSONArray("comments");
        if (jarr != null) {
            comments = Comment.fromJSONArray(jarr, originator);
        }

        title = jP.getString("title");
        viewCount = jP.getInt("view_count");
        isCommunityOwned = jP.getBoolean("community_owned");
        upVoteCount = jP.getInt("up_vote_count");
        downVoteCount = jP.getInt("down_vote_count");
    }

    /**
     * The date this post was locked.
     * @return the date this post was locked, or -1 if not locked.
     */
    public final long getLockedDate() {
        return lockedDate;
    }

    /**
     * The last time this post had activity.
     * @return the last time this post had activity.
     */
    public final long getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * The last time this post was edited.
     * @return the last time this post was edited.
     */
    public final long getLastEditDate() {
        return lastEditDate;
    }

    /**
     * The URL where you can find comments for this post.
     * @return The URL where you can find the comments for this post online.
     */
    public final String getUrlForComments() {
        return commentsUrl;
    }

    /**
     * The number of downvotes on this post.
     * @return the number of downvotes this post has received.
     */
    public final int getDownVoteCount() {
        return downVoteCount;
    }

    /**
     * The number of upvotes on this post.
     * @return the number of upvotes this post has received.
     */
    public final int getUpVoteCount() {
        return upVoteCount;
    }

    /**
     * Whether or not this post is CW.
     * @return whether or not this post is Community Wiki.
     */
    public final boolean isCommunityOwned() {
        return isCommunityOwned;
    }

    /**
     * The view count on this post.
     * @return the number of times this post has been viewed.
     */
    public final int getViewCount() {
        return viewCount;
    }

    /**
     * The title of this post.
     * @return the title of this post.
     */
    public final String getTitle() {
        return title;
    }

    /**
     * The list of comments associated with this post.<br />
     * Will make another call to the API if necessary.
     * @return the comments associated with this post.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the JSON string is incorrectly formatted.
     */
    public List<Comment> getComments() throws IOException, JSONException {
        final int defaultPageSize = 50;
        if (comments != null) {
        	return comments;
        }
        CommentQuery query = new CommentQuery();
        query.setPageSize(defaultPageSize).addId(this.getPostId());
        comments = new CommentsFromPostList(getCreatingApi(), query);
        return comments;
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isCommunityOwned ? 1231 : 1237);
        result = prime * result
                + (int) (lastActivityDate ^ (lastActivityDate >>> 32));
        result = prime * result + (int) (lastEditDate ^ (lastEditDate >>> 32));
        result = prime * result + (int) (lockedDate ^ (lockedDate >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MajorPost)) {
            return false;
        }
        MajorPost other = (MajorPost) obj;
        if (isCommunityOwned != other.isCommunityOwned) {
            return false;
        }
        if (lastActivityDate != other.lastActivityDate) {
            return false;
        }
        if (lastEditDate != other.lastEditDate) {
            return false;
        }
        if (lockedDate != other.lockedDate) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }

}
