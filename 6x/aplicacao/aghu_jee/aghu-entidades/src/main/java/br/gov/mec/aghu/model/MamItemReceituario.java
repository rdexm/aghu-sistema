package br.gov.mec.aghu.model;

import java.io.Serializable;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoUsoReceituario;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "MAM_ITEM_RECEITUARIOS", schema = "AGH")
public class MamItemReceituario extends BaseEntityId<MamItemReceituarioId> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6799320925609821745L;

	private MamItemReceituarioId id;

	private String descricao;
	private String formaUso;
	private String quantidade;
	private DominioSimNao indInterno;
	private DominioSimNao indUsoContinuo;
	private DominioSituacao indSituacao;
	private DominioTipoPrescricaoReceituario tipoPrescricao;
	private Byte nroGrupoImpressao;
	private Byte ordem;
	private DominioSimNao indValidadeProlongada;
	private Byte validadeMeses;
	private Integer version;
	
	private MamReceituarios receituario;

	private enum MamItemReceituarioExceptionCode implements BusinessExceptionCode {
		ERRO_VALIDADE_MESES_INFORMADO_MENOR_ZERO;
	}
		
	// construtores
	public MamItemReceituario() {
	}

	public MamItemReceituario(MamItemReceituarioId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "RCT_SEQ", column = @Column(name = "RCT_SEQ", nullable = false, length = 14)),
			@AttributeOverride(name = "SEQP", column = @Column(name = "SEQP", nullable = false, length = 3))})
	public MamItemReceituarioId getId() {
		return this.id;
	}

	public void setId(MamItemReceituarioId id) {
		this.id = id;
	}

	@Column(name = "DESCRICAO", length = 120, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "FORMA_USO", length = 500)
	public String getFormaUso() {
		return this.formaUso;
	}

	public void setFormaUso(String formaUso) {
		this.formaUso = formaUso;
	}

	@Column(name = "QUANTIDADE", length = 15)
	public String getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "IND_INTERNO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndInterno() {
		return this.indInterno;
	}

	public void setIndInterno(DominioSimNao indInterno) {
		this.indInterno = indInterno;
	}

	@Transient
	public DominioTipoUsoReceituario getIndInternoEnum() {
		if (indInterno==null){
			return null;
		}else if (DominioSimNao.S.equals(this.indInterno)){
			return DominioTipoUsoReceituario.S;
		}else{
			return DominioTipoUsoReceituario.N;			
		}
	}

	public void setIndInternoEnum(DominioTipoUsoReceituario indInterno) {
		if (DominioTipoUsoReceituario.S.equals(indInterno)){
			this.indInterno = DominioSimNao.S;
		}else{
			this.indInterno = DominioSimNao.N;			
		}
	}
	

	@Column(name = "IND_USO_CONTINUO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndUsoContinuo() {
		return this.indUsoContinuo;
	}

	public void setIndUsoContinuo(DominioSimNao indUsoContinuo) {
		this.indUsoContinuo = indUsoContinuo;
	}
	
	@Transient
	public Boolean getIndUsoContinuoBoolean() {
		return DominioSimNao.S.equals(this.indUsoContinuo);
	}

	public void setIndUsoContinuoBoolean(Boolean indUsoContinuo) {
		if (indUsoContinuo){
			this.indUsoContinuo = DominioSimNao.S;
		}else{
			this.indUsoContinuo = DominioSimNao.N;
		}
	}
	

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "TIPO_PRESCRICAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoPrescricaoReceituario getTipoPrescricao() {
		return this.tipoPrescricao;
	}

	public void setTipoPrescricao(DominioTipoPrescricaoReceituario tipoPrescricao) {
		this.tipoPrescricao = tipoPrescricao;
	}

	@Column(name = "NRO_GRUPO_IMPRESSAO", length = 2)
	public Byte getNroGrupoImpressao() {
		return this.nroGrupoImpressao;
	}

	public void setNroGrupoImpressao(Byte nroGrupoImpressao) {
		this.nroGrupoImpressao = nroGrupoImpressao;
	}

	@Column(name = "ORDEM", length = 2)
	public Byte getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Byte ordem) {
		this.ordem = ordem;
	}

	@Column(name = "IND_VALIDADE_PROLONGADA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndValidadeProlongada() {
		return this.indValidadeProlongada;
	}

	public void setIndValidadeProlongada(DominioSimNao indValidadeProlongada) {
		this.indValidadeProlongada = indValidadeProlongada;
	}

	@Column(name = "VALIDADE_MESES", length = 2)
	public Byte getValidadeMeses() {
		return this.validadeMeses;
	}

	public void setValidadeMeses(Byte validadeMeses) {
		this.validadeMeses = validadeMeses;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RCT_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false)
	public MamReceituarios getReceituario() {
		return receituario;
	}

	public void setReceituario(MamReceituarios receituario) {
		this.receituario = receituario;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Função de validação dos dados antes que ocorra a persistência.
	 */
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void valida() {

		// MAM_IRC_CK6
		if (this.validadeMeses != null && this.validadeMeses <= 0) {
			throw new BaseRuntimeException(
					MamItemReceituarioExceptionCode.ERRO_VALIDADE_MESES_INFORMADO_MENOR_ZERO);
		}

	}
	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MamItemReceituario)) {
			return false;
		}
		MamItemReceituario castOther = (MamItemReceituario) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"),
		RCT_SEQ("id.rctSeq"),
		SEQP("id.seqp"),
		DESCRICAO("descricao"), 
		FORMA_USO("formaUso"), 
		QUANTIDADE("quantidade"),
		IND_INTERNO("indInterno"), 
		IND_USO_CONTINUO("indUsoContinuo"),
		IND_SITUACAO("indSituacao"),
		TIPO_PRESCRICAO("tipoPrescricao"),
		NRO_GRUPO_IMPRESSAO("nroGrupoImpressao"),
		ORDEM("ordem"),
		IND_VALIDADE_PROLONGADA("indValidadeProlongada"),
		VALIDADE_MESES("validadeMeses"),
		RECEITUARIO("receituario"),
		RECEITUARIO_SEQ("receituario.seq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	

}