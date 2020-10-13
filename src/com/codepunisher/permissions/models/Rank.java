package com.codepunisher.permissions.models;

import java.util.List;

public class Rank
{
    private String name;
    private int priority;
    private String prefix;
    private List<String> inheritance;
    private List<String> permissions;

    public Rank(String name, int priority, String prefix, List<String> inheritance, List<String> permissions) {
        this.name = name;
        this.priority = priority;
        this.prefix = prefix;
        this.inheritance = inheritance;
        this.permissions = permissions;
    }

    public String getName() { return this.name; }
    int getPriority() { return this.priority; }
    String getPrefix() { return this.prefix; }
    public List<String> getInheritance() { return this.inheritance; }
    public List<String> getPermissions() { return this.permissions; }
}
