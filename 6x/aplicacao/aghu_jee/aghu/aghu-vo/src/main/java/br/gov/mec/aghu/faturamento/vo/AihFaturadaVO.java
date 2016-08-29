package br.gov.mec.aghu.faturamento.vo;

import java.sql.Timestamp;

public class AihFaturadaVO {

	private String pacNome;

	private Integer prontuario;

	private String cidade;
	
	private Integer cthSeq;

	private Timestamp dataIternacao;

	private Timestamp dataAlta;

	private Long nroAih;

	private Long iphCodSusRealiz;
	
	private String descricaoProcedimento;
	
	private Short phoSeq;

	private Integer seq;

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Timestamp getDataIternacao() {
		return dataIternacao;
	}

	public void setDataIternacao(Timestamp dataIternacao) {
		this.dataIternacao = dataIternacao;
	}

	public Timestamp getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Timestamp dataAlta) {
		this.dataAlta = dataAlta;
	}

	public Long getNroAih() {
		return nroAih;
	}

	public void setNroAih(Long nroAih) {
		this.nroAih = nroAih;
	}

	public Long getIphCodSusRealiz() {
		return iphCodSusRealiz;
	}

	public void setIphCodSusRealiz(Long iphCodSusRealiz) {
		this.iphCodSusRealiz = iphCodSusRealiz;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
