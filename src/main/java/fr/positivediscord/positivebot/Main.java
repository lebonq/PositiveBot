package fr.positivediscord.positivebot;

import com.esotericsoftware.minlog.Log;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
        Log.info("Starting RykordBot v" + PositiveBot.VERSION);

        File configFile = new File("config.properties");

        if(!configFile.exists()){
            Log.info("Configuration file does not exist yet, copying default");
            try(InputStream fis = PositiveBot.class.getResourceAsStream("/config.properties")){
                Files.copy(fis, configFile.toPath());
                Log.info("Bot is shutting down. Edit config and launch me again.");
            } catch (IOException e) {
                Log.error("Could not save default config!", e);
            }
            return;
        }


        BotConfig botConfig = ConfigFactory.create(BotConfig.class);
        Log.info("Loaded config");
        PositiveBot bot = new PositiveBot(botConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::close));
        bot.run();
        
        LolFred lolapi = new LolFred(bot);
        lolapi.run();

    }
}
