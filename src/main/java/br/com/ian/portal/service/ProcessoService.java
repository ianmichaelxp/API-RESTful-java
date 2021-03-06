package br.com.ian.portal.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ian.portal.controller.dto.ProcessoDTO;
import br.com.ian.portal.model.Processo;
import br.com.ian.portal.repository.ProcessoRepository;
import br.com.ian.portal.service.exception.RegraNegocioException;

@Service
public class ProcessoService {

	@Autowired
	private ProcessoRepository processoRepository;

	public Page<ProcessoDTO> listarProcessos(String filtro, Pageable paginacao) {

		if (filtro.toUpperCase() == "PUBLICO" || filtro.toUpperCase() == "NAO_PUBLICO" ) {
			//Page<Processo> processos = processoRepository.findByFiltroSegredo(filtro, paginacao);
			Page<Processo> processos = processoRepository.findBySegredoJustica(filtro, paginacao);
			Page<ProcessoDTO> processoDTOs = ProcessoDTO.processoToDTO(processos);
			return processoDTOs;
		}
		
		else if (filtro.toUpperCase() == "ANDAMENTO" || filtro.toUpperCase() == "JULGADO" || filtro.toUpperCase() == "ARQUIVADO") {
			//Page<Processo> processos = processoRepository.findFiltroSitucao(filtro, paginacao);
			Page<Processo> processos = processoRepository.findBySituacao(filtro, paginacao);

			Page<ProcessoDTO> processoDTOs = ProcessoDTO.processoToDTO(processos);
			return processoDTOs;
		}
		else {
			Page<Processo> processos = processoRepository.findAll(paginacao);
			Page<ProcessoDTO> processoDTOs = ProcessoDTO.processoToDTO(processos);
			
			return processoDTOs;
		}		
	}

	public ProcessoDTO obterProcessoPorId(Long id) {
		Optional<Processo> processo = processoRepository.findById(id);
		if (!processo.isPresent()) {
			throw new RegraNegocioException("Não foi possível localizar, Processo não encontrado.");
		}
		return new ProcessoDTO(processo.get());
	}

	public ProcessoDTO salvarProcesso(@Valid ProcessoDTO processoDTO) {
		Processo processo = ProcessoDTO.processoToEntity(processoDTO);
		processoRepository.save(processo);
		return processoDTO;
	}

	public ProcessoDTO editarProcesso(@Valid Long id, ProcessoDTO processoDTO) {
		Optional<Processo> processo = processoRepository.findById(id);
		if (!processo.isPresent()) {
			throw new RegraNegocioException("Não foi possível editar, Processo não encontrado.");
		}
		
		Processo p = processo.get();
		p.setNumero(processoDTO.getNumero());
		p.setSegredoJustica(processoDTO.getSegredoJustica());
		p.setSituacao(processoDTO.getSituacao());
		p.setQtdPartes(processoDTO.getQtdPartes());
		processoRepository.save(p);

		return new ProcessoDTO(p);
	}

	public void excluirProcesso(Long id) {
		Optional<Processo> optional = processoRepository.findById(id);
		if (!optional.isPresent())
			throw new RegraNegocioException("Não foi possível excluir, Processo não encontrado.");
		processoRepository.deleteById(id);

	}
}
