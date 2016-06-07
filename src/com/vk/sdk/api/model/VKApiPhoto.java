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

package com.vk.sdk.api.model;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.vk.sdk.api.model.ParseUtils.parseBoolean;
import static com.vk.sdk.api.model.ParseUtils.parseInt;
import static com.vk.sdk.api.model.VKAttachments.*;

/**
 * Describes a photo object from VK.
 */
public class VKApiPhoto extends VKAttachments.VKApiAttachment implements Identifiable {

    /**
     * Photo ID, positive number
     */
    public int id;

    /**
     * Photo album ID.
     */
    public int album_id;

    /**
     * ID of the user or community that owns the photo.
     */
    public int owner_id;

    /**
     * Width (in pixels) of the original photo.
     */
    public int width;

    /**
     * Height (in pixels) of the original photo.
     */
    public int height;

    /**
     * Text describing the photo.
     */
    public String text;

    /**
     * Date (in Unix time) the photo was added.
     */
    public long date;

    /**
     * URL of image with maximum size 75x75px.
     */
    public String photo_75;

    /**
     * URL of image with maximum size 130x130px.
     */
    public String photo_130;

    /**
     * URL of image with maximum size 604x604px.
     */
    public String photo_604;

    /**
     * URL of image with maximum size 807x807px.
     */
    public String photo_807;

    /**
     * URL of image with maximum size 1280x1024px.
     */
    public String photo_1280;

    /**
     * URL of image with maximum size 2560x2048px.
     */
    public String photo_2560;

    /**
     * All photo thumbs in photo sizes.
     * It has data even if server returned them without {@code PhotoSizes} format.
     */
    public VKPhotoSizes src = new VKPhotoSizes();

    /**
     * Information whether the current user liked the photo.
     */
    public boolean user_likes;

    /**
     * Whether the current user can comment on the photo
     */
    public boolean can_comment;

    /**
     * Number of likes on the photo.
     */
    public int likes;

    /**
     * Number of comments on the photo.
     */
    public int comments;

    /**
     * Number of tags on the photo.
     */
    public int tags;

    /**
     * An access key using for get information about hidden objects.
     */
    public String access_key;

    /**
     * Fills a Photo instance from JSONObject.
     */
    public VKApiPhoto parse(JSONObject from) {
        album_id = from.optInt("album_id");
        date = from.optLong("date");
        height = from.optInt("height");
        width = from.optInt("width");
        owner_id = from.optInt("owner_id");
        id = from.optInt("id");
        text = from.optString("text");
        access_key = from.optString("access_key");

        photo_75 = from.optString("photo_75");
        photo_130 = from.optString("photo_130");
        photo_604 = from.optString("photo_604");
        photo_807 = from.optString("photo_807");
        photo_1280 = from.optString("photo_1280");
        photo_2560 = from.optString("photo_2560");

        JSONObject likes = from.optJSONObject("likes");
        this.likes = ParseUtils.parseInt(likes, "count");
        this.user_likes = ParseUtils.parseBoolean(likes, "user_likes");
        comments = parseInt(from.optJSONObject("comments"), "count");
        tags = parseInt(from.optJSONObject("tags"), "count");
        can_comment = parseBoolean(from, "can_comment");

        src.setOriginalDimension(width, height);
        JSONArray photo_sizes = from.optJSONArray("sizes");
        if(photo_sizes != null) {
            src.fill(photo_sizes);
        } else {
            if(!TextUtils.isEmpty(photo_75)) {
                src.add(VKApiPhotoSize.create(photo_75, VKApiPhotoSize.S, width, height));
            }
            if(!TextUtils.isEmpty(photo_130)) {
                src.add(VKApiPhotoSize.create(photo_130, VKApiPhotoSize.M, width, height));
            }
            if(!TextUtils.isEmpty(photo_604)) {
                src.add(VKApiPhotoSize.create(photo_604, VKApiPhotoSize.X, width, height));
            }
            if(!TextUtils.isEmpty(photo_807)) {
                src.add(VKApiPhotoSize.create(photo_807, VKApiPhotoSize.Y, width, height));
            }
            if(!TextUtils.isEmpty(photo_1280)) {
                src.add(VKApiPhotoSize.create(photo_1280, VKApiPhotoSize.Z, width, height));
            }
            if(!TextUtils.isEmpty(photo_2560)) {
                src.add(VKApiPhotoSize.create(photo_2560, VKApiPhotoSize.W, width, height));
            }
            src.sort();
        }
        return this;
    }

    /**
     * Creates empty Photo instance.
     */
    public VKApiPhoto() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CharSequence toAttachmentString() {
        StringBuilder result = new StringBuilder(TYPE_PHOTO).append(owner_id).append('_').append(id);
        if(!TextUtils.isEmpty(access_key)) {
            result.append('_');
            result.append(access_key);
        }
        return result;
    }
    
    @Override
    public CharSequence toHtml() {
        final int size = src.size();
        VKApiPhotoSize small = src.get(0);
        if (size > 1) small = src.get(1);
        VKApiPhotoSize big = src.get(size - 1);
        return String.format("<a href='%s'><img src='%s'/></a>", big.src, small.src);
    }

    @Override
    public String getType() {
        return TYPE_PHOTO;
    }
}
