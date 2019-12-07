package com.brewery.controller;

import com.brewery.model.Style;
import com.brewery.service.BrewService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping( "")
public class UiController {

    private BrewService brewService;
    @Autowired
    public void setProductsService(BrewService brewService) {
        this.brewService = brewService;
    }

	
    @RequestMapping(path = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(path = "/style/add", method = RequestMethod.GET)
    public String createStyle(Model model) {
        model.addAttribute("style", new Style());
        return "edit";
    }

    @RequestMapping(path = "/style", method = RequestMethod.POST)
    public String saveStyle(Style style) {
    	brewService.saveStyle(style);
        return "redirect:/";
    }

    
    @RequestMapping(path = "/style", method = RequestMethod.GET)
    public String getAllStyles(Model model) {
        model.addAttribute("styles",  brewService.getAllStyles() );
        return "styles";
    }

    @RequestMapping(path = "/style/edit/{id}", method = RequestMethod.GET)
    public String editStyle(Model model, @PathVariable(value = "id") Long id) {
        model.addAttribute("style", brewService.getStyle(id) );
        return "edit";
    }

    @RequestMapping(path = "/style/delete/{id}", method = RequestMethod.GET)
    public String deleteStyle(@PathVariable(name = "id") Long id) {
    	brewService.deleteStyle(id);
        return "redirect:/style";
    }
}
