package br.gov.mec.aghu.internacao.estornar.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EstornarInternacaoON extends BaseBusiness {


@EJB
private EstornarInternacaoRN estornarInternacaoRN;

private static final Log LOG = LogFactory.getLog(EstornarInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6550921929965054189L;

	private enum EstornarInternacaoONExceptionCode implements
	BusinessExceptionCode {
		AIN_00386, ERRO_ESTORNO_INTERNACAO
	}
	
	/**
	 * Método que verifica as regras necessárias antes de estornar a internação
	 * @param intSeq, justificativa
	 *  
	 */
	public boolean verificarRegrasAntesEstornar(Integer intSeq, String justificativa) throws BaseException{
		boolean retorno = false;
		AghAtendimentos atendimentoExames = getEstornarInternacaoRN().verificarExame(intSeq);

		Integer vAtdSeq = atendimentoExames.getSeq();
		DominioSituacaoSumarioAlta vIndSitSumarioAlta = atendimentoExames.getIndSitSumarioAlta();
		
		getEstornarInternacaoRN().verificarEncerramentoConta(intSeq);
		getEstornarInternacaoRN().verificarPrescricao(intSeq, vAtdSeq, vIndSitSumarioAlta);
		getEstornarInternacaoRN().varificarDiagnosticos(atendimentoExames);
		getEstornarInternacaoRN().verificarControlePaciente(intSeq, atendimentoExames.getPaciente());
		if (StringUtils.isBlank(justificativa)){
			throw new ApplicationBusinessException(EstornarInternacaoONExceptionCode.AIN_00386);
		}
		retorno = true;
		return retorno;
		
	}
	
	/**
	 * Método que realiza o estorno da internação
	 * @param internacao
	 *  
	 */
	public void estornarInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		logInfo("Iniciando estorno da internação com SEQ: "+internacao.getSeq());
		try{
			getAinInternacaoDAO().atualizar(internacao);
						
			if (internacao.getLeito() != null){
				if (internacao.getTipoAltaMedica() == null){
					logInfo("Desocupando leito: "+internacao.getLeito().getLeitoID());
					getEstornarInternacaoRN().desocuparLeito(internacao.getLeito(), internacao);
				}
				else{
					logInfo("Atualizando leito: "+internacao.getLeito().getLeitoID());
					getEstornarInternacaoRN().atualizarLeito(internacao.getLeito());
				}
			}
			logInfo("Apagando internação com SEQ: "+internacao.getSeq());
			getEstornarInternacaoRN().deletarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor);
		}
		catch(ApplicationBusinessException e){
			logError(e.getMessage(), e);
			throw e;
		}
		catch(Exception ex){
			logError(ex.getMessage(), ex);
			throw new ApplicationBusinessException(
					EstornarInternacaoONExceptionCode.ERRO_ESTORNO_INTERNACAO,
					ex.getMessage());
		}
	}

	protected EstornarInternacaoRN getEstornarInternacaoRN(){
		return estornarInternacaoRN;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
}
