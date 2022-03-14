package org;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

public class PercentBot extends DefaultAbsSender {

    protected PercentBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
            return "TELEGRAM BOT TOKEN";
    }
}
