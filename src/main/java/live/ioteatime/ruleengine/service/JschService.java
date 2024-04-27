package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.exception.CreateJSchSessionException;

public interface JschService {
    void scpFile(String filePath,String fileName) throws CreateJSchSessionException;
}
