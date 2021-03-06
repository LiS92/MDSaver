//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

/**
 * Post.java
 * vk-android-sdk
 *
 * Created by Babichev Vitaly on 19.01.14.
 * Copyright (c) 2014 VK. All rights reserved.
 */
package com.vk.sdk.api.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A post object describes a wall post.
 */
@SuppressWarnings("unused")
public class VKApiPost extends VKAttachments.VKApiAttachment implements Identifiable {

    /**
     * Post ID on the wall, positive number
     */
    public int id;

    /**
     * Wall owner ID.
     */
    public int to_id;

    /**
     * ID of the user who posted.
     */
    public int from_id;

    /**
     * Date (in Unix time) the post was added.
     */
    public long date;

    /**
     * Text of the post.
     */
    public String text;

    /**
     * ID of the wall owner the post to which the reply is addressed (if the post is a reply to another wall post).
     */
    public int reply_owner_id;

    /**
     * ID of the wall post to which the reply is addressed (if the post is a reply to another wall post).
     */
    public int reply_post_id;

    /**
     * True, if the post was created with "Friends only" option.
     */
    public boolean friends_only;

    /**
     * Number of comments.
     */
    public int comments_count;

    /**
     * Whether the current user can leave comments to the post (false — cannot, true — can)
     */
    public boolean can_post_comment;

    /**
     * Number of users who liked the post.
     */
    public int likes_count;

    /**
     * Whether the user liked the post (false — not liked, true — liked)
     */
    public boolean user_likes;

    /**
     * Whether the user can like the post (false — cannot, true — can).
     */
    public boolean can_like;

    /**
     * Whether the user can repost (false — cannot, true — can).
     */
    public boolean can_publish;

    /**
     * Number of users who copied the post.
     */
    public int reposts_count;

    /**
     * Whether the user reposted the post (false — not reposted, true — reposted).
     */
    public boolean user_reposted;

    /**
     * Type of the post, can be: post, copy, reply, postpone, suggest.
     */
    public String post_type;

    /**
     * Information about attachments to the post (photos, links, etc.), if any;
     */
    public VKAttachments attachments = new VKAttachments();

    /**
     * ID of the author (if the post was published by a community and signed by a user).
     */
    public int signer_id;

    /**
     * List of history of the reposts.
     */
    public VKList<VKApiPost> copy_history;

    /**
     * Fills a Post instance from JSONObject.
     */
    public VKApiPost parse(JSONObject source) throws JSONException {
        id = source.optInt("id");
        to_id = source.optInt("to_id");
        from_id = source.optInt("from_id");
        date = source.optLong("date");
        text = source.optString("text");
        reply_owner_id = source.optInt("reply_owner_id");
        reply_post_id = source.optInt("reply_post_id");
        friends_only = ParseUtils.parseBoolean(source, "friends_only");
        JSONObject comments = source.optJSONObject("comments");
        if(comments != null) {
            comments_count = comments.optInt("count");
            can_post_comment = ParseUtils.parseBoolean(comments, "can_post");
        }
        JSONObject likes = source.optJSONObject("likes");
        if(likes != null) {
            likes_count = likes.optInt("count");
            user_likes = ParseUtils.parseBoolean(likes, "user_likes");
            can_like = ParseUtils.parseBoolean(likes, "can_like");
            can_publish = ParseUtils.parseBoolean(likes, "can_publish");
        }
        JSONObject reposts = source.optJSONObject("reposts");
        if(reposts != null) {
            reposts_count = reposts.optInt("count");
            user_reposted = ParseUtils.parseBoolean(reposts, "user_reposted");
        }
        post_type = source.optString("post_type");
        attachments.fill(source.optJSONArray("attachments"));
        signer_id = source.optInt("signer_id");
        copy_history = new VKList<VKApiPost>(source.optJSONArray("copy_history"), VKApiPost.class);
        return this;
    }

    /**
     * Creates empty Post instance.
     */
    public VKApiPost() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CharSequence toAttachmentString() {
        return new StringBuilder(VKAttachments.TYPE_POST).append(to_id).append('_').append(id);
    }
    
    @Override
    public CharSequence toHtml() {
        String url = "http://vk.com/" + VKAttachments.TYPE_POST + to_id + "_" + id;
        StringBuilder att = new StringBuilder();
        att.append("<div class='hr'>");
        attachments.forEach(p -> att.append(p.toHtml()));
        att.append("</div>");
        return String.format("<div class='wall'>"
                + "<a href='%s'>Запись на стене</a><br/>"
                + "%s\n%s</div>", url, text, att);
    }

    @Override
    public String getType() {
        return VKAttachments.TYPE_POST;
    }
}
