package br.gov.mec.aghu.model;

import java.io.Serializable;
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
@Table(name = "ECP_ITEM_CTRL_CUIDADO_ENFS", schema = "AGH")
@SequenceGenerator(name = "ecpCenSq1", sequenceName = "AGH.ECP_CEN_SQ1", allocationSize = 1)
public class EcpItemCtrlCuidadoEnf extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4539538189123419186L;
	private Integer seq;
	private Date criadoEm;
	private Integer version;
	private EcpItemControle itemControle;
	private EpeCuidados cuidado;
	private RapServidores servidor;

	// construtores
	public EcpItemCtrlCuidadoEnf() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 5, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpCenSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne
	@JoinColumn(name = "ICE_SEQ", referencedColumnName = "SEQ")
	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	@ManyToOne
	@JoinColumn(name = "CUI_SEQ", referencedColumnName = "SEQ")
	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpItemCtrlCuidadoEnf)) {
			return false;
		}
		EcpItemCtrlCuidadoEnf castOther = (EcpItemCtrlCuidadoEnf) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		VERSION("version"),
		ECP_ITEM_CONTROLE("itemControle"),
		EPE_CUIDADOS("cuidado"),
		RAP_SERVIDORES("servidor");

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