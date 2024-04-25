package com.ai.commentbot.configuration;

import com.ai.commentbot.CommentBotApplication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.util.Collections;

@Configuration
public class Oauth2Config {

    private static final String DATA_STORE_DIR = "/Users/shreeram/projects/comment-bot/src/main/resources/tokens";
    private static final String USER_ID = "user";
    private static final int SERVER_PORT = 8090;
    private static final String SERVER_HOST = "localhost";
    private static final String CALLBACK_PATH = "/login/oauth2/code/google";
    private static final String CREDENTIALS_FILE = "/credentials.json";

    @Bean
    public Credential authorize() throws IOException {
        GoogleAuthorizationCodeFlow flow = initializeFlow(jsonFactory());
        return new AuthorizationCodeInstalledApp(
                flow,
                new LocalServerReceiver.Builder()
                        .setPort(SERVER_PORT)
                        .setHost(SERVER_HOST)
                        .setCallbackPath(CALLBACK_PATH)
                        .build()
        ).authorize(USER_ID);
    }

    @Bean
    public JsonFactory jsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    private GoogleAuthorizationCodeFlow initializeFlow(JsonFactory jsonFactory) throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecrets(jsonFactory);
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(DATA_STORE_DIR));
        return new Builder(new NetHttpTransport(),
                jsonFactory,
                clientSecrets,
                Collections.singleton("https://www.googleapis.com/auth/youtube.force-ssl"))
                .setDataStoreFactory(dataStoreFactory)
                .build();
    }

    private GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory) throws IOException {
        InputStream inputStream = CommentBotApplication.class.getResourceAsStream(CREDENTIALS_FILE);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + CREDENTIALS_FILE);
        }
        return GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
    }

}
