package br.gov.mec.aghu.exames.agendamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultaHorarioLivreRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConsultaHorarioLivreRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2501981500849148329L;
	
	public enum ConsultaHorarioLivreRNExceptionCode implements BusinessExceptionCode {
		AEL_01512;
	}
	
	/**
	 * FUNCTION
	 * ORADB AELC_BUSCA_RESP_COL
	 */
	public Boolean verificarResponsavelColetaExame(String sigla, Integer matExame){
		List<AelTipoAmostraExame> lista = this.getAelTipoAmostraExameDAO().pesquisarResponsavelColeta(sigla, matExame);
		if(lista!=null && lista.size()>0){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * FUNCTION
	 * ORADB AELC_BUSCA_ORIGEM_HR
	 *  
	 */
	public Short obterTipoMarcacao(DominioOrigemAtendimento origem, Short unfSeq) throws ApplicationBusinessException{
		if(this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.SMO)){
			AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_FUN);
			return parametroTipoMarcacao.getVlrNumerico().shortValue();
		}
		
		if(origem != null){
			if(origem.equals(DominioOrigemAtendimento.A)){
				AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_AMB);
				return parametroTipoMarcacao.getVlrNumerico().shortValue();
			}
			if(origem.equals(DominioOrigemAtendimento.I)){
				AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_INT);
				return parametroTipoMarcacao.getVlrNumerico().shortValue();
			}
			if(origem.equals(DominioOrigemAtendimento.X)){
				AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_PEX);
				return parametroTipoMarcacao.getVlrNumerico().shortValue();
			}
			if(origem.equals(DominioOrigemAtendimento.U)){
				AghParametros parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_URG);
				return parametroTipoMarcacao.getVlrNumerico().shortValue();
			}
		}
		return 0;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AELP_AGENDA_EXAMES 
	 * 
	 * Verifica se o itemSolicitacaoExame já possui horário marcado.
	 * 
	 * @param itemHorarioAgendadoVO
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void validarTempoExame(ItemHorarioAgendadoVO itemHorarioAgendadoVO) throws ApplicationBusinessException {
		if (itemHorarioAgendadoVO.getItemHorarioAgendadoId() != null) {
			throw new ApplicationBusinessException(ConsultaHorarioLivreRNExceptionCode.AEL_01512);
		}
	}
	
	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
}
