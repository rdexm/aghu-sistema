package br.gov.mec.aghu.financeiro.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class CompetenciaInternacaoPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2508440212572350178L;
	private final String PAGE_CADASTRAR_COMPETENCIA_INTERNACAO = "competenciaInternacao";
	@Inject @Paginator
	private DynamicDataModel<FatCompetencia> dataModel;
	
	private FatCompetencia competencia;
	private FatCompetencia competenciaSelecionada;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Date dtHrInicio;
	private DominioSimNao indFaturado;
	private DominioModuloCompetencia modulo;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		if(competencia == null || competencia.getId() == null){
			competencia = new FatCompetencia();
			competencia.setId(new FatCompetenciaId());
		}
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		dtHrInicio = null;
		modulo = null;
		indFaturado = null;
		competencia = new FatCompetencia();
		competencia.setId(new FatCompetenciaId());
		this.dataModel.limparPesquisa();			
	}
	
	public String editar() {
		return PAGE_CADASTRAR_COMPETENCIA_INTERNACAO;
	}
	
	@Override
	public List<FatCompetencia> recuperarListaPaginada( final Integer firstResult,  final Integer maxResult, 
														final String orderProperty, final boolean asc ) {
		
		if(indFaturado != null){
			getCompetencia().setIndFaturado(indFaturado.isSim());
		} else if(competencia.getIndFaturado() != null){
			getCompetencia().setIndFaturado(null);
		}
		
		if(modulo != null){
			getCompetencia().getId().setModulo(DominioModuloCompetencia.valueOf(modulo.toString()));
		} else {
			getCompetencia().getId().setModulo(null);
		}
		
		return faturamentoFacade.listarFatCompetencia(getCompetencia(), firstResult, maxResult, orderProperty, asc);		
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.listarFatCompetenciaCount(getCompetencia());		
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(final FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public DominioModuloCompetencia getModulo() {
		return modulo;
	}

	public void setModulo(DominioModuloCompetencia modulo) {
		this.modulo = modulo;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public DominioSimNao getIndFaturado() {
		return indFaturado;
	}

	public void setIndFaturado(DominioSimNao indFaturado) {
		this.indFaturado = indFaturado;
	}

	public DynamicDataModel<FatCompetencia> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatCompetencia> dataModel) {
		this.dataModel = dataModel;
	}

	public FatCompetencia getCompetenciaSelecionada() {
		return competenciaSelecionada;
	}

	public void setCompetenciaSelecionada(FatCompetencia competenciaSelecionada) {
		this.competenciaSelecionada = competenciaSelecionada;
	}
}
