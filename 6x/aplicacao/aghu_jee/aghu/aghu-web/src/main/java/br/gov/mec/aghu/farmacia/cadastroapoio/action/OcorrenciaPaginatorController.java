package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class OcorrenciaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private OcorrenciaController ocorrenciaController;
	
	private Short seqPesq;
	private String descricaoPesq;
	private DominioTipoUsoDispensacao tipoUsoPesq;
	private Short seqOcorrenciaExclusao;
	private DominioSituacao situacaoPesq;
	private AfaTipoOcorDispensacao ocorrenciaSelecionada;
	@Inject @Paginator
	private DynamicDataModel<AfaTipoOcorDispensacao> dataModel;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	/**
	 * Realiza a pesquisa de ocorrências
	 */
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}
	
	public String editar() {
		ocorrenciaController.setOcorrencia(ocorrenciaSelecionada);
		return "ocorrenciaCRUD";
	}
	
	public String incluir() {
		try {
			ocorrenciaController.setOcorrencia(new AfaTipoOcorDispensacao());
			ocorrenciaController.getOcorrencia().setCriadoEm(new Date());
			ocorrenciaController.getOcorrencia().setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return "ocorrenciaCRUD";
	}
	
	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de Ocorrências.
	 */
	public void excluir() {
		dataModel.reiniciarPaginator();
		try {
			if (ocorrenciaSelecionada != null) {
				this.farmaciaApoioFacade.removerOcorrencia(ocorrenciaSelecionada);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_OCORRENCIA", ocorrenciaSelecionada.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOCAO_OCORRENCIA_INVALIDA");
			}
			this.seqOcorrenciaExclusao = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa(){
		this.seqPesq = null;
		this.descricaoPesq = null;
		this.tipoUsoPesq = null;
		this.situacaoPesq= null;
		dataModel.limparPesquisa();
	}
	
	@Override
	public List<AfaTipoOcorDispensacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) {
		
		List<AfaTipoOcorDispensacao> result = farmaciaFacade.pesquisarOcorrencias(firstResult, maxResult, orderProperty,
			asc, seqPesq, descricaoPesq, tipoUsoPesq, situacaoPesq);
		
		return result;
	}

	@Override
	public Long recuperarCount() {
		return farmaciaFacade.pesquisarOcorrenciasCount(seqPesq, descricaoPesq, tipoUsoPesq, situacaoPesq);
	}
	
	public Short getSeqPesq() {
		return seqPesq;
	}

	public void setSeqPesq(Short seqPesq) {
		this.seqPesq = seqPesq;
	}

	public String getDescricaoPesq() {
		return descricaoPesq;
	}

	public void setDescricaoPesq(String descricaoPesq) {
		this.descricaoPesq = descricaoPesq;
	}

	public DominioTipoUsoDispensacao getTipoUsoPesq() {
		return tipoUsoPesq;
	}

	public void setTipoUsoPesq(DominioTipoUsoDispensacao tipoUsoPesq) {
		this.tipoUsoPesq = tipoUsoPesq;
	}

	public Short getSeqOcorrenciaExclusao() {
		return seqOcorrenciaExclusao;
	}

	public void setSeqOcorrenciaExclusao(Short seqOcorrenciaExclusao) {
		this.seqOcorrenciaExclusao = seqOcorrenciaExclusao;
	}

	public DominioSituacao getSituacaoPesq() {
		return situacaoPesq;
	}

	public void setSituacaoPesq(DominioSituacao situacaoPesq) {
		this.situacaoPesq = situacaoPesq;
	}

	public DynamicDataModel<AfaTipoOcorDispensacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaTipoOcorDispensacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaTipoOcorDispensacao getOcorrenciaSelecionada() {
		return ocorrenciaSelecionada;
	}

	public void setOcorrenciaSelecionada(AfaTipoOcorDispensacao ocorrenciaSelecionada) {
		this.ocorrenciaSelecionada = ocorrenciaSelecionada;
	}
}
