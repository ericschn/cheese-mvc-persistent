package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    private String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String menuView(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);

        return "menu/view";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        Iterable<Cheese> cheeses = cheeseDao.findAll();

        AddMenuItemForm form = new AddMenuItemForm(menu, cheeses);

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String ProcessAddItem(@ModelAttribute  @Valid AddMenuItemForm form,
                                 Errors errors, @RequestParam int menuId,
                                 @RequestParam int cheeseId, Model model) {

        Menu menu = menuDao.findOne(menuId);

        Cheese cheese = cheeseDao.findOne(cheeseId);

        menu.addItem(cheese);

        menuDao.save(menu);

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());

        return "redirect:/" + "menu/view/" + menu.getId();
    }

}
