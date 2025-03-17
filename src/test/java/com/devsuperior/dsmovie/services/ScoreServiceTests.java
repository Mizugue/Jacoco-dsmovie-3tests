package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	private Long existingId;
	private Long nonExistingId;
	private MovieEntity movieEntity;
	private ScoreEntity scoreEntity;
	private UserEntity userEntity;
	private ScoreDTO scoreDTO;

	@BeforeEach
	void setUp() throws Exception {
		movieEntity = MovieFactory.createMovieEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		userEntity = UserFactory.createUserEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		existingId = 1L;
		nonExistingId = 10L;


		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any(ScoreEntity.class))).thenReturn(scoreEntity);
		Mockito.when(movieRepository.save(ArgumentMatchers.any(MovieEntity.class))).thenReturn(movieEntity);
		Mockito.when(userService.authenticated()).thenReturn(userEntity);
		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());


	}


	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO result = service.saveScore(scoreDTO);
		Assertions.assertNotNull(result);
	}


	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		scoreDTO.setMovieId(nonExistingId);


		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
