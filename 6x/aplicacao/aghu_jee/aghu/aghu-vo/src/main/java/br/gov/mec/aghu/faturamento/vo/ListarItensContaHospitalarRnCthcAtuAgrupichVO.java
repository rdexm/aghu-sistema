package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class ListarItensContaHospitalarRnCthcAtuAgrupichVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8340444079406495575L;

	private Integer phiSeq;

	private Date dthrRealizado;
	
	private Date dtAltaAdministrativa;

	private Long quantidadeRealizada;

	private String matricula;

	private String solicitacao;
	
	private Short cspCnvCodigo;
	


	public ListarItensContaHospitalarRnCthcAtuAgrupichVO() {
	}

	public ListarItensContaHospitalarRnCthcAtuAgrupichVO(Integer phiSeq, 
			Long quantidadeRealizada, String matricula, String solicitacao, 
			Date dthrRealizado,	Date  dtAltaAdministrativa, Short cspCnvCodigo) {
		this.phiSeq = phiSeq;
		this.quantidadeRealizada = quantidadeRealizada;
		this.matricula = matricula;
		this.solicitacao = solicitacao;
		this.dthrRealizado = dthrRealizado;
		this.dtAltaAdministrativa = dtAltaAdministrativa;
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public Long getQuantidadeRealizada() {
		return quantidadeRealizada;
	}

	public void setQuantidadeRealizada(Long quantidadeRealizada) {
		this.quantidadeRealizada = quantidadeRealizada;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Date getDtAltaAdministrativa() {
		return dtAltaAdministrativa;
	}

	public void setDtAltaAdministrativa(Date dtAltaAdministrativa) {
		this.dtAltaAdministrativa = dtAltaAdministrativa;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

}
