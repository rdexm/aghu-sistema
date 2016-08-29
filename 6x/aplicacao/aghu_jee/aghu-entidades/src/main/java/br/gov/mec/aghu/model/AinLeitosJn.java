package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * AinLeitosJn 
 * @author Rafael Morais
 */
@Entity
@Table(name = "AIN_LEITOS_JN", schema = "AGH")
@SequenceGenerator(name = "ainLtoJnSeq", sequenceName = "AGH.AIN_LTO_JN_SEQ", allocationSize = 1)
@Immutable
public class AinLeitosJn extends BaseJournal implements Serializable{


	private static final long serialVersionUID = 6326635704275843341L;
	
	private String leitoId;
	private Short numeroQuarto;
	private String leito;
	private Boolean indConsClinUnidade;
	private Boolean indBloqLeitoLimpeza;
	private Short tipoMovimentoLeito;
	private Short unidadeFuncional;
	private DominioSituacao indSituacao;
	private Short espSeq;
	private Integer internacao;
	private Boolean indPertenceRefl;
	private Boolean indConsEsp;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Boolean indAcompanhamentoCcih;
	private Integer version;
	
	public AinLeitosJn() {
	
	}
	
	public AinLeitosJn(String leitoId,
			Short numeroQuarto, String leito, Boolean indConsClinUnidade,
			Boolean indBloqLeitoLimpeza, Short tipoMovimentoLeito,
			Short unidadeFuncional, DominioSituacao indSituacao,
			Short espSeq, Integer internacao, Boolean indPertenceRefl,
			Boolean indConsEsp, Integer serMatricula, Short serVinCodigo,
			Boolean indAcompanhamentoCcih) {
		
		this.leitoId = leitoId;
		this.numeroQuarto = numeroQuarto;
		this.leito = leito;
		this.indConsClinUnidade = indConsClinUnidade;
		this.indBloqLeitoLimpeza = indBloqLeitoLimpeza;
		this.tipoMovimentoLeito = tipoMovimentoLeito;
		this.unidadeFuncional = unidadeFuncional;
		this.indSituacao = indSituacao;
		this.espSeq = espSeq;
		this.internacao = internacao;
		this.indPertenceRefl = indPertenceRefl;
		this.indConsEsp = indConsEsp;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.indAcompanhamentoCcih = indAcompanhamentoCcih;	

	}
	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ainLtoJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override	
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "LTO_ID")
	@NotNull
	public String getLeitoId() {
		return leitoId;
	}
	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
	
	@Column(name = "QRT_NUMERO")
	@NotNull
	public Short getNumeroQuarto() {
		return numeroQuarto;
	}
	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}
	
	@Column(name = "LEITO")
	@NotNull
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	
	@Column(name = "IND_CONS_CLIN_UNIDADE")
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndConsClinUnidade() {
		return indConsClinUnidade;
	}
	public void setIndConsClinUnidade(Boolean indConsClinUnidade) {
		this.indConsClinUnidade = indConsClinUnidade;
	}
	
	@Column(name = "IND_BLOQ_LEITO_LIMPEZA")
    @NotNull
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndBloqLeitoLimpeza() {
		return indBloqLeitoLimpeza;
	}
	public void setIndBloqLeitoLimpeza(Boolean indBloqLeitoLimpeza) {
		this.indBloqLeitoLimpeza = indBloqLeitoLimpeza;
	}
	
	@Column(name = "TML_CODIGO")
	@NotNull
	public Short getTipoMovimentoLeito() {
		return tipoMovimentoLeito;
	}
	public void setTipoMovimentoLeito(Short tipoMovimentoLeito) {
		this.tipoMovimentoLeito = tipoMovimentoLeito;
	}
	
	@Column(name = "UNF_SEQ")
	@NotNull
	public Short getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(Short unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@Column(name = "IND_SITUACAO")
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "ESP_SEQ")	
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	
	@Column(name = "INT_SEQ")
	public Integer getInternacao() {
		return internacao;
	}
	public void setInternacao(Integer internacao) {
		this.internacao = internacao;
	}
	
	@Column(name = "IND_PERTENCE_REFL")
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndPertenceRefl() {
		return indPertenceRefl;
	}
	public void setIndPertenceRefl(Boolean indPertenceRefl) {
		this.indPertenceRefl = indPertenceRefl;
	}
	
	@Column(name = "IND_CONS_ESP")
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndConsEsp() {
		return indConsEsp;
	}
	public void setIndConsEsp(Boolean indConsEsp) {
		this.indConsEsp = indConsEsp;
	}
	
	@Column(name = "SER_MATRICULA")
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO")
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Column(name = "IND_ACOMPANHAMENTO_CCIH")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndAcompanhamentoCcih() {
		return indAcompanhamentoCcih;
	}
	public void setIndAcompanhamentoCcih(Boolean indAcompanhamentoCcih) {
		this.indAcompanhamentoCcih = indAcompanhamentoCcih;
	}
	
	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	

}
