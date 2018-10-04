package ir.piana.dev.portal.web.module.config;

import java.util.List;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class Server {
    private String name;
    private List<Listener> listeners;
    private Session session;
    private String pianaRoleProvidable;

    public Server() {
    }

    public Server(String name, List<Listener> listeners, Session session, String pianaRoleProvidable) {
        this.name = name;
        this.listeners = listeners;
        this.session = session;
        this.pianaRoleProvidable = pianaRoleProvidable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPianaRoleProvidable() {
        return pianaRoleProvidable;
    }

    public void setPianaRoleProvidable(String pianaRoleProvidable) {
        this.pianaRoleProvidable = pianaRoleProvidable;
    }
}
