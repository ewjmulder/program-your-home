package com.programyourhome.ir;

import java.util.Collection;

import com.programyourhome.ir.model.PyhRemote;

public interface InfraRed {

    public Collection<PyhRemote> getRemotes();

    public void pressRemoteKey(String remoteName, String key);

}
