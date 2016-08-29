package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_AREA_TEC_AVALIACAO", schema = "AGH")
@SequenceGenerator(name="ptmAreaTecAvaliacaoSeq", sequenceName="AGH.PTM_AREA_TEC_AVALIACAO_SEQ", allocationSize = 1)
public class PtmAreaTecAvaliacao extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8520765182964589485L;

	private Integer seq;
	private String nomeAreaTecAvaliacao;
	private FccCentroCustos fccCentroCustos;
	private RapServidores servidorCC;
	private DominioSituacao situacao;
	private String mensagem;
	private RapServidores servidor;
	private Integer version;
	private Boolean indEmailSumarizado;
	private Set<PtmServAreaTecAvaliacao> listaPtmServAreaTecAvaliacao = new HashSet<PtmServAreaTecAvaliacao>(0);
	private Set<PtmItemRecebProvisorios> itensRecebProvisorios = new HashSet<PtmItemRecebProvisorios>(0);
	public PtmAreaTecAvaliacao() {
	}

	public PtmAreaTecAvaliacao(Integer seq, String nomeAreaTecAvaliacao,
			FccCentroCustos fccCentroCustos, RapServidores servidorCC,
			DominioSituacao situacao, String mensagem, RapServidores servidor,
			Integer version) {
		this.seq = seq;
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
		this.fccCentroCustos = fccCentroCustos;
		this.servidorCC = servidorCC;
		this.situacao = situacao;
		this.mensagem = mensagem;
		this.servidor = servidor;
		this.version = version;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmAreaTecAvaliacaoSeq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME_AREA_TEC_AVALIACAO", length = 250)
	@Length(max = 250)
	public String getNomeAreaTecAvaliacao() {
		return nomeAreaTecAvaliacao;
	}

	public void setNomeAreaTecAvaliacao(String nomeAreaTecAvaliacao) {
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_CENTRO_CUSTO", referencedColumnName = "CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_CC", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CC", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCC() {
		return servidorCC;
	}

	public void setServidorCC(RapServidores servidorCC) {
		this.servidorCC = servidorCC;
	}

	@Column(name = "SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "MENSAGEM", length = 2000)
	@Length(max = 2000)
	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "IND_EMAIL_SUMARIZADO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmailSumarizado() {
		return indEmailSumarizado;
	}

	public void setIndEmailSumarizado(Boolean indEmailSumarizado) {
		this.indEmailSumarizado = indEmailSumarizado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ptmAreaTecAvaliacao")
	public Set<PtmServAreaTecAvaliacao> getListaPtmServAreaTecAvaliacao() {
		return listaPtmServAreaTecAvaliacao;
	}

	public void setListaPtmServAreaTecAvaliacao(
			Set<PtmServAreaTecAvaliacao> listaPtmServAreaTecAvaliacao) {
		this.listaPtmServAreaTecAvaliacao = listaPtmServAreaTecAvaliacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "areaTecnicaAvaliacao")
	public Set<PtmItemRecebProvisorios> getItensRecebProvisorios() {
		return itensRecebProvisorios;
	}

	public void setItensRecebProvisorios(
			Set<PtmItemRecebProvisorios> itensRecebProvisorios) {
		this.itensRecebProvisorios = itensRecebProvisorios;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		SEQ("seq"),
		NOME_AREA_TEC_AVALIACAO("nomeAreaTecAvaliacao"),
		FCC_CENTRO_CUSTOS("fccCentroCustos"),
		COD_CENTRO_CUSTOS("fccCentroCustos.codigo"),
		SERVIDOR_CC("servidorCC"),
		SERVIDOR_CC_MATRICULA("servidorCC.id.matricula"),
		SERVIDOR_CC_VIN_CODIGO("servidorCC.id.vinCodigo"),
		SITUACAO("situacao"),
		MENSAGEM("mensagem"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_CODIGO("servidor.id.vinCodigo"),
		ITENS_RECEB_PROVISORIOS("itensRecebProvisorios"),
		VERSION("version"),
		IND_EMAIL_SUMARIZADO("indEmailSumarizado"),
		FCC_CENTRO_CUSTOS_CODIGO("fccCentroCustos.codigo"),
		PTM_SERV_AREA_TEC_AVALIACAO("listaPtmServAreaTecAvaliacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
		
		@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getNomeAreaTecAvaliacao());
        umHashCodeBuilder.append(this.getMensagem());
        umHashCodeBuilder.append(this.getSituacao());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		PtmAreaTecAvaliacao other = (PtmAreaTecAvaliacao) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getNomeAreaTecAvaliacao(), other.getNomeAreaTecAvaliacao());
        umEqualsBuilder.append(this.getMensagem(), other.getMensagem());
        umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
        return umEqualsBuilder.isEquals();
	}
}
