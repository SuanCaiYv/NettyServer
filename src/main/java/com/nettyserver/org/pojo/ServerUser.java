package com.nettyserver.org.pojo;

public class ServerUser {
    private String email;

    private String name;

    private String password;

    private Integer level;

    private String serverFiles;

    private String fileNames;

    public String getFileNames()
    {
        return fileNames;
    }

    public void setFileNames(String fileNames)
    {
        this.fileNames = fileNames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getServerFiles() {
        return serverFiles;
    }

    public void setServerFiles(String serverFiles) {
        this.serverFiles = serverFiles == null ? null : serverFiles.trim();
    }
}