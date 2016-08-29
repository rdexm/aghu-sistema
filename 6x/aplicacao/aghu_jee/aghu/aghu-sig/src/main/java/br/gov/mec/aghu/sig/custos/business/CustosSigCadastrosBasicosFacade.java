package br.gov.mec.aghu.sig.custos.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigEscalaPessoa;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.DetalhamentoCustosGeralVO;
import br.gov.mec.aghu.sig.custos.vo.DetalheProducaoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPesoClienteVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoRateioServicoDAO;
import br.gov.mec.aghu.sig.dao.SigCategoriaConsumosDAO;
import br.gov.mec.aghu.sig.dao.SigCentroProducaoDAO;
import br.gov.mec.aghu.sig.dao.SigComunicacaoEventosDAO;
import br.gov.mec.aghu.sig.dao.SigDetalheProducaoDAO;
import br.gov.mec.aghu.sig.dao.SigDirecionadoresDAO;
import br.gov.mec.aghu.sig.dao.SigEscalaPessoaDAO;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacaoCargosDAO;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacoesDAO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;


@Modulo(ModuloEnum.SIG_CUSTOS_ATIVIDADE)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class CustosSigCadastrosBasicosFacade extends BaseFacade implements Serializable, ICustosSigCadastrosBasicosFacade {


@EJB
private GrupoOcupacaoON grupoOcupacaoON;

@EJB
private IntegracaoCentralPendenciasON integracaoCentralPendenciasON;

@EJB
private ProducaoObjetoCustoON producaoObjetoCustoON;

@EJB
private ObjetosCustoON objetosCustoON;

@EJB
private ObjetoCustoClienteON objetoCustoClienteON;

@EJB
private CalculoMovimentosObjetoCentroCustoON calculoMovimentosObjetoCentroCustoON;

@EJB
private EscalaAlocacaoServicosAssistenciaisON escalaAlocacaoServicosAssistenciaisON;

@EJB
private PesoObjetoCustoON pesoObjetoCustoON;

@EJB
private ManterCentroProducaoON manterCentroProducaoON;

@EJB
private ClienteObjetoCustoON clienteObjetoCustoON;

@EJB
private DirecionadorON direcionadorON;

@EJB
private ManterComunicacaoEventoON manterComunicacaoEventoON;

@Inject
private SigDirecionadoresDAO sigDirecionadoresDAO;

@Inject
private SigMvtoContaMensalDAO sigMvtoContaMensalDAO;

@Inject
private SigDetalheProducaoDAO sigDetalheProducaoDAO;

@Inject
private SigGrupoOcupacaoCargosDAO sigGrupoOcupacaoCargosDAO;

@Inject
private SigEscalaPessoaDAO sigEscalaPessoaDAO;

@Inject
private SigGrupoOcupacoesDAO sigGrupoOcupacoesDAO;

@Inject
private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;

@Inject
private SigComunicacaoEventosDAO sigComunicacaoEventosDAO;

@Inject
private SigCentroProducaoDAO sigCentroProducaoDAO;

@Inject
private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

@Inject
private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;

@Inject
private SigCalculoObjetoCustoDAO sigCalculoObjetoCustoDAO;

@EJB
private ManterCategoriasConsumoRN manterCategoriasConsumoRN;

@EJB
private CadastrarCategoriasConsumoRN cadastrarCategoriasConsumoRN;

@Inject
private SigCategoriaConsumosDAO sigCategoriaConsumosDAO;

@Inject
private SigCalculoRateioServicoDAO sigCalculoRateioServicoDAO;

	private static final long serialVersionUID = -7037760982560759964L;

	@Override
	public boolean verificarContratoContabilizado(ScoContrato contrato){
		return sigCalculoRateioServicoDAO.verificarContratoContabilizado(contrato);
	}
	
	@Override
	public Long listarCentroProducaoCount(String nomeCentroProducao, DominioTipoCentroProducaoCustos tipoCentroProducao, DominioSituacao situacao) {
		return this.getSigCentroProducaoDAO().listarCentroProducaoCount(nomeCentroProducao, tipoCentroProducao, situacao);
	}

	@Override
	public List<SigCentroProducao> pesquisarCentroProducaoCentroTipoSituacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			String nomeCentroProducao, DominioTipoCentroProducaoCustos tipoCentroProducao, DominioSituacao situacao) {
		return this.getSigCentroProducaoDAO().pesquisarCentroProducaoCentroTipoSituacao(firstResult, maxResult, orderProperty, asc, nomeCentroProducao,
				tipoCentroProducao, situacao);
	}

	@Override
	public void excluirCentroProducao(Integer seqCentroProducao) throws ApplicationBusinessException {
		this.getManterCentroProducaoON().excluirSigCentroProducao(seqCentroProducao);
	}

	@Override
	public SigCentroProducao obterSigCentroProducao(Integer seq) {
		return getSigCentroProducaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarCentroProducao(SigCentroProducao centroProducao) throws ApplicationBusinessException {
		getManterCentroProducaoON().alterarCentroProducao(centroProducao);
	}

	@Override
	public void inserirCentroProducao(SigCentroProducao centroProducao) throws ApplicationBusinessException {
		getManterCentroProducaoON().incluirCentroProducao(centroProducao);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosHierarquia(FccCentroCustos principal) {
		return this.getManterCentroProducaoON().pesquisarCentroCustosHierarquia(principal);
	}

	@Override
	public List<SigCentroProducao> pesquisarCentroProducao(Object paramPesquisa, DominioSituacao situacao) throws BaseException {
		return this.getSigCentroProducaoDAO().pesquisarCentroProducao(paramPesquisa, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<SigCentroProducao> pesquisarCentroProducao() {
		return this.getSigCentroProducaoDAO().pesquisarCentroProducao();
	}

	@Override
	public void persistirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException {
		getGrupoOcupacoesON().persistirGrupoOcupacao(grupoOcupacao);
	}

	@Override
	public void excluirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException {
		getGrupoOcupacoesON().excluirGrupoOcupacao(grupoOcupacao);
	}

	@Override
	public SigGrupoOcupacoes obterGrupoOcupacao(Integer seq) {
		return getGrupoOcupacoesON().obterGrupoOcupacaoComListaCargos(seq);
	}

	@Override
	public List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao,
			DominioSituacao situacao, FccCentroCustos centroCusto) {
		return getSigGrupoOcupacoesDAO().pesquisarGrupoOcupacao(firstResult, maxResult, orderProperty, asc, descricao, situacao, centroCusto);
	}

	@Override
	public Long pesquisarGrupoOcupacaoCount(String descricao, DominioSituacao situacao, FccCentroCustos centroCusto) {
		return getSigGrupoOcupacoesDAO().pesquisarGrupoOcupacaoCount(descricao, situacao, centroCusto);
	}

	@Override
	public List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Object paramPesquisa, FccCentroCustos centroCustoAtividade) {
		return this.getSigGrupoOcupacoesDAO().pesquisarGrupoOcupacao(paramPesquisa, centroCustoAtividade);
	}

	@Override
	public void validarRepeticaoCargosGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao, List<SigGrupoOcupacaoCargos> listaOcupacaoCargo,
			RapOcupacaoCargo ocupacaoCargo, Integer posicao) throws ApplicationBusinessException {
		this.getGrupoOcupacoesON().validarRepeticaoCargosGrupoOcupacao(grupoOcupacao, listaOcupacaoCargo, ocupacaoCargo, posicao);
	}

	@Override
	public List<SigComunicacaoEventos> pesquisarComunicacaoEventos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
					SigComunicacaoEventos sigComunicacaoEventos) {
		return this.getSigComunicacaoEventosDAO().pesquisarComunicacaoEventos(firstResult, maxResult, orderProperty, asc, sigComunicacaoEventos);
	}

	@Override
	public Long pesquisarComunicacaoEventosCount(SigComunicacaoEventos sigComunicacaoEventos) {
		return this.getSigComunicacaoEventosDAO().pesquisarComunicacaoEventosCount(sigComunicacaoEventos);
	}

	@Override
	public SigComunicacaoEventos obterComunicacaoEvento(Integer seqComunicacaoEvento) {
		return this.getManterComunicacaoEventoON().obterComunicacaoEvento(seqComunicacaoEvento);
	}

	@Override
	public void excluirComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) {
		this.getSigComunicacaoEventosDAO().removerPorId(sigComunicacaoEventos.getSeq());
	}

	@Override
	public void persistComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) throws ApplicationBusinessException {
		this.getManterComunicacaoEventoON().persistirComunicacaoEvento(sigComunicacaoEventos);
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadores(DominioTipoDirecionadorCustos tipoDirecionador, DominioTipoCalculoObjeto tipoCalculo, 
			Boolean coletaSistema) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadores(tipoDirecionador, tipoCalculo, coletaSistema);
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadores(DominioSituacao situacao, DominioTipoDirecionadorCustos tipo) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadores(situacao, tipo);
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadores(Boolean indTempoIsNull, Boolean filtrarFatConvHoraIsNotNull) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadores(indTempoIsNull, filtrarFatConvHoraIsNotNull);
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadoresTempoMaiorMes() {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadoresTempoMaiorMes();
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadoresAtivosInativo(Boolean indTempoIsNull, Boolean ativo) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadoresAtivosInativo(indTempoIsNull, ativo);
	}

	@Override
	public List<SigDirecionadores> pesquisarDirecionadoresTipoATAB(Boolean ativo) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadoresTipoATAB(ativo);
	}

	@Override
	public SigDirecionadores obterDirecionador(Integer seq) {
		return this.getSigDirecionadoresDAO().obterPorChavePrimaria(seq, true, SigDirecionadores.Fields.SERVIDOR_RESPONSAVEL);
	}

	@Override
	public Long pesquisarDirecionadorAtividadeCount(SigDirecionadores direcionador) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadorCount(direcionador);
	}

	@Override
	public List<SigAtividades> pesquisarDirecionadorAtividade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigDirecionadores direcionador) {
		return this.getSigDirecionadoresDAO().pesquisarDirecionadorAtividade(firstResult, maxResult, orderProperty, asc, direcionador);
	}

	@Override
	public List<SigDirecionadores> pesquisaDirecionadoresDoObjetoCusto(SigObjetoCustoVersoes versao, DominioSituacao direcionadorRateioSituacao,
			DominioTipoDirecionadorCustos indTipo, DominioTipoCalculoObjeto indTipoCalculo) {
		return this.getDirecionadorON().pesquisaDirecionadoresDoObjetoCusto(versao, direcionadorRateioSituacao, indTipo, indTipoCalculo);
	}

	@Override
	public void persistirDirecionador(SigDirecionadores direcionador, RapServidores servidorResponsavel) throws ApplicationBusinessException {
		this.getDirecionadorON().persistirDirecionador(direcionador, servidorResponsavel);
	}
	
	@Override
	public List<DominioTipoCalculoObjeto> listarTiposCalculoObjeto(DominioTipoDirecionadorCustos tipoDirecionadorCusto){
		return this.getDirecionadorON().listarTiposCalculoObjeto(tipoDirecionadorCusto);
	}

	@Override
	public Map<Integer, Double> pesquisarPesoTabelaUnificadaSUS(Integer codigoCentroCusto) {
		return this.getPesoObjetoCustoON().pesquisarPesoTabelaUnificadaSUS(codigoCentroCusto);
	}

	@Override
	public void persistirPesosObjetoCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto, FccCentroCustos centroCusto, DominioTipoRateio tipoRateio,
			SigDirecionadores direcionador, Boolean estaUtilizandoTabelaSus, Map<Integer, Double> mapeamentoSus) throws ApplicationBusinessException {
		this.getPesoObjetoCustoON().persistirPesosObjetoCusto(listaObjetoCustoCentroCusto, centroCusto, tipoRateio, direcionador,  estaUtilizandoTabelaSus, mapeamentoSus);
	}


	@Override
	public VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseOC(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer codigoCentroCusto, Short seqPagador) {
		return this.getSigProcessamentoCustoDAO().obterDetalheVisualizacaoAnaliseOC(seqCompetencia, seqObjetoCustoVersao, codigoCentroCusto, seqPagador);
	}

	@Override
	public VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseCC(Integer seqCompetencia, FccCentroCustos fccCentroCustos) {
		return this.getSigProcessamentoCustoDAO().obterDetalheVisualizacaoAnaliseCC(seqCompetencia, fccCentroCustos);
	}

	@Override
	public List<DetalhamentoCustosGeralVO> buscarMovimentosGeral(Integer pmuSeq, Integer ocvSeq, FccCentroCustos fccCentroCustos,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosGeral(pmuSeq, ocvSeq, fccCentroCustos, tipoVisaoAnaliseItens);
	}

	@Override
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosInsumos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens);
	}

	@Override
	public Integer buscarMovimentosInsumosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosInsumos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens).size();
	}

	@Override
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosEquipamentos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens);
	}

	@Override
	public Integer buscarMovimentosEquipamentosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosEquipamentos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens).size();
	}

	@Override
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoas(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosPessoas(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens);
	}

	@Override
	public Integer buscarMovimentosPessoasCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosPessoas(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens).size();
	}

	@Override
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosServicos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens);
	}

	@Override
	public Integer buscarMovimentosServicosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosServicos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens).size();
	}

	@Override
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosIndiretos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens, Integer seqObjetoCustoDebita) {
		return this.getCalculoObjetoCustoON().buscarMovimentosIndiretos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens, seqObjetoCustoDebita);
	}

	@Override
	public Integer buscarMovimentosIndiretosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		return this.getCalculoObjetoCustoON().buscarMovimentosIndiretos(seqCompetencia, seqObjetoVersao, seqCentroCusto, tipoVisaoAnaliseItens, null).size();
	}

	@Override
	public List<SigDetalheProducao> listarClientesObjetoCustoVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			SigProcessamentoCusto competencia) {
		return this.getProducaoObjetoCustoON().listarClientesObjetoCustoVersao(sigObjetoCustoVersoes, sigDirecionadores, competencia);
	}

	@Override
	public void persistirProducaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador, SigProcessamentoCusto competencia,
			List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException {
		this.getProducaoObjetoCustoON().persistirProducaoObjetoCusto(objetoCustoVersao, direcionador, competencia, listaClientes);

	}

	@Override
	public boolean verificarPreenchimentoValoresClientes(List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException {
		return this.getProducaoObjetoCustoON().verificarPreenchimentoValoresClientes(listaClientes);
	}

	@Override
	public BigDecimal calcularValorTotal(List<SigDetalheProducao> listaClientes) {
		return this.getProducaoObjetoCustoON().calcularValorTotal(listaClientes);
	}

	@Override
	public void verificarEdicaoDetalheProducao(Integer seq) throws ApplicationBusinessException {
		this.getProducaoObjetoCustoON().verificarEdicaoDetalheProducao(seq);
	}

	@Override
	public Long pesquisarProducaoCount(FccCentroCustos centroCusto, SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto,
			SigDirecionadores direcionador) {
		return this.getSigDetalheProducaoDAO().pesquisarProducaoCount(centroCusto, competencia, objetoCusto, direcionador);
	}

	@Override
	public List<DetalheProducaoObjetoCustoVO> pesquisarProducao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto, SigDirecionadores direcionador) {
		return this.getSigDetalheProducaoDAO().pesquisarProducao(firstResult, maxResult, orderProperty, asc, centroCusto, competencia, objetoCusto, direcionador);
	}

	@Override
	public void validarInclusaoAlteracaoClienteObjetoCusto(SigObjetoCustoClientes sigObjetoCustoClientes, List<SigObjetoCustoClientes> listaClientes,
			boolean alteracao) throws ApplicationBusinessException {
		this.getClienteObjetoCustoON().validarInclusaoAlteracao(sigObjetoCustoClientes, listaClientes, alteracao);
	}

	@Override
	public void persistCliente(SigObjetoCustoClientes cliente) {
		this.getClienteObjetoCustoON().persistirCliente(cliente);
	}

	@Override
	public void validarExclusaoClienteObjetoCusto(SigObjetoCustoVersoes objetoCustoVersoes) throws ApplicationBusinessException {
		this.getClienteObjetoCustoON().validarExclusao(objetoCustoVersoes);

	}

	@Override
	public void excluirCliente(SigObjetoCustoClientes clienteExcluir) {
		this.getClienteObjetoCustoON().excluirCliente(clienteExcluir);
	}

	@Override
	public List<SigObjetoCustoClientes> pesquisarObjetoCustoClientePorObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersoes) {
		return this.getSigObjetoCustoClientesDAO().pesquisarObjetoCustoClientes(objetoCustoVersoes);
	}

	@Override
	public void excluirDetalheProducao(Integer seqDetalheProducao) throws ApplicationBusinessException {
		this.getProducaoObjetoCustoON().excluirDetalheProducao(seqDetalheProducao);
	}

	@Override
	public void persistirSigDetalheProducao(SigDetalheProducao detalhe) {
		this.getSigDetalheProducaoDAO().persistir(detalhe);
	}

	@Override
	public SigDetalheProducao obterDetalheProducao(Integer seqDetalheProducao) {
		return this.getSigDetalheProducaoDAO().obterPorChavePrimaria(seqDetalheProducao, false, SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO, SigDetalheProducao.Fields.DIRECIONADOR, SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO);
	}

	@Override
	public List<ObjetoCustoPesoClienteVO> pesquisarObjetoCustoPesoCliente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao) {
		return this.getObjetosCustoON().pesquisarObjetoCustoPesoCliente(firstResult, maxResult, orderProperty, asc, centroCusto, direcionador, nome, situacao);
	}

	@Override
	public Long pesquisarObjetoCustoPesoClienteCount(FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome,
			DominioSituacaoVersoesCustos situacao) {
		return this.getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoPesoClienteCount(centroCusto, direcionador, nome, situacao);
	}

	@Override
	public List<SigObjetoCustoClientes> buscaObjetoClienteVersaoAtivo(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor) {
		return this.getObjetoCustoClienteON().buscaObjetoClienteVersaoAtivo(sigObjetoCustoVersoes, sigDirecionadores, semValor);
	}

	@Override
	public void atualizarValorCliente(List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException {
		this.getObjetoCustoClienteON().atualizarValorCliente(listaClientes);
	}

	@Override
	public SigObjetoCustoClientes validaIndicacaoTodosCC(Integer ocvSeq, Integer dirSeq){
		return this.getSigObjetoCustoClientesDAO().validaIndicacaoTodosCC(ocvSeq, dirSeq);
	}

	@Override
	public void associarCentrosCustoClientes(SigObjetoCustoClientes sigObjetoCustoClientes, RapServidores servidor) {
		this.getObjetoCustoClienteON().associarCentrosCustoClientes(sigObjetoCustoClientes, servidor);
	}

	@Override
	public List<SigEscalaPessoa> pesquisarEscalaPessoas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FccCentroCustos centroCustos) {
		return this.getSigEscalaPessoasDAO().pesquisarEscalaPessoas(firstResult, maxResult, orderProperty, asc, centroCustos);
	}

	@Override
	public Long pesquisarEscalaPessoasCount(FccCentroCustos centroCustos) {
		return this.getSigEscalaPessoasDAO().pesquisarEscalaPessoasCount(centroCustos);
	}

	@Override
	public boolean pesquisarEscalaPessoasCentroCusto(FccCentroCustos centroCustos) {
		return this.getEscalaAlocacaoServicosAssistenciaisON().pesquisarEscalaPessoasPorCentroDeCusto(centroCustos);
	}
	
	@Override
	public void verificarEscalaPessoasCentroCusto(FccCentroCustos centroCustos, SigEscalaPessoa escala) throws ApplicationBusinessException {
		this.getEscalaAlocacaoServicosAssistenciaisON().verificarEscalaPessoasPorCentroDeCusto(centroCustos, escala);
	}
	

	@Override
	public void gravar(SigEscalaPessoa sigEscalaPessoas) {
		this.getSigEscalaPessoasDAO().persistir(sigEscalaPessoas);
	}

	@Override
	public void excluir(SigEscalaPessoa sigEscalaPessoas) {
		this.getSigEscalaPessoasDAO().removerPorId(sigEscalaPessoas.getSeq());
	}

	@Override
	public SigEscalaPessoa obterEscalaPessoas(Integer seq) {
		return this.getSigEscalaPessoasDAO().obterPorChavePrimaria(seq, SigEscalaPessoa.Fields.CENTRO_CUSTO);
	}

	@Override
	public SigEscalaPessoa verificaExistenciaDaAtividadeNoCentroDeCusto(SigEscalaPessoa sigEscalaPessoas) {
		return this.getSigEscalaPessoasDAO().verificaExistenciaDaAtividadeNoCentroDeCusto(sigEscalaPessoas);
	}

	@Override
	public void editar(SigEscalaPessoa sigEscalaPessoas) {
		this.getSigEscalaPessoasDAO().atualizar(sigEscalaPessoas);
	}
	
	public  void adicionarPendenciaProcessamentoHomologado(Integer seq) throws ApplicationBusinessException{
		this.getIntegracaoCentralPendenciasON().adicionarPendenciaProcessamentoHomologado(seq);
	}

	// DAOs e ONs

	protected CalculoMovimentosObjetoCentroCustoON getCalculoObjetoCustoON() {
		return calculoMovimentosObjetoCentroCustoON;
	}

	protected ManterComunicacaoEventoON getManterComunicacaoEventoON() {
		return manterComunicacaoEventoON;
	}

	protected SigMvtoContaMensalDAO getSigMvtoContaMensalDAO() {
		return sigMvtoContaMensalDAO;
	}

	protected SigCalculoObjetoCustoDAO getSigCalculoObjetoCustoDAO() {
		return sigCalculoObjetoCustoDAO;
	}

	protected SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}

	protected GrupoOcupacaoON getGrupoOcupacoesON() {
		return grupoOcupacaoON;
	}

	protected SigGrupoOcupacoesDAO getSigGrupoOcupacoesDAO() {
		return sigGrupoOcupacoesDAO;
	}

	protected SigGrupoOcupacaoCargosDAO getSigGrupoOcupacaoCargosDAO() {
		return sigGrupoOcupacaoCargosDAO;
	}

	protected SigCentroProducaoDAO getSigCentroProducaoDAO() {
		return sigCentroProducaoDAO;
	}

	protected DirecionadorON getDirecionadorON() {
		return direcionadorON;
	}

	protected ManterCentroProducaoON getManterCentroProducaoON() {
		return manterCentroProducaoON;
	}

	protected SigComunicacaoEventosDAO getSigComunicacaoEventosDAO() {
		return sigComunicacaoEventosDAO;
	}

	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}

	protected SigDirecionadoresDAO getSigDirecionadoresDAO() {
		return sigDirecionadoresDAO;
	}

	protected PesoObjetoCustoON getPesoObjetoCustoON() {
		return pesoObjetoCustoON;
	}

	protected ObjetoCustoClienteON getObjetoCustoClienteON() {
		return objetoCustoClienteON;
	}

	protected IntegracaoCentralPendenciasON getIntegracaoCentralPendenciasON() {
		return integracaoCentralPendenciasON;
	}

	protected ProducaoObjetoCustoON getProducaoObjetoCustoON() {
		return producaoObjetoCustoON;
	}

	protected SigDetalheProducaoDAO getSigDetalheProducaoDAO() {
		return sigDetalheProducaoDAO;
	}

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	protected ClienteObjetoCustoON getClienteObjetoCustoON() {
		return clienteObjetoCustoON;
	}

	protected ObjetosCustoON getObjetosCustoON() {
		return objetosCustoON;
	}
	
	private SigEscalaPessoaDAO getSigEscalaPessoasDAO() {
		return sigEscalaPessoaDAO;
	}

	protected EscalaAlocacaoServicosAssistenciaisON getEscalaAlocacaoServicosAssistenciaisON() {
		return escalaAlocacaoServicosAssistenciaisON;
	}
	
	protected ManterCategoriasConsumoRN getManterCategoriasConsumoRN() {
		return manterCategoriasConsumoRN;
	}
	
	protected CadastrarCategoriasConsumoRN getCadastrarCategoriasConsumoRN() {
		return cadastrarCategoriasConsumoRN;
	}

	@Override
	public List<SigCategoriaConsumos> buscaCategoriasDeConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		return getManterCategoriasConsumoRN().buscaCategoriasDeConsumo(firstResult, maxResult, orderProperty, asc, descricao, indContagem, situacao);
	}

	@Override
	public Long buscaCategoriasDeConsumoCount(String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		return getManterCategoriasConsumoRN().buscaCategoriasDeConsumoCount(descricao, indContagem, situacao);
	}

	protected SigCategoriaConsumosDAO getSigCategoriaConsumosDAO() {
		return sigCategoriaConsumosDAO;
	}
	
	@Override
	public SigCategoriaConsumos obterCategoriaConsumo(Integer seq) {
		return getSigCategoriaConsumosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void excluirCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {
		getManterCategoriasConsumoRN().excluirCategoriaConsumo(categoriaConsumo);
	}

	@Override
	public void persistirCategoriaConsumo(SigCategoriaConsumos categoriaConsumo, RapServidores servidor) throws ApplicationBusinessException {
		getCadastrarCategoriasConsumoRN().insereCategoriaConsumo(categoriaConsumo,servidor);		
	}

	@Override
	public void atualizarCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {
		getCadastrarCategoriasConsumoRN().atualizaCategoriaConsumo(categoriaConsumo);		
	}
}