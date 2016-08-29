package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações da página Pesquisar Referencial
 * Clínica/Especialidação do módulo de internações.
 * 
 * @author david.laks
 */

public class PesquisaReferencialClinicaEspecialidadePaginatorController 
       extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1850308068995987167L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private AghClinicas clinica;
	
	@Inject @Paginator
	private DynamicDataModel<PesquisaReferencialClinicaEspecialidadeVO> dataModel;	
	
	private PesquisaReferencialClinicaEspecialidadeVO pesquisaReferencialClinicaEspecialidadeVO;
		
	private final String PAGE_PESQ_REF_ESP_PROF = "pesquisaReferencialEspecialidadeProfissional";
	
	@Inject
	private PesquisaReferencialEspecialidadeProfissionalPaginatorController pesquisaReferencialEspecialidadeProfissionalPaginatorController;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);	
		this.dataModel.setDefaultMaxRow(50);
	}

	

	public void pesquisar() {
		try {
			this.pesquisaInternacaoFacade
					.validaDadosPesquisaReferencialClinicaEspecialidade(this.clinica);
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparPesquisa() {
		this.clinica = null;
		this.dataModel.limparPesquisa();
	}

	public void limparClinica() {
		this.clinica = null;
	}

	public boolean isMostrarLinkExcluirClinica() {
		return this.clinica != null && this.clinica.getCodigo() != null;
	}
	
	public String pesquisaReferencialEspecialidadeProfissional(PesquisaReferencialClinicaEspecialidadeVO pesqRefCliEspVO){		
		this.pesquisaReferencialEspecialidadeProfissionalPaginatorController.setSeqEspecialidade(pesqRefCliEspVO.getSeqEspecialidade().shortValue());
		this.pesquisaReferencialEspecialidadeProfissionalPaginatorController.setCameFrom("refCliEsp");
		this.pesquisaReferencialEspecialidadeProfissionalPaginatorController.inicio();
		return PAGE_PESQ_REF_ESP_PROF;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = Long.valueOf(0);

		try {
			count = this.pesquisaInternacaoFacade
					.pesquisaReferencialClinicaEspecialidadeCount(this.clinica);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PesquisaReferencialClinicaEspecialidadeVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<PesquisaReferencialClinicaEspecialidadeVO> result = null;

		try {
			result = this.pesquisaInternacaoFacade
					.pesquisaReferencialClinicaEspecialidade(firstResult,
							maxResult, orderProperty, asc, this.clinica);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}

		if (result == null) {
			result = new ArrayList<PesquisaReferencialClinicaEspecialidadeVO>();
		}

		return result;
	}
	
	public List<AghClinicas> pesquisarClinicasPorCodigoEDescricao(String objPesquisa) {
		return aghuFacade.pesquisarClinicasPorCodigoEDescricao(objPesquisa);
	}

	// ### GETs e SETs ###

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	public DynamicDataModel<PesquisaReferencialClinicaEspecialidadeVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaReferencialClinicaEspecialidadeVO> dataModel) {
		this.dataModel = dataModel;
	}


	public PesquisaReferencialClinicaEspecialidadeVO getPesquisaReferencialClinicaEspecialidadeVO() {
		return pesquisaReferencialClinicaEspecialidadeVO;
	}


	public void setPesquisaReferencialClinicaEspecialidadeVO(
			PesquisaReferencialClinicaEspecialidadeVO pesquisaReferencialClinicaEspecialidadeVO) {
		this.pesquisaReferencialClinicaEspecialidadeVO = pesquisaReferencialClinicaEspecialidadeVO;
	}

}
