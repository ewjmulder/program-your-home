package com.programyourhome.server.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/pyh");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // TODO: Any client side callable endpoints needed? Replace REST with this? (seems not to work without it)
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }

}