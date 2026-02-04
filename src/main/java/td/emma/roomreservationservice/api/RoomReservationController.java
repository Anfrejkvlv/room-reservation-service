package td.emma.roomreservationservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import td.emma.roomreservationservice.client.guest.Guest;
import td.emma.roomreservationservice.client.guest.GuestServiceClient;
import td.emma.roomreservationservice.client.reservation.Reservation;
import td.emma.roomreservationservice.client.reservation.ReservationServiceClient;
import td.emma.roomreservationservice.client.room.Room;
import td.emma.roomreservationservice.client.room.RoomServiceClient;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("roomReservations")
@CrossOrigin("*")
public class RoomReservationController {

    private final GuestServiceClient guestServiceClient;
    private final ReservationServiceClient reservationServiceClient;
    private final RoomServiceClient roomServiceClient;

    public RoomReservationController(GuestServiceClient guestServiceClient, ReservationServiceClient reservationServiceClient, RoomServiceClient roomServiceClient) {
        this.guestServiceClient = guestServiceClient;
        this.reservationServiceClient = reservationServiceClient;
        this.roomServiceClient = roomServiceClient;
    }

    @GetMapping
    public Collection<RoomReservation> getRoomReservations(@RequestParam(value = "date", required = false) String dateString) {
        if (!StringUtils.hasLength(dateString)) {
            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateString = dateFormat.format(date);
        }
        final String usableDateString = dateString;
        //get all rooms first
        List<Room> rooms = this.roomServiceClient.getAll();
        //now build a room reservation for each one
        //Map<Long, RoomReservation> roomReservations = new HashMap<>(rooms.size());
        Map<Long, RoomReservation> roomReservations = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getRoomId());
            roomReservation.setRoomNumber(room.getRoomNumber());
            roomReservation.setBedInfo(room.getBedInfo());
            roomReservation.setName(room.getName());
            roomReservation.setDate(usableDateString);
            roomReservations.put(room.getRoomId(), roomReservation);
        });
        List<Reservation> reservations = this.reservationServiceClient.getAll(null, null);
        reservations.forEach(reservation -> {
            RoomReservation roomReservation = roomReservations.get(reservation.getRoomId());
            roomReservation.setReservationId(reservation.getReservationId());
            roomReservation.setGuestId(reservation.getGuestId());
            Guest guest = this.guestServiceClient.getGuest(roomReservation.getGuestId());
            roomReservation.setFirstName(guest.getFirstName());
            roomReservation.setLastName(guest.getLastName());
        });
        return roomReservations.values();
    }

    @GetMapping("/rooms")
    public List<Room> getRooms() {
        return this.roomServiceClient.getAll();
    }

    @GetMapping("/reservations")
    public List<Reservation> getReservations(        @RequestParam(value = "dateString", required = false) String dateString,
                                                           @RequestParam(value = "guestId", required = false) Long guestId) {
        return reservationServiceClient.getAll(dateString, guestId);
    }

    @GetMapping("/guests")
    public List<Guest> getGuests() {
        return guestServiceClient.getAll();
    }

    @PostMapping("/reservations")
    public Reservation addReservation(@RequestBody Reservation reservation) {
        return reservationServiceClient.addReservation(reservation);
    }

    @PostMapping("/rooms")
    public Room addRoom(@RequestBody Room room) {
        return roomServiceClient.addRoom(room);
    }

    @PostMapping("/guests")
    public Guest addGuest(@RequestBody Guest guest) {
        return guestServiceClient.addGuest(guest);
    }

    @GetMapping("/reservations/{reservationId}")
    public Reservation getReservation(@PathVariable Long reservationId) {
        return reservationServiceClient.getReservation(reservationId);
    }

    @GetMapping("/rooms/{roomId}")
    public Room getRoom(@PathVariable Long roomId) {
        return roomServiceClient.getRoom(roomId);
    }

    @GetMapping("/guests/{guestId}")
    public Guest getGuest(@PathVariable Long guestId) {
        return guestServiceClient.getGuest(guestId);
    }

    @PutMapping("/reservations/{reservationId}")
    public ResponseEntity<String> updateReservation(@PathVariable Long reservationId, @RequestBody Reservation reservation) {
        reservationServiceClient.updateReservation(reservationId, reservation);
        return new ResponseEntity<>("Reservation successfully updated", HttpStatus.OK);
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<String> updateRoom(@PathVariable Long roomId, @RequestBody Room room) {
        roomServiceClient.updateRoom(roomId,room);
        return new ResponseEntity<>("Room successfully updated", HttpStatus.OK);
    }

    @PutMapping("/guests/{guestId}")
    public ResponseEntity<String> updateGuest(@PathVariable Long guestId, @RequestBody Guest guest) {
        guestServiceClient.updateGuest(guestId,guest);
        return new ResponseEntity<>("Guest successfully updated",HttpStatus.OK);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long reservationId) {
        reservationServiceClient.deleteReservation(reservationId);
        return new ResponseEntity<>("Reservation successfully deleted", HttpStatus.OK);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<String> updateRoom(@PathVariable Long roomId) {
        roomServiceClient.deleteRoom(roomId);
        return new ResponseEntity<>("Room successfully deleted", HttpStatus.OK);
    }

    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<String> updateGuest(@PathVariable Long guestId) {
        guestServiceClient.deleteGuest(guestId);
        return new ResponseEntity<>("Guest successfully deleted",HttpStatus.OK);
    }





}
