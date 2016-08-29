package br.gov.mec.aghu.prescricaomedica.business;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;

/**
 * @author tfelini
 */
@Stateless
public class ManterSumarioSchedulerRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterSumarioSchedulerRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9108889186938858982L;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void gerarDadosSumarioPrescricaoMedica(String cron, Date dataInicio, Date dataFim, RapServidores servidorLogado, String nomeProcessoQuartz) throws ApplicationBusinessException {
		AghJobDetail job = null;
		if (nomeProcessoQuartz != null) {
			job = getSchedulerFacade().obterAghJobDetailPorNome(nomeProcessoQuartz);
			if (job != null) {
				boolean iniciouExecucao = getSchedulerFacade().iniciarExecucao(job);
				if (!iniciouExecucao) {
					LOG.info("gerarDadosSumarioPrescricaoMedica nao conseguiu iniciar execucao do jobDetail!");
				}
			}
		}
		
		if (dataInicio == null) {
			dataInicio = getDataInicio(getDiasAtras());
		}
		if (dataFim == null) {
			dataFim = getDataFim(getDiasAtras());
		}
		
		if (job != null) {
			getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz
					+" - Dt ini: " + DateFormatUtil.formataTimeStamp(dataInicio) 
					+ " Dt Fim: " + DateFormatUtil.formataTimeStamp(dataFim)
			);
		}
		
		List<AghAtendimentos> atendimentos = this.getAghuFacade().buscaAtendimentosSumarioPrescricao(dataInicio, dataFim);

		if (job != null) {
			getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz+" - total de atendimentos: " + atendimentos.size());
			getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz+" - atendimentos: " + atendimentos);
		}

		for (AghAtendimentos atendimento : atendimentos) {
			try {
				IPrescricaoMedicaBeanFacade ejbPrescricaoMedica = ServiceLocator.getBean(IPrescricaoMedicaBeanFacade.class, "aghu-prescricaomedica");
				// controle transacional por EJB e requires_new				
				ejbPrescricaoMedica.geraDadosSumarioPrescricao(atendimento.getSeq(), DominioTipoEmissaoSumario.I, servidorLogado);
				
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
							
				if (job != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos);
					ex.printStackTrace(ps);
					getSchedulerFacade().adicionarLog(job
							, ex.getMessage() + "\n" + baos.toString()
					);
				}
			}
		}
		
		ManterSumarioSchedulerRN sumarioSchedulerRN = ctx.getBusinessObject(ManterSumarioSchedulerRN.class);
		sumarioSchedulerRN.enviarEmailRotinaGeracaoDadosSucesso();
		
		if (job != null) {
			String mensagem = "Quartz Task de "+nomeProcessoQuartz+" - finalizou com: SUCESSO";
			getSchedulerFacade().finalizarExecucao(job, DominioSituacaoJobDetail.C, mensagem);
			
		}

	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void enviarEmailRotinaGeracaoDadosSucesso() throws ApplicationBusinessException {
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		Date dataAtual = new Date();
		List<String> emailParaList = new ArrayList<String>();
		AghParametros emailDe = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		AghParametros emailGeraSumario = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_GERA_SUMARIO);
		
		if (emailDe != null && StringUtils.isNotBlank(emailDe.getVlrTexto())
				&& emailGeraSumario != null && StringUtils.isNotBlank(emailGeraSumario.getVlrTexto())) {
			StringTokenizer emailPara =  new StringTokenizer(emailGeraSumario.getVlrTexto(),";");
			while (emailPara.hasMoreTokens()) {
				emailParaList.add(emailPara.nextToken().trim().toLowerCase());
			}
			String conteudoEmail = "<font size='2' face='Courier New'> Rotina de geração dos dados do sumário de prescrição concluída com sucesso em " + sdf2.format(dataAtual);
			this.getEmailUtil().enviaEmail(emailDe.getVlrTexto(), emailParaList, null, "Geração dos dados do sumário de prescrição", conteudoEmail);
		} else {
			LOG.warn("Manter Sumario Prescricao Medica: enviar email rotina geracao dados - nao enviou e-mail por falta de valores nos parametros: P_AGHU_EMAIL_ENVIO, P_AGHU_EMAIL_GERA_SUMARIO");
		}
	}

	protected ISchedulerFacade getSchedulerFacade() {
		return this.schedulerFacade;
	}
	
	protected ManterSumarioRN getManterSumarioRN() {
		return new ManterSumarioRN();
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
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
