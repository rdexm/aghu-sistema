package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SumarioSessoesQuimioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5261900545791971585L;

	private Integer atdSeq;
	private Date dtInicio;
	private Date dtFim;
	private int idx;
	private boolean selected;
	private List<RelSumarioSessoesQuimioVO> colecao;

	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Date getDtInicio() {
		return dtInicio;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public Date getDtFim() {
		return dtFim;
	}
	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public List<RelSumarioSessoesQuimioVO> getColecao() {
		return colecao;
	}
	public void setColecao(List<RelSumarioSessoesQuimioVO> colecao) {
		this.colecao = colecao;
	}
}
