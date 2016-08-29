package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;

public class VMbcProcEspVO implements Serializable {

	private static final long serialVersionUID = -7482889145371378355L;
	
	private Short espSeq;
	private String sigla;
	private Integer pciSeq;
	private Short seqp;
	private String descricao;
	private String descProc;
	private DominioIndContaminacao indContaminacao;
	private Boolean indProcMultiplo;
	private Short tempoMinimo;
	private DominioSituacao situacaoSinonimo;
	private DominioSituacao situacaoProc;
	private DominioSituacao situacaoEspProc;
	private DominioSituacao situacaoEsp;
	private DominioTipoProcedimentoCirurgico tipo;
	private DominioRegimeProcedimentoCirurgicoSus regimeProcedSus;
	private Boolean indLadoCirurgico;

	public enum Fields {
		ESP_SEQ("espSeq"),
		SIGLA("sigla"),
		PCI_SEQ("pciSeq"),
		SEQP("seqp"),
		DESCRICAO("descricao"),
		DESC_PROC("descProc"),
		IND_CONTAMINACAO("indContaminacao"),
		IND_PROC_MULTIPLO("indProcMultiplo"),
		TEMPO_MINIMO("tempoMinimo"),
		SITUACAO_SINONIMO("situacaoSinonimo"),
		SITUACAO_PROC("situacaoProc"),
		SITUACAO_ESP_PROC("situacaoEspProc"),
		SITUACAO_ESP("situacaoEsp"),
		TIPO("tipo"),
		REGIME_PROCED_SUS("regimeProcedSus"),
		IND_LADO_CIRURGICO("indLadoCirurgico");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescProc() {
		return descProc;
	}

	public void setDescProc(String descProc) {
		this.descProc = descProc;
	}

	public DominioIndContaminacao getIndContaminacao() {
		return indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	public Boolean getIndProcMultiplo() {
		return indProcMultiplo;
	}

	public void setIndProcMultiplo(Boolean indProcMultiplo) {
		this.indProcMultiplo = indProcMultiplo;
	}

	public Short getTempoMinimo() {
		return tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}

	public DominioSituacao getSituacaoSinonimo() {
		return situacaoSinonimo;
	}

	public void setSituacaoSinonimo(DominioSituacao situacaoSinonimo) {
		this.situacaoSinonimo = situacaoSinonimo;
	}

	public DominioSituacao getSituacaoProc() {
		return situacaoProc;
	}

	public void setSituacaoProc(DominioSituacao situacaoProc) {
		this.situacaoProc = situacaoProc;
	}

	public DominioSituacao getSituacaoEspProc() {
		return situacaoEspProc;
	}

	public void setSituacaoEspProc(DominioSituacao situacaoEspProc) {
		this.situacaoEspProc = situacaoEspProc;
	}

	public DominioSituacao getSituacaoEsp() {
		return situacaoEsp;
	}

	public void setSituacaoEsp(DominioSituacao situacaoEsp) {
		this.situacaoEsp = situacaoEsp;
	}

	public DominioTipoProcedimentoCirurgico getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoProcedimentoCirurgico tipo) {
		this.tipo = tipo;
	}

	public Boolean getIndLadoCirurgico() {
		return indLadoCirurgico;
	}

	public void setIndLadoCirurgico(Boolean indLadoCirurgico) {
		this.indLadoCirurgico = indLadoCirurgico;
	}

	public DominioRegimeProcedimentoCirurgicoSus getRegimeProcedSus() {
		return regimeProcedSus;
	}

	public void setRegimeProcedSus(DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.regimeProcedSus = regimeProcedSus;
	}
	
}
