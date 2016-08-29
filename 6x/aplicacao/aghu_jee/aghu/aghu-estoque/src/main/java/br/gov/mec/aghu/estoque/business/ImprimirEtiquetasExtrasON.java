package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudPersist;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ImprimirEtiquetasExtrasON extends AbstractAGHUCrudPersist<SceLoteDocImpressao>{

@EJB
private ImprimirEtiquetasExtrasRN imprimirEtiquetasExtrasRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7091852196794526173L;

	private static final Log LOG = LogFactory.getLog(ImprimirEtiquetasExtrasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ISiconFacade siconFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;
	
	@Override
	public BaseDao<SceLoteDocImpressao> getEntidadeDAO() {
		return sceLoteDocImpressaoDAO;
	}

	@Override
	public Object getChavePrimariaEntidade(SceLoteDocImpressao entidade) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractAGHUCrudRn<SceLoteDocImpressao> getRegraNegocio() {
		return imprimirEtiquetasExtrasRN;
	}
	
	public List<SceLoteDocImpressao> efetuarPesquisaUnitarizacaoDeMedicamentosComEtiqueta(
			SceLoteDocImpressao entidadePesquisa, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getSceLoteDocImpressaoDAO().pesquisarLoteDocImpressao(entidadePesquisa, firstResult, maxResult, orderProperty, asc);
	}

	public Long efetuarPesquisaUnitarizacaoDeMedicamentosComEtiquetaCount(
			SceLoteDocImpressao entidadePesquisa) {
		return getSceLoteDocImpressaoDAO().pesquisarLoteDocImpressaoCount(entidadePesquisa);
	}

	public void inserir(SceLoteDocImpressao entidade,
			String nomeMicrocomputador, 
			Date dataFimVinculoServidor, Boolean comReducaoValidade) throws IllegalStateException,
			BaseException {
		if(comReducaoValidade){
			BigDecimal paramReduzValidade = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PERC_REDUZ_VALIDADE).getVlrNumerico();			
			Date novaValidade = buscarValidade(entidade.getLoteDocumento().getDtValidade(), paramReduzValidade);
			entidade.setDtValidade(novaValidade);
		}
		super.inserir(entidade, nomeMicrocomputador, 
				dataFimVinculoServidor);
	}
	
	private Date buscarValidade(Date dataValidade, BigDecimal porcentagemReducaoValidade) {

		Integer qtdDiasVencimento = DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(new Date()), dataValidade);
		if(qtdDiasVencimento > 3){
			BigDecimal novaQtdDiasVencimento = BigDecimal.ONE.subtract(porcentagemReducaoValidade.movePointLeft(2)).multiply(new BigDecimal(qtdDiasVencimento));
			Date novaValidade = DateUtil.truncaData(DateUtil.adicionaDias(new Date(), novaQtdDiasVencimento.intValue()));
			return novaValidade;
		}
		return dataValidade;
	}
	
	public ISiconFacade getSiconFacade() {
		return siconFacade;
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO() {
		return sceLoteDocImpressaoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}
	
	

}