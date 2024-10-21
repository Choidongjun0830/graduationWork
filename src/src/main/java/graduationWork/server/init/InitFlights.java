package graduationWork.server.init;

import graduationWork.server.domain.Flight;
import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InitFlights {

    private final InitFlightService initFlightService;

   @PostConstruct
   public void init() {
       initFlightService.initFlights();
   }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitFlightService {

        private final EntityManager em;

        public void initFlights() {
            Flight flight1 = new Flight();
            flight1.setDeparture("ICN");
            flight1.setDestination("JFK");
            flight1.setDepartureDate(LocalDateTime.of(2024, 10, 21, 12, 00));
            flight1.setFlightNum("KE081");
            flight1.setStatus(FlightStatus.DELAYED);
            em.persist(flight1);

            Flight flight2 = new Flight();
            flight2.setDeparture("ICN");
            flight2.setDestination("LAX");
            flight2.setDepartureDate(LocalDateTime.of(2024, 10, 22, 12, 00));
            flight2.setFlightNum("KE017");
            flight2.setStatus(FlightStatus.DELAYED);
            em.persist(flight2);

            Flight flight3 = new Flight();
            flight3.setDeparture("ICN");
            flight3.setDestination("YVR");
            flight3.setDepartureDate(LocalDateTime.of(2024, 10, 23, 12, 00));
            flight3.setFlightNum("KE071");
            flight3.setStatus(FlightStatus.DELAYED);
            em.persist(flight3);

            Flight flight4 = new Flight();
            flight4.setDeparture("ICN");
            flight4.setDestination("NRT");
            flight4.setDepartureDate(LocalDateTime.of(2024, 10, 24, 12, 00));
            flight4.setFlightNum("KE701");
            flight4.setStatus(FlightStatus.DELAYED);
            em.persist(flight4);

            Flight flight5 = new Flight();
            flight5.setDeparture("ICN");
            flight5.setDestination("CDG");
            flight5.setDepartureDate(LocalDateTime.of(2024, 10, 25, 12, 00));
            flight5.setFlightNum("KE901");
            flight5.setStatus(FlightStatus.DELAYED);
            em.persist(flight5);

            Flight flight6 = new Flight();
            flight6.setDeparture("ICN");
            flight6.setDestination("HAN");
            flight6.setDepartureDate(LocalDateTime.of(2024, 10, 26, 12, 00));
            flight6.setFlightNum("OZ765");
            flight6.setStatus(FlightStatus.DELAYED);
            em.persist(flight6);

            Flight flight7 = new Flight();
            flight7.setDeparture("ICN");
            flight7.setDestination("OKA");
            flight7.setDepartureDate(LocalDateTime.of(2024, 10, 27, 12, 00));
            flight7.setFlightNum("OZ172");
            flight7.setStatus(FlightStatus.DELAYED);
            em.persist(flight7);


            Flight flight8 = new Flight();
            flight8.setDeparture("ICN");
            flight8.setDestination("CJU");
            flight8.setDepartureDate(LocalDateTime.of(2024, 10, 28, 12, 00));
            flight8.setFlightNum("7C111");
            flight8.setStatus(FlightStatus.DELAYED);
            em.persist(flight8);
        }
    }
}
