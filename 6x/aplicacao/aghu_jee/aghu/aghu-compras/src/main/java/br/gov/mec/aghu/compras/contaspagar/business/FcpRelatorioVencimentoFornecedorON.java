package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FcpRelatorioVencimentoFornecedorON extends BaseBusiness{

	private static final long serialVersionUID = 6706028802320258202L;
	
	@EJB
	private FcpRelatorioVencimentoFornecedorRN relatorioVencimentoFornecedorRN;
	
	/**
	 * Retorna a coleção de dados para o relatório	
	 * @param fornecedor dados referente ao fornecedor
	 * @return coleção de dados do relatório
	 */
	public List<DatasVencimentosFornecedorVO> pesquisarDatasVencimentoFornecedor(Object fornecedor) {
		return this.getRelatorioVencimentosFornecedorRN().pesquisarDatasVencimentoFornecedor(fornecedor);
	}
	
	/**
	 * Retorna o objeto com dados do hospital
	 * @param Parametro dados do hospital
	 * @return objeto
	 */
	public AghParametros pesquisarHospital(Object Parametro) {
		return this.getRelatorioVencimentosFornecedorRN().pesquisarHospital(Parametro);
	}

	public FcpRelatorioVencimentoFornecedorRN getRelatorioVencimentosFornecedorRN() {
		return relatorioVencimentoFornecedorRN;
	}

	public void setRelatorioVencimentosFornecedorRN(FcpRelatorioVencimentoFornecedorRN relatorioVencimentosFornecedorRN) {
		this.relatorioVencimentoFornecedorRN = relatorioVencimentosFornecedorRN;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	

}
