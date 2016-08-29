package br.gov.mec.aghu.compras.autfornecimento.business;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.dominio.DominioVizAutForn;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class ProgrEntregaItensAfON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8245727329425632633L;
	
	private static final Log LOG = LogFactory.getLog(ProgrEntregaItensAfON.class);
	
	@EJB
	public IParametroFacade parametroFacade;
	@EJB
	public IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected void setScoProgEntregaItemAutorizacaoFornecimentoDAO(ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO) {
		this.scoProgEntregaItemAutorizacaoFornecimentoDAO = scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	public enum ProgrEntregaItensAfONExceptionCode implements BusinessExceptionCode { 
		PROG_ENTREG_PERIODO_DATAS_INVALIDO, PROG_ENTREG_DT_PREVISAO_ENTREGA_OBRIGATORIA, PROG_ENTREG_DEVE_SER_REPROGRAMADA ;  
	}
	
	public List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarProgrEntregaItensAf(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		this.validarDatas(filtro);
		
		if(filtro.getVisualizarAutForn() == null) {
			return pesquisaPadrao(firstResult, maxResults, orderProperty, asc);
		}
		else if(DominioVizAutForn.N.equals(filtro.getVisualizarAutForn())){
			return pesquisarNaoProgramadas(filtro, firstResult, maxResults, orderProperty, asc);
		}
		else if(DominioVizAutForn.P.equals(filtro.getVisualizarAutForn())){
			return pesquisarProgramadas(filtro, firstResult, maxResults, orderProperty, asc);
		}
		else if(DominioVizAutForn.T.equals(filtro.getVisualizarAutForn())){
			
			//if(firstResult > 0){
			//	firstResult = firstResult/2;
			//}
			//maxResults = maxResults/2;
			
			List<PesquisarPlanjProgrEntregaItensAfVO> lista = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>();
			lista.addAll(pesquisarNaoProgramadas(filtro, null, null, null, false));
			lista.addAll(pesquisarProgramadas(filtro, null, null, null, false));
			
			//SORT
			if(!lista.isEmpty()) {
				final ComparatorChain ordenacao = new ComparatorChain();
				final BeanComparator ordenarLct = new BeanComparator(PesquisarPlanjProgrEntregaItensAfVO.Fields.NUMERO_LICITACAO.toString(), new NullComparator(true));
				final BeanComparator ordenarCompl = new BeanComparator(PesquisarPlanjProgrEntregaItensAfVO.Fields.COMPLEMENTO.toString(), new NullComparator(true));
				ordenacao.addComparator(ordenarLct);
				ordenacao.addComparator(ordenarCompl);
				Collections.sort(lista, ordenacao);
			}
			
			//PAGINACAO TODOS
			//List<PesquisarPlanjProgrEntregaItensAfVO> listaPaginada = new ArrayList<PesquisarPlanjProgrEntregaItensAfVO>(0);
			//for (int i = firstResult; i < maxResults; i++) {
			//	listaPaginada.add(lista.get(i));
			//}
			
			//return listaPaginada;
			return lista;
		}
		else if(DominioVizAutForn.E.equals(filtro.getVisualizarAutForn())){
			if(filtro.getDataPrevisaoEntrega() == null) {
				throw new ApplicationBusinessException(ProgrEntregaItensAfONExceptionCode.PROG_ENTREG_DT_PREVISAO_ENTREGA_OBRIGATORIA);	
			}
			return pesquisarComPrevisaoEntrega(filtro, firstResult, maxResults, orderProperty, asc);
		}
		else {
			return null;
		}
	}
	
	public void liberarEntrega(List<PesquisarPlanjProgrEntregaItensAfVO> lista, Date dataPrevisaoEntrega, RapServidores usuarioLogado) throws ApplicationBusinessException {
		List<String> afs = new ArrayList<String>();
		for(PesquisarPlanjProgrEntregaItensAfVO vo : lista) {
			if(Boolean.TRUE.equals(vo.getSelecionado())) {
				if(!this.validarLiberacaoEntrega(vo.getNumeroAF(), dataPrevisaoEntrega, usuarioLogado)) {
					afs.add(vo.getNumeroAF() + "/" + (String.format("%05d", (vo.getComplemento() != null) ? vo.getComplemento() : 0)));
				}
			}
		}
		
		this.flush();
		
		if(!afs.isEmpty()) {
			throw new ApplicationBusinessException(ProgrEntregaItensAfONExceptionCode.PROG_ENTREG_DEVE_SER_REPROGRAMADA, StringUtils.join(afs, ", "));	
		}
	}
	
	private Boolean validarLiberacaoEntrega(Integer numeroAF, Date dataPrevisaoEntrega, RapServidores usuarioLogado) throws ApplicationBusinessException {
		List<Integer> fatoresConversao = getScoItemAutorizacaoFornDAO().obterFatorConversao(numeroAF, dataPrevisaoEntrega);
		if(fatoresConversao == null || fatoresConversao.isEmpty()) {
			return false;
		}
		
		for(Integer fatorConversao : fatoresConversao) {
			if(fatorConversao == null || !fatorConversao.equals(0)) {
				return false;
			}
		}
		
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().liberarEntregas(numeroAF, dataPrevisaoEntrega, usuarioLogado);
		
		return true;
	}
	
	public Date obterDataLiberacaoEntrega(Date dataPrevisaoEntrega) throws ApplicationBusinessException {
		if(dataPrevisaoEntrega == null) {
			Integer diaMesCorteProgEntregas = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIA_MES_CORTE_PROGRAMACAO_ENTREGAS);
			Integer diaLiberacao = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS);
			Integer diaDoMes = Integer.valueOf(DateUtil.dataToString(new Date(), "dd"));
			Integer mesAtual = Integer.valueOf(DateUtil.dataToString(new Date(), "MM"));
			Integer anoAtual = Integer.valueOf(DateUtil.dataToString(new Date(), "yyyy"));
			Date dataLiberacao = null;

			if(diaDoMes <= diaMesCorteProgEntregas) {
				Integer qtdMeses = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_N_MESES_MENOR_PROGRAMACAO_ENTREGAS);
				dataLiberacao = DateUtil.obterData(anoAtual, mesAtual-1, diaLiberacao);
				dataLiberacao = DateUtil.adicionaMeses(dataLiberacao, qtdMeses);
			}
			else {
				Integer qtdMeses = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_N_MESES_MAIOR_PROGRAMACAO_ENTREGAS);
				dataLiberacao = DateUtil.obterData(anoAtual, mesAtual-1, diaLiberacao);
				dataLiberacao = DateUtil.adicionaMeses(dataLiberacao, qtdMeses);
			}
			return dataLiberacao;
		}
		
		return dataPrevisaoEntrega;
	}
	
	private List<PesquisarPlanjProgrEntregaItensAfVO> pesquisaPadrao(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		Integer diaComMes = Integer.valueOf(DateUtil.dataToString(new Date(), "MMyyyy"));
		Integer diaDoMes = Integer.valueOf(DateUtil.dataToString(new Date(), "dd"));
		Integer diaInicialProgEntrgFimAno = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIA_INIC_PROG_ENTG_FIM_ANO);
		Integer diaFinalProgEntrgFimAno = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIA_FINAL_PROG_ENTG_FIM_ANO);
		
		Integer diaMais;
		
		
		if(diaComMes >= diaInicialProgEntrgFimAno && diaComMes <= diaFinalProgEntrgFimAno) {
			diaMais = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_FINAL_ANO);
		}
		else {
			diaMais = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_PADRAO);
		}
		
		List<PesquisarPlanjProgrEntregaItensAfVO> listaResultados = getScoAutorizacaoFornDAO().listarProgrEntregaItensAfPadrao(diaMais, diaDoMes, firstResult, maxResults, orderProperty, asc); 
		processaVO(listaResultados);
		
		return listaResultados;
	}

	private List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarNaoProgramadas(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		
		List<PesquisarPlanjProgrEntregaItensAfVO> listaResultados = getScoAutorizacaoFornDAO().listarProgrEntregaItensAfNaoProgramadas(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO), firstResult, maxResults, orderProperty, asc); 
		processaVO(listaResultados);
		
		return listaResultados;
	}

	private List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarProgramadas(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		
		List<PesquisarPlanjProgrEntregaItensAfVO> listaResultados = getScoAutorizacaoFornDAO().listarProgrEntregaItensAfProgramadas(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO), firstResult, maxResults, orderProperty, asc); 
		processaVO(listaResultados);
		
		return listaResultados;
	}

	private List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarComPrevisaoEntrega(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		
		List<PesquisarPlanjProgrEntregaItensAfVO> listaResultados = getScoAutorizacaoFornDAO().listarProgrEntregaItensAfPrevEntrega(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO), firstResult, maxResults, orderProperty, asc); 
		processaVO(listaResultados);
		
		return listaResultados;
	}

	private void validarDatas(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException  {
		if(filtro.getDataInicioPrevisaoEntrega() != null && filtro.getDataFimPrevisaoEntrega() != null) {
			if(!DateUtil.validaDataTruncadaMaiorIgual(filtro.getDataFimPrevisaoEntrega(), filtro.getDataInicioPrevisaoEntrega())) {
				throw new ApplicationBusinessException(ProgrEntregaItensAfONExceptionCode.PROG_ENTREG_PERIODO_DATAS_INVALIDO);
			}
		}
		if(filtro.getDataInicioVencimentoContrato() != null && filtro.getDataFimVencimentoContrato() != null) {
			if(!DateUtil.validaDataTruncadaMaiorIgual(filtro.getDataFimVencimentoContrato(), filtro.getDataInicioVencimentoContrato())) {
				throw new ApplicationBusinessException(ProgrEntregaItensAfONExceptionCode.PROG_ENTREG_PERIODO_DATAS_INVALIDO);
			}
		}
	}
	
	private void processaVO(List<PesquisarPlanjProgrEntregaItensAfVO> listaResultados) throws ApplicationBusinessException {
		Integer fornecedorPadrao = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		for(PesquisarPlanjProgrEntregaItensAfVO vo : listaResultados) {
			/*
			 * RN 1
			 */
			//C2
			Long qtdItensProgramados = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeItensProgramados(vo.getNumeroAF());
			if(qtdItensProgramados > 0) {
				//C3
				Long qtdItensNaoEfetivados = getScoItemAutorizacaoFornDAO().obterQuantidadeItensNaoEfetivados(vo.getNumeroAF());
				if(!qtdItensProgramados.equals(qtdItensNaoEfetivados)) {
					vo.setProgram("P");
					vo.setHintProgram(getResourceBundleValue("HINT_M9"));
					vo.setColoracaoProgram("#FF0");
				}
				else if(qtdItensProgramados.equals(qtdItensNaoEfetivados)) {
					vo.setProgram("T");
					vo.setHintProgram(getResourceBundleValue("HINT_M10"));
					vo.setColoracaoProgram("blue");
				}
			}
			
			/*
			 * RN 2
			 */
			//C4
			Long qtdItensAutomatcos = getScoItemAutorizacaoFornDAO().obterQuantidadeItensAutomaticos(vo.getNumeroAF());
			if(qtdItensAutomatcos > 0) {
				vo.setAutom("S");
				vo.setColoracaoAutom("#FF0");
			}

			/*
			 * RN 3
			 */
			//C5
			Long qtdItensParaProgramacao = getScoItemAutorizacaoFornDAO().obterQuantidadeItensParaProgramacao(vo.getNumeroAF(), fornecedorPadrao);
			if(qtdItensParaProgramacao > 0){
				vo.setGera("S");
				vo.setColoracaoGera("#00FF03");
			}
			
			/*
			 * RN 4
			 */
			//C11
			Long qtdEntregueNaoConfirmada = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeEntregueNaoConfirmada(vo.getNumeroAF());
			if(qtdEntregueNaoConfirmada > 0) {
				vo.setSitEntrega("E");
				vo.setHintSitEntrega(getResourceBundleValue("HINT_SIT_ENTREGA_E"));
				vo.setColoracaoSitEntrega("#FF0");
			}
			else {
				//C10
				Long qtdEntregasVencidas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeEntregasVencidas(vo.getNumeroAF());
				if(qtdEntregasVencidas > 0) {
					vo.setSitEntrega("V");
					vo.setHintSitEntrega(getResourceBundleValue("HINT_SIT_ENTREGA_L"));
					vo.setColoracaoSitEntrega("#F00");
				}
				else {
					//C9
					Long qtdRecalculada = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeRecalulada(vo.getNumeroAF(), this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_PADRAO));
					if(qtdRecalculada > 0) {
						vo.setSitEntrega("R");
						vo.setHintSitEntrega(getResourceBundleValue("HINT_M2"));
						vo.setColoracaoSitEntrega("blue");
					}
					else {
						//C8
						Long qtdEntregasMenor = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeEntregasMenor(vo.getNumeroAF());
						if(qtdEntregasMenor > 0) {
							vo.setSitEntrega("P");
							vo.setHintSitEntrega(getResourceBundleValue("HINT_SIT_ENTREGA_R"));
							vo.setColoracaoSitEntrega("#00F");
						}
						else {
							//C7
							Long qtdEntregasLiberadasAssinatura = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeEntregasLiberadasAssinatura(vo.getNumeroAF(), this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_PADRAO));
							if(qtdEntregasLiberadasAssinatura > 0) {
								vo.setSitEntrega("E");
								vo.setHintSitEntrega(getResourceBundleValue("HINT_SIT_ENTREGA_E_VERDE"));
								vo.setColoracaoSitEntrega("#00FF03");
							}
							else {
								//C6
								Long qtdEntregasPendentesEmpenhadas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterQuantidadeEntregasPendentesEmpenhadas(vo.getNumeroAF());
								if(qtdEntregasPendentesEmpenhadas > 0) {
									vo.setSitEntrega("L");
									vo.setHintSitEntrega(getResourceBundleValue("HINT_SIT_ENTREGA_V"));
									vo.setColoracaoSitEntrega("#00FF03");
								}
							}
						}
					}
				}
			}
			/*
			 * RN 6
			 */
			if(getEstoqueFacade().isItemAFCurvaA(vo.getNumeroAF(), fornecedorPadrao)) {
				vo.setColoracaoNumeroAF("#00F");
			}
		}
	}
	

	public Long pesquisarProgrEntregaItensAfCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException {
		this.validarDatas(filtro);
		
		if(filtro.getVisualizarAutForn() == null) {
			return pesquisaPadraoCount();
		}
		else if(DominioVizAutForn.N.equals(filtro.getVisualizarAutForn())){
			return pesquisarNaoProgramadasCount(filtro);
		}
		else if(DominioVizAutForn.P.equals(filtro.getVisualizarAutForn())){
			return pesquisarProgramadasCount(filtro);
		}
		else if(DominioVizAutForn.T.equals(filtro.getVisualizarAutForn())){
			Long count = pesquisarNaoProgramadasCount(filtro) + pesquisarProgramadasCount(filtro);
			return count;
		}
		else if(DominioVizAutForn.E.equals(filtro.getVisualizarAutForn())){
			if(filtro.getDataPrevisaoEntrega() == null) {
				throw new ApplicationBusinessException(ProgrEntregaItensAfONExceptionCode.PROG_ENTREG_DT_PREVISAO_ENTREGA_OBRIGATORIA);	
			}
			return pesquisarComPrevisaoEntregaCount(filtro);
		}
		else {
			return null;
		}
	}

	private Long pesquisarComPrevisaoEntregaCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException {
		return getScoAutorizacaoFornDAO().listarProgrEntregaItensAfPrevEntregaCount(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)); 
	}

	private Long pesquisarProgramadasCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException {
		return getScoAutorizacaoFornDAO().listarProgrEntregaItensAfProgramadasCount(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)); 
	}

	private Long pesquisarNaoProgramadasCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException {
		return getScoAutorizacaoFornDAO().listarProgrEntregaItensAfNaoProgramadasCount(filtro, this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)); 
	}

	private Long pesquisaPadraoCount() throws ApplicationBusinessException {
		Integer diaComMes = Integer.valueOf(DateUtil.dataToString(new Date(), "MMyyyy"));
		Integer diaDoMes = Integer.valueOf(DateUtil.dataToString(new Date(), "dd"));
		Integer diaInicialProgEntrgFimAno = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIA_INIC_PROG_ENTG_FIM_ANO);
		Integer diaFinalProgEntrgFimAno = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIA_FINAL_PROG_ENTG_FIM_ANO);
		
		Integer diaMais;
		
		
		if(diaComMes >= diaInicialProgEntrgFimAno && diaComMes <= diaFinalProgEntrgFimAno) {
			diaMais = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_FINAL_ANO);
		}
		else {
			diaMais = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PROG_ENTG_PADRAO);
		}
		
		return getScoAutorizacaoFornDAO().listarProgrEntregaItensAfPadraoCount(diaMais, diaDoMes);
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected void setScoAutorizacaoFornDAO(ScoAutorizacaoFornDAO scoAutorizacaoFornDAO) {
		this.scoAutorizacaoFornDAO = scoAutorizacaoFornDAO;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected void setScoItemAutorizacaoFornDAO(ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO) {
		this.scoItemAutorizacaoFornDAO = scoItemAutorizacaoFornDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	protected void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	
}
