package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.dominio.DominioSituacaFatura;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the SCE_RMR_PACIENTES database table.
 * 
 */
@Entity
@Table(name = "SCE_RMR_PACIENTES")

@SequenceGenerator(name = "sceRmpSq1", sequenceName = "AGH.SCE_RMP_SQ1", allocationSize = 1)
public class SceRmrPaciente extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7605060096862888813L;
	private Integer seq;
	private Integer atdSeq;
	private FccCentroCustos centroCusto;
	private MbcCirurgias cirurgia;
	private Integer dmoSeq;
	private Date dtAlteracao;
	private Date dtGeracao;
	private Date dtUtilizacao;
	private Boolean indImpresso;
	private DominioSituacaFatura indSituacao;
	private AinInternacao internacao;
	private Integer nfGeral;
	private FatProcedHospInternos procedimentoHospitalarInterno;
	private RapServidores servidor;
	private RapServidores servidorResponsavel;
	private Set<SceItemRmps> sceItemRmps;
	private ScoFornecedor scoFornecedor;
	private Set<FatItemContaHospitalar> fatItemContaHospitalar;

	public SceRmrPaciente() {
	}

	public SceRmrPaciente(final Integer seq, final Integer atdSeq, final FccCentroCustos centroCusto, final MbcCirurgias cirurgia, final Integer dmoSeq,
			final Date dtAlteracao, final Date dtGeracao, final Date dtUtilizacao, final Boolean indImpresso, final DominioSituacaFatura indSituacao, final AinInternacao internacao,
			final Integer nfGeral, final FatProcedHospInternos procedimentoHospitalarInterno, final RapServidores servidor,
			final RapServidores servidorResponsavel, final ScoFornecedor scoFornecedor, final Set<SceItemRmps> sceItemRmps) {
		this.seq = seq;
		this.atdSeq = atdSeq;
		this.centroCusto = centroCusto;
		this.cirurgia = cirurgia;
		this.dmoSeq = dmoSeq;
		this.dtAlteracao = dtAlteracao;
		this.dtGeracao = dtGeracao;
		this.dtUtilizacao = dtUtilizacao;
		this.indImpresso = indImpresso;
		this.indSituacao = indSituacao;
		this.internacao = internacao;
		this.nfGeral = nfGeral;
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
		this.servidor = servidor;
		this.servidorResponsavel = servidorResponsavel;
		this.scoFornecedor = scoFornecedor;
		this.sceItemRmps = sceItemRmps;
	}

	@Id
	@Column(unique = true, nullable = false, precision = 5)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceRmpSq1")
	public Integer getSeq() {
		return this.seq;
	}
	

	public void setSeq(final Integer seq) {
		this.seq = seq;
	}

	@Column(name = "ATD_SEQ", precision = 7)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(final Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO")		
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(final FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ")	
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(final MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	@Column(name = "DMO_SEQ", precision = 9)
	public Integer getDmoSeq() {
		return this.dmoSeq;
	}

	public void setDmoSeq(final Integer dmoSeq) {
		this.dmoSeq = dmoSeq;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_ALTERACAO")
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(final Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_GERACAO", nullable = false)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(final Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_UTILIZACAO")
	public Date getDtUtilizacao() {
		return this.dtUtilizacao;
	}

	public void setDtUtilizacao(final Date dtUtilizacao) {
		this.dtUtilizacao = dtUtilizacao;
	}

	@Column(name = "IND_IMPRESSO", nullable = false, length = 1)	
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpresso() {
		if(this.indImpresso == null){
			this.indImpresso = Boolean.FALSE;
		}
		return this.indImpresso;
	}

	public void setIndImpresso(final Boolean indImpresso) {
		this.indImpresso = indImpresso;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaFatura getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacaFatura indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INT_SEQ")	
	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(final AinInternacao internacao) {
		this.internacao = internacao;
	}

	@Column(name = "NF_GERAL", precision = 7)
	public Integer getNfGeral() {
		return this.nfGeral;
	}

	public void setNfGeral(final Integer nfGeral) {
		this.nfGeral = nfGeral;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ")	
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(
			final FatProcedHospInternos procedimentoHospitalarInterno) {
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_RESPONSAVEL", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_RESPONSAVEL", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(final RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO")
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(final ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	// bi-directional many-to-one association to SceItemRmps
	@OneToMany(mappedBy = "sceRmrPaciente")
	public Set<SceItemRmps> getSceItemRmps() {
		return this.sceItemRmps;
	}

	public void setSceItemRmps(final Set<SceItemRmps> sceItemRmps) {
		this.sceItemRmps = sceItemRmps;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sceRmrPaciente")
	public Set<FatItemContaHospitalar> getFatItemContaHospitalar() {
		return this.fatItemContaHospitalar;
	}

	public void setFatItemContaHospitalar(final Set<FatItemContaHospitalar> fatItemContaHospitalar) {
		this.fatItemContaHospitalar = fatItemContaHospitalar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		final SceRmrPaciente other = (SceRmrPaciente) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
	
	private enum SceRmrPacienteExceptionCode implements BusinessExceptionCode {
		SCE_RMP_CK3
	}
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if(!((this.getInternacao() != null && this.getCirurgia() == null && this.getProcedimentoHospitalarInterno() == null && this.getAtdSeq() == null)
			||(this.getInternacao() == null && this.getCirurgia() != null && this.getProcedimentoHospitalarInterno() == null && this.getAtdSeq() == null)
			||(this.getInternacao() == null && this.getCirurgia() == null && this.getProcedimentoHospitalarInterno() != null && this.getAtdSeq() == null)
			||(this.getInternacao() == null && this.getCirurgia() == null && this.getProcedimentoHospitalarInterno() == null && this.getAtdSeq() != null)
		  )){
			throw new BaseRuntimeException(SceRmrPacienteExceptionCode.SCE_RMP_CK3);
		}
	}

	public enum Fields {
		
		SEQ("seq"),
		SERVIDOR("servidor"),
		SCO_FORNECEDOR("scoFornecedor"),
		SCO_FORNECEDOR_NUMERO("scoFornecedor.numero"),
		NF_GERAL("nfGeral"),
		SCE_ITEM_RMPS("sceItemRmps"),
		CIRURGIA("cirurgia"),
		DT_UTILIZACAO("dtUtilizacao"),
		INT_SEQ("internacao.seq"),
		CRG_SEQ("cirurgia.seq"),
		INTERNACAO("internacao"),
		IND_SITUACAO("indSituacao"),
		FAT_ITEM_CONTA_HOSPITALAR("fatItemContaHospitalar"),
		CENTRO_CUSTO("centroCusto");
		
		private String field;

		private Fields(final String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
		
	}
	
}
