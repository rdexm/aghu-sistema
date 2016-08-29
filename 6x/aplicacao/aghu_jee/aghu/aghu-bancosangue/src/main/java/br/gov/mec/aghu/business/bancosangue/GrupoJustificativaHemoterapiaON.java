package br.gov.mec.aghu.business.bancosangue;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsGrupoJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoJustificativaHemoterapiaON extends BaseBusiness {

	@EJB
	private GrupoJustificativaHemoterapiaRN grupoJustificativaHemoterapiaRN;
	
	private static final Log LOG = LogFactory.getLog(GrupoJustificativaHemoterapiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AbsJustificativaComponenteSanguineoDAO absJustificativaComponenteSanguineoDAO;
	
	@Inject
	private AbsGrupoJustificativaComponenteSanguineoDAO absGrupoJustificativaComponenteSanguineoDAO;

	private static final long serialVersionUID = 938678941337475044L;

	private enum EnumGrupoJustificativaHemoterapiaMessageCode implements BusinessExceptionCode {
		MSG_INFORMAR_PELO_MENOS_COMP_SANG_PROC_HEMOT,MSG_INFORMAR_APENAS_COMP_SANG_PROC_HEMOT;
	}
	
	private AbsGrupoJustificativaComponenteSanguineoDAO getAbsGrupoJustificativaComponenteSanguineoDAO() {
		return absGrupoJustificativaComponenteSanguineoDAO;
	}
	
	private AbsJustificativaComponenteSanguineoDAO getAbsJustificativaComponenteSanguineoDAO() {
		return absJustificativaComponenteSanguineoDAO;
	}
	
	private GrupoJustificativaHemoterapiaRN getGrupoJustificativaHemoterapiaRN() {
		return grupoJustificativaHemoterapiaRN;
	}
		
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(
			int firstResult, int maxResults, String orderProperty, Boolean asc,
			Short seq, String descricao, DominioSituacao situacao, String titulo) {
		return getAbsGrupoJustificativaComponenteSanguineoDAO().pesquisarGrupoJustificativaComponenteSanquineo(
				firstResult, maxResults, orderProperty, asc, seq, descricao, situacao, titulo);
	}

	public Long pesquisarGrupoJustificativaComponenteSanguineoCount(Short seq, String descricao, DominioSituacao situacao, String titulo) {
		return getAbsGrupoJustificativaComponenteSanguineoDAO().pesquisarGrupoJustificativaComponenteSanquineoCount(
				seq, descricao, situacao, titulo);
	}

	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaComponenteSanguineo(Short seq) {
		return getAbsGrupoJustificativaComponenteSanguineoDAO().obterPorChavePrimaria(seq);
	}
	
	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaPorId(Short seq) {
		return getAbsGrupoJustificativaComponenteSanguineoDAO().obterGrupoJustificativaPorId(seq);
	}

	
	
	public void persistirGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupo) throws ApplicationBusinessException {
		if (grupo.getSeq() == null) {
			//INSERT
			getGrupoJustificativaHemoterapiaRN().preInsertGrupoJustificativaComponenteSanguineo(grupo);
			getAbsGrupoJustificativaComponenteSanguineoDAO().persistir(grupo);
		} else {
			//UPDATE
			getGrupoJustificativaHemoterapiaRN().preUpdateGrupoJustificativaComponenteSanguineo(grupo);
			getAbsGrupoJustificativaComponenteSanguineoDAO().merge(grupo);
		}
	}
	
	public void persistirJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) throws BaseListException, ApplicationBusinessException {
		verificarDadosJustificativaComponenteSanguineo(justificativa);
		
		AbsGrupoJustificativaComponenteSanguineo grupo = getAbsGrupoJustificativaComponenteSanguineoDAO().obterOriginal(justificativa.getGrupoJustificativaComponenteSanguineo());
		
		justificativa.setGrupoJustificativaComponenteSanguineo(grupo);
		
		if (justificativa.getSeq() == null) {
			//INSERT
			getGrupoJustificativaHemoterapiaRN().preInsertJustificativaComponenteSanquineo(justificativa);
			getAbsJustificativaComponenteSanguineoDAO().persistir(justificativa);
		} else {
			//UPDATE
			getGrupoJustificativaHemoterapiaRN().preUpdateJustificativaComponenteSanguineo(justificativa);
			getAbsJustificativaComponenteSanguineoDAO().merge(justificativa);
		}
	}

	private void verificarDadosJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) throws BaseListException {
		if (justificativa.getComponenteSanguineo() != null && justificativa.getProcedimentoHemoterapico() != null){
			throw new BaseListException(EnumGrupoJustificativaHemoterapiaMessageCode.MSG_INFORMAR_APENAS_COMP_SANG_PROC_HEMOT);
			
		}else if(justificativa.getComponenteSanguineo() == null && justificativa.getProcedimentoHemoterapico() == null){
			throw new BaseListException(EnumGrupoJustificativaHemoterapiaMessageCode.MSG_INFORMAR_PELO_MENOS_COMP_SANG_PROC_HEMOT);			
		} 
	}
}