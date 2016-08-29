package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="aelRanSq1", sequenceName="AGH.AEL_RAN_SQ1", allocationSize = 1)
@Table(name = "AEL_REGIOES_ANATOMICAS", schema = "AGH")
public class AelRegiaoAnatomica extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5659692235572816347L;
	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer version;
	
	private Set<AelItemSolicitacaoExames> itemSolicitacaoExames = new HashSet<AelItemSolicitacaoExames>(0);

	public AelRegiaoAnatomica() {
	}

	public AelRegiaoAnatomica(Integer seq, String descricao, DominioSituacao indSituacao, Date criadoEm) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelRanSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao situacao) {
		this.indSituacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(mappedBy = "regiaoAnatomica")
	public Set<AelItemSolicitacaoExames> getItemSolicitacaoExames() {
		return itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(
			Set<AelItemSolicitacaoExames> itemSolicitacaoExames) {
		this.itemSolicitacaoExames = itemSolicitacaoExames;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AelRegiaoAnatomica)) {
			return false;
		}
		AelRegiaoAnatomica castOther = (AelRegiaoAnatomica) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	@Override
	public String toString() {
	    return new ToStringBuilder(this).append(Fields.SEQ.toString(), this.seq).append(Fields.DESCRICAO.toString(), this.descricao).toString();
	 }

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ITEM_SOLICITACAO_EXAMES("itemSolicitacaoExames");
		
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
