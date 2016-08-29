package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
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
 */
@Immutable

@Entity
@Table(name = "LWS_COM_EVENTOS_COMANDOS", schema = "AGH")
public class LwsComEventosComando extends BaseEntityId<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4654062365939901697L;
	private Integer idEvento;
	private LwsComunicacao lwsComunicacao;
	private String formatoDados;
	private String dados;
	private Set<LwsComunicacao> lwsComunicacaos = new HashSet<LwsComunicacao>(0);

	public LwsComEventosComando() {
	}

	public LwsComEventosComando(Integer idEvento) {
		this.idEvento = idEvento;
	}

	public LwsComEventosComando(Integer idEvento, LwsComunicacao lwsComunicacao, String formatoDados, String dados,
			Set<LwsComunicacao> lwsComunicacaos) {
		this.idEvento = idEvento;
		this.lwsComunicacao = lwsComunicacao;
		this.formatoDados = formatoDados;
		this.dados = dados;
		this.lwsComunicacaos = lwsComunicacaos;
	}

	@Id
	@Column(name = "ID_EVENTO", unique = true, nullable = false)
	public Integer getIdEvento() {
		return this.idEvento;
	}

	public void setIdEvento(Integer idEvento) {
		this.idEvento = idEvento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_COMUNICACAO")
	public LwsComunicacao getLwsComunicacao() {
		return this.lwsComunicacao;
	}

	public void setLwsComunicacao(LwsComunicacao lwsComunicacao) {
		this.lwsComunicacao = lwsComunicacao;
	}

	@Column(name = "FORMATO_DADOS", length = 1)
	@Length(max = 1)
	public String getFormatoDados() {
		return this.formatoDados;
	}

	public void setFormatoDados(String formatoDados) {
		this.formatoDados = formatoDados;
	}

	@Column(name = "DADOS", length = 2048)
	@Length(max = 2048)
	public String getDados() {
		return this.dados;
	}

	public void setDados(String dados) {
		this.dados = dados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "lwsComEventosComando")
	public Set<LwsComunicacao> getLwsComunicacaos() {
		return this.lwsComunicacaos;
	}

	public void setLwsComunicacaos(Set<LwsComunicacao> lwsComunicacaos) {
		this.lwsComunicacaos = lwsComunicacaos;
	}

	public enum Fields {

		ID_EVENTO("idEvento"),
		LWS_COMUNICACAO("lwsComunicacao"),
		FORMATO_DADOS("formatoDados"),
		DADOS("dados"),
		LWS_COMUNICACAOS("lwsComunicacaos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getIdEvento() == null) ? 0 : getIdEvento().hashCode());
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
		if (!(obj instanceof LwsComEventosComando)) {
			return false;
		}
		LwsComEventosComando other = (LwsComEventosComando) obj;
		if (getIdEvento() == null) {
			if (other.getIdEvento() != null) {
				return false;
			}
		} else if (!getIdEvento().equals(other.getIdEvento())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getId(){ return this.getIdEvento();} 
 public void setId(Integer id){ this.setIdEvento(id);}
}
