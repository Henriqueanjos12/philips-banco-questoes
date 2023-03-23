package br.com.saper.qenem.services;

import br.com.saper.qenem.dtos.*;
import br.com.saper.qenem.enums.MateriaEnum;
import br.com.saper.qenem.models.*;
import br.com.saper.qenem.repositories.QuestaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class QuestaoService {

    @Autowired
    private QuestaoRepository questaoRepository;

    public ResponseEntity<Object> findAll(String materia, Boolean certificada) {

        if (!materia.isBlank() && certificada != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(questaoRepository.findAllByMateriaAndCertificada(MateriaEnum.valueOf(materia), certificada).stream().map(
                            (questao -> new QuestaoResponseDTO(questao))
                    ).toList());
        } else if (!materia.isBlank() && certificada == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(questaoRepository.findAllByMateria(MateriaEnum.valueOf(materia)).stream().map(
                            (questao -> new QuestaoResponseDTO(questao))
                    ).toList());
        } else if (materia.isBlank() && certificada != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(questaoRepository.findAllByCertificada(certificada).stream().map(
                            (questao -> new QuestaoResponseDTO(questao))
                    ).toList());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(questaoRepository.findAll().stream().map(
                            (questao -> new QuestaoResponseDTO(questao))
                    ).toList());
        }
    }

    @Transactional
    public ResponseEntity<Object> save(QuestaoRequestDTO questaoRequestDTO) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ((principal instanceof Usuario usuario) && usuario.getProfessor() != null ) {

            Questao questao = new Questao();
            questao.setProfessor(usuario.getProfessor());
            questao.setMateria(MateriaEnum.valueOf(questaoRequestDTO.getMateria()));
            questao.setEnunciado(questaoRequestDTO.getEnunciado());
            questao.setNumeroAcessos(Long.valueOf(0));

            for (ItemQuestaoRequestDTO item : questaoRequestDTO.getItensQuestao()) {
                ItemQuestao itemQuestao = new ItemQuestao();
                itemQuestao.setTexto(item.getTexto());
                itemQuestao.setCorreto(item.isCorreto());
                itemQuestao.setQuestao(questao);
                questao.getItensQuestao().add(itemQuestao);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new QuestaoResponseDTO(questaoRepository.save(questao)));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
