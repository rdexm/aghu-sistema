package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ParametrosProcedureVO {

	private String vElaborarPrcrMed;

	private boolean vElaborarPrcrMedPermissao;

	private String vUsaControlePrevAlta;

	private BigDecimal vHorasControlePrevAlta;

	private BigDecimal vDiasPadraoCtrlPrevAlta;

	private Date vHrLimControlPrevAlta;

	private String vSisExigeEstadoPaciente;

	private String vTemTriagem;

	private Integer vUnfSeq;

	private String vMostraEstPac;

	private String vUnidCti;

	private BigDecimal vUnfCtiPaiAdulto;
	
	private Long rgtSeq;

	public String getvElaborarPrcrMed() {
		return vElaborarPrcrMed;
	}

	public void setvElaborarPrcrMed(String vElaborarPrcrMed) {
		this.vElaborarPrcrMed = vElaborarPrcrMed;
	}

	public boolean isvElaborarPrcrMedPermissao() {
		return vElaborarPrcrMedPermissao;
	}

	public void setvElaborarPrcrMedPermissao(boolean vElaborarPrcrMedPermissao) {
		this.vElaborarPrcrMedPermissao = vElaborarPrcrMedPermissao;
	}

	public String getvUsaControlePrevAlta() {
		return vUsaControlePrevAlta;
	}

	public void setvUsaControlePrevAlta(String vUsaControlePrevAlta) {
		this.vUsaControlePrevAlta = vUsaControlePrevAlta;
	}

	public BigDecimal getvHorasControlePrevAlta() {
		return vHorasControlePrevAlta;
	}

	public void setvHorasControlePrevAlta(BigDecimal vHorasControlePrevAlta) {
		this.vHorasControlePrevAlta = vHorasControlePrevAlta;
	}

	public BigDecimal getvDiasPadraoCtrlPrevAlta() {
		return vDiasPadraoCtrlPrevAlta;
	}

	public void setvDiasPadraoCtrlPrevAlta(BigDecimal vDiasPadraoCtrlPrevAlta) {
		this.vDiasPadraoCtrlPrevAlta = vDiasPadraoCtrlPrevAlta;
	}

	public Date getvHrLimControlPrevAlta() {
		return vHrLimControlPrevAlta;
	}

	public void setvHrLimControlPrevAlta(Date vHrLimControlPrevAlta) {
		this.vHrLimControlPrevAlta = vHrLimControlPrevAlta;
	}

	public String getvSisExigeEstadoPaciente() {
		return vSisExigeEstadoPaciente;
	}

	public void setvSisExigeEstadoPaciente(String vSisExigeEstadoPaciente) {
		this.vSisExigeEstadoPaciente = vSisExigeEstadoPaciente;
	}

	public String getvTemTriagem() {
		return vTemTriagem;
	}

	public void setvTemTriagem(String vTemTriagem) {
		this.vTemTriagem = vTemTriagem;
	}

	public Integer getvUnfSeq() {
		return vUnfSeq;
	}

	public void setvUnfSeq(Integer vUnfSeq) {
		this.vUnfSeq = vUnfSeq;
	}

	public String getvMostraEstPac() {
		return vMostraEstPac;
	}

	public void setvMostraEstPac(String vMostraEstPac) {
		this.vMostraEstPac = vMostraEstPac;
	}

	public String getvUnidCti() {
		return vUnidCti;
	}

	public void setvUnidCti(String vUnidCti) {
		this.vUnidCti = vUnidCti;
	}

	public BigDecimal getvUnfCtiPaiAdulto() {
		return vUnfCtiPaiAdulto;
	}

	public void setvUnfCtiPaiAdulto(BigDecimal vUnfCtiPaiAdulto) {
		this.vUnfCtiPaiAdulto = vUnfCtiPaiAdulto;
	}

	public Long getRgtSeq() {
		return rgtSeq;
	}

	public void setRgtSeq(Long rgtSeq) {
		this.rgtSeq = rgtSeq;
	}

}
