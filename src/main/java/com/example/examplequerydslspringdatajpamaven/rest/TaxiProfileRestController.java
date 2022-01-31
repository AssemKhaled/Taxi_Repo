package com.example.examplequerydslspringdatajpamaven.rest;
import com.example.examplequerydslspringdatajpamaven.data.dtos.TaxiProfileDto;
import com.example.examplequerydslspringdatajpamaven.service.TaxiProfileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/taxiProfile")
@CrossOrigin
@RestController
public class TaxiProfileRestController {


    private final TaxiProfileServiceImpl taxiProfileServiceImpl;

    public TaxiProfileRestController(TaxiProfileServiceImpl taxiProfileServiceImpl) {
        this.taxiProfileServiceImpl = taxiProfileServiceImpl;
    }

    @RequestMapping(value = "/getAllTaxiProfile", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getTaxiProfile(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
                                                          @RequestParam(value = "name", defaultValue = "") String name,
                                                          @RequestParam(value = "loggedId", defaultValue = "0") Long loggedId,
                                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                          @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber){

        return taxiProfileServiceImpl.getAllTaxiProfile(TOKEN, loggedId, name, pageSize, pageNumber);
    }
    @PostMapping(value = "/createTaxiProfile")
    public @ResponseBody ResponseEntity<?> getTaxiProfile(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
                                                          @RequestParam(value = "loggedId", defaultValue = "0") Long loggedId,
                                                          @RequestBody(required = false) TaxiProfileDto taxiProfileDto
                                                          ){

        return taxiProfileServiceImpl.createTaxiProfile(TOKEN, loggedId, taxiProfileDto);
    }

    @PostMapping(value = "/editTaxiProfile")
    public @ResponseBody ResponseEntity<?> editTaxiProfile(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
                                                          @RequestParam(value = "loggedId", defaultValue = "0") Long loggedId,
                                                          @RequestBody(required = false) TaxiProfileDto taxiProfileDto){

        return taxiProfileServiceImpl.editTaxiProfile(TOKEN, loggedId, taxiProfileDto);
    }

    @PostMapping(value = "/deleteTaxiProfile")
    public @ResponseBody ResponseEntity<?> deleteTaxiProfile(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
                                                             @RequestParam(value = "loggedId", defaultValue = "0") Long loggedId,
                                                             @RequestParam(value = "userId") Long userId) {

        return taxiProfileServiceImpl.deleteTaxiProfile(TOKEN, loggedId, userId);
    }

    @RequestMapping(value = "/getChildrenTaxiProfiles", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getChildrenTaxiProfiles(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
                                                                   @RequestParam(value = "loggedId", defaultValue = "0") Long loggedId,
                                                                   @RequestParam(value = "userId") Long userId,
                                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                                   @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber){
        return taxiProfileServiceImpl.GetUserChildren(TOKEN, loggedId, userId, pageSize, pageNumber);
    }
}

//    ------------------------------------Commented work--------------------------------------------------------
//    @RequestMapping(value = "/getTaxiProfileByName", method = RequestMethod.GET)
//    public @ResponseBody ResponseEntity<?> getTaxiProfileByName(@RequestParam(value = "name", defaultValue = "") String name/*,
//                                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
//                                                                @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber*/){
//
//        return taxiProfileServiceImpl.getTaxiProfileByName(name);
//    }

