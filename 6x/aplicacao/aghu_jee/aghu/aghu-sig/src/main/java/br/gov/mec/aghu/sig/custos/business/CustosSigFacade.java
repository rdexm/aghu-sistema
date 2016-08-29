package br.gov.mec.aghu.sig.custos.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
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
import br.gov.mec.aghu.patrimonio.IPatrimonioService;
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
import br.gov.mec.aghu.sig.dao.SigAtividadeCentroCustosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoaRestricoesDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPaciente2DAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdProcedimentosDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdReceitaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoDetalheConsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigCategoriaRecursoDAO;
import br.gov.mec.aghu.sig.dao.SigEquipamentoPatrimonioDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoHistoricosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustosDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.sig.dao.VSigAfsContratosServicosDAO;
import br.gov.mec.aghu.sig.dao.VSigCustosCalculoCidDAO;
import br.gov.mec.aghu.sig.dao.VSigCustosCalculoProcedDAO;
import br.gov.mec.aghu.view.VSigAfsContratosServicos;
import br.gov.mec.aghu.vo.AghAtendimentosVO;


@Modulo(ModuloEnum.SIG_CUSTOS_ATIVIDADE)
@Stateless
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveClassLength"})
public class CustosSigFacade extends BaseFacade implements Serializable, ICustosSigFacade {

	@EJB
	private ObjetoCustoComposicoesON objetoCustoComposicoesON;
	
	@EJB
	private DirecionadorRateioObjetoCustoON direcionadorRateioObjetoCustoON;
	
	@EJB
	private AnaliseHistoricoON analiseHistoricoON;
	
	@EJB
	private CalculoMovimentosObjetoCentroCustoON calculoMovimentosObjetoCentroCustoON;
	
	@EJB
	private CentroCustoComplementarON centroCustoComplementarON;
	
	@EJB
	private EfetuaLeituraCargaProducaoObjetoCustoON efetuaLeituraCargaProducaoObjetoCustoON;
	
	@EJB
	private ManterPessoasAtividadeON manterPessoasAtividadeON;
	
	@EJB
	private ManterServicosAtividadeON manterServicosAtividadeON;
	
	@EJB
	private ManterObjetosCustoON manterObjetosCustoON;
	
	@EJB
	private ObjetosCustoON objetosCustoON;
	
	@EJB
	private RelatorioComposicaoAtividadeON relatorioComposicaoAtividadeON;
	
	@EJB
	private ManterAtividadesON manterAtividadesON;
	
	@EJB
	private RelatorioComposicaoObjetoCustoON relatorioComposicaoObjetoCustoON;
	
	@EJB
	private EfetuaCargaObjetoCustoON efetuaCargaObjetoCustoON;
	
	@EJB
	private ManterInsumosAtividadeON manterInsumosAtividadeON;
	
	@EJB
	private ManterEquipamentosAtividadeON manterEquipamentosAtividadeON;
	
	@Inject
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;
	
	@Inject
	private VSigCustosCalculoCidDAO vSigCustosCalculoCidDAO; 
	
	@EJB
	private CalculoAtendimentoPacienteON calculoAtendimentoPacienteON;
	
	@EJB
	private VisualizarCustoPacienteDiagnosticoON visualizarCustoPacienteDiagnosticoON;
	
	@Inject
	private VSigCustosCalculoProcedDAO vSigCustosCalculoProcedDAO;
	
	@Inject
	private SigAtividadeServicosDAO sigAtividadeServicosDAO;
	
	@Inject
	private SigAtividadesDAO sigAtividadesDAO;
	
	@Inject
	private SigAtividadeInsumosDAO sigAtividadeInsumosDAO;
	
	@Inject
	private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;
	
	@Inject
	private SigObjetoCustosDAO sigObjetoCustosDAO;
	
	@Inject
	private SigCalculoObjetoCustoDAO sigCalculoObjetoCustoDAO;
	
	@Inject
	private SigCalculoAtdPacienteDAO sigCalculoAtdPacienteDAO;
	
	@Inject
	private SigCalculoAtdReceitaDAO sigCalculoAtdReceitaDAO;
	
	@Inject
	private SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO;
	
	@Inject
	private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;
	
	@Inject
	private SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO;
	
	@Inject
	private SigEquipamentoPatrimonioDAO sigEquipamentoPatrimonioDAO;
	
	@Inject
	private SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO;
	
	@Inject
	private SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO;
	
	@Inject
	private SigAtividadePessoasDAO sigAtividadePessoasDAO;
	
	@Inject
	private VSigAfsContratosServicosDAO vSigAfsContratosServicosDAO;
	
	@Inject
	private SigCalculoAtdPaciente2DAO sigCalculoAtdPaciente2DAO;
	
	@Inject
	private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	private static final long serialVersionUID = 2080313860794899215L;

	@EJB
	private IPatrimonioService patrimonioService;

	@EJB
	private GrupoOcupacaoON grupoOcupacaoON;

	@EJB
	
	private PesoObjetoCustoON pesoObjetoCustoON;

	@Inject
	private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;

	@EJB
	private VisualizarCustoPacienteON visualizarCustoPacienteON;
	
	@EJB
	private VisualizarCustoPacienteProcedimentoON visualizarCustoPacienteProcedimentoON;
	
	@Inject
	private SigAtividadeCentroCustosDAO sigAtividadeCentroCustosDAO;

	@Inject
	private SigCalculoDetalheConsumoDAO sigCalculoDetalheConsumoDAO;
	
	@Inject
	private SigCalculoAtdProcedimentosDAO sigCalculoAtdProcedimentosDAO;

	@Inject
	private SigCategoriaRecursoDAO sigCategoriaRecursoDAO;

	@Inject
	private SigAtividadePessoaRestricoesDAO sigAtividadePessoaRestricoesDAO;

	@Override
	public void validarSomaPercentuaisDirecionadoresRateio(DominioSituacaoVersoesCustos situacaoObjetoCusto, List<SigObjetoCustoDirRateios> lista)
			throws ApplicationBusinessException {
		this.getDirecionadorRateioObjetoCustoON().validarSomaPercentuaisDirecionadoresRateio(situacaoObjetoCusto, lista);
	}

	@Override
	public boolean validarExclusaoDirecionadorRateio(DominioSituacaoVersoesCustos situacao, Date dataInicio) {
		return this.getDirecionadorRateioObjetoCustoON().validarExclusaoDirecionadorRateio(situacao, dataInicio);
	}

	@Override
	public boolean validarExclusaoDirecionadorRateioAssociadoCliente(List<SigObjetoCustoClientes> lista, SigDirecionadores direcionador) {
		return this.getDirecionadorRateioObjetoCustoON().validarExclusaoDirecionadorRateioAssociadoCliente(lista, direcionador);
	}

	@Override
	public void persistirListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista, List<SigObjetoCustoDirRateios> excluidos,
			SigObjetoCustoVersoes objetoCustoVersao) {
		this.getDirecionadorRateioObjetoCustoON().persistirListaDirecionadorRateioObjetoCusto(lista, excluidos, objetoCustoVersao);
	}

	@Override
	public void validarAlteracaoListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista, SigObjetoCustoDirRateios objetoCustoDirRateio,
			Integer posicao, List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException {
		this.getDirecionadorRateioObjetoCustoON().validarAlteracaoListaDirecionadorRateioObjetoCusto(lista, objetoCustoDirRateio, posicao, listaClientes);
	}

	@Override
	public List<SigObjetoCustoCcts> pesquisarObjetosCustoCentroCusto(FccCentroCustos centroCustos) {
		return getSigObjetoCustoCctsDAO().pesquisarObjetosCustoCentroCusto(centroCustos);
	}

	@Override
	public List<SigAtividades> pesquisarAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigAtividades atividade,
			FccCentroCustos fccCentroCustos) {
		return getSigAtividadesDAO().pesquisarAtividades(firstResult, maxResult, orderProperty, asc, atividade, fccCentroCustos);
	}

	@Override
	public Long pesquisarAtividadesCount(SigAtividades atividade, FccCentroCustos fccCentroCustos) {
		return getSigAtividadesDAO().pesquisarAtividadesCount(atividade, fccCentroCustos);
	}

	@Override
	public List<SigAtividades> pesquisarAtividades(FccCentroCustos fccCentroCustos) {
		return getSigAtividadesDAO().pesquisarAtividades(fccCentroCustos);
	}

	@Override
	public List<SigObjetoCustoHistoricos> pesquisarHistoricoAtividade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigAtividades atividade) {
		return getSigObjetoCustoHistoricosDAO().pesquisarHistoricoAtividades(firstResult, maxResult, orderProperty, asc, atividade);
	}

	@Override
	public Long pesquisarHistoricoAtividadeCount(SigAtividades atividade) {
		return getSigObjetoCustoHistoricosDAO().pesquisarHistoricoAtividadeCount(atividade);
	}

	@Override
	public void iniciaProcessoHistoricoCopiaObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, SigObjetoCustoVersoes objetoCustoVersaoSuggestion,
			List<SigObjetoCustoComposicoes> copia, RapServidores rapServidores) throws ApplicationBusinessException {
		analiseHistoricoON.iniciaProcessoHistoricoCopiaObjetoCusto(objetoCustoVersao, objetoCustoVersaoSuggestion, copia, rapServidores);
	}

	@Override
	public void iniciaProcessoHistoricoProduto(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis, List<SigObjetoCustoPhis> listaPhisExcluir,
			RapServidores rapServidores) throws ApplicationBusinessException {
		analiseHistoricoON.iniciaProcessoHistoricoProduto(objetoCustoVersao, clone, listaObjetoCustoComposicoes, listaPhis, listaPhisExcluir,
				rapServidores);
	}

	@Override
	public void iniciaProcessoHistoricoAtividade(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade, List<SigAtividadeInsumos> listAtividadeInsumos,
			List<SigAtividadeServicos> listaServicos, RapServidores rapServidores) throws ApplicationBusinessException {
		analiseHistoricoON.iniciaProcessoHistoricoAtividade(atividade, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores);
	}

	@Override
	public SigAtividades obterAtividade(Integer seq) {
		return getSigAtividadesDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void persist(SigAtividades atividade) {
		this.getManterAtividadesON().persistirAtividade(atividade);
	}

	@Override
	public void excluirAtividade(SigAtividades atividade) throws ApplicationBusinessException {
		this.getManterAtividadesON().excluirAtividade(atividade);
	}

	@Override
	public void persistEquipamento(SigAtividadeEquipamentos equipamento) {
		this.getManterEquipamentosAtividadeON().persistirEquipamento(equipamento);
	}

	@Override
	public void excluirEquipamentoAtividade(SigAtividadeEquipamentos equipamento) {
		this.getSigAtividadeEquipamentosDAO().removerPorId(equipamento.getSeq());
	}

	@Override
	public List<SigAtividadeEquipamentos> pesquisarListaEquipamentosAtividade(SigAtividades atividades) {
		return this.getSigAtividadeEquipamentosDAO().pesquisarEquipamentosAtividade(atividades);
	}

	@Override
	public List<SigAtividadePessoas> pesquisarPessoasPorSeqAtividade(Integer seqAtividade) {
		return this.getSigAtividadePessoasDAO().pesquisarPessoasPorSeqAtividade(seqAtividade);
	}

	@Override
	public List<SigAtividadeServicos> pesquisarServicosPorSeqAtividade(Integer seqAtividade) {
		return this.getSigAtividadeServicosDAO().pesquisarServicosPorSeqAtividade(seqAtividade);
	}

	@Override
	public void removerAtividadeCentroCustos(List<SigAtividadeCentroCustos> lista) {
		this.getManterAtividadesON().removerAtividadeCentroCustos(lista);
	}
	
	public SigAtividadeCentroCustos obterCentroCustoPorAtividade(Integer seqAtividade){
		return this.getSigAtividadeCentroCustosDAO().obterCentroCustoPorAtividade(seqAtividade);
	}

	@Override
	public void removerObjetosCustoCentroCustos(SigObjetoCustoCcts elemento) {
		this.getSigObjetoCustoCctsDAO().removerPorId(elemento.getSeq());
	}

	@Override
	public void persistPessoa(SigAtividadePessoas sigAtividadePessoas) {
		this.getManterPessoasAtividadeON().persistirPessoa(sigAtividadePessoas);
	}

	@Override
	public void excluirPessoa(SigAtividadePessoas sigAtividadePessoas) {
		this.getSigAtividadePessoasDAO().removerPorId(sigAtividadePessoas.getSeq());
	}

	@Override
	public void validarInclusaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list) throws ApplicationBusinessException {
		this.getManterPessoasAtividadeON().validarInclusaoPessoaAtividade(sigAtividadePessoas, list);
	}

	@Override
	public void validarAlteracaoPessoaAtividade(SigAtividadePessoas sigAtividadePessoas, List<SigAtividadePessoas> list) throws ApplicationBusinessException {
		this.getManterPessoasAtividadeON().validarAlteracaoPessoaAtividade(sigAtividadePessoas, list);
	}

	@Override
	public List<SigAtividadeInsumos> pesquisarAtividadeInsumos(Integer seqAtividade) {
		return this.getManterAtividadesON().pesquisarAtividadeInsumos(seqAtividade);
	}

	@Override
	public void validarInclusaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos)
			throws ApplicationBusinessException {
		this.getManterInsumosAtividadeON().validarInclusaoInsumoAtividade(atividadeInsumos, listAtividadeInsumos);
	}

	@Override
	public void persistInsumo(SigAtividadeInsumos insumo) {
		this.getManterInsumosAtividadeON().persistirInsumos(insumo);
	}

	@Override
	public void persistServico(SigAtividadeServicos servico) {
		this.getManterServicosAtividadeON().persistirServicos(servico);
	}

	@Override
	public boolean verificaAtividadeEstaVinculadaAoObjetoCusto(SigAtividades atividade) {
		return this.getManterAtividadesON().verificaAtividadeEstaVinculadaAObjetoCusto(atividade);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustoSemObrasPeloTipoCentroProducao(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao,
			DominioTipoCentroProducaoCustos... tipos) throws BaseException {
		return this.getManterObjetosCustoON().pesquisarCentroCustoSemObrasPeloTipoCentroProducao(paramPesquisa, seqCentroProducao, situacao, tipos);
	}

	@Override
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoesAssistencial(FccCentroCustos fccCentroCustos, Integer seqSigObjetoCustoVersao, Object param) {
		return getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoesAssistencial(fccCentroCustos, seqSigObjetoCustoVersao, param);
	}

	@Override
	public List<SigObjetoCustoVersoes> buscaObjetoCustoPrincipalAtivoPeloCentroCusto(FccCentroCustos fccCentroCustos, Object paramPesquisa) {
		return getSigObjetoCustoVersoesDAO().buscaObjetoCustoPrincipalAtivoPeloCentroCusto(fccCentroCustos, paramPesquisa);
	}

	@Override
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoIsProdutoServico(FccCentroCustos fccCentroCustos, Object param) {
		return getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoIsProdutoServico(fccCentroCustos, param);
	}

	@Override
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoes(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao, String nome,
			DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao) {
		return getManterObjetosCustoON().pesquisarObjetoCustoVersoes(firstResult, maxResult, orderProperty, asc, sigCentroProducao, fccCentroCustos,
				situacao, nome, tipoObjetoCusto, possuiComposicao);
	}

	@Override
	public Long pesquisarObjetoCustoVersoesCount(SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos,
			DominioSituacaoVersoesCustos situacao, String nome, DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao) {
		return getSigObjetoCustoVersoesDAO().pesquisarCount(sigCentroProducao, fccCentroCustos, situacao, nome, tipoObjetoCusto, possuiComposicao);
	}

	@Override
	public List<SigObjetoCustoHistoricos> pesquisarHistoricoObjetoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigObjetoCustoVersoes objetoCustoVersao) {
		return getSigObjetoCustoHistoricosDAO().pesquisarHistoricoObjetoCusto(firstResult, maxResult, orderProperty, asc, objetoCustoVersao);
	}

	@Override
	public Long pesquisarHistoricoObjetoCustoCount(SigObjetoCustoVersoes objetoCustoVersao) {
		return getSigObjetoCustoHistoricosDAO().pesquisarHistoricoObjetoCustoCount(objetoCustoVersao);
	}

	@Override
	public void validarAlteracaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos)
			throws ApplicationBusinessException {
		this.getManterInsumosAtividadeON().validarAlteracaoInsumoAtividade(atividadeInsumos, listAtividadeInsumos);
	}
	
	@Override
	public void validarUtilizacaoInsumoExclusiva(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		this.getManterInsumosAtividadeON().validarUtilizacaoInsumoExclusiva(atividadeInsumos);
	}

	@Override
	public void validarAdicaoDeInsumosEmLote(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		this.validarUtilizacaoInsumoExclusiva(atividadeInsumos);
		this.validarQtdeEspecifica(atividadeInsumos);
		this.validarQtdeVidaUtil(atividadeInsumos);
	}


	
	@Override	
	public void validarQtdeEspecifica(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		this.getManterInsumosAtividadeON().validarQtdeEspecifica(atividadeInsumos);
	}
	
	@Override
	public void validarQtdeVidaUtil(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		this.getManterInsumosAtividadeON().validarQtdeVidaUtil(atividadeInsumos);
	}

	@Override
	public void excluirInsumo(SigAtividadeInsumos insumo) {
		this.getSigAtividadeInsumosDAO().removerPorId(insumo.getSeq());
	}

	@Override
	public void excluirServico(SigAtividadeServicos servico) {
		this.getSigAtividadeServicosDAO().removerPorId(servico.getSeq());
	}

	@Override
	public SigObjetoCustoVersoes obterObjetoCustoVersoes(Integer seq) {
		return this.getObjetosCustoON().obterObjetoCustoVersoes(seq);
	}
	@Override 
	public SigObjetoCustoCcts obterObjetoCustoCctsPrincipal(Integer seqObjetoCustoVersao){
		return this.getSigObjetoCustoCctsDAO().obterObjetoCustoCctsPrincipal(seqObjetoCustoVersao);
	}

	@Override
	public Map<String, Object> obterObjetoCustoVersoesDesatachado(Integer seq) {
		return analiseHistoricoON.obterObjetoCustoVersoesDesatachado(this.obterObjetoCustoVersoes(seq));
	}

	@Override
	public void validarInclusaoServicoAtividade(SigAtividadeServicos servico, List<SigAtividadeServicos> listaServicos) throws ApplicationBusinessException {
		this.getManterServicosAtividadeON().validarInclusaoServicosAtividade(servico, listaServicos);
	}

	@Override
	public List<VSigAfsContratosServicos> obterAfPorContrato(ScoContrato contrato) {
		return getVSigAfsContratosServicosDAO().obterAfPorContrato(contrato);
	}

	@Override
	public VSigAfsContratosServicos obterAfPorId(Integer seq) {
		return getVSigAfsContratosServicosDAO().obterAfPorId(seq);
	}

	@Override
	public boolean verificaVersoesAtivas(SigObjetoCustoVersoes objetoCustoVersao) {
		return this.getManterObjetosCustoON().verificaVersoesAtivas(objetoCustoVersao);
	}

	@Override
	public void validarObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, DominioSituacaoVersoesCustos situacaoAnterior) throws ApplicationBusinessException {
		this.getManterObjetosCustoON().validarObjetoCusto(objetoCustoVersao, situacaoAnterior);
	}

	@Override
	public void gravarObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException {
		this.getObjetosCustoON().persistirSigObjetoCustoVersoe(objetoCustoVersao, listaClientes, listaDirecionadoresRateio);
	}

	@Override
	public void alterarObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException {
		this.getObjetosCustoON().atualizarSigObjetoCustoVersoe(objetoCustoVersao, listaClientes, listaDirecionadoresRateio);
	}

	@Override
	public void gravarObjetoCustos(SigObjetoCustos sigObjetoCustos) {
		this.getSigObjetoCustosDAO().persistir(sigObjetoCustos);
	}

	@Override
	public void alterarObjetoCustos(SigObjetoCustos sigObjetoCustos) {
		this.getSigObjetoCustosDAO().atualizar(sigObjetoCustos);
	}

	@Override
	public void gravarObjetoCustoCentroCusto(SigObjetoCustoCcts objetoCustoCentroCusto) {
		this.getObjetosCustoON().gravarObjetoCustoCentroCusto(objetoCustoCentroCusto);
	}

	@Override
	public void alterarObjetoCustoCentroCusto(SigObjetoCustoCcts objetoCustoCentroCusto) {
		this.getSigObjetoCustoCctsDAO().atualizar(objetoCustoCentroCusto);
	}

	@Override
	public void persistirListaObjetoCustoCentroCusto(SigObjetoCustoVersoes objetoCustoVersao,
			List<SigObjetoCustoCcts> listaObjetoCustoCcts, List<SigObjetoCustoCcts> listaObjetoCustoCctsExclusao) {
		this.getCentroCustoComplementarON().persistirListaObjetoCustoCentroCusto(objetoCustoVersao, listaObjetoCustoCcts,
				listaObjetoCustoCctsExclusao);
	}

	@Override
	public void inativarVersaoAnterior(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		this.getManterObjetosCustoON().inativarVersaoAnterior(objetoCustoVersao);
	}

	@Override
	public boolean validarExclusaoAssociacaoVersoesObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) {
		return this.getObjetosCustoON().validarExclusaoAssociacaoVersoesObjetoCusto(objetoCustoVersao);
	}

	@Override
	public boolean validarExclusaoObjetoCustoAtivoMaisUmMes(SigObjetoCustoVersoes objetoCustoVersao) {
		return this.getObjetosCustoON().validarExclusaoObjetoCustoAtivoMaisUmMes(objetoCustoVersao);
	}

	@Override
	public void excluirVersaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		this.getObjetosCustoON().excluirVersaoObjetoCusto(objetoCustoVersao);
	}

	@Override
	public void validarInclusaoPhiObjetoCusto(SigObjetoCustoPhis sigObjetoCustoPhis, List<SigObjetoCustoPhis> listaPhis) throws ApplicationBusinessException {
		this.getManterObjetosCustoON().validarInclusaoPhiObjetoCusto(sigObjetoCustoPhis, listaPhis);
	}

	@Override
	public void validarInclusaoEquipamentoAtividade(SigAtividadeEquipamentos equipamento, List<SigAtividadeEquipamentos> listaEquipamentos)
			throws ApplicationBusinessException {
		this.getManterEquipamentosAtividadeON().validarInclusaoEquipamentoAtividade(equipamento, listaEquipamentos);
	}
	
	@Override
	public BigDecimal efetuarCalculoCustoMedioMaterial(ScoMaterial material) {
		return this.getManterInsumosAtividadeON().efetuarCalculoCustoMedioMaterial(material);
	}


	@Override
	public void persistPhi(SigObjetoCustoPhis sigObjetoCustoPhis) {
		this.getManterObjetosCustoON().persistPhi(sigObjetoCustoPhis);
	}

	@Override
	public void persistComposicoesObjetoCusto(SigObjetoCustoComposicoes sigObjetoCustoComposicoes) {
		this.getObjetoCustoComposicoesON().persistComposicoesObjetoCusto(sigObjetoCustoComposicoes);
	}

	@Override
	public void excluirComposicoesObjetoCusto(SigObjetoCustoComposicoes sigObjetoCustoComposicoes) {
		this.getSigObjetoCustoComposicoesDAO().removerPorId(sigObjetoCustoComposicoes.getSeq());
	}

	@Override
	public List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		return this.getSigObjetoCustoComposicoesDAO().pesquisarComposicoesPorObjetoCustoVersao(seqObjetoCustoVersao);
	}
	
	@Override
	public List<SigObjetoCustoDirRateios> pesquisarDirecionadorePorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		return this.sigObjetoCustoDirRateiosDAO.pesquisarDirecionadoresPorObjetoCustoVersao(seqObjetoCustoVersao);
	}

	@Override
	public List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersaoAtivo(Integer seqObjetoCustoVersao) {
		return this.getSigObjetoCustoComposicoesDAO().pesquisarComposicoesPorObjetoCustoVersaoAtivo(seqObjetoCustoVersao);
	}

	@Override
	public List<SigObjetoCustoPhis> pesquisarPhiPorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		return this.getSigObjetoCustoPhisDAO().pesquisarPhiPorObjetoCustoVersao(seqObjetoCustoVersao);
	}

	@Override
	public void excluirPhi(SigObjetoCustoPhis sigObjetoCustoPhis) {
		this.getSigObjetoCustoPhisDAO().removerPorId(sigObjetoCustoPhis.getSeq());
	}

	@Override
	public void validarInclusaoComposicaoObjetoCustoAssistencial(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException {
		this.getManterObjetosCustoON().validarInclusaoComposicaoObjetoCustoAssistencial(objetoCustoComposicao, listComposicao);
	}

	@Override
	public void validarInclusaoComposicaoObjetoCustoApoio(SigObjetoCustoComposicoes objetoCustoComposicao, List<SigObjetoCustoComposicoes> listComposicao)
			throws ApplicationBusinessException {
		this.getManterObjetosCustoON().validarInclusaoComposicaoObjetoCustoApoio(objetoCustoComposicao, listComposicao);
	}

	@Override
	public boolean possuiCalculo(SigAtividades atividade) {
		return this.getManterAtividadesON().possuiCalculo(atividade);
	}

	@Override
	public void validarExclusaoComposicaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		this.getManterObjetosCustoON().validarExclusaoComposicaoObjetoCusto(objetoCustoVersao);
	}

	@Override
	public List<ComposicaoAtividadeVO> buscarComposicaoAtividades(FccCentroCustos filtoCentroCusto, SigAtividades filtroAtividade,
			DominioSituacao filtroSituacao) throws ApplicationBusinessException {
		return this.getRelatorioComposicaoAtividadeON().montarListaComposicaoAtividades(filtoCentroCusto, filtroAtividade, filtroSituacao);
	}

	@Override
	public List<SigAtividades> listarAtividadesRestringindoCentroCusto(FccCentroCustos centroCusto, Object objPesquisa) {
		return getSigAtividadesDAO().listarAtividadesRestringindoCentroCusto(centroCusto, objPesquisa);
	}

	@Override
	public List<SigAtividades> listAtividadesAtivas(FccCentroCustos centroCusto, Object objPesquisa) {
		return getSigAtividadesDAO().pesquisarAtividadesAtivas(centroCusto, objPesquisa);
	}

	@Override
	public List<SigAtividades> listAtividadesAtivas(List<FccCentroCustos> listCentroCusto, Object objPesquisa) {
		return getSigAtividadesDAO().pesquisarAtividadesAtivas(listCentroCusto, objPesquisa);
	}

	@Override
	public void efetuaCargaExamesComoObjetoCusto(String descricaoUsual, FccCentroCustos centroCustoCCTS, String siglaExameEdicao)
			throws ApplicationBusinessException {
		this.getEfetuaCargaObjetoCustoRN().efetuaCargaExames(descricaoUsual, centroCustoCCTS, siglaExameEdicao);
	}

	@Override
	public void copiaObjetoCustoComposicoes(SigObjetoCustoVersoes sigObjetoCustoVersoes, List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes) throws ApplicationBusinessException {
		this.getObjetoCustoComposicoesON().copiaObjetoCustoComposicoes(sigObjetoCustoVersoes, listaObjetoCustoComposicoes);
	}

	@Override
	public SigAtividades copiaAtividade(SigAtividades atividadesSuggestion, String nome, boolean pessoal, boolean insumos, boolean equipamentos,
			boolean servicos) throws ApplicationBusinessException {
		return this.getManterAtividadesON().copiaAtividade(atividadesSuggestion, nome, pessoal, insumos, equipamentos, servicos);
	}

	@Override
	public Map<String, Object> obterAtividadeDesatachado(Integer seqAtividade) {
		return analiseHistoricoON.obterAtividadeDesatachado(this.obterAtividade(seqAtividade));
	}

	@Override
	public List<SigObjetoCustoComposicoes> pesquisarObjetoCustoComposicaoAtivoObjetoCustoVersaoAtivo(SigAtividades atividade) {
		return this.getSigObjetoCustoComposicoesDAO().pesquisarObjetoCustoComposicaoAtivoObjetoCustoVersaoAtivo(atividade);
	}

	@Override
	public SigObjetoCustoVersoes criaNovaVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes) throws ApplicationBusinessException {
		return this.getObjetoCustoComposicoesON().criaNovaVersao(sigObjetoCustoVersoes);
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonio(Object paramPesquisa, Integer centroCustoAtividade)
			throws ApplicationBusinessException {
		return this.patrimonioService.pesquisarEquipamentoSistemaPatrimonio(paramPesquisa, centroCustoAtividade);
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonioByDescricao(String descricao, Integer centroCustoAtividade)
			throws ApplicationBusinessException {
		return patrimonioService.consultaEquipamentoPelaDescricao(descricao, centroCustoAtividade);
	}

	@Override
	public EquipamentoSistemaPatrimonioVO pesquisarEquipamentoSistemaPatrimonioById(String idEquipamentoSistemaPatrimonio, Integer centroCustoAtividade)
			throws ApplicationBusinessException {
		return patrimonioService.buscarInfoModuloPatrimonio(idEquipamentoSistemaPatrimonio, centroCustoAtividade);
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentosSistemaPatrimonioById(List<String> listCodigo) throws ApplicationBusinessException{
		return patrimonioService.pesquisarEquipamentosSistemaPatrimonioById(listCodigo);
	}
	
	@Override
	public List<ObjetoCustoAssociadoAtividadeVO> pesquisarObjetosCustoAssociadosAtividades(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SigAtividades atividade) {
		return this.getSigObjetoCustoComposicoesDAO().pesquisarObjetosCustoAssociadosAtividades(firstResult, maxResult, orderProperty, asc, atividade);
	}

	@Override
	public Integer pesquisarObjetosCustoAssociadosAtividadesCount(SigAtividades atividade) {
		return this.getSigObjetoCustoComposicoesDAO().pesquisarObjetosCustoAssociadosAtividadesCount(atividade);
	}

	@Override
	public boolean validarCentroCustoComposicaoAssistencial(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, FccCentroCustos centroCustoObjetoCusto) {
		return this.getManterObjetosCustoON().validarCentroCustoComposicaoAssistencial(listaObjetoCustoComposicoes, centroCustoObjetoCusto);
	}

	@Override
	public Object[] validarAtividadePertenceAoCentroCustoComposicaoApoio(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes,
			List<FccCentroCustos> listaCC) {
		return this.getManterObjetosCustoON().validarAtividadePertenceAoCentroCustoComposicaoApoio(listaObjetoCustoComposicoes, listaCC);
	}

	@Override
	public List<DominioSituacaoVersoesCustos> selecionaSituacaoPossivelDoObjetoCusto(DominioSituacaoVersoesCustos situacaoAnterior,
			SigObjetoCustoVersoes objetoCustoVersao) {
		return this.getManterObjetosCustoON().selecionaSituacaoPossivelDoObjetoCusto(situacaoAnterior, objetoCustoVersao);
	}

	@Override
	public List<EntradaProducaoObjetoCustoVO> efetuaLeituraExcel(InputStream arquivo, DominioColunaExcel colCentroCusto, DominioColunaExcel colValor, int linInicial)
			throws ApplicationBusinessException, IOException {
		return getEfetuaLeituraCargaProducaoObjetoCustoON().efetuaLeituraExcel(arquivo, colCentroCusto, colValor, linInicial);
	}

	@Override
	public List<SigObjetoCustos> pesquisarObjetoCustoAssociadoClientes(Object param, FccCentroCustos centroCusto) {
		return this.getSigObjetoCustosDAO().pesquisarObjetoCustoAssociadoClientes(param, centroCusto);
	}

	
	
	
	@Override
	public List<ObjetoCustoPorCentroCustoVO> pesquisarObjetoCustoPorCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao, SigCentroProducao centroProducao, DominioSituacao indSituacao) {
		return this.getSigObjetoCustosDAO().pesquisarObjetoCustoPorCentroCusto(firstResult, maxResult, orderProperty, asc, centroCusto, indPossuiObjCusto,
				indPossuiComposicao, centroProducao, indSituacao);
	}

	@Override
	public Integer pesquisarObjetoCustoPorCentroCustoCount(FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao,
			SigCentroProducao centroProducao, DominioSituacao indSituacao) {
		return this.getSigObjetoCustosDAO().pesquisarObjetoCustoPorCentroCustoCount(centroCusto, indPossuiObjCusto, indPossuiComposicao, centroProducao,
				indSituacao);
	}

	@Override
	public List<ComposicaoObjetoCustoVO> buscarComposicaoObjetosCusto(FccCentroCustos filtoCentroCusto, SigObjetoCustos filtroObjetoCusto,
			DominioSituacaoVersoesCustos filtroSituacao, DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto) throws ApplicationBusinessException {
		return this.getRelatorioComposicaoObjetoCustoON().montarListaComposicaoObjetoCusto(filtoCentroCusto, filtroObjetoCusto, filtroSituacao,
				filtroComposicaoObjetoCusto);
	}

	@Override
	public List<SigObjetoCustos> pesquisarObjetoCustoAssociadoCentroCustoOuSemCentroCusto(Object param, FccCentroCustos filtoCentroCusto) {
		return this.getSigObjetoCustosDAO().pesquisarObjetoCustoAssociadoCentroCustoOuSemCentroCusto(param, filtoCentroCusto);
	}

	@Override
	public List<Object[]> buscarAutorizacaoFornecimento(Object paramPesquisa) {
		return this.getSigAtividadesDAO().buscarAutorizacaoFornecimento(paramPesquisa);
	}

	@Override
	public List<Object[]> pesquisarAutorizFornecServico(Integer tvdSeq) {
		return this.getSigAtividadeServicosDAO().pesquisarAutorizFornecServico(tvdSeq);
	}

	@Override
	public SigAtividadeServicos obterAtividadeServico(Integer atvServSeq) {
		return getSigAtividadeServicosDAO().obterPorChavePrimaria(atvServSeq);
	}
	
	@Override
	public SigAtividadeServicos obterAtividadeServicoDetalhada(Integer atvServSeq) {
		return getSigAtividadeServicosDAO().obterAtividadeServicoDetalhada(atvServSeq);
	}

	@Override
	public void persistirEquipamentoCirurgico(SigEquipamentoPatrimonio equipamento) {
		this.getSigEquipamentoPatrimonioDAO().persistir(equipamento);
	}

	@Override
	public void excluirEquipamentoCirurgico(SigEquipamentoPatrimonio equipamento) {
		this.getSigEquipamentoPatrimonioDAO().removerPorId(equipamento.getSeq());
	}

	@Override
	public List<SigEquipamentoPatrimonio> pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio(String codigo) {
		return this.getSigEquipamentoPatrimonioDAO().pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio(codigo);
	}

	@Override
	public List<SigEquipamentoPatrimonio> buscaEquipametosCirurgicos(MbcEquipamentoCirurgico equipamentoCirurgico) {
		return this.getSigEquipamentoPatrimonioDAO().buscaEquipametosCirurgicos(equipamentoCirurgico);
	}

	
	@Override
	public SigObjetoCustoVersoes atualizarObjetoCustoVersao(SigObjetoCustoVersoes objeto) {
		return this.getSigObjetoCustoVersoesDAO().atualizar(objeto);
	}
	
	@Override
	public SigObjetoCustoVersoes atualizar(SigObjetoCustoVersoes objeto) {
		return this.getSigObjetoCustoVersoesDAO().atualizar(objeto);
	}
	
	@Override
	public void desatacharObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.getSigObjetoCustoVersoesDAO().desatachar(objetoCustoVersao);
	}
	
	@Override
	public SigObjetoCustoVersoes buscarObjetoCustoVersoesAtualizado(SigObjetoCustoVersoes sigObjCustoVersoes) {
		return this.getSigObjetoCustoVersoesDAO().buscarObjetoCustoVersoesAtualizado(sigObjCustoVersoes);
	}
	
	@Override
	public void mudarValorViao(DominioVisaoCustoPaciente visao) {
		this.getVisualizarCustoPacienteON().mudarValorViao(visao);
	}


	@Override
	public List<SigProcessamentoCusto> obterSigProcessamentoCustoPorSituacao(DominioSituacaoProcessamentoCusto[] situacao, String campoOrderBy) {
		return this.getSigProcessamentoCustoDAO().obterSigProcessamentoCustoPorSituacao(situacao, campoOrderBy);
	}

	@Override
	public List<SigProcessamentoCusto> obterListaCompetencias(DominioVisaoCustoPaciente visao) {
		return this.getVisualizarCustoPacienteON().visaoCompetencia(visao);
	}

	@Override
	public void adicionarCIDNaLista(List<AghCid> listaCID, AghCid cid) throws ApplicationBusinessException{
		
		this.getVisualizarCustoPacienteON().adicionarCIDNaLista(listaCID, cid); 
	}

	@Override
	public void deletarCIDDaLista(List<AghCid> listaCID, AghCid cid) {
		this.getVisualizarCustoPacienteON().deletarCIDDaLista(listaCID, cid);
		
	}

	@Override
	public void adicionarCentroCustoNaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto)
			throws ApplicationBusinessException {
		
		this.getVisualizarCustoPacienteON().adicionarCentroCustoNaLista(listaCentroCusto, centroCusto);
	}

	@Override
	public void deletarCentroCustoDaLista(List<FccCentroCustos> listaCentroCusto, FccCentroCustos centroCusto) {
		this.getVisualizarCustoPacienteON().deletarCentroCustoDaLista(listaCentroCusto, centroCusto);
		
	}

	@Override
	public void adicionarEspecialidadesNaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		this.getVisualizarCustoPacienteON().adicionarEspecialidadesNaLista(listaEspecialidades, especialidade);
		
	}

	@Override
	public void deletarEspecialidadesDaLista(List<AghEspecialidades> listaEspecialidades, AghEspecialidades especialidade) {
		this.getVisualizarCustoPacienteON().deletarEspecialidadesDaLista(listaEspecialidades, especialidade);
		
	}

	@Override
	public void adicionarEquipeNaLista(List<AghEquipes> listaEquipes, AghEquipes equipes)
			throws ApplicationBusinessException {
		this.getVisualizarCustoPacienteON().adicionarEquipesNaLista(listaEquipes, equipes);
		
	}

	@Override
	public void deletarEquipesDaLista(List<AghEquipes> listaEquipes, AghEquipes equipes) {
		this.getVisualizarCustoPacienteON().deletarEquipesDaLista(listaEquipes, equipes);
		
	}

	@Override
	public List<RapServidores> obterListaReponsaveisPorListaDeEquipes(List<AghEquipes> equipes){
		return this.getVisualizarCustoPacienteON().obterListaReponsaveisPorListaDeEquipes(equipes);
	}

	@Override
	public void validarPacienteInformadoNoFiltro(AipPacientes paciente) throws ApplicationBusinessException{
		this.getVisualizarCustoPacienteON().validarPacienteInformadoNoFiltro(paciente);
	}
	
	@Override
	public void obterValoresCustosReceitasPorProntuario(List<AghAtendimentosVO> listaAtendimentoVO) {
		this.getVisualizarCustoPacienteON().obterValoresCustosReceitasPorProntuario(listaAtendimentoVO);
	}
	
	@Override
	public void obterValoresCustosReceitasPorProntuarioEProcessamento(List<AghAtendimentosVO> listaAtendimentoVO, Integer pmuSeq) {
		this.getVisualizarCustoPacienteON().obterValoresCustosReceitasPorProntuarioEProcessamento(listaAtendimentoVO, pmuSeq);
	}

	@Override
	public void validarConfirmar(List<AghAtendimentosVO> listagem)
			throws ApplicationBusinessException {
		this.getVisualizarCustoPacienteON().validarConfirmar(listagem);
	}

	@Override
	public void validarListaVaziaExibeMensagem(List<AghAtendimentosVO> listagem,  String[] informacoesMensagem) throws ApplicationBusinessException{
		this.getVisualizarCustoPacienteON().validarListaVaziaExibeMensagem(listagem, informacoesMensagem);
	}

	@Override
	public String[] buscarInformacoesParaMostrarMensagem(SigProcessamentoCusto competencia, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEquipes> listaEquipes, List<AghEspecialidades> listaEspecialidades) {
		return this.getVisualizarCustoPacienteON().buscarInformacoesParaMostrarMensagem(competencia, listaCID, listaCentroCusto, listaEquipes, listaEspecialidades);
	}

	@Override
	public List<AghAtendimentosVO> obterSelecionados(List<AghAtendimentosVO> listagem) throws ApplicationBusinessException {
		return this.getVisualizarCustoPacienteON().obterSelecionados(listagem);
	}

	@Override
	public List<ConsumoPacienteNodoVO> pesquisarConsumoPaciente(
			PesquisarConsumoPacienteVO vo) {
		return getSigProcessamentoCustoDAO().pesquisarConsumoPaciente(vo);
	}
	
	@Override
	public SigProcessamentoCusto obterSigProcessamentoCustoPorChavePrimaria(Integer chavePrimaria) {
		return this.getSigProcessamentoCustoDAO().obterPorChavePrimaria(chavePrimaria);
	}
	
	
	@Override
	public boolean verificarObjetoCustoPossuiComposicao(Integer seq){
		return !this.getSigObjetoCustoComposicoesDAO().pesquisarComposicoesPorObjetoCustoVersao(seq).isEmpty();
	}
	
	@Override
	public List<SigObjetoCustoVersoes> buscarObjetoCustoVersoesCentroCusto(Integer seqCct, String paramPesquisa) {
		return this.getSigObjetoCustoVersoesDAO().buscarObjetoCustoVersoesCentroCusto(seqCct, paramPesquisa);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteVisaoGeral(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCusto(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, Short espSeq, Integer atdSeq, Boolean isEspecialidade, List<Integer> seqCategorias) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteVisaoGeralCentroCusto(prontuario, pmuSeq, codCentroCusto, espSeq, atdSeq, isEspecialidade, seqCategorias);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCentrosCustoVisaoGeral(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq) {
		return this.getSigCalculoAtdPacienteDAO().buscarCentrosCustoVisaoGeral(prontuario, pmuSeq, codCentroCusto, categorias, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCustoCategoria(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteVisaoGeralCentroCustoCategoria(prontuario, pmuSeq, codCentroCusto, categorias, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(Integer prontuario, Integer pmuSeq, List<Integer> categorias, Integer atdSeq) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteVisaoGeral(prontuario, pmuSeq, categorias, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarEspecialidades(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias) {
		return this.getSigCalculoAtdPacienteDAO().buscarEspecialidades(prontuario, pmuSeq, atdSeq, seqCategorias);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarEquipesMedicas(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias) {
		return this.getCalculoAtendimentoPacienteON().buscarEquipesMedicas(prontuario, pmuSeq, atdSeq, seqCategorias);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteEquipeMedica(Integer prontuario, Integer pmuSeq, Integer matriculaResp, Short vinCodigoResp, Integer atdSeq, List<Integer> seqCategoria) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteEquipeMedica(prontuario, pmuSeq, matriculaResp, vinCodigoResp, atdSeq, seqCategoria);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustosPacienteInternacao(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public List<SigCalculoAtdProcedimentos> buscarProcedimentosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		return sigCalculoAtdProcedimentosDAO.buscarProcedimentosPacienteInternacao(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public BigDecimal buscarCustoTotal(Integer prontuario, Integer pmuSeq, boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias) {
		return this.getSigCalculoAtdPacienteDAO().buscarCustoTotal(prontuario, pmuSeq, isEspecialidade, isEquipeMedica, atdSeq, seqCategorias);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		return this.getSigCalculoAtdReceitaDAO().pesquisarReceitaGeral(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroCusto(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq) {
		return this.getSigCalculoAtdReceitaDAO().pesquisarReceitaPorCentroCusto(prontuario, pmuSeq, atdSeq, listaCtcSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroProducao(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq) {
		return this.getSigCalculoAtdReceitaDAO().pesquisarReceitaPorCentroProducao(prontuario, pmuSeq, atdSeq, listaCtcSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEspecialidade(Integer prontuario, Integer pmuSeq, Integer atdSeq){
		return this.getSigCalculoAtdReceitaDAO().pesquisarReceitaPorEspecialidade(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEquipeMedica(Integer prontuario, Integer pmuSeq, Integer atdSeq){
		return this.getSigCalculoAtdReceitaDAO().pesquisarReceitaPorEquipeMedica(prontuario, pmuSeq, atdSeq);
	}
	
	@Override
	public BigDecimal buscarValorTotalReceita(Integer prontuario, Integer pmuSeq, boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias) {
		return getSigCalculoAtdPaciente2DAO().buscarValorTotalReceita(prontuario, pmuSeq, isEspecialidade, isEquipeMedica, atdSeq, seqCategorias);
	}
	
	@Override
	public BigDecimal buscarCustoTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		return this.getCalculoAtendimentoPacienteON().buscarCustoTotalPesquisa(prontuario, pmuSeq, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	@Override
	public BigDecimal buscarReceitaTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		return this.getCalculoAtendimentoPacienteON().buscarReceitaTotalPesquisa(prontuario, pmuSeq, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	@Override
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosPrimeiroNivel(Integer pmuSeq, String cidPrincipal, List<AghCid> listaCids, DominioCidCustoPacienteDiagnostico tipo) {
		return this.getVSigCustosCalculoCidDAO().pesquisarDiagnosticosPrimeiroNivel(pmuSeq, cidPrincipal, listaCids, tipo);
	}
	
	@Override
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosSegundoNivel(Integer pmuSeq, Integer cidSeq, List<Integer> listaSeq, Integer quantidadeCids, DominioCidCustoPacienteDiagnostico tipo) {
		return this.visualizarCustoPacienteDiagnosticoON.pesquisarDiagnosticosSegundoNivel(pmuSeq, cidSeq, listaSeq, quantidadeCids, tipo);
	}
	
	
	@Override
	public List<VSigCustosCalculoCidVO> obterListaPacientes(Integer pmuSeq, List<Integer> listaAtendimentos, Integer cidSeq) {
		return this.getVSigCustosCalculoCidDAO().obterListaPacientes(pmuSeq, listaAtendimentos, cidSeq);
	}
	
	
	@Override
	public List<ItemProcedHospVO> buscarProcedimentos(Integer pmuSeq, String descricao) {
		return vSigCustosCalculoProcedDAO.buscarProcedimentos(pmuSeq, descricao);
	}
	
	@Override
	public Long buscarProcedimentosCount(Integer pmuSeq, String descricao) {
		return vSigCustosCalculoProcedDAO.buscarProcedimentosCount(pmuSeq, descricao);
	}

	@Override
	public List<CalculoProcedimentoVO> buscarProcedimentosPrimeiroNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, ItemProcedHospVO procedimentoPrincipal,DominioProcedimentoCustoPaciente tipo, List<ItemProcedHospVO> procedimentos, List<Short> conveniosSelecionados) {
		return vSigCustosCalculoProcedDAO.buscarProcedimentosPrimeiroNivel(processamento, tipoCompetencia, procedimentoPrincipal, tipo, procedimentos, conveniosSelecionados);
	}

	@Override
	public List<CalculoProcedimentoVO> buscarProcedimentosSegundoNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Short iphPhoSeq, Integer iphSeq, List<ItemProcedHospVO> listaProcedimento, Integer quantidadeProcedimentos, DominioProcedimentoCustoPaciente tipo, List<Short> conveniosSelecionados) {
		return this.getVisualizarCustoPacienteProcedimentoON().pesquisarDiagnosticosSegundoNivel(processamento, tipoCompetencia, iphPhoSeq, iphSeq, listaProcedimento, quantidadeProcedimentos, tipo, conveniosSelecionados);
	}

	@Override
	public List<CalculoProcedimentoVO> buscarPacientesProcedimentos(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, List<Integer> atendimentos, List<Short> conveniosSelecionados) {
		return vSigCustosCalculoProcedDAO.buscarPacientesProcedimentos(processamento, tipoCompetencia, atendimentos, conveniosSelecionados);
	}
	
	@Override
	public List<Integer> buscarContasPaciente(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Integer atdSeq, List<Short> conveniosSelecionados) {
		return vSigCustosCalculoProcedDAO.buscarContasPaciente(processamento, tipoCompetencia, atdSeq, conveniosSelecionados);
	}
	
	@Override
	public void adicionarProcedimentoNaLista(List<ItemProcedHospVO> listaProcedimentos, ItemProcedHospVO procedimento) throws ApplicationBusinessException{
		this.getVisualizarCustoPacienteProcedimentoON().adicionarProcedimentoNaLista(listaProcedimentos, procedimento); 
	}
	
	@Override
	public List<DetalheConsumoVO> listarDetalheConsumo(Integer atdSeq, Integer pmuSeq, Integer cctCodigo, String nomeObjetoCusto){
		return this.sigCalculoDetalheConsumoDAO.listarDetalheConsumo(atdSeq, pmuSeq, cctCodigo, nomeObjetoCusto);
	}

	@Override
	public void deletarProcedimentoDaLista(List<ItemProcedHospVO> listaProcedimentos, ItemProcedHospVO procedimento) {
		this.getVisualizarCustoPacienteProcedimentoON().deletarProcedimentoDaLista(listaProcedimentos, procedimento);
	}
	
	@Override
	public SigCategoriaRecurso obterCategoriaRecursoPorChavePrimaria(Integer seq) {
		return this.sigCategoriaRecursoDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public List<SigAtividadePessoaRestricoes> listarAtividadePessoaRestricoes(SigAtividadePessoas sigAtividadePessoas) {
		return this.sigAtividadePessoaRestricoesDAO.listarAtividadePessoaRestricoes(sigAtividadePessoas);
	}
	
	// /////////////////////////////////////////////////////////////////////////////////
	// get DAOs

	protected VisualizarCustoPacienteON getVisualizarCustoPacienteON(){
		return visualizarCustoPacienteON;
	}
	
	protected VisualizarCustoPacienteProcedimentoON getVisualizarCustoPacienteProcedimentoON(){
		return visualizarCustoPacienteProcedimentoON;
	}
	
	protected EfetuaLeituraCargaProducaoObjetoCustoON getEfetuaLeituraCargaProducaoObjetoCustoON() {
		return efetuaLeituraCargaProducaoObjetoCustoON;
	}

	protected SigEquipamentoPatrimonioDAO getSigEquipamentoPatrimonioDAO() {
		return sigEquipamentoPatrimonioDAO;
	}

	protected SigCalculoObjetoCustoDAO getSigCalculoObjetoCustoDAO() {
		return sigCalculoObjetoCustoDAO;
	}

	protected SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}

	protected SigAtividadeEquipamentosDAO getSigAtividadeEquipamentosDAO() {
		return sigAtividadeEquipamentosDAO;
	}

	protected SigAtividadePessoasDAO getSigAtividadePessoasDAO() {
		return sigAtividadePessoasDAO;
	}

	protected SigAtividadeServicosDAO getSigAtividadeServicosDAO() {
		return sigAtividadeServicosDAO;
	}

	protected SigAtividadeInsumosDAO getSigAtividadeInsumosDAO() {
		return sigAtividadeInsumosDAO;
	}

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	protected VSigAfsContratosServicosDAO getVSigAfsContratosServicosDAO() {
		return vSigAfsContratosServicosDAO;
	}

	protected SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO() {
		return sigObjetoCustoPhisDAO;
	}

	protected SigObjetoCustosDAO getSigObjetoCustosDAO() {
		return sigObjetoCustosDAO;
	}

	protected SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO() {
		return sigObjetoCustoCctsDAO;
	}

	protected SigObjetoCustoComposicoesDAO getSigObjetoCustoComposicoesDAO() {
		return sigObjetoCustoComposicoesDAO;
	}

//	protected SigObjetoCustoDirRateiosDAO getSigObjetoCustoDirRateiosDAO(){
//		return sigObjetoCustoDirRateiosDAO;
//	}
	protected SigObjetoCustoHistoricosDAO getSigObjetoCustoHistoricosDAO() {
		return sigObjetoCustoHistoricosDAO;
	}
	
	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}
	
	protected VSigCustosCalculoCidDAO getVSigCustosCalculoCidDAO() {
		return vSigCustosCalculoCidDAO;
	}

	// get ON

	protected RelatorioComposicaoObjetoCustoON getRelatorioComposicaoObjetoCustoON() {
		return relatorioComposicaoObjetoCustoON;
	}

	protected EfetuaCargaObjetoCustoON getEfetuaCargaObjetoCustoRN() {
		return efetuaCargaObjetoCustoON;
	}

	protected DirecionadorRateioObjetoCustoON getDirecionadorRateioObjetoCustoON() {
		return direcionadorRateioObjetoCustoON;
	}

	protected ManterAtividadesON getManterAtividadesON() {
		return manterAtividadesON;
	}
	
	protected ObjetoCustoComposicoesON getObjetoCustoComposicoesON() {
		return objetoCustoComposicoesON;
	}

	protected ManterPessoasAtividadeON getManterPessoasAtividadeON() {
		return manterPessoasAtividadeON;
	}

	protected ManterInsumosAtividadeON getManterInsumosAtividadeON() {
		return manterInsumosAtividadeON;
	}

	protected ManterObjetosCustoON getManterObjetosCustoON() {
		return manterObjetosCustoON;
	}

	protected ObjetosCustoON getObjetosCustoON() {
		return objetosCustoON;
	}

	protected CentroCustoComplementarON getCentroCustoComplementarON() {
		return centroCustoComplementarON;
	}

	protected ManterServicosAtividadeON getManterServicosAtividadeON() {
		return manterServicosAtividadeON;
	}

	protected ManterEquipamentosAtividadeON getManterEquipamentosAtividadeON() {
		return manterEquipamentosAtividadeON;
	}

	protected RelatorioComposicaoAtividadeON getRelatorioComposicaoAtividadeON() {
		return relatorioComposicaoAtividadeON;
	}

	protected CalculoMovimentosObjetoCentroCustoON getCalculoObjetoCustoON() {
		return calculoMovimentosObjetoCentroCustoON;
	}
	
	protected GrupoOcupacaoON getGrupoOcupacoesON() {
		return grupoOcupacaoON;
	}
	
	protected PesoObjetoCustoON getPesoObjetoCustoON() {
		return pesoObjetoCustoON;
	}

	public SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}
	
	private SigAtividadeCentroCustosDAO getSigAtividadeCentroCustosDAO() {
		return sigAtividadeCentroCustosDAO;
	}

	public SigCalculoAtdPacienteDAO getSigCalculoAtdPacienteDAO() {
		return sigCalculoAtdPacienteDAO;
	}
	
	public SigCalculoAtdReceitaDAO getSigCalculoAtdReceitaDAO() {
		return sigCalculoAtdReceitaDAO;
	}

	public CalculoAtendimentoPacienteON getCalculoAtendimentoPacienteON() {
		return calculoAtendimentoPacienteON;
	}

	public SigCalculoAtdPaciente2DAO getSigCalculoAtdPaciente2DAO() {
		return sigCalculoAtdPaciente2DAO;
	}
}