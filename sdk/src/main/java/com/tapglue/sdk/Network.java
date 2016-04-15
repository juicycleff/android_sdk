/**
 * Copyright (c) 2015-2016 Tapglue (https://www.tapglue.com/). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tapglue.sdk;

import android.content.Context;

import com.tapglue.sdk.entities.User;
import com.tapglue.sdk.http.ServiceFactory;
import com.tapglue.sdk.http.TapglueService;
import com.tapglue.sdk.http.payloads.EmailLoginPayload;
import com.tapglue.sdk.http.payloads.UsernameLoginPayload;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

class Network {

    ServiceFactory serviceFactory;
    TapglueService service;
    SessionStore sessionStore;
    UUIDStore uuidStore;

    public Network(ServiceFactory serviceFactory, Context context) {
        this.serviceFactory = serviceFactory;
        service = serviceFactory.createTapglueService();
        sessionStore = new SessionStore(context);
        uuidStore = new UUIDStore(context);
        uuidStore.get().subscribe(new UUIDObserver());
        sessionStore.get().map(new SessionTokenExtractor());

    }

    public Observable<User> loginWithUsername(String username, String password) {
        UsernameLoginPayload payload = new UsernameLoginPayload(username, password);
        return service.login(payload).map(new SessionTokenExtractor()).map(sessionStore.store());
    }

    public Observable<User> loginWithEmail(String email, String password) {
        EmailLoginPayload payload = new EmailLoginPayload(email, password);
        return service.login(payload).map(new SessionTokenExtractor()).map(sessionStore.store());
    }

    public Observable<Void> logout() {
        return service.logout().doOnCompleted(sessionStore.clear());
    }

    public Observable<Void> sendAnalytics() {
        return service.sendAnalytics();
    }

    private class SessionTokenExtractor implements Func1<User, User> {

        @Override
        public User call(User user) {
            serviceFactory.setSessionToken(user.getSessionToken());
            service = serviceFactory.createTapglueService();
            return user;
        }
    }
    private class UUIDObserver implements Observer<String> {

        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {}

        @Override
        public void onNext(String uuid) {
            serviceFactory.setUserUUID(uuid);
            service = serviceFactory.createTapglueService();
        }
    }
}
