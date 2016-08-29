package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

/**
 * 
 * @author daniel.silva
 * @since 21/08/2012
 * VO utilizado na estoria #17321 - POL: emitir relatório de antendimentos na emergência obstétrica
 */
public class RelatorioAtendEmergObstetricaIntercorrenciasVO implements Serializable {

	private static final long serialVersionUID = 4666179577681141417L;
	private String opaDescricao; //OPA_DESCRICAO de QOPA
	private String ingComplemento; //ING_COMPLEMENTO de QING
	private Integer ingOpaSeq; //ING_OPA_SEQ DE QING
	
	public String getOpaDescricao() {
		return opaDescricao;
	}
	
	public void setOpaDescricao(String opaDescricao) {
		this.opaDescricao = opaDescricao;
	}
	
	public String getIngComplemento() {
		return ingComplemento;
	}
	
	public void setIngComplemento(String ingComplemento) {
		this.ingComplemento = ingComplemento;
	}

	public Integer getIngOpaSeq() {
		return ingOpaSeq;
	}

	public void setIngOpaSeq(Integer ingOpaSeq) {
		this.ingOpaSeq = ingOpaSeq;
	}
	
}
