package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository movieRepository;


	private Pageable pageableValid;
	private Long idThatExist;
	private Long idThatNotExist;
	private Long idDependent;
	private MovieDTO movieEntityDto;
	private MovieEntity movieEntity;

	@BeforeEach
	void setUp() throws Exception {
		pageableValid = PageRequest.of(0, 5);
		movieEntity = MovieFactory.createMovieEntity();
		movieEntityDto = MovieFactory.createMovieDTO();

		idThatExist = 1L;
		idThatNotExist = 10L;
		idDependent = 11L;


		Page<MovieEntity> movieEntityPage = new PageImpl<>(List.of(movieEntity));

		Mockito.when(movieRepository.searchByTitle("ValidTitle", pageableValid)).thenReturn(movieEntityPage);
		Mockito.when(movieRepository.findById(idThatExist)).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(idThatNotExist)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(movieRepository.save(ArgumentMatchers.any(MovieEntity.class))).thenReturn(movieEntity);
		Mockito.when(movieRepository.getReferenceById(idThatExist)).thenReturn(movieEntity);
		Mockito.when(movieRepository.getReferenceById(idThatNotExist)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(movieRepository.existsById(idThatExist)).thenReturn(true);
		Mockito.doNothing().when(movieRepository).deleteById(idThatExist);
		Mockito.when(movieRepository.existsById(idThatNotExist)).thenThrow(ResourceNotFoundException.class);
		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(idDependent);
		Mockito.when(movieRepository.existsById(idDependent)).thenReturn(true);


	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		String validTitle = "ValidTitle";
		Page<MovieDTO> result = service.findAll(validTitle, pageableValid);
		Assertions.assertFalse(result.isEmpty());

	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(idThatExist);
		Assertions.assertNotNull(result);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(idThatNotExist);
		});

	}


	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO result = service.insert(movieEntityDto);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(movieEntityDto.getTitle(), result.getTitle());
		Assertions.assertEquals(movieEntityDto.getScore(), result.getScore());
		Assertions.assertEquals(movieEntityDto.getCount(), result.getCount());
		Assertions.assertEquals(movieEntityDto.getImage(), result.getImage());

	}


	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.update(idThatExist, movieEntityDto);
		Assertions.assertNotNull(result);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(idThatNotExist, movieEntityDto);
		});

	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(idThatExist);
		});
	}


	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(idThatNotExist);
		});

	}


	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(idDependent);
		});

	}
}