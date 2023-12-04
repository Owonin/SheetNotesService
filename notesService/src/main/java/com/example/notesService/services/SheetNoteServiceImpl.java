package com.example.notesService.services;

import com.example.notesService.entity.ERoles;
import com.example.notesService.dto.NoteDto;
import com.example.notesService.dto.NotesListDto;
import com.example.notesService.entity.NoteEntity;
import com.example.notesService.exception.SheetNotesException;
import com.example.notesService.mapper.NoteMapper;
import com.example.notesService.repository.SheetNotePaginationAndSortingRepository;
import com.example.notesService.repository.SheetNotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SheetNoteServiceImpl implements SheetNoteService {
    private final SheetNotePaginationAndSortingRepository paginationAndSortingRepository;
    private final SheetNotesRepository sheetNotesRepository;


    @Override
    @Cacheable(value = "sheetNotes", key = "'pagination:' + #pageNumber + ':' + #pageSize + ':' + #sort[0] + ':' +#sort[1]")
    public NotesListDto getAllSheetNotes(int pageNumber,
                                         int pageSize,
                                         String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                var sortArguments = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(sortArguments[1]),
                        sortArguments[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        var notes = paginationAndSortingRepository
                .findAll(PageRequest.of(pageNumber, pageSize, Sort.by(orders)));

        if (notes.getContent().isEmpty()) {
            throw new SheetNotesException(HttpStatus.NOT_FOUND, "Нотные записи не найдены");
        }

        return NoteMapper.NotesEntityListToNotesDtoList(notes);
    }

    private Sort.Direction getSortDirection(String sortDirection) {
        if (sortDirection.toLowerCase().contains("asc")) {
            return Sort.Direction.ASC;
        }

        if (sortDirection.toLowerCase().contains("desc")) {
            return Sort.Direction.DESC;
        }

        return null;
    }

    @Override
    @Cacheable(value = "sheetNote", key = "#id")
    public NoteEntity getSheetNoteById(String id) {
        return sheetNotesRepository.findById(id)
                .orElseThrow(() -> new SheetNotesException(HttpStatus.NOT_FOUND,
                        String.format("Запись с id %s не найдена", id)));
    }

    @Override
    public NoteEntity createSheetNote(NoteDto noteDto, String creatorName) {

        var entity = NoteMapper.noteDtoToNoteEntity(noteDto);
        entity.setCreationTimestamp(LocalDate.now());
        entity.setSheetNoteCreator(creatorName);

        return sheetNotesRepository.save(entity);

    }

    private boolean authStatusForModifyingData(String authorName, Authentication auth) {
        var username = auth.getName();
        var role = auth.getAuthorities();

        return authorName.equals(username)
                || role.stream().anyMatch(a -> a.getAuthority().equals(ERoles.ROLE_ADMIN.toString()));
    }

    @Override
    @CachePut(value = "sheetNotes", key = "#noteEntity.id")
    public NoteEntity updateSheetNote(NoteEntity noteEntity, Authentication auth) {
        var id = noteEntity.getId();

        sheetNotesRepository.findById(id)
                .orElseThrow(() ->
                        new SheetNotesException(HttpStatus.NOT_FOUND,
                                String.format("Записей по id %s не найдено", id)));

        if (authStatusForModifyingData(noteEntity.getSheetNoteCreator(), auth))
            sheetNotesRepository.save(noteEntity);
        else
            throw new SheetNotesException(HttpStatus.FORBIDDEN,
                    "Нет прав на изменение данных");
        return noteEntity;
    }

    @Override
    @CacheEvict(value = "sheetNotes", key = "#id")
    public void deleteSheetNote(String id, Authentication auth) {
        var noteEntity = sheetNotesRepository.findById(id)
                .orElseThrow(() ->
                        new SheetNotesException(HttpStatus.NOT_FOUND,
                                String.format("Записей по id %s не найдено", id)));

        if (authStatusForModifyingData(noteEntity.getSheetNoteCreator(), auth))
            sheetNotesRepository.delete(noteEntity);
        else
            throw new SheetNotesException(HttpStatus.FORBIDDEN,
                    "Нет прав на изменение данных");
    }
}
