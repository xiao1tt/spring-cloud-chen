package com.chen.nozdormu.admin.service;

import com.chen.nozdormu.admin.core.model.JobUser;
import com.chen.nozdormu.admin.core.util.CookieUtil;
import com.chen.nozdormu.admin.core.util.I18nUtil;
import com.chen.nozdormu.admin.core.util.JacksonUtil;
import com.chen.nozdormu.admin.dao.JobUserDao;
import com.chen.nozdormu.core.biz.model.ReturnT;
import java.math.BigInteger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

/**
 * @author xuxueli 2019-05-04 22:13:264
 */
@Configuration
public class LoginService {

    public static final String LOGIN_IDENTITY_KEY = "JOB_LOGIN_IDENTITY";

    @Resource
    private JobUserDao jobUserDao;


    private String makeToken(JobUser jobUser) {
        String tokenJson = JacksonUtil.writeValueAsString(jobUser);
        String tokenHex = new BigInteger(tokenJson.getBytes()).toString(16);
        return tokenHex;
    }

    private JobUser parseToken(String tokenHex) {
        JobUser jobUser = null;
        if (tokenHex != null) {
            String tokenJson = new String(new BigInteger(tokenHex, 16).toByteArray());      // username_password(md5)
            jobUser = JacksonUtil.readValue(tokenJson, JobUser.class);
        }
        return jobUser;
    }


    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String username,
            String password, boolean ifRemember) {

        // param
        if (username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_empty"));
        }

        // valid passowrd
        JobUser jobUser = jobUserDao.loadByUserName(username);
        if (jobUser == null) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }
        String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!passwordMd5.equals(jobUser.getPassword())) {
            return new ReturnT<String>(500, I18nUtil.getString("login_param_unvalid"));
        }

        String loginToken = makeToken(jobUser);

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, loginToken, ifRemember);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     */
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
        return ReturnT.SUCCESS;
    }

    /**
     * logout
     */
    public JobUser ifLogin(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (cookieToken != null) {
            JobUser cookieUser = null;
            try {
                cookieUser = parseToken(cookieToken);
            } catch (Exception e) {
                logout(request, response);
            }
            if (cookieUser != null) {
                JobUser dbUser = jobUserDao.loadByUserName(cookieUser.getUsername());
                if (dbUser != null) {
                    if (cookieUser.getPassword().equals(dbUser.getPassword())) {
                        return dbUser;
                    }
                }
            }
        }
        return null;
    }


}
