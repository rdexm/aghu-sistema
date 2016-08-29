package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDiagnostico;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the cse_categoria_profissionais database table.
 * 
 */
@Entity
@SequenceGenerator(name="cseCagSq1", sequenceName="AGH.CSE_CAG_SQ1", allocationSize = 1)
@Table(name = "CSE_CATEGORIA_PROFISSIONAIS")
public class CseCategoriaProfissional extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2739649015698953014L;

	private Integer seq;

	private Date criadoEm;

	private String descricao;

	private DominioSituacao indSituacao;

	private String nome;

	private RapServidores servidor;

	private DominioTipoDiagnostico tipoDiagnostico;
	
	private List<MamTipoItemAnamneses> tipoItemAnamneses;
	
	private List<MamTipoItemEvolucao> tipoItemEvolucoes;
	
	private List<CseCategoriaPerfil> categoriaPerfis;

	public CseCategoriaProfissional() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cseCagSq1")
	@Column(unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(nullable = false, length = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(nullable = false, length = 60)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "TIPO_DIAGNOSTICO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoDiagnostico getTipoDiagnostico() {
		return this.tipoDiagnostico;
	}

	public void setTipoDiagnostico(DominioTipoDiagnostico tipoDiagnostico) {
		this.tipoDiagnostico = tipoDiagnostico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {
		SEQ("seq"),
		SERVIDOR("servidor"),
		IND_SITUACAO("indSituacao"),
		TIPO_ITEM_ANAMNESE("tipoItemAnamneses"),
		TIPO_ITEM_EVOLUCAO("tipoItemEvolucoes"),
		NOME("nome")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="categoriaProfissional")	
	public List<MamTipoItemAnamneses> getTipoItemAnamneses() {
		return tipoItemAnamneses;
	}

	public void setTipoItemAnamneses(List<MamTipoItemAnamneses> tipoItemanamneses) {
		this.tipoItemAnamneses = tipoItemanamneses;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="categoriaProfissional")	
	public List<MamTipoItemEvolucao> getTipoItemEvolucoes() {
		return tipoItemEvolucoes;
	}

	public void setTipoItemEvolucoes(List<MamTipoItemEvolucao> tipoItemEvolucoes) {
		this.tipoItemEvolucoes = tipoItemEvolucoes;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cseCategoriaProfissional")	
	public List<CseCategoriaPerfil> getCategoriaPerfis() {
		return categoriaPerfis;
	}
	
	public void setCategoriaPerfis(List<CseCategoriaPerfil> categoriaPerfis) {
		this.categoriaPerfis = categoriaPerfis;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CseCategoriaProfissional)) {
			return false;
		}
		CseCategoriaProfissional other = (CseCategoriaProfissional) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}