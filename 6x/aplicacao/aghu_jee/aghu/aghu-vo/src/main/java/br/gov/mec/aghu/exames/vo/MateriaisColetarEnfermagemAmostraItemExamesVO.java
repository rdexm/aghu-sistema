package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;

public class MateriaisColetarEnfermagemAmostraItemExamesVO {

	private String unfDescricao;
	private String nomeUsualMaterial;
	private Date dthrProgramada;
	private Byte nroAmostras;
	private Short iseSeqp;
	private Byte intervaloDias;
	private Date intervaloHoras;
	private DominioSituacaoAmostra amoItemSituacao;	
	
	public MateriaisColetarEnfermagemAmostraItemExamesVO() {}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}


	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}

	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}

	public Date getDthrProgramada() {
		return dthrProgramada;
	}

	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	public Byte getNroAmostras() {
		return nroAmostras;
	}

	public void setNroAmostras(Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Byte getIntervaloDias() {
		return intervaloDias;
	}

	public void setIntervaloDias(Byte intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	public Date getIntervaloHoras() {
		return intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}

	public DominioSituacaoAmostra getAmoItemSituacao() {
		return amoItemSituacao;
	}

	public void setAmoItemSituacao(DominioSituacaoAmostra amoItemSituacao) {
		this.amoItemSituacao = amoItemSituacao;
	}	
}
