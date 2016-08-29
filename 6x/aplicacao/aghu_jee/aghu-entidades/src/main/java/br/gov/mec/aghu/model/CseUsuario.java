package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

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
 *
 * OBSERVAÇÃO IMPORTANTE: Não utilizar esta classe. Em locais onde for 
 * necessário, utilizar a entidade Usuario e demais serviços do CASCA.
 * 
 * @see CascaService
 */

@Entity
@Table(name = "CSE_USUARIOS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = { "ser_matricula", "ser_vin_codigo" }))
public class CseUsuario extends BaseEntityId<String> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2092872942722836781L;
	private String id;
	private RapServidores rapServidores;
	private CseTipoUsuario cseTipoUsuario;
	private String criadoPor;
	private Date criadoEm;
	private String exibeLista;
	private String senhaInicial;
	private String criaUserRede;
	private String consisteRede;
	private String indMultiploLogon;
	private Short timeout;
	private Date assinadoEm;
	private Date geradoEmailEm;
	private Date desabilitaNtEmailEm;
	private Date excluiEmailEm;
	private String categoriaFavoritos;
	private Short gueSeq;
	private Date trocaSenhaEm;
	private Date expiradoEm;
	private Set<CseUsuarioAdm> cseUsuarioAdmes = new HashSet<CseUsuarioAdm>(0);
	private Set<CseControleExpiracao> cseControleExpiracaoes = new HashSet<CseControleExpiracao>(0);

	@Id
	@Column(name = "ID", unique = true, nullable = false, length = 30)
	@Length(max = 30)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPU_CODIGO", nullable = false)
	public CseTipoUsuario getCseTipoUsuario() {
		return this.cseTipoUsuario;
	}

	public void setCseTipoUsuario(CseTipoUsuario cseTipoUsuario) {
		this.cseTipoUsuario = cseTipoUsuario;
	}

	@Column(name = "CRIADO_POR", nullable = false, length = 30)
	@Length(max = 30)
	public String getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "EXIBE_LISTA", length = 1)
	@Length(max = 1)
	public String getExibeLista() {
		return this.exibeLista;
	}

	public void setExibeLista(String exibeLista) {
		this.exibeLista = exibeLista;
	}

	@Column(name = "SENHA_INICIAL", nullable = false, length = 30)
	@Length(max = 30)
	public String getSenhaInicial() {
		return this.senhaInicial;
	}

	public void setSenhaInicial(String senhaInicial) {
		this.senhaInicial = senhaInicial;
	}

	@Column(name = "CRIA_USER_REDE", nullable = false, length = 1)
	@Length(max = 1)
	public String getCriaUserRede() {
		return this.criaUserRede;
	}

	public void setCriaUserRede(String criaUserRede) {
		this.criaUserRede = criaUserRede;
	}

	@Column(name = "CONSISTE_REDE", nullable = false, length = 1)
	@Length(max = 1)
	public String getConsisteRede() {
		return this.consisteRede;
	}

	public void setConsisteRede(String consisteRede) {
		this.consisteRede = consisteRede;
	}

	@Column(name = "IND_MULTIPLO_LOGON", length = 1)
	@Length(max = 1)
	public String getIndMultiploLogon() {
		return this.indMultiploLogon;
	}

	public void setIndMultiploLogon(String indMultiploLogon) {
		this.indMultiploLogon = indMultiploLogon;
	}

	@Column(name = "TIMEOUT")
	public Short getTimeout() {
		return this.timeout;
	}

	public void setTimeout(Short timeout) {
		this.timeout = timeout;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ASSINADO_EM", length = 29)
	public Date getAssinadoEm() {
		return this.assinadoEm;
	}

	public void setAssinadoEm(Date assinadoEm) {
		this.assinadoEm = assinadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GERADO_EMAIL_EM", length = 29)
	public Date getGeradoEmailEm() {
		return this.geradoEmailEm;
	}

	public void setGeradoEmailEm(Date geradoEmailEm) {
		this.geradoEmailEm = geradoEmailEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DESABILITA_NT_EMAIL_EM", length = 29)
	public Date getDesabilitaNtEmailEm() {
		return this.desabilitaNtEmailEm;
	}

	public void setDesabilitaNtEmailEm(Date desabilitaNtEmailEm) {
		this.desabilitaNtEmailEm = desabilitaNtEmailEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXCLUI_EMAIL_EM", length = 29)
	public Date getExcluiEmailEm() {
		return this.excluiEmailEm;
	}

	public void setExcluiEmailEm(Date excluiEmailEm) {
		this.excluiEmailEm = excluiEmailEm;
	}

	@Column(name = "CATEGORIA_FAVORITOS", length = 1)
	@Length(max = 1)
	public String getCategoriaFavoritos() {
		return this.categoriaFavoritos;
	}

	public void setCategoriaFavoritos(String categoriaFavoritos) {
		this.categoriaFavoritos = categoriaFavoritos;
	}

	@Column(name = "GUE_SEQ")
	public Short getGueSeq() {
		return this.gueSeq;
	}

	public void setGueSeq(Short gueSeq) {
		this.gueSeq = gueSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TROCA_SENHA_EM", length = 29)
	public Date getTrocaSenhaEm() {
		return this.trocaSenhaEm;
	}

	public void setTrocaSenhaEm(Date trocaSenhaEm) {
		this.trocaSenhaEm = trocaSenhaEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIRADO_EM", length = 29)
	public Date getExpiradoEm() {
		return this.expiradoEm;
	}

	public void setExpiradoEm(Date expiradoEm) {
		this.expiradoEm = expiradoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<CseUsuarioAdm> getCseUsuarioAdmes() {
		return this.cseUsuarioAdmes;
	}

	public void setCseUsuarioAdmes(Set<CseUsuarioAdm> cseUsuarioAdmes) {
		this.cseUsuarioAdmes = cseUsuarioAdmes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<CseControleExpiracao> getCseControleExpiracaoes() {
		return this.cseControleExpiracaoes;
	}

	public void setCseControleExpiracaoes(Set<CseControleExpiracao> cseControleExpiracaoes) {
		this.cseControleExpiracaoes = cseControleExpiracaoes;
	}

 

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof CseUsuario)) {
			return false;
		}
		CseUsuario other = (CseUsuario) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
