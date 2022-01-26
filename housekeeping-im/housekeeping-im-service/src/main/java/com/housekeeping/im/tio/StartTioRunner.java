package com.housekeeping.im.tio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class StartTioRunner implements CommandLineRunner {

    @Resource
    private TioWsMsgHandler tioWsMsgHandler;

    private TioWebsocketStarter appStarter;

    @Override
    public void run(String... args) throws Exception {
        appStarter = new TioWebsocketStarter(TioServerConfig.SERVER_PORT, tioWsMsgHandler);
        appStarter.getWsServerStarter().start();
    }

    public TioWebsocketStarter getAppStarter() {
        return appStarter;
    }
}
