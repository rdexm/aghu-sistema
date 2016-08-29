package br.gov.mec.aghu.farmacia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.farmacia.vo.InformacaoEnviadaPrescribenteVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class InformacaoEnviadaPrescribenteController extends ActionController implements ActionPaginator  {	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711227558570163183L;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Inject
	@Paginator
	private DynamicDataModel<InformacaoEnviadaPrescribenteVO> dataModel;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaFacade;
	
	private InformacaoEnviadaPrescribenteVO filtro = new InformacaoEnviadaPrescribenteVO();		
	
	private InformacaoEnviadaPrescribenteVO filtroSelection = new InformacaoEnviadaPrescribenteVO();
	
	private AghUnidadesFuncionais unidadeFuncional;
	
	private DominioSimNao variavelCombo;
	
	private final static String TELA_INCLUSAO = "farmacia-enviarInformacaoPrescribente";
	private final static String TELA_VISUALIZAR = "farmacia-visualizarInformacaoPrescribente";
	
	public void preencherfiltroUnidadeFuncional(){
		filtro.setSeqUnidadesFuncionais(this.unidadeFuncional.getSeq());		
	}
	
	public String editar(InformacaoEnviadaPrescribenteVO objeto){
		return TELA_INCLUSAO;
	}
	
	public String visualizar(InformacaoEnviadaPrescribenteVO objeto){
		return TELA_VISUALIZAR;
	}
	public void pesquisar() {
		filtro.setIndInfVerificada(DominioSimNao.getBooleanInstance(variavelCombo));
		this.dataModel.reiniciarPaginator();		
	}	
	
	public void limparPesquisa() {
		this.variavelCombo = null;
		unidadeFuncional = null;
		this.filtro = new InformacaoEnviadaPrescribenteVO();				
		this.dataModel.limparPesquisa();		
	}
	
	public DynamicDataModel<InformacaoEnviadaPrescribenteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<InformacaoEnviadaPrescribenteVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public InformacaoEnviadaPrescribenteVO getFiltro() {
		return filtro;
	}

	public void setFiltro(InformacaoEnviadaPrescribenteVO filtro) {
		this.filtro = filtro;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Override
	public Long recuperarCount() {	
		return this.farmaciaFacade.pesquisarInformacaoEnviadaPrescribenteCount(this.filtro);
	}	

	@Override
	public List<InformacaoEnviadaPrescribenteVO> recuperarListaPaginada(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc) {
		return   this.farmaciaFacade.pesquisarInformacaoEnviadaPrescribente(firstResult, maxResult, orderProperty, asc, this.filtro);
	}
	
	public List<AghUnidadesFuncionais> consultaUnidadefuncional(String param){
		return  this.returnSGWithCount(this.farmaciaFacade.consultaPorDescricaoUnidadeFuncionalOrderCodDesc(param),this.farmaciaFacade.consultaPorDescricaoUnidadeFuncionalCount(param));		
	}

	public DominioSimNao getVariavelCombo() {
		return variavelCombo;
	}

	public void setVariavelCombo(DominioSimNao variavelCombo) {
		this.variavelCombo = variavelCombo;
	}

	public InformacaoEnviadaPrescribenteVO getFiltroSelection() {
		return filtroSelection;
	}

	public void setFiltroSelection(InformacaoEnviadaPrescribenteVO filtroSelection) {
		this.filtroSelection = filtroSelection;
	}	
	
}
