package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcHorarioTurnoCirgON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcHorarioTurnoCirgON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;


	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IAdministracaoFacade iAdministracaoFacade;

	@EJB
	private MbcHorarioTurnoCirgRN mbcHorarioTurnoCirgRN;

	private static final long	serialVersionUID	= 1779953909111759691L;

	protected enum MbcHorarioTurnoCirgONExceptionCode implements BusinessExceptionCode {
		MBC_00075, MBC_00201, ERRO_UNIDADE_FUNCIONAL_CIRURGIA, OFG_00005, CAMPO_OBRIGATORIO;
	}

	public void persistirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg,
			final String nomeMicrocomputador) throws BaseException {
		this.validarCamposObrigatorios(mbcHorarioTurnoCirg);
		this.verificarAntesInserir(mbcHorarioTurnoCirg, nomeMicrocomputador);
		this.getMbcHorarioTurnoCirgRN().persistirMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
	}

	protected boolean validarCamposObrigatorios(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg) throws BaseException {
		boolean validou = true;
		if(mbcHorarioTurnoCirg.getAghUnidadesFuncionais() == null) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, getMessage("LABEL_UNIDADE_CIRURGICA_TURNO"));
		}
		if(mbcHorarioTurnoCirg.getMbcTurnos() == null) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, getMessage("LABEL_TIPO_TURNO"));
		}
		if(mbcHorarioTurnoCirg.getHorarioInicial() == null) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, getMessage("LABEL_HORARIO_INICIAL_TURNO_UNIDADE_CIRURGICA"));
		}
		if(mbcHorarioTurnoCirg.getHorarioFinal() == null) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.CAMPO_OBRIGATORIO, getMessage("LABEL_HORARIO_FINAL_TURNO_UNIDADE_CIRURGICA"));
		}
		
		return validou;
	}
	
	protected String getMessage(String message) {
		return getResourceBundleValue(message);
	}

	/**
	 * @ORADB MBCF_MANTER_TURNOS
	 */
	private void verificarAntesInserir(MbcHorarioTurnoCirg mbcHorarioTurnoCirg, final String nomeMicrocomputador) throws BaseException {
		verificarExistenciaTurno(mbcHorarioTurnoCirg);
		verificarMesmoHorario(mbcHorarioTurnoCirg);
		verificarUnidadeFuncional(mbcHorarioTurnoCirg, nomeMicrocomputador);
	}

	protected void verificarUnidadeFuncional(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg, final String nomeMicrocomputador) throws BaseException {
		final AghMicrocomputador micro = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, null);

		if (micro != null && micro.getAghUnidadesFuncionais() != null
				&& this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(micro.getAghUnidadesFuncionais().getSeq(),
						ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)
				&& !mbcHorarioTurnoCirg.getAghUnidadesFuncionais().getSeq().equals(micro.getAghUnidadesFuncionais().getSeq())) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.ERRO_UNIDADE_FUNCIONAL_CIRURGIA, micro.getAghUnidadesFuncionais().getDescricao());
		}		
	}

	protected void verificarExistenciaTurno(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) throws BaseException {
		if (mbcHorarioTurnoCirg.getVersion() == null) {
			final MbcHorarioTurnoCirg jaExiste = this.getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(
					new MbcHorarioTurnoCirgId(mbcHorarioTurnoCirg.getAghUnidadesFuncionais().getSeq(), mbcHorarioTurnoCirg.getId().getTurno()));
			if (jaExiste != null && jaExiste.getId() != null) {
				throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.MBC_00075);
			}
		}
	}
	
	protected void verificarMesmoHorario(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) throws BaseException {
		if (mbcHorarioTurnoCirg.getHorarioInicial().getTime() == mbcHorarioTurnoCirg.getHorarioFinal().getTime()) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.MBC_00201);
		}
	}

	public void excluirMbcHorarioTurnoCirg(final MbcHorarioTurnoCirgId id) throws ApplicationBusinessException {
		if (this.getMbcHorarioTurnoCirgDAO().existeCaracteristicaSalaCirgs(id)) {
			throw new ApplicationBusinessException(MbcHorarioTurnoCirgONExceptionCode.OFG_00005, "Hor\u00E1rio Turno Cirgs", "Caracter\u00EDstica Sala Cirg");
		}
		this.getMbcHorarioTurnoCirgRN().excluirMbcHorarioTurnoCirg(this.getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(id));
	}

	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}

	protected MbcHorarioTurnoCirgRN getMbcHorarioTurnoCirgRN() {
		return mbcHorarioTurnoCirgRN;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

	protected IAdministracaoFacade getAdministracaoFacade() {
		return iAdministracaoFacade;
	}

}
