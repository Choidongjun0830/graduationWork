package graduationWork.server.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Ticket {

    //예약 대행사
    @Id @GeneratedValue
    @Column(name = "ticket_id")
    private Long id;

    private String from;

    private String to;

    private LocalDateTime departureDateTime;

    private String flightNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
