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

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.enums.Order;
import net.sf.stackwrap4j.json.JSONArray;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.json.JSONObject;
import net.sf.stackwrap4j.json.PoliteJSONObject;
import net.sf.stackwrap4j.query.QuestionQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Question in the Stack Exchange family of sites.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 */
public class Question extends MajorPost {

    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = -4569236296352955838L;
	
    /** The default page to be returned in results. */
    public static final int DEFAULT_PAGE = 1;
    
    /** The default size of the page returned in a result. */
    public static final int DEFAULT_PAGE_SIZE = 30;
    
    /** The default option to include the body of a Question. */
    public static final boolean DEFAULT_BODY = false;
    
    /** The default option to include the comments of a Question. */
    public static final boolean DEFAULT_COMMENTS = false;
    
    /** The default order to return results. */
    public static final Order DEFAULT_ORDER = Order.ASC;
    
    /** The default max date. */
    public static final long DEFAULT_TO_DATE = Long.MAX_VALUE;
    
    /** The default min date. */
    public static final long DEFAULT_FROM_DATE = 1L;
    
    /** The default in title value. */
    public static final String DEFAULT_IN_TITLE = null;
    
    /** The default string for tagged option. */
    public static final String DEFAULT_TAGGED = null;
    
    /** The default string for not tagged option. */
    public static final String DEFAULT_NOT_TAGGED = null;
    
    /** The default search min. */
    public static final int DEFAULT_SEARCH_MIN = Integer.MIN_VALUE;
    
    /** The default search max. */
    public static final int DEFAULT_SEARCH_MAX = Integer.MAX_VALUE;
    
    /** The default option on whether or not to return answers. */
    public static final boolean DEFAULT_ANSWERS = true;

    /** The number of answers on this question. */
    private int answerCount;
    
    /** The list of answers for this question. */
    private List<Answer> answers;
    
    /** The id of the accepted answer to this question. */
    private int acceptedAnswerId;
    
    /** The number of times this questions has been favorited (starred). */
    private int favoriteCount;
    
    /** The end date of the bounty on this question. */
    private long bountyClosesDate;
    
    /** The bounty amount for this question. */
    private int bountyAmount;
    
    /** The date this question was closed. */
    private long closedDate;
    
    /** The reason this question was closed. */
    private String closedReason;
    
    /** The URL for this question's timeline. */
    private String questionTimelineUrl;
    
    /** The URL for this question's answers. */
    private String questionAnswersUrl;

    /** The list of tags on this question. */
    private List<String> tags;

    /**
     * Creates a Question from a JSON string.
     * @param json string containing questions.
     * @param originator the StackExchange instance that created this
     * @throws JSONException if there's a problem parsing the string.
     */
    Question(final String json, final StackWrapper originator) throws JSONException {
        this(new JSONObject(json).getJSONArray("questions").getJSONObject(0), originator);
    }

    /**
     * Creates a Question from a JSON object.
     * @param jQ object containing questions.
     * @param originator originator the StackExchange instance that created this
     * @throws JSONException if there's a problem communicating with the API or if an invalid attribute is requested.
     */
    Question(final JSONObject jQ, final StackWrapper originator) throws JSONException {
        super(jQ, originator);
        PoliteJSONObject jQp = new PoliteJSONObject(jQ);
        bountyClosesDate = jQp.tryGetLong("bounty_closes_date", -1);
        bountyAmount = jQp.tryGetInt("bounty_amount", 0);
        closedDate = jQp.tryGetLong("closed_date", -1);
        closedReason = jQp.tryGetString("closed_reason");
        questionTimelineUrl = jQp.tryGetString("question_timeline_url");
        questionAnswersUrl = jQp.tryGetString("question_answers_url");

        postId = jQ.getInt("question_id"); // inherited member
        answerCount = jQ.getInt("answer_count");

        JSONArray answersArray = jQp.tryGetJSONArray("answers");
        if (answersArray != null) {
            answers = Answer.fromJSONArray(answersArray, originator);
        } else {
            answers = new ArrayList<Answer>();
        }

        acceptedAnswerId = jQp.tryGetInt("accepted_answer_id", -1);
        favoriteCount = jQ.getInt("favorite_count");
        commentsUrl = jQ.getString("question_comments_url"); // inherited member
        
        tags = new ArrayList<String>();
        JSONArray tagsArray = jQ.getJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            tags.add(tagsArray.getString(i));
        }
    }

    /**
     * The id of the answer that was accepted for this question.
     * @return the id of the accepted answer, or -1 if there was no accepted answer
     */
    public final int getAcceptedAnswerId() {
        return acceptedAnswerId;
    }

    /**
     * The number of times this question has been favorited.
     * @return the fav count
     */
    public final int getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * The tags this question was tagged with.
     * @return list of tags
     */
    public final List<String> getTags() {
        return tags;
    }

    /**
     * The number of answers on this question.
     * @return the answer count
     */
    public final int getAnswerCount() {
        return answerCount;
    }

    /**
     * Gets the answers associated with this Question. <br />
     * May make another API  call if no answers have been loaded yet.
     * 
     * @return list of answers
     * @throws IOException if the connection to the API fails
     * @throws JSONException if the JSON returned by the api is bad
     */
    public final List<Answer> getAnswers() throws IOException, JSONException {
        if (answers == null || answers.size() == 0) {
            answers = getCreatingApi().getAnswersByQuestionId(getPostId());
            answerCount = answers.size();
        }
        return answers;
    }

    /**
     * The date the bounty was closed on this question.
     * @return the bountyClosesDate or -1 if no bounty
     */
    public final long getBountyClosesDate() {
        return bountyClosesDate;
    }

    /**
     * Status of bountyness.
     * @return whether or not this question had a bounty
     */
    public final boolean hasBounty() {
        return bountyClosesDate < 0;
    }

    /**
     * The amount of bounty on this question.
     * @return the bountyAmount
     */
    public final int getBountyAmount() {
        return bountyAmount;
    }

    /**
     * The date the question was closed.
     * @return the closedDate
     */
    public final long getClosedDate() {
        return closedDate;
    }

    /**
     * The reason for closing this question.
     * @return the closedReason
     */
    public final String getClosedReason() {
        return closedReason;
    }

    /**
     * The url for a timeline of this question.
     * @return the questionTimelineUrl
     */
    public final String getQuestionTimelineUrl() {
        return questionTimelineUrl;
    }

    /**
     * The url for answers of this question.
     * @return the questionAnswersUrl
     */
    public final String getQuestionAnswersUrl() {
        return questionAnswersUrl;
    }

    /**
     * Gets a list of comments on this question.
     * @return the list of answers.
     * @throws IOException if there's a problem communicating with the API.
     * @throws JSONException if the response cannot be parsed.
     */
    @Override
    public final List<Comment> getComments() throws IOException, JSONException {
        if (comments == null) {
        	QuestionQuery query = new QuestionQuery();
        	query.setComments(true);
            comments = getCreatingApi().getQuestionById(this.getPostId()).comments;
        }
        return comments;
    }

    /**
     * Extracts a list of Questions from a JSONArray object.
     * @param jsonArray containing questions
     * @param originator the StackExchange instance that created this
     * @return the list of questions.
     * @throws JSONException if there's a problem communicating with the API.
     */
    protected static List<Question> fromJSONArray(final JSONArray jsonArray, final StackWrapper originator)
            throws JSONException {
        List<Question> ret = new ArrayList<Question>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add(new Question(jsonArray.getJSONObject(i), originator));
        }
        return ret;
    }

    /**
     * Parses a JSON string into a list of questions.
     * @param json string containing questions.
     * @param originator the StackExchange instance that created this
     * @return a List of questions.
     * @throws JSONException if there's a problem communicating with the API.
     */
    public static List<Question> fromJSONString(final String json, final StackWrapper originator)
            throws JSONException {
        return fromJSONArray(new JSONObject(json).getJSONArray("questions"), originator);
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + acceptedAnswerId;
        result = prime * result + favoriteCount;
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Question)) {
            return false;
        }
        Question other = (Question) obj;
        if (acceptedAnswerId != other.acceptedAnswerId) {
            return false;
        }
        if (favoriteCount != other.favoriteCount) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        return true;
    }

}
