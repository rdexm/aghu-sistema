package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioColetaAtendUrgencia;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicJnDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelPermissaoUnidSolicId;
import br.gov.mec.aghu.model.AelPermissaoUnidSolicsJn;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterPermissoesUnidadesSolicitantesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterPermissoesUnidadesSolicitantesCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AelPermissaoUnidSolicJnDAO aelPermissaoUnidSolicJnDAO;

	@EJB
	private IParametroFacade iParametroFacade;

	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;

	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1190663645408633234L;

	public enum ManterPermissoesUnidadesSolicitantesExceptionCode implements BusinessExceptionCode {
		AEL_00371_SOLICITANTE, AEL_00371_AVISADA, AEL_00471, AEL_01097, AEL_01388, AEL_01334, AEL_00369, ERRO_INSERT_JN, AEL_PUS_PK;
	}

	public void persistirPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		if (aelPermissaoUnidSolic.getId() == null) {
			// inserir
			preInserirPermissaoUnidadeSolicitante(aelPermissaoUnidSolic);
			getAelPermissaoUnidSolicDAO().persistir(aelPermissaoUnidSolic);
			getAelPermissaoUnidSolicDAO().flush();

		} else {

			// atualizar
			Boolean alterou = preAtualizarPermissaoUnidadeSolicitante(aelPermissaoUnidSolic);
			getAelPermissaoUnidSolicDAO().merge(aelPermissaoUnidSolic);
			getAelPermissaoUnidSolicDAO().flush();
			posAtualizarPermissaoUnidadeSolicitante(aelPermissaoUnidSolic, alterou);

		}

	}

	/**
	 * oradb AELT_PUS_BRI
	 * 
	 * @param aelPermissaoUnidSolic
	 * @param unidadeFuncionalSolicitante
	 * @throws ApplicationBusinessException
	 */
	private void preInserirPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		AelPermissaoUnidSolicId id = new AelPermissaoUnidSolicId();
		id.setUfeEmaExaSigla(aelPermissaoUnidSolic.getUnfExecutaExames().getId().getEmaExaSigla());
		id.setUfeEmaManSeq(aelPermissaoUnidSolic.getUnfExecutaExames().getId().getEmaManSeq());
		id.setUfeUnfSeq(aelPermissaoUnidSolic.getUnfExecutaExames().getId().getUnfSeq().getSeq());
		id.setUnfSeq(aelPermissaoUnidSolic.getUnfSolicitante().getSeq());
		aelPermissaoUnidSolic.setId(id);

		AelPermissaoUnidSolic aelPermissaoUnidSolicExistente = getAelPermissaoUnidSolicDAO().obterPorChavePrimaria(aelPermissaoUnidSolic.getId());

		if (aelPermissaoUnidSolicExistente != null) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_PUS_PK);

		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// aelk_ael_rn.rn_aelp_atu_servidor
		aelPermissaoUnidSolic.setRapServidores(servidorLogado);
		aelPermissaoUnidSolic.setRapServidoresAlterado(servidorLogado);

		verificarUnidadeSolicitante(aelPermissaoUnidSolic.getUnfSolicitante());

		verificarUnidadeAviso(aelPermissaoUnidSolic);

		verificaUnidadePlantao(aelPermissaoUnidSolic);

		verificarUnidadeTransporte(aelPermissaoUnidSolic);

		verificarUnidadePrograma(aelPermissaoUnidSolic);

		verificarUnidadeColetaAtend(aelPermissaoUnidSolic);

	}

	private void verificarUnidadeColetaAtend(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {
		// aelk_pus_rn.rn_pusp_ver_col_aten
		String exaSigla = aelPermissaoUnidSolic.getUnfExecutaExames().getId().getEmaExaSigla();
		Integer manSeq = aelPermissaoUnidSolic.getUnfExecutaExames().getId().getEmaManSeq();

		for (AelTipoAmostraExame tae : getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(exaSigla, manSeq)) {

			if ((aelPermissaoUnidSolic.getColetaAtendeUrgencia() != null
					&& (aelPermissaoUnidSolic.getColetaAtendeUrgencia().equals(DominioColetaAtendUrgencia.P) || aelPermissaoUnidSolic.getColetaAtendeUrgencia().equals(DominioColetaAtendUrgencia.T)) && !tae
					.getResponsavelColeta().equals(DominioResponsavelColetaExames.C)

			)) {

				throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_01388);

			}

		}

	}

	private void verificarUnidadePrograma(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		// aelk_pus_rn.rn_pusp_ver_prog_exm
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT);
		Short vlrNumerico = aghParametros.getVlrNumerico().shortValue();

		if (aelPermissaoUnidSolic.getIndPermiteProgramarExames().equals(DominioSimNaoRotina.R)
				&& (aelPermissaoUnidSolic.getUnfSeqAvisa() == null || !aelPermissaoUnidSolic.getUnfSeqAvisa().getSeq().equals(vlrNumerico))) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_01334, vlrNumerico);

		}

	}

	private void verificarUnidadeTransporte(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		// aelk_pus_rn.rn_pusp_ver_transpor
		if (aelPermissaoUnidSolic.getIndExigeTransporteO2() && aelPermissaoUnidSolic.getUnfExecutaExames().getAelExamesMaterialAnalise().getIndDependente()) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_01097);

		}

	}

	private void verificaUnidadePlantao(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		// aelk_pus_rn.rn_pusp_ver_plantao
		if (aelPermissaoUnidSolic.getIndPermiteSolicitarPlantao() && !aelPermissaoUnidSolic.getUnfExecutaExames().getIndExecutaEmPlantao()) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_00471);

		}

	}

	private void verificarUnidadeAviso(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		// aelk_pus_rn.rn_pusp_ver_uf_aviso

		if (aelPermissaoUnidSolic.getUnfSeqAvisa() != null && !aelPermissaoUnidSolic.getUnfSeqAvisa().getIndSitUnidFunc().equals(DominioSituacao.A)) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_00371_AVISADA);

		}

	}

	private void verificarUnidadeSolicitante(AghUnidadesFuncionais unidadeFuncionalSolicitante) throws ApplicationBusinessException {

		// aelk_pus_rn.rn_pusp_ver_unid_sol
		if (!unidadeFuncionalSolicitante.getIndSitUnidFunc().equals(DominioSituacao.A)) {

			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_00371_SOLICITANTE);

		}

	}

	private Boolean preAtualizarPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		getAelPermissaoUnidSolicDAO().desatachar(aelPermissaoUnidSolic);
		AelPermissaoUnidSolic aelPermissaoUnidSolicOriginal = getAelPermissaoUnidSolicDAO().obterPorChavePrimaria(aelPermissaoUnidSolic.getId());

		Boolean alterou = false;
		// aelk_pus_rn.rn_pusp_ver_update
		// Validação removida devido à problemas na implantação do módulo de exames no HUJF. #31355
		/*
		 * if (!aelPermissaoUnidSolic.getRapServidores().getId().equals(aelPermissaoUnidSolicOriginal.getRapServidores().getId())){
		 * 
		 * throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.AEL_00369);
		 * 
		 * }
		 */

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// aelk_ael_rn.rn_aelp_atu_servidor
		aelPermissaoUnidSolic.setRapServidoresAlterado(servidorLogado);

		// aelk_pus_rn.rn_pusp_ver_unid_sol
		if (!aelPermissaoUnidSolic.getUnfSolicitante().getSeq().equals(aelPermissaoUnidSolicOriginal.getUnfSolicitante().getSeq())) {

			alterou = true;
			verificarUnidadeSolicitante(aelPermissaoUnidSolic.getUnfSolicitante());

		}

		// aelk_pus_rn.rn_pusp_ver_uf_aviso
		if (aelPermissaoUnidSolicOriginal.getUnfSeqAvisa() != null && aelPermissaoUnidSolic.getUnfSeqAvisa() != null
				&& !aelPermissaoUnidSolic.getUnfSeqAvisa().getSeq().equals(aelPermissaoUnidSolicOriginal.getUnfSeqAvisa().getSeq())) {

			alterou = true;
			verificarUnidadeAviso(aelPermissaoUnidSolic);

		}

		// aelk_pus_rn.rn_pusp_ver_plantao
		if (!aelPermissaoUnidSolic.getIndPermiteSolicitarPlantao().equals(aelPermissaoUnidSolicOriginal.getIndPermiteSolicitarPlantao())) {

			alterou = true;
			verificaUnidadePlantao(aelPermissaoUnidSolic);

		}

		// aelk_pus_rn.rn_pusp_ver_transpor
		if (!aelPermissaoUnidSolic.getIndExigeTransporteO2().equals(aelPermissaoUnidSolicOriginal.getIndExigeTransporteO2())) {

			alterou = true;
			verificarUnidadeTransporte(aelPermissaoUnidSolic);

		}

		verificarUnidadePrograma(aelPermissaoUnidSolic);

		if (aelPermissaoUnidSolicOriginal.getColetaAtendeUrgencia() != null && aelPermissaoUnidSolic.getColetaAtendeUrgencia() != null
				&& !aelPermissaoUnidSolic.getColetaAtendeUrgencia().equals(aelPermissaoUnidSolicOriginal.getColetaAtendeUrgencia())) {

			alterou = true;
			verificarUnidadeColetaAtend(aelPermissaoUnidSolic);

		}

		return alterou;

	}

	private void persistirAelPermissaoUnidSolicJn(AelPermissaoUnidSolic aelPermissaoUnidSolic, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelPermissaoUnidSolicsJn jn = BaseJournalFactory.getBaseJournal(operacao, AelPermissaoUnidSolicsJn.class, servidorLogado.getUsuario());

		try {

			BeanUtils.copyProperties(jn, aelPermissaoUnidSolic);

		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ManterPermissoesUnidadesSolicitantesExceptionCode.ERRO_INSERT_JN);
		}

		getAelPermissaoUnidSolicJnDAO().persistir(jn);
		getAelPermissaoUnidSolicJnDAO().flush();

	}

	private void posAtualizarPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic, Boolean alterou) throws ApplicationBusinessException {

		persistirAelPermissaoUnidSolicJn(aelPermissaoUnidSolic, DominioOperacoesJournal.UPD);

	}

	public void removerAelPermissaoUnidSolic(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short unfSeqSolicitante) throws ApplicationBusinessException {

		AelPermissaoUnidSolic permissaoRemover = getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(emaExaSigla, emaManSeq, unfSeq,
				unfSeqSolicitante);

		getAelPermissaoUnidSolicDAO().remover(permissaoRemover);
		getAelPermissaoUnidSolicDAO().flush();
		posRemoverPermissaoUnidadeSolicitante(permissaoRemover);

	}

	private void posRemoverPermissaoUnidadeSolicitante(AelPermissaoUnidSolic aelPermissaoUnidSolic) throws ApplicationBusinessException {

		persistirAelPermissaoUnidSolicJn(aelPermissaoUnidSolic, DominioOperacoesJournal.DEL);

	}

	protected AelPermissaoUnidSolicDAO getAelPermissaoUnidSolicDAO() {
		return aelPermissaoUnidSolicDAO;
	}

	protected AelPermissaoUnidSolicJnDAO getAelPermissaoUnidSolicJnDAO() {
		return aelPermissaoUnidSolicJnDAO;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
