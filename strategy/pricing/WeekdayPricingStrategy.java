package casestudies.moviebookingsystem.strategy.pricing;


import casestudies.moviebookingsystem.entities.Seat;

import java.util.List;

public class WeekdayPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(List<Seat> seats) {
        return seats.stream().mapToDouble(seat -> seat.getType().getPrice()).sum();
    }
}