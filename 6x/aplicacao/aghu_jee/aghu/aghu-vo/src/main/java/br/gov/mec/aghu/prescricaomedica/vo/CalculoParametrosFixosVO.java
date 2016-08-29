package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.dominio.DominioTipoLipidio;
import br.gov.mec.aghu.dominio.DominioTipoVolume;

/**
 * #989 - Calcular Nutrição Parenteral Total: VO de MPMK_VARIAVEIS (Parâmetros auxiliares para o cálculo e armazenamento)
 * 
 * @author aghu
 *
 */
public class CalculoParametrosFixosVO {

	// LEGENDAS:
	// PNP = MPM_PRESCRICAO_NPTS

	private Boolean prescricaoCalculada;
	private Boolean moveComponentesCalculados;
	private Boolean calculaNpt;
	private Boolean paramCalcDifUsadoPac;
	private Boolean paramTrocaUnidade;
	private Integer limiteCalculoPeso;
	private DominioTipoVolume tipoCalculoPacAgora; // PNP.TIPO_PARAM_VOLUME
	private Integer seqVelocAdmMlH;
	private String descVelocAdmMlH;
	private Integer composicaoSolNpt;
	private Integer composicaoSolLipidios;
	private Double paramTig;
	private Double paramAmin;
	private Double paramLip;
	private Double paramPercGlic50;
	private Double paramPercGlic10;
	private Double paramPercGlic5;
	private Double paramSodio;
	private Double paramPotassio;
	private Double paramPercKcl;
	private Double paramPercK3po4;
	private Double paramCalcio;
	private Double paramMagnesio;
	private Double paramHeparina;
	private Integer paramAcetZn;
	private Double volAa10;
	private Double volGlicose50;
	private Double volGlicose10;
	private Double volGlicose5;
	private Double volOligo;
	private Double volOligoPed;
	private Double volNacl20;
	private Double volKcl;
	private Double volK3po4;
	private Double volMsgo4;
	private Double volGlucoCa;
	private Double volHeparina;
	private Double volAcetZn;
	private Double volLipidios10;
	private Double volLipidios20;
	private Double paramTempInfusaoLip;
	private Double gotejoSolucao;
	private Double paramTempInfusaoSol;
	private Double gotejoLipidios;
	private DominioTipoLipidio paramTipoLip;
	private DominioTipoVolume tipoParamCalculoVol; // PNP.TIPO_PARAM_VOLUME
	private Double paramOligo;
	private Double volAaPed10;

	public Boolean getPrescricaoCalculada() {
		return prescricaoCalculada;
	}

	public void setPrescricaoCalculada(Boolean prescricaoCalculada) {
		this.prescricaoCalculada = prescricaoCalculada;
	}

	public Boolean getCalculaNpt() {
		return calculaNpt;
	}

	public void setCalculaNpt(Boolean calculaNpt) {
		this.calculaNpt = calculaNpt;
	}

	public Boolean getParamCalcDifUsadoPac() {
		return paramCalcDifUsadoPac;
	}

	public void setParamCalcDifUsadoPac(Boolean paramCalcDifUsadoPac) {
		this.paramCalcDifUsadoPac = paramCalcDifUsadoPac;
	}

	public Boolean getParamTrocaUnidade() {
		return paramTrocaUnidade;
	}

	public void setParamTrocaUnidade(Boolean paramTrocaUnidade) {
		this.paramTrocaUnidade = paramTrocaUnidade;
	}

	public Integer getLimiteCalculoPeso() {
		return limiteCalculoPeso;
	}

	public void setLimiteCalculoPeso(Integer limiteCalculoPeso) {
		this.limiteCalculoPeso = limiteCalculoPeso;
	}

	public DominioTipoVolume getTipoCalculoPacAgora() {
		return tipoCalculoPacAgora;
	}

	public void setTipoCalculoPacAgora(DominioTipoVolume tipoCalculoPacAgora) {
		this.tipoCalculoPacAgora = tipoCalculoPacAgora;
	}

	public Integer getSeqVelocAdmMlH() {
		return seqVelocAdmMlH;
	}

	public void setSeqVelocAdmMlH(Integer seqVelocAdmMlH) {
		this.seqVelocAdmMlH = seqVelocAdmMlH;
	}

	public String getDescVelocAdmMlH() {
		return descVelocAdmMlH;
	}

	public void setDescVelocAdmMlH(String descVelocAdmMlH) {
		this.descVelocAdmMlH = descVelocAdmMlH;
	}

	public Integer getComposicaoSolNpt() {
		return composicaoSolNpt;
	}

	public void setComposicaoSolNpt(Integer composicaoSolNpt) {
		this.composicaoSolNpt = composicaoSolNpt;
	}

	public Integer getComposicaoSolLipidios() {
		return composicaoSolLipidios;
	}

	public void setComposicaoSolLipidios(Integer composicaoSolLipidios) {
		this.composicaoSolLipidios = composicaoSolLipidios;
	}

	public Double getParamTig() {
		return paramTig;
	}

	public void setParamTig(Double paramTig) {
		this.paramTig = paramTig;
	}

	public Double getParamAmin() {
		return paramAmin;
	}

	public void setParamAmin(Double paramAmin) {
		this.paramAmin = paramAmin;
	}

	public Double getParamLip() {
		return paramLip;
	}

	public void setParamLip(Double paramLip) {
		this.paramLip = paramLip;
	}

	public Double getParamPercGlic50() {
		return paramPercGlic50;
	}

	public void setParamPercGlic50(Double paramPercGlic50) {
		this.paramPercGlic50 = paramPercGlic50;
	}

	public Double getParamPercGlic10() {
		return paramPercGlic10;
	}

	public void setParamPercGlic10(Double paramPercGlic10) {
		this.paramPercGlic10 = paramPercGlic10;
	}

	public Double getParamSodio() {
		return paramSodio;
	}

	public void setParamSodio(Double paramSodio) {
		this.paramSodio = paramSodio;
	}

	public Double getParamPotassio() {
		return paramPotassio;
	}

	public void setParamPotassio(Double paramPotassio) {
		this.paramPotassio = paramPotassio;
	}

	public Double getParamPercKcl() {
		return paramPercKcl;
	}

	public void setParamPercKcl(Double paramPercKcl) {
		this.paramPercKcl = paramPercKcl;
	}

	public Double getParamPercK3po4() {
		return paramPercK3po4;
	}

	public void setParamPercK3po4(Double paramPercK3po4) {
		this.paramPercK3po4 = paramPercK3po4;
	}

	public Double getParamCalcio() {
		return paramCalcio;
	}

	public void setParamCalcio(Double paramCalcio) {
		this.paramCalcio = paramCalcio;
	}

	public Double getParamMagnesio() {
		return paramMagnesio;
	}

	public void setParamMagnesio(Double paramMagnesio) {
		this.paramMagnesio = paramMagnesio;
	}

	public Double getParamHeparina() {
		return paramHeparina;
	}

	public void setParamHeparina(Double paramHeparina) {
		this.paramHeparina = paramHeparina;
	}

	public Integer getParamAcetZn() {
		return paramAcetZn;
	}

	public void setParamAcetZn(Integer paramAcetZn) {
		this.paramAcetZn = paramAcetZn;
	}

	public Double getVolAa10() {
		return volAa10;
	}

	public void setVolAa10(Double volAa10) {
		this.volAa10 = volAa10;
	}

	public Double getVolGlicose50() {
		return volGlicose50;
	}

	public void setVolGlicose50(Double volGlicose50) {
		this.volGlicose50 = volGlicose50;
	}

	public Double getVolGlicose10() {
		return volGlicose10;
	}

	public void setVolGlicose10(Double volGlicose10) {
		this.volGlicose10 = volGlicose10;
	}

	public Double getVolNacl20() {
		return volNacl20;
	}

	public void setVolNacl20(Double volNacl20) {
		this.volNacl20 = volNacl20;
	}

	public Double getVolKcl() {
		return volKcl;
	}

	public void setVolKcl(Double volKcl) {
		this.volKcl = volKcl;
	}

	public Double getVolK3po4() {
		return volK3po4;
	}

	public void setVolK3po4(Double volK3po4) {
		this.volK3po4 = volK3po4;
	}

	public Double getVolMsgo4() {
		return volMsgo4;
	}

	public void setVolMsgo4(Double volMsgo4) {
		this.volMsgo4 = volMsgo4;
	}

	public Double getVolGlucoCa() {
		return volGlucoCa;
	}

	public void setVolGlucoCa(Double volGlucoCa) {
		this.volGlucoCa = volGlucoCa;
	}

	public Double getVolHeparina() {
		return volHeparina;
	}

	public void setVolHeparina(Double volHeparina) {
		this.volHeparina = volHeparina;
	}

	public Double getVolAcetZn() {
		return volAcetZn;
	}

	public void setVolAcetZn(Double volAcetZn) {
		this.volAcetZn = volAcetZn;
	}

	public Double getVolLipidios10() {
		return volLipidios10;
	}

	public void setVolLipidios10(Double volLipidios10) {
		this.volLipidios10 = volLipidios10;
	}

	public Double getVolLipidios20() {
		return volLipidios20;
	}

	public void setVolLipidios20(Double volLipidios20) {
		this.volLipidios20 = volLipidios20;
	}

	public Double getParamTempInfusaoLip() {
		return paramTempInfusaoLip;
	}

	public void setParamTempInfusaoLip(Double paramTempInfusaoLip) {
		this.paramTempInfusaoLip = paramTempInfusaoLip;
	}

	public Double getGotejoSolucao() {
		return gotejoSolucao;
	}

	public void setGotejoSolucao(Double gotejoSolucao) {
		this.gotejoSolucao = gotejoSolucao;
	}

	public Double getParamTempInfusaoSol() {
		return paramTempInfusaoSol;
	}

	public void setParamTempInfusaoSol(Double paramTempInfusaoSol) {
		this.paramTempInfusaoSol = paramTempInfusaoSol;
	}

	public Double getGotejoLipidios() {
		return gotejoLipidios;
	}

	public void setGotejoLipidios(Double gotejoLipidios) {
		this.gotejoLipidios = gotejoLipidios;
	}

	public DominioTipoLipidio getParamTipoLip() {
		return paramTipoLip;
	}

	public void setParamTipoLip(DominioTipoLipidio paramTipoLip) {
		this.paramTipoLip = paramTipoLip;
	}

	public DominioTipoVolume getTipoParamCalculoVol() {
		return tipoParamCalculoVol;
	}

	public void setTipoParamCalculoVol(DominioTipoVolume tipoParamCalculoVol) {
		this.tipoParamCalculoVol = tipoParamCalculoVol;
	}

	public Double getParamOligo() {
		return paramOligo;
	}

	public void setParamOligo(Double paramOligo) {
		this.paramOligo = paramOligo;
	}

	public Double getVolAaPed10() {
		return volAaPed10;
	}

	public void setVolAaPed10(Double volAaPed10) {
		this.volAaPed10 = volAaPed10;
	}

	public Boolean getMoveComponentesCalculados() {
		return moveComponentesCalculados;
	}

	public void setMoveComponentesCalculados(Boolean moveComponentesCalculados) {
		this.moveComponentesCalculados = moveComponentesCalculados;
	}

	public Double getParamPercGlic5() {
		return paramPercGlic5;
	}

	public void setParamPercGlic5(Double paramPercGlic5) {
		this.paramPercGlic5 = paramPercGlic5;
	}

	public Double getVolGlicose5() {
		return volGlicose5;
	}

	public void setVolGlicose5(Double volGlicose5) {
		this.volGlicose5 = volGlicose5;
	}

	public Double getVolOligo() {
		return volOligo;
	}

	public void setVolOligo(Double volOligo) {
		this.volOligo = volOligo;
	}

	public Double getVolOligoPed() {
		return volOligoPed;
	}

	public void setVolOligoPed(Double volOligoPed) {
		this.volOligoPed = volOligoPed;
	}

}
