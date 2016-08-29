package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.casca.model.Usuario;
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
@Immutable
@Deprecated
@Entity
@Table(name = "CSE_CONTROLE_EXPIRACOES", schema = "AGH")
public class CseControleExpiracao extends BaseEntityId<CseControleExpiracaoId> implements java.io.Serializable {

	private static final long serialVersionUID = 2218832842992722866L;
	private CseControleExpiracaoId id;
	private Integer version;
	private Usuario usuario;
	private Date dtEnvioEmail;
	private Date dtMensagemAgh;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "usrId", column = @Column(name = "USR_ID", nullable = false, length = 30)),
			@AttributeOverride(name = "dtAvisoExpiracao", column = @Column(name = "DT_AVISO_EXPIRACAO", nullable = false, length = 29)) })
	public CseControleExpiracaoId getId() {
		return this.id;
	}

	public void setId(CseControleExpiracaoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USR_ID", nullable = false, insertable = false, updatable = false)
	public Usuario getUsuario() {
		return this.usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ENVIO_EMAIL", length = 29)
	public Date getDtEnvioEmail() {
		return this.dtEnvioEmail;
	}

	public void setDtEnvioEmail(Date dtEnvioEmail) {
		this.dtEnvioEmail = dtEnvioEmail;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_MENSAGEM_AGH", length = 29)
	public Date getDtMensagemAgh() {
		return this.dtMensagemAgh;
	}

	public void setDtMensagemAgh(Date dtMensagemAgh) {
		this.dtMensagemAgh = dtMensagemAgh;
	}

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
		if (!(obj instanceof CseControleExpiracao)) {
			return false;
		}
		CseControleExpiracao other = (CseControleExpiracao) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
