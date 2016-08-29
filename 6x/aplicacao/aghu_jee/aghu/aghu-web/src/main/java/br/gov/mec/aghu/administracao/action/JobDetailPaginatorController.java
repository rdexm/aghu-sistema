package br.gov.mec.aghu.administracao.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.AgendamentoJobVO;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;





public class JobDetailPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -5367869381717311199L;
	
	private static final String PAGE_ROTINAAUTOMATICA = "rotinaAutomatica";
	private static final String PAGE_REAGENDAMENTOJOBDETAIL = "reAgendamentoJobDetail";
	private static final String PAGE_JOBDETAILCRUD ="jobDetailCRUD";

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private ISchedulerFacade schedulerFacade;
		
	@Inject @Paginator
	private DynamicDataModel<AghJobDetail> dataModel;
	
	
	//filtros
	private Integer numero;
	private String nomeProcesso;
	private Date dataInicial = DateUtil.obterDataInicioCompetencia(new Date());
	private Date dataFinal = DateUtil.obterDataFimCompetencia(new Date());
	private DominioSituacaoJobDetail situacao;
	private Map<Object, Object> filtersMap = new HashMap<Object, Object>();
	
	//controle do parametro de exclusao
	private AghJobDetail jobDetailSelecionado;
	
	
	private List<AgendamentoJobVO> listQuartzJob;
	private Boolean apresentarAgendamentoEmMemoria = Boolean.FALSE;
	
	
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}
	
	
	public void pesquisarTodosQuartzJobs() {
		try {
			this.setListQuartzJob(schedulerFacade.listarTodosQuartzJobs());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
		
	public void excluirJob(AgendamentoJobVO job) {
		
		try {
			schedulerFacade.removerQuartzJob(job);
			
			this.setListQuartzJob(schedulerFacade.listarTodosQuartzJobs());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
	}
	
	
	
		
	public void limparPesquisa() {
		this.numero = null;
		this.nomeProcesso = null;
		//this.iniciar();
		
		this.dataInicial = null;
		this.dataFinal = null;
		
		this.situacao = null;
		//this.setAtivo(Boolean.FALSE);
		this.dataModel.limparPesquisa();
		
		this.setApresentarAgendamentoEmMemoria(Boolean.FALSE);
		this.setListQuartzJob(null);
	}
	
	
	public void excluir() {
		try {
			this.schedulerFacade.removerAghJobDetail(this.getJobDetailSelecionado(), Boolean.FALSE);
			
//			reiniciarPaginator(JobDetailPaginatorController.class);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_AGENDAMENTO_PROCESSO_EXCLUIDO");
			this.pesquisar();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Metodo responsavel por realizar a pesquisa
	 */
	public void pesquisar() {
		//this.setOrder(AghJobDetail.Fields.AGENDADO_PARA + " desc");
		
		this.prepararFiltro();
		
		if (this.getApresentarAgendamentoEmMemoria()) {
			this.pesquisarTodosQuartzJobs();
		}
		
		this.getDataModel().reiniciarPaginator();
	}
	
	
	@Override
	public List<AghJobDetail> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.schedulerFacade.pesquisarAghJobDetailPaginator(filtersMap, 
				firstResult, maxResult, orderProperty, asc);
	}
	
	public String editar() {
		return PAGE_JOBDETAILCRUD;
	}
	
	public String reAgendar() {
		return PAGE_REAGENDAMENTOJOBDETAIL;
	}
	
	
	
	public String iniciarRotinaAutomatica() {
		return PAGE_ROTINAAUTOMATICA;
	}
	
	/*public String pausarAgendamento(AghJobDetail jobDetail) {
		try {
			boolean ok = this.schedulerFacade.pausarAghJobDetail(jobDetail);
			
			if (ok) {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_AGENDAMENTO_PROCESSO_PAUSADA");
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String continuarAgendamento(AghJobDetail jobDetail) {
		try {
			boolean ok = this.schedulerFacade.continuarAghJobDetail(jobDetail);
			
			if (ok) {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_AGENDAMENTO_PROCESSO_CONTINUADO");
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}*/
	
	private void prepararFiltro() {
		this.filtersMap.clear();
		
		this.filtersMap.put("numero", this.numero);
		this.filtersMap.put("nomeProcesso", this.nomeProcesso);
		
		if (this.dataInicial != null) {
			this.filtersMap.put("dataInicial", DateUtil.obterDataComHoraInical(this.dataInicial));
		}
		if (this.dataFinal != null) {
			this.filtersMap.put("dataFinal", DateUtil.obterDataComHoraFinal(this.dataFinal));
		}
		
		this.filtersMap.put("situacao", this.situacao);
	}
	

	@Override
	public Long recuperarCount() {
		return this.schedulerFacade.countAghJobDetailPaginator(filtersMap);
	}
	
	
	/** GET/SET **/
	public Integer getNumero() {
		return numero;
	}


	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	public Date getDataInicial() {
		return dataInicial;
	}


	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}


	public Date getDataFinal() {
		return dataFinal;
	}


	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}


	public DominioSituacaoJobDetail getSituacao() {
		return situacao;
	}


	public void setSituacao(DominioSituacaoJobDetail situacao) {
		this.situacao = situacao;
	}


	public String getNomeProcesso() {
		return nomeProcesso;
	}


	public void setNomeProcesso(String nomeProcesso) {
		this.nomeProcesso = nomeProcesso;
	}


	

	

	public void setListQuartzJob(List<AgendamentoJobVO> l) {
		this.listQuartzJob = l;
	}

	public List<AgendamentoJobVO> getListQuartzJob() {
		return listQuartzJob;
	}

	/**
	 * @return the apresentarAgendamentoEmMemoria
	 */
	public Boolean getApresentarAgendamentoEmMemoria() {
		return apresentarAgendamentoEmMemoria;
	}

	/**
	 * @param apresentarAgendamentoEmMemoria the apresentarAgendamentoEmMemoria to set
	 */
	public void setApresentarAgendamentoEmMemoria(Boolean p) {
		this.apresentarAgendamentoEmMemoria = p;
	}
	
	
	public DynamicDataModel<AghJobDetail> getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DynamicDataModel<AghJobDetail> dataModel) {
		this.dataModel = dataModel;
	}

	public AghJobDetail getJobDetailSelecionado() {
		return jobDetailSelecionado;
	}

	public void setJobDetailSelecionado(AghJobDetail jobDetailSelecionado) {
		this.jobDetailSelecionado = jobDetailSelecionado;
	}
	

}
