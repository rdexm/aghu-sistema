package br.gov.mec.aghu.emergencia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.model.MamUnidAtendem;
/*import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;*/
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
//import br.gov.mec.aghu.ws.ServiceException;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


/**
 * Classe responsável por controlar as ações da listagem de tipo de unidade funcional.
 */



public class UnidadeFuncionalEmergenciaPaginatorController  extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;

	
	//private static final Log LOG = LogFactory.getLog(UnidadeFuncionalPaginatorController.class);

	private IEmergenciaFacade emergenciaFacade = ServiceLocator.getBean(IEmergenciaFacade.class, "aghu-emergencia");
	

	/**
	 * Atributo referente ao campo de filtro de código de tipo de unidade funcional na tela
	 * de pesquisa.
	 */
	//private Integer codigoPesquisaTiposUnidadeFuncional;

	/**
	 * Atributo referente ao campo de filtro de descrição de tipo de unidade funncional na
	 * tela de pesquisa.
	 */
	//private String descricaoPesquisaTiposUnidadeFuncional;
	
	@Inject @Paginator
	private DynamicDataModel<MamUnidAtendem> dataModel;	
	
	
	private MamUnidAtendem mamUnidAtendem;
	
	private UnidadeFuncional unidadeFuncional;
	private String descricao;
	private DominioSituacao indSituacao;
	
	private final String PAGE_CAD_UNID_FUNC = "unidadeFuncionalEmergenciaCRUD";


	@PostConstruct
	public void init() {
		//LOG.info("UnidadeFuncionalEmergenciaPaginatorController method - PostConstruct");
		
		begin(conversation, true);	
		mamUnidAtendem = new MamUnidAtendem();
		indSituacao = indSituacao.A;
	}

		
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();		
	}

	public void limparPesquisa() {
		this.unidadeFuncional = null;
		this.descricao = null;	
		this.indSituacao = null;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * unidade funcional.
	 */
	public void excluir() {
		
		try {
			if (mamUnidAtendem.getUnfSeq() != null) {
				this.emergenciaFacade.excluir(mamUnidAtendem.getUnfSeq());
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_EXCLUSAO_UNIDADE");
			} 			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String novo(){		
		this.setMamUnidAtendem(null);
		return PAGE_CAD_UNID_FUNC;
	}
	public String editar(){		
		return PAGE_CAD_UNID_FUNC;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Short unfSeq = unidadeFuncional != null ? unidadeFuncional.getSeq() : null;
		return this.emergenciaFacade.pesquisarUnidadesFuncionaisEmergenciaCount(unfSeq, getDescricao(), getIndSituacao());
		
		
	}


	@Override
	public List<MamUnidAtendem> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		Short unfSeq = unidadeFuncional != null ? unidadeFuncional.getSeq() : null;
		
		return this.emergenciaFacade.pesquisarUnidadesFuncionaisEmergencia(
				firstResult, maxResults, orderProperty, asc,
				unfSeq, getDescricao(), getIndSituacao());		
		
	}
	
	public List<UnidadeFuncional> pesquisarUnidadeFuncional(String objPesquisa) throws ApplicationBusinessException {
		return  this.returnSGWithCount(this.emergenciaFacade.pesquisarUnidadeFuncional(objPesquisa),pesquisarUnidadeFuncionalCount(objPesquisa));
	}
	
	public Long pesquisarUnidadeFuncionalCount(String objPesquisa) throws ApplicationBusinessException {
		return this.emergenciaFacade.pesquisarUnidadeFuncionalCount(objPesquisa);
	}
	
	public Boolean getBolIndSituacao(DominioSituacao indSituacao){		
		return indSituacao != null && indSituacao.equals(DominioSituacao.A);		
	}

	// ### GETs e SETs ###

	


	public DynamicDataModel<MamUnidAtendem> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamUnidAtendem> dataModel) {
		this.dataModel = dataModel;
	}


	public MamUnidAtendem getMamUnidAtendem() {
		return mamUnidAtendem;
	}


	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}
	
	public UnidadeFuncional getUnidadeFuncional() {
		return unidadeFuncional;
	}


	public void setUnidadeFuncional(UnidadeFuncional unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
}
