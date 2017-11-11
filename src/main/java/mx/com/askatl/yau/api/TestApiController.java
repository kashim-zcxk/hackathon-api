package mx.com.askatl.yau.api;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mx.com.askatl.yau.entity.Availability;
import mx.com.askatl.yau.entity.StationsStatus;
import mx.com.askatl.yau.repository.StationsStatusRepository;
import mx.com.askatl.yau.service.EcobiciService;

@RestController
@RequestMapping(value = "/yau/api/1.0v/test/")
public class TestApiController {

	@Autowired
	private StationsStatusRepository stationsStatusRepository;
	@Autowired
	private EcobiciService ecobiciService;

	@RequestMapping(value = "prediccion/{estacion}/{time}", method = RequestMethod.GET)
	public Object prediccionTest(@PathVariable("estacion") Integer estacion, @PathVariable("time") Integer time,
			Pageable page) {
		return ecobiciService.prediccion(estacion, (float)time/100);
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	public Object obtenerTest(Pageable page) {
		return stationsStatusRepository.findAll(page);
	}

	@RequestMapping(value = "insert", method = RequestMethod.GET)
	public String insertTest() {
		StationsStatus myTest = new StationsStatus();
		Availability myAv = new Availability();

		myAv.setBikes(0);
		myAv.setSlots(27);
		myTest.setAvailability(myAv);
		myTest.setFecha(new Date());
		myTest.setIdEstacion(0);
		myTest.setStatus("Hola pipo, tomemos lejia!!");

		stationsStatusRepository.save(myTest);
		return "vale";
	}
}
