package br.gov.mec.aghu.sig.custos.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class ProcessamentoMensalPaginatorController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR_HISTORICO_PROCESSAMENTO_CUSTO = "visualizarHistoricoProcessamentoCusto";

	private static final String PROCESSAMENTO_MENSAL_CRUD = "processamentoMensalCRUD";

	@Inject @Paginator
	private DynamicDataModel<SigProcessamentoCusto> dataModel;

	private static final long serialVersionUID = -6950783760167440580L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private SigProcessamentoCusto competencia;
	private DominioSituacaoProcessamentoCusto situacao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		if (this.custosSigProcessamentoFacade.pesquisarProcessamentoCustoCount(null, null) == null
				|| this.custosSigProcessamentoFacade.pesquisarProcessamentoCustoCount(null, null) < 1) {
			try {
				insereNovoProcessamento();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return;
			}
		}
	}
	

	private void insereNovoProcessamento() throws ApplicationBusinessException {
		SigProcessamentoCusto sigProcessamentoCusto = new SigProcessamentoCusto();

		Calendar dtCompetencia = Calendar.getInstance();
		dtCompetencia.setTime(new Date());
		dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
		dtCompetencia.add(Calendar.MONTH, -1);

		sigProcessamentoCusto.setCompetencia(dtCompetencia.getTime());

		sigProcessamentoCusto.setIndSituacao(DominioSituacaoProcessamentoCusto.A);
		sigProcessamentoCusto.setCriadoEm(new Date());
		sigProcessamentoCusto.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));

		// Seta data de inicio e fim do processamento;
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.setTime(sigProcessamentoCusto.getCompetencia());
		dataInicio.set(Calendar.DAY_OF_MONTH, 1);
		sigProcessamentoCusto.setDataInicio(dataInicio.getTime());

		Calendar dataFim = Calendar.getInstance();
		dataFim.setTime(sigProcessamentoCusto.getCompetencia());
		int maxDayOfMonth = dataFim.getActualMaximum(Calendar.DAY_OF_MONTH);
		dataFim.set(Calendar.DAY_OF_MONTH, maxDayOfMonth);
		sigProcessamentoCusto.setDataFim(dataFim.getTime());

		this.custosSigProcessamentoFacade.incluirProcessamentoCusto(sigProcessamentoCusto);
	}
	
	public List<SigProcessamentoCusto> listarCompetencias(){
		return this.custosSigProcessamentoFacade.pesquisarCompetencia();
	}
	
	public String calcularTempoExecucao(SigProcessamentoCusto processamento){
		return this.custosSigProcessamentoFacade.calcularTempoExecucao(processamento, null);
	}

	@Override
	public Long recuperarCount() {
		return custosSigProcessamentoFacade.pesquisarProcessamentoCustoCount(this.getCompetencia(), this.getSituacao());
	}

	@Override
	public List<SigProcessamentoCusto> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return custosSigProcessamentoFacade.pesquisarProcessamentoCusto(firstResult, maxResult, orderProperty, asc, this.getCompetencia(), this.getSituacao());
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public void pesquisar() {
		this.reiniciarPaginator();
	}

	public void limpar() {
		this.setCompetencia(null);
		this.setSituacao(null);
		this.setAtivo(false);
	}

	public String editar() {
		return PROCESSAMENTO_MENSAL_CRUD;
	}

	public String visualizar() {
		return PROCESSAMENTO_MENSAL_CRUD;
	}

	public String visualizarHistorico() {
		return VISUALIZAR_HISTORICO_PROCESSAMENTO_CUSTO;
	}

	public String iniciarInclusao() {
		return PROCESSAMENTO_MENSAL_CRUD;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public DominioSituacaoProcessamentoCusto getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoProcessamentoCusto situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<SigProcessamentoCusto> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigProcessamentoCusto> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
