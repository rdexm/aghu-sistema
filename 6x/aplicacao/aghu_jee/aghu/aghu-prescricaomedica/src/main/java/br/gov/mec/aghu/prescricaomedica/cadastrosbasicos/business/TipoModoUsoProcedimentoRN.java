package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoModoUsoProcedimentoRN extends BaseBusiness {

	@EJB
	private ProcedEspecialDiversoRN procedEspecialDiversoRN;

	private static final Log LOG = LogFactory.getLog(TipoModoUsoProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	private static final long serialVersionUID = 3219049500952168601L;

	public enum ManterTipoModoUsoProcedimentoRNExceptionCode implements BusinessExceptionCode {
		MPM_00772, MPM_00682;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	public void inserir(MpmTipoModoUsoProcedimento modoUso, RapServidores servidorLogado) throws BaseException {
		preInsertTipoModoUsoProcedimento(modoUso, servidorLogado);
		getMpmTipoModoUsoProcedimentoDAO().persistir(modoUso);
	}
	
	/**
	 * @ORADB Trigger MPMT_TUP_BRI
	 * 
	 * @param modoUso
	 * @throws ApplicationBusinessException
	 */
	private void preInsertTipoModoUsoProcedimento(MpmTipoModoUsoProcedimento modoUso, RapServidores servidorLogado) throws BaseException {
		//Verifica e atribui o usuário logado
		modoUso.setServidor(servidorLogado);
		
		//Atualiza data de criação do modo de uso
		modoUso.setCriadoEm(new Date());
		
		//Verifica se procedimento especial está ativo
		if(modoUso.getProcedimentoEspecialDiverso() == null || modoUso.getProcedimentoEspecialDiverso().getIndSituacao() == null || !modoUso.getProcedimentoEspecialDiverso().getIndSituacao().isAtivo()) {
			ManterTipoModoUsoProcedimentoRNExceptionCode.MPM_00772.throwException();
		}
	}

	public void alterar(MpmTipoModoUsoProcedimento modoUso, RapServidores servidorLogado) throws BaseException {
		preUpdateTipoModoUsoProcedimento(modoUso, servidorLogado);
		getMpmTipoModoUsoProcedimentoDAO().merge(modoUso);
	}
	
	/**
	 * @ORADB Trigger MPMT_TUP_BRU
	 * 
	 * @param modoUso
	 * @throws ApplicationBusinessException
	 */
	private void preUpdateTipoModoUsoProcedimento(MpmTipoModoUsoProcedimento modoUso, RapServidores servidorLogado) throws BaseException {
		
		//Verifica se algum campo além da situação foi alterado
		MpmTipoModoUsoProcedimento modoUsoOld = getMpmTipoModoUsoProcedimentoDAO().obterTipoModoUsoProcedimentoPeloId(modoUso.getId().getPedSeq(), modoUso.getId().getSeqp());
		if(CoreUtil.modificados(modoUso.getId(), modoUsoOld.getId())
			|| CoreUtil.modificados(modoUso.getUnidadeMedidaMedica(), modoUsoOld.getUnidadeMedidaMedica())
			|| CoreUtil.modificados(modoUso.getDescricao(), modoUsoOld.getDescricao())
			|| CoreUtil.modificados(modoUso.getCriadoEm(), modoUsoOld.getCriadoEm())
			|| CoreUtil.modificados(modoUso.getIndExigeQuantidade(), modoUsoOld.getIndExigeQuantidade())
			|| CoreUtil.modificados(modoUso.getServidor(), modoUsoOld.getServidor())) {
			ManterTipoModoUsoProcedimentoRNExceptionCode.MPM_00682.throwException();
		}
		
		//Verifica e atribui o usuário logado
		modoUso.setServidor(servidorLogado);
	}
	
	public void remover(MpmTipoModoUsoProcedimento modoUso) throws ApplicationBusinessException {
		preDeleteTipoModoUsoProcedimento(modoUso);
		getMpmTipoModoUsoProcedimentoDAO().remover(modoUso);
	}
	
	/**
	 * @ORADB Trigger MPMT_TUP_BRD
	 * 
	 * @param modoUso
	 * @throws ApplicationBusinessException
	 */
	private void preDeleteTipoModoUsoProcedimento(MpmTipoModoUsoProcedimento modoUso) throws ApplicationBusinessException {
		//Verifica se o período para deletar é válido
		getProcedEspecialDiversoRN().validarDiasPermitidosDelecao(modoUso.getCriadoEm());
	}

	protected MpmTipoModoUsoProcedimentoDAO getMpmTipoModoUsoProcedimentoDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}
	
	public ProcedEspecialDiversoRN getProcedEspecialDiversoRN() {
		return procedEspecialDiversoRN;
	} 
}
