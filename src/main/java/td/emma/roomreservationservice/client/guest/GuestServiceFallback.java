package td.emma.roomreservationservice.client.guest;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class GuestServiceFallback implements GuestServiceClient {

  @Override
  public List<Guest> getAll() {
    return new ArrayList<>();
  }

  @Override
  public Guest addGuest(Guest guest) {
    return new Guest();
  }

  @Override
  public Guest getGuest(long id) {
    return new Guest();
  }

  @Override
  public void updateGuest(long id, Guest guest) {

  }

  @Override
  public void deleteGuest(long id) {

  }
}
