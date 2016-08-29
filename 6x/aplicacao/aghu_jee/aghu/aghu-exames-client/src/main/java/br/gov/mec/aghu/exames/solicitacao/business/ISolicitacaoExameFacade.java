package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.vo.AbasIndicadorApresentacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ConfirmacaoImpressaoEtiquetaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;
import br.gov.mec.aghu.exames.solicitacao.vo.HistoricoNumeroUnicoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameItemVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameResultadoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.TipoLoteVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.exames.vo.ExamesCriteriosSelecionadosVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exames.vo.ImprimirExamesRealizadosAtendimentosDiversosVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.exames.vo.SolicitacoesAgendaColetaAmbulatorioVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.view.VAelArcoSolicitacaoAghu;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

public interface ISolicitacaoExameFacade extends Serializable {

	/**
	 * @param valor
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.RelatorioMateriaisColetarRN#buscarAghUnidadesFuncionaisPorParametro(java.lang.String)
	 */
	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(
			final String valor);

	/**
	 * @param filtro
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.RelatorioMateriaisColetarRN#buscaMateriaisColetarInternacao(br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO)
	 */
	public List<SolicitacaoColetarVO> buscaMateriaisColetarInternacao(
			final RelatorioMateriaisColetarInternacaoFiltroVO filtro, String nomeMicrocomputador)
			throws BaseException;
	
	/**
	 * Pesquisa as solicitações de exames que estam agendadas para o dia/unidade de coleta e grade de agendamento.
	 * @param filtro
	 * @return
	 */
	public List<SolicitacoesAgendaColetaAmbulatorioVO> pesquisarAgendaColetaAmbulatorio(final RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws BaseException;

	/**
	 * @param atendimento
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#recuperarLocalPaciente(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	public String recuperarLocalPaciente(final AghAtendimentos atendimento);

	/**
	 * @param atendimento
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#excluirSolicitacaoExamesPorAtendimento(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	public void excluirSolicitacaoExamesPorAtendimento(
			final AghAtendimentos atendimento);

	/**
	 * @param soeSeq
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#atualizarIndImpressaoSolicitacaoExames(java.lang.Integer)
	 */
	public void atualizarIndImpressaoSolicitacaoExames(final Integer soeSeq, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * @param solicitacaoExameVO
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#gravar(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	public SolicitacaoExameVO gravar(final SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador)
			throws BaseException;

	/*
	 * @param seqSolicExame
	 * A geração de exames com AelSitItemSolicitacoes = "PE" necessita que após a inclusão de seus itens com situação definida 
	 * pelo usuário seja gerado um novo registro em AEL_EXTRATO_ITEM_SOLICS com situação Pendente para cada item e estes tenham sua situação atualizada
	 */
	public void finalizarGeracaoSolicExamePendente(Integer seqSolicExame) throws BaseException;
	
	/**
	 * @param solicitacaoExameVO
	 * @return 
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#gravar(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	public List<ItemContratualizacaoVO> gravarContratualizacao(
			final AelSolicitacaoExames aelSolicitacaoExames,
			final List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * @param solicitacaoExameVO
	 * @throws BaseException
	 */
	public List<String> executarValidacoesPosGravacaoSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO,
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			List<ConfirmacaoImpressaoEtiquetaVO> listaConfirmaImpressaoEtiquetas, AghUnidadesFuncionais unidadeExecutora)
			throws BaseException;

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarServidoresSolicitacaoExame(java.lang.String)
	 */
	
	public List<RapServidores> buscarServidoresSolicitacaoExame(
			final String objPesquisa);

	/**
	 * @param atendimentoSeq
	 * @param atendimentoDiversoSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaSolicitacaoExameVO(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	
	public SolicitacaoExameVO buscaSolicitacaoExameVO(
			final Integer atendimentoSeq, final Integer atendimentoDiversoSeq)
			throws ApplicationBusinessException;

	/**
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarItensExames(java.lang.Integer)
	 */
	public List<AelItemSolicitacaoExames> buscarItensExames(final Integer seq)
			throws ApplicationBusinessException;

	/**
	 * @param seq
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarItensExamesAExecutar(java.lang.Integer,
	 *      java.lang.Short)
	 */
	public List<AelItemSolicitacaoExames> buscarItensExamesAExecutar(
			final Integer seq, final Short unfSeq)
			throws ApplicationBusinessException;

	/**
	 * @param soeSeq
	 * @param itemSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarAmostrasItemExame(java.lang.Integer,
	 *      java.lang.Short)
	 */
	public List<AelAmostraItemExames> buscarAmostrasItemExame(
			final Integer soeSeq, final Short itemSeq)
			throws ApplicationBusinessException;

	/**
	 * @param soeSeq
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaExameCancelamentoSolicRespons(java.lang.Integer)
	 */
	public VAelSolicAtendsVO buscaExameCancelamentoSolicRespons(
			final Integer soeSeq) throws BaseException;

	/**
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaDetalhesSolicitacaoExameVO(java.lang.Integer)
	 */
	public SolicitacaoExameVO buscaDetalhesSolicitacaoExameVO(final Integer seq)
			throws ApplicationBusinessException;

	/**
	 * @param unidadeExecutora
	 * @param dtSolicitacao
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#listarSolicitacaoExamesUnExecutora(br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      java.util.Date)
	 */
	public List<VAelSolicAtendsVO> listarSolicitacaoExamesUnExecutora(
			final AghUnidadesFuncionais unidadeExecutora,
			final Date dtSolicitacao) throws BaseException;

	/**
	 * @param atendimentoSeq
	 * @return 
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#verificarPermissoesParaSolicitarExame(java.lang.Integer)
	 */
	public AghAtendimentos verificarPermissoesParaSolicitarExame(
			final Integer atendimentoSeq, boolean origemSumarioAlta) throws BaseException;

	/**
	 * @param atendimentoSeq
	 * @return 
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#verificarPermissoesParaSolicitarExame(java.lang.Integer)
	 */
	public AghAtendimentos verificarPermissoesParaSolicitarExame(
			final Integer atendimentoSeq) throws BaseException;
	/**
	 * @param solicitacoes
	 * @param unidadeExecutora
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#imprimirSolicitacoesColetar(java.util.List,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	public List<SolicitacaoColetarVO> imprimirSolicitacoesColetar(
			final List<Integer> solicitacoes,
			final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * @param vinculo
	 * @param matricula
	 * @param diasServidorFimVinculoPermitidoSolicitarExame
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#pesquisarQualificacoesSolicitacaoExameSemPermissao(java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	public List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(
			final Short vinculo, final Integer matricula,
			final Integer diasServidorFimVinculoPermitidoSolicitarExame);

	/**
	 * @param filter
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#verificarFiltrosPesquisaSolicitacaoExame(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	
	public void verificarFiltrosPesquisaSolicitacaoExame(
			final SolicitacaoExameFilter filter)
			throws ApplicationBusinessException;

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteTotalRegistros(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	
	public Long pesquisarAtendimentosPacienteTotalRegistros(
			final SolicitacaoExameFilter filter) throws BaseException;


	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteInternadoCount(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	public Long pesquisarAtendimentosPacienteInternadoCount(
			SolicitacaoExameFilter filtro) throws BaseException;

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteUnico(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	
	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteUnico(
			final SolicitacaoExameFilter filter) throws BaseException;

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteInternadoUnico(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	
	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteInternadoUnico(
			final SolicitacaoExameFilter filter) throws BaseException;

	/**
	 * @param filter
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPaciente(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter,
	 *      java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	
	public SolicitacaoExameResultadoVO pesquisarAtendimentosPaciente(
			final SolicitacaoExameFilter filter, final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc) throws BaseException;

	/**
	 * @param filtro
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarExamesCancelamentoSolicitante(br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO)
	 */
	public SolicitacaoExameResultadoVO listarExamesCancelamentoSolicitante(
			final PesquisaExamesFiltroVO filtro) throws BaseException;

	/**
	 * @param parametro
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#buscarUnidadeFuncionais(java.lang.String)
	 */
	
	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(
			final String parametro);
	
	public Long buscarUnidadeFuncionaisCount(
			final String parametro);	

	/**
	 * @param seq
	 * @param exame
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarPesquisaIntervaloColeta(java.lang.String,
	 *      br.gov.mec.aghu.model.AelUnfExecutaExames)
	 */
	public List<AelTmpIntervaloColeta> listarPesquisaIntervaloColeta(
			final String seq, final AelUnfExecutaExames exame);

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarRegiaoAnatomica(java.lang.String)
	 */
	public List<AelRegiaoAnatomica> listarRegiaoAnatomica(
			final String objPesquisa, final List<Integer> regioesMama);

	/**
	 * @param dominio
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#getDadosLote(br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote)
	 */
	public List<TipoLoteVO> getDadosLote(
			final DominioSolicitacaoExameLote dominio);

	/**
	 * @param loteExameUsual
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#inserirAelLoteExameUsual(br.gov.mec.aghu.model.AelLoteExameUsual)
	 */
	
	public void inserirAelLoteExameUsual(final AelLoteExameUsual loteExameUsual)
			throws ApplicationBusinessException, BaseException;

	/**
	 * @param loteExameUsual
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#atualizarAelLoteExameUsual(br.gov.mec.aghu.model.AelLoteExameUsual)
	 */
	
	public void atualizarAelLoteExameUsual(
			final AelLoteExameUsual loteExameUsual)
			throws ApplicationBusinessException, BaseException;

	/**
	 * @param loteSeq
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#removerAelLoteExameUsual(java.lang.Short)
	 */
	
	public void removerAelLoteExameUsual(final Short loteSeq)
			throws BaseException;

	/**
	 * @param itens
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#cancelarItensResponsabilidadeSolicitante(java.util.List)
	 */
	public void cancelarItensResponsabilidadeSolicitante(
			final List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * @param item
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizar(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	public AelItemSolicitacaoExames atualizar(
			final AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException;

	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException;
	
	/**
	 * @param item
	 * @param atualizarItemAmostra
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizar(br.gov.mec.aghu.model.AelItemSolicitacaoExames, boolean)
	 */
	public AelItemSolicitacaoExames atualizar(	
			final AelItemSolicitacaoExames item,
			final boolean atualizarItemAmostra, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;
		
	/**
	 * @param item
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizarSemFlush(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	public AelItemSolicitacaoExames atualizarSemFlush(
			final AelItemSolicitacaoExames item, String nomeMicrocomputador, final Boolean flush) throws BaseException;

	/**
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#receberItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	public void receberItemSolicitacaoExame(
			final AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#voltarItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	public void voltarItemSolicitacaoExame(
			final AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * @param itemEstorno
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#estornarItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	public void estornarItemSolicitacaoExame(
			final AelItemSolicitacaoExames itemEstorno, String nomeMicrocomputador) throws BaseException;

	/**
	 * @param itemSolicitacaoExameVO
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#validarItemSolicitacaoExameErros(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	public void validarItemSolicitacaoExameErros(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVO, final Map<String,Object> questionarioSismama)
			throws BaseException;

	public void gerarDependentes(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final AghUnidadesFuncionais unfTrabalho) throws BaseException;

	/**
	 * @param itemSolicitacaoExameVO
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#validarItemSolicitacaoExameMensagens(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	public BaseListException validarItemSolicitacaoExameMensagens(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVO)
			throws BaseException;

	/**
	 * @param unfSolicitante
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#verificarUrgenciaItemSolicitacaoExame(br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	public Boolean verificarUrgenciaItemSolicitacaoExame(
			final AghUnidadesFuncionais unfSolicitante);

	/**
	 * @param unfSolicitante
	 * @param atendimento
	 * @param atendimentoDiverso
	 * @param unfExecutaExame
	 * @param unfTrabalho
	 * @param itemSolicEx
	 * @param solicitacaoExameVo
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterSituacaoExameSugestao(br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AghAtendimentos, br.gov.mec.aghu.model.AelAtendimentoDiversos, br.gov.mec.aghu.model.AelUnfExecutaExames, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO, br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	public AelSitItemSolicitacoes obterSituacaoExameSugestao(
			final AghUnidadesFuncionais unfSolicitante,
			final AghAtendimentos atendimento,
			final AelAtendimentoDiversos atendimentoDiverso,
			final AelUnfExecutaExames unfExecutaExame,
			final AghUnidadesFuncionais unfTrabalho,
			final ItemSolicitacaoExameVO itemSolicEx,
			final SolicitacaoExameVO solicitacaoExameVo)
			throws BaseException;

	/**
	 * @param unfExecutaExame
	 * @param unfSolicitante
	 * @param origem
	 * @param atdDivSeq 
	 * @param atdSeq 
	 * @param indGeradoAutomaticamente 
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterIndicadorApresentacaoAbas(br.gov.mec.aghu.model.AelUnfExecutaExames, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.dominio.DominioOrigemAtendimento)
	 */
	public AbasIndicadorApresentacaoVO obterIndicadorApresentacaoAbas(
			final AelUnfExecutaExames unfExecutaExame,
			final AghUnidadesFuncionais unfSolicitante,
			final AghUnidadesFuncionais unfTrabalho,
			final DominioOrigemAtendimento origem, 
			final AelSolicitacaoExames solicitacao, 
			final Integer atdSeq, 
			final Integer atdDivSeq, 
			final Boolean indGeradoAutomatico, 
			final Boolean includeUnidadeTrabalho,
			final DominioTipoTransporteQuestionario tipoTransporte) throws BaseException;
	
	public AghUnidadesFuncionais obterUnidadeTrabalhoSolicitacaoExame(final SolicitacaoExameVO solicitacaoExame) throws BaseException;

	public Boolean mostrarUnidadeTrabalhoSolicitacaoExame(final SolicitacaoExameVO solicitacaoExame) throws BaseException;
	
	/**
	 * Busca o médico responsável pela consulta ou pela equipe pelo número da consulta
	 * 
	 * @param numeroConsulta
	 * @return
	 */
	public RapServidores buscarResponsavelConsultaOuEquipe(Integer numeroConsulta);
	
	
	/**
	 * @param nomeExame
 	 * @param seqUnidade
	 * @param isSus
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisaUnidadeExecutaSinonimoExame(java.lang.String)
	 */
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(
			final String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, DominioTipoPesquisaExame tipoPesquisa);
	
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(
			final String nomeExame, DominioTipoPesquisaExame tipoPesquisa);
	
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameAntigo(
		final String nomeExame);
	
	/**
	 * @param leuSeq
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisaUnidadeExecutaSinonimoExameLote(java.lang.Short)
	 */
	
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(
			final Short leuSeq, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao);

	/**
	 * @param itemSolicitacaoExameVO
	 * @param unidadeSolicitante
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#verificarCampoDataHora(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	public TipoCampoDataHoraISE verificarCampoDataHora(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final AghUnidadesFuncionais unidadeSolicitante)
			throws ApplicationBusinessException;

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param ordem
	 * @param b
	 * @param aelUnfExecutaExames
	 * @param aelSolicitacaoExames
	 * @param aelSitItemSolicitacoes
	 * @param fatConvenioSaude
	 * @param codigoPaciente
	 * @param prontuario2
	 * @param nomePaciente2
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AelSolicitacaoExames, br.gov.mec.aghu.model.AelSitItemSolicitacoes, br.gov.mec.aghu.model.FatConvenioSaude, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public List<ExamesCriteriosSelecionadosVO> pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(
			final Integer firstResult, final Integer maxResult,
			final String ordem, final boolean b,
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude,
			final String codigoPaciente, final Integer prontuario2,
			final String nomePaciente2);

	/**
	 * @param aelUnfExecutaExames
	 * @param aelSolicitacaoExames
	 * @param aelSitItemSolicitacoes
	 * @param fatConvenioSaude
	 * @param codigoPaciente
	 * @param prontuario
	 * @param nomePaciente
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AelSolicitacaoExames, br.gov.mec.aghu.model.AelSitItemSolicitacoes, br.gov.mec.aghu.model.FatConvenioSaude, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public Long pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(
			final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames,
			final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude,
			final String codigoPaciente, final Integer prontuario,
			final String nomePaciente);

	/**
	 * @param itemSolicitacaoExameVO
	 * @param unidadeSolicitante
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#getHorariosRotina(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	public List<DataProgramadaVO> getHorariosRotina(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final AghUnidadesFuncionais unidadeSolicitante)
			throws BaseException;

	/**
	 * @param solicitacaoExameSeq
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#listarItensExameCancelamentoSolicitante(java.lang.Integer)
	 */
	public List<ItemSolicitacaoExameCancelamentoVO> listarItensExameCancelamentoSolicitante(
			final Integer solicitacaoExameSeq) throws BaseException;

	/**
	 * @param itemSolicitacaoExamePai
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterDependentesOpcionais(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionais(
			final ItemSolicitacaoExameVO itemSolicitacaoExamePai);

	/**
	 * @param itemSolicitacaoExameVo
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterDependentesOpcionaisSelecionados(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionaisSelecionados(
			final ItemSolicitacaoExameVO itemSolicitacaoExameVo);

	/**
	 * @param solicitacaoExame
	 * @param amoSeqp
	 * @param unidadeTrabalho
	 * @param impressoraCups
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.EtiquetasON#gerarEtiquetas(br.gov.mec.aghu.model.AelSolicitacaoExames, java.lang.Short, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.cups.ImpImpressora)
	 */
	public Integer gerarEtiquetas(final AelSolicitacaoExames solicitacaoExame,
			final Short amoSeqp, final AghUnidadesFuncionais unidadeTrabalho,
			final String impressoraCups, String situacaoItemExame) throws BaseException;
	
	public VAelArcoSolicitacaoAghu obterVAelArcoSolicitacaoAghuPeloId(
			final Integer seq);

	public Integer calcularHorasUteisEntreDatas(final Date d1, final Date d2);

	public Integer calcularDiasUteisEntreDatas(final Date d1, final Date d2);

	public List<HistoricoNumeroUnicoVO> listarHistoricoNroUnico(
			final Integer soeSeq, final Short seqp, Boolean isHist);

	public String reimprimirAmostra(final AghUnidadesFuncionais unidadeExecutora,
			final Integer amostraSoeSeqSelecionada,
			final Short amostraSeqpSelecionada, final String nomeMicro, final String nomeImpressora) throws BaseException;

	public String gerarZPLNumeroAp(final String nroAp);

	public AelSolicitacaoExames obterSolicitacaoExame(Integer soeSeq);

	List<AelItemSolicitacaoExames> buscarItensSolicitacaoExamePorAtendimentoParamentro(Integer atdSeq, List<String> situacao);
	
	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExamesPorSolicitacao(final Integer soeSeq);
	
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimento(
			Integer atdSeq, String exaSigla, String[] situacao);
	
	AelSolicitacaoExames obterAelSolicitacaoExamePorAtdSeq(Integer atdSeq);
	
	List<AelItemSolicitacaoExames> obterAelItemSolicitacaoExamesPorUfeEmaManSeqSoeSeq(Integer seq, Integer soeSeq);
	
	public String gerarEtiquetaEnvelopePaciente(String nome, Integer solicitacao, String unidadeExecutora, String data, Integer prontuario);

	AelMotivoCancelaExames obterAelMotivoCancelaExames(Short seq);

	AelSitItemSolicitacoes obterAelSitItemSolicitacoes(String codigo);

	public List<ImprimirExamesRealizadosAtendimentosDiversosVO> imprimirExamesRealizadosAtendimentosDiversos(
			Date dataInicial, Date dataFinal, DominioSimNao grupoSus,
			FatConvenioSaude convenioSaude) throws ApplicationBusinessException;

	public void atualizarItensPendentesAmbulatorio(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;
	
	void gravar(AelSolicitacaoExames solicitacaoExame,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	public Map<String, Object> inicializarMapSismama();

	RapServidores buscarConsulta(SolicitacaoExameVO solicitacaoExameVO) throws BaseException;
	
	public boolean inserirFilaExamesLiberados(final AghJobDetail job);
	
	public List<AelItemSolicitacaoExames> buscarItemExamesLiberadosPorGrupo(final Integer soeSeq, final Integer grupo);

	public String gerarXmlEnvioExameInternet(final List<AelItemSolicitacaoExames> listaItensAgrupados, final Integer seqExameInternetGrupo) throws BaseException;
	
	public void inserirFilaLaudoExames(final AelSolicitacaoExames solicitacaoExame, final List<AelItemSolicitacaoExames> listItemSolicitacao, final Integer seqGrupo, final byte[] arquivoLaudo, final String arquivoXml);	
	
	public String gerarToken() throws BaseException;
	
	public ByteArrayOutputStream buildResultadoPDF(final AelSolicitacaoExames solicitacaoExame, final Integer seqGrupoExame, final String token) throws IOException, BaseException;
	
	public String obterSolicitanteExame(AelExameInternetStatus exameInternetStatus);
	
	public boolean inserirFilaExamesLiberados(final MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO, final boolean isReenvio);
	
	public void inserirStatusInternet(final AelSolicitacaoExames solicitacaoExame, final AelItemSolicitacaoExames itemSolicitacaoExame,
			final Date dataStatus, final DominioSituacaoExameInternet situacao, final DominioStatusExameInternet status, final String mensagem, final RapServidores servidor);

	public void atualizarStatusInternet(final Integer soeSeq, final Integer grupoSeq,
			final DominioStatusExameInternet statusAtualiza, final DominioSituacaoExameInternet situacao,
			final DominioStatusExameInternet statusNovo, final String mensagemErro );
	
	Boolean gerarExameProvaCruzadaTransfusional(AghAtendimentos atendimento,
			MbcCirurgias cirurgia, String nomeMicrocomputador, RapServidores responsavel, Boolean validaHemocomponente)
			throws BaseException;
	
	void cancelarExameProvaCruzadaTransfusional(AghAtendimentos atendimento,
			MbcCirurgias cirurgia, String nomeMicrocomputador) throws BaseException;

	boolean verificarCodigoSituacaoNumeroAP(final AelItemSolicitacaoExames itemSolicitacaoExame,
			final AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException;

	//Long pesquisaUnidadeExecutaSinonimoExameCount(String nomeExame,Short seqUnidade,Boolean isSus, Boolean isOrigemInternacao,Integer seqAtendimento, boolean filtrarExamesProcEnfermagem);
	
	String gerarZPLEtiquetaNumeroExame(ImprimeEtiquetaVO imprimeEtiquetaVO) throws BaseException;

	boolean verificaPctPendente(Integer atdSeqSelecionado, Integer crgSeq);

	boolean verificaPctRealizado(Integer pacCodigoSelecionado);

	Boolean verificaDiaPlantao();

	AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimentoCirurgico(Integer atdSeq, Integer crgSeq, String exaSigla,
			String[] situacao, Boolean geradoAutomatico);
	
	public Boolean verificaConvenioSus(AghAtendimentos atendimento,
			AelAtendimentoDiversos atendimentoDiverso);
	
	public ConfirmacaoImpressaoEtiquetaVO verificarImpressaoEtiqueta(
			final SolicitacaoExameVO solicitacaoExameVO,
			final AghMicrocomputador microcomputador) throws ApplicationBusinessException;

	List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(
			Short leuSeq, short seqUnidade, Boolean isSus, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao);

	List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(
			Short leuSeq, List<UnfExecutaSinonimoExameVO> listaExames, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao);
	
	
	Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException;
	
	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	AelSolicitacaoExames buscarUltimaSolicitacaoExames(Integer atdSeq) throws ApplicationBusinessException;
	
	/**
	 *  Serviço para verificar se o atendimento do Recém Nascido possui alguma solicitação de exame. Serviço #42021
	 * @param atdSeq
	 * @param situacoes
	 * @return List<SolicitacaoExamesVO>
	 */
	List<AelSolicitacaoExames> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String ... situacoes);
	
	List<AelRecomendacaoExame> verificarRecomendacaoExameQueSeraoExibidas(List<AelRecomendacaoExame> aelRecomendacaoExame,ItemSolicitacaoExameVO itemSolicitacaoExameVO);

	public Long buscarServidoresSolicitacaoExameCount(String objPesquisa);
	
	public Boolean verificarSeExameSendoSolicitadoRedome(AelItemSolicitacaoExames itemSolicitacaoExame, AghUnidadesFuncionais aghUnidadeFuncional) throws BaseException;

	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException;

    public void gravarExtratoDoadorRedome(Integer soeSeq) throws BaseException;

    public Boolean validaUnidadeSolicitanteSus(Short unfSeq);

	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(
			final String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean buscaCompleta, DominioTipoPesquisaExame tipoPesquisa);

	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(final Integer soeSeq, final Short seqp);
	public void processarExameInternet(AghJobDetail job);

	void reenviarExameParaPortal(MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO);

	public String obterNomeImpressoraEtiquetasRedome(String nomeMicro) throws ApplicationBusinessException,	UnknownHostException;
	AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item,
			String nomeMicrocomputador, AelItemSolicitacaoExames itemOriginal)
			throws BaseException;
	
	AelSolicitacaoExames atualizar(AelSolicitacaoExames solicExame,
			List<AelItemSolicitacaoExames> itemSolicExameExcluidos,
			String nomeMicrocomputador, RapServidores servidorLogado) throws BaseException;

	public Boolean verificarExamePodeSolicitarUrgente(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short unfSeqSolicitante);

	AelItemSolicitacaoExames atualizar(AelItemSolicitacaoExames item,
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal,
			boolean atualizarItemAmostra, String nomeMicrocomputador)
			throws BaseException;

	String obterNomeImpressoraEtiquetas(String nomeMicro) throws BaseException;

	public ImpImpressora obterImpressoraEtiquetas(String nomeMicro);
	void atualizarItemSolicitacaEmColeta(Integer soeSeq, Short seqp, String nomeMicrocomputador) throws BaseException;

	String obterDescricaoUsualExame(Integer soeSeq, Short seqp);

	Boolean verificarUsuarioLogadoColetador(String login);
	Boolean verificarUsuarioSolicitanteColetador(Integer soeSeq, Short seqp);

	List<AelAmostraItemExames> buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao);

	void atualizarAmostraSolicitacaEmColeta(Integer iseSoeSeq, Short iseSeqp, Integer amoSoeSeq, Integer amoSeqp,
			String nomeMicrocomputador) throws BaseException;

	List<AelItemSolicitacaoExames> buscarItensPorAmostra(Integer soeSeq, Integer amoSeqp);
}