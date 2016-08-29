package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCaeSq1", sequenceName = "SIG_CAE_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_ativ_equipamentos", schema = "agh")
public class SigCalculoAtividadeEquipamento extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -22469349840428867L;

	@Transient
	private final String EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA = "^0*";
	
	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private String codPatrimonio;
	private BigDecimal vlrDepreciacao;
	private BigDecimal peso;
	private SigCalculoComponente sigCalculoComponente;
	private SigDirecionadores sigDirecionadores;
	private SigAtividades sigAtividades;
	private SigAtividadeEquipamentos sigAtividadeEquipamentos;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCaeSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "cod_patrimonio", nullable = false, length = 60)
	public String getCodPatrimonio() {
		return codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		if (codPatrimonio != null) {
			codPatrimonio = codPatrimonio.replaceFirst(EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA, "");		
		}
		this.codPatrimonio = codPatrimonio;
	}

	@Column(name = "vlr_deprec", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDepreciacao() {
		return vlrDepreciacao;
	}

	public void setVlrDepreciacao(BigDecimal vlrDepreciacao) {
		this.vlrDepreciacao = vlrDepreciacao;
	}

	@Column(name = "peso", nullable = false, precision = 20, scale = 4)
	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmt_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoComponente getSigCalculoComponente() {
		return sigCalculoComponente;
	}

	public void setSigCalculoComponente(SigCalculoComponente sigCalculoComponente) {
		this.sigCalculoComponente = sigCalculoComponente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dir_seq", referencedColumnName = "seq")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvd_seq", referencedColumnName = "seq")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ave_seq", referencedColumnName = "seq")
	public SigAtividadeEquipamentos getSigAtividadeEquipamentos() {
		return sigAtividadeEquipamentos;
	}

	public void setSigAtividadeEquipamentos(SigAtividadeEquipamentos sigAtividadeEquipamentos) {
		this.sigAtividadeEquipamentos = sigAtividadeEquipamentos;
	}

	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		VALOR_DEPRECIACAO("vlrDepreciacao"),
		PESO("peso"),
		CALCULO_COMPONENTE("sigCalculoComponente"),
		DIRECIONADOR("sigDirecionadores"),
		ATIVIDADE("sigAtividades"),
		ATIVIDADE_EQUIPAMENTO("sigAtividadeEquipamentos");

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
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoAtividadeEquipamento)) {
			return false;
		}
		SigCalculoAtividadeEquipamento other = (SigCalculoAtividadeEquipamento) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}
}
