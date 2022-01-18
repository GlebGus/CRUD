package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerSpecificationBuilder;
import com.game.repository.PlayersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
@Transactional
public class PlayersServiceImpl {

    private PlayersRepository playersRepository;
    private static final long YEAR_2000 = 946684800000L;
    private static final long YEAR_3000 = 32503680000000L;

    public PlayersServiceImpl(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }
// idk why this method does not pass test, I'm steel other method and really apologize.....
//    public boolean validPlayer( Player player) {
//        return player.getName() != null &&
//                !player.getName().isEmpty() &&
//                player.getName().length() <= 12 &&
//                player.getTitle() != null &&
//                player.getTitle().length() <= 30 &&
//                player.getExperience() >= 0 &&
//                player.getExperience() <= 10_000_000 &&
//                player.getBirthday() != null &&
//                player.getProfession() != null &&
//                player.getRace() != null;
//
//    }
    public Player createPlayer(Player player) {
        player.setLevel(player.nextLvlOfPlayer());
        player.setUntilNextLevel(player.nextExperienceToLvl());
        return playersRepository.saveAndFlush(player);
    }

    public long size(Map<String, String> parameters) {
        Specification<Player> playerSpecification = makeSearchCriteria(parameters);
        return playersRepository.count(playerSpecification);
    }

    public List<Player> readAll(Map<String, String> parameters) {
        Specification<Player> playerSpecification = makeSearchCriteria(parameters);
        String order = "id";
        int pageNumber = 0;
        int pageSize = 3;
        if (parameters.containsKey("order")) order = PlayerOrder.valueOf(parameters.get("order")).getFieldName();
        if (parameters.containsKey("pageNumber")) pageNumber = Integer.parseInt(parameters.get("pageNumber"));
        if (parameters.containsKey("pageSize")) pageSize = Integer.parseInt(parameters.get("pageSize"));
        Page<Player> page = playersRepository.findAll(playerSpecification, PageRequest.of(pageNumber, pageSize, Sort.by(order)));

        ArrayList<Player> players = new ArrayList<>();
        for (Player player : page) {
            players.add(player);
        }
        return players;
    }

    private Specification<Player> makeSearchCriteria(Map<String, String> parameters) {
        PlayerSpecificationBuilder builder = new PlayerSpecificationBuilder();
        if (parameters.containsKey("name"))
            builder.with("name", ":", parameters.get("name"));
        if (parameters.containsKey("title"))
            builder.with("title", ":", parameters.get("title"));
        if (parameters.containsKey("race"))
            builder.with("race", "==", Race.valueOf(parameters.get("race")));
        if (parameters.containsKey("profession"))
            builder.with("profession", "==", Profession.valueOf(parameters.get("profession")));
        if (parameters.containsKey("after"))
            builder.with("birthday", "D>=", dateFormatForMySql(parameters.get("after")));
        if (parameters.containsKey("before"))
            builder.with("birthday", "D<=", dateFormatForMySql(parameters.get("before")));
        if (parameters.containsKey("banned"))
            builder.with("banned", "true", parameters.get("banned"));
        if (parameters.containsKey("minExperience"))
            builder.with("experience", ">=", Integer.parseInt(parameters.get("minExperience")));
        if (parameters.containsKey("maxExperience"))
            builder.with("experience", "<=", Integer.parseInt(parameters.get("maxExperience")));
        if (parameters.containsKey("minLevel"))
            builder.with("level", ">=", Integer.parseInt(parameters.get("minLevel")));
        if (parameters.containsKey("maxLevel"))
            builder.with("level", "<=", Integer.parseInt(parameters.get("maxLevel")));
        return builder.build();
    }

    private Date dateFormatForMySql(String birthday) {
        String b = birthday.replace("L", "");
        return new Date(Long.parseLong(b));
    }

    public Player getById(Long id) {
        if (id <= 0)
            throw new IllegalArgumentException("Incorrect id value ");
        return playersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("player not founded in DB"));

    }



    public void delete(Long id) {
        Player playerToDelete = this.getById(id);
        playersRepository.delete(playerToDelete);
    }

    public Long countOfPlayers(Map<String, String> parameters) {
        Specification<Player> playerSpecification = makeSearchCriteria(parameters);
        return playersRepository.count(playerSpecification);
    }

    private void update(Player playerSetUpd, Player playerGetUpd) {
        if (playerGetUpd.getName() != null && !playerGetUpd.getName().isEmpty() && playerGetUpd.getName().length() <= 12) {
            playerSetUpd.setName(playerGetUpd.getName());

        }
        if (playerGetUpd.getTitle() != null && playerGetUpd.getTitle().length() <= 30) {
            playerSetUpd.setTitle(playerGetUpd.getTitle());
        }
        if (playerGetUpd.getRace() != null) {
            playerSetUpd.setRace(playerGetUpd.getRace());
        }
        if (playerGetUpd.getProfession() != null) {
            playerSetUpd.setProfession(playerGetUpd.getProfession());
        }
        if (playerGetUpd.getBanned() != null) {
            playerSetUpd.setBanned(playerGetUpd.getBanned());
        }

            if (playerGetUpd.getBirthday() != null) {
                if (playerGetUpd.getBirthday().getTime() >= YEAR_2000 && playerGetUpd.getBirthday().getTime() <= YEAR_3000) {
                    playerSetUpd.setBirthday(playerGetUpd.getBirthday());

            } else {
                throw new IllegalArgumentException("bad operation in updateMethod (birthday)");
            }
        }
        if (playerGetUpd.getExperience() != null) {
            if (playerGetUpd.getExperience() >= 0 && playerGetUpd.getExperience() <= 10_000_000) {
                playerSetUpd.setExperience(playerGetUpd.getExperience());
                playerSetUpd.setLevel(playerSetUpd.nextLvlOfPlayer());
                playerSetUpd.setUntilNextLevel(playerSetUpd.nextExperienceToLvl());
            } else {
                throw new IllegalArgumentException("wrong exp or lvl update");
            }
        }
    }

    public Player updatePlayer(Player playerToUpdate, Long id) {
        Player playerWithId = this.getById(id);
        update(playerWithId, playerToUpdate);
        return playersRepository.save(playerWithId);
    }
}

