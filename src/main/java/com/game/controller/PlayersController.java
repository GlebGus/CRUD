package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayersServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class PlayersController {

    private PlayersServiceImpl playersService;
    @Autowired
    public PlayersController(PlayersServiceImpl playersService) {
        this.playersService = playersService;
    }

    @PostMapping("/players")
@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> createPlayer(@RequestBody Player player) {
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!isValidForCreate(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Player created = playersService.createPlayer(player);

        return created != null
                ? new ResponseEntity<>(created, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
        @DeleteMapping ("/players/{id}")
        @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable Long id){
        playersService.delete(id);
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Player> getAllUsers(@RequestParam Map<String, String> parameters) {
        return playersService.readAll(parameters);

    }

    @GetMapping("players/count")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Long getCountOfPlayers(@RequestParam Map<String, String> parameters){
        return playersService.countOfPlayers(parameters);
    }

    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Player updatePlayer(@RequestBody Player player,@PathVariable Long id){
        return playersService.updatePlayer(player, id);
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Player findById(@PathVariable Long id){
return playersService.getById(id);
    }
    //methods



    public boolean validDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long minDate = calendar.getTimeInMillis();
        calendar.set(Calendar.YEAR,3000);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long maxDate = calendar.getTimeInMillis();
        return ((date.getTime() >= minDate) && (date.getTime() <=maxDate));
    }
    private boolean isValidForCreate(Player player) {
        boolean result = player.getName() != null;

        if ((player.getTitle() == null)) result = false;
        if ((player.getExperience() == null)) result = false;
        if ((player.getBirthday() == null)) result = false;

        return result && updateDataIsValid(player);
    }
    private boolean updateDataIsValid(Player player) {
        boolean result = (player.getName() == null) || (player.getName().length() <= 12);
        if ((player.getTitle() != null) && (player.getTitle().length() > 30)) result = false;
        if ((player.getExperience() != null) && ((player.getExperience() < 0) || player.getExperience() > 10_000_000))
            result = false;
        if ((player.getBirthday() != null) && (!validDate(player.getBirthday()))) result = false;
        return result;
    }






















}
