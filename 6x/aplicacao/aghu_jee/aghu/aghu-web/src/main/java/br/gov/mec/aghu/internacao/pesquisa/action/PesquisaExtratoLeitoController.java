package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

@Named
public class PesquisaExtratoLeitoController extends ActionController {
	private static final long serialVersionUID = -7742159937624797376L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private Date dataPesquisa;
	private boolean mostrarDetalhes = false;

	private String leitoPesquisa;

	@Inject
	private PesquisaExtratoLeitoPaginatorController paginator;

	private String codigoLeitoSelecionado;
	private String descricaoMvtoLeito;
	private AinLeitos leitos;

	private String cameFrom;
	
	private final String PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";

	@PostConstruct
	public void iniciarPesquisa(){
		
		this.begin(this.conversation, true);
		
		if (dataPesquisa == null) {
			this.dataPesquisa = new Date();
		}

		if (StringUtils.isNotBlank(leitoPesquisa)) {
			leitos = this.cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitoPesquisa);
			this.codigoLeitoSelecionado = leitoPesquisa;
			this.pesquisar();
		}
	}
	
	/**
	 * Carrega Lista de AinLeitos para lista de valores.
	 */
	public List<AinLeitos> pesquisarLeitos(String param){
		return this.cadastrosBasicosInternacaoFacade.pesquisaLeitoPeloLeitoId(param);
	}
	
	public Boolean mostrarDetalhes(){
		this.mostrarDetalhes = false;
		
		if(leitos != null && !leitos.getLeitoID().isEmpty()){
			mostrarDetalhes = true;
		} 
		
		return mostrarDetalhes;
	}

	public void pesquisar() {

		if (leitos == null || leitos.getLeitoID() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_LEITO_NAO_INFORMADO");
			
		} else if (dataPesquisa.after(new Date())) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_DATA_FUTURA");
			
		} else {
			paginator.setLeito(leitos.getLeitoID());
			paginator.setDataPesquisa(dataPesquisa);
			paginator.getDataModel().setDefaultMaxRow(12);
			paginator.setPesquisaInternacaoFacade(pesquisaInternacaoFacade);
			paginator.getDataModel().reiniciarPaginator();
		}
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.leitos = null;
		this.dataPesquisa = new Date();
		paginator.setDataPesquisa(dataPesquisa);
		paginator.setLeito(null);
		paginator.getDataModel().limparPesquisa();
		paginator.getDataModel().reiniciarPaginator();
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela. 
	 */
	public String cancelar() {
		if("internacao-pesquisarCensoDiarioPacientes".equalsIgnoreCase(cameFrom)){
			return PAGE_PESQUISAR_CENSO_DIARIO_PACIENTES;
		}
		
		return null;
	}
	
	// getters and setters
	public PesquisaExtratoLeitoPaginatorController getPaginator() {
		return paginator;
	}

	public void setPaginator(PesquisaExtratoLeitoPaginatorController paginator) {
		this.paginator = paginator;
	}

	public Date getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(Date dataPesquisa) {
		this.dataPesquisa = dataPesquisa;
	}

	public String getCodigoLeitoSelecionado() {
		return codigoLeitoSelecionado;
	}

	public void setCodigoLeitoSelecionado(String codigoLeitoSelecionado) {
		this.codigoLeitoSelecionado = codigoLeitoSelecionado;
	}

	public void setLeitos(AinLeitos leitos) {
		this.leitos = leitos;
	}

	public AinLeitos getLeitos() {
		return leitos;
	}

	public String getDescricaoMvtoLeito() {
		return descricaoMvtoLeito;
	}

	public void setDescricaoMvtoLeito(String descricaoMvtoLeito) {
		this.descricaoMvtoLeito = descricaoMvtoLeito;
	}

	public boolean isMostrarDetalhes() {
		return mostrarDetalhes;
	}

	public void setMostrarDetalhes(boolean mostrarDetalhes) {
		this.mostrarDetalhes = mostrarDetalhes;
	}

	public String getLeitoPesquisa() {
		return leitoPesquisa;
	}

	public void setLeitoPesquisa(String leitoPesquisa) {
		this.leitoPesquisa = leitoPesquisa;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	
}