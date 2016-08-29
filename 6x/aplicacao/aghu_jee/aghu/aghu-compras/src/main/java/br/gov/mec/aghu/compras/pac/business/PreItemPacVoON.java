package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe com métodos para pré-selecionar itens ao pac.
 * 
 * @modulo compras
 * @author sgralha
 * @since 26/03/2013
 */
@Stateless
public class PreItemPacVoON extends BaseBusiness {

    private static final String ASSOCIADA_A = " (associada à ";

    private static final Log LOG = LogFactory.getLog(PreItemPacVoON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;

    @Inject
    private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

    @Inject
    private ScoLicitacaoDAO scoLicitacaoDAO;

    @EJB
    private IPacFacade pacFacade;

    @Inject
    private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

    @EJB
    private IComprasFacade comprasFacade;

    @Inject
    private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

    @Inject
    private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

    @EJB
    private ISolicitacaoServicoFacade solicitacaoServicoFacade;

    @Inject
    private ScoSsJnDAO scoSsJnDAO;

    @Inject
    private ScoScJnDAO scoScJnDAO;

    @EJB
    private ISolicitacaoComprasFacade solicitacaoComprasFacade;

    @Inject
    private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

    @EJB
    private IParametroFacade parametroFacade;

    @Inject
    private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

    /**
	 * 
	 */
    private static final long serialVersionUID = -893064524258218860L;

    public enum PreItemPacVoONExceptionCode implements BusinessExceptionCode {
	MENSAGEM_SOLICITACAO_NAO_ENCONTRADA, MENSAGEM_SOLICITACAO_NAO_ENCONTRADA_INTERVALO, MENSAGEM_VLR_UNITARIO_INFERIOR, MENSAGEM_ITEM_JA_ASSOCIADO, MSG_MAX_INTERVALO, MENSAGEM_ASSOCIAR_SC_SS_PAC_SC04, MENSAGEM_ASSOCIAR_SC_SS_PAC_SC05,
	CONSULTA_NAO_RETORNA_REGISTRO,MENSAGEM_VALIDA_VLR_ORCAMENTO;
    }

    public List<PreItemPacVO> preSelecionarItensPac(ScoPontoParadaSolicitacao caixa, RapServidores comprador, ScoServico servico,
	    ScoMaterial material) throws ApplicationBusinessException {

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();

	if (material != null) {
	    listaItensPac.addAll(this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(caixa, comprador, material, true, true,
		    true, true, null, null));
	    if(listaItensPac == null || listaItensPac.isEmpty()){
	    	throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.CONSULTA_NAO_RETORNA_REGISTRO);
	    }

	} else if (servico != null) {
	    listaItensPac.addAll(this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(caixa, comprador, servico, true, true, true,
		    true, null, null));
	    if(listaItensPac == null || listaItensPac.isEmpty()){
	    	throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.CONSULTA_NAO_RETORNA_REGISTRO);
	    }
	}

	else {
	    listaItensPac.addAll(this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(caixa, comprador, null, true, true, true,
		    true, null, null));
	    listaItensPac.addAll(this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(caixa, comprador, null, true, true, true,
		    true, null, null));
	}
	return listaItensPac;

    }

    public List<PreItemPacVO> adicionarItensPacPorNumero(DominioTipoSolicitacao tipoSolicitacao, Integer numeroIni, Integer numeroFim)
	    throws ApplicationBusinessException {
	List<PreItemPacVO> itens = new ArrayList<PreItemPacVO>();

	AghParametros parametroMaxIntervalo = this.getParametroFacade().buscarAghParametro(
		AghuParametrosEnum.P_MAX_INTERVALO_SOLICITACOES_PAC);

	if (numeroIni != null && numeroFim != null) {
	    if (numeroFim.intValue() - numeroIni.intValue() > parametroMaxIntervalo.getVlrNumerico().intValue()) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MSG_MAX_INTERVALO, parametroMaxIntervalo
			.getVlrNumerico().intValue());
	    }
	}
	
	if (numeroIni == null && numeroFim == null) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_ASSOCIAR_SC_SS_PAC_SC04);
	   
	}
	
	/*if (numeroIni == null && numeroFim != null) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_ASSOCIAR_SC_SS_PAC_SC05);
	   
	}*/

	if (tipoSolicitacao.equals(DominioTipoSolicitacao.SS)) {
	    itens.addAll(this.adicionarItensPacPorNumeroSS(numeroIni, numeroFim));
	}

	if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
	    itens.addAll(this.adicionarItensPacPorNumeroSC(numeroIni, numeroFim));
	}

	if (itens.size() > 1) {
	    return this.removerDuplicados(itens);
	}

	return itens;

    }

    private List<PreItemPacVO> removerDuplicados(List<PreItemPacVO> itens) {
	List<PreItemPacVO> listaRetorno = new ArrayList<PreItemPacVO>();

	for (PreItemPacVO item : itens) {
	    if (!listaRetorno.contains(item)) {
		listaRetorno.add(item);
	    }
	}

	return listaRetorno;
    }

    protected List<PreItemPacVO> adicionarItensPacPorNumeroSS(Integer numeroSSIni, Integer numeroSSFim) throws ApplicationBusinessException {

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();
	List<PreItemPacVO> listaItensPacSS = new ArrayList<PreItemPacVO>();
	
	StringBuffer solAssociadas = new StringBuffer(100);
	StringBuffer solNaoAutozadas = new StringBuffer(100);
	StringBuffer solExcluidas = new StringBuffer(100);
	StringBuffer solGeraTitAvulso = new StringBuffer(100);

	// Parametros da chamada da DAO
	// ScoPontoParadaSolicitacao caixa, ScoServico servico,
	// boolean verificaFases, boolean excluido, boolean autorizada, boolean
	// verficaPP, Integer numeroSSIni, Integer numeroSSFim
	listaItensPacSS = this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(null, null, null, false, false, false, false,
		numeroSSIni, numeroSSFim);
	
	// verificar se não achou nenhuma solicitação
	if (listaItensPacSS.size() < 1) {
	    if (numeroSSFim == null) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_SOLICITACAO_NAO_ENCONTRADA, numeroSSIni);
	    } else {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_SOLICITACAO_NAO_ENCONTRADA_INTERVALO,
			numeroSSIni, numeroSSFim);
	    }
	}
	// Adicionar as regras #24795 - RN1, RN2, RN3, RN8, RN9, RN11
	if (listaItensPacSS != null && listaItensPacSS.size() > 0) {
	    listaItensPac.addAll(this.verificarlistaItensPacSS(listaItensPacSS, solAssociadas, solNaoAutozadas, solExcluidas, solGeraTitAvulso, numeroSSIni, numeroSSFim));
	}
	

	// Tratar mensagens de sol. Compras e sol. Servico
	this.tratarMensagensPesquisa(solAssociadas, solNaoAutozadas, solExcluidas, listaItensPac, solGeraTitAvulso);

	return listaItensPac;
    }

    private List<PreItemPacVO> adicionarItensPacPorNumeroSC(Integer numeroSCIni, Integer numeroSCFim) throws ApplicationBusinessException {

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();
	List<PreItemPacVO> listaItensPacSS = new ArrayList<PreItemPacVO>();
	List<PreItemPacVO> listaItensPacSC = new ArrayList<PreItemPacVO>();

	StringBuffer solAssociadas = new StringBuffer(100);
	StringBuffer solNaoAutozadas = new StringBuffer(100);
	StringBuffer solExcluidas = new StringBuffer(100);

	// Parametros da chamada da DAO
	// ScoPontoParadaSolicitacao caixa, ScoServico servico,
	// boolean verificaFases, boolean excluido, boolean autorizada, boolean
	// verficaPP, Integer numeroSSIni, Integer numeroSSFim
	listaItensPacSC.addAll(this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(null, null, null, false, false, false,
		false, numeroSCIni, numeroSCFim));
	//FLAVIO listaItensPacSS.addAll(this.getScoSolicitacaoCompraServicoDAO().listaAssociadasSCItensPac(numeroSCIni, numeroSCFim));

	// verificar se não achou nenhuma solicitação
	if (listaItensPacSC.size() < 1) {
	    if (numeroSCFim == null) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_SOLICITACAO_NAO_ENCONTRADA, numeroSCIni);
	    } else {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_SOLICITACAO_NAO_ENCONTRADA_INTERVALO,
			numeroSCIni, numeroSCFim);
	    }
	}
	// Adicionar as regras #24795 - RN1, RN2, RN3, RN8, RN9
	if (listaItensPacSS != null && listaItensPacSS.size() > 0) {
	    listaItensPac.addAll(this.verificarlistaItensPacSS(listaItensPacSS, solAssociadas, solNaoAutozadas, solExcluidas, null, null, null));
	}
	// Adicionar as regras #24795 - RN1, RN2, RN3, RN8, RN9
	if (listaItensPacSC != null && listaItensPacSC.size() > 0) {
	    listaItensPac.addAll(this.verificarlistaItensPacSC(listaItensPacSC, solAssociadas, solNaoAutozadas, solExcluidas));
	}

	// Tratar mensagens de sol. Compras e sol. Servico
	this.tratarMensagensPesquisa(solAssociadas, solNaoAutozadas, solExcluidas, listaItensPac, null);

	return listaItensPac;

    }

    public void verificarTituloAvulsoMensagem(List<PreItemPacVO> listaItensPacSS, StringBuffer solGeraTitAvulso, Integer numeroSCIni, Integer numeroSCFim){
    	//RN11
    	if(numeroSCIni == null || numeroSCFim == null) {
    		return;
    	}
    	for (int ini = numeroSCIni; ini <= numeroSCFim; ini++) {
    		boolean find = false;
    		for (PreItemPacVO preItemPacVO : listaItensPacSS) {
    			if (ini == preItemPacVO.getNumero()) {
    				find = true;
    			}
    		}

    		if (!find) {
    			if (solGeraTitAvulso.length() > 0) {
    				solGeraTitAvulso.append(", ").append(ini);
    			} else {
    				solGeraTitAvulso.append(ini);
    			}
    		}
    	}
    }
    
    public List<PreItemPacVO> verificarlistaItensPacSS(List<PreItemPacVO> listaItensPacSS, StringBuffer solAssociadas,
	    StringBuffer solNaoAutozadas, StringBuffer solExcluidas, StringBuffer solGeraTitAvulso, Integer numeroSCIni, Integer numeroSCFim) {

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();
	int numAnterior = 0;

	verificarTituloAvulsoMensagem(listaItensPacSS, solGeraTitAvulso, numeroSCIni, numeroSCFim);
	
	for (PreItemPacVO item : listaItensPacSS) {

	    String retornoAss = null;
	    String retornoNAuto = null;
	    String retornoExclusao = null;
	    boolean removerItem = false;

	    // RN1 - verificar se a SS está em algum processo
	    retornoAss = this.verificaSSAssociadaPac(item);
	    if (retornoAss == null) {
		removerItem = true;
		if (solAssociadas.length() > 0) {
		    solAssociadas.append(" ,");
		}
		if (item.getSolAssociada() == null) {
		    solAssociadas.append(" " + item.getNumero() + " ");
		} else {
		    solAssociadas.append(item.getNumero().toString() + ASSOCIADA_A + item.getSolAssociada() + ")");
		}
	    }
	    // RN2 - solicitações não autorizadas
	    retornoNAuto = this.verificaSSDtAutorizacao(item);
	    if (retornoNAuto == null) {
		removerItem = true;
		if (solNaoAutozadas.length() > 0) {
		    solNaoAutozadas.append(" ,");
		}
		if (item.getNumero().compareTo(numAnterior) > 0 || item.getNumero().compareTo(numAnterior) < 0) {
		    numAnterior = item.getNumero();
		    if (item.getSolAssociada() == null) {
			solNaoAutozadas.append(", " + item.getNumero() + " ");
		    } else {
			solNaoAutozadas.append(", " + item.getNumero() + ASSOCIADA_A + item.getSolAssociada() + ")");
		    }
		}
	    }
	    // RN9 - solicitações excluídas
	    retornoExclusao = this.verificaSSExcluida(item);
	    if (retornoExclusao == null) {
		removerItem = true;
		if (solExcluidas.length() > 0) {
		    solExcluidas.append(" ,");
		}
		if (item.getSolAssociada() == null) {
		    solExcluidas.append(", " + item.getNumero() + " ");
		} else {
		    solExcluidas.append(", " + item.getNumero() + ASSOCIADA_A + item.getSolAssociada() + ")");
		}
	    }
	    if (!removerItem) {
		listaItensPac.add(item);
	    }
	}
	return listaItensPac;
    }

    public List<PreItemPacVO> verificarlistaItensPacSC(List<PreItemPacVO> listaItensPacSC, StringBuffer solAssociadas,
	    StringBuffer solNaoAutozadas, StringBuffer solExcluidas) {

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();
	int numAnterior = 0;

	for (PreItemPacVO item : listaItensPacSC) {

	    String retornoAss = null;
	    String retornoNAuto = null;
	    String retornoExclusao = null;
	    boolean removerItem = false;

	    // RN1 - verificar se a SS ou SC está em algum processo
	    retornoAss = this.verificaSCAssociadaPac(item);
	    if (retornoAss == null) {
		removerItem = true;
		if (solAssociadas.length() > 0) {
		    solAssociadas.append(" ,");
		}
		if (item.getSolAssociada() == null) {
		    solAssociadas.append(" " + item.getNumero() + " ");
		} else {
		    solAssociadas.append(item.getNumero().toString() + ASSOCIADA_A + item.getSolAssociada() + ")");
		}

	    }
	    // RN2 - solicitações não autorizadas
	    retornoNAuto = this.verificaSCDtAutorizacao(item);
	    if (retornoNAuto == null) {
		removerItem = true;
		if (solNaoAutozadas.length() > 0) {
		    solNaoAutozadas.append(" ,");
		}
		if (item.getNumero().compareTo(numAnterior) > 0 || item.getNumero().compareTo(numAnterior) < 0) {
		    numAnterior = item.getNumero();
		    if (item.getSolAssociada() == null) {
			solNaoAutozadas.append(" " + item.getNumero() + " ");
		    } else {
			solNaoAutozadas.append(" " + item.getNumero() + ASSOCIADA_A + item.getSolAssociada() + ")");
		    }
		}
	    }
	    // RN9 - solicitações excluídas
	    retornoExclusao = this.verificaSCExcluida(item);
	    if (retornoExclusao == null) {
		removerItem = true;
		if (solExcluidas.length() > 0) {
		    solExcluidas.append(" ,");
		}
		if (item.getSolAssociada() == null) {
		    solExcluidas.append(" " + item.getNumero() + " ");
		} else {
		    solExcluidas.append(" " + item.getNumero() + ASSOCIADA_A + item.getSolAssociada() + ")");
		}
	    }

	    if (!removerItem) {
		listaItensPac.add(item);
	    }
	}
	return listaItensPac;
    }

    public void tratarMensagensPesquisa(StringBuffer solAssociadas, StringBuffer solNaoAutozadas, StringBuffer solExcluidas,
	    List<PreItemPacVO> listaItensPac, StringBuffer solGeraTitAvulso) {

	if (listaItensPac.size() == 0) {
	    PreItemPacVO preItem = new PreItemPacVO();
	    listaItensPac.add(preItem);
	}

	if (solAssociadas != null && solAssociadas.length() > 0) {
	    listaItensPac.get(0).setMsgAssociadas(solAssociadas.toString());
	}

	if (solNaoAutozadas != null && solNaoAutozadas.length() > 0) {
	    listaItensPac.get(0).setMsgNAutorizadas(solNaoAutozadas.toString());
	}
	if (solExcluidas != null && solExcluidas.length() > 0) {
	    listaItensPac.get(0).setMsgExcluidas(solExcluidas.toString());
	}
	if(solGeraTitAvulso != null && !solGeraTitAvulso.toString().trim().isEmpty()){
		listaItensPac.get(0).setMsgTituloAvulso(solGeraTitAvulso.toString());
	}
	
    }

    public String verificaSCAssociadaPac(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(null, null, null, true, false,
		false, false, item.getNumero(), null);

	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public String verificaSCDtAutorizacao(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(null, null, null, false, false,
		true, false, item.getNumero(), null);

	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public String verificaSCExcluida(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacoesDeComprasDAO().listaIdentificacaoItensPac(null, null, null, false, true,
		false, false, item.getNumero(), null);

	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public String verificaSSAssociadaPac(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(null, null, null, true, false,
		false, false, item.getNumero(), null);

	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public String verificaSSDtAutorizacao(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(null, null, null, false, false,
		true, false, item.getNumero(), null);
	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public String verificaSSExcluida(PreItemPacVO item) {

	List<PreItemPacVO> listaItensPac = this.getScoSolicitacaoServicoDAO().listaIdentificacaoItensPac(null, null, null, false, true,
		false, false, item.getNumero(), null);

	if (listaItensPac != null && listaItensPac.size() > 0 && listaItensPac.get(0) != null) {
	    return listaItensPac.get(0).getNumero().toString();
	} else {
	    return null;
	}
    }

    public void gravarItens(ScoLicitacao pac, List<PreItemPacVO> listaItensVOPac) throws BaseException {
	int numeroItem = 0;

	// Verificar se não existe algum item com valor igual a zero
	this.validaValoresItens(listaItensVOPac);	

	AghParametros parametroPpsLicitacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_LICITACAO);
	
	pac = this.pacFacade.obterLicitacao(pac.getNumero());

	for (PreItemPacVO itemVO : listaItensVOPac) {

	    // trazer o número do último Pac apenas para o primeiro item
	    if (listaItensVOPac.get(0).equals(itemVO)) {
		numeroItem = this.getScoItemLicitacaoDAO().proximosItensLicitacao(pac.getNumero());
	    } else {
		numeroItem = numeroItem + 1;
	    }

	    ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
	    this.inserirItemLicitacao(itemLicitacao, numeroItem, pac, itemVO);

	    if (itemVO.getQtdSC() != null) {
		ScoSolicitacaoDeCompra solCompras = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(itemVO.getNumero());
		
		// Atualizar SC
		this.alterarSC(solCompras, parametroPpsLicitacao.getVlrNumerico());
		
		// gravar o item na tabela de fases
		this.inserirFaseSolicitacaoCompras(solCompras, itemLicitacao);
		// Inserir na Journal
		// rcdsouza: o método
		// getSolicitacaoComprasFacade().atualizarScoSolicitacaoDeCompra
		// já insere na journal
		// this.inserirSolicitacaoCompraJournal(solCompras,
		// DominioOperacoesJournal.UPD);

	    } else if (itemVO.getQtdSS() != null) {

		ScoSolicitacaoServico solServico = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(itemVO.getNumero());

		// gravar o item na tabela de fases
		this.inserirFaseSolicitacaoServico(solServico, itemLicitacao);

		// Atualizar SS

		this.alterarSS(solServico, parametroPpsLicitacao.getVlrNumerico());

		// Inserir na Journal
		// rcdsouza: o método
		// getSolicitacaoServicoFacade().atualizarSolicitacaoServico já
		// insere na journal quando necessário
		// this.inserirSolicitacaoServicoJournal(solServico,
		// DominioOperacoesJournal.UPD);
	    }

	}

	// atualizar Licitação
	pac.setSituacao(DominioSituacaoLicitacao.GR);
	this.getScoLicitacaoDAO().persistir(pac);
	this.getScoLicitacaoDAO().flush();

    }

    public void validaValoresItens(List<PreItemPacVO> listaItensVOPac) throws ApplicationBusinessException {

	for (PreItemPacVO itemVO : listaItensVOPac) {
	    if (itemVO.getValorUnitPrevisto() == null || itemVO.getValorUnitPrevisto().doubleValue() <= 0) {
		throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_VLR_UNITARIO_INFERIOR);
	    }
	}

    }
    
	public void validaValoresRegrasOrcamentarias(List<PreItemPacVO> listaItensVOPac) throws ApplicationBusinessException {

    	for (PreItemPacVO itemVO : listaItensVOPac) {
    		 if (itemVO.getQtdSC() != null) {
    				ScoSolicitacaoDeCompra solCompras = getScoSolicitacoesDeComprasDAO().obterOriginal(itemVO.getNumero());

    				if (solCompras.getNaturezaDespesa() == null ||
    					solCompras.getGrupoNaturezaDespesa() == null ||
    					solCompras.getVerbaGestao() == null){
    					throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_VALIDA_VLR_ORCAMENTO, "SC", solCompras.getNumero());
    				}
    		 } else if (itemVO.getQtdSS() != null) {

    				ScoSolicitacaoServico solServico = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(itemVO.getNumero());
    				
    				if (solServico.getNaturezaDespesa() == null ||
    					solServico.getVerbaGestao() == null){
        					throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_VALIDA_VLR_ORCAMENTO, "SS", solServico.getNumero());
        				}
    		 }

    	}
	}

    public void alterarSC(ScoSolicitacaoDeCompra solCompras, BigDecimal parametroPpsLicitacao) throws BaseException {
	solCompras.setPontoParada(solCompras.getPontoParadaProxima());
	solCompras.setPontoParadaProxima(this.getScoPontoParadaSolicitacaoDAO().obterOriginal(parametroPpsLicitacao.shortValue()));
	ScoSolicitacaoDeCompra solicitacaoDeCompraOld = getScoSolicitacoesDeComprasDAO().obterOriginal(solCompras.getNumero());

	this.getSolicitacaoComprasFacade().atualizarScoSolicitacaoDeCompra(solCompras, solicitacaoDeCompraOld);

	// this.getScoSolicitacoesDeComprasDAO().persistir(solCompras);
	// this.getScoSolicitacoesDeComprasDAO().flush();

    }

    public void alterarSS(ScoSolicitacaoServico solServico, BigDecimal parametroPpsLicitacao) throws BaseException {

	solServico.setPontoParadaLocAtual(solServico.getPontoParada());
	solServico.setPontoParada(this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(parametroPpsLicitacao.shortValue()));
	ScoSolicitacaoServico scoSolicitacaoServicoOld = this.getScoSolicitacaoServicoDAO().obterOriginal(solServico.getNumero());
	this.getSolicitacaoServicoFacade().atualizarSolicitacaoServico(solServico, scoSolicitacaoServicoOld);
	// this.getScoSolicitacaoServicoDAO().persistir(solServico);
	// this.getScoSolicitacaoServicoDAO().flush();
    }

    public void inserirItemLicitacao(ScoItemLicitacao itemLicitacao, Integer numeroItem, ScoLicitacao pac, PreItemPacVO itemVO)
	    throws ApplicationBusinessException {
	// antes de inserir verificar se o item já não foi inserido em alguma
	// pac
	List<ScoFaseSolicitacao> listaSC = this.getScoFaseSolicitacaoDAO()
		.pesquisarFaseSolicitacaoPorNumeroSolCompraComItemLict(numeroItem);
	List<ScoFaseSolicitacao> listaSS = this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoPorNumeroSolServicoComItemLict(
		numeroItem);

	if ((listaSC == null || listaSC.size() == 0) && (listaSS == null || listaSS.size() == 0)) {
	    itemLicitacao.setId(new ScoItemLicitacaoId(pac.getNumero(), numeroItem.shortValue()));
	    itemLicitacao.setLicitacao(pac);
	    itemLicitacao.setClassifItem(numeroItem.shortValue());
	    itemLicitacao.setValorUnitario(itemVO.getValorUnitPrevisto());
	    itemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.SJ);
	    itemLicitacao.setExclusao(false);
	    itemLicitacao.setPropostaEscolhida(false);
	    itemLicitacao.setEmAf(false);
	    itemLicitacao.setIndFrequenciaEntrega(pac.getTipoFreqEntrega());
	    itemLicitacao.setFrequenciaEntrega(pac.getFrequenciaEntrega());
	    // gravar o item na tabela Item Licitacao
	    this.getPacFacade().inserir(itemLicitacao);

	    // this.getScoItemLicitacaoDAO().persistir(itemLicitacao);
	    // this.getScoItemLicitacaoDAO().flush();

	} else {
	    throw new ApplicationBusinessException(PreItemPacVoONExceptionCode.MENSAGEM_ITEM_JA_ASSOCIADO, itemVO.getNumero());
	}
    }

    public void inserirFaseSolicitacaoCompras(ScoSolicitacaoDeCompra solCompras, ScoItemLicitacao itemLicitacao) throws BaseException {
	ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();

	faseSolicitacao.setTipo(DominioTipoFaseSolicitacao.C);
	faseSolicitacao.setSolicitacaoDeCompra(solCompras);
	faseSolicitacao.setItemLicitacao(itemLicitacao);
	faseSolicitacao.setExclusao(false);

	this.getComprasFacade().inserirScoFaseSolicitacao(faseSolicitacao);

	//getScoFaseSolicitacaoDAO().persistir(faseSolicitacao);
	//getScoFaseSolicitacaoDAO().flush();
    }

    public void inserirFaseSolicitacaoServico(ScoSolicitacaoServico solServico, ScoItemLicitacao itemLicitacao) throws BaseException {
	ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();

	faseSolicitacao.setTipo(DominioTipoFaseSolicitacao.S);
	faseSolicitacao.setSolicitacaoServico(solServico);
	faseSolicitacao.setItemLicitacao(itemLicitacao);
	faseSolicitacao.setExclusao(false);

	this.getComprasFacade().inserirScoFaseSolicitacao(faseSolicitacao);

	//getScoFaseSolicitacaoDAO().persistir(faseSolicitacao);
	//getScoFaseSolicitacaoDAO().flush();

    }

    public void inserirSolicitacaoCompraJournal(ScoSolicitacaoDeCompra solicitacaoDeCompra, DominioOperacoesJournal operacao)
	    throws ApplicationBusinessException {
	//
	// ScoScJn jn = new ScoScJn();
	// jn.setNumero(solicitacaoDeCompra.getNumero());
	// jn.setDevolucao(solicitacaoDeCompra.getDevolucao());
	// jn.setPontoParadaSolicitacao(solicitacaoDeCompra
	// .getPontoParadaProxima());
	// jn.setOperacao(operacao);
	// jn.setServidor(servidor);
	// jn.setNomeUsuario(servidor.getUsuario());

	this.getSolicitacaoComprasFacade().inserirJournalSC(solicitacaoDeCompra, operacao);
	// getScoScJnDAO().persistir(jn);
	// getScoScJnDAO().flush();

    }

    public void inserirSolicitacaoServicoJournal(ScoSolicitacaoServico solicitacaoServico, DominioOperacoesJournal operacao)
	    throws ApplicationBusinessException {
	RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

	ScoSsJn scoSsJn = new ScoSsJn();
	scoSsJn.setOperacao(operacao);
	scoSsJn.setNomeUsuario(servidorLogado.getUsuario());
	scoSsJn.setNumero(solicitacaoServico.getNumero());
	scoSsJn.setPpsCodigo(solicitacaoServico.getPontoParada().getCodigo());
	scoSsJn.setIndDevolucao(solicitacaoServico.getIndDevolucao());
	scoSsJn.setSerMatriculaAutorizada(servidorLogado.getId().getMatricula());
	scoSsJn.setSerVinCodigoAutorizada(servidorLogado.getId().getVinCodigo());

	this.getSolicitacaoServicoFacade().inserirJournalSS(scoSsJn);
	// this.getScoSsJnDAO().persistir(scoSsJn);
	// this.getScoSsJnDAO().flush();

    }

    protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
	return scoSolicitacoesDeComprasDAO;
    }

    protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
	return scoSolicitacaoServicoDAO;
    }

    protected ScoSolicitacaoCompraServicoDAO getScoSolicitacaoCompraServicoDAO() {
	return scoSolicitacaoCompraServicoDAO;
    }

    protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
	return scoItemLicitacaoDAO;
    }

    protected ScoLicitacaoDAO getScoLicitacaoDAO() {
	return scoLicitacaoDAO;
    }

    protected IParametroFacade getParametroFacade() {
	return parametroFacade;
    }

    protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
	return scoPontoParadaSolicitacaoDAO;
    }

    protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
	return scoFaseSolicitacaoDAO;
    }

    protected ScoScJnDAO getScoScJnDAO() {
	return scoScJnDAO;
    }

    protected ScoSsJnDAO getScoSsJnDAO() {
	return scoSsJnDAO;
    }

    protected IComprasFacade getComprasFacade() {
	return comprasFacade;
    }

    protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
	return solicitacaoComprasFacade;
    }

    protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
	return solicitacaoServicoFacade;
    }

    protected IPacFacade getPacFacade() {
	return pacFacade;
    }

    protected IServidorLogadoFacade getServidorLogadoFacade() {
	return this.servidorLogadoFacade;
    }

}
