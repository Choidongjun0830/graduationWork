package graduationWork.server.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

@Embeddable
public class Address {

    private String city;
    private String street;
    private String zipCode;
}
