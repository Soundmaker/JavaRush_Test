package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Data;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.ExceptionBAD_REQUEST;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    @Autowired
    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @PostMapping
    public Player createPlayer(@RequestBody Data params) {


        if (params.getName() == null ||
                params.getTitle() == null ||
                params.getRace() == null ||
                params.getProfession() == null ||
                params.getBirthday() == null ||
                params.getExperience() == null) throw  new ExceptionBAD_REQUEST();

        if (params.getName().length() > 12) throw  new ExceptionBAD_REQUEST();
        if (params.getTitle().length() > 30) throw  new ExceptionBAD_REQUEST();

        if (params.getName().equals("")) throw  new ExceptionBAD_REQUEST();

        if (!(params.getExperience() >= 0 && params.getExperience() <= 10_000_000)) throw  new ExceptionBAD_REQUEST();

        if (params.getBirthday().getTime() < 0) throw  new ExceptionBAD_REQUEST();

        Date date0 = new Date(100, 0, 1);
        Date date1 = new Date(1100, 0, 1);
        if (!(params.getBirthday().getYear() >= date0.getYear() && params.getBirthday().getYear() <= date1.getYear())) throw  new ExceptionBAD_REQUEST();

        Race[] races = Race.values();
        for (int i = 0; i < races.length; i++) {
            if (races[i] == params.getRace()) break;
            if (i == races.length - 1) throw  new ExceptionBAD_REQUEST();
        }

        Profession[] professions = Profession.values();
        for (int i = 0; i < professions.length; i++) {
            if (professions[i] == params.getProfession()) break;
            if (i == professions.length - 1) throw  new ExceptionBAD_REQUEST();
        }

        if (params.getBanned() == null) params.setBanned(false);

        return service.createPlayer(params);
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable Long id) {
        if (id < 1) throw  new ExceptionBAD_REQUEST();
        return service.getPlayer(id);
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Long id) {
        if (id < 1) throw  new ExceptionBAD_REQUEST();
        service.deletePlayer(id);
    }

    @GetMapping
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
        return service.getAllWithoutFilters(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,
                order,
                pageNumber,
                pageSize);
    }

    @PostMapping("/{id}")
    public Player updatePlayer(@RequestBody Data params, @PathVariable Long id) {
        if (id < 1) throw  new ExceptionBAD_REQUEST();

        //  длина значения параметра “name” или “title” превышает размер соответствующего поля в БД (12 и 30 символов);
        //  значение параметра “name” пустая строка;
        if (params.getName() != null) {
            if ( (params.getName().length() > 12) || params.getName().equals("") ) throw  new ExceptionBAD_REQUEST();
        }
        if (params.getTitle() != null) {
            if (params.getTitle().length() > 30) throw  new ExceptionBAD_REQUEST();
        }

        //  “birthday”:[Long] < 0;
        if (params.getBirthday() != null) {
            if (params.getBirthday().getTime() < 0) throw  new ExceptionBAD_REQUEST();
        }

        //  опыт находится вне заданных пределов;
        if (params.getExperience() != null) {
            if (!(params.getExperience() >= 0 && params.getExperience() <= 10_000_000)) throw  new ExceptionBAD_REQUEST();
        }

        //  дата регистрации находятся вне заданных пределов.
        if (params.getBirthday() != null) {
            Date date0 = new Date(100, 0, 1);
            Date date1 = new Date(1100, 0, 1);
            if (!(params.getBirthday().getYear() >= date0.getYear() && params.getBirthday().getYear() <= date1.getYear())) throw  new ExceptionBAD_REQUEST();
        }

        // Проверяем рассу.
        if (params.getRace() != null) {
            Race[] races = Race.values();
            for (int i = 0; i < races.length; i++) {
                if (races[i] == params.getRace()) break;
                if (i == races.length - 1) throw  new ExceptionBAD_REQUEST();
            }
        }

        // Проверяем профессию.
        if (params.getProfession() != null) {
            Profession[] professions = Profession.values();
            for (int i = 0; i < professions.length; i++) {
                if (professions[i] == params.getProfession()) break;
                if (i == professions.length - 1) throw  new ExceptionBAD_REQUEST();
            }
        }

        return service.updatePlayer(params, id);
    }

    @GetMapping("/count")
    public Long getCount(String name,
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
        return service.getCount(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
    }
}