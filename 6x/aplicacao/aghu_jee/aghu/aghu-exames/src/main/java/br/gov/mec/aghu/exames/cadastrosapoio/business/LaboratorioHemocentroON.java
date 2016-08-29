package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelLaboratorioExternosDAO;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class LaboratorioHemocentroON extends BaseBusiness {

	
	@EJB
	private LaboratorioHemocentroRN laboratorioHemocentroRN;
	
	private static final Log LOG = LogFactory.getLog(LaboratorioHemocentroON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelLaboratorioExternosDAO aelLaboratorioExternosDAO;

	private static final long serialVersionUID = -8517388302515601788L;

	public Long countLaboratoioHemocentro(final AelLaboratorioExternos filtros) {
		return this.getAelLaboratorioExternosDAO().countLaboratorioHemocentro(filtros);
	}
	
	public List<AelLaboratorioExternos> pesquisaLaboratoioHemocentroPaginado(final AelLaboratorioExternos filtros, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.getAelLaboratorioExternosDAO().pesquisaLaboratorioHemocentroPaginado(filtros, firstResult, 
				maxResult, orderProperty, asc);
	}
	
	public void saveOrLaboratoioHemocentro(AelLaboratorioExternos laboratorioHemocentro) throws BaseException{
		if(StringUtils.isNotBlank(laboratorioHemocentro.getMail())) {
			laboratorioHemocentro.setMail(laboratorioHemocentro.getMail().toLowerCase());
		}
		
		if(laboratorioHemocentro != null && laboratorioHemocentro.getSeq() == null) {
			this.getLaboratorioHemocentroRN().inserir(laboratorioHemocentro);
		} else {
			this.getLaboratorioHemocentroRN().atualizar(laboratorioHemocentro);
		}
	}
	
	public AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAelLaboratorioExternosDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	public AelLaboratorioExternos obterLaboratorioExternoPorId(Integer seq) {
		return this.getAelLaboratorioExternosDAO().obterPorChavePrimaria(seq);
	}
	
	//getter do DAO
	protected AelLaboratorioExternosDAO getAelLaboratorioExternosDAO() {
		return aelLaboratorioExternosDAO;
	}
	
	protected LaboratorioHemocentroRN getLaboratorioHemocentroRN() {
		return laboratorioHemocentroRN;
	}
	
}
