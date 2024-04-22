package live.ioteatime.ruleengine.service;

import live.ioteatime.ruleengine.domain.BeanSet;

import java.io.File;

public interface BeanService {

    File createConfig(BeanSet beanSet,String path);
}
