package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigGocSq1", sequenceName = "SIG_GOC_SQ1", allocationSize = 1)
@Table(name = "SIG_GRUPO_OCUPACOES", schema = "AGH")
public class SigGrupoOcupacoes extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -3979321488716386280L;
	
	private Integer seq;
	private String descricao;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer version;
	private FccCentroCustos fccCentroCustos;
	private List<SigAtividadePessoas> listAtividadePessoas = new ArrayList<SigAtividadePessoas>(0);
	private List<SigGrupoOcupacaoCargos> listGrupoOcupacaoCargos = new ArrayList<SigGrupoOcupacaoCargos>(0);
	
	public SigGrupoOcupacoes() {
	}
	
	public SigGrupoOcupacoes(Integer seq){
		this.setSeq(seq);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigGocSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	@JoinColumns({ @JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO") })
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigGrupoOcupacoes")
	public List<SigAtividadePessoas> getListAtividadePessoas() {
		return listAtividadePessoas;
	}

	public void setListAtividadePessoas(List<SigAtividadePessoas> listAtividadePessoas) {
		this.listAtividadePessoas = listAtividadePessoas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigGrupoOcupacoes")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public List<SigGrupoOcupacaoCargos> getListGrupoOcupacaoCargos() {
		return listGrupoOcupacaoCargos;
	}

	public void setListGrupoOcupacaoCargos(List<SigGrupoOcupacaoCargos> listGrupoOcupacaoCargos) {
		this.listGrupoOcupacaoCargos = listGrupoOcupacaoCargos;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		CENTRO_CUSTO("fccCentroCustos"),
		LISTA_GRUPO_OCUPACAO_CARGOS("listGrupoOcupacaoCargos"),
		LISTA_ATIVIDADES("listAtividadePessoas");

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
		if (!(other instanceof SigGrupoOcupacoes)) {
			return false;
		}
		SigGrupoOcupacoes castOther = (SigGrupoOcupacoes) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
