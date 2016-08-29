package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class ManterAltaDiagSecundarioRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagSecundarioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8006634779606542139L;

	public enum ManterAltaDiagSecundarioRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_DIAG_SECUNDARIO, MPM_02612, MPM_02613, MPM_02614, MPM_02615;

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
	 * Insere objeto MpmAltaDiagSecundario.
	 * 
	 * @param {MpmAltaDiagSecundario} altaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaDiagSecundario(
			MpmAltaDiagSecundario altaDiagSecundario)
			throws ApplicationBusinessException {

		try {

			this.preInserirAltaDiagSecundario(altaDiagSecundario);
			this.getAltaDiagSecundarioDAO().persistir(altaDiagSecundario);
			this.getAltaDiagSecundarioDAO().flush();

		} catch (Exception e) {

			ManterAltaDiagSecundarioRNExceptionCode.ERRO_INSERIR_ALTA_DIAG_SECUNDARIO
					.throwException(e);

		}

	}

	/**
	 * Atualiza objeto MpmAltaDiagSecundario.
	 * 
	 * @param {MpmAltaDiagSecundario} altaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaDiagSecundario(
			MpmAltaDiagSecundario altaDiagSecundario)
			throws ApplicationBusinessException {

		this.preAtualizarAltaDiagSecundario(altaDiagSecundario);
		this.getAltaDiagSecundarioDAO().merge(altaDiagSecundario);
		this.getAltaDiagSecundarioDAO().flush();
		
	}
	
	/**
	 * Remove objeto MpmAltaDiagSecundario.
	 * 
	 * @param {MpmAltaDiagSecundario} altaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagSecundario(MpmAltaDiagSecundario altaDiagSecundario) throws ApplicationBusinessException {

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagSecundario.getMpmAltaSumarios());
		this.getAltaDiagSecundarioDAO().remover(altaDiagSecundario);
		this.getAltaDiagSecundarioDAO().flush();

	}

	/**
	 * ORADB Trigger MPMT_ADS_BASE_BRI ORADB Trigger MPMT_ADS_BRI
	 * 
	 * @param {MpmAltaDiagSecundario} altaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAltaDiagSecundario(
			MpmAltaDiagSecundario altaDiagSecundario)
			throws ApplicationBusinessException {
		
		if (altaDiagSecundario.getMpmCidAtendimentos() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(altaDiagSecundario.getMpmCidAtendimentos().getSeq());
		}
		
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagSecundario.getMpmAltaSumarios());

	}

	/**
	 * ORADB Trigger MPMT_ADS_BASE_BRU ORADB Trigger MPMT_ADS_BRU
	 * 
	 * @param {MpmAltaDiagSecundario} novoAltaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	public void preAtualizarAltaDiagSecundario(
			MpmAltaDiagSecundario altaDiagSecundario)
			throws ApplicationBusinessException {

		MpmAltaDiagSecundarioDAO dao = this.getAltaDiagSecundarioDAO();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = dao
				.obterMpmAltaDiagSecundarioVelho(altaDiagSecundario);

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagSecundario.getMpmAltaSumarios());

		this.getAltaSumarioRN().verificarSituacao(
				altaDiagSecundario.getIndSituacao(),
				antigoAltaDiagSecundario.getIndSituacao(),
				ManterAltaDiagSecundarioRNExceptionCode.MPM_02614);

		this.verificarAtualizacao(altaDiagSecundario, antigoAltaDiagSecundario);

		this.getAltaSumarioRN().verificarIndCarga(
				altaDiagSecundario.getIndCarga(),
				antigoAltaDiagSecundario.getIndCarga(),
				ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

		this.verificarAlteracaoCampos(altaDiagSecundario,
				antigoAltaDiagSecundario);

		this.verificarAtendimento(altaDiagSecundario, antigoAltaDiagSecundario);

	}

	/**
	 * ORADB Procedure RN_ADSP_VER_UPDATE
	 * 
	 * Só permitir alterar o ind_situacao se o ind_carga for verdadeiro.
	 * 
	 * @param {MpmAltaDiagSecundario} novoAltaDiagSecundario
	 * @param {MpmAltaDiagSecundario} antigoAltaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAtualizacao(
			MpmAltaDiagSecundario novoAltaDiagSecundario,
			MpmAltaDiagSecundario antigoAltaDiagSecundario)
			throws ApplicationBusinessException {

		if (novoAltaDiagSecundario == null || antigoAltaDiagSecundario == null) {

			throw new IllegalArgumentException("Parametro invalido!!!");

		}

		Boolean novoCarga = novoAltaDiagSecundario.getIndCarga();

		if (!novoCarga.booleanValue()
				&& StringUtil.modificado(novoAltaDiagSecundario.getDescCid(),
						antigoAltaDiagSecundario.getDescCid())
				&& !CoreUtil.modificados(novoAltaDiagSecundario.getCidSeq(),
						antigoAltaDiagSecundario.getCidSeq())) {

			throw new ApplicationBusinessException(
					ManterAltaDiagSecundarioRNExceptionCode.MPM_02612);

		}

		if (!novoCarga.booleanValue()
				&& !StringUtil.modificado(novoAltaDiagSecundario.getDescCid(),
						antigoAltaDiagSecundario.getDescCid())
				&& CoreUtil.modificados(novoAltaDiagSecundario.getCidSeq(),
						antigoAltaDiagSecundario.getCidSeq())) {

			throw new ApplicationBusinessException(
					ManterAltaDiagSecundarioRNExceptionCode.MPM_02613);

		}

	}

	/**
	 * Implementa parte da trigger MPMT_ADS_BRU
	 * 
	 * Não permitir alterar nenhum campo se o ind_carga for verdadeiro.
	 * 
	 * @param {MpmAltaDiagSecundario} novoAltaDiagSecundario
	 * @param {MpmAltaDiagSecundario} antigoAltaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAlteracaoCampos(
			MpmAltaDiagSecundario novoAltaDiagSecundario,
			MpmAltaDiagSecundario antigoAltaDiagSecundario)
			throws ApplicationBusinessException {

		if (CoreUtil.modificados(novoAltaDiagSecundario.getId()
				.getAsuApaAtdSeq(), antigoAltaDiagSecundario.getId()
				.getAsuApaAtdSeq())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getId()
						.getAsuApaSeq(), antigoAltaDiagSecundario.getId()
						.getAsuApaSeq())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getId()
						.getAsuSeqp(), antigoAltaDiagSecundario.getId()
						.getAsuSeqp())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getId()
						.getSeqp(), antigoAltaDiagSecundario.getId().getSeqp())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getDescCid(),
						antigoAltaDiagSecundario.getDescCid())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getComplCid() == null ? "" : novoAltaDiagSecundario.getComplCid(),
						antigoAltaDiagSecundario.getComplCid() == null ? "" : antigoAltaDiagSecundario.getComplCid())
				|| CoreUtil.modificados(novoAltaDiagSecundario.getCidSeq(),
						antigoAltaDiagSecundario.getCidSeq())) {

			if (novoAltaDiagSecundario.getIndCarga().booleanValue()) {

				throw new ApplicationBusinessException(
						ManterAltaDiagSecundarioRNExceptionCode.MPM_02615);

			}

		}

	}

	/**
	 * ORADB Trigger MPMT_ADS_BASE_BRU.
	 * 
	 * Se novo atendimento for diferente do antigo, então verifica integridade
	 * com mpm_cid_atendimentos.
	 * 
	 * @param {MpmAltaDiagSecundario} novoAltaDiagSecundario
	 * @param {MpmAltaDiagSecundario} antigoAltaDiagSecundario
	 * @throws ApplicationBusinessException
	 */
	public void verificarAtendimento(
			MpmAltaDiagSecundario novoAltaDiagSecundario,
			MpmAltaDiagSecundario antigoAltaDiagSecundario)
			throws ApplicationBusinessException {

		MpmCidAtendimento mpmCidAtendimentos = novoAltaDiagSecundario.getMpmCidAtendimentos();
		if (mpmCidAtendimentos != null && !mpmCidAtendimentos.equals(
				antigoAltaDiagSecundario.getMpmCidAtendimentos())) {

			this.getAltaSumarioRN().verificarCidAtendimento(
					mpmCidAtendimentos.getSeq());

		}

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaDiagSecundarioDAO getAltaDiagSecundarioDAO() {
		return mpmAltaDiagSecundarioDAO;
	}

}