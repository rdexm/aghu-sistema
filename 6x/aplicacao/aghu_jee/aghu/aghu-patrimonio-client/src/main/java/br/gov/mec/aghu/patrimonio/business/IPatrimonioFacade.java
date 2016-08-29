package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
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
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IPatrimonioFacade extends Serializable {

	/**
	 * Obtem lista paginada de Aceites Tecnicos a Serem Realizados
	 * #43464
	 * @return {@link List} de {@link AceiteTecnicoParaSerRealizadoVO}
	 */
	List<AceiteTecnicoParaSerRealizadoVO> recuperarListaPaginadaAceiteTecnico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroAceiteTecnicoParaSerRealizadoVO filtro);

	/**
	 * Obtem contagem de Aceites Tecnicos a Serem Realizados.
	 * #43464
	 * @return {@link Long} count
	 */
	Long recuperarCountAceiteTecnico(FiltroAceiteTecnicoParaSerRealizadoVO filtro);

	/**
	 * Obtem lista de Area Tecnica de Avaliação para Suggestion Box.
	 * @param parametro {@link String}
	 * @return {@link List} de {@link PtmAreaTecAvaliacao}
	 */
	List<PtmAreaTecAvaliacao> obterAreaTecAvaliacaoPorCodigoOuNome(String parametro);

	/**
	 * Obtem contagem de Area Tecnica de Avaliação para Suggestion Box.
	 * @param parametro {@link String}
	 * @return {@link Long} count
	 */
	Long obterAreaTecAvaliacaoPorCodigoOuNomeCount(String parametro);

	PtmAreaTecAvaliacao obterAreaTecAvaliacaoPorChavePrimaria(Integer seq);

	PtmAreaTecAvaliacao obterAreaTecnicaPorServidor(RapServidores servidor);
	
	PtmAreaTecAvaliacao obterAreaTecPorServidor (RapServidores servidor);

	/**
	 * Método responsável por verificar o tipo e realizar a consulta por itens de recebimento.
	 * 
	 * @param numeroRecebimento - Número de Recebimento
	 * @return Lista de itens de recebimento
	 */
	public List<AnaliseTecnicaBemPermanenteVO> consultarItensRecebimento(Integer numeroRecebimento) throws ApplicationBusinessException;

	/**
	 * Busca Área Técnica de Avaliação por código do Centro de Custo.
	 * 
	 * @param codigo - Código do Centro de Custo
	 * @return Lista de Área técnica de Avaliação
	 */
	List<PtmAreaTecAvaliacao> listarAreaTecAvaliacaoPorCodigoCentroCusto(Integer codigo);

	/**
	 * Obtém lista de {@link PtmAreaTecAvaliacao} por Código de Centro Custo, Código da Área ou Nome da Área.
	 * 
	 * @param parametro {@link String}
	 * @return {@link List} de {@link PtmAreaTecAvaliacao}
	 */
	List<PtmAreaTecAvaliacao> pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC(String parametro);

	/**
	 * Obtém count de {@link PtmAreaTecAvaliacao} para a consulta por Código de Centro Custo, Código da Área ou Nome da Área.
	 *
	 * @param parametro {@link String}
	 * @return {@link Long}
	 */
	Long pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount(String parametro);

	/**
	 * Atualiza os itens informados com a informação de Área Técnica selecionada.
	 * @param itens - Itens a serem atualizados
	 * @param area - Área selecionada
	 * @param numeroRecebimento - Número de Recebimento informado
	 */
	void atualizarItensRecebimentoAnaliseTecnica(List<AnaliseTecnicaBemPermanenteVO> itens, PtmAreaTecAvaliacao area, Integer numeroRecebimento)
			throws ApplicationBusinessException;

	//#43482
	public List <RapServidores> obterusuariosTecnicosPorVinculoMatNome(String parametro);
	
	public Long obterusuariosTecnicosPorVinculoMatNomeCount(String parametro);
	
	public List<UsuarioTecnicoVO> obterUsuariosTecnicosList(PtmAreaTecAvaliacao areaTecnica);
	
	public void validarAssociacaoDeTecnicoAreaAvaliacao(List <UsuarioTecnicoVO> tecnicosList, RapServidores tecnicoSB1, Integer seqAreaTec) throws ApplicationBusinessException;
	
	public void validarUnicidadeTecnicoPadrao(List <UsuarioTecnicoVO> tecnicosList) throws ApplicationBusinessException;
	
	public void gravarAlteracoesAssociarTecnicoPadrao(List <UsuarioTecnicoVO> tecnicosExcluidos, List <UsuarioTecnicoVO> tecnicosAdicionados);
	
	public List<PtmAreaTecAvaliacao> pesquisarOficinasAreaTecnicaAvaliacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao);

	public Long pesquisarOficinasAreaTecnicaAvaliacaoCount(String nomeAreaTec, FccCentroCustos centroCusto, RapServidoresId responsavel, DominioSituacao situacao);

	public void persistirAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao, AghuParametrosEnum perfilEnum)throws BaseException;
	
	public FccCentroCustos buscarCentroCustoResponsavelSuperior(FccCentroCustos fccCentroCustos, AghuParametrosEnum perfilEnum)  throws ApplicationBusinessException;
	
	List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacao(Object objPesquisa);
	
	Long pesquisarAreaTecnicaAvaliacaoCount(Object objPesquisa);
	
	List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(Object objPesquisa, RapServidores servidorLogado);
	
	Long pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(Object objPesquisa, RapServidores servidorLogado);
	
	List<RapServidores> pesquisarResponsavelTecnico(Object objPesquisa);
	
	Long pesquisarResponsavelTecnicoCount(Object objPesquisa);
	
	List<RapServidores> pesquisarResponsavelTecnicoAreaTecnicaAvaliacao(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException;
	
	void enviarEmailSolicitacaoTecnicaAnalise(PtmAreaTecAvaliacao areaTecnicaAvalicao, RapServidores responsavel,
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO, boolean permissaoChefiaAreaTecnicaAvaliacao, boolean permissaoChefiaPatrimonio)
					throws ApplicationBusinessException, ParseException;
	
	public List<PtmAreaTecAvaliacao> obterListaAreaTecnicaPorServidor(RapServidores servidor);

	/**
	 * Obtém todos os Tickets cadastrados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return Lista de PtmTicket
	 */
	List<PtmTicket> listarTodosTickets(Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	/**
	 * Obtém quantidade de registros retornados na consulta por todos os Tickets cadastrados.
	 * 
	 * @return count da lista de PtmTicket
	 */
	Long listarTodosTicketsCount();

	/**
	 * Obtém todos os User Tickets cadastrados, retornando-os em uma lista de objetos VO.
	 * 
	 * @return Lista de UserTicketVO
	 */
	List<UserTicketVO> listarTodosUserTicketVO();

	/**
	 * Atualiza um ticket, encaminhando para os técnicos informados.
	 * (RN01, Situações 3 e 4, estória #44286)
	 * 
	 * @param numeroTicket - Número do Ticket de Avaliação Técnica
	 * @param tecnicos - Técnicos para os quais o ticket será encaminhado
	 * @param caixasPostais - Pendências criadas para os usuários aos quais o ticket foi encaminhado
	 * 
	 * @throws ApplicationBusinessException 
	 */
	void encaminharTicketParaTecnico(Integer numeroTicket, List<RapServidores> tecnicos, List<AghCaixaPostal> caixasPostais) throws ApplicationBusinessException;

	/**
	 * Atualiza um ticket, alterando seu status para Em Atendimento, e atribuindo-o a um usuário.
	 * (RN02, estória #44286)
	 * 
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void assumirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico);

	/**
	 * Atualiza um ticket, alterando seu status para Concluido.
	 * 
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void concluirTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico);

	/**
	 * Atualiza um ticket, alterando seu status para Cancelado.
	 * 
	 * @param numeroTicket - Número do Ticket de Avaliação
	 * @param caixaPostal - Pendência criada para o usuário trabalhando no ticket
	 * @param tecnico - Técnico responsável pelo ticket
	 */
	public void cancelarTicketAvaliacao(Integer numeroTicket, AghCaixaPostal caixaPostal, RapServidores tecnico);

	/**
	 * Método principal que executa todas as RN's da estória 44297.
	 * RN01 da estória #44297
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void enviarPendenciaEmailTicket(AghJobDetail job) throws ApplicationBusinessException;

	Integer obterPagamentoParcialItemRecebimento(Integer recebimento, Integer itemRecebimento);

	/**
	 * #43446 - Ação do botão Gravar.
	 * @param aceites - Lista de Aceites Selecionados
	 * @param toDelete - Lista de Servidores Removidos
	 * @param toInsert - Lista de Servidores Incluidos
	 * @param pagParcial - Pagamento Parcial informado
	 * @param servidor - Usuario Logado
	 * @throws ApplicationBusinessException 
	 */
	void designarTecnicoResponsavel(List<AceiteTecnicoParaSerRealizadoVO> aceites, 
			List<RapServidores> toDelete, List<RapServidores> toInsert, Integer pagParcial, RapServidores servidor) 
			throws ApplicationBusinessException;

	/**
	 * #43446 - Obtem lista de Tecnicos do Item de Recebimento por Servidor
	 * @param servidor - instancia de RapServidores
	 * @param itemRecebimento 
	 * @param recebimento 
	 * @return Lista de PtmTecnicoItemRecebimento
	 */
	List<PtmTecnicoItemRecebimento> obterTecnicosPorServidor(RapServidores servidor, Integer recebimento, Integer itemRecebimento);
	
	/**
	 * Método que executa a RN03 da estória 45707.
	 * @param recebimento
	 * @param itemRecebimento
	 */
	public void atenderAceiteTecnico(Integer recebimento, Integer itemRecebimento);
	
	/**
	 * Obtém todos itens provisórios referente ao recebimento e item do aceite.
	 * @param recebimento
	 * @param itemRecebimento
	 * @return lista
	 */
	public List<PtmItemRecebProvisorios> pesquisarItemRecebProvisorios(Integer recebimento, Integer itemRecebimento, RapServidores servidor);

	/**
	 * Realiza as validações necessárias para a inicialização da tela de Retirada de Bem Permanente.
	 * 
	 * @param itensRetirada - Lista de itens selecionados na tela de Aceite Técnico
	 * @throws ApplicationBusinessException
	 */
	public void validarInicializacaoRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException;

	/**
	 * Realiza as validações necessárias para a inicialização da grid de detalhamento na tela de Retirada de Bem Permanente.
	 * 
	 * @param itensRetirada - Lista de itens selecionados na tela de Aceite Técnico
	 * @throws ApplicationBusinessException
	 */
	public void validarInicializacaoDetalharRetiradaBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws ApplicationBusinessException;

	/**
	 * Realiza a retirada de Bens Permanentes.
	 * 
	 * @param itensDetalhados - Itens a serem retirados
	 * @throws ApplicationBusinessException 
	 */
	public void registrarRetiradaBemPermanente(List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhados) throws ApplicationBusinessException;
	
	
	List<AceiteTecnicoParaSerRealizadoVO> obterSubItensRelatorioProtocoloRetBensPermanentes(List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList, 
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta, boolean reimpressao);
	
	/**
	 * #43475 Realiza Devolução de Bens Permanentes.
	 * 
	 * @param listaRegistrarDevolucaoBemPermanente - Itens a serem devolvidos.
	 * @throws ApplicationBusinessException 
	 */
	public void registrarDevolucaoBemPermanente(
			List<DevolucaoBemPermanenteVO> itensParaDevolucao)
			throws ApplicationBusinessException;
	
	/**
	 * #43475
	 * Obtem numero da nota fiscal.
	 * @param numRecebimento
	 * 
	 */
	public Long obterNotaPorNumeroRecebimento(Integer numrecebimento);
	
	/**
	 * #43475
	 * Obtem quantidades de itens/itens disponiveis.
	 * @param numRecebimento
	 * 
	 */
	public QuantidadeDevolucaoBemPermanenteVO obterQuantidadeItem(Integer numRecebimento, Integer itemRecebimento);
	
	/**
	 * #43475
	 * Carrega lista de itensParaDevolucao.
	 * @param numRecebimento
	 * 
	 */
	public List<DevolucaoBemPermanenteVO> carregarListaBemPermanente(Long seqItemPatrimonio, Integer vlrNumerico);

	/**
	 * @param seqAreaTecnica
	 * @return
	 */
	RapServidores obterResponsavelAreaTecnicaPorSeqArea(Integer seqAreaTecnica);

	public void validarInicializacaoReImpressaoBemPermanente(List<AceiteTecnicoParaSerRealizadoVO> itensRetirada) throws BaseListException, CloneNotSupportedException;

	
	Integer pesquisarNumeroNotaFiscal(Integer numero);

	void gravarNotificacaoTecnica(PtmNotificacaoTecnica ptmNotificacaoTecnica);

	PtmItemRecebProvisorios pesquisarIrpSeq(Integer recebimento, Integer itemRecebimento, RapServidores rapServidores);

	List<PtmNotificacaoTecnica> pesquisarNotificacoesTecnica(Long seq);
	
	public List<PtmTecnicoItemRecebimento> obterPorTecnicoItemRecebimento(Integer recebimento, Integer itemRecebimento, RapServidores servidorTecnico);
	
	public RapServidores obterServidorPorUsuario(String loginUsuario) throws ApplicationBusinessException ;
	
	
	public List<PtmSituacaoMotivoMovimento> obterTodasSituacoesMotivoMovimento();
	
	public void persistirMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException;
	
	public void atualizarMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException;
	
	public void excluirMotivoSituacaoDescricao(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException;
	
	public List<PtmDescMotivoMovimentos> pesquisarDescricoesSituacaoMovimento(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	public Long pesquisarDescricoesSituacaoMovimentoCount(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao);
	
	public PtmDescMotivoMovimentos obterPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos);
	
	public PtmSituacaoMotivoMovimento obterPtmSituacaoMotivoMovimentoPorChavePrimaria(PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento);
	
	public Object[] obterNomeMatriculaServidorCentroCusto(Integer codigoCentroCusto);
	
	public NotificacaoTecnicaItemRecebimentoProvisorioVO obterNotificacaoTecnicaItemRecebProvisorio(Long pntSeq);
	
	public Boolean verificarChefeParaAreaTecnica(Integer seq, RapServidores servidorCC);
	
	public boolean isResponsavelAreaTecnico(RapServidores servidorLogado, Integer seqAreaTecnica);
		
	//#44713
	//SB1 Seleção de bem permanente
	List<PtmBemPermanentes> listarSugestionPatrimonio(Object param);
	Long listarSugestionPatrimonioCount(Object param);
	//SB2 Seleção de Notificação Técnica
	List<PtmNotificacaoTecnica> listarSugestionNotificacaoTecnica(Object param);
	Long listarSugestionNotificacaoTecnicaCount(Object param);
	//SB3 Seleção de Materiais
	List<ScoMaterial> listarScoMateriaisSugestion(Object param);
	Long listarScoMateriaisSugestionCount(Object param);
	//SB4 Seleção de recebimento
	List<PtmItemRecebProvisorios> listarConsultaRecebimentosSugestion(Object param);
	Long listarConsultaRecebimentosSugestionCount(Object param);
	//SB5 Seleção de Servidores. Ao selecionar um item, trazer na label de descrição a Matricula e Nome. 
	List<RapServidores> consultarUsuariosSugestion(Object param);
	Long consultarUsuariosSugestionCount(Object param);
	
	void obterAreaTecnicaPorCodigo(Integer codigoCentroCustos);

	void obterCentroCustoSuperiorPorSituacao(Integer cctSuperior);

	void obterCentroCustoResponsavelPorUsuario(Integer matriculaChefia, Short vinculoChefia);
	
	public List<PtmBemPermanentes> obterPtmBemPermanentesPorNumeroDescricao(Object filtro);

	public Long obterPtmBemPermanentesPorNumeroDescricaoCount(Object filtro);
	
	public DadosEdificacaoVO obterDadosEdificacaoDAO(Integer seq);
		
	public List<DadosEdificacaoVO> obterListaDadosEdificacaoDAO(String nome, String descricao, AipLogradouros logradouro, DominioSituacao situacao, 
			AipCidades municipio, AipUfs uf, AipBairros bairros, Integer numeroEdificacao, String complemento, AipCepLogradouros cep,
			AipLogradouros logradouroS, PtmBemPermanentes bemPermanentes, Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	
	public List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep, Integer codigoCidade) throws ApplicationBusinessException;
	
	public void alterarEdificacao(DominioSituacao situacao, String nome, String descricao, Long bpeSeq,
			Integer lgrSeq, Integer numero, String complemento, double latitude, double longitude, Integer edfSeq) throws ApplicationBusinessException;
	
	public void gravarEdificacao(DominioSituacao situacao, String nome, String descricao, Long bpeSeq,
			Integer lgrSeq, Integer numero, String complemento, double latitude, double longitude) throws ApplicationBusinessException;
	
	public PtmBemPermanentes obterBemPermanentesPorNumeroBem(Long numeroBem);
	
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaSuggestionBox(String parametro, RapServidores servidor);
	
	public List<PtmAreaTecAvaliacao> pesquisarListaAreaTecnicaPorResponsavel(String parametro, RapServidores servidor);
	
	public Long pesquisarListaAreaTecnicaSuggestionBoxCount(String parametro, RapServidores servidor);
	
	public Long pesquisarListaAreaTecnicaPorResponsavelCount(String parametro, RapServidores servidor);
	
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaPorPermissoes(String parametro, RapServidores servidor);
	
	public Long pesquisarAreaTecnicaPorPermissoesCount(String parametro, RapServidores servidor);
	
	//#48782
	public List<FccCentroCustos> pesquisarCentroCustosPorServidorLogadoDescricao(String filtro, RapServidores servidor);
	public Long pesquisarCentroCustosPorServidorLogadoDescricaoCount(String filtro, RapServidores servidor);
	public List<PtmBemPermanentes> obterPatrimonioPorNrBemDetalhamento(String param);
	public Long obterPatrimonioPorNrBemDetalhamentoCount(String param);
	public List<AvaliacaoTecnicaVO> listaAceiteTecnico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor);
	public Long listarAceitetecnicoCount(AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor);
	public List<PtmItemRecebProvisorios> listarPatrimonioPorItemReceb(String param, RapServidores servidor);
	public Long listarPatrimonioPorItemRecebCount(String param, RapServidores servidor);
	public boolean verificarUsuarioLogadoResponsavelPorAceite(Long seqItemReceb, RapServidores servidor);
	public RapServidoresVO verificarUsuarioLogadoResponsavelPorAreaTecnica(Integer itemRecebimento, Integer recebimento);
	public void excluirAvaliacaoTecnica(Integer seq, RapServidores servidor)  throws ApplicationBusinessException;
	public List<PtmItemRecebProvisorios> listarItemRecebPorSeqItemReceb(Integer recebimento, Integer itemRecebimento);
	//#44713 - C5 Pesquisar arquivos anexos por notificacao tecnica
	List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorNotificacaoTecnica(ArquivosAnexosPesquisaFiltroVO filtro,final Integer firstResult, final Integer maxResults);
	//#44713 - C5 Pesquisar arquivos anexos por notificacao tecnica (COUNT)
	Long pesquisarArquivoAnexosPorNotificacaoTecnicaCount(ArquivosAnexosPesquisaFiltroVO filtro);
	//#44713 - C13 Pesquisar arquivos anexos por patrimonio
	List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorPatrimonio(ArquivosAnexosPesquisaFiltroVO filtro,final Integer firstResult, final Integer maxResults);
	//#44713 - C13 Pesquisar arquivos anexos por patrimonio (COUNT)
	Long pesquisarArquivoAnexosPorPatrimonioCount(ArquivosAnexosPesquisaFiltroVO filtro);
	//#44713 - C12 Pesquisar arquivos anexos por recebimento
	List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorRecebimento(ArquivosAnexosPesquisaFiltroVO filtro,Long irpSeq,final Integer firstResult, final Integer maxResults, String orderProperty, boolean asc);
	//#44713 - C12 Pesquisar arquivos anexos por notificacao tecnica (COUNT)
	Long pesquisarArquivoAnexosPorRecebimentoCount(ArquivosAnexosPesquisaFiltroVO filtro,Long irpSeq);
	PtmArquivosAnexos obterDocumentoAnexado(Long seq);
	public PtmItemRecebProvisorios pesquisarItemRecebSeq(Integer recebimento, Integer itemRecebimento);
	
	DetalhamentoArquivosAnexosVO obterVisualizacaoDetalhamento(Long aaSeq, String tipo);
	public PtmArquivosAnexos obterVisualizacaoDetalhesAnexo(Long seqAnexo);
	
	public Long obterIrpSeqNotificacoesTecnica(Long irpSeq);

	public PtmArquivosAnexos obterArquivosAnexosPorId(Long seq);

	/** #48784 */
	public Double carregarCampoDaTela(PtmAvaliacaoTecnica avaliacaoTecnica);
	public List<PtmDesmembramento> pesquisarDesmembramentoPorAvtSeq(Integer irpSeq);
	public List<DevolucaoBemPermanenteVO> pesquisarBensPermanentesPorSeqPirp(Long irpSeq, boolean edicao, Integer avtSeq);
	public void excluirDesmembramento(Integer seq);
	public void registrarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor);
	public void finalizarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor);
	public void certificarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor);
	public PtmAvaliacaoTecnica obterAvaliacaoTecnicaPorSeq(Integer seq);
	//44800
	public List<PtmEdificacaoVO> pesquisarEdificacoesAtivasNrBemouSeqouNome(String strPesquisa);
	
	//44800
	public Long pesquisarEdificacoesAtivasNrBemouSeqouNomeCount(String strPesquisa);
	
	//44800 - C3
	public List<LocalizacaoFiltroVO>pesquisarListaPaginadaLocalizacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, LocalizacaoFiltroVO localizacaoFiltro); 
	
	//44800 - C3
	public Long pesquisarListaPaginadaLocalizacaoCount(LocalizacaoFiltroVO localizacaoFiltro);
	
	public void persistirPtmLocalizacoes(PtmLocalizacoes ptmLocalizacoes)throws BaseException;
	
	public void atualizarPtmLocalizacoes(PtmLocalizacoes ptmLocalizacoes)throws BaseException;
	
	public void atualizarPtmLocalizacoesJN(PtmLocalizacoesJn ptmLocalizacoesJn);
	
	public void inserirPtmLocalizacoesJN(PtmLocalizacoesJn ptmLocalizacoesJn);
	
	public PtmLocalizacoes obterPtmLocalizacoes(Long seqPtmLocalizacoes);
	
	void enviarNotificacaoAceitePendente(AghJobDetail job) throws ApplicationBusinessException;

	public void inserirArquivoAnexos (PtmArquivosAnexos anexoForm, Long aaSeq, PtmNotificacaoTecnica notificacaoTecnica, Long irpSeq, boolean flagEditar);
	
	//43468 c1
	ImprimirFichaAceiteTecnicoBemPermanenteVO recuperarCamposFixosRelatorio(Integer avtSeq);
	
	//43468 c2
	List <PtmBemPermanentes> recuperarCamposVariaveisRelatorio(Integer avtSeq);
	
	public void inserirNotaItemReceb(Long aaSeq, Long irpSeq);
	
	public PtmItemRecebProvisorios listarConsultaRecebimentosSugestionGrid(PtmItemRecebProvisorios ptmItemRecebProvisorios);

	public void validarArquivoAnexo(PtmArquivosAnexos anexoSelecionado) throws ApplicationBusinessException;
	
	Long pesquisarQtdeItemRecebAssociadoAreaTecnica(Integer seqAreaTec);

	public Long obterListaDadosEdificacaoDAOCount(String nome, String descricao, AipLogradouros logradouro, DominioSituacao situacao, 
			AipCidades municipio, AipUfs uf, AipBairros bairros, Integer numeroEdificacao, String complemento, AipCepLogradouros cep,
			AipLogradouros logradouroS, PtmBemPermanentes bemPermanentes);	
	//Melhoria #51196
	public List<PtmArquivosAnexos> obterAnexoArquivoAceiteTecnico(Integer aceiteTecnico);
		
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosComAF(Integer recebimento, Integer itemRecebimento);
		
	public List<RelatorioRecebimentoProvisorioVO> obterRecebimentosESL(Integer recebimento, Integer itemRecebimento);

	public TecnicoItemRecebimentoVO buscarTecnicoResponsavelPorMatENome(Integer nrpSeq, Integer nroItem);

	public TecnicoItemRecebimentoVO obterTecnicoResponsavelPorMatENome(Long numeroRecebimento);
	
	public List<ItemRecebimentoVO> carregarItemRecebimento(Integer numRecebimento, Integer itemRecebimento);
	
	public NomeUsuarioVO obterNomeMatriculaUsuario(Long irpSeq);
	
	public List<PtmNotificacaoTecnica> obterqNotificacoesTecnica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Long irpSeq);
	
	public Long obterqNotificacoesTecnicaCount(Long irpSeq);
	
	public List<TicketsVO> carregarTicketsItemRecebimentoProvisorio(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	public Long carregarTicketsItemRecebimentoProvisorioCount(ItemRecebimentoVO itemRecebimento);
	
	public Integer verificarResponsavelAceiteTecnico(RapServidores servidor, List<Long> seqRecebProvisorios);
	
	public List<AvaliacaoTecnicaVO> carregarAnaliseTecnico(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	public Long carregarAnaliseTecnicoCount(ItemRecebimentoVO itemRecebimento);
	
	public PtmEdificacao obterPtmEdificacaoPorSeq(Integer seqPtmEdificacao);
	
	public ResponsavelAreaTecAceiteTecnicoPendenteVO obterResponsavelAreaTecnica(Integer seqAreaTecnica);
	
	public List<PtmAreaTecAvaliacao> listarAreaTecnicaAvaliacaoAbaixoCentroCusto(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException;

	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaAvaliacaoAssociadoCentroCusto(String objPesquisa, List<Integer> listaAreaTecnica);

	public Number pesquisarAreaTecnicaAvaliacaoAssociadoCentroCustoCount(String objPesquisa, List<Integer> listaAreaTecnica);
}