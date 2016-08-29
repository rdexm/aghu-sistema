package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * Classe VO com atributos análogos aos parâmetros de saída (OUT) na PROCEDURE MBCP_BUSCA_PRINCIPAL_TELA
 * 
 * @author aghu
 * 
 */
public class PesquisaPrincipalTelaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6221069909465810014L;

	private CirurgiaTelaProcedimentoVO procedimentoPrincipal;
	private Byte qtdeProc;
	private Integer contaProcedimentos;

	public CirurgiaTelaProcedimentoVO getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}

	public void setProcedimentoPrincipal(CirurgiaTelaProcedimentoVO procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}

	public Byte getQtdeProc() {
		return qtdeProc;
	}

	public void setQtdeProc(Byte qtdeProc) {
		this.qtdeProc = qtdeProc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getContaProcedimentos() {
		return contaProcedimentos;
	}

	public void setContaProcedimentos(Integer contaProcedimentos) {
		this.contaProcedimentos = contaProcedimentos;
	}

}
