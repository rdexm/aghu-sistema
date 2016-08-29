package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO.PlanosPosAltaCRUDExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class PlanosPosAltaCRUD extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(PlanosPosAltaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPlanoPosAltaDAO mpmPlanoPosAltaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2583270777221183442L;


	/**
	 * Método responsável pelo count de registros de planos de pos alta
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @param unidadeFuncional
	 * @return
	 */
	public Long pesquisarPlanosPosAltaCount(Integer codigoPlano,
			String descricaoPlano,
			DominioSituacao situacaoPlano){

		return this.getMpmPlanoPosAltaDAO().pesquisarPlanosPosAltaCount(codigoPlano, descricaoPlano, situacaoPlano);
	}
	
	public List<MpmPlanoPosAlta> pesquisarPlanosPosAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPlano,
			String descricaoPlano,
			DominioSituacao situacaoPlano){

		return this.getMpmPlanoPosAltaDAO().pesquisarPlanosPosAlta(firstResult, maxResult, orderProperty, asc, codigoPlano, descricaoPlano, situacaoPlano);
	}
	
	public MpmPlanoPosAlta obterPlanoPosAltaPeloId(Integer seq) {
		return this.getMpmPlanoPosAltaDAO().obterPlanoPosAltaPeloId(seq); 
	}
	
	public void removerPlano(final Short codigoPlanoPosAltaExclusao, Integer periodo) throws BaseException{
		final MpmPlanoPosAlta planoPosAlta = this.getMpmPlanoPosAltaDAO().obterPorChavePrimaria(codigoPlanoPosAltaExclusao);
		preRemovePlanoPosAlta(planoPosAlta, periodo);
		this.getMpmPlanoPosAltaDAO().removerPlano(planoPosAlta);
	}

	public void persistPlanoPosAlta(MpmPlanoPosAlta planoPosAlta) throws ApplicationBusinessException {
		MpmPlanoPosAltaDAO dao = mpmPlanoPosAltaDAO;
		if (planoPosAlta.getSeq() == null) {
			prePersistirPlanoPosAlta(planoPosAlta);
			dao.persistir(planoPosAlta);
			dao.flush();
		} else {
			//preUpdatePlanoPosAlta... Não se aplica, já é feito automaticamente
			dao.atualizar(planoPosAlta);
			dao.flush();
		}
	}

	protected void prePersistirPlanoPosAlta(MpmPlanoPosAlta planoPosAlta) throws ApplicationBusinessException {
		if(planoPosAlta.getIndOutros().equals(DominioSimNao.S) && !planoPosAlta.getIndExigeComplemento()){
			throw new ApplicationBusinessException (
					PlanosPosAltaCRUDExceptionCode.MPM_PLANO_POS_ALTA1);
		}

		if(planoPosAlta.getSeq()==null){
			planoPosAlta.setCriadoEm(new Date());
		}
	}
	
	protected void preRemovePlanoPosAlta(MpmPlanoPosAlta planoPosAlta, Integer periodo) throws BaseException {
		if(DateUtil.calcularDiasEntreDatas(new Date(), planoPosAlta.getCriadoEm()) > periodo){
			throw new ApplicationBusinessException (
					PlanosPosAltaCRUDExceptionCode.ERRO_REMOVER_PLANO_POS_ALTA);
		}
		
		this.validaDelecao(planoPosAlta);
	}
	
	/**
	 * @ORADB CHK_ANU_TIPO_ITEM_DIETAS
	 * @param tipoDieta
	 * @throws BaseException
	 */
	public void validaDelecao(MpmPlanoPosAlta planoPosAlta) throws BaseException {
		
		if (planoPosAlta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		BaseListException erros = new BaseListException();
		erros.add(this.existeItemPlanoPosAlta(planoPosAlta, MpmAltaPlano.class, MpmAltaPlano.Fields.PLANO_POS_ALTA, PlanosPosAltaCRUDExceptionCode.MPM_APL_PLA_FK1));

		if (erros.hasException()) {
			throw erros;
		}
		
	}
	

	@SuppressWarnings("unchecked")
	private ApplicationBusinessException existeItemPlanoPosAlta(MpmPlanoPosAlta planoPosAlta, Class class1, Enum field, BusinessExceptionCode exceptionCode) {

		if (planoPosAlta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final boolean isExisteItem = this.getMpmPlanoPosAltaDAO().existeItemPlanoPosAlta(planoPosAlta, class1, field);
		
		if(isExisteItem){
			return new ApplicationBusinessException(exceptionCode);
		}
		
		return null;
	}
	
	protected MpmPlanoPosAltaDAO getMpmPlanoPosAltaDAO() {
		return mpmPlanoPosAltaDAO;
	}
	
}
