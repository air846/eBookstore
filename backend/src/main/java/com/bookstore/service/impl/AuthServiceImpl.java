package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BizException;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.MiniLoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserInfoUpdateRequest;
import com.bookstore.dto.UserPasswordUpdateRequest;
import com.bookstore.entity.User;
import com.bookstore.mapper.UserMapper;
import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.AuthService;
import com.bookstore.vo.LoginVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// 认证服务：注册、登录与令牌生成
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${wechat.miniapp.appid:}")
    private String miniAppId;

    @Value("${wechat.miniapp.secret:}")
    private String miniAppSecret;

    @Override
    public LoginVO userLogin(LoginRequest request) {
        User user = checkUser(request.getUsername(), request.getPassword());
        return buildLoginVo(user);
    }

    @Override
    public LoginVO adminLogin(LoginRequest request) {
        User user = checkUser(request.getUsername(), request.getPassword());
        if (user.getRole() == null || user.getRole() != 1) {
            throw new BizException(403, "仅管理员可登录后台");
        }
        return buildLoginVo(user);
    }

    @Override
    public LoginVO miniLogin(MiniLoginRequest request) {
        String unionId = StringUtils.hasText(request.getUnionId())
                ? request.getUnionId().trim()
                : fetchUnionIdByCode(request.getCode());
        if (!StringUtils.hasText(unionId)) {
            throw new BizException(400, "unionId不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUnionId, unionId)
                .last("limit 1"));
        if (user == null) {
            user = new User();
            user.setUnionId(unionId);
            user.setUsername(generateMiniUsername());
            user.setPassword(encoder.encode(UUID.randomUUID().toString()));
            user.setNickname((request.getNickname() == null || request.getNickname().trim().isEmpty())
                    ? ("微信用户" + user.getUsername().substring(Math.max(0, user.getUsername().length() - 6)))
                    : request.getNickname().trim());
            user.setAvatar(request.getAvatar());
            user.setRole(0);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        } else {
            if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
                user.setNickname(request.getNickname().trim());
            }
            if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
                user.setAvatar(request.getAvatar().trim());
            }
            userMapper.updateById(user);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(403, "账号已被禁用");
        }
        return buildLoginVo(user);
    }

    private String fetchUnionIdByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        if (!StringUtils.hasText(miniAppId) || !StringUtils.hasText(miniAppSecret)) {
            throw new BizException(500, "微信小程序配置缺失，请配置 wechat.miniapp.appid/secret");
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + URLEncoder.encode(miniAppId, StandardCharsets.UTF_8)
                + "&secret=" + URLEncoder.encode(miniAppSecret, StandardCharsets.UTF_8)
                + "&js_code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";
        String raw = restTemplate.getForObject(url, String.class);
        if (!StringUtils.hasText(raw)) {
            throw new BizException(500, "微信登录失败：响应为空");
        }
        Map<String, Object> body;
        try {
            body = objectMapper.readValue(raw, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            String snippet = raw.length() > 120 ? raw.substring(0, 120) + "..." : raw;
            throw new BizException(500, "微信登录失败：响应格式异常 " + snippet);
        }
        Object errCodeRaw = body.get("errcode");
        if (errCodeRaw instanceof Number && ((Number) errCodeRaw).intValue() != 0) {
            String errMsg = String.valueOf(body.getOrDefault("errmsg", "unknown"));
            throw new BizException(400, "微信登录失败：" + errMsg);
        }
        String unionId = body.get("unionid") == null ? null : String.valueOf(body.get("unionid"));
        if (StringUtils.hasText(unionId)) {
            return unionId;
        }
        String openId = body.get("openid") == null ? null : String.valueOf(body.get("openid"));
        if (StringUtils.hasText(openId)) {
            return "wx_openid_" + openId;
        }
        throw new BizException(400, "微信登录失败：未获取到 unionid/openid");
    }

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setRole(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public User userInfo(Long userId) {
        User user = getUserOrThrow(userId);
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateUserInfo(Long userId, UserInfoUpdateRequest request) {
        User user = getUserOrThrow(userId);
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public void updatePassword(Long userId, UserPasswordUpdateRequest request) {
        User user = getUserOrThrow(userId);
        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BizException("原密码错误");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BizException("两次输入的新密码不一致");
        }
        if (encoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BizException("新密码不能与原密码相同");
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    private User checkUser(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("limit 1"));
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new BizException(401, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(403, "账号已被禁用");
        }
        return user;
    }

    private String generateMiniUsername() {
        for (int i = 0; i < 10; i++) {
            String candidate = "wx_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, candidate));
            if (count == null || count == 0) {
                return candidate;
            }
        }
        throw new BizException(500, "生成小程序用户名失败");
    }

    private LoginVO buildLoginVo(User user) {
        return LoginVO.builder()
                .token(jwtTokenProvider.generateToken(user))
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }

    private User getUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        return user;
    }
}
