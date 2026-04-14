package com.zhouyuanzhi.weblog.jwt.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("zyz");
        System.out.println(encodedPassword);
        // 例如，可能会输出：$2a$10$9g.8s7.lWJc.HH.L7q04.e.ys1G.VzT4Gv3H8.X.C/w.Zz.4o.5uG
    }
}
