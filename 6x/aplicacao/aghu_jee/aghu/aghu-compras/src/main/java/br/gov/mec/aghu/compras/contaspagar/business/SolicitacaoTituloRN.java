package br.gov.mec.aghu.compras.contaspagar.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTipoTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloSolicitacoesDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroSolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVOComparator;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloSemLicitacaoVO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoDeCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoTitulo;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SolicitacaoTituloRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4399352774489517653L;

	private static final Log LOG = LogFactory.getLog(SolicitacaoTituloRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	
	@Inject
	private ScoSolicitacaoDeCompraDAO scoSolicitacaoDeCompraDAO;
	
	@Inject
	private FcpTituloSolicitacoesDAO tituloSolicitacoesDAO;
	
	@Inject
	private FcpTituloDAO tituloDAO;
	
	@Inject
	private FsoNaturezaDespesaDAO fsoNaturezaDespesaDAO;
	
	@Inject
	private FcpTipoTituloDAO fcpTipoTituloDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * #46298 - Recuperar lista paginada de solicitações.
	 * 
	 * @return {@link List} de {@link SolicitacaoTituloVO}
	 */
	public List<SolicitacaoTituloVO> recuperarListaPaginadaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<SolicitacaoTituloVO> lista = obterListaSolicitacaoTitulo(filtro, pontoParada);
		
		Comparator<SolicitacaoTituloVO> comparador = new SolicitacaoTituloVOComparator.OrderBySolicitacao();
		
		if (StringUtils.isNotEmpty(orderProperty)) {
			if (orderProperty.equals(SolicitacaoTituloVO.Fields.TIPO.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByTipo();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.SOLICITACAO.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderBySolicitacao();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.CODIGO.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByCodigo();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.SERVICO_MATERIAL.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByServicoMaterial();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.GRUPO_NATUREZA_DESPESA.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByGrupoNaturezaDespesa();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.NTD_CODIGO.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByNtdCodigo();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.NATUREZA_DESPESA.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByNaturezaDespesa();
			} else if (orderProperty.equals(SolicitacaoTituloVO.Fields.DATA_GERACAO.toString())) {
				comparador = new SolicitacaoTituloVOComparator.OrderByDataGeracao();
			} 
		} else {
			asc = false;
		}
		
		Collections.sort(lista, comparador);
		if (!asc) {
			Collections.reverse(lista);
		}
		
		if (lista.size() < firstResult + maxResult) {
			return lista.subList(firstResult, lista.size());
		} else {
			return lista.subList(firstResult, firstResult + maxResult);
		}
	}

	/**
	 * #46298 - Recuperar lista completa de solicitações, conforme filtro informado.
	 * 
	 * @return Todos os elementos de {@link SolicitacaoTituloVO}, conforme filtro informado.
	 */
	public List<SolicitacaoTituloVO> recuperarListaCompletaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada) {
		
		return obterListaSolicitacaoTitulo(filtro, pontoParada);
	}
	
	private List<SolicitacaoTituloVO> obterListaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada) {
		
		List<SolicitacaoTituloVO> lista = new ArrayList<SolicitacaoTituloVO>();
		List<SolicitacaoTituloVO> listaServico = new ArrayList<SolicitacaoTituloVO>();
		List<SolicitacaoTituloVO> listaDeCompra = new ArrayList<SolicitacaoTituloVO>();
		
		if (filtro != null) {
			if (filtro.getTipo() == null || filtro.getTipo().equals(DominioTipoSolicitacaoTitulo.SS)) {
				listaServico = scoSolicitacaoServicoDAO.obterListaSolicitacaoServico(filtro, pontoParada);
			}
			if (filtro.getTipo() == null || filtro.getTipo().equals(DominioTipoSolicitacaoTitulo.SC)) {
				listaDeCompra = scoSolicitacaoDeCompraDAO.obterListaSolicitacaoDeCompra(filtro, pontoParada);
			}
		}
		lista.addAll(editarValorTipoSolicitacao(listaServico, DominioTipoSolicitacaoTitulo.SS.toString()));
		lista.addAll(editarValorTipoSolicitacao(listaDeCompra, DominioTipoSolicitacaoTitulo.SC.toString()));
		
		return lista;
	}

	private List<SolicitacaoTituloVO> editarValorTipoSolicitacao(List<SolicitacaoTituloVO> listaSolicitacao, String tipoSolicitacao) {
		
		if (listaSolicitacao != null && !listaSolicitacao.isEmpty()) {
			for (SolicitacaoTituloVO solicitacao : listaSolicitacao) {
				solicitacao.setTipo(tipoSolicitacao);
			}
		}
		
		return listaSolicitacao;
	}
	
	public List<SolicitacaoTituloVO>  obterTitulosSolicitacaoCompraServico(Integer ttlseq){
		
		List<SolicitacaoTituloVO> lista = new ArrayList<SolicitacaoTituloVO>();
		List<SolicitacaoTituloVO> listaServico = new ArrayList<SolicitacaoTituloVO>();
		List<SolicitacaoTituloVO> listaDeCompra = new ArrayList<SolicitacaoTituloVO>();
		
        listaServico =  tituloSolicitacoesDAO.listarTitulosSolicitacaoServico(ttlseq);
	    listaDeCompra =  tituloSolicitacoesDAO.listarTitulosSolicitacaoCompra(ttlseq);
	    lista.addAll(editarValorTipoSolicitacao(listaServico, DominioTipoSolicitacaoTitulo.SS.toString()));
		lista.addAll(editarValorTipoSolicitacao(listaDeCompra, DominioTipoSolicitacaoTitulo.SC.toString()));
		
		Comparator<SolicitacaoTituloVO> comparador = new SolicitacaoTituloVOComparator.OrderBySolicitacao();
		Collections.sort(lista, comparador);
		Collections.reverse(lista);
		
		return lista;
	}
	
	public void alterarSolicitacoes(List<SolicitacaoTituloVO> listaSolicitacao,ConsultaGeralTituloVO tituloAlterado) throws ApplicationBusinessException, EJBException {
		 Double valorTitulSoma=Double.valueOf(0);
		if (listaSolicitacao != null && !listaSolicitacao.isEmpty()) {
			for (SolicitacaoTituloVO solicitacao : listaSolicitacao) {
				FcpTituloSolicitacoes tituloSolicitacoes = tituloSolicitacoesDAO.obterPorChavePrimaria(solicitacao.getSeqTituloSolicitacao());
				if(!tituloSolicitacoes.getValor().equals(solicitacao.getValorTitulSolicitacao())){
					tituloSolicitacoes.setValor(solicitacao.getValorTitulSolicitacao());
					tituloSolicitacoes.setVersion(tituloSolicitacoes.getVersion()+1);
					tituloSolicitacoesDAO.atualizar(tituloSolicitacoes);
				}
				valorTitulSoma=valorTitulSoma+solicitacao.getValorTitulSolicitacao();
			}

				FcpTitulo titulo = tituloDAO.obterPorChavePrimaria(tituloAlterado.getTtlSeq());
				titulo.setValor(valorTitulSoma);
				titulo.setIndSituacao((tituloAlterado.getSituacaoTitulo()));
				titulo.setDtVencimento(tituloAlterado.getDataVencimento());
				tituloDAO.atualizar(titulo);
		}
	}
	
	/**
	 * #46310 - Gerar Titulos sem Licitação para as Solicitações selecionadas. Realiza o agrupamento por grupo natureza.
	 * 
	 * @param titulo Instancia de {@link TituloSemLicitacaoVO}
	 * @param listaSolicitacao {@link List} de {@link SolicitacaoTituloVO}
	 * @return {@link List} de {@link FcpTitulo}
	 * @throws ApplicationBusinessException 
	 */
	public List<FcpTitulo> gerarTitulosSemLicitacao(TituloSemLicitacaoVO titulo, List<SolicitacaoTituloVO> listaSolicitacao) 
			throws ApplicationBusinessException {

		List<FcpTitulo> listaTitulo = new ArrayList<FcpTitulo>();
		while (!listaSolicitacao.isEmpty()) {
			SolicitacaoTituloVO solicitacao = listaSolicitacao.get(0);
			FsoNaturezaDespesa naturezaDespesa = null;
			if (solicitacao != null && solicitacao.getGrupoNaturezaDespesa() != null && solicitacao.getNtdCodigo() != null) {
				FsoNaturezaDespesaId naturezaDespesaId = new FsoNaturezaDespesaId(solicitacao.getGrupoNaturezaDespesa(), solicitacao.getNtdCodigo());
				naturezaDespesa = fsoNaturezaDespesaDAO.obterNaturezaDespesa(naturezaDespesaId);
			}
			List<SolicitacaoTituloVO> grupoComNaturezaDespesa = new ArrayList<SolicitacaoTituloVO>();
			List<SolicitacaoTituloVO> grupoSemNaturezaDespesa = new ArrayList<SolicitacaoTituloVO>();
			for (SolicitacaoTituloVO item : listaSolicitacao) {
				if (item.getGrupoNaturezaDespesa() == null || item.getNtdCodigo() == null) {
					grupoSemNaturezaDespesa.add(item);
				} else {
					if (item.getGrupoNaturezaDespesa().equals(solicitacao.getGrupoNaturezaDespesa())
							&& item.getNtdCodigo().equals(solicitacao.getNtdCodigo())) {
						grupoComNaturezaDespesa.add(item);
					}
				}
			}
			if (!grupoComNaturezaDespesa.isEmpty()) {
				listaTitulo.addAll(gerarTitulos(titulo, naturezaDespesa, grupoComNaturezaDespesa));
				listaSolicitacao.removeAll(grupoComNaturezaDespesa);
			}
			if (!grupoSemNaturezaDespesa.isEmpty()) {
				listaSolicitacao.removeAll(grupoSemNaturezaDespesa);
			}
		}
		return listaTitulo;
	}

	/**
	 * Gerar Titulos para as Solicitações selecionadas.
	 * 
	 * @param titulo
	 * @param naturezaDespesa
	 * @param listaSolicitacao
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	private List<FcpTitulo> gerarTitulos(TituloSemLicitacaoVO titulo, FsoNaturezaDespesa naturezaDespesa, List<SolicitacaoTituloVO> listaSolicitacao) 
			throws ApplicationBusinessException {
		
		List<FcpTitulo> listaTitulo = new ArrayList<FcpTitulo>();
		FcpTipoTitulo tipoTitulo = fcpTipoTituloDAO.obterPorChavePrimaria(parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_TIPO_TSL_NORMAL));
		RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		if (titulo.getQtdeParcelas() == 1) {
			listaTitulo.add(gerarTitulosSemLicitacaoUnicaParcela(titulo, tipoTitulo, naturezaDespesa, servidor, listaSolicitacao));
		} else {
			Integer diasParcela = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_PARCELAS_TTL_AVULSOS);
			for (short i = 1; i <= titulo.getQtdeParcelas(); i++) {
				listaTitulo.add(gerarTitulosSemLicitacaoMultiplasParcelas(titulo, tipoTitulo, naturezaDespesa, servidor, i, diasParcela, listaSolicitacao));
			}
		}
		return listaTitulo;
	}

	/**
	 * Gera os titulos e os respectivos detalhamentos com mais de uma parcela. 
	 * 
	 * @param titulo
	 * @param tipoTitulo
	 * @param naturezaDespesa
	 * @param servidor
	 * @param nroParcela
	 * @param dias
	 * @param listaSolicitacao
	 */
	private FcpTitulo gerarTitulosSemLicitacaoMultiplasParcelas(TituloSemLicitacaoVO titulo, 
			FcpTipoTitulo tipoTitulo, FsoNaturezaDespesa naturezaDespesa, RapServidores servidor, 
			short nroParcela, Integer diasParcela, List<SolicitacaoTituloVO> listaSolicitacao) {
		
		BigDecimal valorTitulo = BigDecimal.ZERO;
		List<FcpTituloSolicitacoes> listaTituloSolicitacoes = new ArrayList<FcpTituloSolicitacoes>();
		for (SolicitacaoTituloVO solicitacao : listaSolicitacao) {
			BigDecimal parcela = solicitacao.getValor().divide(new BigDecimal(titulo.getQtdeParcelas()), 2, RoundingMode.FLOOR);
			FcpTituloSolicitacoes tituloSolicitacao = null;
			if (nroParcela != titulo.getQtdeParcelas()) {
				valorTitulo = valorTitulo.add(parcela);
				tituloSolicitacao = obterTituloSolicitacao(solicitacao, parcela);
			} else {
				BigDecimal ultimaParcela = parcela.add(solicitacao.getValor().subtract(parcela.multiply(new BigDecimal(titulo.getQtdeParcelas()))));
				valorTitulo = valorTitulo.add(ultimaParcela);
				tituloSolicitacao = obterTituloSolicitacao(solicitacao, ultimaParcela);
			}
			listaTituloSolicitacoes.add(tituloSolicitacao);
		}
		FcpTitulo fcpTitulo = obterTitulo(titulo, tipoTitulo, naturezaDespesa, servidor, nroParcela, valorTitulo, diasParcela);
		cadastrarTituloSemLicitacao(fcpTitulo);
		cadastrarDetalhamentoTituloSolicitacoes(listaTituloSolicitacoes, fcpTitulo);
		return fcpTitulo;
	}

	/**
	 * Gera os titulos e os respectivos detalhamentos para uma unica parcela selecionada.
	 * 
	 * @param titulo
	 * @param tipoTitulo
	 * @param naturezaDespesa
	 * @param servidor
	 * @param listaSolicitacao
	 * @return 
	 */
	private FcpTitulo gerarTitulosSemLicitacaoUnicaParcela(TituloSemLicitacaoVO titulo, 
			FcpTipoTitulo tipoTitulo, FsoNaturezaDespesa naturezaDespesa, RapServidores servidor, 
			List<SolicitacaoTituloVO> listaSolicitacao) {
		
		BigDecimal valorTitulo = BigDecimal.ZERO;
		List<FcpTituloSolicitacoes> listaTituloSolicitacoes = new ArrayList<FcpTituloSolicitacoes>();
		for (SolicitacaoTituloVO solicitacao : listaSolicitacao) {
			valorTitulo = valorTitulo.add(solicitacao.getValor());
			FcpTituloSolicitacoes tituloSolicitacao = obterTituloSolicitacao(solicitacao, solicitacao.getValor());
			listaTituloSolicitacoes.add(tituloSolicitacao);
		}
		FcpTitulo fcpTitulo = obterTitulo(titulo, tipoTitulo, naturezaDespesa, servidor, titulo.getQtdeParcelas(), valorTitulo, null);
		cadastrarTituloSemLicitacao(fcpTitulo);
		cadastrarDetalhamentoTituloSolicitacoes(listaTituloSolicitacoes, fcpTitulo);
		return fcpTitulo;
	}
	
	/**
	 * Retorna um {@link FcpTituloSolicitacoes} preenchido com os campos necessarios para a persistencia.
	 * 
	 * @param solicitacao
	 * @param valor
	 * @return
	 */
	private FcpTituloSolicitacoes obterTituloSolicitacao(SolicitacaoTituloVO solicitacao, BigDecimal valor) {
		
		FcpTituloSolicitacoes fcpTituloSolicitacao = new FcpTituloSolicitacoes();
		if (solicitacao.getTipo().equals("SS")) {
			fcpTituloSolicitacao.setSlsNumero(solicitacao.getSolicitacao());
		} else if (solicitacao.getTipo().equals("SC")) {
			fcpTituloSolicitacao.setSlcNumero(solicitacao.getSolicitacao());
		} 
		fcpTituloSolicitacao.setValor(valor.doubleValue());
		return fcpTituloSolicitacao;
	}

	/**
	 * Retorna um {@link FcpTitulo} preenchido com os campos necessarios para a persistencia.
	 * 
	 * @param titulo
	 * @param tipoTitulo
	 * @param naturezaDespesa
	 * @param servidor
	 * @param nroParcela
	 * @param valorTitulo
	 * @param diasParcela
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private FcpTitulo obterTitulo(TituloSemLicitacaoVO titulo, FcpTipoTitulo tipoTitulo, FsoNaturezaDespesa naturezaDespesa, 
			RapServidores servidor, Short nroParcela, BigDecimal valorTitulo, Integer diasParcela) {
		
		FcpTitulo fcpTitulo = new FcpTitulo();
		fcpTitulo.setNroParcela(nroParcela);
		fcpTitulo.setDtVencimento(calcularDataVencimento(titulo, nroParcela, diasParcela));
		fcpTitulo.setValor(valorTitulo.doubleValue());
		fcpTitulo.setNaturezaDespesa(naturezaDespesa);
		fcpTitulo.setFornecedor(titulo.getCredor());
		fcpTitulo.setClassificacaoTitulo(titulo.getClassificacao());
		fcpTitulo.setModalidadeEmpenho(titulo.getModalidadeEmpenho());
		fcpTitulo.setTipoTitulo(tipoTitulo);
		fcpTitulo.setIndSituacao(DominioSituacaoTitulo.APG);
		fcpTitulo.setDtCompetencia(DateUtil.obterDataInicioCompetencia(new Date()));
		fcpTitulo.setDtGeracao(new Date());
		fcpTitulo.setServidor(servidor);
		fcpTitulo.setIndEstorno(Boolean.FALSE);
		fcpTitulo.setIndDocumentacao(DominioSimNao.N.toString());
		return fcpTitulo;
	}

	private Date calcularDataVencimento(TituloSemLicitacaoVO titulo, Short nroParcela, Integer diasParcela) {
		if (diasParcela == null) {
			return titulo.getDataVencimento();
		} else {
			Integer amount = ((nroParcela - 1) * diasParcela);
			return DateUtils.addDays(titulo.getDataVencimento(), amount);
		}
	}
	
	private void cadastrarTituloSemLicitacao(FcpTitulo titulo) {
		if (titulo.getValor() > 0) {
			tituloDAO.persistir(titulo);
		}
	}
	
	private void cadastrarDetalhamentoTituloSolicitacoes(List<FcpTituloSolicitacoes> listaTituloSolicitacoes, FcpTitulo fcpTitulo) {
		if (fcpTitulo.getSeq() != null) {
			for (FcpTituloSolicitacoes tituloSolicitacao : listaTituloSolicitacoes) {
				tituloSolicitacao.setTtlSeq(fcpTitulo.getSeq());
				tituloSolicitacoesDAO.persistir(tituloSolicitacao);
			}
		}
	}
}
