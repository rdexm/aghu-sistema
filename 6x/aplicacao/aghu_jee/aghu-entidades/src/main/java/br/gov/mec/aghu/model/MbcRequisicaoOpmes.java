package br.gov.mec.aghu.model;


import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcRopSq1", sequenceName="AGH.MBC_ROP_SQ1", allocationSize = 1)
@Table(name = "MBC_REQUISICAO_OPMES", schema = "AGH")
public class MbcRequisicaoOpmes extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -8492806268907944587L;
	private Short seq;
	private MbcAgendas agendas;
	private MbcCirurgias cirurgia;
	private DominioSituacaoRequisicao situacao;
	private String observacaoOpme;
	private String justificativaRequisicaoOpme;
	private String justificativaConsumoOpme;
	private Date criadoEm;
	private Date modificadoEm;
	private RapServidores rapServidores;
	private RapServidores rapServidoresModificacao;	
	private Boolean indCompativel;
	private Boolean indAutorizado;
	private Boolean indConsAprovacao;
	private Date dataFim;
	private MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms;
//	private transient AghWFTemplateEtapa situacaoProcessoAutorizacao; 
	
	private Integer version;
	private AghWFFluxo fluxo;
	private List<MbcItensRequisicaoOpmes> itensRequisicao;
	
	public MbcRequisicaoOpmes() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcRopSq1")
	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGD_SEQ")
	public MbcAgendas getAgendas() {
		return agendas;
	}
	public void setAgendas(MbcAgendas agendas) {
		this.agendas = agendas;
	}

	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFICADO_EM", length = 29)
	public Date getModificadoEm() {
		return modificadoEm;
	}


	public void setModificadoEm(Date modificadoEm) {
		this.modificadoEm = modificadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MODIFICACAO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_MODIFICACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresModificacao() {
		return rapServidoresModificacao;
	}


	public void setRapServidoresModificacao(RapServidores rapServidoresModificacao) {
		this.rapServidoresModificacao = rapServidoresModificacao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA", nullable = false),
				   @JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ")
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioSituacaoRequisicao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioSituacaoRequisicao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoRequisicao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "OBSERVACAO_OPME", nullable = true, length = 2000)
	public String getObservacaoOpme() {
		return observacaoOpme;
	}

	public void setObservacaoOpme(String observacaoOpme) {
		this.observacaoOpme = observacaoOpme;
	}

	@Column(name = "JUST_REQUISICAO_OPME", nullable = true, length = 2000)
	public String getJustificativaRequisicaoOpme() {
		return justificativaRequisicaoOpme;
	}

	public void setJustificativaRequisicaoOpme(String justificativaRequisicaoOpme) {
		this.justificativaRequisicaoOpme = justificativaRequisicaoOpme;
	}

	@Column(name = "JUST_CONSUMO_OPME", nullable = true, length = 2000)
	public String getJustificativaConsumoOpme() {
		return justificativaConsumoOpme;
	}

	public void setJustificativaConsumoOpme(String justificativaConsumoOpme) {
		this.justificativaConsumoOpme = justificativaConsumoOpme;
	}

	@Column(name = "IND_COMPATIVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCompativel() {
		return indCompativel;
	}

	public void setIndCompativel(Boolean indCompativel) {
		this.indCompativel = indCompativel;
	}

	@Column(name = "IND_AUTORIZADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAutorizado() {
		return indAutorizado;
	}

	public void setIndAutorizado(Boolean indAutorizado) {
		this.indAutorizado = indAutorizado;
	}

	@Column(name = "IND_CONS_APROV", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsAprovacao() {
		return indConsAprovacao;
	}

	public void setIndConsAprovacao(Boolean indConsAprovacao) {
		this.indConsAprovacao = indConsAprovacao;
	}

	@Column(name = "DT_FIM", nullable = true)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="WFL_SEQ", referencedColumnName="SEQ")
	public AghWFFluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(AghWFFluxo fluxo) {
		this.fluxo = fluxo;
	}

//	@Transient
//	public AghWFTemplateEtapa getSituacaoProcessoAutorizacao() {
//		return situacaoProcessoAutorizacao;
//	}
//
//	public void setSituacaoProcessoAutorizacao(
//			AghWFTemplateEtapa situacaoProcessoAutorizacao) {
//		this.situacaoProcessoAutorizacao = situacaoProcessoAutorizacao;
//	}
	
	public void setItensRequisicao(List<MbcItensRequisicaoOpmes> itensRequisicao) {
		this.itensRequisicao = itensRequisicao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "requisicaoOpmes")
	public List<MbcItensRequisicaoOpmes> getItensRequisicao() {
		return itensRequisicao;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="GAO_SEQ", referencedColumnName="SEQ")
	public MbcGrupoAlcadaAvalOpms getGrupoAlcadaAvalOpms() {
		return grupoAlcadaAvalOpms;
	}

	public void setGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		this.grupoAlcadaAvalOpms = grupoAlcadaAvalOpms;
	}
	
	public enum Fields {
		
		ID("seq"),
		GRUPO_ALCADA_SEQ("grupoAlcadaAvalOpms.seq"),
		NIVEL_ALCADA("nivelAlcada"),
		DESCRICAO("descricao"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo"),
		CRIADO_EM("criadoEm"),
		MODIFICADO_EM("modificadoEm"),
		RAP_SERVIDORES("rapServidores"),
		RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao"),
		AGENDA("agendas"),
		AGENDA_SEQ("agendas.seq"),
		SITUACAO("situacao"),
		FLUXO("fluxo"),
		FLUXO_ID("fluxo.seq"),
		SER_MATRICULA_CRIACAO("rapServidores.id.matricula"),
		SER_VIN_CODIGO_CRIACAO("rapServidores.id.vinCodigo"),
		JUST_REQUISICAO_OPME("justificativaRequisicaoOpme"),
		OBSERVACAO_OPME("observacaoOpme"),
		DT_FIM("dataFim"),
		ITENS_REQUISICAO("itensRequisicao"),
		IND_AUTORIZADO("indAutorizado"),
		CIRURGIA_SEQ("cirurgia"),
		JUST_CONSUMO_OPME("justificativaConsumoOpme")
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


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MbcRequisicaoOpmes)) {
			return false;
		}
		MbcRequisicaoOpmes other = (MbcRequisicaoOpmes) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}



}
