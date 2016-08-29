package br.gov.mec.aghu.compras.contaspagar.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioMovimentacaoFornecedorVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioMovimentacaoFornecedorON extends BaseBusiness {

	private static final long serialVersionUID = 6706028802320258202L;

	@EJB
	private RelatorioMovimentacaoFornecedorRN relatorioMovimentacaoFornecedorRN;

	/**
	 * Retorna a coleção de dados para o relatório
	 * 
	 * @param fornecedor
	 *            dados referente ao fornecedor
	 * @return coleção de dados do relatório
	 * @throws ApplicationBusinessException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<RelatorioMovimentacaoFornecedorVO> pesquisaMovimentacaoFornecedor(
			Object fornecedor, Date dataInicio, Date dataFim)
			throws ApplicationBusinessException, IllegalAccessException,
			InvocationTargetException {
		return this.relatorioMovimentacaoFornecedorRN
				.pesquisaMovimentacaoFornecedor(fornecedor, dataInicio, dataFim);
	}

	/**
	 * Retorna o objeto com dados do hospital
	 * 
	 * @param Parametro
	 *            dados do hospital
	 * @return objeto
	 */
	public AghParametros pesquisarHospital(Object Parametro) {
		return this.relatorioMovimentacaoFornecedorRN
				.pesquisarHospital(Parametro);
	}
	
	/**
	 * Envia um email com o relatório em anexo.
	 * 
	 * @param contatoEmail
	 * @param jasper
	 * @throws ApplicationBusinessException
	 */
	public void enviarEmailMovimentacaoFornecedor(String contatoEmail, byte[] jasper) throws ApplicationBusinessException {
		this.relatorioMovimentacaoFornecedorRN.enviarEmailMovimentacaoFornecedor(contatoEmail, jasper);
	}

	@Override
	protected Log getLogger() {
		return null;
	}

}
