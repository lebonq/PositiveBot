package fr.positivediscord.positivebot;

import org.aeonbits.owner.Config;

@Config.Sources(value = "file:config.properties")
public interface BotConfig extends Config {
    String token();

    int verbose();

    long channel();
    long guild();
    long channelPost();
    String riotApi();
}