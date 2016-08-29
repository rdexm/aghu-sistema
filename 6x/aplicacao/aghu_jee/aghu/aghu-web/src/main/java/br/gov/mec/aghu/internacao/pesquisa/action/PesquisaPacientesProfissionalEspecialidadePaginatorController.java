package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.PacienteProfissionalEspecialidadeVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class PesquisaPacientesProfissionalEspecialidadePaginatorController
		extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6896919521573085509L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;	

	private AghEspecialidades especialidade;
	
	private EspCrmVO profissional;
	
	private String cameFrom;
	private Short seqEspecialidade;
	private String crmProfissional;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private PacienteProfissionalEspecialidadeVO pacienteProfEspVOSelecionado;
	
	private final String PAGE_PESQ_REF_ESP_PROF = "pesquisaReferencialEspecialidadeProfissional";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	
	public void inicializar() {
		try {
			if (this.seqEspecialidade != null || this.crmProfissional != null) {
				this.especialidade = this.cadastrosBasicosInternacaoFacade.obterEspecialidade(this.seqEspecialidade);
				this.profissional = this.pesquisaInternacaoFacade.pesquisarProfissionalPorEspecialidadeCRM(this.especialidade, this.crmProfissional);
				
				this.seqEspecialidade = null;
				this.crmProfissional = null;
				
				this.pesquisar();
			}			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public List<AghEspecialidades> pesquisarEspecialidade(String strPesquisa) {
		return this.returnSGWithCount(this.pesquisaInternacaoFacade.pesquisarEspecialidadePorSiglaNome(strPesquisa),pesquisarEspecialidadeCount(strPesquisa));
	}
	
	public Long pesquisarEspecialidadeCount(String strPesquisa) {
		return this.pesquisaInternacaoFacade.pesquisarEspecialidadePorSiglaNomeCount(strPesquisa);
	}
	
	public void limpaProfissional(){
		profissional = null;
	}
	
	public List<EspCrmVO> pesquisarProfissional(String strPesquisa) {
		try {
			return this.pesquisaInternacaoFacade.pesquisaProfissionalEspecialidade(this.especialidade, strPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return new ArrayList<EspCrmVO>();
	}

	public void limparPesquisa() {
		this.especialidade = null;
		this.profissional = null;
		this.dataModel.limparPesquisa();
	}

	public void limparEspecialidade() {
		this.especialidade = null;
		this.profissional = null;
	}

	public boolean isMostrarLinkExcluirEspecialidade() {
		return this.especialidade != null
				&& this.especialidade.getSeq() != null;
	}
	
	public void limparProfissional() {
		this.profissional = null;
	}

	public boolean isMostrarLinkExcluirProfissional() {
		return this.profissional != null
				&& this.profissional.getNroRegConselho() != null;
	}

	public String cancelar(){
		return PAGE_PESQ_REF_ESP_PROF;
	}
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Integer count = 0;

		try {
			count = this.pesquisaInternacaoFacade
					.pesquisaPacientesProfissionalEspecialidadeCount(this.especialidade, this.profissional);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}

		return count.longValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<PacienteProfissionalEspecialidadeVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<PacienteProfissionalEspecialidadeVO> result = null;

		try {
			result = this.pesquisaInternacaoFacade
					.pesquisaPacientesProfissionalEspecialidade(firstResult,
							maxResult, orderProperty, asc, this.especialidade, this.profissional);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}

		if (result == null) {
			result = new ArrayList<PacienteProfissionalEspecialidadeVO>();
		}

		return result;
	}
	
	
	// ### GETs e SETs ###

	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public EspCrmVO getProfissional() {
		return this.profissional;
	}

	public void setProfissional(EspCrmVO profissional) {
		this.profissional = profissional;
	}

	public String getCameFrom() {
		return this.cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getCrmProfissional() {
		return crmProfissional;
	}

	public void setCrmProfissional(String crmProfissional) {
		this.crmProfissional = crmProfissional;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

	public PacienteProfissionalEspecialidadeVO getPacienteProfEspVOSelecionado() {
		return pacienteProfEspVOSelecionado;
	}

	public void setPacienteProfEspVOSelecionado(
			PacienteProfissionalEspecialidadeVO pacienteProfEspVOSelecionado) {
		this.pacienteProfEspVOSelecionado = pacienteProfEspVOSelecionado;
	}
}