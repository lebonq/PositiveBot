package fr.positivediscord.positivebot;


import com.esotericsoftware.minlog.Log;

import fr.positivediscord.positivebot.listener.SuggestionListener;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.Closeable;

import javax.security.auth.login.LoginException;

public class PositiveBot implements Runnable, Closeable {

    public static final String VERSION = "0.1-ALPHA";

    @Getter private final BotConfig config;
    private JDA jda;
    private JDABuilder jdaB;

    public PositiveBot(BotConfig config) {
        this.config = config;
    }

    @SneakyThrows
    @Override
    public void run() {
        jdaB = JDABuilder.createDefault(config.token());
        try {
            jda = jdaB.addEventListeners(new SuggestionListener(this)).build();
            
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        Log.info("Bot started");
    }

    @Override
    public void close()  {
        Log.info("Shutting down bot...");
        jda.shutdown();
    }


    public JDA getJDA() {
        try {
            return jda.awaitReady();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public BotConfig getConfig() {
        return config;
    }
}
