package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import br.gov.mec.aghu.model.AfaItemNptPadraoId;
import br.gov.mec.aghu.model.RapServidores;

public class AfaItemNptPadraoVO {

	private static final String PATTERN_QTD_ITEM_NPT = "##############.####";

	private AfaItemNptPadraoId id;

	private Integer seqVMpmDosagem;
	
	private String seqUnidadeVMpmDosagem;

	private Integer medMatCodigoComponenteNpts;

	private String descricaoComponenteNpts;

	private Double qtdItemNpt;

	private RapServidores rapServidores;

	private Date criadoEm;

	private String criadoPor;

	private String unidadeMedica;

	private Integer fdsSeq;

	private Integer ummSeq;

	public enum Fields {

		AFA_ITEM_NPT_PADRAO_ID("id"), SEQ_VMPM_DOSAGEM("seqVMpmDosagem"), SEQ_UNIDADE_VMPM_DOSAGEM("seqUnidadeVMpmDosagem"), MED_MAT_CODIGO_COMPONENTE_NPTS("medMatCodigoComponenteNpts"), DESCRICAO_COMPONENTE_NPTS("descricaoComponenteNpts"), QTD_ITEM_NPT("qtdItemNpt"), AFA_ITEM_NPT_PADRAO_USUARIO(
				"rapServidores"), AFA_ITEM_NPT_PADRAO_CRIADO_EM("criadoEm"), AFA_ITEM_NPT_PADRAO_CRIADO_POR("criadoPor"), UNIDADE_MEDICA("unidadeMedica"), FDS_SEQ("fdsSeq"), UMM_SEQ("ummSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeqVMpmDosagem() {
		return seqVMpmDosagem;
	}
	
	public void setSeqVMpmDosagem(Integer seqVMpmDosagem) {
		this.seqVMpmDosagem = seqVMpmDosagem;
	}
	
	public String getSeqUnidadeVMpmDosagem() {
		return seqUnidadeVMpmDosagem;
	}
	
	public void setSeqUnidadeVMpmDosagem(String seqUnidadeVMpmDosagem) {
		this.seqUnidadeVMpmDosagem = seqUnidadeVMpmDosagem;
	}
	

	public Integer getMedMatCodigoComponenteNpts() {
		return medMatCodigoComponenteNpts;
	}

	public void setMedMatCodigoComponenteNpts(Integer medMatCodigoComponenteNpts) {
		this.medMatCodigoComponenteNpts = medMatCodigoComponenteNpts;
	}

	public String getDescricaoComponenteNpts() {
		return descricaoComponenteNpts;
	}

	public void setDescricaoComponenteNpts(String descricaoComponenteNpts) {
		this.descricaoComponenteNpts = descricaoComponenteNpts;
	}

	public Double getQtdItemNpt() {
		return qtdItemNpt;
	}

	public void setQtdItemNpt(Double qtdItemNpt) {
		this.qtdItemNpt = qtdItemNpt;
	}

	public AfaItemNptPadraoId getId() {
		return id;
	}

	public void setId(AfaItemNptPadraoId id) {
		this.id = id;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	public String getUnidadeMedica() {
		return unidadeMedica;
	}

	public void setUnidadeMedica(String unidadeMedica) {
		this.unidadeMedica = unidadeMedica;
	}

	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}

	public Integer getUmmSeq() {
		return ummSeq;
	}

	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}

	public String getQtdItemNptFormatada() {
		if (this.qtdItemNpt != null) {
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat df = new DecimalFormat(PATTERN_QTD_ITEM_NPT, dfSymbols);
			df.setRoundingMode(RoundingMode.DOWN);
			return df.format(qtdItemNpt);
		}
		return null;
	}
}
