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
@Table(name = "RAP_VEICULOS", schema = "AGH")
public class RapVeiculo extends BaseEntityId<RapVeiculoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6756363039154957858L;
	private RapVeiculoId id;
	private Integer version;
	private RapMarcaModeloVeiculo rapMarcaModeloVeiculo;
	private RapPessoasFisicas rapPessoasFisicas;
	private Short ano;
	private String cor;
	private Date dtCadastro;
	private String indPermissao;
	private String indSituacao;
	private Date dtAtualizacao;
	private String indOcupManha;
	private String indOcupTarde;
	private String indOcupNoite;
	private String observacao;

	public RapVeiculo() {
	}

	public RapVeiculo(RapVeiculoId id, RapMarcaModeloVeiculo rapMarcaModeloVeiculo, RapPessoasFisicas rapPessoasFisicas,
			Short ano, String cor, Date dtCadastro, Date dtAtualizacao) {
		this.id = id;
		this.rapMarcaModeloVeiculo = rapMarcaModeloVeiculo;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.ano = ano;
		this.cor = cor;
		this.dtCadastro = dtCadastro;
		this.dtAtualizacao = dtAtualizacao;
	}

	public RapVeiculo(RapVeiculoId id, RapMarcaModeloVeiculo rapMarcaModeloVeiculo, RapPessoasFisicas rapPessoasFisicas,
			Short ano, String cor, Date dtCadastro, String indPermissao, String indSituacao, Date dtAtualizacao, String indOcupManha,
			String indOcupTarde, String indOcupNoite, String observacao) {
		this.id = id;
		this.rapMarcaModeloVeiculo = rapMarcaModeloVeiculo;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.ano = ano;
		this.cor = cor;
		this.dtCadastro = dtCadastro;
		this.indPermissao = indPermissao;
		this.indSituacao = indSituacao;
		this.dtAtualizacao = dtAtualizacao;
		this.indOcupManha = indOcupManha;
		this.indOcupTarde = indOcupTarde;
		this.indOcupNoite = indOcupNoite;
		this.observacao = observacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "placa", column = @Column(name = "PLACA", nullable = false, length = 7)),
			@AttributeOverride(name = "pesCodigo", column = @Column(name = "PES_CODIGO", nullable = false)) })
	public RapVeiculoId getId() {
		return this.id;
	}

	public void setId(RapVeiculoId id) {
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
	@JoinColumn(name = "MMV_CODIGO", nullable = false)
	public RapMarcaModeloVeiculo getRapMarcaModeloVeiculo() {
		return this.rapMarcaModeloVeiculo;
	}

	public void setRapMarcaModeloVeiculo(RapMarcaModeloVeiculo rapMarcaModeloVeiculo) {
		this.rapMarcaModeloVeiculo = rapMarcaModeloVeiculo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PES_CODIGO", nullable = false, insertable = false, updatable = false)
	public RapPessoasFisicas getRapPessoasFisicas() {
		return this.rapPessoasFisicas;
	}

	public void setRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas) {
		this.rapPessoasFisicas = rapPessoasFisicas;
	}

	@Column(name = "ANO", nullable = false)
	public Short getAno() {
		return this.ano;
	}

	public void setAno(Short ano) {
		this.ano = ano;
	}

	@Column(name = "COR", nullable = false, length = 30)
	@Length(max = 30)
	public String getCor() {
		return this.cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CADASTRO", nullable = false, length = 29)
	public Date getDtCadastro() {
		return this.dtCadastro;
	}

	public void setDtCadastro(Date dtCadastro) {
		this.dtCadastro = dtCadastro;
	}

	@Column(name = "IND_PERMISSAO", length = 1)
	@Length(max = 1)
	public String getIndPermissao() {
		return this.indPermissao;
	}

	public void setIndPermissao(String indPermissao) {
		this.indPermissao = indPermissao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ATUALIZACAO", nullable = false, length = 29)
	public Date getDtAtualizacao() {
		return this.dtAtualizacao;
	}

	public void setDtAtualizacao(Date dtAtualizacao) {
		this.dtAtualizacao = dtAtualizacao;
	}

	@Column(name = "IND_OCUP_MANHA", length = 1)
	@Length(max = 1)
	public String getIndOcupManha() {
		return this.indOcupManha;
	}

	public void setIndOcupManha(String indOcupManha) {
		this.indOcupManha = indOcupManha;
	}

	@Column(name = "IND_OCUP_TARDE", length = 1)
	@Length(max = 1)
	public String getIndOcupTarde() {
		return this.indOcupTarde;
	}

	public void setIndOcupTarde(String indOcupTarde) {
		this.indOcupTarde = indOcupTarde;
	}

	@Column(name = "IND_OCUP_NOITE", length = 1)
	@Length(max = 1)
	public String getIndOcupNoite() {
		return this.indOcupNoite;
	}

	public void setIndOcupNoite(String indOcupNoite) {
		this.indOcupNoite = indOcupNoite;
	}

	@Column(name = "OBSERVACAO", length = 150)
	@Length(max = 150)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_MARCAS_MODELOS_VEICULOS("rapMarcaModeloVeiculo"),
		RAP_PESSOAS_FISICAS("rapPessoasFisicas"),
		ANO("ano"),
		COR("cor"),
		DT_CADASTRO("dtCadastro"),
		IND_PERMISSAO("indPermissao"),
		IND_SITUACAO("indSituacao"),
		DT_ATUALIZACAO("dtAtualizacao"),
		IND_OCUP_MANHA("indOcupManha"),
		IND_OCUP_TARDE("indOcupTarde"),
		IND_OCUP_NOITE("indOcupNoite"),
		OBSERVACAO("observacao");

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
		if (!(obj instanceof RapVeiculo)) {
			return false;
		}
		RapVeiculo other = (RapVeiculo) obj;
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
