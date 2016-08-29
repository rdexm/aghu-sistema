package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameItemVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameResultadoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class SolicitacaoExamePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -6946522027603780739L;
	private static final String PAGE_SOLICITACAO_EXAMES_CRUD = "exames-solicitacaoExameCRUD";
	

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;	
	
	private SolicitacaoExameResultadoVO solicitacaoExameResultadoVO;	
	private SolicitacaoExameFilter filtro = new SolicitacaoExameFilter();
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	//private SolicitacaoExameItemVO itemAtendimentoParaSolicitacao;
	
	private Integer atendimentoSeq;
	
	@Inject @Paginator
	private DynamicDataModel<SolicitacaoExameItemVO> dataModel;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation, true);
	}	
	
	
	@Override
	public List<SolicitacaoExameItemVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<SolicitacaoExameItemVO> lista;
		
		try {
			this.getFiltro().setNomePaciente(getFiltro().getNomePaciente() != null ? getFiltro().getNomePaciente().toUpperCase().trim() : null);
			this.solicitacaoExameResultadoVO = solicitacaoExameFacade.pesquisarAtendimentosPaciente(this.getFiltro(), firstResult, maxResult, orderProperty, false);
			lista = this.solicitacaoExameResultadoVO.getListaAtendimentosDoPaciente();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			lista = new LinkedList<SolicitacaoExameItemVO>();
		}
		
		return lista;
	}

	@Override
	public Long recuperarCount() {
		Long total = 0l;
		if (this.solicitacaoExameResultadoVO != null && this.solicitacaoExameResultadoVO.getTotalRegistros() != null) {
			total = this.solicitacaoExameResultadoVO.getTotalRegistros().longValue(); 
		}
		return total;
	}

	public String pesquisar() {
		String returnValue = null;
		
		try {
			solicitacaoExameResultadoVO = new SolicitacaoExameResultadoVO();
			solicitacaoExameFacade.verificarFiltrosPesquisaSolicitacaoExame(this.getFiltro());
			Long totalRegistros = solicitacaoExameFacade.pesquisarAtendimentosPacienteTotalRegistros(this.getFiltro());
			solicitacaoExameResultadoVO.setTotalRegistros(totalRegistros.intValue());

			if (totalRegistros != null && totalRegistros.intValue() == 1) {
				SolicitacaoExameItemVO atendimento = solicitacaoExameFacade.pesquisarAtendimentosPacienteUnico(this.getFiltro());
				returnValue = solicitarExame(atendimento);
			} else {

                // Verifica se o paciente está internado
                // Se sim, redireciona direto para a tela de solicitação de exames (#47714)
				if (totalRegistros != null && totalRegistros.intValue() > 0 && solicitacaoExameFacade.pesquisarAtendimentosPacienteInternadoCount(this.getFiltro()).intValue() == 1) {
					SolicitacaoExameItemVO atendimento = solicitacaoExameFacade.pesquisarAtendimentosPacienteInternadoUnico(this.getFiltro());
					returnValue = solicitarExame(atendimento);
				}
			}

			//super.setOrder(AghAtendimentos.Fields.DTHR_INICIO.toString() + " desc ");

			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		return returnValue;
	}
	
	
	public String solicitarExame(SolicitacaoExameItemVO item) {
		try {
			AghAtendimentos atendimento = solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(item.getAtendimentoSeq());
			boolean isOrigemAmbulatorio = DominioOrigemAtendimento.A.equals(item.getOrigem());
			solicitacaoExameController.setOrigemAmbulatorio(isOrigemAmbulatorio);
			solicitacaoExameController.setAtendimentoSeq(item.getAtendimentoSeq());
			solicitacaoExameController.setAtendimento(atendimento);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		this.setAtendimentoSeq(item.getAtendimentoSeq());
		
		return PAGE_SOLICITACAO_EXAMES_CRUD;
	}
	
	public void limpar() {
		this.setFiltro(new SolicitacaoExameFilter());
		this.solicitacaoExameResultadoVO = null;
		dataModel.limparPesquisa();
	}
	
	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(String objPesquisa) {
		return this.returnSGWithCount(solicitacaoExameFacade.buscarUnidadeFuncionais((String) objPesquisa),buscarUnidadeFuncionaisCount(objPesquisa));
	}
	
	public Long buscarUnidadeFuncionaisCount(String objPesquisa) {
		return solicitacaoExameFacade.buscarUnidadeFuncionaisCount((String) objPesquisa);
	}
	
	
	/**
	 * @return the filtro
	 */
	public SolicitacaoExameFilter getFiltro() {
		return filtro;
	}

	/**
	 * @param filtro the filtro to set
	 */
	public void setFiltro(SolicitacaoExameFilter filtro) {
		this.filtro = filtro;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}


	public DynamicDataModel<SolicitacaoExameItemVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SolicitacaoExameItemVO> dataModel) {
	 this.dataModel = dataModel;
	}
}