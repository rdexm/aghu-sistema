package br.gov.mec.aghu.prescricaomedica.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira - 29/10/2010
 *
 */
@Stateless
public class ManterAltaConsultoriaRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaConsultoriaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1605857877307223128L;

	public enum ManterAltaConsultoriaRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_CONSULTORIA, ERRO_ATUALIZAR_ALTA_CONSULTORIA, MPM_02638, MPM_02639, MPM_02635, MPM_02636, MPM_02637, MPM_02653, MPM_02640;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

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
	 * Insere objeto MpmAltaConsultoria.
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 */
	public void inserirAltaConsultoria(
			MpmAltaConsultoria altaConsultoria)
			throws ApplicationBusinessException {

		try {

			this.preInserirAltaConsultoria(altaConsultoria);
			this.getAltaConsultoriaDAO().persistir(altaConsultoria);
			this.getAltaConsultoriaDAO().flush();

		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterAltaConsultoriaRNExceptionCode.ERRO_INSERIR_ALTA_CONSULTORIA
					.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_ACN_BRI
	 * ORADB Trigger MPMT_ACN_BASE_BRI
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 */
	private void preInserirAltaConsultoria(
			MpmAltaConsultoria altaConsultoria) throws ApplicationBusinessException {

		//Verifica integridade com MPM_SOLICITACAO_CONSULTORIAS
		if (altaConsultoria.getSolicitacaoConsultoria() != null) {
			
			getAltaSumarioRN().verificarSolicitacaoConsultoria(altaConsultoria.getSolicitacaoConsultoria().getId().getSeq());
			
		}
		
		//Verifica se alta_sumarios está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaConsultoria.getAltaSumario());
		
		//Verifica data de consultoria
		getAltaSumarioRN().verificarDtHr(altaConsultoria.getAltaSumario().getId().getApaAtdSeq(), altaConsultoria.getDthrConsultoria());

	}
	
	/**
	 * Atualiza objeto MpmAltaConsultoria.
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaConsultoria(MpmAltaConsultoria altaConsultoria)
			throws ApplicationBusinessException {

		try {

			this.preAtualizarAltaConsultoria(altaConsultoria);
			this.getAltaConsultoriaDAO().merge(altaConsultoria);
			this.getAltaConsultoriaDAO().flush();

		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterAltaConsultoriaRNExceptionCode.ERRO_ATUALIZAR_ALTA_CONSULTORIA
					.throwException(e);

		}

	}


	/**
	 * ORADB Trigger MPMT_ACN_BRU ORADB Trigger MPMT_ACN_BASE_BRU
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 */
	private void preAtualizarAltaConsultoria(MpmAltaConsultoria altaConsultoria)
			throws ApplicationBusinessException {

		MpmAltaConsultoria antigoAltaConsultoria = getAltaConsultoriaDAO()
				.obterPorChavePrimaria(altaConsultoria.getId());

		Integer antigoScnSeq = antigoAltaConsultoria
				.getSolicitacaoConsultoria() != null ? antigoAltaConsultoria
				.getSolicitacaoConsultoria().getId().getSeq() : Integer
				.valueOf(0);

		Integer novoScnSeq = altaConsultoria.getSolicitacaoConsultoria() != null ? altaConsultoria
				.getSolicitacaoConsultoria().getId().getSeq()
				: Integer.valueOf(0);

		if (novoScnSeq.intValue() != antigoScnSeq.intValue()) {

			getAltaSumarioRN().verificarSolicitacaoConsultoria(novoScnSeq);

		}

		// Verifica se alta_sumarios está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaConsultoria.getAltaSumario());

		// Não permite inativar a situacao
		getAltaSumarioRN().verificarSituacao(altaConsultoria.getIndSituacao(),
				antigoAltaConsultoria.getIndSituacao(),
				ManterAltaConsultoriaRNExceptionCode.MPM_02638);

		// Só permite alterar a desc consultoria se a especialidade também for
		// alterada
		this.verificarAtualizacao(altaConsultoria, antigoAltaConsultoria);

		// Não permitir alterar nenhum campo se o ind_carga for igual a 'S'
		this.verificarPermissaoAlteracao(altaConsultoria, antigoAltaConsultoria);

		// Não permitir alterar o IND_CARGA
		getAltaSumarioRN().verificarIndCarga(altaConsultoria.getIndCarga(),
				antigoAltaConsultoria.getIndCarga(),
				ManterAltaConsultoriaRNExceptionCode.MPM_02639);

		if (altaConsultoria.getDthrConsultoria().getTime() != antigoAltaConsultoria
				.getDthrConsultoria().getTime()) {

			// Verifica data de consultoria
			getAltaSumarioRN().verificarDtHr(
					altaConsultoria.getId().getAsuApaAtdSeq(),
					altaConsultoria.getDthrConsultoria());

		}

	}
	
	/**
	 * Remove objeto MpmAltaConsultoria.
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaConsultoria(MpmAltaConsultoria altaConsultoria) throws ApplicationBusinessException {

		this.preRemoverAltaConsultoria(altaConsultoria);
		this.getAltaConsultoriaDAO().remover(altaConsultoria);
		this.getAltaConsultoriaDAO().flush();

	}
	
	/**
	 * ORADB Trigger MPMT_ACN_BRD
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 * @throws ApplicationBusinessException
	 */
	private void preRemoverAltaConsultoria(MpmAltaConsultoria altaConsultoria) throws ApplicationBusinessException {
		
		//Verifica se alta_sumarios está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaConsultoria.getAltaSumario());

	}

	/**
	 * ORADB Procedure mpmk_acn_rn.rn_acnp_ver_modific.
	 * 
	 * Não permitir alterar nenhum campo se o ind_carga for igual a 'S'.
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 * @param {MpmAltaConsultoria} antigoAltaConsultoria
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarPermissaoAlteracao(
			MpmAltaConsultoria altaConsultoria,
			MpmAltaConsultoria antigoAltaConsultoria)
			throws ApplicationBusinessException {

		Integer antigoScnSeq = antigoAltaConsultoria.getSolicitacaoConsultoria() != null
				&& antigoAltaConsultoria.getSolicitacaoConsultoria().getId() != null
				&& antigoAltaConsultoria.getSolicitacaoConsultoria().getId().getSeq() != null ?

		antigoAltaConsultoria.getSolicitacaoConsultoria().getId().getSeq() : Integer.valueOf(0);

		Integer novoScnSeq = altaConsultoria.getSolicitacaoConsultoria() != null
				&& altaConsultoria.getSolicitacaoConsultoria().getId() != null
				&& altaConsultoria.getSolicitacaoConsultoria().getId().getSeq() != null 
						? altaConsultoria.getSolicitacaoConsultoria().getId().getSeq()
						: Integer.valueOf(0);

		Integer antigoScnAtdSeq = antigoAltaConsultoria.getSolicitacaoConsultoria() != null
				&& antigoAltaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica() != null
				&& antigoAltaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId() != null
				&& antigoAltaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId().getAtdSeq() != null 
					? antigoAltaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId().getAtdSeq() 
					: Integer.valueOf(0);

		Integer novoScnAtdSeq = altaConsultoria.getSolicitacaoConsultoria() != null
				&& altaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica() != null
				&& altaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId() != null
				&& altaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId().getAtdSeq() != null 
					? altaConsultoria.getSolicitacaoConsultoria().getPrescricaoMedica().getId().getAtdSeq()
					: Integer.valueOf(0);

		Short novoEspSeq = altaConsultoria.getAghEspecialidade().getSeq();

		Short antigoEspSeq = antigoAltaConsultoria.getAghEspecialidade().getSeq();

		if ((CoreUtil.modificados(altaConsultoria.getId().getAsuApaAtdSeq(),

				antigoAltaConsultoria.getId().getAsuApaAtdSeq())
				|| CoreUtil.modificados(altaConsultoria.getId().getAsuApaSeq(),
				antigoAltaConsultoria.getId().getAsuApaSeq())
				|| CoreUtil.modificados(altaConsultoria.getId().getAsuSeqp(),
				antigoAltaConsultoria.getId().getAsuSeqp())
				|| CoreUtil.modificados(altaConsultoria.getId().getSeqp(),
				antigoAltaConsultoria.getId().getSeqp())
				|| CoreUtil.modificados(altaConsultoria.getDescConsultoria(),
				antigoAltaConsultoria.getDescConsultoria())
				|| altaConsultoria.getDthrConsultoria().getTime() != antigoAltaConsultoria.getDthrConsultoria().getTime()
				|| CoreUtil.modificados(altaConsultoria.getIndCarga(),antigoAltaConsultoria.getIndCarga())
				|| novoScnAtdSeq.intValue() != antigoScnAtdSeq.intValue()
				|| novoScnSeq.intValue() != antigoScnSeq.intValue() || novoEspSeq.shortValue() != antigoEspSeq.shortValue())
				&& (altaConsultoria.getIndCarga().booleanValue())) {

			ManterAltaConsultoriaRNExceptionCode.MPM_02640.throwException();
		}
	}

	/**
	 * ORADB Procedure mpmk_acn_rn.rn_acnp_ver_update.
	 * 
	 * Operação: UPD. Descrição: Se o ind_carga for = 'N' e desc consultoria for
	 * alterado a FK da ESPECIALIDADE tabém deve ser alterada. Se o ind_carga =
	 * 'S', a fk da SOLICITACAO CONSULTORIA deverá esta preenchida, caso
	 * contrário ela deverá ser obrigatoriamente nula.
	 * 
	 * @param {MpmAltaConsultoria} altaConsultoria
	 * @param {MpmAltaConsultoria} antigoAltaConsultoria
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAtualizacao(MpmAltaConsultoria altaConsultoria,
			MpmAltaConsultoria antigoAltaConsultoria)
			throws ApplicationBusinessException {

		Boolean carga = altaConsultoria.getIndCarga();
		String novoDesc = altaConsultoria.getDescConsultoria();
		String antigoDesc = antigoAltaConsultoria.getDescConsultoria();
		Short novoEspSeq = altaConsultoria.getAghEspecialidade().getSeq();
		Short antigoEspSeq = antigoAltaConsultoria.getAghEspecialidade()
				.getSeq();

		if (!carga.booleanValue()
				&& novoDesc != antigoDesc
				&& ((novoEspSeq == null && antigoEspSeq == null) || (novoEspSeq
						.shortValue() == antigoEspSeq.shortValue()))) {

			ManterAltaConsultoriaRNExceptionCode.MPM_02635.throwException();

		}

		if (!carga.booleanValue() && novoDesc.equals(antigoDesc)
				&& novoEspSeq != antigoEspSeq) {

			ManterAltaConsultoriaRNExceptionCode.MPM_02653.throwException();

		}

		if (carga.booleanValue()
				&& altaConsultoria.getSolicitacaoConsultoria() == null) {

			ManterAltaConsultoriaRNExceptionCode.MPM_02636.throwException();

		}

		if (!carga.booleanValue()
				&& altaConsultoria.getSolicitacaoConsultoria() != null) {

			ManterAltaConsultoriaRNExceptionCode.MPM_02637.throwException();

		}

	}
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	protected MpmAltaConsultoriaDAO getAltaConsultoriaDAO() {
		return mpmAltaConsultoriaDAO;
	}

}
