package br.gov.mec.aghu.internacao.leitos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LiberaLeitoLimpezaRN extends BaseBusiness {


@EJB
private ExtratoLeitoON extratoLeitoON;

private static final Log LOG = LogFactory.getLog(LiberaLeitoLimpezaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@Inject
private AinInternacaoDAO ainInternacaoDAO;

@Inject
private AinExtratoLeitosDAO ainExtratoLeitosDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 111315374777258084L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum LiberaLeitoLimpezaRNExceptionCode implements
			BusinessExceptionCode {
		AIN_00418, AIN_00798, AIN_00429
	}

	/**
	 * ORADB FUNCTION AINC_DATA_BLQ_LTO
	 * 
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	public Date recuperarDataBloqueio(String leito) {
		return getAinExtratoLeitosDAO().recuperarDataBloqueio(leito);
	}

	/**
	 * Método responsável pela liberação do leito.
	 * 
	 * @dbtables AghParametros select
	 * @dbtables AinInternacao select
	 * @dbtables AipPacientes select
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	public void liberaLeitoLimpeza(AinLeitos leito) throws ApplicationBusinessException {

		// Busca em AGH_PARAMETRO
		AghParametrosVO aghParametroVO = new AghParametrosVO();
		AinTiposMovimentoLeito tiposMovimentoLeito;

		aghParametroVO.setNome("P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA");
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		if (aghParametroVO.getMsg() != null) {
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00418);
		}

		aghParametroVO.setNome("P_COD_MVTO_LTO_OCUPADO");
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		if (aghParametroVO.getMsg() != null) {
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00418);
		}

		aghParametroVO.setNome("P_COD_MVTO_LTO_DESOCUPADO");
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		if (aghParametroVO.getMsg() != null) {
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00418);
		}

		// Valida Prontuário
		AinInternacao internacao = this.ainInternacaoDAO.obterInternacaoPorLeito(leito.getLeitoID());
				
		if (internacao != null
				&& internacao.getPaciente().getProntuario() == null) {
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00798);
		}

		tiposMovimentoLeito = getCadastrosBasicosInternacaoFacade()
				.obterTipoSituacaoLeito(aghParametroVO.getVlrNumerico()
						.shortValue());

		try {
			getExtratoLeitoON().inserirExtrato(leito, tiposMovimentoLeito, null,
					null, null, null, null, null, null, null, null, null);
		} catch (ApplicationBusinessException e) {
			logError(e);
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00429);
		}

	}

	/**
	 * Método responsável pelo bloqueio do leito.
	 * 
	 * @dbtables AghParametros select
	 * @dbtables AinInternacao select
	 * @dbtables AipPacientes select
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	public void bloquearLeitoLimpeza(AinLeitos leito, AinInternacao internacao)
			throws ApplicationBusinessException {

		AinTiposMovimentoLeito tiposMovimentoLeito;
		AghParametrosVO aghParametroVO = new AghParametrosVO();
		String justificativa = null;

		// Busca em AGH_PARAMETRO
		aghParametroVO.setNome("P_COD_MVTO_LTO_PERTENCES_PAC");
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		if (aghParametroVO.getMsg() != null) {
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00418);
		}
		
		if (leito.getTipoMovimentoLeito().getCodigo() == aghParametroVO.getVlrNumerico().shortValue()) {
			//Bloqueio Limpeza
			aghParametroVO.setNome("P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA");
			this.getParametroFacade().getAghpParametro(aghParametroVO);
			if (aghParametroVO.getMsg() != null) {
				throw new ApplicationBusinessException(
						LiberaLeitoLimpezaRNExceptionCode.AIN_00418);
			}
			tiposMovimentoLeito = getCadastrosBasicosInternacaoFacade().obterTipoSituacaoLeito(aghParametroVO.getVlrNumerico().shortValue());
		} else {
			//Bloqueio Pertences Pacientes
			tiposMovimentoLeito = getCadastrosBasicosInternacaoFacade().obterTipoSituacaoLeito(aghParametroVO.getVlrNumerico().shortValue());
			justificativa = "Pertences Paciente";
		}
		
		try {
			getExtratoLeitoON().inserirExtrato(leito, tiposMovimentoLeito, null,
					null, justificativa, null, null, null, internacao, null,
					null, null);
		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					LiberaLeitoLimpezaRNExceptionCode.AIN_00429);
		}
	}
	
	protected ExtratoLeitoON getExtratoLeitoON(){
		return extratoLeitoON;
	}
	
	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO(){
		return ainExtratoLeitosDAO;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

}
