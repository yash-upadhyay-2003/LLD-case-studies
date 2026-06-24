package casestudies.moviebookingsystem.strategy.payment;

import casestudies.moviebookingsystem.entities.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
}