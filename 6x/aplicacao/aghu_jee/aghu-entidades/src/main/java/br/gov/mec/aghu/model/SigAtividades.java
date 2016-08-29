package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.dominio.DominioOrigemDadoAtividade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigTvdSq1", sequenceName = "SIG_TVD_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIVIDADES", schema = "AGH")
public class SigAtividades extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -833664048234859124L;
	private Integer seq;
	private String nome;
	private DominioOrigemDadoAtividade indOrigemDados;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer version;

	private Set<SigAtividadeCentroCustos> listSigAtividadeCentroCustos = new HashSet<SigAtividadeCentroCustos>(0);
	private Set<SigAtividadePessoas> listAtividadePessoas = new HashSet<SigAtividadePessoas>(0);
	private Set<SigAtividadeInsumos> listAtividadeInsumos = new HashSet<SigAtividadeInsumos>(0);
	private Set<SigAtividadeEquipamentos> listAtividadeEquipamentos = new HashSet<SigAtividadeEquipamentos>(0);
	private Set<SigAtividadeServicos> listAtividadeServicos = new HashSet<SigAtividadeServicos>(0);
	private Set<SigObjetoCustoComposicoes> listObjetoCustoComposicoes = new HashSet<SigObjetoCustoComposicoes>(0);
	
	public SigAtividades(){		
	}
	
	public SigAtividades(Integer seq){	
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigTvdSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", nullable = false, length = 120)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "IND_ORIGEM_DADOS", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioOrigemDadoAtividade getIndOrigemDados() {
		return indOrigemDados;
	}

	public void setIndOrigemDados(DominioOrigemDadoAtividade indOrigemDados) {
		this.indOrigemDados = indOrigemDados;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "sigAtividades", cascade = CascadeType.ALL)
	public Set<SigAtividadeCentroCustos> getListSigAtividadeCentroCustos() {
		return listSigAtividadeCentroCustos;
	}

	public void setListSigAtividadeCentroCustos(Set<SigAtividadeCentroCustos> listSigAtividadeCentroCustos) {
		this.listSigAtividadeCentroCustos = listSigAtividadeCentroCustos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividades")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigAtividadePessoas> getListAtividadePessoas() {
		return listAtividadePessoas;
	}

	public void setListAtividadePessoas(Set<SigAtividadePessoas> listAtividadePessoas) {
		this.listAtividadePessoas = listAtividadePessoas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividades")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigAtividadeInsumos> getListAtividadeInsumos() {
		return listAtividadeInsumos;
	}

	public void setListAtividadeInsumos(Set<SigAtividadeInsumos> listAtividadeInsumos) {
		this.listAtividadeInsumos = listAtividadeInsumos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividades")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigAtividadeEquipamentos> getListAtividadeEquipamentos() {
		return listAtividadeEquipamentos;
	}

	public void setListAtividadeEquipamentos(Set<SigAtividadeEquipamentos> listAtividadeEquipamentos) {
		this.listAtividadeEquipamentos = listAtividadeEquipamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividades")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigAtividadeServicos> getListAtividadeServicos() {
		return listAtividadeServicos;
	}

	public void setListAtividadeServicos(Set<SigAtividadeServicos> listAtividadeServicos) {
		this.listAtividadeServicos = listAtividadeServicos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigAtividades")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigObjetoCustoComposicoes> getListObjetoCustoComposicoes() {
		return listObjetoCustoComposicoes;
	}

	public void setListObjetoCustoComposicoes(Set<SigObjetoCustoComposicoes> listObjetoCustoComposicoes) {
		this.listObjetoCustoComposicoes = listObjetoCustoComposicoes;
	}

	@Transient
	public SigAtividadeCentroCustos getSigAtividadeCentroCustos() {
		if (listSigAtividadeCentroCustos != null && !listSigAtividadeCentroCustos.isEmpty()) {
			return listSigAtividadeCentroCustos.iterator().next();
		} else {
			FccCentroCustos fccCentroCustos = new FccCentroCustos();
			SigAtividadeCentroCustos sigAtividadeCentroCustos = new SigAtividadeCentroCustos();
			sigAtividadeCentroCustos.setFccCentroCustos(fccCentroCustos);
			return sigAtividadeCentroCustos;
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigAtividades)) {
			return false;
		}
		SigAtividades castOther = (SigAtividades) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		IND_ORIGEM_DADOS("indOrigemDados"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		ATIVIDADE_CENTROS_CUSTOS("listSigAtividadeCentroCustos"),
		LISTA_ATIVIDADE_INSUMO("listAtividadeInsumos")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
