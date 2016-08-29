package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import java.io.Serializable;

public class ItemProcedimentoVO implements Serializable {
	private static final long serialVersionUID = 8620877110378612566L;
	private Integer pciSeq;
	private String pciDescricao;
	private Integer phiSeq;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Long iphCodTabela;
	private String iphDescricao;
	private String ceiIndComparacao;
	private String ceiIndCompatExclus;
	private Short cmpPhoSeq;
	private Integer cmpSeq;
	private Long cmpCodTabela;
	private String cmpDescricao;
	private String excIndComparacao;
	private String excIndCompatExclus;
	private DominioIndComparacao dominioIndComparacao;
	private DominioIndCompatExclus dominioIndCompatExclus;
	private Integer ipxSeq;
	private Long ipxCodTabela;
	private String ipxDescricao;
	private Short ipxPhoSeq;

	public Integer getPciSeq() {
		return this.pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public String getPciDescricao() {
		return this.pciDescricao;
	}

	public void setPciDescricao(String pciDescricao) {
		this.pciDescricao = pciDescricao;
	}

	public Integer getPhiSeq() {
		return this.phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Short getIphPhoSeq() {
		return this.iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return this.iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Long getIphCodTabela() {
		return this.iphCodTabela;
	}

	public void setIphCodTabela(Long iphCodTabela) {
		this.iphCodTabela = iphCodTabela;
	}

	public String getIphDescricao() {
		return this.iphDescricao;
	}

	public void setIphDescricao(String iphDescricao) {
		this.iphDescricao = iphDescricao;
	}

	public String getCeiIndComparacao() {
		return this.ceiIndComparacao;
	}

	public void setCeiIndComparacao(String ceiIndComparacao) {
		this.ceiIndComparacao = ceiIndComparacao;
	}

	public String getCeiIndCompatExclus() {
		return this.ceiIndCompatExclus;
	}

	public void setCeiIndCompatExclus(String ceiIndCompatExclus) {
		this.ceiIndCompatExclus = ceiIndCompatExclus;
	}

	public Short getCmpPhoSeq() {
		return this.cmpPhoSeq;
	}

	public void setCmpPhoSeq(Short cmpPhoSeq) {
		this.cmpPhoSeq = cmpPhoSeq;
	}

	public Integer getCmpSeq() {
		return this.cmpSeq;
	}

	public void setCmpSeq(Integer cmpSeq) {
		this.cmpSeq = cmpSeq;
	}

	public Long getCmpCodTabela() {
		return this.cmpCodTabela;
	}

	public void setCmpCodTabela(Long cmpCodTabela) {
		this.cmpCodTabela = cmpCodTabela;
	}

	public String getCmpDescricao() {
		return this.cmpDescricao;
	}

	public void setCmpDescricao(String cmpDescricao) {
		this.cmpDescricao = cmpDescricao;
	}

	public String getExcIndComparacao() {
		return this.excIndComparacao;
	}

	public void setExcIndComparacao(String excIndComparacao) {
		this.excIndComparacao = excIndComparacao;
	}

	public String getExcIndCompatExclus() {
		return this.excIndCompatExclus;
	}

	public void setExcIndCompatExclus(String excIndCompatExclus) {
		this.excIndCompatExclus = excIndCompatExclus;
	}

	public Integer getIpxSeq() {
		return this.ipxSeq;
	}

	public void setIpxSeq(Integer ipxSeq) {
		this.ipxSeq = ipxSeq;
	}

	public Long getIpxCodTabela() {
		return this.ipxCodTabela;
	}

	public void setIpxCodTabela(Long ipxCodTabela) {
		this.ipxCodTabela = ipxCodTabela;
	}

	public String getIpxDescricao() {
		return this.ipxDescricao;
	}

	public void setIpxDescricao(String ipxDescricao) {
		this.ipxDescricao = ipxDescricao;
	}

	public Short getIpxPhoSeq() {
		return this.ipxPhoSeq;
	}

	public void setIpxPhoSeq(Short ipxPhoSeq) {
		this.ipxPhoSeq = ipxPhoSeq;
	}

	public DominioIndComparacao getDominioIndComparacao() {
		return this.dominioIndComparacao;
	}

	public void setDominioIndComparacao(
			DominioIndComparacao dominioIndComparacao) {
		this.dominioIndComparacao = dominioIndComparacao;
	}

	public DominioIndCompatExclus getDominioIndCompatExclus() {
		return this.dominioIndCompatExclus;
	}

	public void setDominioIndCompatExclus(
			DominioIndCompatExclus dominioIndCompatExclus) {
		this.dominioIndCompatExclus = dominioIndCompatExclus;
	}
	
	public enum Fields {
		PCI_SEQ("pciSeq"), 
		PCI_DESCRICAO("pciDescricao"), 
		PHI_SEQ("phiSeq"), 
		IPH_PHO_SEQ("iphPhoSeq"), 
		IPH_SEQ("iphSeq"), 
		IPH_COD_TABELA("iphCodTabela"), 
		IPH_DESCRICAO("iphDescricao"), 
		CEI_IND_COMPARACAO("ceiIndComparacao"), 
		CEI_IND_COMPAT_EXCLUS("ceiIndCompatExclus"), 
		CMP_PHO_SEQ("cmpPhoSeq"), 
		CMP_SEQ("cmpSeq"), 
		CMP_COD_TABELA("cmpCodTabela"), 
		CMP_DESCRICAO("cmpDescricao"), 
		EXC_IND_COMPARACAO("excIndComparacao"), 
		EXC_IND_COMPAT_EXCLUS("excIndCompatExclus"), 
		IPX_SEQ("ipxSeq"), 
		IPX_COD_TABELA("ipxCodTabela"), 
		IPX_DESCRICAO("ipxDescricao"), 
		DOMINIO_IND_COMPARACAO("dominioIndComparacao"), 
		IPX_PHO_SEQ("ipxPhoSeq"), 
		DOMINIO_IND_COMPAT_EXCLUS("dominioIndCompatExclus");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}		
	}
}