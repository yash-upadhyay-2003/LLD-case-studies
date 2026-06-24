# 🎬 Event-Driven Concurrent Movie Reservation Engine

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Architecture](https://img.shields.io/badge/Architecture-LLD--Clean--Design-blue.svg?style=flat-square)](#)
[![Design Patterns](https://img.shields.io/badge/Patterns-Strategy%20%7C%20Observer%20%7C%20State-green.svg?style=flat-square)](#)
[![Performance](https://img.shields.io/badge/Concurrency-Atomic%20%7C%20Locking-red.svg?style=flat-square)](#)

A high-performance, low-level design (LLD) implementation of a scalable ticket reservation system capable of handling catastrophic traffic surges (e.g., millions of concurrent clicks during blockbusters drops). Built with strict object-oriented paradigms, thread safety, decoupling boundaries, and state isolation.

---

## 📌 Architectural Blueprint

The core domain relies on an explicit separation of concerns. While read-heavy operations handle catalog exploration, the write-heavy reservation engine isolates state modification through dedicated transaction boundaries.

```mermaid
classDiagram
    direction TB

    %% --- Classes and Entities ---
    class User {
        -id: String
        -name: String
        -email: String
    }

    class Booking {
        -id: String
        -user: User
        -show: Show
        -seats: List~Seat~
        -totalAmount: double
        -payment: Payment
        +confirmBooking()
    }

    class BookingManager {
        +lockSeats()
        +processPayment()
        +confirmBooking()
    }

    class Movie {
        -id: String
        -title: String
        -durationInMinutes: int
    }

    class Show {
        -id: String
        -movie: Movie
        -screen: Screen
        -startTime: LocalDateTime
        -pricingStrategy: PricingStrategy
    }

    class Seat {
        -id: String
        -row: int
        -col: int
        -type: SeatType
        -status: SeatStatus
    }

    class SeatLockManager {
        +lockSeats()
        +unlockSeats()
    }

    class MovieBookingService {
        +findShows()
        +bookTickets()
    }

    class Cinema {
        -id: String
        -name: String
        -city: City
    }

    class Payment {
        -id: String
        -amount: double
        -status: PaymentStatus
        -transactionId: String
    }

    %% --- Relationships & Multiplicities ---
    Booking "n" --> "1" User : Association
    Booking "n" --> "1" Show : Association
    
    Movie "1" <-- "1..*" Show : Association
    Cinema "1..*" --> "1" Movie : For
    Cinema "1" ..> Payment : Dependency
    
    Show "1" --> "1" Seat : 1 contains
    Show "1..*" --> "1" Payment : Association

    %% --- Service Managers Connections ---
    BookingManager ..> Booking : Manages
    BookingManager ..> SeatLockManager : Dependency
    SeatLockManager ..> Seat : Dependency
    SeatLockManager ..> MovieBookingService : Dependency
    MovieBookingService ..> Show : Manages
⚡ Core Design Implementations🏎️ 1. Ephemeral Lease Concurrency ControlTo prevent the catastrophic Thundering Herd problem when 500+ users attempt to checkout the exact same cinema seat, the system decouples locking logic away from database transactions:Atomic Leases: The SeatLockManager issues a low-overhead, short-lived lease on specific seat indices during the checkout lifecycle.Deterministic Transitions: Seat availability changes state strictly through bounded inputs inside a secure transaction ring:$$\text{AVAILABLE} \xrightarrow{\text{User Selection}} \text{LOCKED} \xrightarrow{\text{Settlement}} \text{BOOKED}$$Self-Healing Backouts: A TTL scheduler clears abandoned or timed-out orders automatically, returning seat allocations gracefully back to AVAILABLE.🎛️ 2. Dynamic Policy Extensibility (Strategy Pattern)Pricing Policies: By defining a modular PricingStrategy adapter interface, the layout supports fluid configurations for dynamic pricing changes (e.g., WeekdayPricingStrategy vs. WeekendPricingStrategy) without breaking internal entity logic.Settlement Inversion: Payments decouple underlying processors completely via standard polymorphism (CreditCardPaymentStrategy, open hooks for UPI).🔔 3. Reactive State Propagations (Observer Pattern)Maintains system-wide alignment by driving asynchronous notifications via structural hooks (MovieSubject, MovieObserver, UserObserver). Any fundamental change in availability or schedule updates dependency targets across the cluster in real time.📂 Structural Directory AnatomyPlaintext├── entities/       # Encapsulated state representations (User, Movie, Show, Seat, Booking)
├── enums/          # Bounded system typings (PaymentStatus, SeatStatus, SeatType)
├── observer/       # Event-driven subscription hooks for cross-domain notifications
├── strategy/       # Polymorphic behavioral adapters for pricing & processing logic
└── README.md       # Core engineering specifications & system documentation
📈 Scalability Optimization VectorsTo upgrade this single-node low-level model into a distributed microservice layer, the following tactical modifications are planned:CQRS Partitioning: Segregate read workflows (findShows()) out of relational databases entirely and route them onto distributed key-value memory blocks (e.g., Redis Read Replicas).Distributed Queue Ingestion: Transition state mutations out of synchronous context loops into distributed logs (e.g., Apache Kafka clusters) to smooth processing spikes.Optimistic Distributed Locks: Exchange standard sync mechanisms for high-throughput distributed algorithms using Redis Sorted Sets (ZADD), enforcing a strict, scale-safe "First in, first served" reservation ring.