package br.gov.mec.aghu.sig.custos.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioCidCustoPacienteDiagnostico;
import br.gov.mec.aghu.dominio.DominioColunaExcel;
import br.gov.mec.aghu.dominio.DominioComposicaoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioProcedimentoCustoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoaRestricoes;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtdProcedimentos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigEquipamentoPatrimonio;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.CalculoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoAtividadeVO;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteNodoVO;
import br.gov.mec.aghu.sig.custos.vo.DetalheConsumoVO;
import br.gov.mec.aghu.sig.custos.vo.EntradaProducaoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.ItemProcedHospVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoAssociadoAtividadeVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPorCentroCustoVO;
import br.gov.mec.aghu.sig.custos.vo.PesquisarConsumoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.VSigCustosCalculoCidVO;
import br.gov.mec.aghu.view.VSigAfsContratosServicos;
import br.gov.mec.aghu.vo.AghAtendimentosVO;

public interface ICustosSigFacade extends Serializable {

	void validarSomaPercentuaisDirecionadoresRateio(DominioSituacaoVersoesCustos situacaoObjetoCusto, List<SigObjetoCustoDirRateios> lista)
			throws ApplicationBusinessException;

	boolean validarExclusaoDirecionadorRateio(DominioSituacaoVersoesCustos situacao, Date dataInicio);

	void persistirListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista, List<SigObjetoCustoDirRateios> excluidos,
			SigObjetoCustoVersoes objetoCustoVersao);

	void validarAlteracaoListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista, SigObjetoCustoDirRateios objetoCustoDirRateio,
			Integer posicao, List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException;

	List<SigObjetoCustoCcts> pesquisarObjetosCustoCentroCusto(FccCentroCustos centroCustos);

	List<SigAtividades> pesquisarAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigAtividades atividade,
			FccCentroCustos fccCentroCustos);

	Long pesquisarAtividadesCount(SigAtividades atividade, FccCentroCustos fccCentroCustos);

	List<SigObjetoCustoHistoricos> pesquisarHistoricoAtividade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigAtividades atividade);

	Long pesquisarHistoricoAtividadeCount(SigAtividades atividade);

	List<SigAtividades> pesquisarAtividades(FccCentroCustos fccCentroCustos);

	SigAtividades obterAtividade(Integer seq);

	void persist(SigAtividades atividade);

	void excluirAtividade(SigAtividades atividade) throws ApplicationBusinessException;

	void persistEquipamento(SigAtividadeEquipamentos equipamento);

	void excluirEquipamentoAtividade(SigAtividadeEquipamentos equipamento);

	List<SigAtividadeEquipamentos> pesquisarListaEquipamentosAtividade(SigAtividades atividades);

	List<SigAtividadePessoas> pesquisarPessoasPorSeqAtividade(Integer seqAtividade);

	List<SigAtividadeServicos> pesquisarServicosPorSeqAtividade(Integer seqAtividade);

	void removerAtividadeCentroCustos(List<SigAtividadeCentroCustos> lista);
	
	SigAtividadeCentroCustos obterCentroCustoPorAtividade(Integer seqAtividade);

	void persistPessoa(SigAtividadePessoas sigAtividadePessoas);

	void excluirPessoa(SigAtividadePessoas sigAtividadePessoas);

	void validarInclusaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list) throws ApplicationBusinessException;

	void validarAlteracaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list) throws ApplicationBusinessException;

	List<SigAtividadeInsumos> pesquisarAtividadeInsumos(Integer seqAtividade);

	void validarInclusaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos) throws ApplicationBusinessException;
	
	void validarQtdeEspecifica(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException ;
	
	void validarQtdeVidaUtil(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException ;

	void persistInsumo(SigAtividadeInsumos insumo);

	void persistServico(SigAtividadeServicos servico);

	boolean verificaAtividadeEstaVinculadaAoObjetoCusto(SigAtividades atividade);

	List<FccCentroCustos> pesquisarCentroCustoSemObrasPeloTipoCentroProducao(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao,
			DominioTipoCentroProducaoCustos... tipos) throws BaseException;

	List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoes(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao, String nome,
			DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao);

	Long pesquisarObjetoCustoVersoesCount(SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao,
			String nome, DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao);

	List<SigObjetoCustoHistoricos> pesquisarHistoricoObjetoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigObjetoCustoVersoes objetoCustoVersao);

	Long pesquisarHistoricoObjetoCustoCount(SigObjetoCustoVersoes objetoCustoVersao);

	void validarAlteracaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos) throws ApplicationBusinessException;

	void excluirInsumo(SigAtividadeInsumos insumo);

	void excluirServico(SigAtividadeServicos servico);

	SigObjetoCustoVersoes obterObjetoCustoVersoes(Integer seq);
	
	SigObjetoCustoCcts obterObjetoCustoCctsPrincipal(Integer seq);

	void validarInclusaoServicoAtividade(SigAtividadeServicos servico, List<SigAtividadeServicos> listaServicos) throws ApplicationBusinessException;

	List<VSigAfsContratosServicos> obterAfPorContrato(ScoContrato contrato);

	VSigAfsContratosServicos obterAfPorId(Integer seq);

	boolean verificaVersoesAtivas(SigObjetoCustoVersoes objetoCustoVersao);

	void validarObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, DominioSituacaoVersoesCustos situacaoAnterior) throws ApplicationBusinessException;

	void gravarObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException;

	void alterarObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException;

	void gravarObjetoCustos(SigObjetoCustos sigObjetoCustos);

	void alterarObjetoCustos(SigObjetoCustos sigObjetoCustos);

	void gravarObjetoCustoCentroCusto(SigObjetoCustoCcts objetoCustoCentroCusto);

	void alterarObjetoCustoCentroCusto(SigObjetoCustoCcts objetoCustoCentroCusto);

	void persistirListaObjetoCustoCentroCusto(SigObjetoCustoVersoes objetoCustoVersao,
			List<SigObjetoCustoCcts> listaObjetoCustoCcts, List<SigObjetoCustoCcts> listaObjetoCustoCctsExclusao);

	void inativarVersaoAnterior(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException;

	boolean validarExclusaoAssociacaoVersoesObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao);

	boolean validarExclusaoObjetoCustoAtivoMaisUmMes(SigObjetoCustoVersoes objetoCustoVersao);

	List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoesAssistencial(FccCentroCustos fccCentroCustos, Integer seqSigObjetoCustoVersao, Object param);

	List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersaoAtivo(Integer seqObjetoCustoVersao);

	void excluirVersaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException;

	void validarInclusaoPhiObjetoCusto(SigObjetoCustoPhis sigObjetoCustoPhis, List<SigObjetoCustoPhis> listaPhis) throws ApplicationBusinessException;

	void validarInclusaoEquipamentoAtividade(SigAtividadeEquipamentos equipamento, List<SigAtividadeEquipamentos> listaEquipamentos)
			throws ApplicationBusinessException;
	
	public BigDecimal efetuarCalculoCustoMedioMaterial(ScoMaterial material);

	void persistPhi(SigObjetoCustoPhis sigObjetoCustoPhis);

	void persistComposicoesObjetoCusto(SigObjetoCustoComposicoes sigObjetoCustoComposicoes);

	void excluirComposicoesObjetoCusto(SigObjetoCustoComposicoes sigObjetoCustoComposicoes);

	List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersao(Integer seqObjetoCustoVersao);

	List<SigObjetoCustoDirRateios> pesquisarDirecionadorePorObjetoCustoVersao(Integer seqObjetoCustoVersao);
	
	List<SigObjetoCustoPhis> pesquisarPhiPorObjetoCustoVersao(Integer seqObjetoCustoVersao);

	void excluirPhi(SigObjetoCustoPhis sigObjetoCustoPhis);

	void validarInclusaoComposicaoObjetoCustoAssistencial(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException;

	void validarInclusaoComposicaoObjetoCustoApoio(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException;

	boolean possuiCalculo(SigAtividades atividade);

	void validarExclusaoComposicaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException;

	List<ComposicaoAtividadeVO> buscarComposicaoAtividades(FccCentroCustos filtoCentroCusto, SigAtividades filtroAtividade, DominioSituacao filtroSituacao)
			throws ApplicationBusinessException;

	List<SigAtividades> listarAtividadesRestringindoCentroCusto(FccCentroCustos centroCusto, Object objPesquisa);

	List<SigAtividades> listAtividadesAtivas(FccCentroCustos centroCusto, Object objPesquisa);

	List<SigAtividades> listAtividadesAtivas(List<FccCentroCustos> listCentroCusto, Object objPesquisa);

	List<SigObjetoCustoVersoes> pesquisarObjetoCustoIsProdutoServico(FccCentroCustos fccCentroCustos, Object param);

	List<SigObjetoCustoVersoes> buscaObjetoCustoPrincipalAtivoPeloCentroCusto(FccCentroCustos fccCentroCustos, Object paramPesquisa);

	void efetuaCargaExamesComoObjetoCusto(String descricaoUsual, FccCentroCustos centroCustoCCTS, String siglaExameEdicao)
			throws ApplicationBusinessException;

	void copiaObjetoCustoComposicoes(SigObjetoCustoVersoes sigObjetoCustoVersoes, List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes) throws ApplicationBusinessException;

	SigAtividades copiaAtividade(SigAtividades atividadesSuggestion, String nome, boolean pessoal, boolean insumos, boolean equipamentos, boolean servicos) throws ApplicationBusinessException;

	Map<String, Object> obterAtividadeDesatachado(Integer seqAtividade);

	List<SigObjetoCustoComposicoes> pesquisarObjetoCustoComposicaoAtivoObjetoCustoVersaoAtivo(SigAtividades atividade);

	Map<String, Object> obterObjetoCustoVersoesDesatachado(Integer seq);

	SigObjetoCustoVersoes criaNovaVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes) throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonio(Object paramPesquisa, Integer centroCustoAtividade)
			throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonioByDescricao(String descricao, Integer centroCustoAtividade)
			throws ApplicationBusinessException;

	EquipamentoSistemaPatrimonioVO pesquisarEquipamentoSistemaPatrimonioById(String idEquipamentoSistemaPatrimonio, Integer centroCustoAtividade)
			throws ApplicationBusinessException;

	List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentosSistemaPatrimonioById(List<String> listCodigo)	throws ApplicationBusinessException;
	
	List<ObjetoCustoAssociadoAtividadeVO> pesquisarObjetosCustoAssociadosAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigAtividades atividade);

	Integer pesquisarObjetosCustoAssociadosAtividadesCount(SigAtividades atividade);

	void iniciaProcessoHistoricoProduto(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis, List<SigObjetoCustoPhis> listaPhisExcluir,
			RapServidores rapServidores) throws ApplicationBusinessException;

	void iniciaProcessoHistoricoAtividade(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade, List<SigAtividadeInsumos> listAtividadeInsumos,
			List<SigAtividadeServicos> listaServicos, RapServidores rapServidores) throws ApplicationBusinessException;

	boolean validarCentroCustoComposicaoAssistencial(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, FccCentroCustos centroCustoObjetoCusto);

	Object[] validarAtividadePertenceAoCentroCustoComposicaoApoio(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<FccCentroCustos> listaCC);

	List<DominioSituacaoVersoesCustos> selecionaSituacaoPossivelDoObjetoCusto(DominioSituacaoVersoesCustos situacaoAnterior,
			SigObjetoCustoVersoes objetoCustoVersao);

	void removerObjetosCustoCentroCustos(SigObjetoCustoCcts elemento);

	void iniciaProcessoHistoricoCopiaObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, SigObjetoCustoVersoes objetoCustoVersaoSuggestion,
			List<SigObjetoCustoComposicoes> copia, RapServidores rapServidores) throws ApplicationBusinessException;

	List<EntradaProducaoObjetoCustoVO> efetuaLeituraExcel(InputStream arquivo, DominioColunaExcel colCentroCusto, DominioColunaExcel colValor, int linInicial)
			throws ApplicationBusinessException, IOException;

	List<SigObjetoCustos> pesquisarObjetoCustoAssociadoClientes(Object param, FccCentroCustos centroCusto);

	List<ObjetoCustoPorCentroCustoVO> pesquisarObjetoCustoPorCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao, SigCentroProducao centroProducao, DominioSituacao indSituacao);

	Integer pesquisarObjetoCustoPorCentroCustoCount(FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao,
			SigCentroProducao centroProducao, DominioSituacao indSituacao);

	List<Object[]> buscarAutorizacaoFornecimento(Object paramPesquisa);

	List<Object[]> pesquisarAutorizFornecServico(Integer tvdSeq);

	SigAtividadeServicos obterAtividadeServico(Integer atvServSeq);
	
	SigAtividadeServicos obterAtividadeServicoDetalhada(Integer atvServSeq);

	List<ComposicaoObjetoCustoVO> buscarComposicaoObjetosCusto(FccCentroCustos filtoCentroCusto, SigObjetoCustos filtroObjetoCusto,
			DominioSituacaoVersoesCustos filtroSituacao, DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto) throws ApplicationBusinessException;

	List<SigObjetoCustos> pesquisarObjetoCustoAssociadoCentroCustoOuSemCentroCusto(Object paramPesquisa, FccCentroCustos filtoCentroCusto);

	void persistirEquipamentoCirurgico(SigEquipamentoPatrimonio equipamento);

	void excluirEquipamentoCirurgico(SigEquipamentoPatrimonio equipamento);

	List<SigEquipamentoPatrimonio> pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio(String codigo);

	List<SigEquipamentoPatrimonio> buscaEquipametosCirurgicos(MbcEquipamentoCirurgico equipamentoCirurgico);

	boolean validarExclusaoDirecionadorRateioAssociadoCliente(List<SigObjetoCustoClientes> lista, SigDirecionadores direcionador);

	void validarUtilizacaoInsumoExclusiva(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException;
	
	/**
	 * Efetua as validações necessarias para adição de insumos em lote.
	 * @param atividadeInsumos
	 * @throws ApplicationBusinessException se insumo não for válido
	 */
	void validarAdicaoDeInsumosEmLote(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException;
	
	
	SigObjetoCustoVersoes atualizar(SigObjetoCustoVersoes objeto);

	SigObjetoCustoVersoes atualizarObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao);

	void desatacharObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao);

	SigObjetoCustoVersoes buscarObjetoCustoVersoesAtualizado(SigObjetoCustoVersoes sigObjCustoVersoes);

	
	void mudarValorViao(DominioVisaoCustoPaciente visao);
	
	/**
	 * Filtra SigProcessamentoCusto por situação(ões) passada por parametro. 
	 * Se o parametro campoOrderBy não for nulo ou em branco é adicionado 'order by' na consulta acordando com o parametro. 
	 * 
	 * @param situacao
	 * @param campoOrderBy
	 * @return
	 */
	public List<SigProcessamentoCusto> obterSigProcessamentoCustoPorSituacao(DominioSituacaoProcessamentoCusto situacao[], String campoOrderBy);
	
	List<SigProcessamentoCusto> obterListaCompetencias(DominioVisaoCustoPaciente visao);

	void adicionarCIDNaLista(List<AghCid> listaCID, AghCid cid) throws ApplicationBusinessException;

	void deletarCIDDaLista(List<AghCid> listaCID, AghCid cid);

	void adicionarCentroCustoNaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto) throws ApplicationBusinessException;

	void deletarCentroCustoDaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto);

	void adicionarEspecialidadesNaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade) throws ApplicationBusinessException;

	void deletarEspecialidadesDaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade);

	void adicionarEquipeNaLista(List<AghEquipes> listaEquipes, AghEquipes equipes) throws ApplicationBusinessException;

	void deletarEquipesDaLista(List<AghEquipes> listaEquipes, AghEquipes equipes);
	
	List<RapServidores> obterListaReponsaveisPorListaDeEquipes(List<AghEquipes> equipes);
	
	SigProcessamentoCusto obterSigProcessamentoCustoPorChavePrimaria(Integer integer);

	void validarPacienteInformadoNoFiltro(AipPacientes paciente) throws ApplicationBusinessException;
	
	void obterValoresCustosReceitasPorProntuario(List<AghAtendimentosVO> listaAtendimentoVO);
	
	void obterValoresCustosReceitasPorProntuarioEProcessamento(List<AghAtendimentosVO> listaAtendimentoVO, Integer pmuSeq);

	void validarConfirmar(List<AghAtendimentosVO> listagem) throws ApplicationBusinessException;

	void validarListaVaziaExibeMensagem(List<AghAtendimentosVO> listagem, String[] informacoesMensagem) throws ApplicationBusinessException;

	String[] buscarInformacoesParaMostrarMensagem(SigProcessamentoCusto competencia, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEquipes> listaEquipes, List<AghEspecialidades> listaEspecialidades);
	
	public List<AghAtendimentosVO> obterSelecionados(List<AghAtendimentosVO> listagem) throws ApplicationBusinessException;
	
	public List<ConsumoPacienteNodoVO> pesquisarConsumoPaciente(PesquisarConsumoPacienteVO vo);

	boolean verificarObjetoCustoPossuiComposicao(Integer seq);

	List<SigObjetoCustoVersoes> buscarObjetoCustoVersoesCentroCusto(
			Integer seqCct, String paramPesquisa);

	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq);

	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCusto(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, Short espSeq, Integer atdSeq, Boolean isEspecialidade, List<Integer> seqCategorias);

	List<CalculoAtendimentoPacienteVO> buscarCentrosCustoVisaoGeral(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq);

	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCustoCategoria(
			Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq);
	
	List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroCusto(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq);
	
	List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroProducao(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq);
	
	List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEspecialidade(Integer prontuario, Integer pmuSeq, Integer atdSeq);
	
	List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEquipeMedica(Integer prontuario, Integer pmuSeq, Integer atdSeq);
	
	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(
			Integer prontuario, Integer pmuSeq, List<Integer> categorias, Integer atdSeq);

	List<CalculoAtendimentoPacienteVO> buscarEspecialidades(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias);

	List<CalculoAtendimentoPacienteVO> buscarEquipesMedicas(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias);

	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteEquipeMedica(Integer prontuario, Integer pmuSeq, Integer matriculaResp, Short vinCodigoResp, Integer atdSeq, List<Integer> seqCategorias);

	List<CalculoAtendimentoPacienteVO> buscarCustosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq);
	
	List<SigCalculoAtdProcedimentos> buscarProcedimentosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq);

	BigDecimal buscarCustoTotal(Integer prontuario, Integer pmuSeq,	boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias);
	
	public BigDecimal buscarValorTotalReceita(Integer prontuario, Integer pmuSeq, boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias);
	
	List<CalculoAtendimentoPacienteVO> pesquisarReceitaGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq);
	
	BigDecimal buscarCustoTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis);
		
	BigDecimal buscarReceitaTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis);
	
	List<VSigCustosCalculoCidVO> pesquisarDiagnosticosPrimeiroNivel(Integer pmuSeq, String cidPrincipal, List<AghCid> listaCids, DominioCidCustoPacienteDiagnostico tipo);
	
	List<VSigCustosCalculoCidVO> pesquisarDiagnosticosSegundoNivel(Integer pmuSeq, Integer cidSeq, List<Integer> listaSeq, Integer quantidadeCids, DominioCidCustoPacienteDiagnostico tipo);
	
	List<VSigCustosCalculoCidVO> obterListaPacientes(Integer pmuSeq, List<Integer> listaAtendimentos, Integer cidSeq);
	
	List<ItemProcedHospVO> buscarProcedimentos(Integer pmuSeq, String descricao);
	
	Long buscarProcedimentosCount(Integer pmuSeq, String descricao);
	
	List<CalculoProcedimentoVO> buscarProcedimentosPrimeiroNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, ItemProcedHospVO procedimentoPrincipal, DominioProcedimentoCustoPaciente tipo, List<ItemProcedHospVO> procedimentos, List<Short> conveniosSelecionados);
	
	List<CalculoProcedimentoVO> buscarProcedimentosSegundoNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Short iphPhoSeq, Integer iphSeq, List<ItemProcedHospVO> listaProcedimento, Integer quantidadeProcedimentos, DominioProcedimentoCustoPaciente tipo, List<Short> conveniosSelecionados);
	
	List<CalculoProcedimentoVO> buscarPacientesProcedimentos(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, List<Integer> atendimentos, List<Short> conveniosSelecionados);
	
	List<Integer> buscarContasPaciente(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Integer atdSeq, List<Short> conveniosSelecionados);
	
	void adicionarProcedimentoNaLista(List<ItemProcedHospVO> listaProcedimentos, ItemProcedHospVO procedimento) throws ApplicationBusinessException;
	
	void deletarProcedimentoDaLista(List<ItemProcedHospVO> listaProcedimentos, ItemProcedHospVO procedimento);

	List<DetalheConsumoVO> listarDetalheConsumo(Integer atdSeq, Integer pmuSeq, Integer cctCodigo, String nomeObjetoCusto);

	SigCategoriaRecurso obterCategoriaRecursoPorChavePrimaria(Integer seq);

	List<SigAtividadePessoaRestricoes> listarAtividadePessoaRestricoes(SigAtividadePessoas sigAtividadePessoas);
	
}