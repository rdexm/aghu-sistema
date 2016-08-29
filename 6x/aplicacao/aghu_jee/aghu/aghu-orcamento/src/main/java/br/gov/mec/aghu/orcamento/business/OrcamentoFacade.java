package br.gov.mec.aghu.orcamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.orcamento.dao.FsoConveniosFinanceiroDAO;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoPrevisaoOrcamentariaDAO;


@Stateless
public class OrcamentoFacade extends BaseFacade implements Serializable, IOrcamentoFacade {

@Inject
private FsoConveniosFinanceiroDAO fsoConveniosFinanceiroDAO;

@Inject
private FsoPrevisaoOrcamentariaDAO fsoPrevisaoOrcamentariaDAO;

@Inject
private FsoNaturezaDespesaDAO fsoNaturezaDespesaDAO;

	private static final long serialVersionUID = 8852351198803964288L;

	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#listarFsoConveniosAtiva(java.lang.Object)
	 */
	@Override
	public List<FsoConveniosFinanceiro> listarFsoConveniosAtiva(Object pesquisa)
			throws BaseException {
		return getFsoConveniosFinanceiroDAO().listarFsoConveniosAtiva(pesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#obterConvenioPorChave(java.lang.Integer)
	 */
	@Override
	public FsoConveniosFinanceiro obterConvenioPorChave(Integer _seq) {
		return this.getFsoConveniosFinanceiroDAO().obterPorChavePrimaria(_seq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#listarConvenios(java.lang.Object)
	 */
	@Override
	public List<FsoConveniosFinanceiro> listarConvenios(Object objPesquisa) {
		return getFsoConveniosFinanceiroDAO().listarConvenios(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#listarConveniosCount(java.lang.Object)
	 */
	@Override
	public Long listarConveniosCount(Object objPesquisa) {
		return getFsoConveniosFinanceiroDAO().listarConveniosCount(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#listarNaturezaDespesa(java.lang.Object)
	 */
	@Override
	public List<FsoNaturezaDespesa> listarNaturezaDespesa(Object objPesquisa) {
		return getFsoNaturezaDespesaDAO().listarNaturezaDespesa(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#listarNaturezaDespesaCount(java.lang.Object)
	 */
	@Override
	public Long listarNaturezaDespesaCount(Object objPesquisa) {
		return getFsoNaturezaDespesaDAO().listarNaturezaDespesaCount(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#obterFsoNaturezaDespesaPorChavePrimaria(br.gov.mec.aghu.model.FsoNaturezaDespesaId)
	 */
	@Override
	public FsoNaturezaDespesa obterFsoNaturezaDespesaPorChavePrimaria(
			FsoNaturezaDespesaId fsoNaturezaDespesaId) {
		return this.getFsoNaturezaDespesaDAO().obterPorChavePrimaria(
				fsoNaturezaDespesaId);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.orcamento.business.IOrcamentoFacade#obterFsoConveniosFinanceiroPorChavePrimaria(java.lang.Integer)
	 */
	@Override
	public FsoConveniosFinanceiro obterFsoConveniosFinanceiroPorChavePrimaria(
			Integer codigo) {
		return this.getFsoConveniosFinanceiroDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public BigDecimal obterSaldoOrcamento(final Integer codigo) {
		return this.getFsoPrevisaoOrcamentariaDAO().obterSaldoOrcamento(codigo);
	}

	protected FsoConveniosFinanceiroDAO getFsoConveniosFinanceiroDAO() {
		return fsoConveniosFinanceiroDAO;
	}

	protected FsoNaturezaDespesaDAO getFsoNaturezaDespesaDAO() {
		return fsoNaturezaDespesaDAO;
	}

	protected FsoPrevisaoOrcamentariaDAO getFsoPrevisaoOrcamentariaDAO() {
		return fsoPrevisaoOrcamentariaDAO;
	}

}
