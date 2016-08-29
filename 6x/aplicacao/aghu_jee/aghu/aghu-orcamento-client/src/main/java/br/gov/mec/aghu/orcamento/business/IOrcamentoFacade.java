package br.gov.mec.aghu.orcamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IOrcamentoFacade extends Serializable {

	public abstract List<FsoConveniosFinanceiro> listarFsoConveniosAtiva(
			Object pesquisa) throws BaseException;

	public abstract FsoConveniosFinanceiro obterConvenioPorChave(Integer _seq);

	public abstract List<FsoConveniosFinanceiro> listarConvenios(
			Object objPesquisa);

	public abstract Long listarConveniosCount(Object objPesquisa);

	public BigDecimal obterSaldoOrcamento(final Integer codigo);

	public abstract List<FsoNaturezaDespesa> listarNaturezaDespesa(
			Object objPesquisa);

	public abstract Long listarNaturezaDespesaCount(Object objPesquisa);

	public abstract FsoNaturezaDespesa obterFsoNaturezaDespesaPorChavePrimaria(
			FsoNaturezaDespesaId fsoNaturezaDespesaId);

	public abstract FsoConveniosFinanceiro obterFsoConveniosFinanceiroPorChavePrimaria(
			Integer codigo);

}