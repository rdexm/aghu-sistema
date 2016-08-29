package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoUsoMdtoAntimicrobia;
import br.gov.mec.aghu.core.commons.BaseBean;

public class JustificativaMedicamentoUsoGeralVO implements BaseBean {

	private static final long serialVersionUID = 3335100256010481938L;
	
	private String descricao;
	private String medDescricao;
	private BigDecimal medConcentracao;
	private String ummDescricao;
	private String umm2Descricao;
	private String descricaoEdit;
	private String doseEdit;
	private String freqEdit;
	private BigDecimal dose;
	private String tfqSintaxe;
	private Short pmdFrequencia;
	private String tfqDescricao;
	private String vadSigla;
	private String vadDescricao;
	private Short duracaoTratSolicitado;
	private DominioTipoUsoMdtoAntimicrobia usoMdtoAntimicrobia;
	private String tprSigla;
	private Short gupSeq;
	private String indAntimicrobiano;
	private String indQuimioterapico;
	
	//Convenio
	private Short cspCnvCodigo;
	private Byte cspSeq;
	
	//MpmPrescricaoMdtoId
	private Integer pmdAtdSeq;
	private Long pmdSeq;
		
	//MpmItemPrescricaoMdtoId
	private Integer imePmdAtdSeq;
	private Long imePmdSeq;
	private Integer medMatCodigo;
	private Short imeSeqp;
	
	//MpmJustificativaUsoMdto
	private Integer jusSeq;
	
	//AfaGrupoUsoMedicamento
	private String gupDesc;
	
	private Short seqp;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public String getTfqSintaxe() {
		return tfqSintaxe;
	}

	public void setTfqSintaxe(String tfqSintaxe) {
		this.tfqSintaxe = tfqSintaxe;
	}

	public Short getPmdFrequencia() {
		return pmdFrequencia;
	}

	public void setPmdFrequencia(Short pmdFrequencia) {
		this.pmdFrequencia = pmdFrequencia;
	}

	public String getTfqDescricao() {
		return tfqDescricao;
	}

	public void setTfqDescricao(String tfqDescricao) {
		this.tfqDescricao = tfqDescricao;
	}

	public String getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	public String getVadDescricao() {
		return vadDescricao;
	}

	public void setVadDescricao(String vadDescricao) {
		this.vadDescricao = vadDescricao;
	}

	public Short getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}

	public void setDuracaoTratSolicitado(Short duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}

	public DominioTipoUsoMdtoAntimicrobia getUsoMdtoAntimicrobia() {
		return usoMdtoAntimicrobia;
	}

	public void setUsoMdtoAntimicrobia(
			DominioTipoUsoMdtoAntimicrobia usoMdtoAntimicrobia) {
		this.usoMdtoAntimicrobia = usoMdtoAntimicrobia;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	public Long getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}

	public Integer getImePmdAtdSeq() {
		return imePmdAtdSeq;
	}

	public void setImePmdAtdSeq(Integer imePmdAtdSeq) {
		this.imePmdAtdSeq = imePmdAtdSeq;
	}

	public Long getImePmdSeq() {
		return imePmdSeq;
	}

	public void setImePmdSeq(Long imePmdSeq) {
		this.imePmdSeq = imePmdSeq;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Short getImeSeqp() {
		return imeSeqp;
	}

	public void setImeSeqp(Short imeSeqp) {
		this.imeSeqp = imeSeqp;
	}

	public Integer getJusSeq() {
		return jusSeq;
	}

	public void setJusSeq(Integer jusSeq) {
		this.jusSeq = jusSeq;
	}
	
	public String getMedDescricao() {
		return medDescricao;
	}

	public void setMedDescricao(String medDescricao) {
		this.medDescricao = medDescricao;
	}

	public BigDecimal getMedConcentracao() {
		return medConcentracao;
	}

	public void setMedConcentracao(BigDecimal medConcentracao) {
		this.medConcentracao = medConcentracao;
	}

	public String getUmmDescricao() {
		return ummDescricao;
	}

	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}

	public String getDescricaoEdit() {
		return descricaoEdit;
	}

	public void setDescricaoEdit(String descricaoEdit) {
		this.descricaoEdit = descricaoEdit;
	}

	public String getDoseEdit() {
		return doseEdit;
	}

	public void setDoseEdit(String doseEdit) {
		this.doseEdit = doseEdit;
	}

	public String getFreqEdit() {
		return freqEdit;
	}

	public void setFreqEdit(String freqEdit) {
		this.freqEdit = freqEdit;
	}
	
	public String getUmm2Descricao() {
		return umm2Descricao;
	}

	public void setUmm2Descricao(String umm2Descricao) {
		this.umm2Descricao = umm2Descricao;
	}

	public String getTprSigla() {
		return tprSigla;
	}

	public void setTprSigla(String tprSigla) {
		this.tprSigla = tprSigla;
	}
		
	public Short getGupSeq() {
		return gupSeq;
	}

	public void setGupSeq(Short gupSeq) {
		this.gupSeq = gupSeq;
	}
	
	public String getGupDesc() {
		return gupDesc;
	}

	public void setGupDesc(String gupDesc) {
		this.gupDesc = gupDesc;
	}

	public String getIndAntimicrobiano() {
		return indAntimicrobiano;
	}
	
	public DominioSimNao getIndAntimicrobianoBoolean() {
		return "S".equals(indAntimicrobiano) ? DominioSimNao.S : DominioSimNao.N;
	}

	public void setIndAntimicrobiano(String indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	public String getIndQuimioterapico() {
		return indQuimioterapico;
	}
	
	public DominioSimNao getIndQuimioterapicoBoolean() {
		return "S".equals(indQuimioterapico) ? DominioSimNao.S : DominioSimNao.N;
	}

	public void setIndQuimioterapico(String indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public enum Fields {
		DESCRICAO("descricao"),
		DOSE("dose"),
		DURACAO_TRAT_SOLIC("duracaoTratSolicitado"),
		IME_PMD_ATD_SEQ("imePmdAtdSeq"),
		IME_PMD_SEQ("imePmdSeq"),
		IME_SEQP("imeSeqp"),
		JUS_SEQ("jusSeq"),
		MED_MAT_CODIGO("medMatCodigo"),
		PMD_ATD_SEQ("pmdAtdSeq"),
		PMD_FREQUENCIA("pmdFrequencia"),
		PMD_SEQ("pmdSeq"),
		TFQ_DESCRICAO("tfqDescricao"),
		TFQ_SINTAXE("tfqSintaxe"),
		USO_MDTO_ANTIMICROBIA("usoMdtoAntimicrobia"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		VAD_SIGLA("vadSigla"),
		MED_DESCRICAO("medDescricao"),
		MED_CONCENTRACAO("medConcentracao"),
		UMM_DESCRICAO("ummDescricao"),
		DESCRICAO_EDIT("descricaoEdit"),
		DOSE_EDIT("doseEdit"),
		FREQ_EDIT("freqEdit"),
		VAD_DESCRICAO("vadDescricao"),
		UMM2_DESCRICAO("umm2Descricao"),
		TPR_SIGLA("tprSigla"),
		GUP_SEQ("gupSeq"),
		GUP_DESC("gupDesc"),
		IND_ANTIMICROBIANO("indAntimicrobiano"),
		IND_QUIMIOTERAPICO("indQuimioterapico"),
		SEQP("seqp");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}