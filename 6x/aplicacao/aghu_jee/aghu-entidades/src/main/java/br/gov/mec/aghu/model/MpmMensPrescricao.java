package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@SequenceGenerator(name="mpmMepSq1", sequenceName="AGH.MPM_MEP_SQ1", allocationSize = 1)
@Table(name = "MPM_MENS_PRESCRICOES", schema = "AGH")
public class MpmMensPrescricao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8067655321630538955L;

	/**
	 * Chave primária da base de dados.
	 */
	private Short seq;
	
	/**
	 * 
	 */
	private String mensagem;
	
	/**
	 * 
	 */
	private Date dtInicioVigencia;
	
	/*
	 * 
	 */
	private Date dtFimVigencia;
	
	/**
	 * 
	 */
	private String linkNomeArquivo;
	
	/**
	 * 
	 */
	private String linkSiteIntranet;
	
	/**
	 * 
	 */
	private String linkForms;
	
	/**
	 * 
	 */
	private Date criadoEm;
	
	/**
	 * 
	 */
	private Date alteradoEm;
	
	/**
	 * 
	 */
	private RapServidores servidor;

	/**
	 * 
	 */
	private RapServidores servidorAlterado;
	
	
	
	private Set<MpmLogMensPrescricao> mpmLogMensPrescricoes = new HashSet<MpmLogMensPrescricao>(
			0);
	private Set<MpmMensPrcrCuidado> mpmMensPrcrCuidados = new HashSet<MpmMensPrcrCuidado>(
			0);

	public MpmMensPrescricao() {
	}

	public MpmMensPrescricao(Short seq, String mensagem,
			Date dtInicioVigencia, Date dtFimVigencia, Date criadoEm,
			RapServidores servidor) {
		this.seq = seq;
		this.mensagem = mensagem;
		this.dtInicioVigencia = dtInicioVigencia;
		this.dtFimVigencia = dtFimVigencia;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	public MpmMensPrescricao(Short seq, String mensagem,
			Date dtInicioVigencia, Date dtFimVigencia, String linkNomeArquivo,
			String linkSiteIntranet, String linkForms, Date criadoEm,
			Date alteradoEm, RapServidores servidor,
			RapServidores servidorAlterado,
			Set<MpmLogMensPrescricao> mpmLogMensPrescricoes,
			Set<MpmMensPrcrCuidado> mpmMensPrcrCuidados) {
		this.seq = seq;
		this.mensagem = mensagem;
		this.dtInicioVigencia = dtInicioVigencia;
		this.dtFimVigencia = dtFimVigencia;
		this.linkNomeArquivo = linkNomeArquivo;
		this.linkSiteIntranet = linkSiteIntranet;
		this.linkForms = linkForms;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.servidor = servidor;
		this.servidorAlterado = servidorAlterado;
		this.mpmLogMensPrescricoes = mpmLogMensPrescricoes;
		this.mpmMensPrcrCuidados = mpmMensPrcrCuidados;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmMepSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "MENSAGEM", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getMensagem() {
		return this.mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INICIO_VIGENCIA", nullable = false, length = 7)
	public Date getDtInicioVigencia() {
		return this.dtInicioVigencia;
	}

	public void setDtInicioVigencia(Date dtInicioVigencia) {
		this.dtInicioVigencia = dtInicioVigencia;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_FIM_VIGENCIA", nullable = false, length = 7)
	public Date getDtFimVigencia() {
		return this.dtFimVigencia;
	}

	public void setDtFimVigencia(Date dtFimVigencia) {
		this.dtFimVigencia = dtFimVigencia;
	}

	@Column(name = "LINK_NOME_ARQUIVO", length = 240)
	@Length(max = 240)
	public String getLinkNomeArquivo() {
		return this.linkNomeArquivo;
	}

	public void setLinkNomeArquivo(String linkNomeArquivo) {
		this.linkNomeArquivo = linkNomeArquivo;
	}

	@Column(name = "LINK_SITE_INTRANET", length = 240)
	@Length(max = 240)
	public String getLinkSiteIntranet() {
		return this.linkSiteIntranet;
	}

	public void setLinkSiteIntranet(String linkSiteIntranet) {
		this.linkSiteIntranet = linkSiteIntranet;
	}

	@Column(name = "LINK_FORMS", length = 240)
	@Length(max = 240)
	public String getLinkForms() {
		return this.linkForms;
	}

	public void setLinkForms(String linkForms) {
		this.linkForms = linkForms;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return this.servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidor) {
		this.servidorAlterado = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mensagemPrescricao")
	public Set<MpmLogMensPrescricao> getMpmLogMensPrescricoeses() {
		return this.mpmLogMensPrescricoes;
	}

	public void setMpmLogMensPrescricoeses(
			Set<MpmLogMensPrescricao> mpmLogMensPrescricoeses) {
		this.mpmLogMensPrescricoes = mpmLogMensPrescricoeses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mensagemPrescricao")
	public Set<MpmMensPrcrCuidado> getMpmMensPrcrCuidadoses() {
		return this.mpmMensPrcrCuidados;
	}

	public void setMpmMensPrcrCuidadoses(
			Set<MpmMensPrcrCuidado> mpmMensPrcrCuidadoses) {
		this.mpmMensPrcrCuidados = mpmMensPrcrCuidadoses;
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
		if (!(obj instanceof MpmMensPrescricao)) {
			return false;
		}
		MpmMensPrescricao other = (MpmMensPrescricao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@PrePersist
	@PreUpdate
	protected void validar(){
		if (dtFimVigencia.before(dtInicioVigencia)){
			throw new BaseRuntimeException();
		}
	}

	public enum Fields {

		SEQ("seq"),
		MENSAGEM("mensagem"),
		DT_INICIO_VIGENCIA("dtInicioVigencia"),
		DT_FIM_VIGENCIA("dtFimVigencia"),
		LINK_NOME_ARQUIVO("linkNomeArquivo"),
		LINK_SITE_INTRANET("linkSiteIntranet"),
		LINK_FORMS("linkForms"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERADO("servidorAlterado");

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
