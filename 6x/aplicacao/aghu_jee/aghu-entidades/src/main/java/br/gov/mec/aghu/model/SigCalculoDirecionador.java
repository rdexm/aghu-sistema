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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCdiSq1", sequenceName = "AGH.SIG_CDI_SQ1", allocationSize = 1)
@Table(name = "SIG_CALCULO_DIRECIONADORES", schema = "AGH")
public class SigCalculoDirecionador extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -23890547348589434L;

	private Integer seq;
	private SigDirecionadores direcionador;
	private SigCalculoObjetoCusto calculoObjetoCusto;
	private RapServidores servidor;
	private Date criadoEm;
	private BigDecimal percentual;
	
	private BigDecimal vlrDirInsumos = BigDecimal.ZERO;
	private BigDecimal vlrDirPessoas = BigDecimal.ZERO;
	private BigDecimal vlrDirEquipamentos = BigDecimal.ZERO;
	private BigDecimal vlrDirServicos = BigDecimal.ZERO;
	private BigDecimal vlrIndInsumos = BigDecimal.ZERO;
	private BigDecimal vlrIndPessoas = BigDecimal.ZERO;
	private BigDecimal vlrIndEquipamentos = BigDecimal.ZERO;
	private BigDecimal vlrIndServicos = BigDecimal.ZERO;
	private Integer version;	
	
	public SigCalculoDirecionador() {
	}
	
	public SigCalculoDirecionador(Integer seq) {
		this.seq = seq;
	}

	@Id	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sigCdiSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ")
	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CBJ_SEQ")
	public SigCalculoObjetoCusto getCalculoObjetoCusto() {
		return calculoObjetoCusto;
	}

	public void setCalculoObjetoCusto(SigCalculoObjetoCusto calculoObjetoCusto) {
		this.calculoObjetoCusto = calculoObjetoCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	/**
	 * @return
	 */
	@Column(name = "PERCENTUAL", nullable = false, precision = 3, scale = 2)
	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	@Column(name = "VLR_DIR_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDirInsumos() {
		return vlrDirInsumos;
	}

	public void setVlrDirInsumos(BigDecimal vlrDirInsumos) {
		this.vlrDirInsumos = vlrDirInsumos;
	}

	@Column(name = "VLR_DIR_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDirPessoas() {
		return vlrDirPessoas;
	}

	public void setVlrDirPessoas(BigDecimal vlrDirPessoas) {
		this.vlrDirPessoas = vlrDirPessoas;
	}

	
	@Column(name = "VLR_DIR_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDirEquipamentos() {
		return vlrDirEquipamentos;
	}

	public void setVlrDirEquipamentos(BigDecimal vlrDirEquipamentos) {
		this.vlrDirEquipamentos = vlrDirEquipamentos;
	}

	@Column(name = "VLR_DIR_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDirServicos() {
		return vlrDirServicos;
	}

	public void setVlrDirServicos(BigDecimal vlrDirServicos) {
		this.vlrDirServicos = vlrDirServicos;
	}

	@Column(name = "VLR_IND_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndInsumos() {
		return vlrIndInsumos;
	}

	public void setVlrIndInsumos(BigDecimal vlrIndInsumos) {
		this.vlrIndInsumos = vlrIndInsumos;
	}

	@Column(name = "VLR_IND_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndPessoas() {
		return vlrIndPessoas;
	}

	public void setVlrIndPessoas(BigDecimal vlrIndPessoas) {
		this.vlrIndPessoas = vlrIndPessoas;
	}

	@Column(name = "VLR_IND_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndEquipamentos() {
		return vlrIndEquipamentos;
	}

	public void setVlrIndEquipamentos(BigDecimal vlrIndEquipamentos) {
		this.vlrIndEquipamentos = vlrIndEquipamentos;
	}

	@Column(name = "VLR_IND_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndServicos() {
		return vlrIndServicos;
	}

	public void setVlrIndServicos(BigDecimal vlrIndServicos) {
		this.vlrIndServicos = vlrIndServicos;
	}

	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigCalculoDirecionador)){
			return false;
		}
		SigCalculoDirecionador castOther = (SigCalculoDirecionador) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	public enum Fields {
		SEQ("seq"), 
		DIRECIONADOR("direcionador"),
		CALCULO_OBJETO_CUSTO("calculoObjetoCusto"),
		SERVIDOR("servidor"), 
		CRIADO_EM("criadoEm"),
		PERCENTUAL("percentual"),
		VLR_DIR_INSUMOS("vlrDirInsumos"),
		VLR_DIR_PESSOAS("vlrDirPessoas"),
		VLR_DIR_EQUIPAMENTOS("vlrDirEquipamentos"),
		VLR_DIR_SERVICOS("vlrDirServicos"),
		VLR_IND_INSUMOS("vlrIndInsumos"),
		VLR_IND_PESSOAS("vlrIndPessoas"),
		VLR_IND_EQUIPAMENTOS("vlrIndEquipamentos"),
		VLR_IND_SERVICOS("vlrIndServicos"),	
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}