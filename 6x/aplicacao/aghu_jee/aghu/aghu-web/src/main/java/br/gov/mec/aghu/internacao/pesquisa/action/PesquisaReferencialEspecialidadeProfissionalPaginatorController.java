package br.gov.mec.aghu.internacao.pesquisa.action;
	
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.ReferencialEspecialidadeProfissonalGridVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
	
	/**
 * Classe responsável por controlar as ações da página Pesquisar Referencial
 * Especialidade/Profissional do módulo de internações.
 * 
 * @author Marcelo Tocchetto
 */

public class PesquisaReferencialEspecialidadeProfissionalPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2859299553195239237L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private String cameFrom;
	
	private Short seqEspecialidade;
	
	private AghEspecialidades especialidade;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private ReferencialEspecialidadeProfissonalGridVO referencialEspecialidadeProfissonalGridVO;
	
	private final String PAGE_PESQ_REF_CLI_ESP = "pesquisaReferencialClinicaEspecialidade";
	private final String PAGE_PESQ_PAC_PROF_ESP = "pesquisaPacientesProfissionalEspecialidade";
	
	@Inject
	private PesquisaPacientesProfissionalEspecialidadePaginatorController pesquisaPacientesProfissionalEspecialidadePaginatorController;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
	 

		try {
			if (this.seqEspecialidade != null) {
				this.especialidade = this.cadastrosBasicosInternacaoFacade.obterEspecialidade(this.seqEspecialidade);
				pesquisar();
			}
		} finally {
			this.seqEspecialidade = null;
		}
	
	}

	public void pesquisar() {
		try {
			this.pesquisaInternacaoFacade.validaDadosPesquisaReferencialEspecialidadeProfissional(this.especialidade);
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparPesquisa() {
		this.especialidade = null;
		this.dataModel.limparPesquisa();
	}

	public void limparEspecialidade() {
		this.especialidade = null;
	}

	public boolean isMostrarLinkExcluirEspecialidade() {
		return this.especialidade != null
				&& this.especialidade.getSeq() != null;
	}
	
	
	public String cancelarPesquisaReferencialEspecialidadeProfissional(){
		if (cameFrom.equals("refCliEsp")) {
			return PAGE_PESQ_REF_CLI_ESP;
		}
		else {
			return null;
		}
	}

	public String redirecionaPesqPacProfEsp(ReferencialEspecialidadeProfissonalGridVO refEspProfGridVO){
		this.pesquisaPacientesProfissionalEspecialidadePaginatorController.setSeqEspecialidade(refEspProfGridVO.getEspSeq());
		this.pesquisaPacientesProfissionalEspecialidadePaginatorController.setCrmProfissional(refEspProfGridVO.getCrm());
		this.pesquisaPacientesProfissionalEspecialidadePaginatorController.setCameFrom("refEspProf");
		this.pesquisaPacientesProfissionalEspecialidadePaginatorController.inicializar();
		return PAGE_PESQ_PAC_PROF_ESP;
	}
	
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = Long.valueOf(0);

		try {
			count = this.pesquisaInternacaoFacade.pesquisarReferencialEspecialidadeProfissonalGridVOCount(this.especialidade);
		} catch (ApplicationBusinessException e) {
			
			apresentarExcecaoNegocio(e);
		}

		return count;
	}

	@Override
	public List<ReferencialEspecialidadeProfissonalGridVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<ReferencialEspecialidadeProfissonalGridVO> result = null;

		try {
			result = this.pesquisaInternacaoFacade
					.pesquisarReferencialEspecialidadeProfissonalGridVO(
							firstResult, maxResult, orderProperty, asc,
							this.especialidade);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}

		if (result == null) {
			result = new ArrayList<ReferencialEspecialidadeProfissonalGridVO>();
		}

		return result;
	}
	
	/**
	 * 
	 * Lista as Especialidades pela sigla e nome
	 * 
	 * @return
	 */	
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(String paramPesquisa) {
		return this.returnSGWithCount(cadastrosBasicosInternacaoFacade.pesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa),pesquisarEspecialidadesInternasPorSiglaENomeCount(paramPesquisa));
	}

	public Integer pesquisarEspecialidadesInternasPorSiglaENomeCount(String paramPesquisa) {
		return cadastrosBasicosInternacaoFacade.pesquisarEspecialidadesInternasPorSiglaENomeCount(paramPesquisa).intValue();
	}

	public String getCameFrom() {
		return this.cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Short getSeqEspecialidade() {
		return this.seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

	public ReferencialEspecialidadeProfissonalGridVO getReferencialEspecialidadeProfissonalGridVO() {
		return referencialEspecialidadeProfissonalGridVO;
	}

	public void setReferencialEspecialidadeProfissonalGridVO(
			ReferencialEspecialidadeProfissonalGridVO referencialEspecialidadeProfissonalGridVO) {
		this.referencialEspecialidadeProfissonalGridVO = referencialEspecialidadeProfissonalGridVO;
	}
}