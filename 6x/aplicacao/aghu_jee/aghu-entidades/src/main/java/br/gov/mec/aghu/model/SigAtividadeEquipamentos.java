package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigAveSq1", sequenceName = "SIG_AVE_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIVIDADE_EQUIPAMENTOS", schema = "AGH")
public class SigAtividadeEquipamentos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 3396783834270644763L;

	@Transient
	private final String EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA = "^0*";

	private Integer seq;
	private SigAtividades sigAtividades;
	private String codPatrimonio;
	private SigDirecionadores sigDirecionadores;
	private Date criadoEm;
	private RapServidores servidorResp;
	private DominioSituacao indSituacao;

	//transient
	private Boolean emEdicao = Boolean.FALSE;

	private Integer codigoCC;

	private Integer version;

	private Boolean selected = Boolean.FALSE;

	public SigAtividadeEquipamentos() {
	}

	public SigAtividadeEquipamentos(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigAveSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "COD_PATRIMONIO", nullable = false, length = 60)
	public String getCodPatrimonio() {
		return this.codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		if (codPatrimonio != null) {
			codPatrimonio = codPatrimonio.replaceFirst(EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA, "");
		}
		this.codPatrimonio = codPatrimonio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResp() {
		return this.servidorResp;
	}

	public void setServidorResp(RapServidores servidorResp) {
		this.servidorResp = servidorResp;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	@Transient
	public Integer getCodigoCC() {
		return codigoCC;
	}

	public void setCodigoCC(Integer codigoCC) {
		this.codigoCC = codigoCC;
	}

	@Transient
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		ATIVIDADE("sigAtividades"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("servidorResp"),
		IND_SITUACAO("indSituacao"),
		DIRECIONADOR("sigDirecionadores");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigAtividadeEquipamentos)) {
			return false;
		}
		SigAtividadeEquipamentos castOther = (SigAtividadeEquipamentos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
