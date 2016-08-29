package br.gov.mec.aghu.ambulatorio.vo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class RelatorioGuiaAtendimentoUnimedSubProced2VO implements BaseEntity {

	private static final long serialVersionUID = 9128835777634955682L;
	
	private String dtProc;	
	private String tabela;
	private String codProcedimento;
	private String descrProcedimento;
	private String qtde;	
	private String via;
	private String cid;
	private String percentual;
	private String vlrTot;
	private String vlrUnit;	
	
	/*
	 * Getters e Setters
	 */
	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public String getCodProcedimento() {
		return codProcedimento;
	}

	public void setCodProcedimento(String codProcedimento) {
		this.codProcedimento = codProcedimento;
	}
	
	public String getCodProcedimentoFormatado() {
		if (StringUtils.isNotBlank(codProcedimento)) {
			return StringUtil.adicionaZerosAEsquerda(codProcedimento, 8);
		}
		return codProcedimento;
	}

	public String getDescrProcedimento() {
		return descrProcedimento;
	}

	public void setDescrProcedimento(String descrProcedimento) {
		this.descrProcedimento = descrProcedimento;
	}

	public String getQtde() {
		return qtde;
	}

	public void setQtde(String qtde) {
		this.qtde = qtde;
	}

	public String getDtProc() {
		return dtProc;
	}

	public void setDtProc(String dtProc) {
		this.dtProc = dtProc;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getPercentual() {
		return percentual;
	}

	public void setPercentual(String percentual) {
		this.percentual = percentual;
	}

	public String getVlrTot() {
		return vlrTot;
	}

	public void setVlrTot(String vlrTot) {
		this.vlrTot = vlrTot;
	}

	public String getVlrUnit() {
		return vlrUnit;
	}

	public void setVlrUnit(String vlrUnit) {
		this.vlrUnit = vlrUnit;
	}	

	
	/*
	 * Métodos utilitários
	 */

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getTabela());
		umHashCodeBuilder.append(this.getCodProcedimento());
		umHashCodeBuilder.append(this.getDescrProcedimento());
		umHashCodeBuilder.append(this.getQtde());
		umHashCodeBuilder.append(this.getDtProc());
		umHashCodeBuilder.append(this.getVia());
		umHashCodeBuilder.append(this.getCid());
		umHashCodeBuilder.append(this.getPercentual());
		umHashCodeBuilder.append(this.getVlrTot());
		umHashCodeBuilder.append(this.getVlrUnit());
		
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
		if (!(obj instanceof RelatorioGuiaAtendimentoUnimedSubProced2VO)) {
			return false;
		}
		RelatorioGuiaAtendimentoUnimedSubProced2VO other = (RelatorioGuiaAtendimentoUnimedSubProced2VO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getTabela(), other.getTabela());
		umEqualsBuilder.append(this.getCodProcedimento(), other.getCodProcedimento());
		umEqualsBuilder.append(this.getDescrProcedimento(), other.getDescrProcedimento());
		umEqualsBuilder.append(this.getQtde(), other.getQtde());
		umEqualsBuilder.append(this.getDtProc(), other.getDtProc());
		umEqualsBuilder.append(this.getVia(), other.getVia());
		umEqualsBuilder.append(this.getCid(), other.getCid());
		umEqualsBuilder.append(this.getPercentual(), other.getPercentual());
		umEqualsBuilder.append(this.getVlrTot(), other.getVlrTot());
		umEqualsBuilder.append(this.getVlrUnit(), other.getVlrUnit());
				
		return umEqualsBuilder.isEquals();
	}
	
}