package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import com.example.examplequerydslspringdatajpamaven.entity.Points;

public interface PointsService {

	public ResponseEntity<?> getPointsList(String TOKEN,Long id,int offset,String search);
	public ResponseEntity<?> getPointsById(String TOKEN,Long PointId,Long userId);
	public ResponseEntity<?> deletePoints(String TOKEN,Long PointId,Long userId);
	public ResponseEntity<?> createPoints(String TOKEN,Points points,Long userId);
	public ResponseEntity<?> editPoints(String TOKEN,Points points,Long userId);

}
