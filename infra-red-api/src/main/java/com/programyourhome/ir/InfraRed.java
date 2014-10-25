package com.programyourhome.ir;

import java.util.Collection;

import com.programyourhome.ir.model.Remote;

public interface InfraRed {

    public Collection<Remote> getRemotes();

    public void pressRemoteKey(String remoteName, String key);

}
