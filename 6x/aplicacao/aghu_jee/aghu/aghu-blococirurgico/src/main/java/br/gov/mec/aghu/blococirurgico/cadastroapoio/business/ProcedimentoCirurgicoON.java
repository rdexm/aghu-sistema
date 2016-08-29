package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responśavel pelas regras de negócio para cadastro de Procedimentos
 * Cirúrgicos (MbcProcedimentoCirurgicos).
 * 
 * @author dpacheco
 * 
 */
@Stateless
public class ProcedimentoCirurgicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProcedimentoCirurgicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;


	@EJB
	private ProcedimentoCirurgicoRN procedimentoCirurgicoRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8764355096879412157L;
	
	private static final Integer TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS = 4;

	public enum ProcedimentoCirurgicoONExceptionCode implements
			BusinessExceptionCode {
		ERRO_PROCEDIMENTO_CIRURGICO_TEMPO_MINIMO_HORAS_EXCEDIDO, ERRO_PROCEDIMENTO_CIRURGICO_TEMPO_MINIMO_MINUTOS_INVALIDO, MBC_00765, PHI_NAO_RELACIONADO;
	}

	/**
	 * Procedure
	 * 
	 * ORADB: EVT_WHEN_VALIDATE_ITEM (form)
	 * 
	 * @param tempoMinimo
	 * @throws ApplicationBusinessException
	 */
	public void validarTempoMinimoProcedimentoCirurgico(Short tempoMinimo)
			throws ApplicationBusinessException {
		StringBuffer strTempoMinimo = new StringBuffer(tempoMinimo.toString());
		
		while (strTempoMinimo.length() < TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS) {
			// Coloca zeros a esquerda
			strTempoMinimo.insert(0, "0");
		}
		
		String strHora = strTempoMinimo.substring(0, 2);
		String strMinuto = strTempoMinimo.substring(2, TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS);

		Short hora = Short.valueOf(strHora);
		Short minuto = Short.valueOf(strMinuto);

		if (hora > 48) {
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoONExceptionCode.ERRO_PROCEDIMENTO_CIRURGICO_TEMPO_MINIMO_HORAS_EXCEDIDO);
		} else if (minuto > 59) {
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoONExceptionCode.ERRO_PROCEDIMENTO_CIRURGICO_TEMPO_MINIMO_MINUTOS_INVALIDO);
		}
	}
	
	public void validarDescricaoProcedimentoCirurgico(String descricao)
			throws ApplicationBusinessException {
		
		Long countProcCirurgicoMesmaDescricao = getMbcProcedimentoCirurgicoDAO()
				.obterCountProcedimentoCirurgicoPorDescricao(descricao);
		
		if (countProcCirurgicoMesmaDescricao > 0) {
			throw new ApplicationBusinessException(
					ProcedimentoCirurgicoONExceptionCode.MBC_00765);
		}
	}
	
	public void persistirProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) throws BaseException {
		if (procedimentoCirurgico.getSeq() == null) {
			getProcedimentoCirurgicoRN().inserirProcedimentoCirurgico(procedimentoCirurgico);
		} else {
			getProcedimentoCirurgicoRN().atualizarProcedimentoCirurgico(procedimentoCirurgico);
		}
	}
	
	public void validarProcedimentoHospitarInternoRelacionado(Integer numeroPHI,
			List<FatConvGrupoItemProced> convGrupoItemProcedList) throws ApplicationBusinessException {
		if(convGrupoItemProcedList != null && !convGrupoItemProcedList.isEmpty() && 
				numeroPHI == null) {
			throw new ApplicationBusinessException(ProcedimentoCirurgicoONExceptionCode.PHI_NAO_RELACIONADO,Severity.ERROR);
		}
		
	}
	
	protected ProcedimentoCirurgicoRN getProcedimentoCirurgicoRN() {
		return procedimentoCirurgicoRN;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}	

}
