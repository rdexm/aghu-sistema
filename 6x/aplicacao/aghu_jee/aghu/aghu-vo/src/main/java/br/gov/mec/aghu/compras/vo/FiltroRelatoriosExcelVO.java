package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDividaHospitalar;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class FiltroRelatoriosExcelVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4833198944388686014L;


	private Date dataInicioDivida;
	private Date dataFimDivida;
	private ScoFornecedor fornecedor;
	private DominioDividaHospitalar dominioDividaHospitalar;
	
	public Date getDataInicioDivida() {
		return dataInicioDivida;
	}
	public void setDataInicioDivida(Date dataInicioDivida) {
		this.dataInicioDivida = dataInicioDivida;
	}
	public Date getDataFimDivida() {
		return dataFimDivida;
	}
	public void setDataFimDivida(Date dataFimDivida) {
		this.dataFimDivida = dataFimDivida;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public DominioDividaHospitalar getDominioDividaHospitalar() {
		return dominioDividaHospitalar;
	}
	public void setDominioDividaHospitalar(
			DominioDividaHospitalar dominioDividaHospitalar) {
		this.dominioDividaHospitalar = dominioDividaHospitalar;
	} 
	
	

}
