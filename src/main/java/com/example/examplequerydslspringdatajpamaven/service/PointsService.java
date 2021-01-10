package com.example.examplequerydslspringdatajpamaven.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Points;

@Service
public interface PointsService {

	public ResponseEntity<?> getPointsMap(String TOKEN,Long id);
	public ResponseEntity<?> getPointsList(String TOKEN,Long id,int offset,String search,String exportData);
	public ResponseEntity<?> getPointsById(String TOKEN,Long PointId,Long userId);
	public ResponseEntity<?> deletePoints(String TOKEN,Long PointId,Long userId);
	public ResponseEntity<?> createPoints(String TOKEN,Points points,Long userId);
	public ResponseEntity<?> editPoints(String TOKEN,Points points,Long userId);

	public ResponseEntity<?> getPointSelect(String TOKEN,Long loggedUserId,Long userId);
	public ResponseEntity<?> getPointUnSelectOfClient(String TOKEN,Long loggedUserId,Long userId);


	public ResponseEntity<?> assignClientPoints(String TOKEN,Long loggedUserId,Long userId,Long [] pointIds);
	public ResponseEntity<?> getClientPoints(String TOKEN,Long loggedUserId,Long userId);
}
