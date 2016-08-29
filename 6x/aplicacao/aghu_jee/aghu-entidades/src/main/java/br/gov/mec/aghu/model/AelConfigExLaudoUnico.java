package br.gov.mec.aghu.model;

// Generated 09/04/2012 13:17:38 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidadeTempoLiberacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AelConfigExLaudoUnicos generated by hbm2java
 */
@Entity
@Table(name = "AEL_CONFIG_EX_LAUDO_UNICOS")
@SequenceGenerator(name = "aelLu2Sq1", sequenceName = "AGH.AEL_LU2_SQ1", allocationSize = 1)
public class AelConfigExLaudoUnico extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42210741360701876L;

	private Integer seq;
	private String nome;
	private String sigla;
	private Boolean imagem;
	private Boolean macro;
	private Boolean micro;
	private Boolean lamina;
	private Short tempoAposLib;
	private DominioUnidadeTempoLiberacao unidTempoLib;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Boolean laudoAnterior;
	private RapServidores servidor;
	private Set<AelItemConfigExame> itensConfigExame = new HashSet<AelItemConfigExame>(0);
	private Long ultimoNumero;
	private Integer version;
	private Set<AelSecaoConfExames> secaoConfExames = new HashSet<AelSecaoConfExames>(0);

	public AelConfigExLaudoUnico() {
	}

	public AelConfigExLaudoUnico(Integer lu2Seq) {
		this.seq = lu2Seq;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLu2Sq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", nullable = false, length = 500)
	@NotNull
	@Length(max = 500)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "SIGLA", nullable = false, length = 3)
	@NotNull
	@Length(max = 3)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "IND_IMAGEM", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getImagem() {
		return imagem;
	}

	public void setImagem(Boolean imagem) {
		this.imagem = imagem;
	}

	@Column(name = "IND_MACRO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getMacro() {
		return macro;
	}

	public void setMacro(Boolean macro) {
		this.macro = macro;
	}

	@Column(name = "IND_MICRO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getMicro() {
		return micro;
	}

	public void setMicro(Boolean micro) {
		this.micro = micro;
	}

	@Column(name = "IND_LAMINA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	@NotNull
	public Boolean getLamina() {
		return lamina;
	}

	public void setLamina(Boolean lamina) {
		this.lamina = lamina;
	}

	@Column(name = "TEMPO_APOS_LIB", precision = 3, scale = 0)
	public Short getTempoAposLib() {
		return this.tempoAposLib;
	}

	public void setTempoAposLib(Short tempoAposLib) {
		this.tempoAposLib = tempoAposLib;
	}

	@Column(name = "UNID_TEMPO_LIB", length = 1)
	@Enumerated(EnumType.STRING)	
	public DominioUnidadeTempoLiberacao getUnidTempoLib() {
		return this.unidTempoLib;
	}

	public void setUnidTempoLib(DominioUnidadeTempoLiberacao unidTempoLib) {
		this.unidTempoLib = unidTempoLib;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)	
	@NotNull
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_LAUDO_ANTERIOR", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	@NotNull
	public Boolean getLaudoAnterior() {
		return laudoAnterior;
	}

	public void setLaudoAnterior(Boolean laudoAnterior) {
		this.laudoAnterior = laudoAnterior;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "configExLaudoUnico")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public Set<AelItemConfigExame> getItensConfigExame() {
		return itensConfigExame;
	}

	public void setItensConfigExame(Set<AelItemConfigExame> itensConfigExame) {
		this.itensConfigExame = itensConfigExame;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelConfigExLaudoUnico")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	public Set<AelSecaoConfExames> getSecaoConfExames() {
		return secaoConfExames;
	}

	public void setSecaoConfExames(Set<AelSecaoConfExames> secaoConfExames) {
		this.secaoConfExames = secaoConfExames;
	}

	@Column(name = "ULTIMO_NUM", nullable = false, precision = 10, scale = 0)
	@NotNull
	public Long getUltimoNumero() {
		return ultimoNumero;
	}

	public void setUltimoNumero(Long ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}
	
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		SIGLA("sigla"),
		IMAGEM("imagem"),
		MACRO("macro"),
		MICRO("micro"),
		LAMINA("lamina"),
		TEMPO_APOS_LIB("tempoAposLib"),
		UNID_TEMPO_LIB("unidTempoLib"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		LAUDO_ANTERIOR("laudoAnterior"),
		SERVIDOR("servidor"),
		ULTIMO_NUM("ultimoNumero"),
		SECAO_CONF_EXAMES("secaoConfExames"),
		ITENS_CONFIG_EXAME("itensConfigExame");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		AelConfigExLaudoUnico b = (AelConfigExLaudoUnico) o;
		return new EqualsBuilder().append(getSeq(), b.getSeq()).isEquals();
	}
}