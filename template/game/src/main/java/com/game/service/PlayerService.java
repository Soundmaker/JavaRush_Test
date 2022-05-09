package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Data;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.ExceptionNOT_FOUND;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PlayerService {

    @Autowired
    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public Player createPlayer(Data params) {

        Integer L = (int) (Math.sqrt(2500 + 200 * params.getExperience()) - 50) / 100;

        Integer N = 50 * (L + 1) * (L + 2) - params.getExperience();

        Player player = new Player();

        player.setName(params.getName());
        player.setTitle(params.getTitle());
        player.setRace(params.getRace());
        player.setProfession(params.getProfession());
        player.setExperience(params.getExperience());
        player.setLevel(L);
        player.setUntilNextLevel(N);
        player.setBirthday(params.getBirthday());
        player.setBanned(params.getBanned());

        return repository.save(player);
    }

    public Player getPlayer(Long id) {
        return repository.findById(id).orElseThrow(ExceptionNOT_FOUND::new);
    }

    public void deletePlayer(Long id) {
        if (repository.existsById(id)) repository.deleteById(id);
        else throw new ExceptionNOT_FOUND();
    }

    public Player updatePlayer(Data params, Long id) {
        Player player = repository.findById(id).orElseThrow(ExceptionNOT_FOUND::new);

        if (params.getName() != null) player.setName(params.getName());
        if (params.getTitle() != null) player.setTitle(params.getTitle());
        if (params.getRace() != null) player.setRace(params.getRace());
        if (params.getProfession() != null) player.setProfession(params.getProfession());
        if (params.getExperience() != null) {

            Integer level = (int) (Math.sqrt(2500 + 200 * params.getExperience()) - 50) / 100;
            Integer nextLevel = 50 * (level + 1) * (level + 2) - params.getExperience();

            player.setExperience(params.getExperience());
            player.setLevel(level);
            player.setUntilNextLevel(nextLevel);
        }

        if (params.getBirthday() != null) player.setBirthday(params.getBirthday());
        if (params.getBanned() != null) player.setBanned(params.getBanned());

        return repository.save(player);
    }

    public long getCount(String name,
                         String title,
                         Race race,
                         Profession profession,
                         Long after,
                         Long before,
                         Boolean banned,
                         Integer minExperience,
                         Integer maxExperience,
                         Integer minLevel,
                         Integer maxLevel) {

        if (repository.count() == 0) return 0L;

        Stream<Player> playerStream = ((List<Player>) repository.findAll()).stream().filter(new Predicate<Player>() {
            @Override
            public boolean test(Player player) {
                if (name != null) if (player.getName().indexOf(name) < 0) return false;
                if (title != null) if (player.getTitle().indexOf(title) < 0) return false;
                if (race != null) if (!player.getRace().toString().equals(race.toString())) return false;
                if (profession != null)
                    if (!player.getProfession().toString().equals(profession.toString())) return false;
                if (after != null) if (player.getBirthday().getTime() < after) return false;
                if (before != null) if (player.getBirthday().getTime() > before) return false;
                if (banned != null) if (!(player.getBanned().equals(banned))) return false;
                if (minExperience != null) if (player.getExperience() < minExperience) return false;
                if (maxExperience != null) if (player.getExperience() > maxExperience) return false;
                if (minLevel != null) if (player.getLevel() < minLevel) return false;
                if (maxLevel != null) if (player.getLevel() > maxLevel) return false;
                return true;
            }
        });

        return playerStream.count();
    }


    public List<Player> getAllWithoutFilters(String name,
                                             String title,
                                             Race race,
                                             Profession profession,
                                             Long after,
                                             Long before,
                                             Boolean banned,
                                             Integer minExperience,
                                             Integer maxExperience,
                                             Integer minLevel,
                                             Integer maxLevel,
                                             PlayerOrder order,
                                             Integer pageNumber,
                                             Integer pageSize) {

        Stream<Player> playerStream = ((List<Player>) repository.findAll()).stream().filter(new Predicate<Player>() {
            @Override
            public boolean test(Player player) {
                if (name != null) if (player.getName().indexOf(name) < 0) return false;
                if (title != null) if (player.getTitle().indexOf(title) < 0) return false;
                if (race != null) if (!player.getRace().toString().equals(race.toString())) return false;
                if (profession != null) if (!player.getProfession().toString().equals(profession.toString())) return false;
                if (after != null) if (player.getBirthday().getTime() < after) return false;
                if (before != null) if (player.getBirthday().getTime() > before) return false;
                if (banned != null) if (!(player.getBanned().equals(banned))) return false;
                if (minExperience != null) if (player.getExperience() < minExperience) return false;
                if (maxExperience != null) if (player.getExperience() > maxExperience) return false;
                if (minLevel != null) if (player.getLevel() < minLevel) return false;
                if (maxLevel != null) if (player.getLevel() > maxLevel) return false;
                return true;
            }
        });

        List<Player> players = playerStream.collect(Collectors.toList());

        if (order != null) {
            if (order == PlayerOrder.NAME)
                Collections.sort(players, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
            if (order == PlayerOrder.EXPERIENCE)
                Collections.sort(players, (o1, o2) -> o1.getExperience().compareTo(o2.getExperience()));
            if (order == PlayerOrder.BIRTHDAY)
                Collections.sort(players, (o1, o2) -> o1.getBirthday().compareTo(o2.getBirthday()));
        } else {
            Collections.sort(players, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        }

        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        List<Player> result = new ArrayList<>();

        for (int i = pageNumber * pageSize; (i < (pageNumber + 1) * pageSize) && (i < players.size()); i++) {
            result.add(players.get(i));
        }

        return result;
    }
}