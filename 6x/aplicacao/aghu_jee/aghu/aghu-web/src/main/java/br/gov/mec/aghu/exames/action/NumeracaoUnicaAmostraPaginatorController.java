package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class NumeracaoUnicaAmostraPaginatorController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@EJB
	private IExamesFacade examesFacade;

	private static final long serialVersionUID = 6916921956241308871L;
	
	private static final String PAGE_EXAMES_COPIAR_NUMERACAO_UNICA_AMOSTRA = "exames-copiarNumeracaoUnicaAmostra";

	// campos de pesquisa
	private AelAmostraItemExames origem;
	private AelAmostraItemExames destino;
	private Integer numeroSolicitacaoOrigem;
	private Integer numeroSolicitacaoDestino;
	private Short seqpOrig;
	private Short seqpDest;

	/**
	 * Botão Limpar
	 */
	public void limparPesquisa() {

		this.origem = null;
		this.destino = null;
		this.numeroSolicitacaoOrigem = null;
		this.numeroSolicitacaoDestino = null;
		this.seqpOrig = null;
		this.seqpDest = null;

	}

	/**
	 * Botão Ajustar Numero Unico
	 * 
	 * @return
	 */
	public String ajustarNumeracaoUnica() {
		if (this.origem != null && this.destino != null) {
			return PAGE_EXAMES_COPIAR_NUMERACAO_UNICA_AMOSTRA;
		} else {
			apresentarMsgNegocio(Severity.ERROR, "AEL_01916");
			return null;
		}
	}

	public List<AelAmostraItemExames> obterAmostraItemExameOrigem(String objPesquisa) throws ApplicationBusinessException {

		List<DominioSituacaoAmostra> situacaoAmostras = new ArrayList<DominioSituacaoAmostra>();
		situacaoAmostras.add(DominioSituacaoAmostra.R);
		situacaoAmostras.add(DominioSituacaoAmostra.E);
		return this.examesFacade.buscarAelAmostraItemExamesPorSituacaoAmostras(objPesquisa, situacaoAmostras);

	}
	
	public List<AelAmostraItemExames> obterAmostraItemExameDestino(String objPesquisa) throws ApplicationBusinessException {

		List<DominioSituacaoAmostra> situacaoAmostras = new ArrayList<DominioSituacaoAmostra>();
		situacaoAmostras.add(DominioSituacaoAmostra.R);
		return this.examesFacade.buscarAelAmostraItemExamesPorSituacaoAmostras(objPesquisa, situacaoAmostras);

	}

	/** GET/SET **/

	public Integer getNumeroSolicitacaoOrigem() {
		return numeroSolicitacaoOrigem;
	}

	public void setNumeroSolicitacaoOrigem(Integer numeroSolicitacaoOrigem) {
		this.numeroSolicitacaoOrigem = numeroSolicitacaoOrigem;
	}

	public Integer getNumeroSolicitacaoDestino() {
		return numeroSolicitacaoDestino;
	}

	public void setNumeroSolicitacaoDestino(Integer numeroSolicitacaoDestino) {
		this.numeroSolicitacaoDestino = numeroSolicitacaoDestino;
	}

	public Short getSeqpOrig() {
		return seqpOrig;
	}

	public void setSeqpOrig(Short seqpOrig) {
		this.seqpOrig = seqpOrig;
	}

	public Short getSeqpDest() {
		return seqpDest;
	}

	public void setSeqpDest(Short seqpDest) {
		this.seqpDest = seqpDest;
	}

	public AelAmostraItemExames getOrigem() {
		return origem;
	}

	public void setOrigem(AelAmostraItemExames origem) {
		this.origem = origem;
	}

	public AelAmostraItemExames getDestino() {
		return destino;
	}

	public void setDestino(AelAmostraItemExames destino) {
		this.destino = destino;
	}

}