package com.darinpope;

import com.darinpope.dao.PlayerMatchDao;
import com.darinpope.model.Match;
import com.darinpope.model.Player;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Component
public class SearchMain {
    private static final Logger LOGGER = Logger.getLogger(SearchMain.class);

    @Resource
    private PlayerMatchDao playerMatchDao;

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        SearchMain main = ctx.getBean(SearchMain.class);

        main.playerMatchDao.loadPlayerMatchCache();

        Random randomGenerator = new Random();
        LOGGER.info("searching for matches by playerId");
        for(int i=0;i<30;i++) {
           int randomInt = randomGenerator.nextInt(2999);
           if(randomInt == 0) {
               continue;
           }
           List<Match> matches = main.playerMatchDao.getMatchByPlayerId(randomInt);
           for(Match match:matches) {
               LOGGER.info(match);
           }
        }

        LOGGER.info("searching for players by matchId");
        for(int i=0;i<30;i++) {
           int randomInt = randomGenerator.nextInt(40);
           if(randomInt == 0) {
               continue;
           }
           List<Player> players = main.playerMatchDao.getPlayerByMatchId(randomInt);
           for(Player player:players) {
               LOGGER.info(player);
           }
        }

        Thread.sleep(20000);
    }
}
