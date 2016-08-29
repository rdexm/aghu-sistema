package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioConsultaGenerica;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de grupo da condição
 * de atendimento.
 * 
 * @author gzapalaglio
 */


public class ManterCondicaoAtendimentoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1393921256668856337L;
	
	private static final String PAGE_MANTER_CONDICAO_ATENDIMENTO_CRUD = "manterCondicaoAtendimentoCRUD";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject @Paginator
	private DynamicDataModel<AacCondicaoAtendimento> dataModel;
	
	private AacCondicaoAtendimento parametroSelecionado;
	
	/**
	 * Atributos dos campo de filtro da pesquisa da condição de atendimento.
	 */
	private Short codigoCondicaoAtendimento;
	private String descricaoCondicaoAtendimento;
	private String siglaCondicaoAtendimento;
	private DominioConsultaGenerica indGenericaAmb;
	private DominioConsultaGenerica indGenericaInternacao;
	private Boolean criticaApac;
	private DominioSituacao situacaoCondicaoAtendimento;

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoCondicaoAtendimento = null;
		this.descricaoCondicaoAtendimento = null;
		this.siglaCondicaoAtendimento = null;
		this.indGenericaAmb = null;
		this.indGenericaInternacao = null;
		this.situacaoCondicaoAtendimento = null;
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		Long count = ambulatorioFacade.countCondicaoAtendimentoPaginado(codigoCondicaoAtendimento, descricaoCondicaoAtendimento,
				siglaCondicaoAtendimento, indGenericaAmb, indGenericaInternacao, criticaApac, situacaoCondicaoAtendimento);
		return count;
	}

	@Override
	public List<AacCondicaoAtendimento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		
		List<AacCondicaoAtendimento> result = this.ambulatorioFacade.pesquisarCondicaoAtendimentoPaginado(firstResult, maxResult, 
				orderProperty, asc, codigoCondicaoAtendimento, descricaoCondicaoAtendimento, siglaCondicaoAtendimento, indGenericaAmb, 
				indGenericaInternacao, criticaApac, situacaoCondicaoAtendimento);

		if (result == null) {
			result = new ArrayList<AacCondicaoAtendimento>();
		}

		return result;
	}
	
	public void excluir() {		
		try {
			this.ambulatorioFacade.removerCondicaoAtendimento(this.parametroSelecionado.getSeq());
			
			this.getDataModel().reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, 
					"MSG_CONDICAO_ATENDIMENTO_EXCLUIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserirEditar() {
		return PAGE_MANTER_CONDICAO_ATENDIMENTO_CRUD;
	}
	
	/************************* getters and setters *************************************/

	public String getSiglaCondicaoAtendimento() {
		return siglaCondicaoAtendimento;
	}

	public void setSiglaCondicaoAtendimento(String siglaCondicaoAtendimento) {
		this.siglaCondicaoAtendimento = siglaCondicaoAtendimento;
	}

	public DominioConsultaGenerica getIndGenericaAmb() {
		return indGenericaAmb;
	}

	public void setIndGenericaAmb(DominioConsultaGenerica indGenericaAmb) {
		this.indGenericaAmb = indGenericaAmb;
	}

	public DominioConsultaGenerica getIndGenericaInternacao() {
		return indGenericaInternacao;
	}

	public void setIndGenericaInternacao(
			DominioConsultaGenerica indGenericaInternacao) {
		this.indGenericaInternacao = indGenericaInternacao;
	}

	public Boolean getCriticaApac() {
		return criticaApac;
	}

	public void setCriticaApac(Boolean criticaApac) {
		this.criticaApac = criticaApac;
	}

	public DominioSituacao getSituacaoCondicaoAtendimento() {
		return situacaoCondicaoAtendimento;
	}

	public void setSituacaoCondicaoAtendimento(
			DominioSituacao situacaoCondicaoAtendimento) {
		this.situacaoCondicaoAtendimento = situacaoCondicaoAtendimento;
	}
	
	public Short getCodigoCondicaoAtendimento() {
		return codigoCondicaoAtendimento;
	}

	public void setCodigoCondicaoAtendimento(Short codigoCondicaoAtendimento) {
		this.codigoCondicaoAtendimento = codigoCondicaoAtendimento;
	}

	public void setDescricaoCondicaoAtendimento(
			String descricaoCondicaoAtendimento) {
		this.descricaoCondicaoAtendimento = descricaoCondicaoAtendimento;
	}

	public String getDescricaoCondicaoAtendimento() {
		return descricaoCondicaoAtendimento;
	}

	public DynamicDataModel<AacCondicaoAtendimento> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacCondicaoAtendimento> dataModel) {
	 this.dataModel = dataModel;
	}

	public AacCondicaoAtendimento getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AacCondicaoAtendimento parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
