package br.gov.mec.aghu.paciente.historico.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IHistoricoPacienteFacade extends Serializable {

	void inserirEnderecoPacienteHist(AipEndPacientesHist endPacienteHist);

	AipEndPacientesHist converterEnderecoPacienteEmEndPacienteHist(
			AipEnderecosPacientes endPac);

	List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(Integer codigo);

	AipEnderecosPacientes converterEnderecoPacienteHistEmEndPaciente(
			AipEndPacientesHist historicoEndPac);

	HistoricoPacienteVO obterHistoricoPaciente(Integer prontuario,
			Integer codigo, boolean buscarUltEventos, boolean buscarSitAnt)
			throws ApplicationBusinessException;

	AipPacientesHist obterHistoricoPaciente(Integer prontuario, Integer codigo);

	void persistirHistoricoPaciente(AipPacientesHist historicoPaciente) throws ApplicationBusinessException;

	Long obterHistoricoPacientesExcluidosCount(Integer codigo, Integer prontuario, String nome);

	List<AipPacientesHist> pesquisarHistoricoPacientesExcluidos(
			Integer firstResult, Integer maxResult, Integer codigo,
			Integer prontuario, String nome);

	void inserirPacienteHist(AipPacientesHist pacienteHist)
			throws ApplicationBusinessException;

	void evictObject(Object entity);

}