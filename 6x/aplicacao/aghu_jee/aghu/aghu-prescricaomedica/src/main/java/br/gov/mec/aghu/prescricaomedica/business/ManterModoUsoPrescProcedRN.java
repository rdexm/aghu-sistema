package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira
 */
@Stateless
public class ManterModoUsoPrescProcedRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterModoUsoPrescProcedRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@Inject
private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;

@Inject
private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8674848876025184308L;
	
	public enum ManterModoUsoPrescProcedRNExceptionCode implements
			BusinessExceptionCode {
		MPM_00081, MPM_01003, MPM_00277, MPM_01004, MPM_01005, ERRO_INSERIR_MODO_USO_PRESC_PROCEDIMENTO
		, ERRO_MODO_USO_PRESC_PROCED;

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}
	}

	/**
	 * Insere objeto MpmModoUsoPrescProced.
	 * 
	 * @param {MpmModoUsoPrescProced} modoUsoPrescProced
	 * @throws ApplicationBusinessException
	 */
	public void inserirModoUsoPrescProced(MpmModoUsoPrescProced modoUsoPrescProced) throws ApplicationBusinessException {
		try {
			this.preInserirModoUsoPrescProced(modoUsoPrescProced);
			getModoUsoPrescProcedDAO().persistir(modoUsoPrescProced);
			getModoUsoPrescProcedDAO().flush();

		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterModoUsoPrescProcedRNExceptionCode.ERRO_INSERIR_MODO_USO_PRESC_PROCEDIMENTO.throwException(e);
		}

	}
	
	public void removerModoUsoPrescProced(MpmModoUsoPrescProced modoUsoPrescProced) throws ApplicationBusinessException {
		try {
			modoUsoPrescProced = this.getModoUsoPrescProcedDAO().merge(modoUsoPrescProced);
			this.getModoUsoPrescProcedDAO().remover(modoUsoPrescProced);
			getModoUsoPrescProcedDAO().flush();
	    } catch (PersistenceException e) {
	    	logError(e.getMessage(), e);
	    	throw new ApplicationBusinessException(ManterModoUsoPrescProcedRNExceptionCode.ERRO_MODO_USO_PRESC_PROCED);
	    }
	}

	/**
	 * ORADB Trigger MPMT_MUP_BRI
	 * 
	 * EXECUTA ANTES DE FAZER O INSERT DO OBJETO.
	 * 
	 * Tipo modo procedimento deve estar ativo e ind exige quantidade = S
	 * quantidade deve ser informada .
	 * 
	 * @param {MpmModoUsoPrescProced} modoUsoPrescProced
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirModoUsoPrescProced(
			MpmModoUsoPrescProced modoUsoPrescProced)
			throws ApplicationBusinessException {

		this.verificarTipoModoUsoProcedimento(modoUsoPrescProced
				.getTipoModUsoProcedimento().getId().getSeqp(),
				modoUsoPrescProced.getTipoModUsoProcedimento().getId()
						.getPedSeq(), modoUsoPrescProced.getQuantidade());

		this.verificarAlteracao(modoUsoPrescProced.getPrescricaoProcedimento()
				.getId().getAtdSeq(), modoUsoPrescProced
				.getPrescricaoProcedimento().getId().getSeq());

	}

	/**
	 * ORADB Procedure MPMK_MUP_RN.RN_MUPP_VER_TP_US_PR.
	 * 
	 * Operação: INS Descrição: tipo mod uso procedimento deve estar ativo se
	 * ind exige quantidade = S, quantidade é obrigatória ind_pendente = S.
	 * 
	 * bsoliveira 26/10/2010
	 * 
	 * @param {Short} novoTupSeqp Seqp de MpmTipoModoUsoProcedimento.
	 * @param {Short} novoTupPedSeq pedSeq de MpmTipoModoUsoProcedimento.
	 * @param {Integer} novaQuantidade.
	 * @throws ApplicationBusinessException
	 */
	public void verificarTipoModoUsoProcedimento(Short novoTupSeqp,
			Short novoTupPedSeq, Short novaQuantidade)
			throws ApplicationBusinessException {

		MpmTipoModoUsoProcedimentoDAO tipoModoUsoProcedimentoDAO = this
				.getTipoModoUsoProcedimentoDAO();
		MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = tipoModoUsoProcedimentoDAO
				.obterTipoModoUsoProcedimentoPeloId(novoTupPedSeq, novoTupSeqp);

		if (tipoModoUsoProcedimento != null) {

			DominioSituacao indSituacao = tipoModoUsoProcedimento
					.getIndSituacao();
			Boolean indQuantidade = tipoModoUsoProcedimento
					.getIndExigeQuantidade();

			if (!DominioSituacao.A.equals(indSituacao)) {

				throw new ApplicationBusinessException(
						ManterModoUsoPrescProcedRNExceptionCode.MPM_01004);

			} else if (indQuantidade != null
					&& indQuantidade.booleanValue()
					&& (novaQuantidade == null || (novaQuantidade != null && novaQuantidade
							.intValue() == 0))) {

				throw new ApplicationBusinessException(
						ManterModoUsoPrescProcedRNExceptionCode.MPM_01005);

			}

		} else {

			throw new ApplicationBusinessException(
					ManterModoUsoPrescProcedRNExceptionCode.MPM_00277);

		}

	}

	/**
	 * ORADB Procedure MPMK_MUP_RN.RN_MUPP_VER_ALTERA.
	 * 
	 * Operação: INS Descrição: somente pode alterar se em prescricao
	 * procedimento ind_pendente = P OU = B OU = D.
	 * 
	 * bsoliveira 25/10/2010
	 * 
	 * @param novoPprAtdSeq
	 *            Atd Seq de MpmPrescricaoProcedimento.
	 * @param novoPprSeq
	 *            Seq de MpmPrescricaoProcedimento.
	 * @throws ApplicationBusinessException
	 */
	public void verificarAlteracao(Integer novoPprAtdSeq, Long novoPprSeq)
			throws ApplicationBusinessException {

		MpmPrescricaoProcedimentoDAO prescricaoProcedimentoDAO = this
				.getPrescricaoProcedimentoDAO();
		MpmPrescricaoProcedimento prescricaoProcedimento = prescricaoProcedimentoDAO
				.obterProcedimentoPeloId(novoPprAtdSeq, novoPprSeq);

		if (prescricaoProcedimento != null) {

			DominioIndPendenteItemPrescricao pendente = prescricaoProcedimento
					.getIndPendente();
			if (pendente != null
					&& !DominioIndPendenteItemPrescricao.P
							.equals(pendente)
					&& !DominioIndPendenteItemPrescricao.B
							.equals(pendente)
					&& !DominioIndPendenteItemPrescricao.D
							.equals(pendente)) {

				throw new ApplicationBusinessException(
						ManterModoUsoPrescProcedRNExceptionCode.MPM_01003);

			}

		} else {

			throw new ApplicationBusinessException(
					ManterModoUsoPrescProcedRNExceptionCode.MPM_00081);

		}

	}

	protected MpmModoUsoPrescProcedDAO getModoUsoPrescProcedDAO() {
		return mpmModoUsoPrescProcedDAO;
	}

	protected MpmTipoModoUsoProcedimentoDAO getTipoModoUsoProcedimentoDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}

	protected MpmPrescricaoProcedimentoDAO getPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

}
