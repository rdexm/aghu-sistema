package br.gov.mec.aghu.compras.autfornecimento.action;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class TesteItensAutorizacaoFornecimentoController extends ActionController {

	private static final String LISTAR_ITENS_AUTORIZACAO_FORNECIMENTO = "listarItensAutorizacaoFornecimento";

	private static final long serialVersionUID = -4305412613061953416L;

	private Integer numeroAf;
	private Integer numeroComplemento;
	private boolean btProgramacaoPendente;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() throws ApplicationBusinessException {
	 

	 

		numeroAf = null;
		numeroComplemento = null;
	
	}
	

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @param numeroAf
	 *            the numeroAf to set
	 */
	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	/**
	 * @return the numeroAf
	 */
	public Integer getNumeroAf() {
		return numeroAf;
	}

	/**
	 * @param numeroComplemento
	 *            the numeroComplemento to set
	 */
	public void setNumeroComplemento(Integer numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	/**
	 * @return the numeroComplemento
	 */
	public Integer getNumeroComplemento() {
		return numeroComplemento;
	}

	public String direcionarParaProgramacaoPendente() {
		this.btProgramacaoPendente = true;
		return LISTAR_ITENS_AUTORIZACAO_FORNECIMENTO;
	}

	public String direcionarParaProgramacaoGeral() {
		this.btProgramacaoPendente = false;
		return LISTAR_ITENS_AUTORIZACAO_FORNECIMENTO;
	}

	/**
	 * @param btProgramacaoPendente
	 *            the btProgramacaoPendente to set
	 */
	public void setBtProgramacaoPendente(boolean btProgramacaoPendente) {
		this.btProgramacaoPendente = btProgramacaoPendente;
	}

	/**
	 * @return the btProgramacaoPendente
	 */
	public boolean isBtProgramacaoPendente() {
		return btProgramacaoPendente;
	}

}
