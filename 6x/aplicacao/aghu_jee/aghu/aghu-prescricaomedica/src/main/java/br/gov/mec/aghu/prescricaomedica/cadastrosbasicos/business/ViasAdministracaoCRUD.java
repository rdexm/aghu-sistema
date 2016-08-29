package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.MpaUsoOrdMdto;
import br.gov.mec.aghu.model.MpmItemMdtoSumario;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ViasAdministracaoCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ViasAdministracaoCRUD.class);
	
	@Inject
	private AfaViaAdministracaoDAO afaViaAdministracaoDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFarmaciaFacade iFarmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -457870837830573041L;

	protected enum ViasAdministracaoCRUDExceptionCode implements
			BusinessExceptionCode {
		AFA_00178, MENSAGEM_ERRO_REMOCAO_VIAS_ADMINISTRACAO, MPT_PMO_VAD_FK1, MPM_PMD_VAD_FK1, MPM_MBM_VAD_FK1, MPM_IMS_VAD_FK1, MPA_UOM_VAD_FK1, AFA_VAU_VAD_FK1, AFA_VAM_VAD_FK1, MENSAGEM_ERRO_SIGLA_VIAS_ADMINISTRACAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return iFarmaciaFacade;
	}

	public Long pesquisarViasAdministracaoCount(String sigla,
			String descricao, DominioSituacao situacao) {

		return this.getFarmaciaFacade()
				.pesquisarViasAdministracaoCount(sigla, descricao, situacao);
	}

	public List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao) {

		return this.getFarmaciaFacade().pesquisarViasAdministracao(
				firstResult, maxResult, orderProperty, asc, sigla, descricao,
				situacao);
	}

	public void persistirViasAdministracao(
			AfaViaAdministracao viaAdministracao,
			AfaViaAdministracao viaAdministracaoAux)
			throws ApplicationBusinessException {
		if (viaAdministracao.getCriadoEm() == null) {
			preInsertViaAdministracao(viaAdministracao);
			getFarmaciaFacade().inserirViaAdministracao(viaAdministracao);
		} else {
			preUpdateViaAdministracao(viaAdministracao, viaAdministracaoAux);
			getFarmaciaFacade().atualizarViaAdministracao(viaAdministracao, true);
		}
	}

	/**
	 * @ORADB AFAT_VAD_BRU
	 * @param viaAdministracao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preUpdateViaAdministracao(
			AfaViaAdministracao viaAdministracao,
			AfaViaAdministracao viaAdministracaoAux)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// afak_vad_rn.rn_vadp_ver_uso_nutr
		if (!viaAdministracao.getIndSituacao().isAtivo()
				&& (viaAdministracaoAux.getIndUsoNutricao().booleanValue() != viaAdministracao
						.getIndUsoNutricao().booleanValue())) {
			throw new ApplicationBusinessException(
					ViasAdministracaoCRUDExceptionCode.AFA_00178);
		}

		viaAdministracao.setServidor(servidorLogado);

	}

	/**
	 * @ORADB AFAT_VAD_BRI
	 * @param viaAdministracao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preInsertViaAdministracao(AfaViaAdministracao viaAdministracao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		validarSigla(viaAdministracao.getSigla());

		viaAdministracao.setServidor(servidorLogado);
		viaAdministracao.setCriadoEm(new Date());
	}

	public void removerViasAdministracao(final String siglaViaAdministracao,
			Integer periodo) throws BaseException {
		AfaViaAdministracao viaAdministracao = getAfaViaAdministracaoDAO().obterPorChavePrimaria(siglaViaAdministracao);
		preRemoveViasAdministracao(viaAdministracao, periodo);
		getFarmaciaFacade().removerViaAdministracao(viaAdministracao);

	}

	/**
	 * Valida se já existe via com a mesma sigla.
	 * 
	 * @param sigla
	 * @throws ApplicationBusinessException
	 */
	private void validarSigla(String sigla) throws ApplicationBusinessException {

		if (this.getFarmaciaFacade().existeSiglaCadastrada(sigla)) {

			throw new ApplicationBusinessException(
					ViasAdministracaoCRUDExceptionCode.MENSAGEM_ERRO_SIGLA_VIAS_ADMINISTRACAO);

		}

	}

	/**
	 * @ORADB AFAT_VAD_BRD
	 * @param viaAdministracao
	 * @throws ApplicationBusinessException
	 */
	private void preRemoveViasAdministracao(
			AfaViaAdministracao viaAdministracao, Integer periodo)
			throws BaseException {

		if (DateUtil.calcularDiasEntreDatas(new Date(),
				viaAdministracao.getCriadoEm()) > periodo) {
			throw new ApplicationBusinessException(
					ViasAdministracaoCRUDExceptionCode.MENSAGEM_ERRO_REMOCAO_VIAS_ADMINISTRACAO);
		}

		this.validaDelecao(viaAdministracao);

	}

	/**
	 * @ORADB CHK_ANU_TIPO_ITEM_DIETAS
	 * @param tipoDieta
	 * @throws BaseException
	 */
	public void validaDelecao(AfaViaAdministracao viaAdministracao)
			throws BaseException {

		if (viaAdministracao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		BaseListException erros = new BaseListException();
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				MptPrescricaoMedicamento.class,
				MptPrescricaoMedicamento.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.MPT_PMO_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				MpmPrescricaoMdto.class,
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.MPM_PMD_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				MpmModeloBasicoMedicamento.class,
				MpmModeloBasicoMedicamento.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.MPM_MBM_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				MpmItemMdtoSumario.class,
				MpmItemMdtoSumario.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.MPM_IMS_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				MpaUsoOrdMdto.class, MpaUsoOrdMdto.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.MPA_UOM_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				AfaViaAdmUnf.class, AfaViaAdmUnf.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.AFA_VAU_VAD_FK1));
		erros.add(this.existeItemViaAdministracao(viaAdministracao,
				AfaViaAdministracaoMedicamento.class,
				AfaViaAdministracaoMedicamento.Fields.VIA_ADMINISTRACAO,
				ViasAdministracaoCRUDExceptionCode.AFA_VAM_VAD_FK1));

		if (erros.hasException()) {
			throw erros;
		}

	}

	@SuppressWarnings("unchecked")
	private ApplicationBusinessException existeItemViaAdministracao(
			AfaViaAdministracao viaAdministracao, Class class1, Enum field,
			BusinessExceptionCode exceptionCode) {

		if (viaAdministracao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final boolean isExisteItem = getFarmaciaFacade()
				.existeItemViaAdministracao(viaAdministracao, class1, field);

		if (isExisteItem) {
			return new ApplicationBusinessException(exceptionCode);
		}

		return null;
	}
	
	public AfaViaAdministracaoDAO  getAfaViaAdministracaoDAO(){
		return afaViaAdministracaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
