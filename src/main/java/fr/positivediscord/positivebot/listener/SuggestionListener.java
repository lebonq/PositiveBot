package fr.positivediscord.positivebot.listener;

import com.esotericsoftware.minlog.Log;

import fr.positivediscord.positivebot.PositiveBot;
import fr.positivediscord.positivebot.Util;
import fr.positivediscord.positivebot.music.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

public class SuggestionListener extends ListenerAdapter {

    private final PositiveBot rykordBot;
    private Emote emoteStonk;
    private Emote emoteNotStonk;
    private Emote aBite;
    private MessageReceivedEvent aPreviousEvent;
    private String[] aEmoteDog = new String[4];
    private Guild aGuild;
    private TextChannel aChannel;


    public SuggestionListener(PositiveBot rykordBot) {
        this.rykordBot = rykordBot;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        initEmotes();

        if(this.aPreviousEvent == null){
            this.aGuild = this.rykordBot.getJDA().getGuildById(this.rykordBot.getConfig().guild());
           Log.info(this.aGuild.getName());
            this.aChannel = this.aGuild.getTextChannelById(this.rykordBot.getConfig().channelPost());
            this.aPreviousEvent = event;
        }

        if(event.getChannelType().compareTo(ChannelType.PRIVATE) == 0 ){//Si mp on return

            if(!(event.getMessage().getAuthor().isBot())){
                Random vRand = new Random();

                if(vRand.nextInt(11) == 5){
                    this.aChannel.sendMessage( event.getAuthor().getAsMention() + " me chuchotte : ").queue();
                }
                else{
                    this.aChannel.sendMessage("On me chuchotte : ").queue();
                }
                

                List<Attachment> vAttachement = event.getMessage().getAttachments();
                if(!vAttachement.isEmpty()){
                    String vExtention = vAttachement.get(0).getFileExtension();
                    CompletableFuture<File> vFileDownloading = vAttachement.get(0).downloadToFile("temp." + vExtention);

                    while(vFileDownloading.isDone()){}
                    
                    File vFile = new File("temp." +vExtention);
                    this.aChannel.sendFile(vFile).queue();
                    return;
                }
                this.aChannel.sendMessage(event.getMessage()).queue();
            }
            
            return;
        }

        User author = event.getAuthor();
        TextChannel channelPost = event.getGuild().getTextChannelById(rykordBot.getConfig().channelPost());
        String[] prefix = event.getMessage().getContentDisplay().split(" ");

        if(event.getChannel().getIdLong() == rykordBot.getConfig().channel() && author.isBot()){
            event.getMessage().addReaction(emoteStonk).complete();
            event.getMessage().addReaction(emoteNotStonk).complete();
            return;
        }

        if(!event.getChannelType().equals(ChannelType.TEXT))
            return;

        if(event.getChannel().getIdLong() == rykordBot.getConfig().channel() && !(author.isBot())){
            
            if(prefix[0].equals("!lepen2022")){
                channelPost.sendMessage("**" + Util.stickTab(prefix) + "**").queue();
                event.getChannel().sendMessage("**" + Util.stickTab(prefix) + "**").queue();
                return;
            }

        }

        String vMessage = event.getMessage().getContentDisplay();
        if(event.getAuthor().getId().equals("208937829936398346") && (vMessage.contains("i") || vMessage.contains("I"))){
            Random vRand = new Random();

            if(25 == vRand.nextInt(51)){
                vMessage = vMessage.replaceAll("i", "i̊");
                vMessage = vMessage.replaceAll("I", "i̊");
                event.getChannel().sendMessage(vMessage).queue();
            }
        }

        if(prefix[0].equals("!bark")){
            Random vRand = new Random();
            event.getChannel().sendMessage("**Ouaf Ouaf** " + this.aEmoteDog[vRand.nextInt(4)])
                    .queue();
            event.getMessage().delete().queue();
            return;
        }

        if(prefix[0].equals("!polytech")){
            event.getMessage().delete().queue();
            this.aPreviousEvent.getMessage().addReaction("🇵").complete();
            this.aPreviousEvent.getMessage().addReaction("🇴").complete();
            this.aPreviousEvent.getMessage().addReaction("🇱").complete();
            this.aPreviousEvent.getMessage().addReaction("🇾").complete();
            this.aPreviousEvent.getMessage().addReaction("🇹").complete();
            this.aPreviousEvent.getMessage().addReaction("🇪").complete();
            this.aPreviousEvent.getMessage().addReaction("🇨").complete();
            this.aPreviousEvent.getMessage().addReaction("🇭").complete();
            return;
        }

        if(prefix[0].equals("!8k")){
            event.getMessage().delete().queue();
            this.aPreviousEvent.getMessage().addReaction("🎱").complete();
            this.aPreviousEvent.getMessage().addReaction("🇰").complete();
            return;
        }

        if(prefix[0].equals("!!bark")){
            event.getMessage().delete().queue();
            VoiceChannel activeVoice = event.getMember().getVoiceState().getChannel();
            if(activeVoice == null){
                event.getChannel().sendMessage("T'es pas dans un vocal sale chien crevé.").queue();
                return;
            }
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(activeVoice); 

            PlayerManager manager = PlayerManager.getInstance();
            manager.loadAndPlay(event.getTextChannel(), "bark.mp3");
            manager.getGuildMusicManager(event.getGuild()).player.setVolume(70);

            audioManager.closeAudioConnection();       
            return;
        }

        if(prefix[0].equals("!!cri")){
            event.getMessage().delete().queue();
            VoiceChannel activeVoice = event.getMember().getVoiceState().getChannel();
            if(activeVoice == null){
                event.getChannel().sendMessage("T'es pas dans un channel vocal").queue();
                return;
            }
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(activeVoice); 

            PlayerManager manager = PlayerManager.getInstance();
            manager.loadAndPlay(event.getTextChannel(), "spotham.mp3");
            manager.getGuildMusicManager(event.getGuild()).player.setVolume(70);

            audioManager.closeAudioConnection();       
            return;
        }

        this.aPreviousEvent = event;
    }

    private void initEmotes() {
        if(emoteStonk == null){
            Log.info("Loading emojis...");
            Guild guild = rykordBot.getJDA().getGuildById(rykordBot.getConfig().guild());
            assert guild != null;
            this.emoteStonk = rykordBot.getJDA().getEmoteById(746062781773774868L);
            this.emoteNotStonk = rykordBot.getJDA().getEmoteById(746062813734371388L);
            this.aBite = rykordBot.getJDA().getEmoteById(745996480564953208L);
            Log.info("Loaded");
            assert emoteStonk != null;
            assert emoteNotStonk != null;

            this.aEmoteDog[0] = ":dog:";
            this.aEmoteDog[1] = ":service_dog:";
            this.aEmoteDog[2] = ":dog2:";
            this.aEmoteDog[3] = ":guide_dog:";
        }
    }
}