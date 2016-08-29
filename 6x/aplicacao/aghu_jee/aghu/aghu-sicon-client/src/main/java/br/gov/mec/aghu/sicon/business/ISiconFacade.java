package br.gov.mec.aghu.sicon.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoConvItensContratoId;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.vo.AutorizacaoFornecimentoVO;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.LicitacaoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ListaDetalhesItensLicVO;
import br.gov.mec.aghu.sicon.vo.ListaLicitacaoVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ISiconFacade extends Serializable {

	void inserirContratoManual(ScoContrato contratoManual) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void inserirContratoAutomatico(ScoContrato contratoManual) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void alterarContratoManual(ScoContrato contratoManual) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void alterarContratoAutomatico(ScoContrato contratoAutomatico) throws ApplicationBusinessException,
			ApplicationBusinessException;

	void deletarContrato(ScoContrato input) throws ApplicationBusinessException;

	List<ScoConvItensContrato> listarConvItensContratos(ScoItensContrato input)
			throws BaseException;

	List<ScoItensContrato> listarItensContratos(ScoContrato input)
			throws BaseException;

	boolean possuiItensContrato(ScoContrato input);

	ScoResContrato getRescicaoContrato(ScoContrato input)
			throws BaseException;

	ScoContrato getContrato(Integer seq) throws BaseException;

	ScoContrato obterContratoPorNumeroContrato(Long nrContrato);

	void desatacharContrato(ScoContrato contrato);

	void desatacharItemContrato(ScoItensContrato itemContrato);

	List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa)
			throws ApplicationBusinessException;

	void inserirAtualizarRescicao(ScoResContrato resContrato) throws BaseException;

	List<ScoAditContrato> listarAditivosByContrato(ScoContrato input);

	ScoAditContrato getAditContratoBySeq(ScoAditContratoId id);

	void excluirAditContrato(ScoAditContratoId id)
			throws ApplicationBusinessException;

	List<RapServidores> pesquisarServidorAtivoGestorEFiscalContrato(
			Object paramPesquisa) throws BaseException;

	ScoItensContrato getItemContratoBySeq(Integer seq) throws BaseException;

	void excluirItemContrato(Integer itemSeq) throws BaseException;

	ScoItensContrato gravarOrAtualizarItemContrato(ScoItensContrato input,
			FsoConveniosFinanceiro fsoConveniosFinanceiro) throws BaseException;

	ScoAditContrato gravarOrAtualizarAditivo(ScoAditContrato input) throws ApplicationBusinessException,
			ApplicationBusinessException;

	List<AutorizacaoFornecimentoVO> listarAfTheFornLicContrato(
			ScoContrato contrato);

	void gravarAfContratos(
			List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO,
			ScoContrato contrato)
			throws BaseException;

	List<ScoContrato> obterContratoByFiltro(ContratoFiltroVO input);

	List<ScoAfContrato> obterAfByContrato(ScoContrato input);
	
	public List<ScoItemAutorizacaoForn> obterItemAfAtivoPorAf(ScoAfContrato afContrato);

	public List<ScoFaseSolicitacao> obterFaseSolicitacaoPorItemAf(ScoItemAutorizacaoForn item);

	public List<ScoItensContrato> obterItensContratoPorContrato(ScoContrato contrato);

	List<ScoContrato> listarContratosFiltro(
			ContratoFiltroVO filtroContratoIntegracao);

	List<ScoResContrato> listarRescisoesContrato(List<ScoContrato> listContratos);

	List<ScoAditContrato> pesquisarAditivos(
			ContratoFiltroVO filtroContratoIntegracao);

	List<ScoResContrato> listarRescisoesContratoFiltro(
			List<ScoContrato> listContratos,
			ContratoFiltroVO filtroContratoIntegracao);

	List<ScoResContrato> pesquisarRescisoes(
			ContratoFiltroVO filtroContratoIntegracao);

	List<ScoResContrato> listarRescisoesSituacaoEnvio(
			DominioSituacaoEnvioContrato situacao);

	// Operações XML - Integração SICON
	void validarEnvioContrato(Long nrContrato)
			throws ApplicationBusinessException;

	void validarEnvioAditivo(ScoAditContrato aditContrato) throws ApplicationBusinessException;

	void validarEnvioRescisao(ScoResContrato rescisao) throws ApplicationBusinessException;

	DadosEnvioVO gerarXml(Integer seq, String autenticacaoSicon) throws ApplicationBusinessException,
			ApplicationBusinessException;

	DadosEnvioVO gerarXmlAditivo(ScoAditContrato aditContrato,
			String autenticacaoSicon)
			throws ApplicationBusinessException;

	SiconResponseVO integrarSIASG(DadosEnvioVO dadosEnvioVO, int contSeq,
			Boolean usaVlrAfs) throws ApplicationBusinessException;

	SiconResponseVO integrarRescisaoSIASG(DadosEnvioVO dadosEnvioVO,
			ScoResContrato rescisaoContrato) throws ApplicationBusinessException;

	SiconResponseVO enviarAditivo(DadosEnvioVO dadosEnvioVO,
			ScoAditContrato aditContrato) throws ApplicationBusinessException;

	DadosEnvioVO gerarXmlRescisaoContrato(Integer seq,
			String autenticacaoSicon)
			throws ApplicationBusinessException;

	List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoContratos(
			ScoContrato contrato);

	List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoAditivos(
			ScoContrato contrato);

	List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoRescicao(
			ScoContrato contrato);

	void atualizaLogEnvioSicon(ScoLogEnvioSicon log);

	ScoLogEnvioSicon obterLogEnvioSicon(Integer seqScoLogEnvioSicon)
			throws ApplicationBusinessException;

	List<ScoContrato> obterListaContratoAssociado(
			ScoTipoContratoSicon _tipoContrato);

	List<ScoAditContrato> obterListaAditContratoAssociado(
			ScoTipoContratoSicon _tipoContrato);

	List<ScoContrato> obterListaContratoPorCriterioReajuste(
			ScoCriterioReajusteContrato scoCriterioReajusteContrato);

	List<ScoContrato> listarContratoByNroOuDescricao(Object paramPesquisa);

	List<ScoItensContrato> getItensContratoByContratoManual(ScoContrato input);

	ScoAfContrato obterAfContratosById(Integer seq);
	
	ScoAditContrato obterAfContratoComContrato(ScoAditContratoId id);

	Boolean indicarExclusaoAditivo(ScoAditContrato aditivo);

	/**
	 * Descobrir se a AF pertence a um contrato deve ser executada a seguinte
	 * consulta.
	 * 
	 * @author rmalvezzi
	 * @param iafAfnNumero
	 *            Número da Autorização.
	 * @return O contrato se exisitr.
	 */
	ScoAfContrato buscaItemContratoAF(Integer iafAfnNumero);

	/**
	 * Monta a lista de licitações
	 * 
	 * @param filtro
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	public List<ListaLicitacaoVO> montarListaLicitacoes(
			LicitacaoFiltroVO filtro, DominioModalidadeEmpenho param)
			throws BaseException;

	/**
	 * Monta a lista de detalhes da licitação
	 * 
	 * @param listaDetalhesItensLic
	 * @return
	 * @throws BaseException
	 */
	public List<ListaDetalhesItensLicVO> montarlistaDetalhesItensLic(
			List<Object[]> listaDetalhesItensLic) throws BaseException;

	/**
	 * Param: Licitação Verifica a inexistência de registros Ativos(INATIVOS),
	 * em Compras/Serviço, SE PELO MENOS UM não for, retorna FALSE, caso todos
	 * forem ativos retorna TRUE. FALSE - pinta de vermelho - TRUE - pinta de
	 * verde
	 **/
	public Boolean possuiCodSiasg(Integer numeroLicitacao)
			throws ApplicationBusinessException;
	
	void gravarConvenioItemContrato(List<ScoConvItensContrato> _listaConvenios,
			List<ScoConvItensContratoId> _listaIdConveniosExcluidos,
			BigDecimal _valorTotal) throws BaseException;

	/**
	 * Retorna quantidades de materiais pendentes de codigo sicon de acordo com
	 * origem do contrato.
	 * 
	 * @param c
	 * @return
	 */
	Long siconPendentesCount(ScoContrato c);

}