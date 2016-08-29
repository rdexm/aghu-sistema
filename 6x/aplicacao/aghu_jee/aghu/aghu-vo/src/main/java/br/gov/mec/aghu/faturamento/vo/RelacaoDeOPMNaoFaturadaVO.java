package br.gov.mec.aghu.faturamento.vo;

import java.sql.Timestamp;

public class RelacaoDeOPMNaoFaturadaVO {

	private String sigla;

	private String especialidade;

	private Integer prontuario;

	private String pacnome;

	private String leito;

	private Long nroaih;

	private Integer ssm;

	private Long codtabela;

	private String descricao;

	private Integer unfseq;

	private Timestamp datautl;

	private Long quantidade;

	private Double valorapres;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPacnome() {
		return pacnome;
	}

	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Long getNroaih() {
		return nroaih;
	}

	public void setNroaih(Long nroaih) {
		this.nroaih = nroaih;
	}

	public Integer getSsm() {
		return ssm;
	}

	public void setSsm(Integer ssm) {
		this.ssm = ssm;
	}

	public Long getCodtabela() {
		return codtabela;
	}

	public void setCodtabela(Long codtabela) {
		this.codtabela = codtabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getUnfseq() {
		return unfseq;
	}

	public void setUnfseq(Integer unfseq) {
		this.unfseq = unfseq;
	}

	public Timestamp getDatautl() {
		return datautl;
	}

	public void setDatautl(Timestamp datautl) {
		this.datautl = datautl;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValorapres() {
		return valorapres;
	}

	public void setValorapres(Double valorapres) {
		this.valorapres = valorapres;
	}

}