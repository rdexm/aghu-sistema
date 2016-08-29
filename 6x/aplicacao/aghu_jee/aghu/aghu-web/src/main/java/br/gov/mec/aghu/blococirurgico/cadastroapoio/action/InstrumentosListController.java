package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class InstrumentosListController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PdtInstrumental> dataModel;

	
	private static final long serialVersionUID = 8377124220277489320L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	private Integer codigo;
	private String descricao;
	private DominioSituacao situacao;
	private Integer codigoExclusao;
	private Boolean exibirBotaoNovo;	
	
	private PdtInstrumental pdtInstrumentalSel;
	
	private final String PAGE_CAD_INSTRUMENTAL = "instrumentosEquipamentosCRUD";
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}
	
	public void limpar(){		
		codigo = null;
		descricao = null;
		situacao = null;
		exibirBotaoNovo = false;
		this.dataModel.limparPesquisa();
	}
	
	public String novo(){		
		this.setPdtInstrumentalSel(null);
		return PAGE_CAD_INSTRUMENTAL;
	}
	public String editar(){		
		return  PAGE_CAD_INSTRUMENTAL;
	}
	
	@Override
	public List<PdtInstrumental> recuperarListaPaginada(Integer firstResult,Integer maxResult,String orderProperty,boolean asc) {
		return blocoCirurgicoCadastroApoioFacade.listarPdtInstrumentalPorSeqDescricaoSituacao(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoCadastroApoioFacade.listarPdtInstrumentalPorSeqDescricaoSituacaoCount(codigo, descricao, situacao);
	}
	
	public void excluir() {		
		PdtInstrumental instrumento = blocoCirurgicoCadastroApoioFacade.obterPdtInstrumentalPorSeq(codigo);
		String mensagemSucesso = blocoCirurgicoCadastroApoioFacade.removerPdtInstrumental(instrumento);
		apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		codigo = null;		
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getCodigoExclusao() {
		return codigoExclusao;
	}

	public void setCodigoExclusao(Integer codigoExclusao) {
		this.codigoExclusao = codigoExclusao;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
	 


	public DynamicDataModel<PdtInstrumental> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtInstrumental> dataModel) {
	 this.dataModel = dataModel;
	}

	public PdtInstrumental getPdtInstrumentalSel() {
		return pdtInstrumentalSel;
	}

	public void setPdtInstrumentalSel(PdtInstrumental pdtInstrumentalSel) {
		this.pdtInstrumentalSel = pdtInstrumentalSel;
	}
}
