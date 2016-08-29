package br.gov.mec.aghu.faturamento.vo;


public class FaturamentoPorProcedimentoVO {

	private Long codSus;
	
	private String descricao;

	private Long qtd;

	private Double hosp;

	private Double prof;

	private Long qtdProc;

	private Double sadtProc;

	private Double servHospProc;

	private Double servProfProc;

	private Long qtdAih;

	private Double sadtAih;

	private Double hospAih;

	private Double profAih;

	private Integer ordem;

	
	
	private Double diariaAcompServHosp;
	
	private Double diariaAcompServProf;
	
	private Double diariaUtiHosp;
	
	private Double diariaUtiProf;
	
	private Long diasAcomp;
	
	private Long diasUTI;
	
	private Double valorUTI;
	
	private Double valorAcomp;

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

	public Long getQtdProc() {
		return qtdProc;
	}

	public void setQtdProc(Long qtdProc) {
		this.qtdProc = qtdProc;
	}

	public Double getSadtProc() {
		return sadtProc;
	}

	public void setSadtProc(Double sadtProc) {
		this.sadtProc = sadtProc;
	}

	public Double getServHospProc() {
		return servHospProc;
	}

	public void setServHospProc(Double servHospProc) {
		this.servHospProc = servHospProc;
	}

	public Double getServProfProc() {
		return servProfProc;
	}

	public void setServProfProc(Double servProfProc) {
		this.servProfProc = servProfProc;
	}

	public Long getQtdAih() {
		return qtdAih;
	}

	public void setQtdAih(Long qtdAih) {
		this.qtdAih = qtdAih;
	}

	public Double getSadtAih() {
		return sadtAih;
	}

	public void setSadtAih(Double sadtAih) {
		this.sadtAih = sadtAih;
	}

	public Double getHospAih() {
		return hospAih;
	}

	public void setHospAih(Double hospAih) {
		this.hospAih = hospAih;
	}

	public Double getProfAih() {
		return profAih;
	}

	public void setProfAih(Double profAih) {
		this.profAih = profAih;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Double getDiariaAcompServHosp() {
		return diariaAcompServHosp;
	}

	public void setDiariaAcompServHosp(Double diariaAcompServHosp) {
		this.diariaAcompServHosp = diariaAcompServHosp;
	}

	public Double getDiariaAcompServProf() {
		return diariaAcompServProf;
	}

	public void setDiariaAcompServProf(Double diariaAcompServProf) {
		this.diariaAcompServProf = diariaAcompServProf;
	}

	public Double getDiariaUtiHosp() {
		return diariaUtiHosp;
	}

	public void setDiariaUtiHosp(Double diariaUtiHosp) {
		this.diariaUtiHosp = diariaUtiHosp;
	}

	public Double getDiariaUtiProf() {
		return diariaUtiProf;
	}

	public void setDiariaUtiProf(Double diariaUtiProf) {
		this.diariaUtiProf = diariaUtiProf;
	}

	public Long getDiasAcomp() {
		return diasAcomp;
	}

	public void setDiasAcomp(Long diasAcomp) {
		this.diasAcomp = diasAcomp;
	}

	public Long getDiasUTI() {
		return diasUTI;
	}

	public void setDiasUTI(Long diasUTI) {
		this.diasUTI = diasUTI;
	}

	public Double getValorUTI() {
		return valorUTI;
	}

	public void setValorUTI(Double valorUTI) {
		this.valorUTI = valorUTI;
	}

	public Double getValorAcomp() {
		return valorAcomp;
	}

	public void setValorAcomp(Double valorAcomp) {
		this.valorAcomp = valorAcomp;
	}

	public Long getCodSus() {
		return codSus;
	}

	public void setCodSus(Long codSus) {
		this.codSus = codSus;
	}
}