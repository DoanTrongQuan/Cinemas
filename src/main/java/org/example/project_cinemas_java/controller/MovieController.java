package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.InvalidMovieDataException;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.UpdateMovieRequest;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.CreateRoomRequest;
import org.example.project_cinemas_java.service.implement.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/movie")
@RequiredArgsConstructor
public class MovieController {
    @Autowired
    private MovieService movieService;

    @PostMapping("/create-movie")
    public ResponseEntity<?> createMovie(@RequestBody CreateMovieRequest createMovieRequest){
        try {
            Movie movie = movieService.createMovie(createMovieRequest);
            return ResponseEntity.ok().body(movie);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (InvalidMovieDataException ex){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-movie")
    public ResponseEntity<?> updateMovie(@RequestBody UpdateMovieRequest updateMovieRequest){
        try {
            Movie movie = movieService.updateMovie(updateMovieRequest);
            return ResponseEntity.ok().body(movie);

        }catch (org.example.project_cinemas_java.exceptions.DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (InvalidMovieDataException ex){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-movie-by-cinema")
    public ResponseEntity<?> getAllMovieByCinema(@RequestParam String nameOfCinema){
        try {
            return ResponseEntity.ok().body(movieService.getAllMovieByCinema(nameOfCinema));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-movie-detail")
    public ResponseEntity<?> getMovieDetail(@RequestParam String slug){
        try {
            return ResponseEntity.ok().body(movieService.getMovieDetail(slug));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-movie-by-schedule")
    public ResponseEntity<?> getMovieBySchedule(@RequestParam int schedule){
        try {
            return ResponseEntity.ok().body(movieService.getMovieBySchedule(schedule));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-movie-by-admin")
    public ResponseEntity<?> getMovieByAdmin(){
        return ResponseEntity.ok().body(movieService.getMovieByAdmin());
    }
}
