package com.iuh.printshop.printshop_be.Service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // TODO: thay bằng @AuthenticationPrincipal sau khi gắn Security
    public Integer currentUserId() { return 2; }    // user demo
    public boolean isAdmin() { return true; }       // demo
}
