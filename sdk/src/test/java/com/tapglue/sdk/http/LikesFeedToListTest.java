/*
 *  Copyright (c) 2015-2016 Tapglue (https://www.tapglue.com/). All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tapglue.sdk.http;

import com.tapglue.sdk.entities.Like;
import com.tapglue.sdk.entities.User;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LikesFeedToListTest {
    @Test
    public void returnsLikes() {
        LikesFeedToList converter = new LikesFeedToList();
        List<Like> likes = new ArrayList<>();
        Map<String, User> users = new HashMap<>();
        LikesFeed feed = new LikesFeed();
        feed.likes = likes;
        feed.users = users;

        assertThat(converter.call(feed), equalTo(likes));
    }

    @Test
    public void populatesUsersToLikes() {
        LikesFeedToList converter = new LikesFeedToList();
        String id = "someId";
        Like like = mock(Like.class);
        List<Like> likes = Arrays.asList(like);
        Map<String, User> userMap = new HashMap<>();
        User user = mock(User.class);
        userMap.put(id, user);

        when(like.getUserId()).thenReturn(id);

        LikesFeed feed = new LikesFeed();
        feed.likes = likes;
        feed.users = userMap;

        converter.call(feed);

        verify(like).setUser(user);
    }

}
