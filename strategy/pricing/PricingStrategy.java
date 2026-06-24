package casestudies.moviebookingsystem.strategy.pricing;

import casestudies.moviebookingsystem.entities.Seat;

import java.util.List;

public interface PricingStrategy {
    double calculatePrice(List<Seat> seats);
}