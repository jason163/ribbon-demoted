package com.yj.provider.vo;

/**
 * Create with IDEA
 * User: Jason
 * Date: 2018/5/26
 * Time: 13:29
 */
public class User {
    private Long id;
    private String account;
    private String password;

    public User() {
    }

    public User(Long id, String account, String password) {
        this();
        this.id = id;
        this.account = account;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
