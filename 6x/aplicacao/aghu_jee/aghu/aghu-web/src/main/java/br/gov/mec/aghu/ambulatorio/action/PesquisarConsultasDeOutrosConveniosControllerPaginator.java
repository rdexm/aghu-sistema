package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasDeOutrosConveniosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PesquisarConsultasDeOutrosConveniosControllerPaginator extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8821750528837252423L;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject @Paginator
	private DynamicDataModel<ConsultasDeOutrosConveniosVO> dataModel;
	
	private static final Log LOG = LogFactory.getLog(PesquisarConsultasDeOutrosConveniosControllerPaginator.class);
	
	// Filtro principal da pesquisa
	private Date mesAno = null;;

	
	@Override
	public List<ConsultasDeOutrosConveniosVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		try {
			return this.ambulatorioFacade.pesquisarConsultasDeOutrosConvenios(firstResult, maxResult, orderProperty, asc, this.mesAno);
		} catch(ApplicationBusinessException exception){
			apresentarExcecaoNegocio(exception);
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		try {
			return this.ambulatorioFacade.pesquisarConsultasDeOutrosConveniosCount(this.mesAno);
		} catch(ApplicationBusinessException exception){
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(), exception);
		}
		return null;
	}

	/**
	 * Pesquisa passando filtro
	 */
	public void pesquisar() {
		
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpar os campos das tela e remove a grid e botão novo
	 */
	public void limpar() {
		this.mesAno = null;
		this.dataModel.limparPesquisa();
	}
	
	/**
	 * Truncar os itens e adiciona o símbolo de reticências (...)
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}
	
	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<ConsultasDeOutrosConveniosVO> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<ConsultasDeOutrosConveniosVO> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the mesAno
	 */
	public Date getMesAno() {
		return mesAno;
	}

	/**
	 * @param mesAno the mesAno to set
	 */
	public void setMesAno(Date mesAno) {
		this.mesAno = mesAno;
	}

	

}
