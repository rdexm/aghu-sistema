package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastrarCaracteristicasDeComplexidadeController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6997702441706919433L;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		limparInclusao();
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCaractComplexidade caractComplexidade = new FatCaractComplexidade();
	
	private FatCaractComplexidade caractComplexidadePai;

	private Boolean situacao;
	
	private String voltarPara;
	
	private Integer seqSus;
	
	/**
	 * 
	 */
	public void limparInclusao(){
		this.caractComplexidade = new FatCaractComplexidade();
		this.caractComplexidadePai = null;
		this.seqSus = null;
		this.situacao = true; // situação vem checkada
	}
	
	/**
	 * Ação Gravar
	 */
	public String gravar() {
		
		this.caractComplexidade.setIndSituacao(DominioSituacao.getInstance(this.situacao));
		this.caractComplexidade.setFatCaractComplexidade(this.caractComplexidadePai);
		this.caractComplexidade.setSeqSus(this.seqSus);
		try {
			this.faturamentoFacade.persistirCaracteristicasDeComplexidade(caractComplexidade, false);

			if (caractComplexidade.getSeq() != null) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_CARACT_COMPLEXIDADE", caractComplexidade.getDescricao());
			}

			limparInclusao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {

		final String retorno = this.voltarPara;
		limparInclusao();
		this.voltarPara = null;
		return retorno;
	}
	
	
	/**
	 * Lista SuggestionBox Caracteristicas De Complexidade Codigo Pai
	 * 
	 * @param caractComplexidade
	 * @return
	 */
	public List<FatCaractComplexidade> listaCaracteristicasDeComplexidadePai(String caractComplexidade) {
		return  this.returnSGWithCount(this.faturamentoFacade.listaCaracteristicasDeComplexidade(caractComplexidade),listaCaracteristicasDeComplexidadePaiCount(caractComplexidade));
	}

	public Long listaCaracteristicasDeComplexidadePaiCount(String caractComplexidade) {
		return this.faturamentoFacade.listaCaracteristicasDeComplexidadeCount(caractComplexidade);
	}
	
	/**
	 * @return the caractComplexidade
	 */
	public FatCaractComplexidade getCaractComplexidade() {
		return caractComplexidade;
	}

	/**
	 * @param caractComplexidade the caractComplexidade to set
	 */
	public void setCaractComplexidade(FatCaractComplexidade caractComplexidade) {
		this.caractComplexidade = caractComplexidade;
	}

	/**
	 * @return the situacao
	 */
	public Boolean getSituacao() {
		return situacao;
	}

	/**
	 * @param situacao the situacao to set
	 */
	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	/**
	 * @return the caractComplexidadePai
	 */
	public FatCaractComplexidade getCaractComplexidadePai() {
		return caractComplexidadePai;
	}

	/**
	 * @param caractComplexidadePai the caractComplexidadePai to set
	 */
	public void setCaractComplexidadePai(FatCaractComplexidade caractComplexidadePai) {
		this.caractComplexidadePai = caractComplexidadePai;
	}

	/**
	 * @return the voltarPara
	 */
	public String getVoltarPara() {
		return voltarPara;
	}

	/**
	 * @param voltarPara the voltarPara to set
	 */
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSeqSus() {
		return seqSus;
	}

	public void setSeqSus(Integer seqSus) {
		this.seqSus = seqSus;
	}
	
}
