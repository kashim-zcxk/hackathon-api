package mx.com.askatl.yau.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import mx.com.askatl.yau.entity.StationsStatus;

public interface StationsStatusRepository  extends MongoRepository<StationsStatus, String>{
	List<StationsStatus> findByIdEstacion(Integer idEstacion);
	long countByIdEstacion(Integer idEstacion);
}
