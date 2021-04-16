package fr.positivediscord.positivebot;

import java.util.Map;
import java.util.Set;

import com.esotericsoftware.minlog.Log;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.Match;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.Participant;
import net.rithms.riot.api.endpoints.match.dto.ParticipantStats;
import net.rithms.riot.api.endpoints.match.dto.ParticipantTimeline;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

public class LolFred implements Runnable{

    private ApiConfig aConfig;
    private RiotApi aApi;
    private Summoner aSummoner;
    private long aLastestGameId;
    private PositiveBot aPositiveBot;
    private TextChannel aChannel;
    private long aIdPythaarDiscord;
    private User quentDm;
    private PrivateChannel quentDmChannel;

    public LolFred(PositiveBot pBot){
        this.aPositiveBot = pBot;
        this.aConfig = new ApiConfig().setKey(this.aPositiveBot.getConfig().riotApi());
        this.aApi = new RiotApi(this.aConfig);
        try {
            this.aSummoner = this.aApi.getSummoner(Platform.EUW, "7VynR__ltNA4IAByLrqU4FLxNWvN5IAcNPXVkcdLU_ctu_o");
            MatchList matchList =  this.aApi.getMatchListByAccountId(Platform.EUW, this.aSummoner.getAccountId());
            this.aLastestGameId = matchList.getMatches().get(matchList.getStartIndex()).getGameId();
        } catch (RiotApiException e) {
            e.printStackTrace();
        }

        long guildId = (long)this.aPositiveBot.getConfig().guild();

        Guild vGuild = this.aPositiveBot.getJDA().getGuildById(guildId);
        Log.info( "Serveur d'envoie message LoL " + vGuild.getName());
        this.aChannel = vGuild.getTextChannelById(this.aPositiveBot.getConfig().channelPost());
        Log.info("Channel d'envoie message LoL " + this.aChannel.getName());
        this.aIdPythaarDiscord = 208936665975095296L;

       
        this.quentDm = this.aPositiveBot.getJDA().retrieveUserById("187568513035141120").complete();
        this.quentDmChannel = this.quentDm.openPrivateChannel().complete();//open quent dm
    }

    @Override
    public void run() {
        this.quentDmChannel.sendMessage("Fred lol lance").queue();
            while(true){

                try{
                    MatchList matchList =  this.aApi.getMatchListByAccountId(Platform.EUW, this.aSummoner.getAccountId());
                    long vIdLastestGame = matchList.getMatches().get(matchList.getStartIndex()).getGameId();

                    if(vIdLastestGame != this.aLastestGameId){
                        Match vMatch = this.aApi.getMatch(Platform.EUW, vIdLastestGame);

                        Participant vParticipant = vMatch.getParticipantByAccountId(this.aSummoner.getAccountId());
                        ParticipantStats vStats = vParticipant.getStats();
                        ParticipantTimeline vTimeline = vParticipant.getTimeline();

                        String message = "";
                        
                        if(!vStats.isWin()){
                            message+="Bravo à Pythaar pour avoir encore lose en "+ vMatch.getGameMode().toLowerCase() + " ! Avec " + vStats.getDeaths() +
                            " morts en jouant " + Util.getChampById(vParticipant.getChampionId()) + "\n";
                            if(vStats.getTotalDamageDealtToChampions() < 10000){
                                message+="Avec seulement " + vStats.getTotalDamageDealtToChampions() + " de damages ahahahah"+ "\n";
                            }
                            double vcreep = 0.0;

                            Map<String, Double> vMap = vTimeline.getCreepsPerMinDeltas();
                            Set<String> vKey = vMap.keySet();

                            for (String string : vKey) {
                                vcreep += vMap.get(string);
                            }
                            vcreep /= vMap.size();

                            if(vcreep < 7){
                                message+="Avec " + (int)vcreep + " creeps par minute quelle honte"+ "\n";
                            }
                            message+="Et oui la " + vTimeline.getLane().toLowerCase() + " lane est mal gardée"+ "\n";
                        }
                        else{
                            message+="Bravo à Pythaar pour avoir encore wins ! Avec " + vStats.getKills() + " kills en jouant " + Util.getChampById(vParticipant.getChampionId()) + " 🥰"+ "\n";
                        }
                        int vMinutes = vStats.getLongestTimeSpentLiving()/60;
                        int vSecondes =  vStats.getLongestTimeSpentLiving()%60;

                        message+="Ta plus longue duree vivant est " + vMinutes + " minutes et " + vSecondes + " secondes belle perf ^^"+ "\n";
                        this.aLastestGameId = vIdLastestGame;
                        this.aChannel.sendMessage(message).queue();
                    }

                }
                catch(RiotApiException e){
                    this.quentDmChannel.sendMessage("Error API Riot \n " + e).queue();
                    Log.error("" + e.getErrorCode());
                    e.printStackTrace();
                    if(e.getErrorCode() == RiotApiException.GATEWAY_TIMEOUT || e.getErrorCode() ==  RiotApiException.UNAVAILABLE) this.run();
                }

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    this.quentDmChannel.sendMessage("Error fred interrupted \n " + e).queue();
                }
            }
    }
    
}
