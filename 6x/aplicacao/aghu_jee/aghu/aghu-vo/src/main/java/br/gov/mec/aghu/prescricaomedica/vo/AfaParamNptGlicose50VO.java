package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoCaloria;
import br.gov.mec.aghu.dominio.DominioTipoVolume;

/**
 * #989 - Calcular Nutrição Parenteral Total: VO de MPMK_VARIAVEIS vide mpmp_carga_calculo_npt (VÁRIOS ITENS)
 * 
 * @author aghu
 *
 */
public class AfaParamNptGlicose50VO implements Serializable {

	private static final long serialVersionUID = -5951332325818991061L;

	// LEGENDAS:
	// CNP = AFA_COMPONENTE_NPTS
	// PCN = AFA_PARAM_COMPONENTE_NPTS
	// IPN = MPM_ITEM_PRESCRICAO_NPTS

	// GLICOSE_50
	private Integer ummSeq; // PCN.UMM_SEQ
	private String ummDescricao; // MPM_UNIDADE_MEDIDA_MEDICAS.DESCRICAO
	private Double convMl; // PCN.FATOR_CONV_ML_MOSM
	private Double convCalorias; // PCN.FATOR_CONV_UNID_CALORIAS
	private Double convMlMosm; // PCN.FATOR_CONV_ML_MOSM
	private Double convUnidNitrogenio; // PCN.FATOR_CONV_ML_FOSFORO
	private Double convMlFosforo; // PCN.FATOR_CONV_ML_FOSFORO
	private DominioTipoVolume tipoParamCalculo; // PNP.TIPO_PARAM_VOLUME
	private Double volMaximo; // PCN.VOLUME_MAXIMO_ML
	private DominioTipoCaloria tipoCaloria; // PCN.TIPO_CALORIA
	private Integer nroCasasDecimais; // get_casas_decimais(FET_COMP.MED_MAT_CODIGO)
	private Integer pcnSeqp; // PCN.SEQP
	private DominioTipoVolume tpParamCalcUsado; // IPN.TIPO_PARAM_CALCULO
	//--
	private Integer tigUmmSeq; // PCN.UMM_SEQ
	private String tigUmmDescricao; // MPM_UNIDADE_MEDIDA_MEDICAS.DESCRICAO
	private Double tigConvMl; // PCN.FATOR_CONV_ML_MOSM
	private Double tigConvCalorias; // PCN.FATOR_CONV_UNID_CALORIAS
	private Double tigConvMlMosm; // PCN.FATOR_CONV_ML_MOSM
	private Double tigConvUnidNitrogenio; // PCN.FATOR_CONV_ML_FOSFORO
	private Double tigConvMlFosforo; // PCN.FATOR_CONV_ML_FOSFORO
	private DominioTipoVolume tigTipoParamCalculo; // PNP.TIPO_PARAM_VOLUME
	private Double tigVolMaximo; // PCN.VOLUME_MAXIMO_ML
	private DominioTipoCaloria tigTipoCaloria; // PCN.TIPO_CALORIA
	private Integer tigNroCasasDecimais; // get_casas_decimais(FET_COMP.MED_MAT_CODIGO)
	private Integer tigPcnSeqp; // PCN.SEQP
	private DominioTipoVolume tigTpParamCalcUsado; // IPN.TIPO_PARAM_CALCULO
	
	public Integer getUmmSeq() {
		return ummSeq;
	}
	
	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}

	public String getUmmDescricao() {
		return ummDescricao;
	}

	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}

	public Double getConvMl() {
		return convMl;
	}

	public void setConvMl(Double convMl) {
		this.convMl = convMl;
	}

	public Double getConvCalorias() {
		return convCalorias;
	}

	public void setConvCalorias(Double convCalorias) {
		this.convCalorias = convCalorias;
	}

	public Double getConvMlMosm() {
		return convMlMosm;
	}

	public void setConvMlMosm(Double convMlMosm) {
		this.convMlMosm = convMlMosm;
	}

	public Double getConvUnidNitrogenio() {
		return convUnidNitrogenio;
	}

	public void setConvUnidNitrogenio(Double convUnidNitrogenio) {
		this.convUnidNitrogenio = convUnidNitrogenio;
	}

	public Double getConvMlFosforo() {
		return convMlFosforo;
	}

	public void setConvMlFosforo(Double convMlFosforo) {
		this.convMlFosforo = convMlFosforo;
	}

	public DominioTipoVolume getTipoParamCalculo() {
		return tipoParamCalculo;
	}

	public void setTipoParamCalculo(DominioTipoVolume tipoParamCalculo) {
		this.tipoParamCalculo = tipoParamCalculo;
	}

	public Double getVolMaximo() {
		return volMaximo;
	}

	public void setVolMaximo(Double volMaximo) {
		this.volMaximo = volMaximo;
	}

	public DominioTipoCaloria getTipoCaloria() {
		return tipoCaloria;
	}

	public void setTipoCaloria(DominioTipoCaloria tipoCaloria) {
		this.tipoCaloria = tipoCaloria;
	}

	public Integer getNroCasasDecimais() {
		return nroCasasDecimais;
	}

	public void setNroCasasDecimais(Integer nroCasasDecimais) {
		this.nroCasasDecimais = nroCasasDecimais;
	}

	public Integer getPcnSeqp() {
		return pcnSeqp;
	}

	public void setPcnSeqp(Integer pcnSeqp) {
		this.pcnSeqp = pcnSeqp;
	}

	public DominioTipoVolume getTpParamCalcUsado() {
		return tpParamCalcUsado;
	}

	public void setTpParamCalcUsado(DominioTipoVolume tpParamCalcUsado) {
		this.tpParamCalcUsado = tpParamCalcUsado;
	}

	public Integer getTigUmmSeq() {
		return tigUmmSeq;
	}

	public void setTigUmmSeq(Integer tigUmmSeq) {
		this.tigUmmSeq = tigUmmSeq;
	}

	public String getTigUmmDescricao() {
		return tigUmmDescricao;
	}

	public void setTigUmmDescricao(String tigUmmDescricao) {
		this.tigUmmDescricao = tigUmmDescricao;
	}

	public Double getTigConvMl() {
		return tigConvMl;
	}

	public void setTigConvMl(Double tigConvMl) {
		this.tigConvMl = tigConvMl;
	}

	public Double getTigConvCalorias() {
		return tigConvCalorias;
	}

	public void setTigConvCalorias(Double tigConvCalorias) {
		this.tigConvCalorias = tigConvCalorias;
	}

	public Double getTigConvMlMosm() {
		return tigConvMlMosm;
	}

	public void setTigConvMlMosm(Double tigConvMlMosm) {
		this.tigConvMlMosm = tigConvMlMosm;
	}

	public Double getTigConvUnidNitrogenio() {
		return tigConvUnidNitrogenio;
	}

	public void setTigConvUnidNitrogenio(Double tigConvUnidNitrogenio) {
		this.tigConvUnidNitrogenio = tigConvUnidNitrogenio;
	}

	public Double getTigConvMlFosforo() {
		return tigConvMlFosforo;
	}

	public void setTigConvMlFosforo(Double tigConvMlFosforo) {
		this.tigConvMlFosforo = tigConvMlFosforo;
	}

	public DominioTipoVolume getTigTipoParamCalculo() {
		return tigTipoParamCalculo;
	}

	public void setTigTipoParamCalculo(DominioTipoVolume tigTipoParamCalculo) {
		this.tigTipoParamCalculo = tigTipoParamCalculo;
	}

	public Double getTigVolMaximo() {
		return tigVolMaximo;
	}

	public void setTigVolMaximo(Double tigVolMaximo) {
		this.tigVolMaximo = tigVolMaximo;
	}

	public DominioTipoCaloria getTigTipoCaloria() {
		return tigTipoCaloria;
	}

	public void setTigTipoCaloria(DominioTipoCaloria tigTipoCaloria) {
		this.tigTipoCaloria = tigTipoCaloria;
	}

	public Integer getTigNroCasasDecimais() {
		return tigNroCasasDecimais;
	}

	public void setTigNroCasasDecimais(Integer tigNroCasasDecimais) {
		this.tigNroCasasDecimais = tigNroCasasDecimais;
	}

	public Integer getTigPcnSeqp() {
		return tigPcnSeqp;
	}

	public void setTigPcnSeqp(Integer tigPcnSeqp) {
		this.tigPcnSeqp = tigPcnSeqp;
	}

	public DominioTipoVolume getTigTpParamCalcUsado() {
		return tigTpParamCalcUsado;
	}

	public void setTigTpParamCalcUsado(DominioTipoVolume tigTpParamCalcUsado) {
		this.tigTpParamCalcUsado = tigTpParamCalcUsado;
	}
}