package br.gov.mec.aghu.parametrosistema.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.AgrupadorRelatorioJasper;



public class ParametroListPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 2844456018216630506L;
	
	private static final String PAGE_DETALHAR_PARAMETRO = "parametroCRUD";
	

	private AghParametros parametroFiltro;
	
	private AghParametroVO parametroSelecionado;
	
	@Inject @Paginator
	private DynamicDataModel<AghParametroVO> dataModel;
	
	@Inject
	private ParametroListReportGenerator parametroListRelatorioController;
	
	@Inject
	private ParametroListFinalReportGenerator parametroListFinalRelatorioController;
	
	private List<AghParametroVO> parametrosEncontrados;
	
	
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IParametroSistemaFacade parametroSistemaFacade;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.setParametroFiltro(new AghParametros());
	}
	
	@Override
	public Long recuperarCount() {
		return parametroSistemaFacade.pesquisaParametroListCount(this.getParametroFiltro());
	}

	@Override
	public List<AghParametroVO> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		parametrosEncontrados = parametroSistemaFacade.pesquisaParametroList(firstResult, maxResult, orderProperty, asc, this.getParametroFiltro());
		return parametrosEncontrados;  
	}
	
	
	/**
	 * Método responsável para ação do botão 'Pesquisar'.
	 */
	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
	}
		
	/**
	 * Método que limpa os campos da tela de pesquisa.
	 */
	public void limparPesquisa() {
		this.setParametroFiltro(new AghParametros());
		this.getDataModel().limparPesquisa();
	}
	
	public String novo() {
		this.setParametroSelecionado(null);
		return PAGE_DETALHAR_PARAMETRO;
	}
	
	public String editar() {
		return PAGE_DETALHAR_PARAMETRO;
	}
	
	public void excluir() {
		
		try {
			this.parametroSistemaFacade.excluirAghParametro(getParametroSelecionado().getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PARAMETRO", getParametroSelecionado().getNome());
			this.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public List<AghSistemas> pesquisarSistemaPorNome(String criterioNome) {
		return this.parametroSistemaFacade.pesquisarSistemaPorNome((String) criterioNome);
	}
	
	public void downloadArquivo() {
		try {
			AgrupadorRelatorioJasper agrupador = new AgrupadorRelatorioJasper();
			
			parametroListRelatorioController.setParametros(this.parametrosEncontrados);
			agrupador.addReport(this.parametroListRelatorioController.gerarDocumento(true).getJasperPrint());

			parametroListFinalRelatorioController.setParametros(this.parametrosEncontrados);
			agrupador.addReport(this.parametroListFinalRelatorioController.gerarDocumento(true).getJasperPrint());
			
			ByteArrayOutputStream outputStream = agrupador.exportReportAsOutputStream();
			byte[] bytes = outputStream.toByteArray();
			
			download(bytes, "listaParametroSistemas.pdf", "text/pdf");
			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
		} catch (JRException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
		}
	}
	
	
	
	
	public DynamicDataModel<AghParametroVO> getDataModel() {
		return dataModel;
	}
	
	public AghParametros getParametroFiltro() {
		return parametroFiltro;
	}

	public void setParametroFiltro(AghParametros parametroFiltro) {
		this.parametroFiltro = parametroFiltro;
	}

	public AghParametroVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AghParametroVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	
}
