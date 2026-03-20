package service;

import org.devberat.DTO.SeatMapDto;
import org.devberat.DTO.TicketDto;
import org.devberat.exception.BaseException;
import org.devberat.model.*;
import org.devberat.repository.IFlightRepository;
import org.devberat.repository.ITicketRepository;
import org.devberat.service.Impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private ITicketRepository ticketRepository;

    @Mock
    private IFlightRepository flightRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Flight mockFlight;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // 1. Mock User Setup
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setFirstName("Berat");

        // 2. Security Context Mock (Lenient - Her testte kullanılmayabilir)
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(auth);
        lenient().when(auth.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);

        // 3. Aircraft & Flight Setup
        Aircraft aircraft = new Aircraft();
        aircraft.setSeatCapacity(100);

        mockFlight = new Flight();
        mockFlight.setId(UUID.randomUUID());
        mockFlight.setAircraft(aircraft);
        mockFlight.setBasePrice(new BigDecimal("1000.00"));
        mockFlight.setCurrentOccupancy(10);
        mockFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
    }

    @Test
    void bookTicket_ShouldApplyBasePrice_WhenOccupancyIsLow() {
        // GIVEN: Düşük doluluk oranı
        mockFlight.setCurrentOccupancy(10);
        TicketDto.BookingRequest request = TicketDto.BookingRequest.builder()
                .flightId(mockFlight.getId())
                .seatNumber("1A")
                .useWallet(false)
                .build();

        when(flightRepository.findById(mockFlight.getId())).thenReturn(Optional.of(mockFlight));
        when(ticketRepository.existsByFlightIdAndSeatNumberAndStatus(any(), any(), any())).thenReturn(false);

        // Mocking Save: Kaydedilen objeyi aynen geri döndür (Fiyatın taşınması için kritik)
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        TicketDto.Info result = ticketService.bookTicket(request);

        // THEN
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getPrice(), "Price should not be null");
        assertEquals(0, new BigDecimal("1000.00").compareTo(result.getPrice()), "Should apply base price");
    }

    @Test
    void bookTicket_ShouldIncreasePrice_WhenOccupancyIsVeryHigh() {
        mockFlight.setCurrentOccupancy(95);
        TicketDto.BookingRequest request = TicketDto.BookingRequest.builder()
                .flightId(mockFlight.getId())
                .seatNumber("10C")
                .useWallet(false)
                .build();

        when(flightRepository.findById(mockFlight.getId())).thenReturn(Optional.of(mockFlight));
        when(ticketRepository.existsByFlightIdAndSeatNumberAndStatus(any(), any(), any())).thenReturn(false);

        // SAVE Mock'unu en garantici hale getirdik:
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            return t;
        });

        // WHEN
        TicketDto.Info result = ticketService.bookTicket(request);

        // THEN
        assertNotNull(result, "Service returned null result");
        assertNotNull(result.getPrice(), "Price is NULL in the DTO! Check your Ticket -> DTO converter/mapper.");

        BigDecimal expectedPrice = new BigDecimal("1500.00");
        assertEquals(0, expectedPrice.compareTo(result.getPrice()), "Price calculation mismatch");
    }

    @Test
    void bookTicket_ShouldThrowException_WhenSeatIsAlreadyTaken() {
        // GIVEN
        TicketDto.BookingRequest request = TicketDto.BookingRequest.builder()
                .flightId(mockFlight.getId())
                .seatNumber("1A")
                .useWallet(false)
                .build();

        when(flightRepository.findById(mockFlight.getId())).thenReturn(Optional.of(mockFlight));
        when(ticketRepository.existsByFlightIdAndSeatNumberAndStatus(any(), eq("1A"), any())).thenReturn(true);

        // WHEN & THEN
        assertThrows(BaseException.class, () -> ticketService.bookTicket(request));
    }

    @Test
    void checkIn_ShouldThrowException_WhenFlightHasAlreadyDeparted() {
        // GIVEN: Uçuş geçmişte kalmış
        mockFlight.setDepartureTime(LocalDateTime.now().minusHours(1));
        Ticket ticket = new Ticket();
        ticket.setPnrCode("OLD123");
        ticket.setFlight(mockFlight);

        when(ticketRepository.findByPnrCode("OLD123")).thenReturn(Optional.of(ticket));

        // WHEN & THEN
        assertThrows(BaseException.class, () -> ticketService.checkIn("OLD123"));
    }

    @Test
    void checkIn_ShouldSucceed_BeforeDeparture() {
        // GIVEN: Uçuşa 48 saat var (Eski 24 saat sınırı kalktı)
        mockFlight.setDepartureTime(LocalDateTime.now().plusHours(48));
        Ticket ticket = new Ticket();
        ticket.setPnrCode("PNR789");
        ticket.setFlight(mockFlight);
        ticket.setCheckedIn(false);

        when(ticketRepository.findByPnrCode("PNR789")).thenReturn(Optional.of(ticket));

        // WHEN
        ticketService.checkIn("PNR789");

        // THEN
        assertTrue(ticket.isCheckedIn());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void cancelTicket_ShouldDecreaseOccupancy_WhenSuccessful() {
        // GIVEN
        UUID ticketId = UUID.randomUUID();

        // Kullanıcıya tip atıyoruz ki NullPointerException almayalım
        mockUser.setUserType(UserType.PASSENGER);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setPassenger(mockUser); // Bilet Berat'ın
        ticket.setFlight(mockFlight);
        mockFlight.setCurrentOccupancy(50);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // WHEN
        ticketService.cancelTicket(ticketId);

        // THEN
        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
        assertEquals(49, mockFlight.getCurrentOccupancy());
        verify(ticketRepository).save(ticket);
        verify(flightRepository).save(mockFlight);
    }

    @Test
    void cancelTicket_ShouldThrowException_WhenUserIsNotOwner() {
        // GIVEN
        UUID ticketId = UUID.randomUUID();
        mockUser.setUserType(UserType.PASSENGER); // Berat bir yolcu

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID()); // Başka birinin ID'si
        anotherUser.setUserType(UserType.PASSENGER);

        Ticket ticket = new Ticket();
        ticket.setPassenger(anotherUser); // Bilet Berat'ın değil!
        ticket.setStatus(TicketStatus.ACTIVE);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // WHEN & THEN: Başkasının biletini iptal etmeye çalışınca BaseException bekliyoruz
        assertThrows(BaseException.class, () -> ticketService.cancelTicket(ticketId));
    }

    @Test
    void getSeatMap_ShouldMarkOccupiedSeatsCorrectly() {
        // GIVEN
        UUID flightId = mockFlight.getId();
        mockFlight.getAircraft().setSeatCapacity(10); // Test kolaylığı için 10 koltuk

        Ticket activeTicket = new Ticket();
        activeTicket.setSeatNumber("1A");

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(mockFlight));
        when(ticketRepository.findByFlightIdAndStatus(flightId, TicketStatus.ACTIVE))
                .thenReturn(List.of(activeTicket));

        // WHEN
        List<SeatMapDto.SeatInfo> seatMap = ticketService.getSeatMap(flightId);

        // THEN
        assertEquals(10, seatMap.size());
        assertFalse(seatMap.get(0).isAvailable()); // 1A dolu olmalı
        assertTrue(seatMap.get(1).isAvailable());  // 1B boş olmalı
    }
    @Test
    void bookTicket_ShouldThrowException_WhenWalletBalanceInsufficient() {
        // GIVEN
        mockUser.setBalance(new BigDecimal("100.00")); // Yetersiz bakiye
        mockFlight.setBasePrice(new BigDecimal("1000.00"));

        TicketDto.BookingRequest request = TicketDto.BookingRequest.builder()
                .flightId(mockFlight.getId())
                .seatNumber("1A")
                .useWallet(true)
                .build();

        when(flightRepository.findById(mockFlight.getId())).thenReturn(Optional.of(mockFlight));

        // WHEN & THEN
        assertThrows(BaseException.class, () -> ticketService.bookTicket(request));
    }
}