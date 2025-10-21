package com.hrbank.backup;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    private IpUtils() {
        // 유틸 클래스이므로 인스턴스화 방지
    }

    public static String extractClientIp(HttpServletRequest request) {
        // 프록시나 로드밸런서를 거쳤을 때 원래 클라이언트 IP가 담길 수 있는 헤더들
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR",
                "X-Real-IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For: "client1, proxy1, proxy2" 형태일 수 있음
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr(); // 직접 연결된 클라이언트 IP
    }
}
