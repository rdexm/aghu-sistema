package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacEspecialidadePmpa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEspecialidadePmpaPaginatorController
	extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AacEspecialidadePmpa> dataModel;
	private static final Log LOG = LogFactory.getLog(ManterEspecialidadePmpaPaginatorController.class);
	private static final long serialVersionUID = 5920429836668387708L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private boolean exibirBotaoIncluirEspecialidade = false;
	private Short seqEspecialidadePmpa;
	private Long codigoEspecialidadePmpa;
	private AacEspecialidadePmpa aacEspecialidadePmpa;
	private AghEspecialidades especialidade;
	private final String REDIRECIONA_MANTER_ESPECIALIDADE_PMPA_CRUD ="ambulatorio-manterEspecialidadePMPACRUD";

	
	 public String iniciarInclusao() {
			return REDIRECIONA_MANTER_ESPECIALIDADE_PMPA_CRUD;
	 }
	
	@Override
	public Long recuperarCount() {

		return this.ambulatorioFacade.countEspecialidadePmpaPaginado(
			seqEspecialidadePmpa, codigoEspecialidadePmpa);
	}

	@Override
	public List<AacEspecialidadePmpa> recuperarListaPaginada(
		Integer firstResult, Integer maxResult, String orderProperty,
		boolean asc) {

		this.seqEspecialidadePmpa = null;
		if (especialidade != null) {
			this.seqEspecialidadePmpa = especialidade.getSeq();
		}

		return this.ambulatorioFacade.listarEspecialidadePmpaPaginado(
			firstResult, maxResult, orderProperty, asc, seqEspecialidadePmpa,
			codigoEspecialidadePmpa);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.aacEspecialidadePmpa = null;
		this.seqEspecialidadePmpa = null;
		this.codigoEspecialidadePmpa = null;
		this.exibirBotaoIncluirEspecialidade = false;
		this.especialidade = null;
		this.getDataModel().reiniciarPaginator();
	}

	public void excluir() {

		this.getDataModel().reiniciarPaginator();
		AacEspecialidadePmpa especialidadePmpa =
			this.ambulatorioFacade.obterAacEspecialidadePmpaPorChavePrimaria(
				seqEspecialidadePmpa, codigoEspecialidadePmpa);
		try {
			this.ambulatorioFacade.excluirEspecialidadePmpa(especialidadePmpa);
			this.getDataModel().reiniciarPaginator();
			this.apresentarMsgNegocio(
				Severity.INFO, "MSG_ESPECIALIDADE_SUCESSO_REMOCAO");
			this.seqEspecialidadePmpa = null;
			this.codigoEspecialidadePmpa = null;
		}
		catch (BaseException e) {
			LOG.error(e.getClass().getName(), e);
			apresentarExcecaoNegocio(e);
		}
	}

	public List<AghEspecialidades> listarEspecialidades(String objPesquisa) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadePorNomeOuSigla((String) objPesquisa),listarEspecialidadesCount(objPesquisa));
	}
	
    public Long listarEspecialidadesCount(String objPesquisa){
    	return this.aghuFacade.listarEspecialidadePorNomeOuSiglaCount((String) objPesquisa); 
    }

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoIncluirEspecialidade = true;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public boolean isExibirBotaoIncluirEspecialidade() {
		return exibirBotaoIncluirEspecialidade;
	}

	public void setExibirBotaoIncluirEspecialidade(
		boolean exibirBotaoIncluirEspecialidade) {
		this.exibirBotaoIncluirEspecialidade = exibirBotaoIncluirEspecialidade;
	}

	public Short getSeqEspecialidadePmpa() {
		return seqEspecialidadePmpa;
	}

	public void setSeqEspecialidadePmpa(Short seqEspecialidadePmpa) {
		this.seqEspecialidadePmpa = seqEspecialidadePmpa;
	}

	public Long getCodigoEspecialidadePmpa() {
		return codigoEspecialidadePmpa;
	}

	public void setCodigoEspecialidadePmpa(Long codigoEspecialidadePmpa) {
		this.codigoEspecialidadePmpa = codigoEspecialidadePmpa;
	}

	public AacEspecialidadePmpa getAacEspecialidadePmpa() {
		return aacEspecialidadePmpa;
	}

	public void setAacEspecialidadePmpa(
		AacEspecialidadePmpa aacEspecialidadePmpa) {
		this.aacEspecialidadePmpa = aacEspecialidadePmpa;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public DynamicDataModel<AacEspecialidadePmpa> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacEspecialidadePmpa> dataModel) {
	 this.dataModel = dataModel;
	}
}
