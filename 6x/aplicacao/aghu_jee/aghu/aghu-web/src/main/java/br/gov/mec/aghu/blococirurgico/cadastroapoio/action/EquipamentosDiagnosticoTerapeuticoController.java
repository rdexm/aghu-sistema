package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class EquipamentosDiagnosticoTerapeuticoController extends ActionController implements ActionPaginator{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PdtEquipamento> dataModel;

	private static final long serialVersionUID = 8184573107688754567L;
	
	private static final String EQUIPAMENTOS_DIAG_CRUD = "equipamentosDiagnosticoTerapeuticoCRUD";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@Inject
	private EquipamentosDiagnosticoTerapeuticoCRUDController equipamentosDiagnosticoTerapeuticoCRUDController;
	
	@EJB
	private IAghuFacade aghuFacade;

	private PdtEquipamento equipamentoDiagTerapeutico;
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	private String enderecoImagens;
	
	private boolean exibirBotaoNovo;
		
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	public void limpar() {
		// Limpa filtro
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.enderecoImagens = null;
		this.exibirBotaoNovo = false;

		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
	}

	public String iniciarInclusao() {
		equipamentosDiagnosticoTerapeuticoCRUDController.setSeqEquipamento(null);
		return EQUIPAMENTOS_DIAG_CRUD;
	}
	
	public String editar(Short seq) {
		equipamentosDiagnosticoTerapeuticoCRUDController.setSeqEquipamento(seq);
		return EQUIPAMENTOS_DIAG_CRUD;
		
	}
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarEquipamentosDiagnosticoTerapeuticoCount(
				descricao, codigo, situacao, enderecoImagens);
	}

	@Override
	public List<PdtEquipamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarEquipamentosDiagnosticoTerapeutico(
				firstResult, maxResult, orderProperty, asc, descricao, codigo, situacao, enderecoImagens);
	}

	// gets e sets
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public PdtEquipamento getEquipamentoDiagTerapeutico() {
		return equipamentoDiagTerapeutico;
	}

	public void setEquipamentoDiagTerapeutico(
			PdtEquipamento equipamentoDiagTerapeutico) {
		this.equipamentoDiagTerapeutico = equipamentoDiagTerapeutico;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getEnderecoImagens() {
		return enderecoImagens;
	}

	public void setEnderecoImagens(String enderecoImagens) {
		this.enderecoImagens = enderecoImagens;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	} 


	public DynamicDataModel<PdtEquipamento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PdtEquipamento> dataModel) {
	 this.dataModel = dataModel;
	}
}