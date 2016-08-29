package br.gov.mec.aghu.exames.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IExamesBeanFacade extends Serializable {

	void recepcionarPaciente(AelItemSolicitacaoExames itemSolicitacaoExames, final DominioTipoTransporteUnidade transporte,
			final Boolean indUsoO2Un, String nomeMicrocomputador) throws BaseException;

	SolicitacaoExameVO gravaSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador) throws BaseException;

	List<ItemContratualizacaoVO> gravaSolicitacaoExameContratualizacao(AelSolicitacaoExames aelSolicitacaoExames,
			List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador) throws BaseException;

	boolean voltarSituacaoAmostraSolicitacao(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador)
			throws BaseException;

	boolean voltarAmostra(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador) throws BaseException;

	void receberItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException;

	void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException;
	
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException;

	void voltarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException;

	void inserirAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException;

	void excluirItensExamesSelecionados(List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador) throws BaseException;

	void estornarItemSolicitacaoExame(AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException;

	AelSolicitacaoExames atualizarSolicitacaoExame(AelSolicitacaoExames solicExame, List<AelItemSolicitacaoExames> itemSolicExameExcluidos,
			String nomeMicrocomputador) throws BaseException;

	void removerDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	void anexarDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	void processarComunicacaoModuloGestaoAmostra(String nomeMicrocomputador) throws BaseException;

	void removerAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException;

	void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException;

	void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException;

	ImprimeEtiquetaVO receberAmostra(AghUnidadesFuncionais unidadeExecutora,
			AelAmostras amostra, String nroFrascoFabricante,
			List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador) throws BaseException;

	void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, AelMotivoCancelaExames motivoCancelar, String nomeMicrocomputador)
			throws BaseException;

	ImprimeEtiquetaVO receberAmostraSolicitacao(
			AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra,
			List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador) throws BaseException;


}