package net.clarenceho.multi_liquibase.web;

import jakarta.transaction.Transactional;
import net.clarenceho.multi_liquibase.modules.vehicle.models.Car;
import net.clarenceho.multi_liquibase.modules.vehicle.repositories.CarRepository;
import net.clarenceho.multi_liquibase.modules.vessel.models.Frigate;
import net.clarenceho.multi_liquibase.modules.vessel.repositories.FrigateRepository;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Transactional
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    CarRepository carRepository;
    @Autowired
    FrigateRepository frigateRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("cars", carRepository.findAllByOrderByIdAsc());
        model.addAttribute("frigates", frigateRepository.findAllByOrderByIdAsc());
        return "home";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String update(Model model, @RequestParam("type") String type) {
        if ("car".equals(type)) {
            List<Car> cars = carRepository.findAll();
            cars.forEach(c -> {
                c.setPrice(Precision.round(c.getPrice() * 1.1, 2));
                carRepository.save(c);
            });
        } else if ("frigate".equals(type)) {
            List<Frigate> frigates = frigateRepository.findAll();
            frigates.forEach(f -> {
                f.setMaxSpeed(Precision.round(f.getMaxSpeed() * 1.1, 2));
                frigateRepository.save(f);
            });
        }

        model.addAttribute("appName", appName);
        model.addAttribute("cars", carRepository.findAllByOrderByIdAsc());
        model.addAttribute("frigates", frigateRepository.findAllByOrderByIdAsc());
        return "home";
    }

}