package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_TMP_FICHA_FARMACOS", schema = "AGH")
public class MbcTmpFichaFarmaco extends BaseEntityId<MbcTmpFichaFarmacoId> implements java.io.Serializable {

	private static final long serialVersionUID = -2922992443117068418L;
	private MbcTmpFichaFarmacoId id;
	private Integer version;
	private Integer sessao;
	private Integer ficSeq;
	private Integer ffaSeq;
	private String descMedicamento;
	private String doseTotal;
	private Date dthrOcorrencia;
	private String celula;
	private Integer ordem;
	private Short tempoDecorrido;
	private Date criadoEm;
	private String idSessao;
	//

	public MbcTmpFichaFarmaco() {
	}

	public MbcTmpFichaFarmaco(MbcTmpFichaFarmacoId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "sessao", column = @Column(name = "SESSAO")),
			@AttributeOverride(name = "ficSeq", column = @Column(name = "FIC_SEQ")),
			@AttributeOverride(name = "ffaSeq", column = @Column(name = "FFA_SEQ")),
			@AttributeOverride(name = "descMedicamento", column = @Column(name = "DESC_MEDICAMENTO", length = 200)),
			@AttributeOverride(name = "doseTotal", column = @Column(name = "DOSE_TOTAL", length = 100)),
			@AttributeOverride(name = "dthrOcorrencia", column = @Column(name = "DTHR_OCORRENCIA", length = 29)),
			@AttributeOverride(name = "celula", column = @Column(name = "CELULA", length = 20)),
			@AttributeOverride(name = "ordem", column = @Column(name = "ORDEM")),
			@AttributeOverride(name = "tempoDecorrido", column = @Column(name = "TEMPO_DECORRIDO")),
			@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", length = 29)),
			@AttributeOverride(name = "idSessao", column = @Column(name = "ID_SESSAO", length = 29))})
	public MbcTmpFichaFarmacoId getId() {
		return this.id;
	}

	public void setId(MbcTmpFichaFarmacoId id) {
		this.id = id;
	}

	public enum Fields {

		ID_IDSESSAO("id.idSessao"),
		ID_SESSAO("id.sessao"),
		ID_FIC_SEQ("id.ficSeq"),
		ID_FFA_SEQ("id.ffaSeq"),
		ID_DTHR_OCORRENCIA("id.dthrOcorrencia"),
		ID_CRIADO_EM("id.criadoEm"),
		ID_ORDEM("id.ordem"),
		ID_DESC_MEDICAMENTO("id.descMedicamento"),
		ID_DOSE_TOTAL("id.doseTotal"),
		ID_CELULA("id.celula"),
		ID_TEMPO_DECORRIDO("id.tempoDecorrido"),

		IDSESSAO("idSessao"),
		SESSAO("sessao"),
		FIC_SEQ("ficSeq"),
		FFA_SEQ("ffaSeq"),
		DTHR_OCORRENCIA("dthrOcorrencia"),
		CRIADO_EM("criadoEm"),
		ORDEM("ordem"),
		DESC_MEDICAMENTO("descMedicamento"),
		DOSE_TOTAL("doseTotal"),
		CELULA("celula"),
		TEMPO_DECORRIDO("tempoDecorrido");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Version
	@Column(nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	@Column(name = "SESSAO", insertable=false, updatable=false)
	public Integer getSessao() {
		return this.sessao;
	}

	public void setSessao(Integer sessao) {
		this.sessao = sessao;
	}

	@Column(name = "FIC_SEQ", insertable=false, updatable=false)
	public Integer getFicSeq() {
		return this.ficSeq;
	}

	public void setFicSeq(Integer ficSeq) {
		this.ficSeq = ficSeq;
	}

	@Column(name = "FFA_SEQ", insertable=false, updatable=false)
	public Integer getFfaSeq() {
		return this.ffaSeq;
	}

	public void setFfaSeq(Integer ffaSeq) {
		this.ffaSeq = ffaSeq;
	}

	@Column(name = "DESC_MEDICAMENTO", length = 200, insertable=false, updatable=false)
	@Length(max = 200)
	public String getDescMedicamento() {
		return this.descMedicamento;
	}

	public void setDescMedicamento(String descMedicamento) {
		this.descMedicamento = descMedicamento;
	}

	@Column(name = "DOSE_TOTAL", length = 100, insertable=false, updatable=false)
	@Length(max = 100)
	public String getDoseTotal() {
		return this.doseTotal;
	}

	public void setDoseTotal(String doseTotal) {
		this.doseTotal = doseTotal;
	}

	@Column(name = "DTHR_OCORRENCIA", length = 29, insertable=false, updatable=false)
	public Date getDthrOcorrencia() {
		return this.dthrOcorrencia;
	}

	public void setDthrOcorrencia(Date dthrOcorrencia) {
		this.dthrOcorrencia = dthrOcorrencia;
	}

	@Column(name = "CELULA", length = 20, insertable=false, updatable=false)
	@Length(max = 20)
	public String getCelula() {
		return this.celula;
	}

	public void setCelula(String celula) {
		this.celula = celula;
	}

	@Column(name = "ORDEM", insertable=false, updatable=false)
	public Integer getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "TEMPO_DECORRIDO", insertable=false, updatable=false)
	public Short getTempoDecorrido() {
		return this.tempoDecorrido;
	}

	public void setTempoDecorrido(Short tempoDecorrido) {
		this.tempoDecorrido = tempoDecorrido;
	}

	@Column(name = "CRIADO_EM", length = 29, insertable=false, updatable=false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ID_SESSAO", length = 256, insertable=false, updatable=false)
	public String getIdSessao() {
		return idSessao;
	}

	public void setIdSessao(String idSessao) {
		this.idSessao = idSessao;
	}

}
