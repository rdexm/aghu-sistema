package br.gov.mec.aghu.paciente.historico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;

@Modulo(ModuloEnum.PACIENTES)
@Stateless
public class HistoricoPacienteFacade extends BaseFacade implements IHistoricoPacienteFacade {

	private static final long serialVersionUID = 5816367432427522112L;

	@EJB
	private HistoricoPacienteJournalON historicoPacienteJournalON;

	@EJB
	private HistoricoEnderecoPacienteRN historicoEnderecoPacienteRN;

	@EJB
	private HistoricoPacienteON historicoPacienteON;
	
	@EJB
	private HistoricoPacienteRN historicoPacienteRN;
	
	@Override
	public void evictObject(Object entity) {
		this.flush();
		this.evict((BaseEntity) entity);
	}

	@Override
	public void inserirEnderecoPacienteHist(AipEndPacientesHist endPacienteHist) {
		this.getHistoricoEnderecoPacienteRN().inserirEnderecoPacienteHist(endPacienteHist);
	}

	@Override
	public AipEndPacientesHist converterEnderecoPacienteEmEndPacienteHist(AipEnderecosPacientes endPac) {
		return this.getHistoricoEnderecoPacienteRN().converterEnderecoPacienteEmEndPacienteHist(endPac);
	}

	@Override
	public List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(Integer codigo) {
		return this.getHistoricoEnderecoPacienteRN().pesquisarHistoricoEnderecoPaciente(codigo);
	}

	@Override
	public AipEnderecosPacientes converterEnderecoPacienteHistEmEndPaciente(AipEndPacientesHist historicoEndPac) {
		return this.getHistoricoEnderecoPacienteRN().converterEnderecoPacienteHistEmEndPaciente(historicoEndPac);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','pesquisar')}")
	public HistoricoPacienteVO obterHistoricoPaciente(Integer prontuario, Integer codigo, boolean buscarUltEventos, boolean buscarSitAnt) throws ApplicationBusinessException {
		return this.getHistoricoPacienteON().obterHistoricoPaciente(prontuario, codigo, buscarUltEventos, buscarSitAnt);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','pesquisar')}")
	public AipPacientesHist obterHistoricoPaciente(Integer prontuario, Integer codigo) {
		return this.getHistoricoPacienteON().obterHistoricoPaciente(prontuario, codigo);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','alterar')}")
	public void persistirHistoricoPaciente(AipPacientesHist historicoPaciente) throws ApplicationBusinessException {
		this.getHistoricoPacienteON().persistirHistoricoPaciente(historicoPaciente);
	}

	@Override
	public Long obterHistoricoPacientesExcluidosCount(Integer codigo, Integer prontuario, String nome) {
		return this.getHistoricoPacienteON().obterHistoricoPacientesExcluidosCount(codigo, prontuario, nome);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','pesquisar')}")
	public List<AipPacientesHist> pesquisarHistoricoPacientesExcluidos(Integer firstResult, Integer maxResult, Integer codigo, Integer prontuario, String nome) {
		return this.getHistoricoPacienteON().pesquisarHistoricoPacientesExcluidos(firstResult, maxResult, codigo, prontuario, nome);
	}

	@Override
	public void inserirPacienteHist(AipPacientesHist pacienteHist) throws ApplicationBusinessException {
		this.getHistoricoPacienteRN().inserirPacienteHist(pacienteHist);
	}

	public HistoricoPacienteJournalON getHistoricoPacienteJournalON() {
		return historicoPacienteJournalON;
	}

	public HistoricoEnderecoPacienteRN getHistoricoEnderecoPacienteRN() {
		return historicoEnderecoPacienteRN;
	}

	public HistoricoPacienteON getHistoricoPacienteON() {
		return historicoPacienteON;
	}

	public HistoricoPacienteRN getHistoricoPacienteRN() {
		return historicoPacienteRN;
	}
}
