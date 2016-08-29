package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da tela de pesquisa de cadastro de
 * itens.
 * 
 * @author Ricardo Costa
 * 
 */
public class CadastroItensPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2962714246766618996L;
	@Inject @Paginator
	private DynamicDataModel<CidVO> dataModel;
	
	/**
	 * Código do CID (Ex: A23, B21, ... ).
	 */
	private String codCid;

	/**
	 * Código do CID ou Descrição.
	 */
	private String codDescCidLov;

	/**
	 * Instancia de CID.
	 */
	private AghCid aghCid = null;

	/**
	 * Lista da entidade.
	 */
	private List<AghCid> aghCids = new ArrayList<AghCid>();
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * Busca pela pk.
	 */
	public void buscarCid() {
		if (StringUtils.isNotBlank(codCid)) {
			this.aghCid = aghuFacade.obterCid(codCid);
		} else {
			aghCid = new AghCid();
		}
	}

	/**
	 * Busca por descricao.
	 */
	public List<AghCid> pesquisarCids(String param){
		this.aghCids = aghuFacade.obterCids(param, false);
		return this.aghCids;
	}
	
	/**
	 * Metodo invocado ao clicar no botão 'Limpar'.
	 */
	public void limparPesquisa() {
		this.codCid = null;
		this.aghCid = null;
		this.codDescCidLov = null;
		this.dataModel.setPesquisaAtiva(false);
	}


	/**
	 * Ação do botão pesquisar.
	 * 
	 * @return String
	 */
	public String pesquisar() {
		try {
			this.aghuFacade.validarCodigoCid(aghCid.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.WARN, e.getMessage());
			return null;
		}

		this.dataModel.reiniciarPaginator();

		return null;
	}

	@Override
	public Long recuperarCount() {
		return this.aghuFacade.pesquisarProcedimentosParaCidCount(aghCid.getCodigo());
	}

	@Override
	public List<CidVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return this.aghuFacade.pesquisarProcedimentosParaCid(firstResult, maxResults, orderProperty, asc, aghCid.getCodigo());
	}

	/*
	 * GETs e SETs.
	 */
	public String getCodCid() {
		return codCid;
	}

	public void setCodCid(String codCid) {
		this.codCid = codCid;
	}

	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public List<AghCid> getAghCids() {
		return aghCids;
	}

	public void setAghCids(List<AghCid> aghCids) {
		this.aghCids = aghCids;
	}

	public String getCodDescCidLov() {
		return codDescCidLov;
	}

	public void setCodDescCidLov(String codDescCidLov) {
		this.codDescCidLov = codDescCidLov;
	}

	public DynamicDataModel<CidVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CidVO> dataModel) {
		this.dataModel = dataModel;
	}
}
