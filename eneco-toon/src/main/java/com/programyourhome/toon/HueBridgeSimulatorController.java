package com.programyourhome.toon;

import java.awt.Color;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.huebridgesimulator.model.menu.MenuItem;
import com.programyourhome.huebridgesimulator.model.menu.SimColor;

@RestController
@RequestMapping("huebridgesimulator")
public class HueBridgeSimulatorController {

    private String item1 = "Test name";
    private String item2 = "Other test";

    @RequestMapping(value = "currentMenu", method = RequestMethod.GET)
    public MenuItem[] getCurrentMenu() {
        return new MenuItem[] { new MenuItem(this.item1, new SimColor(Color.RED), true), new MenuItem(this.item2, new SimColor(Color.BLUE), false) };
    }

    @RequestMapping(value = "menuItemSelected/{name}", method = RequestMethod.PUT)
    public void menuItemSelected(@PathVariable("name") final String itemName) {
        final String temp = this.item1;
        this.item1 = this.item2;
        this.item2 = temp;
    }

}
