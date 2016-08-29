package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoPadraoPdtPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 descricaoPadrao = new PdtDescPadrao();
	}

	@Inject @Paginator
	private DynamicDataModel<PdtDescPadrao> dataModel;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -78678869136973541L;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private PdtDescPadrao descricaoPadrao;

	private AghEspecialidades aghEspecialidades;
	
	private PdtProcDiagTerap pdtProcDiagTerap;
	
	private String titulo;
	
	@Inject
	private DescricaoPadraoPdtCRUDController descricaoPadraoPdtCRUDController;
	
	private final String PAGE_CAD_DESCRICAO_PADRAO_PDT  = "descricaoPadraoPdtCRUD";

	

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.descricaoPadrao = new PdtDescPadrao();
		this.descricaoPadrao.setTitulo(null);
		this.descricaoPadrao.setAghEspecialidades(null);
		this.descricaoPadrao.setPdtProcDiagTerap(null);
		this.aghEspecialidades = null;		
		this.pdtProcDiagTerap = null;
		this.setTitulo(null);
		this.dataModel.limparPesquisa();
	}

	
	
	//Metodo para Suggestion Box de especialidades
	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidades((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesCount((String) strPesquisa);
	}
	
	//Metodo para Suggestion Box de Procedimentos Diag. Terapeuticos
	public List<PdtProcDiagTerap> obterProcedimentoDiagTerapeuticos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerap(objPesquisa),obterProcedimentoDiagTerapeuticosCount(objPesquisa));
	}
	
	public Long obterProcedimentoDiagTerapeuticosCount(String objPesquisa){
		return this.blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapCount(objPesquisa);
	}

	
	public String novo(){		
		this.setDescricaoPadrao(null);
		descricaoPadraoPdtCRUDController.setDescricaoPadrao(null);
		return PAGE_CAD_DESCRICAO_PADRAO_PDT;
	}
	public String editar(){	
		if (descricaoPadrao != null && descricaoPadrao.getId() != null ) {
			try {
				blocoCirurgicoCadastroApoioFacade.verificarExisteRegistroPdtDescPadrao(descricaoPadrao);
				return PAGE_CAD_DESCRICAO_PADRAO_PDT;
			} catch (ApplicationBusinessException e) {
				descricaoPadrao = null;
				apresentarExcecaoNegocio(e);
			}
		}else{
			apresentarMsgNegocio(Severity.ERROR, "REGISTRO_NULO_BUSCA");
			this.dataModel.reiniciarPaginator();
			return null;
		}
		return null;

	}	
	
		
	@Override
	public Long recuperarCount() {
		Short especialidadeId = null;
		Integer procedimentoId = null;
		if(getAghEspecialidades()!= null){
			especialidadeId = getAghEspecialidades().getSeq();
		}
		if(getPdtProcDiagTerap()!=null){
			procedimentoId = getPdtProcDiagTerap().getSeq();
		}
		return blocoCirurgicoProcDiagTerapFacade.pesquisarDescricaoPadraoCount(especialidadeId, procedimentoId, this.titulo);
	}
	
	@Override
	public List<PdtDescPadrao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short especialidadeId = null;
		Integer procedimentoId = null;
		if(getAghEspecialidades()!= null){
			especialidadeId = getAghEspecialidades().getSeq();
		}
		if(getPdtProcDiagTerap()!=null){
			procedimentoId = getPdtProcDiagTerap().getSeq();
		}
		return blocoCirurgicoProcDiagTerapFacade.pesquisarDescricaoPadrao
			(firstResult, maxResult, orderProperty, asc, especialidadeId, procedimentoId, this.titulo);
	}
	
	
	public void excluir() {
		String msgRetorno = this.blocoCirurgicoCadastroApoioFacade.excluirPdtDescPadrao(this.descricaoPadrao);	
		this.apresentarMsgNegocio(Severity.INFO,msgRetorno);
		this.pesquisar();
	}
	
	public PdtDescPadrao getDescricaoPadrao() {
		return descricaoPadrao;
	}

	public void setDescricaoPadrao(PdtDescPadrao descricaoPadrao) {
		this.descricaoPadrao = descricaoPadrao;
	}
	

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setPdtProcDiagTerap(PdtProcDiagTerap pdtProcDiagTerap) {
		this.pdtProcDiagTerap = pdtProcDiagTerap;
	}

	public PdtProcDiagTerap getPdtProcDiagTerap() {
		return pdtProcDiagTerap;
	}
 


	public DynamicDataModel<PdtDescPadrao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtDescPadrao> dataModel) {
	 this.dataModel = dataModel;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}