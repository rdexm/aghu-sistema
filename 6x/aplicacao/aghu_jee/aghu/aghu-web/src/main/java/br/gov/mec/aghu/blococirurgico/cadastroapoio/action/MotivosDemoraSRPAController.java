package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoDemoraSalaRec;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MotivosDemoraSRPAController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcMotivoDemoraSalaRec> dataModel;

	private static final String MOTIVO_DEMORA_CRUD = "motivosDemoraSRPACRUD";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	
	//filtros
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
		if(this.dataModel.getPesquisaAtiva()){
			pesquisar();
		}
	}

	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo=true;
	}
	
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoFacade.pesquisarMotivosDemoraSalaRecCount(codigo, descricao, situacao);
	}
	
	@Override
	public List<MbcMotivoDemoraSalaRec> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return blocoCirurgicoFacade.pesquisarMotivosDemoraSalaRec(codigo, descricao, situacao, firstResult, maxResult);
	}
	
	public boolean isAtiva(MbcMotivoDemoraSalaRec motivoDemoraSalaRec){
		return motivoDemoraSalaRec.getSituacao().isAtivo();
	}

	public String iniciarInclusao() {
		return MOTIVO_DEMORA_CRUD;
	}
	
	public void ativar(MbcMotivoDemoraSalaRec motivoDemoraSalaRec){
		
		try {
			if(motivoDemoraSalaRec.getSituacao().equals(DominioSituacao.A)){
				motivoDemoraSalaRec.setSituacao(DominioSituacao.I);
			}else{
				motivoDemoraSalaRec.setSituacao(DominioSituacao.A);
			}
			blocoCirurgicoCadastroApoioFacade.atualizarMotivoDemoraSalaRec(motivoDemoraSalaRec);
		
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_DEMORA");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparPesquisa() {
		codigo=null;
		descricao=null;
		situacao=null;
		this.exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	
	
	/*
	 * Getters and Setters abaixo...
	 */
	
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}
	
	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	
	 


	public DynamicDataModel<MbcMotivoDemoraSalaRec> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcMotivoDemoraSalaRec> dataModel) {
	 this.dataModel = dataModel;
	}
}