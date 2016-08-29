package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.AutomaticJobEnum;
import br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.quartz.QuartzUtils;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ProcessamentoMensalController extends ActionController {

	private static final String VISUALIZAR_HISTORICO_PROCESSAMENTO_CUSTO = "visualizarHistoricoProcessamentoCusto";

	private static final String PROCESSAMENTO_MENSAL_LIST = "processamentoMensalList";

	private static final Log LOG = LogFactory.getLog(ProcessamentoMensalController.class);

	private static final long serialVersionUID = -8346981833568307417L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ISchedulerFacade schedulerFacade;

	private Integer seqProcessamentoCusto;
	private Integer seqProcessamentoPasso;
	private SigProcessamentoCusto processamentoCusto;
	private List<SigPassos> passosList;
	private List<SigProcessamentoPassos> processamentoPassosList;
	private Boolean novoProcessamento;
	private Boolean resolverPendencia;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		
		this.resolverPendencia = false;
		//Recebe o parâmetro pela url quando vier direto da central de pendência
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if(request.getParameter("seqProcessamentoCusto") != null && request.getParameter("cxtSeq") != null){
			this.seqProcessamentoCusto = Integer.valueOf(request.getParameter("seqProcessamentoCusto"));
			resolverPendencia = true;
		}
		
		if (this.seqProcessamentoCusto != null) {
			this.preparaEdicaoProcessamentoCusto();
		} else {
			this.preparaNovoProcessamentoCusto();
			this.pesquisarListaPassosProcessamento();
		}
	}

	private void preparaNovoProcessamentoCusto() {
		this.processamentoCusto = new SigProcessamentoCusto();
		this.processamentoCusto.setCompetencia(obterDataCompetenciaDefault());
		this.processamentoCusto.setDataInicio(DateUtil.obterDataInicioCompetencia(this.processamentoCusto.getCompetencia()));
		this.processamentoCusto.setDataFim(DateUtil.obterDataFimCompetencia(this.processamentoCusto.getCompetencia()));
		this.setNovoProcessamento(Boolean.TRUE);
	}

	private void preparaEdicaoProcessamentoCusto() {
		this.processamentoCusto = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.seqProcessamentoCusto);
		this.custosSigProcessamentoFacade.refreshProcessamentoCusto(this.processamentoCusto);
		this.processamentoPassosList = this.custosSigProcessamentoFacade.pesquisarSigProcessamentoPassos(this.processamentoCusto);
		this.setNovoProcessamento(Boolean.FALSE);
	}

	private Date obterDataCompetenciaDefault() {
		Calendar dtCompetencia = Calendar.getInstance();

		dtCompetencia.setTime(new Date());
		dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
		dtCompetencia.set(Calendar.HOUR_OF_DAY, 0);
		dtCompetencia.set(Calendar.MINUTE, 0);
		dtCompetencia.set(Calendar.SECOND, 0);
		dtCompetencia.set(Calendar.MILLISECOND, 0);
		dtCompetencia.add(Calendar.MONTH, -1);

		return dtCompetencia.getTime();
	}

	private void pesquisarListaPassosProcessamento() {
		this.passosList = custosSigProcessamentoFacade.pesquisarListaPassosProcessamento();
	}

	public String calcularTempoExecucao(SigProcessamentoPassos passo){
		return this.custosSigProcessamentoFacade.calcularTempoExecucao(processamentoCusto, passo);
	}
	
	public String executar() {
		try {
			this.custosSigProcessamentoFacade.incluirProcessamentoCusto(processamentoCusto);
			Date dataAgenda = agendarProcessamento();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AGEND_PROCESSAMENTO", processamentoCusto.getCompetenciaMesAno(), dataAgenda);
			return this.cancelar();

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (IllegalArgumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_AGEND_PROCESSAMENTO", processamentoCusto.getCompetenciaMesAno());
		}

		return null;
	}

	public void atualizarPeriodoCompetencia() {
		if (this.processamentoCusto != null && this.processamentoCusto.getCompetencia() != null) {
			this.processamentoCusto.setDataInicio(DateUtil.obterDataInicioCompetencia(this.processamentoCusto.getCompetencia()));
			this.processamentoCusto.setDataFim(DateUtil.obterDataFimCompetencia(this.processamentoCusto.getCompetencia()));
		}
	}

	public String cancelar() {
		this.seqProcessamentoCusto = null;
		return PROCESSAMENTO_MENSAL_LIST;
	}
	
	public String visualizarHistoricoProcesamentoParaPasso(){
		return VISUALIZAR_HISTORICO_PROCESSAMENTO_CUSTO;
	}

	private Date agendarProcessamento() throws BaseException {

		IAutomaticJobEnum automaticJobEnum = AutomaticJobEnum.SCHEDULERINIT_PROCESSARCUSTODIARIOAUTOMATIZADO;

		Calendar dtAgendamento = Calendar.getInstance();

		dtAgendamento.setTime(new Date());

		// TODO -- Descomentar após fase de testes,
		// onde o agendamento ficará fixo para o turno da noite as 00:30

		// Dt Atual + 1 Dia
		// dtAgendamento.add(Calendar.DAY_OF_MONTH, +1);
		// dtAgendamento.set(Calendar.HOUR_OF_DAY, 0);
		// dtAgendamento.set(Calendar.MINUTE, 30);
		// dtAgendamento.set(Calendar.SECOND, 0);
		// dtAgendamento.set(Calendar.MILLISECOND, 0);

		// TODO -- Comentar ou excluir após fase de testes para respeitar o
		// agendamento acima.
		dtAgendamento.add(Calendar.MINUTE, +1);// dtAgendamento.add(Calendar.MINUTE,
												// +5);

		String cron = QuartzUtils.dataAsCronExpression(dtAgendamento.getTime());

		automaticJobEnum.setCron(cron);

		LOG.debug(" # Agendando manualmente a rotina de processamento mensal dos custos por atividade...");

		this.schedulerFacade.agendarRotinaAutomatica(automaticJobEnum, automaticJobEnum.getCron(), null, null, processamentoCusto.getRapServidores());

		return dtAgendamento.getTime();
	}
	
	public Boolean exibirReprocessarED() {
		Calendar dataAtual = Calendar.getInstance();
		Calendar competencia = Calendar.getInstance();
		competencia.setTime(processamentoCusto.getCompetencia());
		if(competencia.get(Calendar.MONTH) == dataAtual.get(Calendar.MONTH)
				&& competencia.get(Calendar.YEAR) == dataAtual.get(Calendar.YEAR)) {
			int ultimoDiaDoMes = competencia.getActualMaximum(Calendar.DAY_OF_MONTH);
			if(ultimoDiaDoMes == competencia.get(Calendar.DAY_OF_MONTH)) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public String fecharProcessamento() {
		try {
			this.processamentoCusto.setIndSituacao(DominioSituacaoProcessamentoCusto.F);
			this.custosSigProcessamentoFacade.alterarProcessamentoCusto(this.getProcessamentoCusto());
			this.custosSigProcessamentoFacade.removerHistoricoProcessamento(this.getProcessamentoCusto().getSeq());	
			this.custosSigCadastrosBasicosFacade.adicionarPendenciaProcessamentoHomologado(this.getProcessamentoCusto().getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_FECHAMENTO_PROCESSAMENTO",
					processamentoCusto.getCompetenciaMesAno());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} 
		return this.cancelar();
	}

	public DominioSituacaoProcessamentoCusto[] listarDominioSituacaoProcessamentoProcessadoCusto(){
		List<DominioSituacaoProcessamentoCusto> list = new ArrayList<DominioSituacaoProcessamentoCusto>();
		for(DominioSituacaoProcessamentoCusto d : DominioSituacaoProcessamentoCusto.values()){
			if(d == DominioSituacaoProcessamentoCusto.P || d == DominioSituacaoProcessamentoCusto.F){
				list.add(d);
			}
		}
		return list.toArray(new DominioSituacaoProcessamentoCusto[]{});
	}
	
	public String executarReprocessamento() {

		try {
			this.custosSigProcessamentoFacade.reprocessarProcessamentoCusto(processamentoCusto);
			Date dataAgenda = this.agendarProcessamento();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AGEND_PROCESSAMENTO", processamentoCusto.getCompetenciaMesAno(),dataAgenda);
			return this.cancelar();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (IllegalArgumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_AGEND_PROCESSAMENTO", processamentoCusto.getCompetenciaMesAno());
		}

		return null;
	}

	public void verificaExecucaoAgendamento() {
		this.openDialog("modalAgendExecProcessamentoWG");
	}

	public void verificaExecucaoSalvarFechamento() {
		if(processamentoCusto.getIndSituacao()  == DominioSituacaoProcessamentoCusto.F){
			this.openDialog("modalSalvarProcessamentoWG");
		}
	}

	public void verificaExecucaoReprocessamento() {
		this.openDialog("modalReprocessamentoWG");
	}

	// getters and setters
	public SigProcessamentoCusto getProcessamentoCusto() {
		return processamentoCusto;
	}

	public void setProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.processamentoCusto = processamentoCusto;
	}

	public List<SigPassos> getPassosList() {
		return passosList;
	}

	public void setPassosList(List<SigPassos> passosList) {
		this.passosList = passosList;
	}

	public Integer getSeqProcessamentoCusto() {
		return seqProcessamentoCusto;
	}

	public void setSeqProcessamentoCusto(Integer seqProcessamentoCusto) {
		this.seqProcessamentoCusto = seqProcessamentoCusto;
	}

	public List<SigProcessamentoPassos> getProcessamentoPassosList() {
		return processamentoPassosList;
	}

	public void setProcessamentoPassosList(List<SigProcessamentoPassos> processamentoPassosList) {
		this.processamentoPassosList = processamentoPassosList;
	}

	public Boolean getNovoProcessamento() {
		return novoProcessamento;
	}

	public void setNovoProcessamento(Boolean novoProcessamento) {
		this.novoProcessamento = novoProcessamento;
	}

	public Boolean getResolverPendencia() {
		return resolverPendencia;
	}

	public void setResolverPendencia(Boolean resolverPendencia) {
		this.resolverPendencia = resolverPendencia;
	}

	public Integer getSeqProcessamentoPasso() {
		return seqProcessamentoPasso;
	}

	public void setSeqProcessamentoPasso(Integer seqProcessamentoPasso) {
		this.seqProcessamentoPasso = seqProcessamentoPasso;
	}
}
