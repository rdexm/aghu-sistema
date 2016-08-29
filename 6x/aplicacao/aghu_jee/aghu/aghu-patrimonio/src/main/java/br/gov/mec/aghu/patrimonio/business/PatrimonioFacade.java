package br.gov.mec.aghu.patrimonio.business;

import java.text.ParseException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmDescMotivoMovimentos;
import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.model.PtmEdificacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmLocalizacoes;
import br.gov.mec.aghu.model.PtmLocalizacoesJn;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.PtmSituacaoMotivoMovimento;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.paciente.dao.AipBairrosCepLogradouroDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmArquivosAnexosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAvaliacaoTecnicaDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmDescMotivoMovimentosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmDesmembramentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmEdificacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmLocalizacoesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmLocalizacoesJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmNotificacaoTecnicaDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmServAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmSituacaoMotivoMovimentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTecnicoItemRecebimentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTicketDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmUserTicketDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AnaliseTecnicaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaGridVO;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.DadosEdificacaoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoArquivosAnexosVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.FiltroAceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.ImprimirFichaAceiteTecnicoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.LocalizacaoFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.NomeUsuarioVO;
import br.gov.mec.aghu.patrimonio.vo.NotificacaoTecnicaItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.patrimonio.vo.PtmEdificacaoVO;
import br.gov.mec.aghu.patrimonio.vo.QuantidadeDevolucaoBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.RelatorioRecebimentoProvisorioVO;
import br.gov.mec.aghu.patrimonio.vo.ResponsavelAreaTecAceiteTecnicoPendenteVO;
import br.gov.mec.aghu.patrimonio.vo.TecnicoItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.TicketsVO;
import br.gov.mec.aghu.patrimonio.vo.UserTicketVO;
import br.gov.mec.aghu.patrimonio.vo.UsuarioTecnicoVO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.vo.RapServidoresVO;
@Modulo(ModuloEnum.PATRIMONIO)
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class PatrimonioFacade extends BaseFacade implements IPatrimonioFacade {
	
	private static final long serialVersionUID = 7621673807612164550L;

	@EJB
	private AnaliseTecnicaBemPermanenteON analiseTecnicaBemPermanenteON;
	
	@EJB
	private PtmAreaTecAvaliacaoRN ptmAreaTecAvaliacaoRN;
	
	@EJB
	private PtmTicketRN ptmTicketRN;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO areaTecAvaliacaoDAO;
	
	@EJB
	private PtmItemRecebProvisoriosRN ptmItemRecebProvisoriosRN;
	
	@EJB
	private RegistrarDevolucaoBemPermanenteRN registrarDevolucaoBemPermanenteRN;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject 
	private PtmServAreaTecAvaliacaoDAO ptmServAreaTecAvaliacaoDAO;
	
	@Inject
	private PtmEdificacaoDAO ptmEdificacaoDAO;
	
	@EJB
	private PtmServAreaTecnicaRN ptmServAreaTecnicaRN;
	
	@EJB
	private RetiradaBemPermanenteON retiradaBemPermanenteON;
	
	@Inject
	private PtmTicketDAO ptmTicketDAO;
	
	@Inject
	private PtmUserTicketDAO ptmUserTicketDAO;
	
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;
	
	@Inject
	private PtmTecnicoItemRecebimentoDAO ptmTecnicoItemRecebimentoDAO;
	
	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;
	
	@Inject
	private PtmNotificacaoTecnicaDAO ptmNotificacaoTecnicaDAO;
	
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	
	@Inject
	private PtmAvaliacaoTecnicaDAO ptmAvaliacaoTecnicaDAO;
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	
	@Inject 
	private PtmSituacaoMotivoMovimentoDAO ptmSituacaoMotivoMovimentoDAO;
	
	@EJB 
	private PtmDescMotivoMovimentosRN ptmDescMotivoMovimentosRN; 

	@EJB 
	private PtmAvaliacaoTecnicaRN ptmAvaliacaoTecnicaRN; 
	
	@Inject 
	private PtmDescMotivoMovimentosDAO ptmDescMotivoMovimentosDAO;
	
	@Inject 
	private FccCentroCustosDAO fccCentroCustosDAO;
	
	@Inject
	private AipBairrosCepLogradouroDAO aipBairrosCepLogradouroDAO;
	
	@EJB
	private PtmEdificacaoRN ptmEdificacaoRN;
	
	@EJB
	private AceiteTecnicoRN aceiteTecnicoRN;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@Inject
	private PtmLocalizacoesDAO ptmLocalizacoesDAO;
	
	@Inject
	private PtmLocalizacoesJnDAO ptmLocalizacoesJnDAO;
	
	@Inject
	private PtmDesmembramentoDAO ptmDesmembramentoDAO;
	
	
	@Inject
	private PtmArquivosAnexosDAO ptmArquivosAnexosDAO;
	
	@EJB
	private PtmArquivosAnexosRN ptmArquivosAnexosRN;
	
	/*
	 * a(non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#consultarItensRecebimento(java.lang.Integer)
	 */
	@Override
	public List<AnaliseTecnicaBemPermanenteVO> consultarItensRecebimento(Integer numeroRecebimento) throws ApplicationBusinessException {

		return analiseTecnicaBemPermanenteON.consultarItensRecebimento(numeroRecebimento);
	}

	@Override
	public List<AceiteTecnicoParaSerRealizadoVO> recuperarListaPaginadaAceiteTecnico(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		return ptmItemRecebProvisoriosRN.recuperarListaPaginadaAceiteTecnico(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	@Override
	public Long recuperarCountAceiteTecnico(FiltroAceiteTecnicoParaSerRealizadoVO filtro) {
		return ptmItemRecebProvisoriosRN.recuperarCountAceiteTecnico(filtro);
	}
	
	@Override
	public List<PtmAreaTecAvaliacao> obterAreaTecAvaliacaoPorCodigoOuNome(String parametro) {
		return ptmAreaTecAvaliacaoDAO.obterListaAreaTecAvaliacaoParaSuggestionBox(parametro);
	}
	
	@Override
	public Long obterAreaTecAvaliacaoPorCodigoOuNomeCount(String parametro) {
		return ptmAreaTecAvaliacaoDAO.obterCountAreaTecAvaliacaoParaSuggestionBox(parametro);
	}
	
	@Override
	public PtmAreaTecAvaliacao obterAreaTecAvaliacaoPorChavePrimaria(Integer seq) {
		return ptmAreaTecAvaliacaoRN.obterAreaTecAvaliacaoPorChavePrimaria(seq);
	}

	@Override
	public PtmAreaTecAvaliacao obterAreaTecnicaPorServidor(RapServidores servidor) {
		return ptmAreaTecAvaliacaoRN.obterAreaTecnicaPorServidor(servidor);
	}
	
	@Override
	public PtmAreaTecAvaliacao obterAreaTecPorServidor(RapServidores servidor) {
		return ptmAreaTecAvaliacaoDAO.obterAreaTecPorServidor(servidor);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#listarAreaTecAvaliacaoPorCodigoCentroCusto(java.lang.Integer)
	 */
	@Override
	public List<PtmAreaTecAvaliacao> listarAreaTecAvaliacaoPorCodigoCentroCusto(Integer codigo) {
		return ptmAreaTecAvaliacaoDAO.listarPorCodigoCentroCusto(codigo);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC(java.lang.String)
	 */
	@Override
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC(String parametro) {
		return ptmAreaTecAvaliacaoDAO.pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC(parametro);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount(java.lang.String)
	 */
	@Override
	public Long pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount(String parametro) {
		return ptmAreaTecAvaliacaoDAO.pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount(parametro);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#atualizarItensRecebimentoAnaliseTecnica(java.util.List, br.gov.mec.aghu.model.PtmAreaTecAvaliacao,
	 *  java.lang.Integer)
	 */
	@Override
	public void atualizarItensRecebimentoAnaliseTecnica(List<AnaliseTecnicaBemPermanenteVO> itens, PtmAreaTecAvaliacao area, Integer numeroRecebimento)
			throws ApplicationBusinessException {
		analiseTecnicaBemPermanenteON.atualizarItensRecebimentoAnaliseTecnica(itens, area, numeroRecebimento);
	}
	
	@Override
	public List<RapServidores> obterusuariosTecnicosPorVinculoMatNome(String parametro) {
		return rapServidoresDAO.obterusuariosTecnicosPorVinculoMatNome(parametro);
	}
	
	@Override
	public Long obterusuariosTecnicosPorVinculoMatNomeCount(String parametro) {
		return rapServidoresDAO.obterusuariosTecnicosPorVinculoMatNomeCount(parametro);
	}
	
	@Override
	public List<UsuarioTecnicoVO> obterUsuariosTecnicosList(PtmAreaTecAvaliacao areaTecnica) {
		return ptmServAreaTecAvaliacaoDAO.obterUsuariosTecnicosList(areaTecnica);
	}
	
	@Override
	public List<PtmAreaTecAvaliacao> pesquisarOficinasAreaTecnicaAvaliacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao) {
		return areaTecAvaliacaoDAO.pesquisarOficinasAreaTecnicaAvaliacao(firstResult, maxResult, orderProperty, asc, nomeAreaTec, centroCusto, responsavel, situacao);
	}

	@Override
	public void validarAssociacaoDeTecnicoAreaAvaliacao(
			List<UsuarioTecnicoVO> tecnicosList, RapServidores tecnicoSB1,  Integer seqAreaTec)
			throws ApplicationBusinessException {
		ptmServAreaTecnicaRN.validarAssociacaoDeTecnicoAreaAvaliacao(tecnicosList, tecnicoSB1, seqAreaTec);
	}
	
	@Override
	public void validarUnicidadeTecnicoPadrao(List<UsuarioTecnicoVO> tecnicosList)	throws ApplicationBusinessException {
		ptmServAreaTecnicaRN.validarUnicidadeTecnicoPadrao(tecnicosList);
	}
	
	@Override
	public void gravarAlteracoesAssociarTecnicoPadrao(
			List<UsuarioTecnicoVO> tecnicosExcluidos,
			List<UsuarioTecnicoVO> tecnicosAdicionados) {
		ptmServAreaTecnicaRN.gravarAlteracoesAssociarTecnicoPadrao(tecnicosExcluidos, tecnicosAdicionados);	
	}
	
	@Override
	public Long pesquisarOficinasAreaTecnicaAvaliacaoCount(String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao) {
		return areaTecAvaliacaoDAO.pesquisarOficinasAreaTecnicaAvaliacaoCount(nomeAreaTec, centroCusto, responsavel, situacao);
	}
	
	@Override
	public void persistirAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao, AghuParametrosEnum perfilEnum) throws BaseException, ApplicationBusinessException {
		ptmAreaTecAvaliacaoRN.persistir(areaTecnicaAvaliacao, perfilEnum);
	}

	@Override
	public FccCentroCustos buscarCentroCustoResponsavelSuperior(FccCentroCustos fccCentroCustos, AghuParametrosEnum perfilEnum) throws ApplicationBusinessException {
		return ptmAreaTecAvaliacaoRN.buscarCentroCustoResponsavelSuperior(fccCentroCustos, perfilEnum);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#listarTodosTickets(java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	public List<PtmTicket> listarTodosTickets(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return getPtmTicketDAO().listarTodosTickets(firstResult, maxResult, orderProperty, asc);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#listarTodosTicketsCount()
	 */
	@Override
	public Long listarTodosTicketsCount() {

		return getPtmTicketDAO().listarTodosTicketsCount();
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#listarTodosUserTicketVO()
	 */
	@Override
	public List<UserTicketVO> listarTodosUserTicketVO() {

		return getPtmUserTicketDAO().listarTodosUserTicketVO();
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#encaminharTicketParaTecnico(java.lang.Integer, java.util.List, java.util.List)
	 */
	@Override
	public void encaminharTicketParaTecnico(Integer numeroTicket, List<RapServidores> tecnicos, List<AghCaixaPostal> caixasPostais)
			throws ApplicationBusinessException {

		getPtmTicketRN().encaminharTicketParaTecnico(numeroTicket, tecnicos, caixasPostais);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#assumirTicketAvaliacao(java.lang.Integer, br.gov.mec.aghu.model.AghCaixaPostal, br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public void assumirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico){

		getPtmTicketRN().assumirTicketAvaliacao(numeroTicket, caixaPostal, tecnico);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#concluirTicketAvaliacao(java.lang.Integer, br.gov.mec.aghu.model.AghCaixaPostal, br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public void concluirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico) {

		getPtmTicketRN().concluirTicketAvaliacao(numeroTicket, caixaPostal, tecnico);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#cancelarTicketAvaliacao(java.lang.Integer, br.gov.mec.aghu.model.AghCaixaPostal, br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public void cancelarTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico){

		getPtmTicketRN().cancelarTicketAvaliacao(numeroTicket, caixaPostal, tecnico);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#enviarPendenciaEmailTicket()
	 */
	@Override
	public void enviarPendenciaEmailTicket(AghJobDetail job) throws ApplicationBusinessException {

		getPtmTicketRN().enviarPendenciaEmailTicket(job);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#validarInicializacaoRetiradaBemPermanente(java.util.List)
	 */
	@Override
	public void validarInicializacaoRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException {

		getRetiradaBemPermanenteON().validarInicializacaoRetiradaBemPermanente(itensRetirada);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#validarInicializacaoDetalharRetiradaBemPermanente(java.util.List)
	 */
	@Override
	public void validarInicializacaoDetalharRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException {

		getRetiradaBemPermanenteON().validarInicializacaoDetalharRetiradaBemPermanente(itensRetirada);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#registrarRetiradaBemPermanente(java.util.List)
	 */
	@Override
	public void registrarRetiradaBemPermanente(List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhados) throws ApplicationBusinessException {

		getRetiradaBemPermanenteON().registrarRetiradaBemPermanente(itensDetalhados);
	}

	@Override
	public void validarInicializacaoReImpressaoBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws BaseListException {

		getRetiradaBemPermanenteON().validarInicializacaoReImpressaoBemPermanente(itensRetirada);
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade#obterResponsavelAreaTecnicaPorSeqArea(java.lang.Integer)
	 */
	@Override
	public RapServidores obterResponsavelAreaTecnicaPorSeqArea(Integer seqAreaTecnica) {

		return getPtmAreaTecAvaliacaoDAO().obterResponsavelAreaTecnicaPorSeqArea(seqAreaTecnica);
	}

	public PtmAreaTecAvaliacaoDAO getPtmAreaTecAvaliacaoDAO() {
		return ptmAreaTecAvaliacaoDAO;
	}
	
	public PtmAreaTecAvaliacaoRN getPtmAreaTecAvaliacaoRN() {
		return ptmAreaTecAvaliacaoRN;
	}

	public PtmTicketDAO getPtmTicketDAO() {
		return ptmTicketDAO;
	}

	public PtmUserTicketDAO getPtmUserTicketDAO() {
		return ptmUserTicketDAO;
	}

	public PtmTicketRN getPtmTicketRN() {
		return ptmTicketRN;
	}
	
	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	public PtmItemRecebProvisoriosDAO getPtmItemRecebProvisoriosDAO() {
		return ptmItemRecebProvisoriosDAO;
	}

	public RetiradaBemPermanenteON getRetiradaBemPermanenteON() {
		return retiradaBemPermanenteON;
	}

	@Override
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacao(
			Object objPesquisa) {
		return this.getPtmAreaTecAvaliacaoDAO().pesquisarAreaTecnicaAvaliacao(
				objPesquisa);
	}

	@Override
	public Long pesquisarAreaTecnicaAvaliacaoCount(Object objPesquisa) {
		return getPtmAreaTecAvaliacaoDAO().pesquisarAreaTecnicaAvaliacaoCount(
				objPesquisa);
	}

	@Override
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(
			Object objPesquisa, RapServidores servidorLogado) {
		return this.getPtmAreaTecAvaliacaoDAO()
				.pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(objPesquisa,
						servidorLogado);
	}

	@Override
	public Long pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(
			Object objPesquisa, RapServidores servidorLogado) {
		return this.getPtmAreaTecAvaliacaoDAO()
				.pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(
						objPesquisa, servidorLogado);
	}

	@Override
	public List<RapServidores> pesquisarResponsavelTecnico(
			Object objPesquisa) {
		return this.getPtmAreaTecAvaliacaoDAO().pesquisarResponsavelTecnico(
				objPesquisa);
	}

	@Override
	public Long pesquisarResponsavelTecnicoCount(Object objPesquisa) {
		return this.getPtmAreaTecAvaliacaoDAO()
				.pesquisarResponsavelTecnicoCount(objPesquisa);
	}

	@Override
	public List<RapServidores> pesquisarResponsavelTecnicoAreaTecnicaAvaliacao(
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException {
		return this.getPtmAreaTecAvaliacaoRN()
				.pesquisarResponsavelTecnicoAreaTecnicaAvaliacao(aceiteTecnicoParaSerRealizadoVO);
	}

	@Override
	public void enviarEmailSolicitacaoTecnicaAnalise( PtmAreaTecAvaliacao areaTecnicaAvalicao, RapServidores responsavel,
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO, boolean permissaoChefiaAreaTecnicaAvaliacao, boolean permissaoChefiaPatrimonio)
			throws ApplicationBusinessException, ParseException {

		this.getPtmAreaTecAvaliacaoRN().enviarEmailSolicitacaoTecnicaAnalise( areaTecnicaAvalicao, responsavel, aceiteTecnicoParaSerRealizadoVO,
				permissaoChefiaAreaTecnicaAvaliacao, permissaoChefiaPatrimonio);
	}

	@Override
	public List<PtmAreaTecAvaliacao> obterListaAreaTecnicaPorServidor(
			RapServidores servidor) {
		return this.getPtmAreaTecAvaliacaoRN().obterListaAreaTecnicaPorServidor(servidor);
	}

	@Override
	public Integer obterPagamentoParcialItemRecebimento(Integer recebimento, Integer itemRecebimento) {
		return this.ptmItemRecebProvisoriosDAO.obterPagamentoParcialItemRecebimento(recebimento, itemRecebimento);
	}

	@Override
	public void designarTecnicoResponsavel(List<AceiteTecnicoParaSerRealizadoVO> aceites, 
			List<RapServidores> toDelete, List<RapServidores> toInsert, Integer pagamento, RapServidores servidor) 
			throws ApplicationBusinessException {
		this.ptmItemRecebProvisoriosRN.designarTecnicoResponsavel(aceites, toDelete, toInsert, pagamento, servidor);
	}

	@Override
	public List<PtmTecnicoItemRecebimento> obterTecnicosPorServidor(RapServidores servidor, Integer recebimento, Integer itemRecebimento) {
		return this.ptmTecnicoItemRecebimentoDAO.obterPorTecnicoItemRecebimento(recebimento, itemRecebimento, servidor);
	}
	
	@Override
	public void atenderAceiteTecnico(Integer recebimento,
			Integer itemRecebimento) {
		this.getPtmTicketRN().atenderAceiteTecnico(recebimento, itemRecebimento);
	}

	@Override
	public List<PtmItemRecebProvisorios> pesquisarItemRecebProvisorios(
			Integer recebimento, Integer itemRecebimento, RapServidores servidor) {
		return this.getPtmItemRecebProvisoriosDAO().pesquisarItemRecebProvisorios(recebimento, itemRecebimento, servidor);
	}
	
	@Override
	public Long obterNotaPorNumeroRecebimento(Integer numrecebimento){
		return sceNotaRecebProvisorioDAO.obterNotaPorNumeroRecebimento(numrecebimento);
	}
	
	@Override
	public QuantidadeDevolucaoBemPermanenteVO obterQuantidadeItem(Integer numRecebimento, Integer itemRecebimento){
		return sceItemRecebProvisorioDAO.obterQuantidadeItensEntrega(numRecebimento, itemRecebimento);
	}

	@Override
	public Integer pesquisarNumeroNotaFiscal(Integer numero) {
		return sceNotaRecebProvisorioDAO.pesquisarNumeroNotaFiscal(numero);
	}

	@Override
	public void gravarNotificacaoTecnica(PtmNotificacaoTecnica ptmNotificacaoTecnica) { 
		ptmNotificacaoTecnicaDAO.persistir(ptmNotificacaoTecnica);
	}

	@Override
	public PtmItemRecebProvisorios pesquisarIrpSeq(Integer recebimento, Integer itemRecebimento, RapServidores servidor) {		
		return ptmItemRecebProvisoriosDAO.pesquisarIrpSeq(recebimento, itemRecebimento, servidor);
	}

	@Override
	public List<PtmNotificacaoTecnica> pesquisarNotificacoesTecnica(Long seq) {
		return ptmNotificacaoTecnicaDAO.pesquisarNotificacoesTecnica(seq);		 
	}
	
	@Override
	public List<AceiteTecnicoParaSerRealizadoVO> obterSubItensRelatorioProtocoloRetBensPermanentes(List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList,
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta, boolean reimpressao) {
		return retiradaBemPermanenteON.obterSubItensRelatorioProtocoloRetBensPermanentes(itemRetiradaList, itensDetalhamentoListCompleta, reimpressao);
	}

	@Override
	public void registrarDevolucaoBemPermanente(List<DevolucaoBemPermanenteVO> itensParaDevolucao) throws ApplicationBusinessException {
		this.registrarDevolucaoBemPermanenteRN.atualizar(itensParaDevolucao);
	}
	
	@Override
	public List<DevolucaoBemPermanenteVO> carregarListaBemPermanente(Long seqItemPatrimonio, Integer vlrNumerico){
		return ptmBemPermanentesDAO.carregarListaBemPermanente(seqItemPatrimonio, vlrNumerico);
	}
	
	@Override
	public List<PtmTecnicoItemRecebimento> obterPorTecnicoItemRecebimento(Integer recebimento, Integer itemRecebimento, RapServidores servidorTecnico) {
		return ptmTecnicoItemRecebimentoDAO.obterPorTecnicoItemRecebimento(recebimento, itemRecebimento, servidorTecnico);
	}

	@Override
	public RapServidores obterServidorPorUsuario(String loginUsuario) throws ApplicationBusinessException {
		return rapServidoresDAO.obterServidorPorUsuario(loginUsuario);
	}
	
	@Override
	public List<PtmSituacaoMotivoMovimento> obterTodasSituacoesMotivoMovimento() {
		return ptmSituacaoMotivoMovimentoDAO.obterTodasSituacoesMotivoMovimento();
	}
	
	@Override
	public void persistirMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException{
		ptmDescMotivoMovimentosRN.inserirPtmDescMotivoMovimentos(ptmDescMotivoMovimentos);
	}
	
	@Override
	public void atualizarMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException{
		ptmDescMotivoMovimentosRN.atualizarPtmDescMotivoMovimentos(ptmDescMotivoMovimentos);
	}
	
	@Override
	public void excluirMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException{
		ptmDescMotivoMovimentosRN.excluirPtmDescMotivoMovimentos(ptmDescMotivoMovimentos);
	}
	
	@Override
	public List<PtmDescMotivoMovimentos> pesquisarDescricoesSituacaoMovimento(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return ptmDescMotivoMovimentosDAO.pesquisarDescricoesSituacaoMovimento(situacaoSeq, ativo, obrigatorio, descricao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarDescricoesSituacaoMovimentoCount(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao){
		return ptmDescMotivoMovimentosDAO.pesquisarDescricoesSituacaoMovimentoCount(situacaoSeq, ativo, obrigatorio, descricao);
	}
	
	@Override
	public PtmDescMotivoMovimentos obterPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos){
		return ptmDescMotivoMovimentosDAO.obterPtmDescMotivoMovimentos(ptmDescMotivoMovimentos);
	}
	
	@Override
	public PtmSituacaoMotivoMovimento obterPtmSituacaoMotivoMovimentoPorChavePrimaria(PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento){
		return ptmSituacaoMotivoMovimentoDAO.obterPorChavePrimaria(ptmSituacaoMotivoMovimento.getSeq());
	}

	@Override
	public NotificacaoTecnicaItemRecebimentoProvisorioVO obterNotificacaoTecnicaItemRecebProvisorio(Long pntSeq) {
		return ptmNotificacaoTecnicaDAO.obterNotificacaoTecnicaItemRecebProvisorio(pntSeq);
	}
	
	@Override
	public Object[] obterNomeMatriculaServidorCentroCusto(Integer codigoCentroCusto) {
		return rapServidoresDAO.obterNomeMatriculaServidorCentroCusto(codigoCentroCusto);
	}
	
	@Override
	public boolean isResponsavelAreaTecnico(RapServidores servidorLogado, Integer seqAreaTecnica){
		return ptmAreaTecAvaliacaoDAO.isResponsavelAreaTecnico(servidorLogado, seqAreaTecnica);
	}
	
	@Override
	public Boolean verificarChefeParaAreaTecnica(Integer seq,
			RapServidores servidorCC) {
		return this.ptmAreaTecAvaliacaoDAO.verificarChefeParaAreaTecnica(seq, servidorCC);
	}

	@Override
	public List<PtmBemPermanentes> listarSugestionPatrimonio(Object param) {
		return ptmBemPermanentesDAO.listarSugestionPatrimonio(param);
	}

	@Override
	public Long listarSugestionPatrimonioCount(Object param) {
		return ptmBemPermanentesDAO.listarSugestionPatrimonioCount(param);
	}
	
	@Override
	public List<PtmNotificacaoTecnica> listarSugestionNotificacaoTecnica(Object param) {
		return ptmNotificacaoTecnicaDAO.listarSugestionNotificacaoTecnica(param);
	}

	@Override
	public Long listarSugestionNotificacaoTecnicaCount(Object param) {
		return ptmNotificacaoTecnicaDAO.listarSugestionNotificacaoTecnicaCount(param);
	}
	
	@Override
	public List<ScoMaterial> listarScoMateriaisSugestion(Object param) {
		return scoMaterialDAO.listarScoMateriaisSugestion(param);
	}
	
	@Override
	public Long listarScoMateriaisSugestionCount(Object param) {
		return scoMaterialDAO.listarScoMateriaisSugestionCount(param);
	}
	
	@Override
	public List<PtmItemRecebProvisorios> listarConsultaRecebimentosSugestion(Object param) {
		return ptmItemRecebProvisoriosDAO.listarConsultaRecebimentosSugestion(param);
	}

	@Override
	public Long listarConsultaRecebimentosSugestionCount(Object param) {
		return ptmItemRecebProvisoriosDAO.listarConsultaRecebimentosSugestionCount(param);
	}
	
	@Override
	public List<RapServidores> consultarUsuariosSugestion(Object param) {
		return rapServidoresDAO.consultarUsuariosSugestion(param);
	}
	
	@Override
	public Long consultarUsuariosSugestionCount(Object param) {
		return rapServidoresDAO.consultarUsuariosSugestionCount(param);
	}

	@Override
	public void obterAreaTecnicaPorCodigo(Integer codigoCentroCusto) {
		areaTecAvaliacaoDAO.obterAreaTecnicaPorCodigo(codigoCentroCusto);
	}
	
	@Override
	public void obterCentroCustoSuperiorPorSituacao(Integer cctSuperior) {
		fccCentroCustosDAO.obterCentroCustoSuperiorPorSituacao(cctSuperior);
	}
	
	@Override
	public void obterCentroCustoResponsavelPorUsuario(Integer matriculaChefia, Short vinculoChefia) {
		fccCentroCustosDAO.obterCentroCustoResponsavelPorUsuario(matriculaChefia, vinculoChefia);
	}
	

	@Override
	public List<PtmBemPermanentes> obterPtmBemPermanentesPorNumeroDescricao(
			Object filtro) {
		return ptmBemPermanentesDAO.obterPtmBemPermanentesPorNumeroDescricao(filtro);
	}

	@Override
	public Long obterPtmBemPermanentesPorNumeroDescricaoCount(Object filtro) {
		return ptmBemPermanentesDAO.obterPtmBemPermanentesPorNumeroDescricaoCount(filtro);
	}

	@Override
	public DadosEdificacaoVO obterDadosEdificacaoDAO(Integer seq) {
		return ptmEdificacaoDAO.obterDadosEdificacaoDAO(seq);
	}

	@Override
	public List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep,
			Integer codigoCidade) throws ApplicationBusinessException {
		return aipBairrosCepLogradouroDAO.pesquisarCeps(cep, codigoCidade);
	}

	@Override
	public void alterarEdificacao(DominioSituacao situacao, String nome,
			String descricao, Long bpeSeq, Integer lgrSeq, Integer numero,
			String complemento, double latitude, double longitude,
			Integer edfSeq) throws ApplicationBusinessException {
		ptmEdificacaoRN.alterarEdificacao(situacao, nome, descricao, bpeSeq, lgrSeq, numero, complemento, latitude, longitude, edfSeq);
	}

	@Override
	public void gravarEdificacao(DominioSituacao situacao, String nome,
			String descricao, Long bpeSeq, Integer lgrSeq, Integer numero,
			String complemento, double latitude, double longitude)
			throws ApplicationBusinessException {
		ptmEdificacaoRN.gravarEdificacao(situacao, nome, descricao, bpeSeq, lgrSeq, numero, complemento, latitude, longitude);
	}

	@Override
	public PtmBemPermanentes obterBemPermanentesPorNumeroBem(Long numeroBem) {
		return ptmBemPermanentesDAO.obterBemPermanentePorNumeroBem(numeroBem);
	}
	
	@Override
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaSuggestionBox(
			String parametro, RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaSuggestionBox(parametro, servidor);
	}

	@Override
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaPorResponsavel(
			String parametro, RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaPorResponsavel(parametro, servidor);
	}

	@Override
	public Long pesquisarListaAreaTecnicaSuggestionBoxCount(String parametro,
			RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaSuggestionBoxCount(parametro, servidor);
	}

	@Override
	public Long pesquisarListaAreaTecnicaPorResponsavelCount(String parametro,
			RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaPorResponsavelCount(parametro, servidor);
	}

	@Override
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaPorPermissoes(
			String parametro, RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoRN.pesquisarAreaTecnicaPorPermissoes(parametro, servidor);
	}

	@Override
	public Long pesquisarAreaTecnicaPorPermissoesCount(String parametro,
			RapServidores servidor) {
		return this.ptmAreaTecAvaliacaoRN.pesquisarAreaTecnicaPorPermissoesCount(parametro, servidor);
	}
	//#48782
	@Override
	public List<PtmItemRecebProvisorios> listarItemRecebPorSeqItemReceb(Integer recebimento, Integer itemRecebimento){
		return this.ptmItemRecebProvisoriosDAO.listarItemRecebPorSeqItemReceb(recebimento, itemRecebimento);
	}
	@Override
	public List<FccCentroCustos> pesquisarCentroCustosPorServidorLogadoDescricao(String filtro, RapServidores servidor) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosPorServidorLogadoDescricao(filtro, servidor);
	}
	@Override
	public Long pesquisarCentroCustosPorServidorLogadoDescricaoCount(String filtro, RapServidores servidor) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosPorServidorLogadoDescricaoCount(filtro, servidor);
	}
	@Override
	public List<PtmBemPermanentes> obterPatrimonioPorNrBemDetalhamento(String param) {
		return this.ptmBemPermanentesDAO.pesquisarSugestionPatrimonio(param);
	}
	@Override
	public Long obterPatrimonioPorNrBemDetalhamentoCount(String param) {
		return this.ptmBemPermanentesDAO.listarSugestionPatrimonioCount(param);
	}
	@Override
	public List<AvaliacaoTecnicaVO> listaAceiteTecnico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor){
		return this.aceiteTecnicoRN.recuperarListaPaginadaAceiteTecnico(firstResult, maxResult, orderProperty, asc, filtro, vinculoCentroCusto, servidor);
	}
	@Override
	public Long listarAceitetecnicoCount(AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor){
		return this.ptmAvaliacaoTecnicaDAO.recuperarListaPaginadaAceiteTecnicoCount(filtro, vinculoCentroCusto, servidor);
	}
	@Override
	public boolean verificarUsuarioLogadoResponsavelPorAceite(Long seqItemReceb, RapServidores servidor){
		return this.ptmItemRecebProvisoriosDAO.verificarUsuarioLogadoResponsavelPorAceite(seqItemReceb, servidor);
	}
	@Override
	public RapServidoresVO verificarUsuarioLogadoResponsavelPorAreaTecnica(Integer itemRecebimento, Integer recebimento){
		return ptmAreaTecAvaliacaoDAO.verificarUsuarioLogadoResponsavelPorAreaTec(itemRecebimento, recebimento);
	}
	@Override
	public List<PtmItemRecebProvisorios> listarPatrimonioPorItemReceb(String param, RapServidores servidor){
		return this.ptmItemRecebProvisoriosDAO.pesquisarItemRecebProvisorioPorCentroCusto(param, servidor);
	}
	@Override
	public Long listarPatrimonioPorItemRecebCount(String param, RapServidores servidor){
		return this.ptmItemRecebProvisoriosDAO.pesquisarItemRecebProvisorioPorCentroCustoCount(param, servidor);
	}
	@Override
	public void excluirAvaliacaoTecnica(Integer seq, RapServidores servidor) throws ApplicationBusinessException {
		this.aceiteTecnicoRN.excluirPtmAvaliacaoTecnica(seq, servidor);
	}
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorNotificacaoTecnica(ArquivosAnexosPesquisaFiltroVO filtro,final Integer firstResult, final Integer maxResults){
		return this.ptmArquivosAnexosDAO.pesquisarArquivoAnexosPorNotificacaoTecnica(filtro, firstResult, maxResults);
	}
	
	public Long pesquisarArquivoAnexosPorNotificacaoTecnicaCount(ArquivosAnexosPesquisaFiltroVO filtro){
		return this.ptmArquivosAnexosDAO.pesquisarArquivoAnexosPorNotificacaoTecnicaCount(filtro);
	}
	
	@Override
	public Long pesquisarArquivoAnexosPorRecebimentoCount(ArquivosAnexosPesquisaFiltroVO filtro,Long irpSeq){
		return this.ptmArquivosAnexosDAO.pesquisarArquivoAnexosPorRecebimentoCount(filtro,irpSeq);
	}

	@Override
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorPatrimonio(ArquivosAnexosPesquisaFiltroVO filtro, Integer firstResult, Integer maxResults) {
		return this.ptmArquivosAnexosDAO.pesquisarArquivoAnexosPorPatrimonio(filtro,firstResult,maxResults);
	}

	@Override
	public Long pesquisarArquivoAnexosPorPatrimonioCount(ArquivosAnexosPesquisaFiltroVO filtro) {
		return this.ptmArquivosAnexosDAO.pesquisarArquivoAnexosPorPatrimonioCount(filtro);
	}

	@Override
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorRecebimento(ArquivosAnexosPesquisaFiltroVO filtro,Long irpSeq, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return this.ptmArquivosAnexosDAO.pesquisarArquivosAnexosPorRecebimento(filtro,irpSeq,firstResult,maxResults, orderProperty, asc);
	}
	public PtmArquivosAnexos obterDocumentoAnexado(Long seq){
		return this.ptmArquivosAnexosDAO.obterPorChavePrimaria(seq);
	}
	//#48784 
	@Override
	public Double carregarCampoDaTela(PtmAvaliacaoTecnica avaliacaoTecnica){
		return this.aceiteTecnicoRN.carregarCampoDaTela(avaliacaoTecnica);
	}
	@Override
	public List<DevolucaoBemPermanenteVO> pesquisarBensPermanentesPorSeqPirp(Long irpSeq, boolean edicao, Integer avtSeq){
		return this.ptmBemPermanentesDAO.pesquisarBensPermanentesPorSeqPirp(irpSeq, edicao, avtSeq);
	}
	@Override
	public List<PtmDesmembramento> pesquisarDesmembramentoPorAvtSeq(Integer irpSeq){
		return this.ptmDesmembramentoDAO.pesquisarDesmembramentoPorAvtSeq(irpSeq);
	}
	@Override
	public void excluirDesmembramento(Integer seq){
		ptmDesmembramentoDAO.removerPorId(seq);
	}
	@Override
	public PtmItemRecebProvisorios pesquisarItemRecebSeq(Integer recebimento, Integer itemRecebimento) {
		return this.ptmItemRecebProvisoriosDAO.pesquisarItemRecebSeq(recebimento, itemRecebimento);
	}
	@Override
	public void registrarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
		this.aceiteTecnicoRN.registrarAceiteTecnico(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
	}
	@Override
	public void finalizarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
		this.aceiteTecnicoRN.finalizarAceiteTecnico(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
	}
	@Override
	public void certificarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
		this.aceiteTecnicoRN.certificarAceiteTecnico(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
	}
	@Override
	public PtmAvaliacaoTecnica obterAvaliacaoTecnicaPorSeq(Integer seq){
		return this.ptmAvaliacaoTecnicaDAO.obterPorChavePrimaria(seq);
	}
	
	//44800
	@Override
	public List<PtmEdificacaoVO> pesquisarEdificacoesAtivasNrBemouSeqouNome(String strPesquisa){
		return ptmEdificacaoDAO.obterSb1EdificacaoAtivo(strPesquisa);
	}
	
	//44800
	@Override
	public Long pesquisarEdificacoesAtivasNrBemouSeqouNomeCount(String strPesquisa){
		return ptmEdificacaoDAO.obterSb1EdificacaoAtivoCount(strPesquisa);
	}
	
	//44800 - C3
	@Override
	public List<LocalizacaoFiltroVO>pesquisarListaPaginadaLocalizacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, LocalizacaoFiltroVO localizacaoFiltro){
		return ptmLocalizacoesDAO.obterListaPaginadaLocalizacao(firstResult, maxResult, orderProperty, asc, localizacaoFiltro);
	}
	
	//44800 - C3
	@Override
	public Long pesquisarListaPaginadaLocalizacaoCount(LocalizacaoFiltroVO localizacaoFiltro){
		return ptmLocalizacoesDAO.obterListaPaginadaLocalizacaoCount(localizacaoFiltro);
	}
	
	//44800
	@Override
	public void persistirPtmLocalizacoes(PtmLocalizacoes ptmLocalizacoes)throws BaseException{
		ptmLocalizacoesDAO.persistir(ptmLocalizacoes);
	}
	
	//44800
	@Override
	public void atualizarPtmLocalizacoes(PtmLocalizacoes ptmLocalizacoes)throws BaseException{
		ptmLocalizacoesDAO.atualizar(ptmLocalizacoes);
	}
	@Override
	public void atualizarPtmLocalizacoesJN(PtmLocalizacoesJn ptmLocalizacoesJn){
		ptmLocalizacoesJnDAO.atualizar(ptmLocalizacoesJn);
	}
	@Override
	public void inserirPtmLocalizacoesJN(PtmLocalizacoesJn ptmLocalizacoesJn){
		ptmLocalizacoesJnDAO.persistir(ptmLocalizacoesJn);
	}
	@Override
	public PtmLocalizacoes obterPtmLocalizacoes(Long seqPtmLocalizacoes) {
		return ptmLocalizacoesDAO.obterPtmLocalizacoesComPtmBemPermanentes(seqPtmLocalizacoes);
	}
	
	@Override
	public PtmArquivosAnexos obterVisualizacaoDetalhesAnexo(Long aaSeq){
		return this.ptmArquivosAnexosDAO.obterVisualizacaoDetalhesAnexo(aaSeq);
	}
	@Override
	public DetalhamentoArquivosAnexosVO obterVisualizacaoDetalhamento(Long aaSeq,String tipo){
		return this.ptmArquivosAnexosDAO.obterVisualizacaoDetalhamento(aaSeq,tipo);
	}
	@Override
	public PtmArquivosAnexos obterArquivosAnexosPorId(Long seq){
		return this.ptmArquivosAnexosDAO.obterPorChavePrimaria(seq);
	}
 
	@Override
	public Long obterIrpSeqNotificacoesTecnica(Long irpSeq) {
		return ptmNotificacaoTecnicaDAO.obterIrpSeqNotificacoesTecnica(irpSeq);
	}
	
	@Override
	public void inserirArquivoAnexos(PtmArquivosAnexos anexoForm, Long aaSeq, PtmNotificacaoTecnica notificacaoTecnica, Long irpSeq, boolean flagEditar) {
		this.ptmArquivosAnexosRN.salvar(anexoForm, aaSeq, notificacaoTecnica, irpSeq, flagEditar);
	}
		
	@Override
	public ImprimirFichaAceiteTecnicoBemPermanenteVO recuperarCamposFixosRelatorio(Integer avtSeq) {
		return ptmAvaliacaoTecnicaDAO.recuperarCamposFixosRelatorio(avtSeq);
	}

	@Override
	public List<PtmBemPermanentes> recuperarCamposVariaveisRelatorio(Integer avtSeq) {
		return ptmAvaliacaoTecnicaDAO.recuperarCamposVariaveisRelatorio(avtSeq);
	}
	
	@Override
	public void inserirNotaItemReceb(Long aaSeq, Long irpSeq) {
		this.ptmArquivosAnexosRN.persistirNotaItemReceb(aaSeq, irpSeq);
	}
	
	@Override
	public PtmItemRecebProvisorios listarConsultaRecebimentosSugestionGrid(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		return this.ptmItemRecebProvisoriosDAO.listarConsultaRecebimentosSugestionGrid(ptmItemRecebProvisorios);
	}
	
	@Override
	public void validarArquivoAnexo(PtmArquivosAnexos anexoSelecionado) throws ApplicationBusinessException {
		this.ptmArquivosAnexosRN.validarArquivoAnexo(anexoSelecionado);
	}
	
	@Override
	public Long obterListaDadosEdificacaoDAOCount(String nome, String descricao, AipLogradouros logradouro,
			DominioSituacao situacao, AipCidades municipio, AipUfs uf, AipBairros bairros, Integer numeroEdificacao, String complemento,
			AipCepLogradouros cep, AipLogradouros logradouroS, PtmBemPermanentes bemPermanentes) {
		return ptmEdificacaoDAO.obterListaDadosEdificacaoDAOCount(nome, descricao, logradouro, situacao, municipio, uf, bairros, 
				numeroEdificacao, complemento, cep, logradouroS, bemPermanentes);
	}

	@Override
	public List<DadosEdificacaoVO> obterListaDadosEdificacaoDAO(String nome, String descricao, AipLogradouros logradouro, DominioSituacao situacao, AipCidades municipio, AipUfs uf,
			AipBairros bairros, Integer numeroEdificacao, String complemento, AipCepLogradouros cep, AipLogradouros logradouroS,
			PtmBemPermanentes bemPermanentes, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return ptmEdificacaoDAO.obterListaDadosEdificacaoDAO(nome, descricao, logradouro, situacao, municipio, uf, bairros, numeroEdificacao, complemento, cep, logradouroS, bemPermanentes, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public void enviarNotificacaoAceitePendente(AghJobDetail job) throws ApplicationBusinessException{
		ptmTicketRN.enviarNotificacaoAceitePendente(job);
	}
	
	@Override
	public PtmEdificacao obterPtmEdificacaoPorSeq(Integer seqPtmEdificacao){
		return ptmEdificacaoDAO.obterOriginal(seqPtmEdificacao);
	}

	@Override
	public TecnicoItemRecebimentoVO obterTecnicoResponsavelPorMatENome(Long numeroRecebimento){
		return ptmTecnicoItemRecebimentoDAO.obterTecnicoResponsavelPorMatENome(numeroRecebimento);
	}

	@Override
	public List<PtmArquivosAnexos> obterAnexoArquivoAceiteTecnico(
			Integer aceiteTecnico) {
		return ptmArquivosAnexosDAO.obterAnexoArquivoAceiteTecnico(aceiteTecnico);
	}
	
	@Override
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosComAF(Integer recebimento, Integer itemRecebimento) {
		return ptmNotificacaoTecnicaDAO.obterRecebimentosComAF(recebimento, itemRecebimento);
	}
	
	@Override
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosESL(Integer recebimento, Integer itemRecebimento) {
		return ptmNotificacaoTecnicaDAO.obterRecebimentosESL(recebimento, itemRecebimento);
	}
	
	@Override
	public TecnicoItemRecebimentoVO buscarTecnicoResponsavelPorMatENome(Integer nrpSeq, Integer nroItem){
		return ptmTecnicoItemRecebimentoDAO.buscarTecnicoResponsavelPorMatENome(nrpSeq, nroItem);
	}

	@Override
	public List<ItemRecebimentoVO> carregarItemRecebimento(Integer numRecebimento, Integer itemRecebimento){
		return ptmItemRecebProvisoriosRN.carregarItemRecebimento(numRecebimento, itemRecebimento);
	}	

	@Override
	public NomeUsuarioVO obterNomeMatriculaUsuario(Long irpSeq) {
		return ptmTecnicoItemRecebimentoDAO.obterNomeMatriculaUsuario(irpSeq);
	}	

	@Override
	public List<PtmNotificacaoTecnica> obterqNotificacoesTecnica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Long irpSeq) {
		return ptmNotificacaoTecnicaDAO.obterqNotificacoesTecnica(firstResult, maxResult, orderProperty, asc, irpSeq);
	}	

	@Override
	public Long obterqNotificacoesTecnicaCount(Long irpSeq) {
		return ptmNotificacaoTecnicaDAO.obterqNotificacoesTecnicaCount(irpSeq);
	}	

	@Override
	public List<TicketsVO> carregarTicketsItemRecebimentoProvisorio(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return ptmTicketRN.carregarTicketsItemRecebimentoProvisorio(itemRecebimento, firstResult, maxResult, orderProperty, asc);
	}	
	
	@Override
	public Long carregarTicketsItemRecebimentoProvisorioCount(ItemRecebimentoVO itemRecebimento) {
		return ptmTicketRN.carregarTicketsItemRecebimentoProvisorioCount(itemRecebimento);
	}

	@Override
	public Integer verificarResponsavelAceiteTecnico(RapServidores servidor, List<Long> seqRecebProvisorios) {
		return ptmTecnicoItemRecebimentoDAO.verificarResponsavelAceiteTecnico(servidor, seqRecebProvisorios);		
	}
	
	@Override
	public List<PtmAreaTecAvaliacao> listarAreaTecnicaAvaliacaoAbaixoCentroCusto(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException{
		return ptmAreaTecAvaliacaoRN.listarAreaTecAbaixoCentroCusto(aceiteTecnicoParaSerRealizadoVO);		
	}

	@Override
	public Long pesquisarQtdeItemRecebAssociadoAreaTecnica(Integer seqAreaTec) {
		return ptmAreaTecAvaliacaoDAO.pesquisarQtdeItemRecebAssociadoAreaTecnica(seqAreaTec);		
	}
	
	@Override
	public List<AvaliacaoTecnicaVO> carregarAnaliseTecnico(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return ptmAvaliacaoTecnicaRN.carregarAnaliseTecnico(itemRecebimento, firstResult, maxResult, orderProperty, asc);
	}	
	
	@Override
	public Long carregarAnaliseTecnicoCount(ItemRecebimentoVO itemRecebimento) {
		return ptmAvaliacaoTecnicaDAO.carregarAnaliseTecnicoCount(itemRecebimento);
	}

	@Override
	public ResponsavelAreaTecAceiteTecnicoPendenteVO obterResponsavelAreaTecnica(Integer seqAreaTecnica){
		return ptmAreaTecAvaliacaoDAO.obterResponsavelAreaTecnica(seqAreaTecnica);
	}

	@Override
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(String objPesquisa, List<Integer> listaSeqAreaTecnica) {
		return this.getPtmAreaTecAvaliacaoDAO().pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(objPesquisa, listaSeqAreaTecnica);
	}

	@Override
	public Number pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(String objPesquisa, List<Integer> listaSeqAreaTecnica) {
		return this.getPtmAreaTecAvaliacaoDAO().pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(objPesquisa, listaSeqAreaTecnica);
	}

}