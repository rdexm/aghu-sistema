package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.LocalNotificacaoOrigemRetornoVO;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class NotificacoesRN extends BaseBusiness {

	private static final long serialVersionUID = -3947047013176684273L;
	
	private static final Log LOG = LogFactory.getLog(NotificacoesRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;

	
	@EJB
	private AtualizaLocalNotificacaoOrigemRN atualizaLocalNotificacaoOrigemRN;
	
	public void validarNumeroDiasDecorridosCriacaoRegistro(Date dataCriacao, BusinessExceptionCode exceptionCode)throws BaseException {
		controleInfeccaoRN.validarNumeroDiasDecorridosCriacaoRegistro(dataCriacao, exceptionCode);
	}
	
	/**
	 * Testa se a data de encerramento é menor ou igual à data de hoje e maior ou igual a data de instalação.
	 * Senão for lança exceção. O teste só é executado se a data de encerramento de fato foi informada na tela. 
	 * @param dataEncerramento data encerramento 
	 * @param dataInstalacao data insatalacao
	 * @param exceptionCode Codigo da excecao que deve ser lancada
	 */
	public void validarDataEncerramento(Date dataEncerramento, Date dataInstalacao, BusinessExceptionCode exceptionCode) throws ApplicationBusinessException {
		if(dataEncerramento != null) {
			Date dtInstalacaoTruncada = DateUtil.truncaData(dataInstalacao);
			Date dtEncerramentoTruncada = DateUtil.truncaData(dataEncerramento);
			if(!DateUtil.validaDataMenorIgual(dtInstalacaoTruncada, new Date()) || !DateUtil.validaDataMaiorIgual(dtEncerramentoTruncada, dtInstalacaoTruncada )) {
				throw new  ApplicationBusinessException(exceptionCode);
			}
		}
	}
	
	/**
	 * ORADB PROCEDURE MCIK_RN.RN_MCIP_ATU_LOCAL
	 */
	public LocalNotificacaoOrigemRetornoVO obterLocalAtendimento(Integer atdSeq, String einTipo, DominioTipoMovimentoLocalNotificacao tipoMvt, Date dtMvt, MamRespostaNotifInfeccao pRniPnnSeq) throws ApplicationBusinessException{
		return atualizaLocalNotificacaoOrigemRN.atualizarLocalNotificacaoOrigem(atdSeq,einTipo, tipoMvt, dtMvt, pRniPnnSeq);
	}
	

}
