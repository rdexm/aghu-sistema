package br.gov.mec.aghu.view;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "V_MAM_RECEITAS", schema = "AGH")
@Immutable
public class VMamReceitas implements BaseEntity{

	private static final long serialVersionUID = -3652112568020659558L;
	private Long seq; //rct.seq,
	private Integer conNumero; //rct.con_numero,
	private DominioTipoReceituario tipo; //rct.tipo,
	private Short ircSeq; //irc.seqp,
	private String descricao; //irc.descricao,
	private String quantidade; //irc.quantidade,
	private String formaUso; //irc.forma_uso,
	private Boolean indUsoContinuo; //ind_uso_continuo
	private Boolean indInterno; //ind_interno
	private Integer atdSeq; //rct.atd_seq,
	private Integer asuApaAtdSeq; //rct.asu_apa_atd_seq,
	private Date dtHrCriacao; //rct.dthr_criacao,
	private Integer pacCodigo; //rct.pac_codigo 
	
	private boolean selecionado;
	
	@Id
	@Column(name = "SEQ")
	public Long getSeq() {
		return seq;
	}
	
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@Column(name = "CON_NUMERO")
	public Integer getConNumero() {
		return conNumero;
	}
	
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	
	@Column(name = "TIPO", length = 2)
    @Enumerated(EnumType.STRING)
	public DominioTipoReceituario getTipo() {
		return tipo;
	}
	
	public void setTipo(DominioTipoReceituario tipo) {
		this.tipo = tipo;
	}
	
	@Column(name = "SEQP")
	public Short getIrcSeq() {
		return ircSeq;
	}
	
	public void setIrcSeq(Short ircSeq) {
		this.ircSeq = ircSeq;
	}
	
	@Column(name = "DESCRICAO", length = 120)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "QUANTIDADE")
	public String getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}
	
	@Column(name = "FORMA_USO", length = 500)
	public String getFormaUso() {
		return formaUso;
	}
	
	public void setFormaUso(String formaUso) {
		this.formaUso = formaUso;
	}
	
	@Column(name = "IND_USO_CONTINUO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoContinuo() {
		return indUsoContinuo;
	}
	
	public void setIndUsoContinuo(Boolean indUsoContinuo) {
		this.indUsoContinuo = indUsoContinuo;
	}
	
	@Column(name = "IND_INTERNO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInterno() {
		return indInterno;
	}
	
	public void setIndInterno(Boolean indInterno) {
		this.indInterno = indInterno;
	}
	
	@Column(name = "ATD_SEQ")
	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	@Column(name = "ASU_APA_ATD_SEQ")
	public Integer getAsuApaAtdSeq() {
		return asuApaAtdSeq;
	}
	
	public void setAsuApaAtdSeq(Integer asuApaAtdSeq) {
		this.asuApaAtdSeq = asuApaAtdSeq;
	}
	
	@Column(name = "DTHR_CRIACAO")
	public Date getDtHrCriacao() {
		return dtHrCriacao;
	}
	
	public void setDtHrCriacao(Date dtHrCriacao) {
		this.dtHrCriacao = dtHrCriacao;
	}
	
	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public enum Fields {

		SEQ("seq"),
		CON_NUMERO("conNumero"),
		TIPO("tipo"),
		IRC_SEQ("ircSeq"),
		DESCRICAO("descricao"),
		QUANTIDADE("quantidade"),
		FORMA_USO("formaUso"),
		IND_USO_CONTINUO("indUsoContinuo"),
		IND_INTERNO("indInterno"),
		ATD_SEQ("atdSeq"),
		ASU_APA_ATD_SEQ("asuApaAtdSeq"),
		DTHR_CRIACAO("dtHrCriacao"),
		PAC_CODIGO("pacCodigo");

		private final String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	// ##### GeradorEqualsHashCodeMain #####
			@Override
			public int hashCode() {
				HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
				umHashCodeBuilder.append(this.getSeq());
				umHashCodeBuilder.append(this.getConNumero());
				umHashCodeBuilder.append(this.getTipo());
				umHashCodeBuilder.append(this.getPacCodigo());
				umHashCodeBuilder.append(this.getDescricao());
				umHashCodeBuilder.append(this.getQuantidade());
				umHashCodeBuilder.append(this.getFormaUso());
				return umHashCodeBuilder.toHashCode();
			}
			
			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (!(obj instanceof VMamReceitas)) {
					return false;
				}
				VMamReceitas other = (VMamReceitas) obj;
				EqualsBuilder umEqualsBuilder = new EqualsBuilder();
				umEqualsBuilder.append(this.getSeq(), other.getSeq());
				umEqualsBuilder.append(this.getConNumero(), other.getConNumero());
				umEqualsBuilder.append(this.getTipo(), other.getTipo());
				umEqualsBuilder.append(this.getPacCodigo(), other.getPacCodigo());
				umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
				umEqualsBuilder.append(this.getQuantidade(), other.getQuantidade());
				umEqualsBuilder.append(this.getFormaUso(), other.getFormaUso());
				return umEqualsBuilder.isEquals();
			}
			// ##### GeradorEqualsHashCodeMain #####
}
