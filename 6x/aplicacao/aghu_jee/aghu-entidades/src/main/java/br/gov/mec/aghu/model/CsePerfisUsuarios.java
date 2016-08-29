package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioSituacao;
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
 * NÃO mapear esta classe para a tabela CSE_PERFIS. 
 * Já foi realizada uma refatoração para a troca de chamadas desta classe
 * para utilizar os serviço de segurança do AGHU:
 * http://redmine.mec.gov.br/projects/aghu/repository/revisions/82769
 *  
 * No lugar, devem ser utilizados os métodos disponíveis em: 
 * br.gov.mec.aghu.casca.service.CascaService
 */
@Entity
@Table(name = "CSE_PERFIS_USUARIOS", schema = "AGH")
public class CsePerfisUsuarios extends BaseEntityId<CsePerfisUsuariosId> implements java.io.Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6332762403003851249L;

	private CsePerfisUsuariosId id;
	
	private Boolean administrador;
	
	private DominioSituacao indSituacao;
	
	private Date criadoEm;
	
	private Date criadoPor;
	
	private Perfil perfil;
	
	private CsePerfisUsuarios() {
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "usrId", column = @Column(name = "USR_ID", nullable = false, length = 30)),
			@AttributeOverride(name = "perNome", column = @Column(name = "PER_NOME", nullable = false, length = 30)) })
	public CsePerfisUsuariosId getId() {
		return this.id;
	}

	public void setId(CsePerfisUsuariosId id) {
		this.id = id;
	}
	
	
	@Column(name = "ADMINISTRADOR", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Boolean administrador) {
		this.administrador = administrador;
	}

	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "CRIADO_POR", nullable = false, length = 30)
	@Length(max = 30)
	public Date getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Date criadoPor) {
		this.criadoPor = criadoPor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PER_NOME", referencedColumnName = "NOME", nullable = false, insertable = false, updatable = false)
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	public enum Fields {

		ID("id"),
		USR_ID("id.usrId"),
		PER_NOME("id.perNome"),
		PERFIL("perfil"),
		IND_SITUACAO("indSituacao")
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

}
