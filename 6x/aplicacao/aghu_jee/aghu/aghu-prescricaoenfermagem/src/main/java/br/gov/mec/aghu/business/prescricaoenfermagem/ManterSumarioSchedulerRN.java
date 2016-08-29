package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterSumarioSchedulerRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterSumarioSchedulerRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

		
	private static final long serialVersionUID = -4684036820515323001L;

	/**
	 * Método que gera dados para sumário de prescrição enfermagem
	 * @param cron
	 * @param dataInicio
	 * @param dataFim
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void gerarDadosSumarioPrescricaoEnfermagem(String cron, Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		if (dataInicio == null) {
			dataInicio = getDataInicio(getDiasAtras());
		}
		if (dataFim == null) {
			dataFim = getDataFim(getDiasAtras());
		}
		
//		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
//		Date dataAtual = new Date();

		List<String> emailParaList = new ArrayList<String>();
//		AghParametros emailDe = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		AghParametros emailGeraSumario = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_GERA_SUMARIO);
		
		if (emailGeraSumario != null && emailGeraSumario.getVlrTexto() != null) {
			StringTokenizer emailPara =  new StringTokenizer(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_GERA_SUMARIO).getVlrTexto(),";");
	
			while (emailPara.hasMoreTokens()) {
				emailParaList.add(emailPara.nextToken().trim().toLowerCase());
			}
		}
//		String conteudoEmail = "";
		AghParametros enfermagemAtivo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_ENFERMAGEM_ATIVO);
		if(enfermagemAtivo.getVlrNumerico().equals(BigDecimal.ONE)){
		
			List<AghAtendimentos> atendimentos = getAghuFacade().buscaAtendimentosSumarioPrescricao(dataInicio, dataFim);
			for (AghAtendimentos atendimento : atendimentos){
				this.getPrescricaoEnfermagemFacade().geraDadosSumarioPrescricaoEnfermagem(atendimento.getSeq(), DominioTipoEmissaoSumario.I);
			}
//			conteudoEmail = "<font size='2' face='Courier New'> Rotina de geração dos dados do sumário de prescrição enfermagem concluída com sucesso em " + sdf2.format(dataAtual);
//		}
//		else{
//			conteudoEmail = "<font size='2' face='Courier New'> Rotina de geração dos dados do sumário de prescrição enfermagem não executada em " + sdf2.format(dataAtual) + ", pois o parâmetro do modulo de enfermagem está marcado como inativo.";
		}


//		/this.getAghuFacade().enviaEmail(emailDe.getVlrTexto(), emailParaList, null, "Geração dos dados do sumário de prescrição enfermagem", conteudoEmail);
	}
	
	
	private Date getDataFim(Integer diasAtras) {
		Calendar dataFim = Calendar.getInstance();
		dataFim.add(Calendar.DATE, - diasAtras);
		dataFim.set(Calendar.HOUR_OF_DAY, 23);
		dataFim.set(Calendar.MINUTE, 59);
		dataFim.set(Calendar.SECOND, 59);
		dataFim.set(Calendar.MILLISECOND, 999);
		
		return dataFim.getTime();
	}

	private Date getDataInicio(Integer diasAtras) {
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.DATE, - diasAtras);
		dataInicio.set(Calendar.HOUR_OF_DAY, 0);
		dataInicio.set(Calendar.MINUTE, 0);
		dataInicio.set(Calendar.SECOND, 0);
		dataInicio.set(Calendar.MILLISECOND, 0);
		
		return dataInicio.getTime();
	}

	private Integer getDiasAtras() throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA).getVlrNumerico().intValue();
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}
}
