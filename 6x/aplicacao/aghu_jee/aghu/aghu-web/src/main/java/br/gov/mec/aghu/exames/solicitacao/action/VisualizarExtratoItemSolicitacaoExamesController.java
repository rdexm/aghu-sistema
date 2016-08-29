package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.action.ActionController;



public class VisualizarExtratoItemSolicitacaoExamesController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7110909652356389488L;

	@EJB
	private IExamesFacade examesFacade;

	// Parametros da conversacao para obter o item de solicitacao de exame
	private Integer solicitacao; // Equivale ao parametro iseSoeSeq
	private Short item; // Equivale ao parametro iseSeq
	
	// Campos com as informacoes basicas do item de solicitacao de exame
	private String exame; 
	private String nomePaciente;
	private Integer prontuario;
	
	// lista de extrados do item de solicitacao de exame
	List<AelExtratoItemSolicitacaoVO> listaExtratoItemSolicitacaoVO;
	
	private Boolean isHist = Boolean.FALSE;
	
	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void inicio() {
	 

		
		Object itemSolicitacaoExameObj;
		/*// Obtem o item de solicitacao de exame*/
		if(isHist){
			itemSolicitacaoExameObj = this.examesFacade.buscaItemSolicitacaoExamePorIdHist(this.solicitacao,this.item);
		}else{
			itemSolicitacaoExameObj = this.examesFacade.buscaItemSolicitacaoExamePorId(this.solicitacao,this.item);
		}
		// Popula fieldset com informacoes basicas do extrado do item de solicitacao de exames
		this.popularExtratoItemSolicitacaoExames(itemSolicitacaoExameObj);
		
		// Popula lista de extrados do item de solicitacao de exame
		this.listaExtratoItemSolicitacaoVO = this.examesFacade.pesquisarAelExtratoItemSolicitacaoVOPorItemSolicitacao(this.solicitacao, this.item, isHist);
		
	
	}

	/**
	 * Popula fieldset com informacoes basicas do extrado do item de solicitacao de exames
	 */
	private void popularExtratoItemSolicitacaoExames(Object itemSolicitacaoExameObj){
		
		if(itemSolicitacaoExameObj != null){
			
			if(isHist){
				AelItemSolicExameHist itemSolicitacaoExame = (AelItemSolicExameHist) itemSolicitacaoExameObj;
				processarDescricaoExame(itemSolicitacaoExame.getExame().getDescricao(), itemSolicitacaoExame.getMaterialAnalise().getDescricao());
		
				 // Caso a solicitacao de exame exista realiza a consulta de itens de solicitacao de exame
				if(itemSolicitacaoExame.getSolicitacaoExame() != null){
					// Resgata a solicitacao de exame do item de solicitacao de exame
					final AelSolicitacaoExamesHist solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
					
					// Resgata dados do atendimento da solicitacao de exame
					populaAtributosPaciente(solicitacaoExame.getAtendimento());
				}
			}else{
				AelItemSolicitacaoExames itemSolicitacaoExame = (AelItemSolicitacaoExames) itemSolicitacaoExameObj;
				processarDescricaoExame(itemSolicitacaoExame.getExame().getDescricao(), itemSolicitacaoExame.getMaterialAnalise().getDescricao());
		
				 // Caso a solicitacao de exame exista realiza a consulta de itens de solicitacao de exame
				if(itemSolicitacaoExame.getSolicitacaoExame() != null){
					// Resgata a solicitacao de exame do item de solicitacao de exame
					final AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
					
					// Resgata dados do atendimento da solicitacao de exame
					populaAtributosPaciente(solicitacaoExame.getAtendimento());
				}
			}
		}
	}

	private void populaAtributosPaciente(AghAtendimentos atendimento) {
		// Popula os atributos relacionados ao paciente
		if(atendimento != null){
			this.nomePaciente = atendimento.getPaciente().getNome();
			this.prontuario = atendimento.getPaciente().getProntuario();
		}
	}

	/**
	 * Seta o nome do exame: Composto pela descricao do exame e descricao usual material de analise
	 * @param descricaoUsualExame
	 * @param descricaoMaterialAnalise
	 */
	private void processarDescricaoExame(
			String descricaoUsualExame, String descricaoMaterialAnalise) {
		this.exame = descricaoUsualExame + " / " + descricaoMaterialAnalise;
	}
	
	/*
	 * Getters e Setters
	 */

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	public List<AelExtratoItemSolicitacaoVO> getListaExtratoItemSolicitacaoVO() {
		return listaExtratoItemSolicitacaoVO;
	}
	
	public void setListaExtratoItemSolicitacaoVO(List<AelExtratoItemSolicitacaoVO> listaExtratoItemSolicitacaoVO) {
		this.listaExtratoItemSolicitacaoVO = listaExtratoItemSolicitacaoVO;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

}
