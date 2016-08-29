package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AssociarNotaDeSalaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final String MANTER_NOTA_SALA = "manterNotaDeSalaUnidade";	
	
	private MbcUnidadeNotaSala unidadeNotaSala;
	
	private MbcEquipamentoNotaSala equipamentoNotaSala;
	private List<MbcEquipamentoNotaSala> equipamentosNotas;
	
	private MbcEquipamentoNotaSala itemSelecionado;
	

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
		atualizaLista();
		equipamentoNotaSala = new MbcEquipamentoNotaSala();
	
	}
	

	public void atualizaLista(){
		equipamentosNotas = this.blocoCirurgicoCadastroApoioFacade.listarEquipamentoNotaSalaPorUnfSeqp(unidadeNotaSala.getId().getUnfSeq(), unidadeNotaSala.getId().getSeqp());
	}
	
	public void editar() {
		try {
			this.blocoCirurgicoCadastroApoioFacade.persistirEquipamentoNotaDeSala(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_EQUIPAMENTO_NOTA_SALA");
			atualizaLista();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	public void confirmar() {
		
		try {
			
						
			equipamentoNotaSala.setMbcUnidadeNotaSala(unidadeNotaSala);			

			this.blocoCirurgicoCadastroApoioFacade.persistirEquipamentoNotaDeSala(equipamentoNotaSala);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_EQUIPAMENTO_NOTA_SALA");

			limpaCampos();
			atualizaLista();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limpaCampos(){
		equipamentoNotaSala = new MbcEquipamentoNotaSala();
	}
	
	public String voltar(){
		this.unidadeNotaSala=null;
		return MANTER_NOTA_SALA;
	}
	/**
	 * Excluir
	 */
	public void excluir()  {
		this.blocoCirurgicoCadastroApoioFacade.excluirEquipamentoNotaDeSala(itemSelecionado.getId());		
		limpaCampos();
		atualizaLista();
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NOTA_SALA");
	}
	
	public List<MbcEquipamentoCirurgico> buscaEquipamentosCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.buscaEquipamentosCirurgicos(objPesquisa),buscaEquipamentosCirurgicosCount(objPesquisa));
	}
	
	public Long buscaEquipamentosCirurgicosCount(String objPesquisa){
		return this.blocoCirurgicoFacade.buscaEquipamentosCirurgicosCount(objPesquisa);
	}
	
	public Integer compararOrdemImp(Object ordem, Object outraOrdem) {
		return ((Short)ordem).compareTo((Short) outraOrdem);
	}

	/*
	 * Getters and Setters abaixo...
	 */


	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public MbcUnidadeNotaSala getUnidadeNotaSala() {
		return unidadeNotaSala;
	}

	public void setUnidadeNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		this.unidadeNotaSala = unidadeNotaSala;
	}

	public MbcEquipamentoNotaSala getEquipamentoNotaSala() {
		return equipamentoNotaSala;
	}

	public void setEquipamentoNotaSala(MbcEquipamentoNotaSala equipamentoNotaSala) {
		this.equipamentoNotaSala = equipamentoNotaSala;
	}

	public List<MbcEquipamentoNotaSala> getEquipamentosNotas() {
		return equipamentosNotas;
	}

	public void setEquipamentosNotas(List<MbcEquipamentoNotaSala> equipamentosNotas) {
		this.equipamentosNotas = equipamentosNotas;
	}

	public MbcEquipamentoNotaSala getItemSelecionado() {
		return itemSelecionado;
	}


	public void setItemSelecionado(MbcEquipamentoNotaSala itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}


}