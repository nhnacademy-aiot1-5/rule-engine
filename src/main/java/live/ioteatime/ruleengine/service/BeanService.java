package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.BeanSet;

public interface BeanService {

    String createConfig(BeanSet beanSet,String path);

    String createFolder(String name,String path);
    String splitFileName(String filePath);
}
