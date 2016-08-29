package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

/**
 * VO da listagem de procedimentos na estória #22460 – Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
public class CirurgiaTelaProcedimentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 203480330301828708L;
	private MbcProcEspPorCirurgiasId id;
	private Boolean indPrincipal;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs;
	private DominioSituacao situacao;
	private Byte qtd;
	private AghCid cid;
	private boolean excluir;
	private String sigla;
	private String descricaoPhi;
	private Integer seqPhi;
	private DominioIndContaminacao indContaminacao;
	private DominioRegimeProcedimentoCirurgicoSus regimeProcedSus;
	private Short tempoMinimo;
	
	
	/*
	 * Outros atributos (UTILIZADOS NAS PROCEDURES) TODO Mover PARA AS ONs se necessário!
	 */
	// Definido como String pois pode ser tanto o cod_tabela quanto *ESCOLHER*
	private String cgnbtEprPciseq;
	private BigDecimal cgnbtEprPciSeq2;
	private Short cgnbtEprPciseq3;
	private Integer phiSeq;
	
	private DominioLadoCirurgiaAgendas lado;

	public MbcProcEspPorCirurgiasId getId() {
		return id;
	}

	public void setId(MbcProcEspPorCirurgiasId id) {
		this.id = id;
	}

	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public MbcEspecialidadeProcCirgs getMbcEspecialidadeProcCirgs() {
		return mbcEspecialidadeProcCirgs;
	}

	public void setMbcEspecialidadeProcCirgs(MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs) {
		this.mbcEspecialidadeProcCirgs = mbcEspecialidadeProcCirgs;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Byte getQtd() {
		return qtd;
	}

	public void setQtd(Byte qtd) {
		this.qtd = qtd;
	}
	
	public String getCgnbtEprPciseq() {
		return cgnbtEprPciseq;
	}

	public void setCgnbtEprPciseq(String cgnbtEprPciseq) {
		this.cgnbtEprPciseq = cgnbtEprPciseq;
	}

	public BigDecimal getCgnbtEprPciSeq2() {
		return cgnbtEprPciSeq2;
	}

	public void setCgnbtEprPciSeq2(BigDecimal cgnbtEprPciSeq2) {
		this.cgnbtEprPciSeq2 = cgnbtEprPciSeq2;
	}

	public Short getCgnbtEprPciseq3() {
		return cgnbtEprPciseq3;
	}

	public void setCgnbtEprPciseq3(Short cgnbtEprPciseq3) {
		this.cgnbtEprPciseq3 = cgnbtEprPciseq3;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	public AghCid getCid() {
		return cid;
	}
	
	
	public void setCid(AghCid cid) {
		this.cid = cid;
	}
	
	
	public DominioLadoCirurgiaAgendas getLado() {
		return lado;
	}

	public void setLado(DominioLadoCirurgiaAgendas lado) {
		this.lado = lado;
	}

	/**
	 * Utilizado no XHTML para que o hascode seja reconhecido como atributo
	 * 
	 * @return
	 */
	public int getHashCode() {
		return this.hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		CirurgiaTelaProcedimentoVO other = (CirurgiaTelaProcedimentoVO) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public boolean isExcluir() {

		return excluir;

	}

	public void setExcluir(boolean excluir) {

		this.excluir = excluir;

	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricaoPhi() {
		return descricaoPhi;
	}

	public void setDescricaoPhi(String descricaoPhi) {
		this.descricaoPhi = descricaoPhi;
	}

	public Integer getSeqPhi() {
		return seqPhi;
	}

	public void setSeqPhi(Integer seqPhi) {
		this.seqPhi = seqPhi;
	}

	public DominioIndContaminacao getIndContaminacao() {
		return indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	public DominioRegimeProcedimentoCirurgicoSus getRegimeProcedSus() {
		return regimeProcedSus;
	}

	public void setRegimeProcedSus(DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.regimeProcedSus = regimeProcedSus;
	}

	public Short getTempoMinimo() {
		return tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}


}
