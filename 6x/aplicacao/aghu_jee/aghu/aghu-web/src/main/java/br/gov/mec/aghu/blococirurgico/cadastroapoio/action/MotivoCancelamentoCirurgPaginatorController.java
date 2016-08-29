package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class MotivoCancelamentoCirurgPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcMotivoCancelamento> dataModel;
	private static final String MOTIVO_CANCELAMENTO_CRUD = "motivoCancelamentoCRUD";
	private static final String MOTIVO_CANCELAMENTO_LIST = "motivoCancelamentoList";
	private static final String QUESTAO_CANCELAMENTO = "questaoCancelamento";

	@Inject
	private QuestaoCancelamentoController questaoCancelamentoController;
	
	@Inject
	private MotivoCancelamentoCirurgController motivoCancelamentoCirurgController;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7228613921790065434L;
	
	//Campos filtro
	private Short codigo;
	private String descricao;
	private DominioSimNao erroAgendamento;
	private DominioSimNao destSr;
	private DominioMotivoCancelamento classificacao;
	private DominioSituacao situacao;
	

	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	public String iniciarInclusao() {
		motivoCancelamentoCirurgController.setSeq(null);
		motivoCancelamentoCirurgController.setMotivoCancelamento(new MbcMotivoCancelamento());
		motivoCancelamentoCirurgController.setSituacaoCheck(Boolean.TRUE);
		motivoCancelamentoCirurgController.setPerfilCancelamentoList(null);
		motivoCancelamentoCirurgController.setEmEdicao(false);
		motivoCancelamentoCirurgController.setVoltarPara(MOTIVO_CANCELAMENTO_LIST);
		return MOTIVO_CANCELAMENTO_CRUD;
	}
	
	public String redirecionarCadastro(Short seq) {
		motivoCancelamentoCirurgController.setSeq(seq);
		motivoCancelamentoCirurgController.setVoltarPara(MOTIVO_CANCELAMENTO_LIST);
		return MOTIVO_CANCELAMENTO_CRUD;
	}
	
	public String redirecionarQuestaoCancelamento(Short seq) {
		questaoCancelamentoController.setSeq(seq);
		questaoCancelamentoController.setVoltarPara(MOTIVO_CANCELAMENTO_LIST);
		return QUESTAO_CANCELAMENTO;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		//Limpa filtro
		codigo = null;
		descricao = null;
		situacao = null;
		exibirBotaoNovo = false;
		erroAgendamento = null;
		destSr = null;
		classificacao = null;
		
		//Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
	}
	
	@Override
	public List<MbcMotivoCancelamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return blocoCirurgicoFacade.pesquisarMotivosCancelamento(
				this.codigo, this.descricao, this.obterErroAgendamento(this.erroAgendamento), 
				this.obterDestSr(this.destSr), this.classificacao, this.situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoFacade.pesquisarMotivosCancelamentoCount(
				this.codigo, this.descricao, this.obterErroAgendamento(
						this.erroAgendamento), this.obterDestSr(this.destSr), this.classificacao, this.situacao);
	}
	
	public String obterDescricaoSimNao(Boolean valor) {
		if(valor != null) {
			return DominioSimNao.getInstance(valor).getDescricao();
		}
		return null;
	}
	
	
	private Boolean obterErroAgendamento(DominioSimNao valor) {
		if(valor != null) {
			return this.erroAgendamento.isSim();
		}
		return null;
	}
	
	
	private Boolean obterDestSr(DominioSimNao valor) {
		if(valor != null) {
			return this.destSr.isSim();
		}
		return null;
	}
	

	/** GET/SET **/
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DominioSimNao getErroAgendamento() {
		return erroAgendamento;
	}

	public void setErroAgendamento(DominioSimNao erroAgendamento) {
		this.erroAgendamento = erroAgendamento;
	}

	public DominioSimNao getDestSr() {
		return destSr;
	}

	public void setDestSr(DominioSimNao destSr) {
		this.destSr = destSr;
	}

	public DominioMotivoCancelamento getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(DominioMotivoCancelamento classificacao) {
		this.classificacao = classificacao;
	}
 


	public DynamicDataModel<MbcMotivoCancelamento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcMotivoCancelamento> dataModel) {
	 this.dataModel = dataModel;
	}
}
