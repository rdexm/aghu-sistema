package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesComPrevisaoAltaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;




/**
 * Classe responsável por controlar as ações da tela de pesquisa de pacientes com previsão de alta
 * 
 * @author Stanley Araujo
 * 
 */

public class PesquisaPacientesComPrevisaoAltaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4247329217955809909L;

	/**
	 * Data inicial
	 *  
	 * */
	private Date dataInicial = new Date();

	/**
	 * Data final
	 * 
	 * */
	private Date dataFinal = new Date();
	
	/**
	 * Injeção da Facade de PesquisaInternacao.
	 */
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	
	@Inject @Paginator
	private DynamicDataModel<PesquisaPacientesComPrevisaoAltaVO> dataModel;	
		
	/**
	 * Metodo invocado ao pressionar o botão 'Limpar'.
	 */
	public void limparPesquisa() {
		this.dataInicial = new Date();
		this.dataFinal = new Date();
		this.dataModel.limparPesquisa();
				 
	}
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.dataInicial = new Date();
		this.dataFinal = new Date();
	}
	
	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		try{
			this.pesquisaInternacaoFacade.validarDiferencaDataInicialFinalSemEspecialidade(this.dataInicial, this.dataFinal);
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			dataModel.limparPesquisa();
		}
	}

	@Override
	public Long recuperarCount() {
		return this.pesquisaInternacaoFacade.pesquisaPacientesComPrevisaoAltaCount(this.dataInicial, this.dataFinal);
	}

	@Override
	public List<PesquisaPacientesComPrevisaoAltaVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.pesquisaInternacaoFacade.pesquisaPacientesComPrevisaoAlta(firstResult, maxResult, orderProperty, asc, this.dataInicial, this.dataFinal);
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

	public DynamicDataModel<PesquisaPacientesComPrevisaoAltaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaPacientesComPrevisaoAltaVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	
}
