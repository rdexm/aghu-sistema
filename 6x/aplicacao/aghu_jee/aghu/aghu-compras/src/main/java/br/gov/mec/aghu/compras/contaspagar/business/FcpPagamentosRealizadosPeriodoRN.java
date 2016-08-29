package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfVO;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FcpPagamentosRealizadosPeriodoRN extends BaseBusiness{

	private static final long serialVersionUID = -5543076075950382486L;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	/** Injeção do objeto de título da camada de dados */
	@Inject
	private FcpTituloDAO tituloDAO;
	
	@Inject
	private FsoVerbaGestaoDAO verbaGestaoDAO;
	
	/**
	 * Método responsável por retornar a coleção de dados para gerar o relatório em PDF
	 * @param inicioPeriodo
	 * @param finalPeriodo
	 * @param codVerbaGestao
	 * @return coleção de dados do relatório 
	 * @throws ApplicationBusinessException
	 */
	public List<PagamentosRealizadosPeriodoPdfVO> pesquisarPagamentosRealizadosPeriodoPDF(
			Date inicioPeriodo, Date finalPeriodo, Integer codVerbaGestao) throws ApplicationBusinessException {
		return this.getTituloDAO().pesquisarPagamentosRealizadosPeriodoPDF(inicioPeriodo, finalPeriodo, codVerbaGestao);
	}
	
	/**
	 * Método para retorno de verbas de gestão
	 * @param paramPesquisa
	 * @return coleçãoo de verbas gestão
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(Object paramPesquisa) {
		return this.getVerbaGestaoDAO().pesquisarVerbaGestaoPorSeqOuDescricao(paramPesquisa);
	}
	
	public FcpTituloDAO getTituloDAO() {
		return tituloDAO;
	}

	public FsoVerbaGestaoDAO getVerbaGestaoDAO() {
		return verbaGestaoDAO;
	}

}
