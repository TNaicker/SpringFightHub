package com.tnikes.fighthub.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnikes.fighthub.model.Game;
import com.tnikes.fighthub.model.GameCharacter;
import com.tnikes.fighthub.model.Normals;
import com.tnikes.fighthub.service.ICharacterService;
import com.tnikes.fighthub.service.IGameService;
import com.tnikes.fighthub.service.INormalService;

@RestController
public class MainDataController {
	
//	final static String DEFAULT_API_PATH = "/fighthubAPI";
	
	//Services
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private ICharacterService characterService;
	
	@Autowired
	private INormalService normalService;
	
	//Default path 
	@RequestMapping(value = "/fighthub-api/", method = RequestMethod.GET)
	public String defaultPath() {
		
		return "o hi there";
	}
	
	//Path for the games. Only needs to have a get request since I don't want to be able to edit these values in the db from the api?
	@RequestMapping(value = "/fighthub-api/games", method = RequestMethod.GET)
 	public ResponseEntity<List<Game>> findGames(@RequestParam(value = "category", required = false) String category,
 								@RequestParam(value = "gameName", required = false) String name) {
		
		//Checking if either parameters exist
		Boolean categoryCheck, nameCheck;
		categoryCheck = category == null ? false : true;
		nameCheck = name == null ? false : true;
		
		//Declaring List to return here
		List<Game> games = new ArrayList<>();
		
		//Checking if either parameters exist in the path and returns a list accordingly
		//Did this instead of creating 2 separate request mapping
		//Should I move this logic out of here?
		if(nameCheck) {
			games = (List<Game>) gameService.findGameByName(name);
		} else if(categoryCheck) {
			games = (List<Game>) gameService.findGameByGenre(category);
		} else {
			games = (List<Game>) gameService.findAllGames();
		}
		
		return ResponseEntity.ok().body(games);
	}
	
	//Path for the characters. There should probably be a separate path for game specified characters. EX: guiltygear/characters or guiltygear/characters?name=venom
	//Again, do not need to run CUD of CRUD for this endpoint.
	@RequestMapping(value = "/fighthub-api/characters", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<List<GameCharacter>> findCharacters(@RequestParam(value = "type", required = false) Integer gameId,
											@RequestParam(value = "name", required = false) String name) {
		
		//Checking if the gameid is provided. This should really be a name(String) that gets converted to the required id from a SQL search.
		//The below logic should pull up a game id given a string for a fighting game name. This should not be within the controller itself though.
		Boolean gameIdCheck, nameCheck;
		nameCheck = name == null ? false : true;
		gameIdCheck = gameId == null ? false : true;
		
		List<GameCharacter> gameCharacters = new ArrayList<>();
		
		//Checking if gameid is provided and returning characters for specific game. If not, returning all characters lol.
		if(gameIdCheck) {
			gameCharacters = (List<GameCharacter>) characterService.findCharactersByGame(gameId);
		} else if(nameCheck) {
			gameCharacters = (List<GameCharacter>) characterService.findCharacterByName(name);
		} else {
			gameCharacters = (List<GameCharacter>) characterService.findAllCharacters();
		}
		
		return ResponseEntity.ok().body(gameCharacters);
	}
	
	//Path for the normal moves. As of right now this is ok since it's just pulling guilty gear data, but the sql statement needs to pull only the first result. Very brittle
	//No point in making the request parameter optional, pulling in all of the normals unorganized makes 0 fucking sense.
	//Need to do a join statement to pull in normals categorized into their respective character names in order to make the parameter optional.
	//Path also needs to be something like '/{game}/{character}/normals
	@RequestMapping(value = "/fighthub-api/normals", method = RequestMethod.GET)
	public ResponseEntity<List<Normals>> findNormals(@RequestParam String name) {
		
		List<Normals> normals = (List<Normals>) normalService.findNormalsByChar(name);
		return ResponseEntity.ok().body(normals);
	}
	
}