package br.gov.mec.aghu.compras.business;

import javax.ejb.EJBException;

import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoDataEnvioFornecedorVO;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.contaspagar.vo.*;
import br.gov.mec.aghu.compras.pac.vo.AtaRegistroPrecoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoCadastradaImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.*;
import br.gov.mec.aghu.dominio.*;
import br.gov.mec.aghu.estoque.vo.*;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.DadosConciliacaoVO;
import br.gov.mec.aghu.faturamento.vo.DadosMateriaisConciliacaoVO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoAnexoDescricaoTecnica;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv1Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.model.ScoClassifMatNiv2Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.model.ScoClassifMatNiv3Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.model.ScoClassifMatNiv4Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedorId;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoFnRamoComerClasId;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.model.ScoFornecedorMarcaId;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.model.ScoHistoricoAdvertFornId;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.model.ScoHistoricoMultaFornId;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.model.ScoHistoricoOcorrFornId;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.model.ScoHistoricoSuspensFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoeJn;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.model.ScoMaterialVinculo;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPontoServidorId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoProgrAcessoFornAfp;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.model.ScoSociosFornecedoresId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.suprimentos.vo.RelAutorizacaoFornecimentoSaldoEntregaVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPagamentoProposVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IComprasFacade extends Serializable {

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	List<ScoMaterial> listarScoMateriais(Object objPesquisa, Boolean indEstocavel);

	List<ScoMaterial> listarScoMateriais(Object objPesquisa, Boolean indEstocavel, final Boolean pesquisarDescricao);

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais
	 * recuperando apenas os ativos, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa);

	Long listarScoMatriaisAtivosCount(Object objPesquisa);

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo. Ordenado por material.nome
	 * 
	 * @param objPesquisa
	 * @return
	 */
	List<ScoMaterial> pesquisarMateriais(Object objPesquisa);

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	Long listarScoMateriaisCount(Object objPesquisa);

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pelo nome, descricao ou código.
	 * 
	 * @param objPesquisa
	 * @param indEstocavel
	 * @param pesquisarDescricao
	 * @return count
	 */
	Long listarScoMateriaisCount(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao);

	/**
	 * Retorna fornecedores de acordo com número ou razão social.
	 * 
	 * @param objPesquisa
	 * @return Lista de fornecedor
	 */
	List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(Object objPesquisa);

	List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(Object parametro, int firstResult, int maxResults);

	/**
	 * Recupera uma lista de grupos de material por código ou descrição
	 * 
	 * @param parametro
	 *            Código ou descrição
	 * @return Lista de Grupos de Material
	 */
	List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(Object parametro);

	List<ScoMaterial> listarScoMateriaisPorGrupoEspecifico(Object objPesquisa, Integer firstResult, Integer maxResults,
			String orderProperty, Boolean asc, Integer pGrFarmIndustrial, Integer pGrMatMedic);

	ScoMaterialJN inserirScoMaterialJn(ScoMaterialJN materialJN);

	ScoMaterial obterScoMaterial(Object chavePrimaria);

	Long listarScoMateriaisPorGrupoEspecificoCount(Object objPesquisa, Integer pGrFarmIndustrial, Integer pGrMatMedic);

	List<ScoMaterial> listarMateriaisAtivos(String param, Boolean indEstocavel);

	Long listarMateriaisAtivosCount(String param, Boolean indEstocavel);

	ScoProgEntregaItemAutorizacaoFornecimento obterProgEntregaAutorizacaoFornecimentoPorId(ScoProgEntregaItemAutorizacaoFornecimentoId id);

	void atualizarScoFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao, ScoFaseSolicitacao faseSolicitacaoOld) throws BaseException;

	ScoItemPropostaFornecedor obterItemPropostaFornPorChavePrimaria(ScoItemPropostaFornecedorId id);

	void persistirProgramacaoEntregaAf(ScoProgEntregaItemAutorizacaoFornecimento progEntrega);

	ScoUnidadeMedida obterUnidadeMedidaPorSeq(String seq);

	List<SolicitacaoDeComprasVO> listarSolicitacoesDeComprasPorNumero(List<Integer> numSolicComp) throws ApplicationBusinessException,
			ApplicationBusinessException;

	List<ScoFornecedor> obterFornecedor(String param);

	List<ScoMarcaComercial> obterMarcas(Object param);

	void persistirFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca) throws ApplicationBusinessException;

	ScoFornecedorMarca obterScoFornecedorMarcaPorId(ScoFornecedorMarcaId id);

	void excluirScoFornecedorMarca(ScoFornecedorMarca fornecedorMarcaExcluir) throws ApplicationBusinessException;

	List<ScoFornecedorMarca> listaFornecedorMarca(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoFornecedor fornecedor, String descricaoMarca);

	Long listaFornecedorMarcaCount(ScoFornecedor scoFornecedor);

	// ScoFornecedorMarcaRN getscoFornecedorMarcaRN();

	String obterCnpjCpfFornecedorFormatado(ScoFornecedor fornecedor);

	String obterFornecedorDescricao(final Integer numero);

	String montarRazaoSocialFornecedor(ScoFornecedor fornecedor);

	/**
	 * @author lucasbuzzo #5521
	 * 
	 * @param Integer
	 *            nroLicitacao, Integer itemInicial, Integer itemFinal
	 * @return imprimirJulgamentoPropostasLicitacaoVO
	 */
	List<JulgamentoPropostasLicitacaoVO> gerarRelatorioJulgamentoPropostasLicitacao(Integer nroLicitacao, Integer itemInicial,
			Integer itemFinal);

	Long contarScoMateriaisGrupoAtiva(Object objPesquisa, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao);

	void inserirScoFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws BaseException;

	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricao(Object param);

	public Long pesquisarMarcaComercialPorCodigoDescricaoCount(Object param);

	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoAtivo(Object param);

	public Long pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(Object param);

	List<ScoFornecedor> listarFornecedoresAtivos(Object pesquisa, Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	
	List<FornecedorVO> pesquisarFornecedoresAtivosVO(final Object pesquisa,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc);
	
	List<ScoGrupoServico> listarGrupoServico(Object pesquisa);

	List<ScoMaterial> listarScoMateriaisGrupoAtiva(Object param, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao, Boolean orderNome)
			throws BaseException;

	List<ScoServico> listarServicosByNomeOrCodigoGrupoAtivo(Object param, ScoGrupoServico grupoSrv) throws BaseException;

	public Long listarServicosByNomeOrCodigoGrupoAtivoCount(Object param, ScoGrupoServico grupoSrv) throws BaseException;

	void persistirParcelaPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException;

	List<ScoMarcaComercial> getListaMarcasByNomeOrCodigo(Object paramString) throws BaseException;

	List<ScoMarcaComercial> getListaMarcasByNomeOrCodigo(Object objPesquisa, Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc) throws BaseException;

	Long getListaMarcasByNomeOrCodigoCount(Object objPesquisa);

	List<ScoMaterial> getListaMaterialByNomeOrCodigo(Object param) throws BaseException;

	List<ScoMaterial> pesquisarMaterialPorFiltro(Object _input);

	Long pesquisarMateriaisPorFiltroCount(Object _input);

	List<ScoServico> listarServicosAtivos(Object pesquisa);

	public Long listarServicosAtivosCount(Object pesquisa);

	Long listarFornecedoresAtivosCount(Object pesquisa);

	List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltro(Object _input);

	Long pesquisarGrupoMaterialPorFiltroCount(Object _input);

	List<ScoFornecedor> listarFornecedoresAtivos(Object pesquisa);

	List<ScoOrigemParecerTecnico> obterOrigemParecerTecnico(Object objPesquisa);

	Long obterOrigemParecerTecnicoCount(Object objPesquisa);

	ScoClassifMatNiv5 obterScoClassifMatNiv5PorSeq(Long numero);

	VScoClasMaterial obterVScoClasMaterialPorNumero(Long numero);

	List<VScoClasMaterial> pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(Object param, Short gmtCodigo);

	Boolean verificarComprasWeb(AghParametros param, ScoMaterial material);

	List<ScoGrupoMaterial> obterGrupoMaterialPorSeqDescricao(Object param);

	List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltroAlmoxarifado(Short almoxSeq, Object _input);

	List<ScoMaterial> pesquisaMateriaisPorParamAlmox(Short almoxSeq, String paramPesq);

	List<ScoMaterial> obterMateriais(Object param);

	List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraComAutForn(Integer numero);

	Boolean getScEmFases(Integer numero);

	List<ScoFornecedor> obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(String param, Short almoxSeq, Integer materialCodigo);

	/**
	 * Lista os fornecedores com propostas para determinada licitacao para
	 * suggestions
	 * 
	 * @param pesquisa
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param numeroPac
	 * @return
	 */
	List<ScoFornecedor> listarFornecedoresComProposta(Object pesquisa, Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer numeroPac);

	ScoMaterial obterMaterialPorId(Integer codigo);

	ScoFornecedor obterFornecedorPorNumero(Integer numero);

	ScoContatoFornecedor obterContatoFornecedor(final ScoContatoFornecedorId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	void processarPedidosPapelaria(Date date, String cron, RapServidores servidorLogado) throws ApplicationBusinessException;
	
	ScoFornecedor obterFornecedorPorChavePrimaria(Integer numero);

	ScoServico obterServicoPorId(Integer codigo);

	ScoItemAutorizacaoForn obterItemAutorizacaoFornPorId(Integer afnNumero, Integer numero);

	ScoMaterial obterScoMaterialPorChavePrimaria(Integer codigo);

	// INCLUIDO PROVISÓRIAMENTE PARA NÃO QUEBRAR CONTROLLER
	ScoMaterial obterScoMaterialPorChavePrimaria(Long codigo);

	ScoMaterial obterScoMaterialDetalhadoPorChavePrimaria(Integer codigo);

	Long pesquisarManterMaterialCount(ScoMaterial material,VScoClasMaterial classificacaoMaterial);

	List<ScoMaterial> pesquisarManterMaterial(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMaterial material,VScoClasMaterial classificacaoMaterial);

	DominioTipoFaseSolicitacao obterTipoFaseSolicitacaoPorNumeroAF(final Integer numeroAf);

	/**
	 * Retorna a quantidade e itens da lista de materiais para catálogo
	 * 
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	Long pesquisarListaMateriaisParaCatalogoCount(Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial,
			DominioSimNao estocavel, DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ,
			Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial);

	void excluirFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws ApplicationBusinessException;

	List<ScoMaterial> pesquisarMaterial(Object _input);

	// Estória # 6795
	List<RelAutorizacaoFornecimentoSaldoEntregaVO> obterListaAutorizacaoFornecimentoSaldoEntrega(final Integer lctNumero,
			final Integer sequenciaAlteracao, final Short nroComplemento);

	// Estória # 6795
	ScoMarcaComercial obterMarcaComercialPorCodigo(final Integer mcmCodigo);

	// Estória # 6795
	ScoNomeComercial obterNomeComercialPorMcmCodigoNumero(Integer mcmCodigo, Integer numero);

	// Estória # 6795
	ScoSolicitacaoServico obterDescricaoSolicitacaoServicoPeloId(Integer numero) throws ApplicationBusinessException;

	// Estória # 6795
	Long pesquisarNumeroParcelasCount(Integer cdpNumero);

	ScoGrupoMaterial obterGrupoMaterialPorId(Integer id);

	/**
	 * Método de pesquisa para a página de lista. Pesquisa por código e/ou
	 * descrição
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param marcaComercial
	 * @return
	 */
	List<ScoMarcaComercial> listaMarcaComercial(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMarcaComercial marcaComercial);

	Long pesquisarMarcaComecialCount(ScoMarcaComercial marcaComercial);

	/**
	 * Obtem o fornecedor pelo numero da AF
	 * 
	 * @param numeroAf
	 * @return ScoFornecedor
	 */
	ScoFornecedor obterFornecedorPorNumeroAf(Integer numeroAf);

	ScoMarcaComercial buscaScoMarcaComercialPorId(Integer codigo);

	ScoMarcaModelo buscaScoMarcaModeloPorId(Integer seqp, Integer codigo);

	List<ScoMarcaModelo> listaMarcaModelo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigo);

	Long pesquisarMarcaModeloCount(Integer codigo);

	ScoUnidadeMedida obterUnidadeMedidaPorId(String idUnidade);

	/**
	 * Obtém uma lista de ScoRefCodes que possuem o campo rvDomain = ao
	 * parâmetro informado.
	 * 
	 * @param rvDomain
	 * @return
	 * @author bruno.mourao
	 * @since 07/02/2012
	 */
	List<ScoRefCodes> buscarScoRefCodesPorDominio(String rvDomain);

	/**
	 * Busca utilizando os campos que estiverem preenchidos
	 * 
	 * @param refCode
	 * @param valorExato
	 *            - True indica que o operador nos campos sera =, False indica
	 *            que o like será utilizado
	 * @return
	 * @author bruno.mourao
	 * @since 28/03/2012
	 */
	List<ScoRefCodes> pesquisarScoRefCodesPorScoRefCodes(ScoRefCodes refCode, Boolean valorExato);

	/**
	 * Pesquisa fornecedor por vários campos de fornecedor que estejam
	 * preenchidos.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fornecedor
	 * @return
	 * @author bruno.mourao
	 * @since 08/03/2012
	 */
	List<ScoFornecedor> pesquisarFornecedorCompleta(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc,
			ScoFornecedor fornecedor, String cpfCnpj);

	Long pesquisarFornecedorCompletaCount(ScoFornecedor fornecedor, String cpfCnpj);

	/**
	 * Uma das queries utilizadas para verificar se o material eh classificado
	 * como imobilizado
	 * 
	 * @param matCodigo
	 * @return Boolean
	 */
	Boolean verificarMaterialImobilizado(Integer matCodigo);

	/**
	 * Persite o fornecedor. Se id = null um novo objeto é inserido, caso
	 * contrário, é atualizado. Flush já é executado.
	 * 
	 * @param fornecedor
	 * @author bruno.mourao
	 * @param oldFornecedor
	 * @throws ApplicationBusinessException
	 * @since 20/03/2012
	 */
	void persistirFornecedor(ScoFornecedor fornecedor) throws ApplicationBusinessException, BaseListException;

	List<ScoSolicitacaoDeCompra> listarSolicitacoesDeComprasPorCodigoPaciente(Integer pacCodigo);

	List<ScoMaterial> listarScoMateriaisAtiva(Object objPesquisa);

	List<RelUltimasComprasPACVO> obterItensRelatorioUltimasComprasMaterial(Integer numLicitacao, List<Short> itens,
			List<String> itensModalidade, DominioAgrupadorItemFornecedorMarca agrupador);

	Long getFatItensContaHospitalarComMateriaisOrteseseProteses(final Integer cthSeq, final Short seq, final Integer phiSeq,
			final Integer codGrupo);

	ScoMaterial obterMaterialPorCodigoSituacao(ScoMaterial material);

	Boolean existeMaterialEstocavelPorAlmoxarifadoCentral(Integer codigoMaterial, Short seqAlmoxarifado, Boolean indEstocavel);

	List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel, DominioSimNao generico,
			String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ, Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo,
			VScoClasMaterial classificacaoMaterial);

	Date recuperarUltimaDataGeracaoMovMaterial(Integer matCodigo, Short almSeq);

	RelatorioDiarioMateriaisComSaldoAteVinteDiasVO obterLicitacoesRelatorioMateriasAteVinteDias(Integer slcNumero);

	RelatorioDiarioMateriaisComSaldoAteVinteDiasVO pesquisarLicitacoesRelatorioMateriasAteVinteDiasUnionAll(Integer slcNumero);

	List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(Date dataCompetencia,
			Integer limiteDiasDuracaoEstoque, AghParametros paramClassificacaoComprasWeb);

	Long obterQuantidadeBloqueada(Integer matCodigo, Short EalSeq);

	Long obterQuantidadeTotal(Integer matCodigo, Short EalSeq);

	Long obterQuantidadeDisponivel(Integer matCodigo, Short EalSeq);

	Long pesquisaScoMateriaisRelMensal(Integer gmtCodigo, DominioSimNao indEstoc);

	List<ScoMaterial> obterMateriaisOrteseseProteses(BigDecimal paramVlNumerico, Object objPesquisa);

	List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa);

	Long obterMateriaisOrteseseProtesesAgendaCount(BigDecimal paramVlNumerico, String objPesquisa);

	List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome);

	List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome, int maxResults);

	void efetuarInclusao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	void efetuarAlteracao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	void efetuarRemocao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	List<String> obterRazoesExcessaoMaterial();

	Long consultarMaterialFarmaciaCount(ScoMaterial material);

	List<ScoMaterial> consultarMaterialFarmacia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMaterial material);

	ScoMaterial obterScoMaterialOriginal(Integer codigo);

	ScoMaterial obterScoMaterialOriginal(ScoMaterial scoMaterial);

	void persistirScoMaterial(ScoMaterial scoMaterial);

	void atualizarScoMaterial(ScoMaterial scoMaterial);

	ScoFornecedor obterScoFornecedorPorChavePrimaria(Integer codigo);
	
	ScoFornecedor obterScoFornecedorComCidadePorChavePrimaria(Integer codigo);

	void persistirScoMaterial(ScoFornecedor scoFornecedor);

	List<ScoFornecedor> pesquisarFornecedoresSaldoEstoque(Object objPesquisa, Short almSeq, Integer matCodigo, Integer maxResults);

	ScoFornecedor obterOriginalScoFornecedor(ScoFornecedor scoFornecedor);

	ScoFornecedor atualizarScoFornecedor(ScoFornecedor scoFornecedor);

	List<ScoMateriaisClassificacoes> pesquisarScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo);

	ScoMateriaisClassificacoes buscarPrimeiroScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo);

	List<ScoMateriaisClassificacoes> buscarScoMateriaisClassificacoesPorMaterialEClassificacao(Integer scoMaterialCodigo, Long classIni,
			Long classFim);

	void persistirScoMateriaisClassificacoes(ScoMateriaisClassificacoes scoMateriaisClassificacoes);

	void removerScoMateriaisClassificacoes(ScoMateriaisClassificacoes scoMateriaisClassificacoes);

	/**
	 * Baseado na PK do item da AF retorna uma lista de programacoes de entrega
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param filtraQtde
	 * @param filtraCanceladas
	 * @return List
	 */
	List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAf(Integer iafAfnNum, Integer iafNumero, Boolean filtraQtde,
			Boolean filtraCanceladas);

	List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAfAss(Integer iafAfnNum, Integer iafNumero);

	List<ScoFaseSolicitacao> pesquisarAutorizacaoFormAntigaNaoEncerrada(Integer codigoMaterial, String codigoUnidadeMedida);

	List<ScoFaseSolicitacao> pesquisarFasePorIafAfnNumeroIafNumero(Integer iafAfnNumero, Integer iafNumero);

	List<ScoServico> pesquisaCodigoSolicitacaoServico(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	Boolean pesquisaTipoFaseSolicitacao(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	List<ScoMaterial> pesquisaMaterialSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	ScoSolicitacaoDeCompra pesquisaSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	/**
	 * 
	 * @param itemAutorizacaoFornId
	 * @param isSC
	 * @return Integer numero da solicitação de compra original
	 * 
	 *         Método retorna o número da solicitação (de compra ou serviço)
	 *         original realizando a busca na tabela SCO_FASES_SOLICITACOES a
	 *         partir do ID do item da autorização
	 */
	Integer obterNumeroSolicitacaoCompraOriginal(ScoItemAutorizacaoFornId itemAutorizacaoFornId, boolean isSC);

	List<ScoFaseSolicitacao> pesquisaItensAutPropostaFornecedor(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	Boolean pesquisaIndConsignado(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	DominioTipoFaseSolicitacao pesquisaTipoFaseSolic(ScoItemAutorizacaoFornId itemAutorizacaoFornId);

	List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompra(Integer numero);

	List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(Integer numero);

	void inserirScoItemAutorizacaoForn(ScoItemAutorizacaoForn scoItemAutorizacaoForn);

	ScoItemAutorizacaoFornJn obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn);

	void inserirScoItemAutorizacaoFornJn(ScoItemAutorizacaoFornJn scoItemAutorizacaoFornJn);

	void atualizarScoMarcaComercialDepreciado(ScoMarcaComercial scoMarcaComercial);

	void inserirScoMarcaComercial(ScoMarcaComercial scoMarcaComercial);

	ScoMarcaComercial obterScoMarcaComercialOriginal(ScoMarcaComercial scoMarcaComercial);

	boolean verificarMarcaExistente(Integer codigo, String descricao);

	ScoMarcaComercial obterScoMarcaComercialPorChavePrimaria(Integer codigoMarca);

	ScoUnidadeMedida obterScoUnidadeMedidaOriginal(String codigo);

	ScoUnidadeMedida obterScoUnidadeMedidaPorChavePrimaria(String codigo);

	void inserirScoAutorizacaoFornJnComFlush(ScoAutorizacaoFornJn journalAutorizacaoFornecimento);

	List<ScoGrupoMaterial> pesquisaRelMensalMateriais(Date dataCompetencia, Integer codigo, DominioAgruparRelMensal agrupar);

	ScoGrupoMaterial obterScoGrupoMaterialPorChavePrimaria(Integer codigo);

	List<ScoMarcaModelo> pesquisarMarcaModeloPorMarcaComercial(Integer id);

	void atualizarScoMarcaModeloDepreciado(ScoMarcaModelo scoMarcaModelo);

	void inserirScoMarcaModelo(ScoMarcaModelo scoMarcaModelo);

	void inserirScoItemLicitacao(ScoItemLicitacao itemLicitacao);

	Long validarScoRefCodesPorTipoOperConversao(final String valor, final String dominio);

	List<ScoRefCodes> buscarScoRefCodesPorSituacao(String rvLowValue, String rvMeaning, List<String> rvLowValues);

	ScoClassifMatNiv5 obterClassifMatNiv5PorNumero(Long numero);

	List<Long> pesquisarNumerosClassificacaoGrupoMaterial(Integer codigoGrupo);

	/**
	 * Obtem condições de pagamento do PAC
	 * 
	 * @param número
	 *            Código do Fornecedor
	 * @param numero
	 *            Número da licitação.
	 * @param numero
	 *            Número do Item.
	 * @return lista de condições de pagamento da proposta do fornecedor
	 */
	public List<ScoCondicaoPagamentoProposVO> obterCondicaoPagamentoPropos(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem,
			Integer first, Integer max, String order, boolean asc);

	public Long obterCondicaoPagamentoProposCount(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem);

	/**
	 * Método verifica número de condições de pagamento cadastradas e valor do
	 * parâmetro P_ACEITA_UNICA_COND_PGTO retornando permissão para cadastrar
	 * nova condição de pagamento
	 * 
	 * @param frnNumero
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @return true permitindo novas condições de pagamento, false informa
	 *         restrição ao cadastro de novas condições
	 */
	public boolean permitirNovaCondicaoPgtoProposta(Integer frnNumero, Integer numeroLicitacao, Short numeroItem);

	/**
	 * Obtem condição de pagamento de proposta pelo ID.
	 * 
	 * @param id
	 *            ID da condição.
	 * @return Condição
	 */
	public ScoCondicaoPagamentoPropos obterCondicaoPagamentoPropos(Integer id);

	/**
	 * Insere condição de pagamento de proposta.
	 * 
	 * @param condicao
	 *            Condição a ser inserida.
	 * @param parcelas
	 *            Parcelas a serem inseridas.
	 * @throws BaseException
	 */
	void inserir(ScoCondicaoPagamentoPropos condicao, List<ScoParcelasPagamento> parcelas) throws BaseException;

	/**
	 * Atualiza condição de pagamento de proposta.
	 * 
	 * @param condicao
	 *            Condição a ser atualizada.
	 * @param parcelas
	 *            Parcelas a serem inseridas/atualizadas.
	 * @param parcelasExcluidas
	 *            Parcelas a serem excluídas.
	 * @throws BaseException
	 */
	void atualizar(ScoCondicaoPagamentoPropos condicao, List<ScoParcelasPagamento> parcelas, List<ScoParcelasPagamento> parcelasExcluidas)
			throws BaseException;

	/**
	 * Exclui condição de pagamento de uma proposta.
	 * 
	 * @param id
	 *            ID da condição de pagamento.
	 */
	public void excluirCondicaoPagamentoProposta(Integer id) throws ApplicationBusinessException;

	ScoFormaPagamento obterScoFormaPagamentoPorChavePrimaria(Short codigo);

	void inserirScoItemPropostaFornecedorDAO(ScoItemPropostaFornecedor scoItemPropostaFornecedor);

	void inserirScoLoteLicitacao(ScoLoteLicitacao scoLoteLicitacao);

	void inserirScoPropostaFornecedor(ScoPropostaFornecedor scoPropostaFornecedor);

	List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(Date dtCompetencia);

	List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisSemComprasCurvaAbc(Date dtCompetencia);

	void inserirScoMateriaisClassificacoeJn(ScoMateriaisClassificacoeJn scoMateriaisClassificacoeJn);

	void inserirScoMaterialJN(ScoMaterialJN scoMaterialJN);

	boolean existeMateriaisClassificacoesPorConsumoSinteticoMaterial(Integer codigoMaterial, final Long cn5Numero);

	public abstract List<ScoAutorizacaoForn> visualizarAFsGeradas(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao,
			Date dtPrevEntrega, DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) throws ApplicationBusinessException;

	public abstract void validaPreenchimentoCamposPesquisaAFsGeradas(Integer numeroLicitacao, Short nroComplemento)
			throws ApplicationBusinessException;

	public abstract Long visualizarAFsGeradasCount(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega,
			DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao) throws BaseListException, ApplicationBusinessException;

	public abstract List<ScoRefCodes> buscarScoRefCodesPorSituacao(String consultaCampo) throws BaseListException,
			ApplicationBusinessException;

	public abstract ScoFornecedor clonarFornecedor(final ScoFornecedor fornecedor) throws BaseException;

	public abstract SceItemRmps persistirItemRmps(final SceItemRmps itemNew, final SceItemRmps itemOld, final Boolean flush)
			throws BaseException;

	public abstract ScoFornecedor inserirScoFornecedor(final ScoFornecedor fornecedor, final boolean gravar)
			throws ApplicationBusinessException;

	public abstract ScoFornecedor atualizarScoFornecedor(final ScoFornecedor fornecedor, final ScoFornecedor oldFornecedor,
			final boolean gravar) throws ApplicationBusinessException;

	public abstract SceItemRmps cloneSceItemRmps(final SceItemRmps itemRmps) throws BaseException;

	// #5574
	public abstract void persistirScoMarcaComercial(ScoMarcaComercial marcaComercial) throws ApplicationBusinessException;

	public abstract void persistirScoMarcaModelo(ScoMarcaModelo marcaModelo, ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException;

	public abstract SceRmrPaciente persistirSceRmrPaciente(final SceRmrPaciente sceRmrPacienteNew, final SceRmrPaciente sceRmrPacienteOld,
			final Boolean flush) throws BaseException;

	public abstract SceRmrPaciente cloneSceRmrPaciente(final SceRmrPaciente paciente) throws BaseException;

	public abstract void removerNotaFiscal(final FatItemContaHospitalar itemExcluirNota) throws BaseException;

	List<ScoSolicitacaoDeCompra> listarSolicitacoesAparelho(Integer pacCodigo, Integer phi, Boolean indRecebimento, Long cn5Numero);

	/**
	 * Conta serviços cadastrados conforme critério.
	 * 
	 * @param criteria
	 *            Critério.
	 * @return Número de serviços cadastrados.
	 */
	Long contarServicos(ScoServicoCriteriaVO criteria);

	/**
	 * Pesquisa serviços conforme critério.
	 * 
	 * @param criteria
	 *            Critério.
	 * @param first
	 *            Primeiro resultado.
	 * @param max
	 *            Número máximo de resultados.
	 * @param order
	 *            Ordenar por.
	 * @param asc
	 *            Ordem crescente (true)/decrescente (false).
	 * @return Serviços encontrados.
	 */
	List<ScoServico> pesquisarScoServico(ScoServicoCriteriaVO criteria, Integer first, Integer max, String order, boolean asc);

	/**
	 * Altera um serviço.
	 * 
	 * @param servico
	 *            Serviço a ser alterado.
	 */
	void alterar(ScoServico servico);

	/**
	 * Inclui um serviço.
	 * 
	 * @param servico
	 *            Serviço a ser incluído.
	 */
	void incluir(ScoServico servico);

	List<ScoServico> listarServicos(Object param);

	Long listarServicosCount(Object param);

	/**
	 * 
	 * @param codigo
	 * @return
	 */
	// ScoPontoServidor obterPontoParadaServidor(final Short codigo);

	/**
	 * 
	 * @param pontoServidor
	 * @throws BaseListException
	 */
	public void inserirPontoParadaServidor(final ScoPontoServidor pontoServidor) throws BaseListException;

	public void removerPontoParadaServidor(ScoPontoServidorId idPontoServidor) throws ApplicationBusinessException;

	public Long pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(final Short codigo, final Integer matricula, final Short vinculo);

	public List<ScoPontoServidor> pesquisarPontoParadaServidorCodigoMatriculaVinculo(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short codigo, final Integer matricula, final Short vinculo);

	List<ItensSCSSVO> listarSCItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSC);

	List<ItensSCSSVO> listarSSItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSS);

	Long listarSCItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSC);

	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2)
			throws ApplicationBusinessException;
	
		
	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2, String ignorarMetodo3)
			throws ApplicationBusinessException;

	public Boolean verificaCamposFiltroData(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2)
			throws ApplicationBusinessException;

	public void validaFiltroPesquisa(FiltroConsSCSSVO filtroConsulta) throws ApplicationBusinessException;

	List<ScoFornecedor> listarFornecedoresByLicitacao(Object param, ScoLicitacao licitacao);

	List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa);

	List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa, Integer limite);

	Long contarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa);

	Long pesquisarVFornecedorPorCgcCpfRazaoSocialCount(final Object pesquisa);

	List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa, List<Integer> listaGmtCodigo);

	ScoMaterial obterMaterialPorCodigoDescricaoUnidadeMedida(Integer matCodigo, Object param);

	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo);

	public Long pesquisarMarcaModeloPorCodigoDescricaoCount(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo);

	public Integer obterMaxItemAF(Integer numAf);

	public ScoItemLicitacao obterItemLicitacaoPorNumeroLicitacaoENumeroItem(Integer numeroLCT, Short numeroItem) throws BaseException;

	/**
	 * Obtem parcelas de condição de pagamento de proposta.
	 * 
	 * @param condicao
	 *            Condição
	 * @return
	 */
	List<ScoParcelasPagamento> obterParcelas(ScoCondicaoPagamentoPropos condicao);

	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorItemAutorizacao(Integer iafAfnNumero, Integer iafNumero);

	/**
	 * Obtem uma Atorização de Fornecimento por sua chave primária.
	 * 
	 * @param numeroAf
	 * @return ScoAutorizacaoForn
	 */
	ScoAutorizacaoForn obterScoAutorizacaoFornPorChavePrimaria(Integer numeroAf);

	public List<ScoContatoFornecedor> pesquisarContatosPorFornecedor(Integer numFornecedor);

	void persistirContatoFornecedor(Integer numeroFrn, ScoContatoFornecedor scoContatoFornecedor) throws ApplicationBusinessException;

	void removerContatoFornecedor(ScoContatoFornecedorId id) throws ApplicationBusinessException;

	public List<ScoGrupoServico> pesquisarGrupoServico(String pesquisa);

	public Long pesquisarGrupoServicoCount(String pesquisa);

	public boolean verificarAlteracaoContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor);

	public void inserirScoLicitacao(ScoLicitacao scoLicitacao);

	public Long verificarValorAssinadoPorAf(Integer afnNumero);

	public GeracaoMovimentoEstoqueVO obterGeracaoMovimentoEstoque(Integer afnNumero, Integer numero, Integer matCodigo);

	public ScoFornecedor obterFornecedorComPropostaPorAF(ScoAutorizacaoForn scoAutorizacaoForn);

	public List<ScoFaseSolicitacao> obterFaseSolicitacao(Integer numero, boolean isNull, DominioTipoSolicitacao tipoSolicitacao);

	public List<ScoFornecedor> listarFornAtivosComPropostaAceitaAfsSemContratos(Object pesquisa);

	public List<ScoFaseSolicitacao> pesquisarFasePorLicitacaoNumero(Integer lctNumero);

	public ScoFaseSolicitacao obterFaseSolicitacaoPorNumero(Integer numero);

	void fatpAtuIchSce(final Integer rmpSeq, final Short numero, final Integer quantidade, final Integer phiSeqICH,
			final Date dataFimVinculoServidor) throws BaseException;

	List<ScoContaCorrenteFornecedor> obterScoContaCorrenteFornecedorPorFornecedor(final ScoFornecedor fornecedor);

	Boolean getSsEmFases(Integer numero);

	List<ScoLicitacao> listarLicitacoesPorNumeroEDescricao(Object param);

	//Long listarItensLicitacoesCount(Object param);

	Long listarLicitacoesCount(Object param);

	List<DadosConciliacaoVO> pesquisarDadosConcilicacao(Integer seq);

	List<DadosMateriaisConciliacaoVO> pesquisarDadosMaterialConcilicacao(Integer seq);
	
	ScoMaterial getMaterialPreferencialCUM(Integer codigoMat);
	
	Integer obterScoMaterialMaxId();

	ScoUnidadeMedida obterPorCodigo(String codigo);

	
	public List<ScoHistoricoAdvertForn> pesquisarAdvertenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException;
	
	public List<ScoHistoricoMultaForn> pesquisarMultasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException;
	
	public List<ScoHistoricoSuspensForn> pesquisarSuspensoesFornecedor(Integer numero, Date inicio, Date fim) throws BaseException;
	
	public List<ScoHistoricoOcorrForn> pesquisarOcorrenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException;
		
	public void persistirMulta(ScoHistoricoMultaForn multa) throws BaseException;
	
	public void persistirAdvertencia(ScoHistoricoAdvertForn advertencia) throws BaseException;
	
	public void persistirOcorrencia(ScoHistoricoOcorrForn ocorrencia) throws BaseException;
	
	public void persistirSuspensao(ScoHistoricoSuspensForn suspensao, RapServidores servidor) throws BaseException;
	
	public void removerAdvertencia(ScoHistoricoAdvertFornId advertenciaId);
	
	public void removerMulta(ScoHistoricoMultaFornId multaIdId);
	
	public void removerOcorrencia(ScoHistoricoOcorrFornId ocorrenciaId);
	
	public void removerSuspensao(ScoHistoricoSuspensFornId suspensaoId);	
	
	public List<ScoTipoOcorrForn> pesquisarTipoOcorrenciaPorCodigoOuDescricao(Object pesquisa);
	
	public Long pesquisarTipoOcorrenciaPorCodigoOuDescricaoCount(Object pesquisa);
	
	public ScoHistoricoAdvertForn obterAdvertenciaPorId(ScoHistoricoAdvertFornId id);
	
	public ScoHistoricoMultaForn obterMultaPorId(ScoHistoricoMultaFornId id);
	
	public ScoHistoricoOcorrForn obterOcorrenciaPorId(ScoHistoricoOcorrFornId id);
	
	public ScoHistoricoSuspensForn obterSuspensaoPorId(ScoHistoricoSuspensFornId id);

	List<ScoLicitacao> pesquisarLicitacao(ScoLicitacao licitacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	public List<PropostasVencedorasVO> listarItensLicitacao(Integer numeroLicitacao);

	ScoSolicitacaoDeCompra obterScoSolicitacaoDeCompraPorChavePrimaria(Integer numero);

	List<ScoPedItensMatExpediente> pesquisarItensNotaFiscalByNumeroNotaFiscal(Integer numeroNotaFiscal);

	List<ScoPedItensMatExpediente> pesquisarItensPedidoByNotaFiscalCodigoMaterial(final Integer numeroNotaFiscal, Integer codigoMaterial);

	/**
	 * Método para consultar a contagem de notas fiscais para serem validadas.
	 * Estas notas fiscais são consultadas através dos pedidos realizados, que
	 * ficam armazenados nas tabelas 'sco_pedidos_mat_expediente' e
	 * 'sco_ped_itens_mat_expediente'.
	 * 
	 * @param numeroNotaFiscal
	 * @param dataInicioEmissao
	 * @param dataFimEmissao
	 * @param dataInicioPedido
	 * @param dataFimPedido
	 * @return
	 */
	Long pesquisarNotasFiscaisCount(ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO);

	/**
	 * Método para consultar as notas fiscais para serem validadas. Estas notas
	 * fiscais são consultadas através dos pedidos realizados, que ficam
	 * armazenados nas tabelas 'sco_pedidos_mat_expediente' e
	 * 'sco_ped_itens_mat_expediente'.
	 * 
	 * @param asc
	 * @param orderProperty
	 * @param maxResult
	 * @param firstResult
	 * 
	 * @param numeroNotaFiscal
	 * @param dataInicioEmissao
	 * @param dataFimEmissao
	 * @param dataInicioPedido
	 * @param dataFimPedido
	 * @return
	 */
	List<ScoPedidoMatExpedienteVO> pesquisarNotasFiscais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO);

	void validarDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException;

	List<ScoPedidoMatExpediente> pesquisarScoPedidoMatExp(ScoPedidoMatExpedienteVO filtro, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) throws ApplicationBusinessException;

	Long pesquisarScoPedidoMatExpCount(ScoPedidoMatExpedienteVO filtro);

	ScoPedidoMatExpediente obterScoPedidoMatExpPorChavePrimaria(Integer seq);

	ScoDataEnvioFornecedorVO consultarRegistroComDetalhesDasParcelas(ParcelaAfPendenteEntregaVO parcelaAfPendenteEntregaVO);

	void atualizarScePedidoMatExpedienteById(Integer seq, DominioValidacaoNF situacao) throws ApplicationBusinessException;

	void atualizarScoPedidoMatExp(ScoPedidoMatExpediente pedido, Boolean recusa, RapServidores servidorAlteracao)
			throws ApplicationBusinessException;

	void validaPesquisaGeralAF(FiltroPesquisaGeralAFVO filtro) throws ApplicationBusinessException;

	String geraArquivoConsultaPorAF(FiltroPesquisaGeralAFVO filtro) throws IOException;

	String geraArquivoConsultaPorIAF(FiltroPesquisaGeralAFVO filtro) throws IOException;

	//void downloaded(String fileName) throws IOException;

	String geraArquivoConsultaPorPIAF(FiltroPesquisaGeralAFVO filtro) throws IOException;

	String geraArquivoConsultaPorPAF(FiltroPesquisaGeralAFVO filtro) throws IOException;

	List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(Integer codigoMaterial, DominioOperacoesJournal operacao);

	List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa, String colunaOrdenacao);

	void persistirMaterialVinculado(ScoMaterial material, ScoMaterial materialVinculado);

	void excluirMaterialVinculado(ScoMaterialVinculo scoMaterialVinculo) throws ApplicationBusinessException;

	List<ScoMaterialVinculo> obterMateriaisVinculados(ScoMaterial material);

	Boolean validarVisualizacaoHistoricoMaterial(Integer codigoMaterial);

	String salvarDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica, String descricao, List<ScoMaterial> listaMateriais,
			List<ScoAnexoDescricaoTecnica> listaAnexo) throws BaseException;

	ScoDescricaoTecnicaPadrao buscarScoDescricaoTecnicaPadraoByCodigo(Short codigo, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	ScoMaterialDescTecnica obterScoDescricaoTecnicaPorCodigo(Integer codigo, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	void deletarDescricaoTecnica(Short codigo);

	Long listarDescricaoTecnicaCount(ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material);

	List<ScoDescricaoTecnicaPadrao> listarDescricaoTecnica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material);

	List<ScoDescricaoTecnicaPadrao> listarScoDescricaoTecnicaPadrao(Object objPesquisa);

	Long listarScoDescricaoTecnicaPadraoCount(Object objPesquisa);

	Integer obterQuantidadeEslPendenteAF(Integer afnNumero, Integer numero);

	List<ScoAutorizacaoForn> pesquisarAFComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor, String tipo);

	List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF);

	ScoOrigemParecerTecnico obterOrigemParecerTecnicoPorId(Integer id);

	String associarMaterialDescricaoTecnicaByMaterial(ScoMaterial material, List<ScoDescricaoTecnicaPadrao> listaDescricao);

	List<ScoDescricaoTecnicaPadrao> buscarListaDescricaoTecnicaMaterial(ScoMaterial material);

	List<ScoDescricaoTecnicaPadrao> obterRelatorioDescricaoTecnica(Short codigoDescricaoTecnica);

	List<ScoMaterial> listarScoMateriaisAtivosOrderByCodigo(Object objPesquisa);

	Long contarVFornecedorPorCnpjRaiz(String cnpjRaiz);

	List<ScoMaterial> getListaMaterialByNomeOrDescOrCodigo(Object param);

	Long listarScoMateriaisAtivosCount(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao);

	void persistirAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException;

	ScoProgrCodAcessoForn buscarScoProgrCodAcessoForn(Integer seq);

	Long listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(Object param);

	List<ScoFornecedor> listarFornecedoresAtivosPorNumeroCnpjRazaoSocial(Object param);

	Long listarFornecedoresPorNumeroCnpjRazaoSocialCount(Object param);

	List<ScoFornecedor> listarFornecedoresPorNumeroCnpjRazaoSocial(Object param);

	List<ScoProgrCodAcessoFornVO> listarFornecedores(ScoFornecedor fornecedor, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	Long listarFornecedoresCount(ScoFornecedor fornecedor);

	void atualizarAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException;

	String buscarPendenciaAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor);

	void enviarEmailContatos(Integer seq) throws ApplicationBusinessException;

	void enviarEmailSenha(Integer seq, byte[] jasper) throws ApplicationBusinessException;

	String buscarSituacaoAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor, String colorSituacao);

	ScoProgrCodAcessoForn obterScoProgrCodAcessoFornPorChavePrimaria(Integer codigo);

	String programacaoAutomaticaParcelasAF(Integer afnNumero, Integer numero) throws ApplicationBusinessException;

	List<ParcelaAfPendenteEntregaVO> pesquisarParcelaAfPendenteEntrega(ParcelasAFPendEntVO vo);

	void validarPeriodoInformadoParcelasAfPendenteEntrega(ParcelasAFPendEntVO vo) throws ApplicationBusinessException;

	void validarCamposParaPesquisaParcelasAfPendenteEntrega(ParcelasAFPendEntVO vo) throws ApplicationBusinessException;

	void selecionarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo);

	void gravarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo) throws ApplicationBusinessException;

	List<ParcelasAFVO> recuperaAFParcelas(Integer numeroAutorizacao);

	List<AutFornEntregaProgramadaVO> obtemAutFornecimentosEntregasProgramadas(ScoGrupoMaterial grupoMaterial, ScoFornecedor fornecedor,
			Date dataInicial, Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso) throws ApplicationBusinessException;

	String gerarRelatorioCSVAFsPendentesComprador(ScoGrupoMaterial grupoMaterial, ScoMaterial material) throws IOException,
			ApplicationBusinessException;

	VScoComprMaterialVO pesquisaUltimaEntrega(Integer codigoMaterial);

	List<ItemAutFornEntregaProgramadaVO> obtemItemAutFornecimentosEntregasProgramadas(Integer afNumero, Date dataInicial, Date dataFinal,
			EntregasGlobaisAcesso entregasGlobaisAcesso) throws ApplicationBusinessException;

	List<VScoFornecedor> pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(Object pesquisa);

	Long pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(Object pesquisa);

	List<EntregaProgramadaGrupoMaterialVO> obtemGrupoMateriaEntregaProgramada(FiltroGrupoMaterialEntregaProgramadaVO filtro,
			Date dataLiberacao) throws ApplicationBusinessException;

	Date calculaDataLiberacao(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException;

	String validaDatas(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException;

	void programacaoAutomaticaParcelasAF(List<Integer> listNumeroAFs) throws ApplicationBusinessException;

	ProgramacaoEntregaGlobalTotalizadorVO totalizaValores(List<EntregaProgramadaGrupoMaterialVO> listagem);

	ProgramacaoEntregaGlobalTotalizadorVO totalizaValoresEntregaProgramada(List<AutFornEntregaProgramadaVO> listagem);

	ProgramacaoEntregaGlobalTotalizadorVO totalizaValoresItensEntregaProgramada(List<ItemAutFornEntregaProgramadaVO> listagem);

	File gerarArquivoCSVConsultaProgramacaoGeralEntregaAF(List<ProgrGeralEntregaAFVO> listagem) throws ApplicationBusinessException,
			IOException;

	ProgramacaoParcelaItemVO pesquisarProgParcelaItem(Integer iafAfnNumero, Integer iafNumero);

	List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItensAFParcelas(Integer iafAfnNumero, Integer iafNumero,
			Integer numero, Date dataInicial, Date dataFinal);

	List<AutorizacaoFornVO> listarAfComSaldoProgramar(Object objPesquisa);

	Long obterGrupoMaterialPorSeqDescricaoCount(Object param);

	Long listarAfComSaldoProgramarCount(Object objPesquisa);

	List<ScoMaterial> listarMateriaisAtivosPorGrupoMaterial(Object param, Integer codGrupoMaterial);

	public Long listarMaterialAtivoPorGrupoMaterialCount(Object param, Integer codGrupoMaterial);

	void setaValoresDefaults(FiltroConsSCSSVO filtro) throws ApplicationBusinessException;

	Long listarSSItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSS);
	
	List<ScoLicitacao> buscarLicitacaoPorNumero(Integer numeroPac, String[] modalidades);

	List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa, String ordenacao);

	Integer obterSeqFornecedorPorNumero(ScoFornecedor fornecedor);

	Integer obterProximoNumeroCrcFornecedor(ScoFornecedor fornecedor);

	boolean existePenalidade(ScoFornecedor fornecedor);

	long obterNumeroMulta(ScoFornecedor fornecedor);

	long obterNumeroOcorrencias(ScoFornecedor fornecedor);

	AtaRegistroPrecoVO pesquisarInfoAtaDeRegistroDePreco(Integer pacCodigo, Integer fornecedor);

	int obterNumeroPenalidades(ScoFornecedor fornecedor);

	long obterNumeroSuspensoes(ScoFornecedor fornecedor);

	long obterNumeroAdvertencias(ScoFornecedor fornecedor);

	List<ScoMaterial> obterMateriaisOrteseseProtesesSB(BigDecimal paramVlNumerico, Object objPesquisa);

	String gerarRelatorioEP(Integer anoEP) throws ApplicationBusinessException, IOException;

	String gerarRelatorioAP(Integer numeroPac) throws ApplicationBusinessException, IOException;

	DominioTipoFaseSolicitacao consultaAFMaterialServico(Integer nroAF);

	ScoDescricaoTecnicaPadrao buscarScoDescricaoTecnicaPadraoByCodigo(Integer codigo);

	public ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo(FiltroRelatoriosExcelVO filtroRelatoriosExcelVO)
			throws ApplicationBusinessException;

	/**
	 * Método responsável por realizar a chamada do método
	 * gerarArquivoTextoTitulosBloqueados do objeto relatorioExcelON
	 * 
	 * @return Retorna um objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do
	 *         arquivo CSV para download
	 */
	public ArquivoURINomeQtdVO gerarArquivoTextoTitulosBloqueados() throws ApplicationBusinessException;

	void persistirSocio(ScoSocios socio);

	void persistirSocioFornecedor(ScoSociosFornecedores socioFornecedor);

	boolean verificarSocioExistentePorCPF(Long cpf);

	void atualizarSocioFornecedor(ScoSociosFornecedores socioFornecedor);

	ScoSociosFornecedores buscarScoSociosFornecedores(ScoSociosFornecedoresId id);

	void atualizarSocio(ScoSocios socio);

	void removerScoSocios(ScoSocios socio);

	void removerScoSocioFornecedor(ScoSociosFornecedores scoSociosFornecedores);

	Long listarSociosCount(Integer filtroCodigo, String filtroNomeSocio, String filtroRG, Long filtroCPF);

	Long listarSociosFornecedoresCount(Integer filtroCodigo, String filtroNomeSocio, String filtroRG, Long filtroCPF,
			Integer filtroFornecedor);

	Long quantidadeFornecedorPorSeqSocio(Integer seq);

	List<ScoSocios> listarSocios(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer filtroCodigo,
			String filtroNomeSocio, String filtroRG, Long filtroCPF);

	List<ScoSociosFornecedores> listarSociosFornecedores(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroCodigo, String filtroNomeSocio, String filtroRG, Long filtroCPF, Integer filtroFornecedor);

	boolean verificarSocioExistentePorRG(String rg);

	Long listarClassifMatNiv5PorGrupoCount(Integer codGrupo, Object parametro);

	List<MaterialClassificacaoVO> listarMateriasPorClassificacao(Long cn5, Integer codGrupo);

	List<ScoClassifMatNiv5> listarClassifMatNiv5PorGrupo(Integer codGrupo, Object parametro);

	ScoMateriaisClassificacoes obterScoMateriaisClassificacoes(Object idRemocao);
	
	List<ScoGrupoServico> pesquisarGrupoServicos(Integer codigo,
						String descricao, Boolean engenharia, Boolean tituloAvulso, Integer first, Integer max,
						String order, boolean asc);

	Long pesquisarGrupoServicosCount(Integer codigo, String descricao,
			Boolean tituloAvulso, Boolean engenharia);

	void salvarScoGrupoServico(ScoGrupoServico grupoServico)
						throws BaseException;

	void editarScoGrupoServico(ScoGrupoServico grupoServico)
						throws BaseException;

	ScoGrupoServico obterGrupoServico(Integer codigo);

	void excluirScoGrupoServico(ScoGrupoServico grupoServico)
						throws BaseException;

	/**
	 * Estória 37808. Método para realizar a pesquisa de {@link FcpTitulo} com
	 * pagamento pendente.
	 * 
	 * @param dtInicialVencimentoTitulo
	 *            Data inicial do período de vencimento do título.
	 * @param dtFinalVencimentoTitulo
	 *            Data final do período de vencimento do título.
	 * @param dtInicialEmissaoDocumentoFiscal
	 *            Data inicial do período de emissão do documento fiscal.
	 * @param dtFinalEmissaoDocumentoFiscal
	 *            Data final do período de emissão do documento fiscal.
	 * @param fornecedor
	 *            Fornecedor que possui o título a ser pago.
	 * @return Coleção contendo o resultado da pesquisa.
	 * @throws BaseException
	 */
	public List<TituloPendenteVO> pesquisarTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor)
			throws BaseException;

	/**
	 * Estória 37808. Método para gerar o arquivo CSV de {@link FcpTitulo} com
	 * pagamento pendente.
	 * 
	 * @param dtInicialVencimentoTitulo
	 *            Data inicial do período de vencimento do título.
	 * @param dtFinalVencimentoTitulo
	 *            Data final do período de vencimento do título.
	 * @param dtInicialEmissaoDocumentoFiscal
	 *            Data inicial do período de emissão do documento fiscal.
	 * @param dtFinalEmissaoDocumentoFiscal
	 *            Data final do período de emissão do documento fiscal.
	 * @param fornecedor
	 *            Fornecedor que possui o título a ser pago.
	 * @return objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do arquivo
	 *         CSV para download
	 * @throws BaseException
	 */
	public ArquivoURINomeQtdVO gerarCSVTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor)
			throws BaseException;
	
	public abstract List<FcpCalendarioVencimentoTributosVO> listarCalendariosVencimentos(Date dataApuracao, DominioTipoTributo tipoTributoVencimento) throws BaseException;
	
	FcpCalendarioVencimentoTributos pesquisarFcpCalendarioVencimentoTributoPorCodigo(Integer numeroCalendarioVencimento);
	
	public List<MovimentacaoFornecedorVO> pesquisarMovimentacaoFornecedores(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException;
	

	public Long pesquisarMovimentacaoFornecedoresCount(Integer frnNumero, DominioSituacaoTitulo situacao, Integer notaRecebimento, String serie, 
			Integer nroAf, Short nroComplemento, Long nf) throws ApplicationBusinessException;
	
	public List<DatasVencimentosFornecedorVO> pesquisaDatasVencimentoFornecedor(Object fornecedor);

	public AghParametros pesquisarHospital(Object parametro);
	
	public FcpCalendarioVencimentoTributos persistirCalendarioVencimento( FcpCalendarioVencimentoTributos fcpCalendarioVencimento )
			throws ApplicationBusinessException;

	List<ScoFornecedorVO> buscarCertificadoDeRegistroDeCadastro(
			Integer numeroFrn);

	List<ScoRefCodes> obterClassificacaoEconomica(String string,
			String classificacaoEconomicaFornecedor);

	List<ScoMarcaModelo> pesquisarMacaModelo(Object marcaModelo);

	void adicionarMarcaModelo(ScoMarcaModelo marcaModelo, ScoMaterial material) throws ApplicationBusinessException;

	void excluirMarcaModelo(MarcaModeloMaterialVO itemExclusao,
			ScoMaterial material);

	List<ScoGrupoMaterial> pesquisarScoGrupoMaterial(Integer codigo,
			String descricao, Boolean converterParaBoolean,
			Boolean converterParaBoolean2, Boolean converterParaBoolean3,
			Boolean converterParaBoolean4, Boolean converterParaBoolean5,
			Boolean converterParaBoolean6, Boolean converterParaBoolean7,
			Integer ntdCodigo, Integer codMercadoriaBb,
			DominioDiaSemanaMes diaFavEntgMaterial, Short seqAgrupa);

	void excluirScoGrupoMaterial(Integer codigo) throws ApplicationBusinessException;

	void persistirScoGrupoMaterial(ScoGrupoMaterial grupoMaterial) throws ApplicationBusinessException;

	List<MateriaisParalClassificacaoVO> pesquisarMateriaisParaClassificar(
			ScoMaterial material);

	List<ScoMaterial> listarScoMateriaisPorGrupo(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao, Integer codGrupoMaterial);

	Long listarScoMateriaisPorGrupoCount(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao, Integer codGrupoMaterial);

	ScoFornecedor buscarFornecedorPorNumero(Integer numeroFornecedor);

	List<ScoSociosFornecedores> listarFornecedoresPorSeqSocio(Integer seq);

	ScoSocios buscarSocioPorSeq(Integer seq);

	void removerScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1) throws ApplicationBusinessException;

	void removerScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2) throws ApplicationBusinessException;

	void removerScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3) throws ApplicationBusinessException;

	void removerScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4) throws ApplicationBusinessException;

	void removerScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5); 

	ScoClassifMatNiv1 obterscoClassifMatNiv1PorChavePrimaria(
			ScoClassifMatNiv1Id id);

	ScoClassifMatNiv2 obterscoClassifMatNiv2PorChavePrimaria(
			ScoClassifMatNiv2Id id);

	ScoClassifMatNiv3 obterscoClassifMatNiv3PorChavePrimaria(
			ScoClassifMatNiv3Id id);

	ScoClassifMatNiv4 obterscoClassifMatNiv4PorChavePrimaria(
			ScoClassifMatNiv4Id id);

	void atualizarScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1,
			String descricaoNiv1);

	void inserirScoClassifMatNiv1(Integer codigo, String descricaoNiv1);

	void atualizarScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2,
			String descricaoNiv2);

	void inserirScoClassifMatNiv2(Integer gmtCodigo, Integer codigo,
			String descricaoNiv2);

	void atualizarScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3,
			String descricaoNiv3);

	void inserirScoClassifMatNiv3(Integer cn1GmtCodigo, Integer cn1Codigo,
			Integer codigo, String descricaoNiv3);

	void atualizarScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4,
			String descricaoNiv4);

	void inserirScoClassifMatNiv4(Integer cn2Cn1GmtCodigo,
			Integer cn2Cn1Codigo, Integer cn2Codigo, Integer codigo,
			String descricaoNiv4);

	void atualizarScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5,
			String descricaoNiv5);

	void inserirScoClassifMatNiv5(ScoClassifMatNiv4 scoClassifMatNiv4,
			String descricaoNiv5);

	List<MarcaModeloMaterialVO> pesquisarMarcaModeloMaterial(Integer codigo,
			Integer codigo2, Integer seqp) throws ApplicationBusinessException;
	
	public void removerCalendarioVencimentoTributo(FcpCalendarioVencimentoTributos calendarioVencimentoTributo);

	ScoLicitacao buscarLicitacaoPorNumero(Integer numeroParam);

	String geraArquivoUltimasCompras(List<ScoUltimasComprasMaterialVO> lista) throws IOException;
	
	List<ScoFornRamoComercial> obterRamosComerciais(Object param, Integer numeroFornecedor) throws BaseException;
    Long obterRamosComerciaisCount(Object param, Integer numero) throws BaseException;
    
    List<ClassificacaoVO> obterClassificacoes(Object param) throws BaseException;
    Long obterClassificacoesCount(Object param) throws BaseException;
    Long listarClassificacoesCount(Integer numero, Short rcmCodigo) throws BaseException;
    List<ClassificacaoVO> listarClassificacoes(Integer numero, Short rcmCodigo, String orderProperty, boolean asc) throws BaseException;
    void inserirScoFnRamoComerClas(ScoFnRamoComerClas scoFnRamoComerClas) throws BaseException;
    void removerScoFnRamoComerClas(ScoFnRamoComerClasId scoFnRamoComerClasId) throws BaseException;
    List<ScoClassifMatNiv1>  listarScoClassifMatNiv1PorGrupoMaterial(Integer codigoGrupoMaterial);
	
    List<ContasCorrentesFornecedorVO> buscarDadosContasCorrentesFornecedor(
			CadastroContasCorrentesFornecedorVO filtro);

	void deletarContaCorrenteFornecedor(ContasCorrentesFornecedorVO itemExclusao) throws BaseException;

	void verificaRegistrosExistentes(
			List<ContasCorrentesFornecedorVO> validationData)throws ApplicationBusinessException;

	void verificaContaPreferencialParaFornecedor(Integer numero,
			DominioSimNao montarPreferencia, boolean b) throws ApplicationBusinessException;

	ScoContaCorrenteFornecedor montarContaCorrenteFornecedor(
			CadastroContasCorrentesFornecedorVO filtro);

	void inserirContaCorrenteFornecedor(ScoContaCorrenteFornecedor montarContaCorrenteFornecedor);

	void atualizarContaCorrenteFornecedor(ContasCorrentesFornecedorVO item) throws BaseException;

	Long listarCriterioEscolhaFornCount(Short codigo, String descricao,
			DominioSituacao situacao);

	List<ScoCriterioEscolhaForn> listarCriterioEscolhaForn(Short codigo,
			String descricao, DominioSituacao situacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	ScoCriterioEscolhaForn obterCriterioEscolhaForn(Short codigo);

	void persistirCriterioEscolhaForn(ScoCriterioEscolhaForn criterioEscolhaForn) throws ApplicationBusinessException;

	void atualizarCriterioEscolhaForn(ScoCriterioEscolhaForn criterioEscolhaForn) throws ApplicationBusinessException;

	List<ScoEtapaPac> obterEtapaPacPorLicitacao(Integer numero);

	ModPacSolicCompraServicoVO obterModaldiadePacSocilitacao(Integer numero,
			String codigo);

	List<EtapasRelacionadasPacVO> acompanharHistorico(Integer numero,
			String descricaoObjeto, String codigoModalidade);

	List<LocalPACVO> pesquisarLocaisPac(Integer numero,
			String codigoModalidade, String descricaoObjeto);

	List<EtapaPACVO> pesquisaEtapasPac(Object etapa, Integer numero,
			LocalPACVO localPACVO, String descricaoObjeto,
			String codigoModalidade);

	List<EtapasRelacionadasPacVO> pesquisarEtapas(Integer numero,
			ModPacSolicCompraServicoVO modPacSolicCompraServicoVO,
			DominioSituacaoEtapaPac situacaoEtapa, LocalPACVO localPACVO,
			RapServidoresId idServidor, EtapaPACVO etapaVO, Date dataPac);

	void gravarNovaEtapa(ScoLocalizacaoProcesso localNovaEtapa, Integer numero,
			String descricaoNovaEtapa, Short novaEtapaTempoPrevisto) throws ApplicationBusinessException;

	void atualizarEtapa(Integer codigoEtapaAtlz,
			DominioSituacaoEtapaPac situacaoEtapaAtualizar,
			Short tempoPrevistoAtualizar, String descricaoObsAtualizar);

	List<HistoricoLogEtapaPacVO> pesquisarHistoricoEtapa(Integer codigoEtapa);
	
	List<ScoRamoComercial> listarRamosComerciaisAtivos(Object param);
	
public ScoFornecedorVO bindValoresLista(List<ScoFornecedorVO> listVO);
	
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAtrasados(Date dataFinal)
			throws ApplicationBusinessException, BaseException;
	
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAVencer(
			Date dataFinal) throws ApplicationBusinessException, BaseException;
	
	public String gerarCSVDividaResumo(Date dataFinal)
			throws ApplicationBusinessException, IOException, BaseException;
	
	/**
	 * Pesquisar lista titulos NR.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tituloVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<TituloNrVO> pesquisarListaTitulosNR(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, TituloNrVO tituloNrVO) throws ApplicationBusinessException;
	
	/**
	 * Pesquisar count titulos NR.
	 * @param tituloVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCountTitulosNR(TituloNrVO tituloNrVO) throws ApplicationBusinessException;
	
	public ArquivoURINomeQtdVO gerarArquivoTextoPagamentosRealizadosPeriodo (FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException;
	
	public List<PagamentosRealizadosPeriodoPdfVO> pesquisarPagamentosRealizadosPeriodoPDF(
			Date inicioPeriodo, Date finalPeriodo, Integer codVerbaGestao) throws ApplicationBusinessException;		
	
	
	/**
	 * Método de pesquisa dos {@link FcpTipoDocumentoPagamento}.
	 * 
	 * @param codigo
	 *            Código do tipo de pagamento.
	 * @param descricao
	 *            Descrição do tipo de pagamento.
	 * @param indSituacao
	 *            Situação do tipo de pagamento(ativo/inativo).
	 * @return {@link List} de {@link FcpTipoDocumentoPagamento}.
	 */
	public List<FcpTipoDocumentoPagamento> pesquisarTiposDocumentoPagamento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) ;
	
	
	/**
	 * Pesquisar count Tipo Documento Pagamento.
	 * @param FcpTipoDocumentoPagamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCountTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) ;
	
	/**
	 * Método de inserção do {@link FcpTipoDocumentoPagamento}, observando a regra que
	 * dita que não pode haver dois {@link FcpTipoDocumentoPagamento} com a mesma
	 * descrição ativo.
	 * 
	 * @param descricao
	 *            Descrição do tipo de pagamento.
	 * @param indSituacao
	 *            Situação do tipo de pagamento(ativo/inativo).
	 * @return Mensagem indicando se a operação foi bem sucedida ou não.
	 */
	public void inserirTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException;
	
	/**
	 * Método de atualização do {@link FcpTipoDocumentoPagamento}, observando a regra
	 * que dita que não pode haver dois {@link FcpTipoDocumentoPagamento} com a mesma
	 * descrição ativo.
	 * 
	 * @param codigo
	 *            Código do tipo de pagamento.
	 * @param descricao
	 *            Descrição do tipo de pagamento.
	 * @param indSituacao
	 *            Situação do tipo de pagamento(ativo/inativo).
	 * @return Mensagem indicando se a operação foi bem sucedida ou não.
	 */
	public void atualizarTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException;

	
	
	
	public List<RelatorioMovimentacaoFornecedorVO> pesquisaMovimentacaoFornecedor(Object fornecedor, Date dataInicio, Date dataFim) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException;

	void enviarEmailMovimentacaoFornecedor(String contatoEmail, byte[] jasper) throws ApplicationBusinessException;
	
	Long pesquisarContatoFornecedorPorNumeroCount(Integer frnNumero);

	List<ScoContatoFornecedor> pesquisarContatoFornecedorPorNumero(
			Integer frnNumero);

	public void cadastrarNomeComercialDaMarca(ScoMarcaComercial marcaComercial,
			String nome, boolean active) throws BaseException;

	public void alterarNovoNomeComercialDaMarca(ScoNomeComercial nomeSobEdicao,
			String nome, boolean active) throws BaseException;

	public Long buscaMarcasComeriaisCount(ScoMarcaComercial marcaComercial);

	public List<ScoNomeComercial> buscaMarcasComeriais(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			ScoMarcaComercial marcaComercial);
	
	List<ScoTipoOcorrForn> buscarTipoOcorrenciaFornecedor(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, CadastroTipoOcorrenciaFornecedorVO filtro);

	Long buscarTipoOcorrenciaFornecedorCount(CadastroTipoOcorrenciaFornecedorVO filtro);
	
	ScoTipoOcorrForn buscarOcorrenciaFornecedor(Short codigo);

	void inserirOcorrenciaFornecedor(ScoTipoOcorrForn ocorrencia) throws BaseException;

	Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa,
			List<Integer> listaGmtCodigo);

	/**
	 * Realiza a consulta de Materiais por codigo ou descrição.
	 * #43464
	 * @param parametro - Código ou descrição do Material
	 * @return Lista de Materiais
	 */
	public List<ScoMaterial> obterMateriaisPorCodigoOuDescricao(String parametro);
	
	
	/**
	 * Realiza a contagem dos objetos retornados da consulta de Materiais por código ou descrição.
	 * #43464
	 * @param parametro - Código ou descrição do Material
	 * @return Quantidade de registros retornados
	 */
	public Long obterMateriaisPorCodigoOuDescricaoCount(String parametro);
	
	List<ScoFaseSolicitacao> pesquisarFasePorItemLicitacao(Integer lctNumero, Short numeroItem);

	void procedureGeraAfpAutAutomatica(RapServidores servidorLogado) throws ApplicationBusinessException;
	
	public List<ScoModalidadeLicitacao> pesquisarModalidadesProcessoAdministrativo(String parametro);
	
	public Long pesquisarModalidadesProcessoAdministrativoCount(String parametro);
	
	public List<ScoLicitacao> pesquisarLicitacoesPorNumero(String parametro);
	
	public Long pesquisarLicitacoesPorNumeroCount(String parametro);
	
	public List<RapServidoresVO> pesquisarGestorPorNomeOuMatricula(String parametro);
	
	public Long pesquisarGestorPorNomeOuMatriculaCount(String parametro);
	
	public Long pesquisarLicitacoesVOCount(ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno);
	
	public List<ScoLicitacaoVO> pesquisarLicitacoesVO(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno);
	
	public void validarDataPregaoEletronicoBB(Date dataInicial, Date dataFinal) throws ApplicationBusinessException;

	public List<ScoLicitacao> pesquisarLicitacoesPorNomeArquivoRetorno(String nome);
	
	public void validarFormatoDataPregaoEletronicoBB(Date data) throws ApplicationBusinessException;
	
	public String gerarRealatorioES() throws ApplicationBusinessException, IOException;
	
	public Integer contadorLinhasRelatorioEntradaSemEmpenhoSemAssinaturaAF() throws ApplicationBusinessException, IOException;
	
	/**
	 * #5481 Gera arquivo de remessa de licitação para o pregão BB
	 * @param nrosPac
	 */
	public String montarArqPregaoBB(List<Integer> nrosPac, AghParametros paramDir) throws ApplicationBusinessException;
	
	public String montarSolCodBB(List<Integer>  P_PG_BB_NumLicitacoes, AghParametros  P_PG_BB_ParamDir) throws ApplicationBusinessException;
	
	/**
	 * #41955 Realiza a rotina de importação dos arquivos de retono do pregão BB.
	 * @param job É a rotina agendada.
	 */
	public void importarArqPregaoBBRetorno(final AghJobDetail job)throws ApplicationBusinessException;
	
	public List<Long> obterLoteLicitacaoSelecionada(Integer nroPac);
	
	public List<ScoLicitacaoImpressaoVO> imprimirHistoricoDetalhadoLicitacaoHomologada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException;
		
	void validarSolicitacaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException;	
	
	String validarNomeArqCad(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException;

	List<ScoLicitacaoCadastradaImpressaoVO> imprimirLicitacaoCadastrada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException;
	
	
	boolean consultarFornecedorAdjudicado(Integer numLicitacao, Integer numFornecedor);
	
	public List<ScoLicitacaoImpressaoVO> lerArquivoProcessadoLicitacaoHomologada(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException;
	List<ScoMaterial> listarMaterial(String objPesquisa, Boolean indEstocavel, Integer gmtCodigo, DominioSituacao situacao);
	
	Long listarMaterialCount(String objPesquisa, Boolean indEstocavel, DominioSituacao situacao);
	
	public Long contarParcelasPagamentoProposta(ScoCondicaoPagamentoPropos cond);
	
	List<MaterialOpmeVO> obterValorMateriaisProcedimento(Short seq);
		
	List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoSemLucene(Object param);
	
	List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricaoSemLucene(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo);

	List<ConsultaGeralTituloVO> consultaGeralTitulos(
			FiltroConsultaGeralTituloVO filtro);

	void excluirTitulosTelaConsultaGeral(ConsultaGeralTituloVO item);


	Long obterCountTipoTitulo(String parametro);

	List<FcpTipoTitulo> obterListaTipoTitulo(String parametro);

	/**
	 * #46298 - Suggestion Box de Grupo Serviço - Lista
	 */
	List<ScoGrupoServico> obterListaGrupoServicoPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Grupo Serviço - Count
	 */
	Long obterCountGrupoServicoPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Grupo Material - Lista
	 */
	List<ScoGrupoMaterial> obterListaGrupoMaterialPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Grupo Material - Count
	 */
	Long obterCountGrupoMaterialPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Serviço - Lista
	 */
	List<ScoServico> obterListaServicoAtivosPorCodigoOuNome(String filter);

	/**
	 * #46298 - Suggestion Box de Serviço - Count
	 */
	Long obterCountServicoAtivosPorCodigoOuNome(String filter);

	/**
	 * #46298 - Suggestion Box de Material - Lista
	 */
	List<ScoMaterial> obterListaMaterialAtivosPorCodigoNomeOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Material - Count
	 */
	Long obterCountMaterialAtivosPorCodigoNomeOuDescricao(String filter);

	/**
	 * #46298 - Retorna lista paginada de {@link SolicitacaoTituloVO}
	 */
	List<SolicitacaoTituloVO> recuperarListaPaginadaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	/**
	 * #46298 - Retorna lista com todos os valores de {@link SolicitacaoTituloVO}, respeitando o filtro informado.
	 */
	List<SolicitacaoTituloVO> recuperarListaCompletaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada);

	/**
	 * #46298 - Suggestion Box de Classificação do Titulo - Lista
	 */
	List<FcpClassificacaoTitulo> obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Classificação do Titulo - Count
	 */
	Long obterCountClassificacaoTituloAtivosPorCodigoOuDescricao(String filter);

	List<SolicitacaoTituloVO> obterTitulosSolicitacaoCompraServico(
			Integer ttlseq);

	void alterarSolicitacoes(List<SolicitacaoTituloVO> listaSolicitacao,
			ConsultaGeralTituloVO tituloAlterado) throws  ApplicationBusinessException, EJBException; 
	
	/**
	 * #46310 - Gerar Titulos Sem Licitação
	 */
	List<FcpTitulo> gerarTitulosSemLicitacao(TituloSemLicitacaoVO titulo, List<SolicitacaoTituloVO> listaSolicitacao) throws ApplicationBusinessException;
	public Boolean existeAcessoFornecedorPorFornecedorDtEnvio(ScoFornecedor fornecedor);
		
	public ScoAutorizacaoFornecedorPedido obterScoAutorizacaoFornecimentoPedido(ScoAutorizacaoFornecedorPedidoId id);
		
	public void persistirScoAutorizacaoFornecedorPedido(ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido);
	
	public void persistirScoProgrAcessoFornAfp(ScoProgrAcessoFornAfp scoProgrAcessoFornAfp);
		
	public Long contarProgrEntregasPorAfeAfeNum(Integer afNum, Integer afeNumero);
	
	public Boolean verificarAcessosFronAFP(Integer afnNumero, Integer afeNumero, ScoFornecedor forcenedor);

	List<MaterialOpmeVO> obterMateriaisOrteseseProtesesAgendaComValoreMarca(
			BigDecimal paramVlNumerico, String objPesquisa, String ordenacao,
			Date dtAgenda);

	FsoNaturezaDespesa obtemNaturezaDespesaPorMaterial(Integer materialId);

	void inserirScoFaseSolicitacaoSemRn(ScoFaseSolicitacao faseSolicitacao,
			Boolean flush) throws BaseException;
}
