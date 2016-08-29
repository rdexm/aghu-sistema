package br.gov.mec.aghu.model;

// Generated 19/03/2010 17:25:07 by Hibernate Tools 3.2.5.Beta

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcPciSq1", sequenceName="AGH.MBC_PCI_SQ1", allocationSize = 1)
@Table(name = "MBC_PROCEDIMENTO_CIRURGICOS", schema = "AGH")
public class MbcProcedimentoCirurgicos extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -6040510728057965410L;
	private Integer seq;
	private String descricao;
	private Boolean indProcRealizadoLeito;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private Boolean indSalaEspecial;
	private Short tempoMinimo;
	private Byte numeroDoadores;
	private String cuidadosPreOper;
	private DominioIndContaminacao indContaminacao;
	private Boolean indProcMultiplo;
	private RapServidores servidorAlterado;
	private Boolean indNsSemPront;
	private DominioTipoProcedimentoCirurgico tipo;
	private Boolean indTipagemSangue;
	private Boolean indAplicacaoQuimio;
	private Boolean indInteresseCcih;
	private Boolean indLancaAmbulatorio;
	private Boolean indGeraImagensPacs;
	private DominioRegimeProcedimentoCirurgicoSus regimeProcedSus;
	private Boolean ladoCirurgia;
	private Set<MbcProcEspPorCirurgias> procEspPorCirurgias;
	private Set<FatProcedHospInternos> procedimentosHospitalaresInternos;
	private Set<PdtProcDiagTerap> procsDiagsTeraps;
	private Set<MbcEspecialidadeProcCirgs> especialidadesProcsCirgs;
	private Set<MbcSinonimoProcCirg> sinonimosProcsCirgs;
	
	private Integer version;

	
	public MbcProcedimentoCirurgicos() {
	}

	public MbcProcedimentoCirurgicos(Integer seq, String descricao,
			Boolean indProcRealizadoLeito, Date criadoEm, DominioSituacao indSituacao,
			RapServidores servidor, Boolean indSalaEspecial,
			DominioIndContaminacao indContaminacao, Boolean indProcMultiplo, DominioTipoProcedimentoCirurgico tipo,
			Boolean indAplicacaoQuimio, Boolean indInteresseCcih) {
		this.seq = seq;
		this.descricao = descricao;
		this.indProcRealizadoLeito = indProcRealizadoLeito;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.servidor = servidor;
		this.indSalaEspecial = indSalaEspecial;
		this.indContaminacao = indContaminacao;
		this.indProcMultiplo = indProcMultiplo;
		this.tipo = tipo;
		this.indAplicacaoQuimio = indAplicacaoQuimio;
		this.indInteresseCcih = indInteresseCcih;
	}

	public MbcProcedimentoCirurgicos(Integer seq, String descricao,
			Boolean indProcRealizadoLeito, Date criadoEm, DominioSituacao indSituacao,
			RapServidores servidor, Boolean indSalaEspecial,
			Short tempoMinimo, Byte numeroDoadores, String cuidadosPreOper,
			DominioIndContaminacao indContaminacao, Boolean indProcMultiplo,
			RapServidores servidorAlterado,
			Boolean indNsSemPront, DominioTipoProcedimentoCirurgico tipo, Boolean indTipagemSangue,
			Boolean indAplicacaoQuimio, Boolean indInteresseCcih,
			Boolean indLancaAmbulatorio, Boolean indGeraImagensPacs,
			DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.seq = seq;
		this.descricao = descricao;
		this.indProcRealizadoLeito = indProcRealizadoLeito;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.servidor = servidor;
		this.indSalaEspecial = indSalaEspecial;
		this.tempoMinimo = tempoMinimo;
		this.numeroDoadores = numeroDoadores;
		this.cuidadosPreOper = cuidadosPreOper;
		this.indContaminacao = indContaminacao;
		this.indProcMultiplo = indProcMultiplo;
		this.servidorAlterado = servidorAlterado;
		this.indNsSemPront = indNsSemPront;
		this.tipo = tipo;
		this.indTipagemSangue = indTipagemSangue;
		this.indAplicacaoQuimio = indAplicacaoQuimio;
		this.indInteresseCcih = indInteresseCcih;
		this.indLancaAmbulatorio = indLancaAmbulatorio;
		this.indGeraImagensPacs = indGeraImagensPacs;
		this.regimeProcedSus = regimeProcedSus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcPciSq1")
	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", unique = true, nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_PROC_REALIZADO_LEITO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProcRealizadoLeito() {
		return this.indProcRealizadoLeito;
	}

	public void setIndProcRealizadoLeito(Boolean a) {
		this.indProcRealizadoLeito = a;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Transient
	public Boolean getIndSituacaoBoolean(){
	    if(getIndSituacao().equals("A")){
		return getIndSituacao() == DominioSituacao.A;
	    }else{
		return getIndSituacao() == DominioSituacao.I;
	    }
	}

	@Column(name = "IND_SALA_ESPECIAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSalaEspecial() {
		return this.indSalaEspecial;
	}

	public void setIndSalaEspecial(Boolean indSalaEspecial) {
		this.indSalaEspecial = indSalaEspecial;
	}

	@Column(name = "TEMPO_MINIMO", precision = 4, scale = 0)
	public Short getTempoMinimo() {
		return this.tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}

	@Column(name = "NUMERO_DOADORES", precision = 2, scale = 0)
	public Byte getNumeroDoadores() {
		return this.numeroDoadores;
	}

	public void setNumeroDoadores(Byte numeroDoadores) {
		this.numeroDoadores = numeroDoadores;
	}

	@Column(name = "CUIDADOS_PRE_OPER", length = 100)
	@Length(max = 100)
	public String getCuidadosPreOper() {
		return this.cuidadosPreOper;
	}

	public void setCuidadosPreOper(String cuidadosPreOper) {
		this.cuidadosPreOper = cuidadosPreOper;
	}

	@Column(name = "IND_CONTAMINACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndContaminacao getIndContaminacao() {
		return this.indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	@Column(name = "IND_PROC_MULTIPLO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProcMultiplo() {
		return this.indProcMultiplo;
	}

	public void setIndProcMultiplo(Boolean indProcMultiplo) {
		this.indProcMultiplo = indProcMultiplo;
	}

	@Column(name = "IND_NS_SEM_PRONT", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNsSemPront() {
		return this.indNsSemPront;
	}

	public void setIndNsSemPront(Boolean indNsSemPront) {
		this.indNsSemPront = indNsSemPront;
	}

	@Column(name = "TIPO", nullable = false, precision = 1, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoProcedimentoCirurgico getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoProcedimentoCirurgico tipo) {
		this.tipo = tipo;
	}

	@Column(name = "IND_TIPAGEM_SANGUE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTipagemSangue() {
		return this.indTipagemSangue;
	}

	public void setIndTipagemSangue(Boolean indTipagemSangue) {
		this.indTipagemSangue = indTipagemSangue;
	}

	@Column(name = "IND_APLICACAO_QUIMIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAplicacaoQuimio() {
		return this.indAplicacaoQuimio;
	}

	public void setIndAplicacaoQuimio(Boolean indAplicacaoQuimio) {
		this.indAplicacaoQuimio = indAplicacaoQuimio;
	}

	@Column(name = "IND_INTERESSE_CCIH", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInteresseCcih() {
		return this.indInteresseCcih;
	}

	public void setIndInteresseCcih(Boolean indInteresseCcih) {
		this.indInteresseCcih = indInteresseCcih;
	}

	@Column(name = "IND_LANCA_AMBULATORIO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndLancaAmbulatorio() {
		return this.indLancaAmbulatorio;
	}

	public void setIndLancaAmbulatorio(Boolean indLancaAmbulatorio) {
		this.indLancaAmbulatorio = indLancaAmbulatorio;
	}

	@Column(name = "IND_GERA_IMAGENS_PACS", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeraImagensPacs() {
		return this.indGeraImagensPacs;
	}

	public void setIndGeraImagensPacs(Boolean indGeraImagensPacs) {
		this.indGeraImagensPacs = indGeraImagensPacs;
	}

	@Column(name = "REGIME_PROCED_SUS", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioRegimeProcedimentoCirurgicoSus getRegimeProcedSus() {
		return this.regimeProcedSus;
	}

	public void setRegimeProcedSus(DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.regimeProcedSus = regimeProcedSus;
	} 
	
	@Column(name = "IND_LADO_CIRURGIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getLadoCirurgia() {
		return this.ladoCirurgia;
	}	
	
	public void setLadoCirurgia(Boolean ladoCirurgia) {
		this.ladoCirurgia = ladoCirurgia;
	}
	
 	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoCirurgico")
	public Set<MbcProcEspPorCirurgias> getProcEspPorCirurgias() {
		return procEspPorCirurgias;
	}

	public void setProcEspPorCirurgias(
			Set<MbcProcEspPorCirurgias> procEspPorCirurgias) {
		this.procEspPorCirurgias = procEspPorCirurgias;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoCirurgico")
	public Set<FatProcedHospInternos> getProcedimentosHospitalaresInternos() {
		return procedimentosHospitalaresInternos;
	}

	public void setProcedimentosHospitalaresInternos(
			Set<FatProcedHospInternos> procedimentosHospitalaresInternos) {
		this.procedimentosHospitalaresInternos = procedimentosHospitalaresInternos;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof MbcProcedimentoCirurgicos)) {
			return false;
		}
		MbcProcedimentoCirurgicos other = (MbcProcedimentoCirurgicos) obj;
		if (this.getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!this.getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}
	
	@Transient
	public String getSeqEDescricao(){
		return getSeq() + "   " + getDescricao();
	}

	public enum Fields {
		SEQ("seq")
		, DESCRICAO("descricao")
		, IND_SITUACAO("indSituacao")
		, IND_PROC_REALIZADO_LEITO("indProcRealizadoLeito")
		, IND_CONTAMINACAO("indContaminacao")
		, REGIME_PROCED_SUS("regimeProcedSus")
		, PROCEDIMENTOS_HOSPITALARES_INTERNOS("procedimentosHospitalaresInternos")
		, TIPO("tipo")
		, SERVIDOR("servidor")
		, IND_GERA_IMAGENS_PACS("indGeraImagensPacs")
		, IND_INTERESSE_CCIH("indInteresseCcih")
		, ESPECIALIDADES_PROCS_CIRGS("especialidadesProcsCirgs")
		, SINONIMOS_PROCS_CIRGS("sinonimosProcsCirgs")
		, SITUACAO("indSituacao")
		, IND_TIPAGEM_SANGUE("indTipagemSangue")
		, PROCS_DIAGS_TERAPS("procsDiagsTeraps")
		, IND_LANCA_AMBULATORIO("indLancaAmbulatorio")
		, PROC_ESP_POR_CIRURGIAS("procEspPorCirurgias")
		, IND_LADO_CIRURGIA("ladoCirurgia");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedimentoCirurgico")
	public Set<PdtProcDiagTerap> getProcsDiagsTeraps() {
		return procsDiagsTeraps;
	}

	public void setProcsDiagsTeraps(Set<PdtProcDiagTerap> procsDiagsTeraps) {
		this.procsDiagsTeraps = procsDiagsTeraps;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProcedimentoCirurgicos")
	public Set<MbcEspecialidadeProcCirgs> getEspecialidadesProcsCirgs() {
		return especialidadesProcsCirgs;
	}

	public void setEspecialidadesProcsCirgs(
			Set<MbcEspecialidadeProcCirgs> especialidadesProcsCirgs) {
		this.especialidadesProcsCirgs = especialidadesProcsCirgs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProcedimentoCirurgicos")
	public Set<MbcSinonimoProcCirg> getSinonimosProcsCirgs() {
		return sinonimosProcsCirgs;
	}

	public void setSinonimosProcsCirgs(Set<MbcSinonimoProcCirg> sinonimosProcsCirgs) {
		this.sinonimosProcsCirgs = sinonimosProcsCirgs;
	}
}