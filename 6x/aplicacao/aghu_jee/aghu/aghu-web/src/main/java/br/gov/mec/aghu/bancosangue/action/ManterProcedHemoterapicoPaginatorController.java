package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;

public class ManterProcedHemoterapicoPaginatorController  extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 4282877415523802264L;
	
	private String codigo;
	private String descricao;
	private DominioSimNao indAmostra;
	private DominioSituacao indSituacao;
	private DominioSimNao indJustificativa;
	Boolean amostra = null;
	Boolean justificativa = null;
	private boolean reinicia;
	
	private static final Integer maxWidth = 70;

	private static final String MANTER_PROCEDIMENTOS_HEMOTERAPICOS = "bancodesangue-manterProcedimentosHemoterapicos";
	
	public static enum ProcedimentoHemoterapicoMessages{
		MSG_PROCEDIMENTO_GRAVADO_SUCESSO,
		MSG_PROCEDIMENTO_ATUALIZADO_SUCESSO,
		PROCEDIMENTO_EXCLUIDO_SUCESSO;
	}

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AbsProcedHemoterapico> dataModel;
	
	private AbsProcedHemoterapico selecionado;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
//	public void inicio(){
//		if (reinicia) {
//			reinicia = false;
//			pesquisar();
//		}
//	}

	
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa(){
		dataModel.limparPesquisa();
		codigo = null;
		descricao = null;
		indAmostra = null;
		indSituacao = null;
		indJustificativa = null;
		amostra = null;
		justificativa = null;
	}
	
	public List<AbsProcedHemoterapico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		preparaFiltros();
		return bancoDeSangueFacade.listarAbsProcedHemoterapicos(codigo, descricao, amostra, justificativa, indSituacao, firstResult, maxResult);
	}
	
	private void preparaFiltros(){
		if (StringUtils.isBlank(codigo)) {
			codigo = null;
		}

		if (StringUtils.isBlank(descricao)) {
			descricao = null;
		}
		
		if (indAmostra != null) {
			amostra = DominioSimNao.S.equals(indAmostra);
		}
		
		if (indJustificativa != null) {
			justificativa = DominioSimNao.S.equals(indJustificativa);
		}
	}

	public Long recuperarCount() {
		preparaFiltros();
		return bancoDeSangueFacade.listarAbsProcedHemoterapicosCount(codigo, descricao, amostra, justificativa, indSituacao);
	}

	public void excluir(){
		try {
			bancoDeSangueFacade.excluirProcedimentoHemoterapico(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO, ProcedimentoHemoterapicoMessages.PROCEDIMENTO_EXCLUIDO_SUCESSO.toString());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String editar(){
		return MANTER_PROCEDIMENTOS_HEMOTERAPICOS;
	}

	public String inserir() {
		return MANTER_PROCEDIMENTOS_HEMOTERAPICOS;
	}
	
	public String abreviar(String str){
		String abreviado = str;
		if(isAbreviar(str)) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public Boolean isAbreviar(String str){
		Boolean abreviar = Boolean.FALSE;
		if (str != null) {
			abreviar = str.length() > maxWidth;
		}
		return abreviar;
	} 
	
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioSimNao getIndAmostra() {
		return indAmostra;
	}

	public void setIndAmostra(DominioSimNao indAmostra) {
		this.indAmostra = indAmostra;
	}

	public DominioSimNao getIndJustificativa() {
		return indJustificativa;
	}

	public void setIndJustificativa(DominioSimNao indJustificativa) {
		this.indJustificativa = indJustificativa;
	}
	
	public String getDescricaoBoolean(Boolean valor){
		return DominioSimNao.getInstance(valor).getDescricao();
	}
	
	public String getDescricaoSituacao(DominioSituacao situacao){
		return situacao.getDescricao();
	}

	public boolean isReinicia() {
		return reinicia;
	}

	public void setReinicia(boolean reinicia) {
		this.reinicia = reinicia;
	}

	public DynamicDataModel<AbsProcedHemoterapico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AbsProcedHemoterapico> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsProcedHemoterapico getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AbsProcedHemoterapico selecionado) {
		this.selecionado = selecionado;
	}
}
