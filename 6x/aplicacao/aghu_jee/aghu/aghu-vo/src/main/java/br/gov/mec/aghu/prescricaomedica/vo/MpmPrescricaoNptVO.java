package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoLipidio;
import br.gov.mec.aghu.dominio.DominioTipoVolume;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.core.commons.BaseBean;
/**
 * #990 VO de Componentes - C5 - VO pai
 * 
 * @author paulo
 *
 */
public class MpmPrescricaoNptVO implements BaseBean {

	private static final long serialVersionUID = 652711289683989485L;

	// PNP.atd_seq,
	private Integer atdSeq;
	//        PNP.seq,
	private Integer seq;
	//        PNP.pnp_atd_seq,
	private Integer pnpAtdSeq;
	//        PNP.pnp_seq,
	private Integer pnpSeq;
	//        PNP.ser_matricula,
	private Integer matricula;
	//        PNP.ser_vin_codigo,
	private Short vinCodigo;
	//        PNP.ser_matricula_movimentada,
	private Integer matriculaMvto;
	//        PNP.ser_vin_codigo_movimentada,
	private Short vinCodigoMvto;
	//        PNP.fnp_seq,
	private Short fnpSeq;
	//        fnp.DESCRICAO AS FORMULA_DESCRICAO,
	private String descricaoFormula;
	//        PNP.ped_seq,
	private Short pedSeq;
	//        PNP.dthr_inicio,
	private Date dthrInicio;
	//        PNP.ind_segue_gotejo_padrao,
	private Boolean segueGotejoPadrao;
	//        PNP.ind_pendente,
	private DominioIndPendenteItemPrescricao indPendente;
	//        PNP.criado_em,
	private Date criadoEm;
	//        PNP.justificativa,
	private String justificativa;
	//        PNP.dthr_fim,
	private Date dthrFim;
	//        PNP.observacao,
	private String observacao;
	//        PNP.volume_ml_kg_dia,
	private Short volumeMlKgDia;
	//        PNP.alterado_em,
	private Date alteradoEm;
	//        PNP.ser_matricula_mvto_valida,
	private Integer matriculaMvtoValida;
	//        PNP.ser_vin_codigo_mvto_valida,
	private Short vinCodigoMvtoValida;
	//        PNP.ser_matricula_valida,
	private Integer matriculaValida;
	//        PNP.ser_vin_codigo_valida,
	private Short vinCodigoValida;
	//        PNP.jnp_seq,
	private Short jnpSeq;
	//        PNP.param_volume_ml,
	private Double paramVolumeMl;
	//        PNP.tipo_param_volume,
	private DominioTipoVolume tipoParamVolume;
	//        PNP.volume_calculado,
	private Double volumeCalculado;
	//        PNP.volume_desejado,
	private Double volumeDesejado;
	//        PNP.tempo_h_infusao_solucao,
	private Short tempoHInfusaoSolucao;
	//        PNP.tempo_h_infusao_lipidios,
	private Short tempoHInfusaoLipidios;
	//        PNP.calorias_dia,
	private Double caloriasDia;
	//        PNP.calorias_kg_dia,
	private Double caloriasKgDia;
	//        PNP.rel_cal_n_prot_nitrogenio,
	private Double relCalNProtNitrogenio;
	//        PNP.perc_cal_aminoacidos,
	private Double percCalAminoacidos;
	//        PNP.perc_cal_lipidios,
	private Double percCalLipidios;
	//        PNP.perc_cal_glicose,
	private Double percCalGlicose;
	//        PNP.glicose_rel_glic_lipid,
	private Double glicoseRelGlicLipid;
	//        PNP.lipidios_rel_glic_lipid,
	private Double lipidiosRelGlicLipid;
	//        PNP.relacao_calcio_fosforo,
	private Double relacaoCalcioFosforo;
	//        PNP.conc_glic_sem_lipidios,
	private Double concGlicSemLipidios;
	//        PNP.conc_glic_com_lipidios,
	private Double concGlicComLipidios;
	//        PNP.taxa_infusao_lipidios,
	private Double taxaInfusaoLipidios;
	//        PNP.osmolaridade_sem_lipidios,
	private Double osmolaridadeSemLipidios;
	//        PNP.osmolaridade_com_lipidios,
	private Double osmolaridadeComLipidios;
	//        PNP.pca_atd_seq,
	private Integer pcaAtdSeq;
	//        PNP.pca_criado_em,
	private Date pcaCriadoEm;
	//        PNP.param_tipo_lipidio,
	private DominioTipoLipidio paramTipoLipidio;
	//        PNP.duracao_trat_solicitado,
	private Short duracaoTratSolicitado;
	//        PNP.ind_bomba_infusao,
	private Boolean bombaInfusao;
	//        PNP.pme_atd_seq,
	private Integer pmeAtdSeq;
	//        PNP.pme_seq,
	private Integer pmeSeq;
	//        PNP.dthr_valida,
	private Date dthrValida;
	//        PNP.dthr_valida_mvto,
	private Date dthrValidaMovimentacao;
	
	//        PNP.pnp_atd_seq_prcr_ant,
	private Integer pnpAtdSeqPrcrAnt;
	
	//        PNP.pnp_seq_prcr_ant
	private Integer pnpSeqPrcrAnt;
	
	private Boolean indPacPediatrico;
	
	private MpmPrescricaoMedica prescricaoMedica;
	
	private List<MpmComposicaoPrescricaoNptVO> composicoes = new ArrayList<MpmComposicaoPrescricaoNptVO>();
	
	 
	
	
	public enum Fields {
		ATD_SEQ("atdSeq"),
		SEQ("seq"), 
		PNP_ATD_SEQ("pnpAtdSeq"),
		PNP_SEQ("pnpSeq"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		MATRICULA_MVTO("matriculaMvto"),
		VIN_CODIGO_MVTO("vinCodigoMvto"),
		FNP_SEQ("fnpSeq"),
		DESCRICAO_FORMULA("descricaoFormula"),
		PED_SEQ("pedSeq"),
		DTHR_INICIO("dthrInicio"),
		IND_SEGUE_GOTEJO_PADRAO("segueGotejoPadrao"),
		IND_PENDENTE("indPendente"),
		CRIADO_EM("criadoEm"),
		JUSTIFICATIVA("justificativa"),
		DTHR_FIM("dthrFim"),
		OBSERVACAO("observacao"),
		VOLUME_ML_KG_DIA("volumeMlKgDia"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_MVTO_VALIDA("matriculaMvtoValida"),		
		SER_VIN_CODIGO_MVTO_VALIDA("vinCodigoMvtoValida"),
		SER_MATRICULA_VALIDA("matriculaValida"),
		SER_VIN_CODIGO_VALIDA("vinCodigoValida"),
		JNP_SEQ("jnpSeq"),
		PARAM_VOLUME_ML("paramVolumeMl"),
		TIPO_PARAM_VOLUME("tipoParamVolume"),
		VOLUME_CALCULADO("volumeCalculado"),
		VOLUME_DESEJADO("volumeDesejado"),
		TEMPO_H_INFUSAO_SOLUCAO("tempoHInfusaoSolucao"),
		TEMPO_H_INFUSAO_LIPIDIOS("tempoHInfusaoLipidios"),
		CALORIAS_DIA("caloriasDia"),
		CALORIAS_KG_DIA("caloriasKgDia"),
		REL_CAL_N_PROT_NITROGENIO("relCalNProtNitrogenio"),
		PERC_CAL_AMINOACIDOS("percCalAminoacidos"),
		PERC_CAL_LIPIDIOS("percCalLipidios"),
		PERC_CAL_GLICOSE("percCalGlicose"),
		GLICOSE_REL_GLIC_LIPID("glicoseRelGlicLipid"),
		LIPIDIOS_REL_GLIC_LIPID("lipidiosRelGlicLipid"),
		RELACAO_CALCIO_FOSFORO("relacaoCalcioFosforo"),
		CONC_GLIC_SEM_LIPIDIOS("concGlicSemLipidios"),
		CONC_GLIC_COM_LIPIDIOS("concGlicComLipidios"),
		TAXA_INFUSAO_LIPIDIOS("taxaInfusaoLipidios"),
		OSMOLARIDADE_SEM_LIPIDIOS("osmolaridadeSemLipidios"),
		OSMOLARIDADE_COM_LIPIDIOS("osmolaridadeComLipidios"),
		PCA_ATD_SEQ("pcaAtdSeq"),
		PCA_CRIADO_EM("pcaCriadoEm"),
		PARAM_TIPO_LIPIDIO("paramTipoLipidio"),
		DURACAO_TRAT_SOLICITADO("duracaoTratSolicitado"),
		IND_BOMBA_INFUSAO("bombaInfusao"),
		PME_ATD_SEQ("pmeAtdSeq"),
		PME_SEQ("pmeSeq"),
		DTHR_VALIDA("dthrValida"),
		DTHR_VALIDA_MVTO("dthrValidaMovimentacao"),
		PNP_ATD_SEQ_PRCR_ANT("pnpAtdSeqPrcrAnt"),
		PNP_SEQ_PRCR_ANT("pnpSeqPrcrAnt")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getPnpAtdSeq() {
		return pnpAtdSeq;
	}
	public void setPnpAtdSeq(Integer pnpAtdSeq) {
		this.pnpAtdSeq = pnpAtdSeq;
	}
	public Integer getPnpSeq() {
		return pnpSeq;
	}
	public void setPnpSeq(Integer pnpSeq) {
		this.pnpSeq = pnpSeq;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public Integer getMatriculaMvto() {
		return matriculaMvto;
	}
	public void setMatriculaMvto(Integer matriculaMvto) {
		this.matriculaMvto = matriculaMvto;
	}
	public Short getVinCodigoMvto() {
		return vinCodigoMvto;
	}
	public void setVinCodigoMvto(Short vinCodigoMvto) {
		this.vinCodigoMvto = vinCodigoMvto;
	}
	public Short getFnpSeq() {
		return fnpSeq;
	}
	public void setFnpSeq(Short fnpSeq) {
		this.fnpSeq = fnpSeq;
	}
	public String getDescricaoFormula() {
		return descricaoFormula;
	}
	public void setDescricaoFormula(String descricaoFormula) {
		this.descricaoFormula = descricaoFormula;
	}
	public Short getPedSeq() {
		return pedSeq;
	}
	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public Boolean getSegueGotejoPadrao() {
		return segueGotejoPadrao;
	}
	public void setSegueGotejoPadrao(Boolean segueGotejoPadrao) {
		this.segueGotejoPadrao = segueGotejoPadrao;
	}
	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}
	public void setIndPendente(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public Date getDthrFim() {
		return dthrFim;
	}
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Short getVolumeMlKgDia() {
		return volumeMlKgDia;
	}
	public void setVolumeMlKgDia(Short volumeMlKgDia) {
		this.volumeMlKgDia = volumeMlKgDia;
	}
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	public Integer getMatriculaMvtoValida() {
		return matriculaMvtoValida;
	}
	public void setMatriculaMvtoValida(Integer matriculaMvtoValida) {
		this.matriculaMvtoValida = matriculaMvtoValida;
	}
	public Short getVinCodigoMvtoValida() {
		return vinCodigoMvtoValida;
	}
	public void setVinCodigoMvtoValida(Short vinCodigoMvtoValida) {
		this.vinCodigoMvtoValida = vinCodigoMvtoValida;
	}
	public Integer getMatriculaValida() {
		return matriculaValida;
	}
	public void setMatriculaValida(Integer matriculaValida) {
		this.matriculaValida = matriculaValida;
	}
	public Short getVinCodigoValida() {
		return vinCodigoValida;
	}
	public void setVinCodigoValida(Short vinCodigoValida) {
		this.vinCodigoValida = vinCodigoValida;
	}
	public Short getJnpSeq() {
		return jnpSeq;
	}
	public void setJnpSeq(Short jnpSeq) {
		this.jnpSeq = jnpSeq;
	}
	public Double getParamVolumeMl() {
		return paramVolumeMl;
	}
	public void setParamVolumeMl(Double paramVolumeMl) {
		this.paramVolumeMl = paramVolumeMl;
	}
	public DominioTipoVolume getTipoParamVolume() {
		return tipoParamVolume;
	}
	public void setTipoParamVolume(DominioTipoVolume tipoParamVolume) {
		this.tipoParamVolume = tipoParamVolume;
	}
	public Double getVolumeCalculado() {
		return volumeCalculado;
	}
	public void setVolumeCalculado(Double volumeCalculado) {
		this.volumeCalculado = volumeCalculado;
	}
	public Double getVolumeDesejado() {
		return volumeDesejado;
	}
	public void setVolumeDesejado(Double volumeDesejado) {
		this.volumeDesejado = volumeDesejado;
	}
	public Short getTempoHInfusaoSolucao() {
		return tempoHInfusaoSolucao;
	}
	public void setTempoHInfusaoSolucao(Short tempoHInfusaoSolucao) {
		this.tempoHInfusaoSolucao = tempoHInfusaoSolucao;
	}
	public Short getTempoHInfusaoLipidios() {
		return tempoHInfusaoLipidios;
	}
	public void setTempoHInfusaoLipidios(Short tempoHInfusaoLipidios) {
		this.tempoHInfusaoLipidios = tempoHInfusaoLipidios;
	}
	public Double getCaloriasDia() {
		return caloriasDia;
	}
	public void setCaloriasDia(Double caloriasDia) {
		this.caloriasDia = caloriasDia;
	}
	public Double getCaloriasKgDia() {
		return caloriasKgDia;
	}
	public void setCaloriasKgDia(Double caloriasKgDia) {
		this.caloriasKgDia = caloriasKgDia;
	}
	public Double getRelCalNProtNitrogenio() {
		return relCalNProtNitrogenio;
	}
	public void setRelCalNProtNitrogenio(Double relCalNProtNitrogenio) {
		this.relCalNProtNitrogenio = relCalNProtNitrogenio;
	}
	public Double getPercCalAminoacidos() {
		return percCalAminoacidos;
	}
	public void setPercCalAminoacidos(Double percCalAminoacidos) {
		this.percCalAminoacidos = percCalAminoacidos;
	}
	public Double getPercCalLipidios() {
		return percCalLipidios;
	}
	public void setPercCalLipidios(Double percCalLipidios) {
		this.percCalLipidios = percCalLipidios;
	}
	public Double getPercCalGlicose() {
		return percCalGlicose;
	}
	public void setPercCalGlicose(Double percCalGlicose) {
		this.percCalGlicose = percCalGlicose;
	}
	public Double getGlicoseRelGlicLipid() {
		return glicoseRelGlicLipid;
	}
	public void setGlicoseRelGlicLipid(Double glicoseRelGlicLipid) {
		this.glicoseRelGlicLipid = glicoseRelGlicLipid;
	}
	public Double getLipidiosRelGlicLipid() {
		return lipidiosRelGlicLipid;
	}
	public void setLipidiosRelGlicLipid(Double lipidiosRelGlicLipid) {
		this.lipidiosRelGlicLipid = lipidiosRelGlicLipid;
	}
	public Double getRelacaoCalcioFosforo() {
		return relacaoCalcioFosforo;
	}
	public void setRelacaoCalcioFosforo(Double relacaoCalcioFosforo) {
		this.relacaoCalcioFosforo = relacaoCalcioFosforo;
	}
	public Double getConcGlicSemLipidios() {
		return concGlicSemLipidios;
	}
	public void setConcGlicSemLipidios(Double concGlicSemLipidios) {
		this.concGlicSemLipidios = concGlicSemLipidios;
	}
	public Double getConcGlicComLipidios() {
		return concGlicComLipidios;
	}
	public void setConcGlicComLipidios(Double concGlicComLipidios) {
		this.concGlicComLipidios = concGlicComLipidios;
	}
	public Double getTaxaInfusaoLipidios() {
		return taxaInfusaoLipidios;
	}
	public void setTaxaInfusaoLipidios(Double taxaInfusaoLipidios) {
		this.taxaInfusaoLipidios = taxaInfusaoLipidios;
	}
	public Double getOsmolaridadeSemLipidios() {
		return osmolaridadeSemLipidios;
	}
	public void setOsmolaridadeSemLipidios(Double osmolaridadeSemLipidios) {
		this.osmolaridadeSemLipidios = osmolaridadeSemLipidios;
	}
	public Double getOsmolaridadeComLipidios() {
		return osmolaridadeComLipidios;
	}
	public void setOsmolaridadeComLipidios(Double osmolaridadeComLipidios) {
		this.osmolaridadeComLipidios = osmolaridadeComLipidios;
	}
	public Integer getPcaAtdSeq() {
		return pcaAtdSeq;
	}
	public void setPcaAtdSeq(Integer pcaAtdSeq) {
		this.pcaAtdSeq = pcaAtdSeq;
	}
	public Date getPcaCriadoEm() {
		return pcaCriadoEm;
	}
	public void setPcaCriadoEm(Date pcaCriadoEm) {
		this.pcaCriadoEm = pcaCriadoEm;
	}
	public DominioTipoLipidio getParamTipoLipidio() {
		return paramTipoLipidio;
	}
	public void setParamTipoLipidio(DominioTipoLipidio paramTipoLipidio) {
		this.paramTipoLipidio = paramTipoLipidio;
	}
	public Short getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}
	public void setDuracaoTratSolicitado(Short duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}
	public Boolean getBombaInfusao() {
		return bombaInfusao;
	}
	public void setBombaInfusao(Boolean bombaInfusao) {
		this.bombaInfusao = bombaInfusao;
	}
	public Integer getPmeAtdSeq() {
		return pmeAtdSeq;
	}
	public void setPmeAtdSeq(Integer pmeAtdSeq) {
		this.pmeAtdSeq = pmeAtdSeq;
	}
	public Integer getPmeSeq() {
		return pmeSeq;
	}
	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}
	public Date getDthrValida() {
		return dthrValida;
	}
	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}
	public Date getDthrValidaMovimentacao() {
		return dthrValidaMovimentacao;
	}
	public void setDthrValidaMovimentacao(Date dthrValidaMovimentacao) {
		this.dthrValidaMovimentacao = dthrValidaMovimentacao;
	}
	public List<MpmComposicaoPrescricaoNptVO> getComposicoes() {
		return composicoes;
	}
	public void setComposicoes(List<MpmComposicaoPrescricaoNptVO> composicoes) {
		this.composicoes = composicoes;
	}
	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}
	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}
	public Integer getPnpAtdSeqPrcrAnt() {
		return pnpAtdSeqPrcrAnt;
	}
	public void setPnpAtdSeqPrcrAnt(Integer pnpAtdSeqPrcrAnt) {
		this.pnpAtdSeqPrcrAnt = pnpAtdSeqPrcrAnt;
	}
	public Integer getPnpSeqPrcrAnt() {
		return pnpSeqPrcrAnt;
	}
	public void setPnpSeqPrcrAnt(Integer pnpSeqPrcrAnt) {
		this.pnpSeqPrcrAnt = pnpSeqPrcrAnt;
	}	
	
	public Boolean getIndPacPediatrico() {
		return indPacPediatrico;
	}
	
	public void setIndPacPediatrico(Boolean indPacPediatrico) {
		this.indPacPediatrico = indPacPediatrico;
	}

}
