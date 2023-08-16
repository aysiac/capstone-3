package com.techelevator.controller;

import com.techelevator.dao.*;
import com.techelevator.model.Food;
import com.techelevator.model.Meals;
import com.techelevator.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
@CrossOrigin()
@PreAuthorize("isAuthenticated()")
@RestController
public class MealsController {
    private JdbcTemplate jdbcTemplate;
    private MealsDao mealsDao;
    private FoodDao foodDao;
    private UserDao userDao;

    public MealsController(MealsDao mealsDao, FoodDao foodDao, UserDao userDao) {
        this.mealsDao = mealsDao;
        this.foodDao = foodDao;
        this.userDao = userDao;
    }

    @GetMapping("/meals")
    public List<Meals> listMeals(Principal principal) {
        //Profile profile = profileDao.getProfileById(profileId);
        User user = userDao.getUserByUsername(principal.getName());
        if (user != null) {
            return mealsDao.findAll(user.getId());
        } else {
        }
        return null;
    }


    @GetMapping("/meals/{mealId}")
    public Meals get(@PathVariable int mealId, Principal principal) {
        //Profile profile = profileDao.getProfileById(profileId);
        User user = userDao.getUserByUsername(principal.getName());
        Meals meals = new Meals();
        if (user != null)
        {
            meals = mealsDao.getMealById(mealId);
            if (meals != null)
            {
                return meals;
            }
        }
        return meals;
    }

//    @GetMapping("/meals/{mealId}/calories")
//    public int calTotal(@PathVariable int mealId, Principal principal, Meals meals){
//        User user = userDao.getUserByUsername(principal.getName());
//        meals.setUserId(user.getId());
//        int meal = mealsDao.getTotalCalories(mealId);
//        return meal;
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/createMeal")
    public Meals createMeal(@RequestBody Meals meals, Principal principal) {
        User user = userDao.getUserByUsername(principal.getName());
        meals.setUserId(user.getId());
        if(user != null){
            return mealsDao.createMeal(meals);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }
    @PostMapping("meals/{mealId}/addFood")
    public int addFoodToMeal(@PathVariable int mealId, @RequestBody Food food, @RequestBody Meals meals) {
        mealsDao.addFoodToMeal(food.getFoodId(), meals.getLogDay(), mealId);
        return mealId;
        }
    //        try {
//            int rowsAffected = mealsDao.addFoodToMeal(mealId);
//            if(rowsAffected > 0){
//                Meals updatedMeal = mealsDao.getMealById(mealId);
//                return updatedMeal;
//            }else{
//                return null;
//            }
//        }catch (DaoException e){
//            return null;


    @PutMapping("/meals/{mealId}")
    public Meals update(Principal principal, @RequestBody Meals updatedMeal,@PathVariable int mealId){
        Meals newMeal = mealsDao.updateMealsById(updatedMeal, mealId);
        return newMeal;

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/meals/{mealId}")
    public void delete ( @PathVariable int mealId, Principal principal){
        User user = userDao.getUserByUsername(principal.getName());
        if (user != null) {
            mealsDao.deleteMealById(mealId);
        }
        if (mealsDao.deleteMealById(mealId) != 1) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Meal not found");
        }
    }
}



