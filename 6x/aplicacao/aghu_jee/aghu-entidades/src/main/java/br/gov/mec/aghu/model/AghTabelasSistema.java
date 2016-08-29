package br.gov.mec.aghu.model;

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


import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.dominio.DominioCategoriaTabela;
import br.gov.mec.aghu.dominio.DominioPeriodicidadeAdm;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AGH_TABELAS_SISTEMA", schema = "AGH")
@Indexed
@SequenceGenerator(name="aghTasSeq", sequenceName="AGH.AGH_TAS_SEQ", allocationSize = 1)
public class AghTabelasSistema extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -284803848040124008L;
	private Integer seq;
	private String nome;
	private String origem;
	
	private String gestor;
	private Short periodicidadeNro;
	private DominioPeriodicidadeAdm periodicidadeTipo;
	
	private Date dataCarga;
	private AghCoresTabelasSistema cor;
	private DominioCategoriaTabela categoria;
	private String versao;
	private String descricao;
	private Date dataValidacao;
	private RapServidores servidorResponsavel;
	private Date criadoEm;
	private RapServidores servidorCriacao;
	private Date dataUltimaAlteracao;
	private RapServidores servidorUltAlteracao;
	private Menu menu;
	private Integer version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghTasSeq")
	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	@DocumentId
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "NOME_TABELA", length = 50, nullable = false)
	@Length(max = 50, message="Nome tem mais de 50 caracteres.")
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CARGA")
	public Date getDataCarga() {
		return dataCarga;
	}
	public void setDataCarga(Date dataCarga) {
		this.dataCarga = dataCarga;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_COR")
	public AghCoresTabelasSistema getCor() {
		return cor;
	}
	public void setCor(AghCoresTabelasSistema cor) {
		this.cor = cor;
	}
	
	@Column(name = "CATEGORIA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioCategoriaTabela getCategoria() {
		return categoria;
	}
	public void setCategoria(DominioCategoriaTabela categoria) {
		this.categoria = categoria;
	}
	
	@Column(name = "VERSAO", length = 10, nullable = false)
	public String getVersao() {
		return versao;
	}
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	@Column(name = "DESCRICAO", length = 250)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_VALIDACAO")
	public Date getDataValidacao() {
		return dataValidacao;
	}
	public void setDataValidacao(Date dataValidacao) {
		this.dataValidacao = dataValidacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_RESPONSAVEL", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_RESPONSAVEL", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}
	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
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
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorCriacao() {
		return servidorCriacao;
	}
	public void setServidorCriacao(RapServidores servidorCriacao) {
		this.servidorCriacao = servidorCriacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_ALTERACAO", nullable = false)
	public Date getDataUltimaAlteracao() {
		return dataUltimaAlteracao;
	}
	public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
		this.dataUltimaAlteracao = dataUltimaAlteracao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ULT_ALTERACAO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_ULT_ALTERACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorUltAlteracao() {
		return servidorUltAlteracao;
	}
	public void setServidorUltAlteracao(RapServidores servidorUltAlteracao) {
		this.servidorUltAlteracao = servidorUltAlteracao;
	}
	public static Long getSerialversionuid() {
		return serialVersionUID;
	}


	@Column(name = "GESTOR", length = 50)
	public String getGestor() {
		return gestor;
	}
	public void setGestor(String gestor) {
		this.gestor = gestor;
	}
	
	@Column(name = "PERIODICIDADE_TIPO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioPeriodicidadeAdm getPeriodicidadeTipo() {
		return periodicidadeTipo;
	}
	public void setPeriodicidadeTipo(DominioPeriodicidadeAdm periodicidadeTipo) {
		this.periodicidadeTipo = periodicidadeTipo;
	}
	
	@Column(name = "PERIODICIDADE_NRO", precision = 3, scale = 0)
	public Short getPeriodicidadeNro() {
		return periodicidadeNro;
	}
	public void setPeriodicidadeNro(Short periodicidadeNro) {
		this.periodicidadeNro = periodicidadeNro;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "origem", length = 50)
	@Length(max = 50, message="Origem tem mais de 50 caracteres.")
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_MENU")
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	@Transient
	public String getCategoriaFormat() {
		String retorno = "";
		if (categoria != null){
			retorno = categoria.getDescricao();
		}
		return retorno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof AghTabelasSistema)) {
			return false;
		}
		AghTabelasSistema other = (AghTabelasSistema) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		SEQ("seq"),
		NOME("nome"),
		COR("cor"),
		CATEGORIA("categoria"),
		GESTOR("gestor"),
		PERIODICIDADE_NRO("periodicidadeNro"),
		PERIODICIDADE_TIPO("periodicidadeTipo"),
		ORIGEM("origem"),
		MENU("menu"),
		VERSAO("versao"),
		SERVIDOR_CRIACAO("servidorCriacao"),
		SERVIDOR_ULT_ALTERACAO("servidorUltAlteracao"),
		SERVIDOR_RESPONSAVEL("servidorResponsavel");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	

}
