package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MedicamentoRecemNascidoVO implements BaseBean {

	private static final long serialVersionUID = -6027033817613081069L;
	private String descricaoPni;
	private String descricaoMed;
	private Integer medMatCodigo;
	private DominioSituacao indSituacao;
	private String codigoCsa;
	private Integer seqPni;
	private String descricaoCsa;
	private Integer serMatriculaPni;
	private Short serVinCodigoPni;
	private Integer dose;
	private String unidade;
	private String vadSigla;

	// informacoes recem nascido
	private Integer rnaGsoPacCodigo;
	private Short rnaGsoSeqp;
	private Byte rnaSeqp;

	public MedicamentoRecemNascidoVO(String descricaoPni, String descricaoMed,
			DominioSituacao indSituacao, String codigoCsa, Integer seqPni,
			String descricaoCsa, Integer serMatriculaPni,
			Short serVinCodigoPni, String descricao, Integer dose,
			String unidade, String vadSigla, Integer medMatCodigo) {
		super();
		this.descricaoPni = descricaoPni;
		this.descricaoMed = descricaoMed;
		this.indSituacao = indSituacao;
		this.codigoCsa = codigoCsa;
		this.seqPni = seqPni;
		this.descricaoCsa = descricaoCsa;
		this.serMatriculaPni = serMatriculaPni;
		this.serVinCodigoPni = serVinCodigoPni;
		this.dose = dose;
		this.unidade = unidade;
		this.vadSigla = vadSigla;
		this.medMatCodigo = medMatCodigo;
	}

	public MedicamentoRecemNascidoVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getDose() {
		return dose;
	}

	public void setDose(Integer dose) {
		this.dose = dose;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	public String getDescricaoPni() {
		return descricaoPni;
	}

	public void setDescricaoPni(String descricaoPni) {
		this.descricaoPni = descricaoPni;
	}

	public String getDescricaoMed() {
		return descricaoMed;
	}

	public void setDescricaoMed(String descricaoMed) {
		this.descricaoMed = descricaoMed;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getCodigoCsa() {
		return codigoCsa;
	}

	public void setCodigoCsa(String codigoCsa) {
		this.codigoCsa = codigoCsa;
	}

	public Integer getSeqPni() {
		return seqPni;
	}

	public void setSeqPni(Integer seqPni) {
		this.seqPni = seqPni;
	}

	public String getDescricaoCsa() {
		return descricaoCsa;
	}

	public void setDescricaoCsa(String descricaoCsa) {
		this.descricaoCsa = descricaoCsa;
	}

	public Integer getSerMatriculaPni() {
		return serMatriculaPni;
	}

	public void setSerMatriculaPni(Integer serMatriculaPni) {
		this.serMatriculaPni = serMatriculaPni;
	}

	public Short getSerVinCodigoPni() {
		return serVinCodigoPni;
	}

	public void setSerVinCodigoPni(Short serVinCodigoPni) {
		this.serVinCodigoPni = serVinCodigoPni;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Integer getRnaGsoPacCodigo() {
		return rnaGsoPacCodigo;
	}

	public void setRnaGsoPacCodigo(Integer rnaGsoPacCodigo) {
		this.rnaGsoPacCodigo = rnaGsoPacCodigo;
	}

	public Short getRnaGsoSeqp() {
		return rnaGsoSeqp;
	}

	public void setRnaGsoSeqp(Short rnaGsoSeqp) {
		this.rnaGsoSeqp = rnaGsoSeqp;
	}

	public Byte getRnaSeqp() {
		return rnaSeqp;
	}

	public void setRnaSeqp(Byte rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}

	public enum Fields {
		DESCRICAO_PNI("descricaoPni"), DESCRICAO_MED("descricaoMed"), IND_SITUACAO(
				"indSituacao"), CODIGO_CSA("codigoCsa"), SEQ_PNI("seqPni"), DESCRICAO_CSA(
				"descricaoCsa"), SER_MATRICULA_PNI("serMatriculaPni"), SER_VIN_CODIGO_PNI(
				"serVinCodigoPni"), DOSE("dose"), UNIDADE("unidade"), VAD_SIGLA(
				"vadSigla"), MED_MAT_CODIGO("medMatCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rnaGsoPacCodigo == null) ? 0 : rnaGsoPacCodigo.hashCode());
		result = prime * result
				+ ((rnaGsoSeqp == null) ? 0 : rnaGsoSeqp.hashCode());
		result = prime * result + ((rnaSeqp == null) ? 0 : rnaSeqp.hashCode());
		result = prime * result + ((seqPni == null) ? 0 : seqPni.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return validarParametro(obj);
	}

	private boolean validarParametro(Object obj) {		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return validarCampos(obj);
	}

	private boolean validarCampos(Object obj) {
		MedicamentoRecemNascidoVO other = (MedicamentoRecemNascidoVO) obj;
		if (rnaGsoPacCodigo == null) {
			if (other.rnaGsoPacCodigo != null) {
				return false;
			}
		} else if (!rnaGsoPacCodigo.equals(other.rnaGsoPacCodigo)) {
			return false;
		}
		if (rnaGsoSeqp == null) {
			if (other.rnaGsoSeqp != null) {
				return false;
			}
		} else if (!rnaGsoSeqp.equals(other.rnaGsoSeqp)) {
			return false;
		}
		if (rnaSeqp == null) {
			if (other.rnaSeqp != null) {
				return false;
			}
		} else if (!rnaSeqp.equals(other.rnaSeqp)) {
			return false;
		}
		if (seqPni == null) {
			if (other.seqPni != null) {
				return false;
			}
		} else if (!seqPni.equals(other.seqPni)) {
			return false;
		}
		return true;
	}
}
