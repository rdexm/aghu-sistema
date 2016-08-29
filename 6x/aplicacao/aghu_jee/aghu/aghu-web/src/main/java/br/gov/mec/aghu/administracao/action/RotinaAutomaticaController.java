package br.gov.mec.aghu.administracao.action;

import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;



public class RotinaAutomaticaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(RotinaAutomaticaController.class);
	private static final long serialVersionUID = -8695760847263822289L;
	
	private static final String VOLTAR_JOBDETAILIST = "jobDetailList.xhtml";
	

	
	
	
	
	private AghJobDetail jobSelecionado;
	
	private IAutomaticJobEnum enumSelecionado;
	
	private String cronExpression;
	
	private Date dataAgendada;
	
	private String horaServidor;
	
	@EJB
	private ISchedulerFacade schedulerFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;



	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void initForm() {
	 

	 

		initHoraServidor();
	
	}
	
	
	public String cancelar() {
		initFormValues();
		return VOLTAR_JOBDETAILIST;
	}
	
	
	
	
	
	public void agendar() {
		try {
			if (enumSelecionado == null || enumSelecionado.getTriggerName() == null) {
				apresentarMsgNegocio(Severity.WARN, "ERRO_TAREFA_NAO_INFORNADA");
				return;
			}
			if (StringUtils.isBlank(cronExpression) && dataAgendada == null) {
				apresentarMsgNegocio(Severity.WARN, "ERRO_DATA_OU_CRON_OBRIGATORIOS");
				return; 
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			schedulerFacade.agendarRotinaAutomatica(enumSelecionado, cronExpression, dataAgendada, nomeMicrocomputador, servidorLogado);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_AGENDAMENTO", enumSelecionado.toString());
			
			// Reinicia a paginação.
			//reiniciarPaginator(JobDetailPaginatorController.class);
			
			initHoraServidor();
			initFormValues();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}


	private void initHoraServidor() {
		this.setHoraServidor(DateFormatUtil.formataTimeStamp(new Date()));
	}
	
	private void initFormValues() {
		this.enumSelecionado = null;
		this.cronExpression = null;
		this.dataAgendada = null;
	}

	
	

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setDataAgendada(Date dataAgendada) {
		this.dataAgendada = dataAgendada;
	}

	public Date getDataAgendada() {
		return dataAgendada;
	}
	
	public void setJobSelecionado(AghJobDetail jobSelecionado) {
		this.jobSelecionado = jobSelecionado;
	}
	
	public AghJobDetail getJobSelecionado() {
		return jobSelecionado;
	}
	
	public void setEnumSelecionado(IAutomaticJobEnum enumSelecionado) {
		this.enumSelecionado = enumSelecionado;
	}
	
	public IAutomaticJobEnum getEnumSelecionado() {
		return enumSelecionado;
	}
	
	public void setHoraServidor(String horaServidor) {
		this.horaServidor = horaServidor;
	}
	
	public String getHoraServidor() {
		return horaServidor;
	}
	
	

	
}
