package casestudies.moviebookingsystem.observer;


import casestudies.moviebookingsystem.entities.Movie;

public interface MovieObserver {
    void update(Movie movie);
}