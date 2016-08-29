package br.gov.mec.aghu.administracao.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;



public class ReagendamentoJobDetailController extends ActionController {
	
	private static final long serialVersionUID = 9046348071668158826L;
	private static final Log LOG = LogFactory.getLog(ReagendamentoJobDetailController.class);

	private static final String VOLTAR_JOBDETAILIST = "jobDetailList.xhtml";
	
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	private AghJobDetail jobDetail;

	
	private String cronExpression;
	
	private Date dataAgendada;	
	
	private String horaServidor;

	
	
	
	@PostConstruct
	protected void inicializar() {
		// iniciar valores apos a construcao, quando necessario.
		LOG.info("inicializar");
	}
	
	public void initForm() throws BaseException {
	 

	 

		//this.jobDetail = this.schedulerFacade.obterAghJobDetailPorId(this.jobDetail.getSeq());
		initHoraServidor();
	
	}
	
	
	public String voltar() {
		initFormValues();
		return VOLTAR_JOBDETAILIST;
	}
	
	public void reAgendar() {
		try {
			initHoraServidor();
			if (jobDetail == null || jobDetail.getSeq() == null) {
				// TAREFA_DEVE_SER_INFORMADA
				this.apresentarMsgNegocio(Severity.WARN, "Uma Tarefa deve ser Selecionada.");
				return;
			}
			if (StringUtils.isBlank(cronExpression) && dataAgendada == null) {
				// UM_DOS_CAMPOS_EH_OBRIGATORIO=Um dos campos é obrigatório: ({0})
				this.apresentarMsgNegocio(Severity.WARN, "Um dos campos é obrigatório: (Data Agendada ou Expressão Cron).");
				return; 
			}
			
			schedulerFacade.reAgendar(jobDetail, cronExpression, dataAgendada);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_RE_AGENDAMENTO", jobDetail.getNomeProcesso());
			
			initFormValues();
			initHoraServidor();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	private void initHoraServidor() {
		this.setHoraServidor(DateFormatUtil.formataTimeStamp(new Date()));
	}
	
	private void initFormValues() {
		jobDetail = null;
		cronExpression = null;
		dataAgendada = null;
		
	}
	
	
	/** GET/SET **/

	public AghJobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(AghJobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}


	
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getDataAgendada() {
		return dataAgendada;
	}

	public void setDataAgendada(Date dataAgendada) {
		this.dataAgendada = dataAgendada;
	}

	public void setHoraServidor(String horaServidor) {
		this.horaServidor = horaServidor;
	}

	public String getHoraServidor() {
		return horaServidor;
	}
	
	

}
