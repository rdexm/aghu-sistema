package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisaOrdemExamesPOLController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6550599172799727830L;

	private static final String MANTER_ORDEM_EXAMES_POL = "exames-manterOrdemExamesPOL";

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private SecurityController securityController;
	
	private Short ordemNivel1, ordemNivel2;
	private AelExames exame;
	private AelMateriaisAnalises materialAnalise;
	
	//atributos da pesquisa
	private Short ordemNivel1Pesquisa;
	private Short ordemNivel2Pesquisa;
	private String emaExaSigla;
	private Integer emaManSeq;
	private String examePesquisaSigla;
	private Integer materialAnalisePesquisaSeq;
	
	private Boolean editarOrdemExamesPOL;
	
	@Inject @Paginator
	private DynamicDataModel<AelOrdExameMatAnalise> dataModel;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		editarOrdemExamesPOL = securityController.usuarioTemPermissao("editarOrdemExamesPOL", "editar");
	}
	
	public Long listarExamesCount(String objPesquisa) {
		return examesFacade.listarExamesCount((String) objPesquisa);
	}

	public List<AelExames> listarExames(String objPesquisa) {
		return this.returnSGWithCount(examesFacade.listarExames((String) objPesquisa),listarExamesCount(objPesquisa));
	}

	public Long listarMateriaisAnaliseCount(String objPesquisa) {
		return examesFacade.listarMateriaisAnaliseCount((String) objPesquisa);
	}

	public List<AelMateriaisAnalises> listarMateriaisAnalise(String objPesquisa) {
		return this.returnSGWithCount(examesFacade.listarMateriaisAnalise((String) objPesquisa),listarMateriaisAnaliseCount(objPesquisa));
	}

	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarAelOrdExameMatParaPOLCount(exame, materialAnalise, ordemNivel1, ordemNivel2);
	}

	@Override
	public List<AelOrdExameMatAnalise> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return examesFacade.pesquisarAelOrdExameMatParaPOL(firstResult, maxResults, orderProperty, asc, exame, materialAnalise, ordemNivel1, ordemNivel2);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		setOrdemNivel1(null);
		setOrdemNivel2(null);
		setExame(null);
		setMaterialAnalise(null);
	}

	
	public boolean habilitarLinkEditar() {
		
		boolean habilitarBotao = false;
		
		if(editarOrdemExamesPOL) {
			habilitarBotao = true;
		}
		
		return habilitarBotao;
	}
	
	public String editar(){
		return MANTER_ORDEM_EXAMES_POL;
	}

	public Short getOrdemNivel1() {
		return ordemNivel1;
	}

	public void setOrdemNivel1(Short ordemNivel1) {
		this.ordemNivel1 = ordemNivel1;
	}

	public Short getOrdemNivel2() {
		return ordemNivel2;
	}

	public void setOrdemNivel2(Short ordemNivel2) {
		this.ordemNivel2 = ordemNivel2;
	}

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public AelMateriaisAnalises getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(AelMateriaisAnalises materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public Short getOrdemNivel1Pesquisa() {
		return ordemNivel1Pesquisa;
	}

	public void setOrdemNivel1Pesquisa(Short ordemNivel1Pesquisa) {
		this.ordemNivel1Pesquisa = ordemNivel1Pesquisa;
	}

	public Short getOrdemNivel2Pesquisa() {
		return ordemNivel2Pesquisa;
	}

	public void setOrdemNivel2Pesquisa(Short ordemNivel2Pesquisa) {
		this.ordemNivel2Pesquisa = ordemNivel2Pesquisa;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public String getExamePesquisaSigla() {
		return examePesquisaSigla;
	}

	public void setExamePesquisaSigla(String examePesquisaSigla) {
		this.examePesquisaSigla = examePesquisaSigla;
	}

	public Integer getMaterialAnalisePesquisaSeq() {
		return materialAnalisePesquisaSeq;
	}

	public void setMaterialAnalisePesquisaSeq(Integer materialAnalisePesquisaSeq) {
		this.materialAnalisePesquisaSeq = materialAnalisePesquisaSeq;
	}

	public DynamicDataModel<AelOrdExameMatAnalise> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelOrdExameMatAnalise> dataModel) {
		this.dataModel = dataModel;
	}
}