package com.example.notesService.repository;

import com.example.notesService.entity.NoteEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface SheetNotePaginationAndSortingRepository extends PagingAndSortingRepository<NoteEntity,String> {

}
