package br.gov.mec.aghu.faturamento.vo;


public class TotaisPorDCIHVO {

	// 'contas apresentadas' seq_rea 
	private String situacao;

	// dci.codigo_dcih dcih
	private String dcih;

	// tcs.descricao c
	private String descricao;

	// count(cth.seq) qtd
	private Long qtd;

	// sum( cth.valor_sh + vct.valor_sh_uti + vct.valor_sh_utie....
	private Double hosp; 

	// sum( cth.valor_sp + vct.valor_sp_uti + vct.valor_sp_utie...
	private Double prof;

	// sum( cth.valor_sadt + vct.valor_sadt_uti + vct.valor_sadt_utie
	private Double sadt;

	// sum(cth.valor_hemat) hemat
	private Double hemat;
	
	// sum(cth.valor_opm)   prote
	private Double prote;
	
	// sum( cth.valor_sh + vct.valor_sh_uti + vct.valor_sh_utie + vct.valor_sh_acomp +...
	private Double total;

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getDcih() {
		return dcih;
	}

	public void setDcih(String dcih) {
		this.dcih = dcih;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}

	public Double getHosp() {
		return hosp;
	}

	public void setHosp(Double hosp) {
		this.hosp = hosp;
	}

	public Double getProf() {
		return prof;
	}

	public void setProf(Double prof) {
		this.prof = prof;
	}

	public Double getSadt() {
		return sadt;
	}

	public void setSadt(Double sadt) {
		this.sadt = sadt;
	}

	public Double getHemat() {
		return hemat;
	}

	public void setHemat(Double hemat) {
		this.hemat = hemat;
	}

	public Double getProte() {
		return prote;
	}

	public void setProte(Double prote) {
		this.prote = prote;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}