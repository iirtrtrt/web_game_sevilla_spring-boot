package org.springframework.samples.parchisoca.game;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.beans.HasProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.parchisoca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisoca.enums.GameStatus;
import org.springframework.samples.parchisoca.game.GameController;
import org.springframework.samples.parchisoca.game.TurnsService;

import static org.hamcrest.Matchers.nullValue;


import org.springframework.samples.parchisoca.game.GameService;
import org.springframework.samples.parchisoca.user.StatisticUser;
import org.springframework.samples.parchisoca.user.User;
import org.springframework.samples.parchisoca.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.samples.parchisoca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import java.util.Optional;


import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value = GameController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE), excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityAutoConfiguration.class)

public class GameControllerTests {


    @Autowired
    private GameController gameController;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TurnsService turnsService;

    @MockBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    private Optional<Game> createTestCreatedGame(){
       System.out.println("Starting");
       Game game = new Game();
       User creator = createTestUser().get();

       game.setAI(false);
       game.setCreator(creator);
       game.setGame_id(1);
       game.setHas_started(false);
       game.setMax_player(2);
       game.setName("newgame");
       System.out.println("before add");
       game.setCurrent_players(creator);
       System.out.println("after add");

       game.setStatus(GameStatus.CREATED);

       return Optional.of(game);
    }

    private Optional<User> createTestUser(){
      User testUser = new User();
      testUser.setUsername("testuser");

      testUser.setFirstname("Max");
      testUser.setLastname("Mustermann");
      testUser.setEmail("Max@web.de");
      testUser.setPassword("12345");
      testUser.setPasswordConfirm("12345");
      StatisticUser statistic = new StatisticUser(1,1,6);
      testUser.setStatistic(statistic);
      Optional<User> userOptional = Optional.of(testUser);
      return userOptional;
   }

   private String JsonUser(User user) throws Exception{
      System.out.println("user to json" + user);
      Map<String, String> input = new HashMap<>();
      input.put("username", user.getUsername());
      input.put("firstname", user.getFirstname());
      input.put("lastname", user.getLastname());
      input.put("email", user.getEmail());
      input.put("password", user.getPassword());
      input.put("passwordConfirm", user.getPasswordConfirm());
      System.out.println("user to json done" + input);


      return objectMapper.writeValueAsString(input);
   }

   private List<Game> createTestGame(){
      //Game game = new Game();
      List<Game> games = new ArrayList();
      //games.add
      return games;
   }

   @BeforeEach
   void init(){
      when(gameController.findAllCreatedGames())
         .thenReturn(createTestGame());

      when(userService.getCurrentUser())
            .thenReturn(createTestUser());
   }


    @Disabled
    @Test
    @WithMockUser(value = "flogam1")
    void testCorrectViewForCreateGet() throws Exception
    {
      mockMvc.perform(get("/game/create"))
         .andDo(print())
         .andExpect(status().isOk())
         .andExpect(view().name("game/createGameForm"));

    
    }

    @Disabled
    @Test
    @WithMockUser(value = "flogam1")
    void testCorrectViewForJoinGet() throws Exception
    {
      mockMvc.perform(get("/game/join"))
         .andDo(print())
         .andExpect(status().isOk())
         .andExpect(view().name("game/joinGameForm"));
    
    }

    /*
    @Test
    void testCorrectModelAttribute() throws Exception
    {
      mockMvc.perform(get("/game/create"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(model().attribute("game", notNull()));
    }*/

    @Disabled
    @Test
    void testGameCreation() throws Exception
    {
      
      mockMvc.perform(post("/game/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(JsonUser(createTestUser().get())))
      .andExpect(status().isOk())
      .andDo(print());
    }

    @Test
    void testParchisJoin() throws Exception
    {

      Integer gameID = 1;
      when(gameService.findGamebyID(gameID))
         .thenReturn(createTestCreatedGame());


      mockMvc.perform(post("/join/Parchis/{gameID}", 1)
      .contentType(MediaType.APPLICATION_JSON)
      .content(JsonUser(createTestUser().get())))
      .andExpect(status().isOk())
      .andDo(print());
    }
    @Disabled
    @Test
    void testOcaJoin() throws Exception
    {

      Integer gameID = 1;
      when(gameService.findGamebyID(gameID))
         .thenReturn(createTestCreatedGame());


      mockMvc.perform(post("/join/Oca/{gameID}", 1)
      .contentType(MediaType.APPLICATION_JSON)
      .content(JsonUser(createTestUser().get())))
      .andExpect(status().isOk())
      .andDo(print());
    }


/*
    @Test
    void testGameCreationDoubleName() throws Exception
    {
      
      mockMvc.perform(post("/game/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(JsonUser(createTestUser().get())))
      .andExpect(status().isOk())
      .andDo(print());

      mockMvc.perform(post("/game/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(JsonUser(createTestUser().get())))
      .andExpect(status().isOk())
      .andDo(print());

    }
*/






}