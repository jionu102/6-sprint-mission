package com.sprint.mission.discodeit.config;
//
//import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketChannelInterceptor implements ChannelInterceptor {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor != null) {
//            StompCommand command = accessor.getCommand();
//
//            if (command == null) {
//                return message;
//            }
//
//            try {
//                switch (command) {
//                    case CONNECT:
//                        return handleConnect(accessor, message);
//                    case SUBSCRIBE:
//                        return handleSubscribe(accessor, message);
////                    case SEND:
////                        return handleSend(accessor, message);
//                    case DISCONNECT:
//                        handleDisconnect(accessor);
//                        break;
//                    default:
//                        return message;
//                }
//            } catch (SecurityException e) {
//                System.err.println("보안 위반 - 메시지 차단: " + e.getMessage());
//                return null;
//            }
//        }
//        return message;
//    }
//
//    private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
//        String bearerToken = accessor.getFirstNativeHeader("Authorization");
//        String token = bearerToken.substring(7);
//        if (jwtTokenProvider.validateAccessToken(token)) {
//            String username = jwtTokenProvider.getUsernameFromToken(token);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            Authentication auth = new UsernamePasswordAuthenticationToken(
//                    userDetails, null, userDetails.getAuthorities()
//            );
//
//            accessor.setUser(auth);
//            return message;
//        } else {
//            throw new SecurityException("인증에 실패했습니다.");
//        }
//    }
//
//    private Message<?> handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
//        String destination = accessor.getDestination();
//        if (destination == null || !destination.matches("/sub/channels\\.[^.]\\.messages")) {
//            throw new SecurityException("잘못된 구독 경로 입니다.");
//        }
//        return message;
//    }

//    private Message<?> handleSend(StompHeaderAccessor accessor, Message<?> message) {
//        String destination = accessor.getDestination();
//
//        if (destination == null) {
//            return message;
//        }
//
//        if (destination.startsWith("/pub/messages")) {
//
//        }
//    }
//
//    private void handleDisconnect(StompHeaderAccessor accessor) {
//        String username = accessor.getUser().getName();
//    }
//}
