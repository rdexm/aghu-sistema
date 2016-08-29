package br.gov.mec.aghu.prescricaoenfermagem.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * #4961 - Manter medicamentos x cuidados
 * 
 * @author jback
 * 
 */
public class CuidadoMedicamentoVO implements BaseBean {

	private static final long serialVersionUID = -4527119226337411489L;

	private Short cuiSeq;
	private String descricao;
	private Integer horasAntes;
	private Integer horasApos;
	private String situacao;

	public Short getCuiSeq() {
		return cuiSeq;
	}

	public void setCuiSeq(Short cuiSeq) {
		this.cuiSeq = cuiSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getHorasAntes() {
		return horasAntes;
	}

	public void setHorasAntes(Integer horasAntes) {
		this.horasAntes = horasAntes;
	}

	public Integer getHorasApos() {
		return horasApos;
	}

	public void setHorasApos(Integer horasApos) {
		this.horasApos = horasApos;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

}
