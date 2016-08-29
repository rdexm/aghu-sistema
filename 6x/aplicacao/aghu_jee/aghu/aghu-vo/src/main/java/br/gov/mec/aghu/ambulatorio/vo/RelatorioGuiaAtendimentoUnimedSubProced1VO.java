package br.gov.mec.aghu.ambulatorio.vo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class RelatorioGuiaAtendimentoUnimedSubProced1VO implements BaseEntity {

	private static final long serialVersionUID = 9128835777665955682L;

	private String carater;
	private String tabela;
	private String codProcedimento;
	private String descrProcedimento;
	private String qtde;

	/*
	 * Getters e Setters
	 */
	public String getCarater() {
		return carater;
	}

	public void setCarater(String carater) {
		this.carater = carater;
	}

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

	/*
	 * Métodos utilitários
	 */

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCarater());
		umHashCodeBuilder.append(this.getTabela());
		umHashCodeBuilder.append(this.getCodProcedimento());
		umHashCodeBuilder.append(this.getDescrProcedimento());
		umHashCodeBuilder.append(this.getQtde());

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
		if (!(obj instanceof RelatorioGuiaAtendimentoUnimedSubProced1VO)) {
			return false;
		}
		RelatorioGuiaAtendimentoUnimedSubProced1VO other = (RelatorioGuiaAtendimentoUnimedSubProced1VO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCarater(), other.getCarater());
		umEqualsBuilder.append(this.getTabela(), other.getTabela());
		umEqualsBuilder.append(this.getCodProcedimento(), other.getCodProcedimento());
		umEqualsBuilder.append(this.getDescrProcedimento(), other.getDescrProcedimento());
		umEqualsBuilder.append(this.getQtde(), other.getQtde());

		return umEqualsBuilder.isEquals();
	}

}