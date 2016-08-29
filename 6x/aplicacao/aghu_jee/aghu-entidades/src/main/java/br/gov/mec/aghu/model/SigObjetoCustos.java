package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigObjSq1", sequenceName = "SIG_OBJ_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTOS", schema = "AGH")
public class SigObjetoCustos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -806562318397420188L;
	private Integer seq;
	private String nome;
	private Boolean indCompartilha;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigObjetoCustoPesos sigObjetoCustoPesos;
	private DominioTipoObjetoCusto indTipo;
	private Integer version;
	private Set<SigObjetoCustoVersoes> listObjetoCustoVersoes = new HashSet<SigObjetoCustoVersoes>(0);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigObjSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", nullable = false, length = 200)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "IND_COMPARTILHA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCompartilha() {
		return indCompartilha;
	}

	public void setIndCompartilha(Boolean indCompartilha) {
		this.indCompartilha = indCompartilha;
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
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@OneToOne(mappedBy = "sigObjetoCustos", fetch = FetchType.LAZY)
	public SigObjetoCustoPesos getSigObjetoCustoPesos() {
		return sigObjetoCustoPesos;
	}

	public void setSigObjetoCustoPesos(SigObjetoCustoPesos sigObjetoCustoPesos) {
		this.sigObjetoCustoPesos = sigObjetoCustoPesos;
	}

	@Column(name = "IND_TIPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoObjetoCusto getIndTipo() {
		return indTipo;
	}

	public void setIndTipo(DominioTipoObjetoCusto indTipo) {
		this.indTipo = indTipo;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigObjetoCustos")
	public Set<SigObjetoCustoVersoes> getListObjetoCustoVersoes() {
		return listObjetoCustoVersoes;
	}

	public void setListObjetoCustoVersoes(Set<SigObjetoCustoVersoes> listObjetoCustoVersoes) {
		this.listObjetoCustoVersoes = listObjetoCustoVersoes;
	}

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		IND_COMPARTILHA("indCompartilha"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		OBJETO_CUSTO_PESO("sigObjetoCustoPesos"),
		IND_TIPO("indTipo"),
		VERSOES("listObjetoCustoVersoes");

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
		if (!(other instanceof SigObjetoCustos)) {
			return false;
		}
		SigObjetoCustos castOther = (SigObjetoCustos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
