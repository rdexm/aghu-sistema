/**
 * Identifica o modulo usado dentro da aplicacao
 */
package br.gov.mec.aghu.casca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * @author marcelofilho
 *
 */

@Entity
@Table(name = "CSC_MODULO", schema = "CASCA")
@SequenceGenerator(name = "cscModuloSeq", sequenceName = "casca.casca_modulo_sq1", allocationSize = 1)
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class Modulo extends BaseEntityId<Integer> {

	private static final long serialVersionUID = -9132964637317912110L;
	
	private Integer id;
	
	private String nome;
	
	private String descricao;
	
	private Aplicacao aplicacao;
	
	private Boolean ativo;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cscModuloSeq")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "NOME", length = 20, nullable = false)
	@Length(max = 20, message = "Nome do módulo pode conter no máximo 20 caracteres")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	@Length(max = 60, message = "Descrição do módulo pode conter no máximo 60 caracteres")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@ManyToOne
	@JoinColumn(name="ID_APLICACAO", nullable = false)
	public Aplicacao getAplicacao() {
		return aplicacao;
	}
	
	public void setAplicacao(Aplicacao aplicacao) {
		this.aplicacao = aplicacao;
	}
	
	@Column(name = "ATIVO", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	public enum Fields {
		
		ID("id"), 
		NOME("nome"), 
		DESCRICAO("descricao"),
		ATIVO("ativo"),
		APLICACAO("aplicacao");
		
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