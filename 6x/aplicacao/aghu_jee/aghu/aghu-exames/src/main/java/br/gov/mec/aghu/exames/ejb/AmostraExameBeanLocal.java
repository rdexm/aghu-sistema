package br.gov.mec.aghu.exames.ejb;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface AmostraExameBeanLocal {
	ImprimeEtiquetaVO receberAmostra(final AghUnidadesFuncionais unidadeExecutora, 
			final AelAmostras amostra, final String nroFrascoFabricante, 
			List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) 
			throws BaseException;

	ImprimeEtiquetaVO receberAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador)
			throws BaseException;

	boolean voltarAmostra(AghUnidadesFuncionais unidadeExecutora,
			AelAmostras amostra, String nomeMicrocomputador) throws BaseException;

	boolean voltarSituacaoAmostraSolicitacao(
			AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra,
			String nomeMicrocomputador)
			throws BaseException;

	// #25907
	void atualizarSituacaoExamesAmostra(AelAmostras amostra,
			String nomeMicrocomputador)
			throws BaseException;

	// #25907
	void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra,
			String nomeMicrocomputador)
			throws BaseException;
}