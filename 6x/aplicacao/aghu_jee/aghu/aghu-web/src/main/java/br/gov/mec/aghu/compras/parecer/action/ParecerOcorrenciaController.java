package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Classe responsável por controlar as ações do criação e edição de Parecer
 * 
 */


public class ParecerOcorrenciaController extends ActionController {

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final long serialVersionUID = -2626263949658954609L;

	@EJB
	protected IComprasFacade comprasFacade;	
	
	@EJB
	protected IParecerFacade parecerFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private ScoParecerMaterial  parecerMaterial = new ScoParecerMaterial();
	private ScoParecerOcorrencia parecerOcorencia = new ScoParecerOcorrencia();
	private ScoParecerOcorrencia parecerOcorenciaClone = new ScoParecerOcorrencia();
	//private ScoParecerOcorrencia itemListaParecerOcorencia = new ScoParecerOcorrencia();
	private List<ScoParecerOcorrencia> listaParecerOcorrencia = new ArrayList<ScoParecerOcorrencia>();
	
	private Integer codigoParecer;
	private Boolean situacao = Boolean.FALSE;
	
	private Boolean modoEdit = Boolean.FALSE;
	private Boolean editando = Boolean.FALSE;
	private Boolean disabledEdicao = Boolean.TRUE;
	private Boolean renderGravar = Boolean.TRUE;
	
	private Boolean isPendenteItens = Boolean.FALSE;
	private String voltarParaUrl;
	private Boolean parecerAtivo = Boolean.TRUE;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.limpar();
		
		/*if (this.getCodigo() != null){	
			
			this.setParecerOcorencia(this.parecerFacade.obterParecerOcorrenciaPorCodigo(this.getCodigo()));			
		}*/				
		
		if (this.getCodigoParecer() != null){
			this.setParecerMaterial(this.parecerFacade.obterParecer(this
					.getCodigoParecer()));
			this.setListaParecerOcorrencia(new ArrayList<ScoParecerOcorrencia>());
			this.setListaParecerOcorrencia(this.parecerFacade.listaOcorrenciaParecer(this.getParecerMaterial(), null));
		}
	}		
	
	public void limpar(){
		this.setParecerMaterial(new ScoParecerMaterial());
		this.setParecerOcorencia(new ScoParecerOcorrencia());
		this.setListaParecerOcorrencia(new ArrayList<ScoParecerOcorrencia>());
		this.setSituacao(Boolean.TRUE);	
		this.setEditando(Boolean.FALSE);
		this.setPendenteItens(Boolean.FALSE);
		//this.setParecerOcorenciaClone(new ScoParecerOcorrencia());	
		this.setDisabledEdicao(Boolean.TRUE);
		this.setRenderGravar(Boolean.TRUE);
	}
	
	public void editar(ScoParecerOcorrencia scoParecerOcorrencia, boolean editando){
		try {
			//this.setItemListaParecerOcorencia(scoParecerOcorrencia);
			this.setParecerOcorenciaClone(this.parecerFacade.clonarParecerOcorrencia(scoParecerOcorrencia));
			this.setParecerOcorencia(scoParecerOcorrencia);
			this.setParecerMaterial(scoParecerOcorrencia.getParecerMaterial());
		    this.setEditando(editando);	 
		    this.setRenderGravar(editando);
			if (scoParecerOcorrencia.getIndSituacao() != null) {
				this.setSituacao(scoParecerOcorrencia.getIndSituacao().equals(
						DominioSituacao.A));
			}
			
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
		
	}	
	
	public Boolean exibirEditar(ScoParecerOcorrencia scoParecerOcorrencia){
		
		this.setDisabledEdicao(Boolean.FALSE);
		if (scoParecerOcorrencia.getIndSituacao() != null){
			this.setDisabledEdicao(scoParecerOcorrencia.getIndSituacao().equals(DominioSituacao.A));
			return scoParecerOcorrencia.getIndSituacao().equals(DominioSituacao.A);
		}		
		return Boolean.FALSE;		
	}
	
	public void gravar() {
		try {
			this.setPendenteItens(Boolean.FALSE);
			this.getParecerOcorencia().setParecerMaterial(this.getParecerMaterial());	
			this.getParecerOcorencia().setIndSituacao(this.getSituacao().equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);			
			this.parecerFacade.persistirParecerOcorrencia(this.getParecerOcorencia());
			
			this.setCodigoParecer(this.parecerMaterial.getCodigo());
			this.inicio();	
			this.setDisabledEdicao(Boolean.TRUE);
			this.setRenderGravar(Boolean.TRUE);
			this.setParecerOcorenciaClone(this.parecerFacade.clonarParecerOcorrencia(this.getParecerOcorencia()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_PARECER_OCORRENCIA_SUCESSO_M05");

	}	
		
	public void prepararCancelamentoEdicao() {
		this.setCodigoParecer(this.parecerMaterial.getCodigo());		
		this.getParecerOcorencia().setParecerOcorrencia(this.getParecerOcorenciaClone().getParecerOcorrencia());
		this.getParecerOcorencia().setDtOcorrencia(this.getParecerOcorenciaClone().getDtOcorrencia());
		this.getParecerOcorencia().setServidorResponsavel(this.getParecerOcorenciaClone().getServidorResponsavel());
		this.getParecerOcorencia().setDescricao(this.getParecerOcorenciaClone().getDescricao());
		this.getParecerOcorencia().setLote(this.getParecerOcorenciaClone().getLote());
		this.getParecerOcorencia().setIndSituacao(this.getParecerOcorenciaClone().getIndSituacao());		
		//this.limpar();
		this.inicio();
	}
	
	public String cancelar() {			
		return voltarParaUrl;
	}	
	
	public Boolean compararParecerOcorrencia(){
		return (this.getParecerOcorencia().getParecerOcorrencia() != null ? this
				.getParecerOcorencia().getParecerOcorrencia()
				.equals(this.getParecerOcorenciaClone().getParecerOcorrencia())
				: Boolean.TRUE)
				&& (this.getParecerOcorencia().getDtOcorrencia() != null ? this
						.getParecerOcorencia()
						.getDtOcorrencia()
						.equals(this.getParecerOcorenciaClone()
								.getDtOcorrencia()) : Boolean.TRUE);
	}
	
	public Boolean compararResponsavelDescricao(){
		return (this.getParecerOcorencia().getServidorResponsavel() != null ? this
				.getParecerOcorencia()
				.getServidorResponsavel()
				.equals(this.getParecerOcorenciaClone()
						.getServidorResponsavel()) : Boolean.TRUE)
		&& (this.getParecerOcorencia().getDescricao() != null ? this
				.getParecerOcorencia().getDescricao()
				.equals(this.getParecerOcorenciaClone().getDescricao())
				: Boolean.TRUE);
	}
	
	public Boolean compararLoteSituacao(){
		DominioSituacao situacaoAtual = (this.getSituacao().equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
		
		return (this.getParecerOcorencia().getLote() != null ? this
				.getParecerOcorencia().getLote()
				.equals(this.getParecerOcorenciaClone().getLote())
				: Boolean.TRUE) && (this.getParecerOcorenciaClone()
		.getIndSituacao() != null ? this.getParecerOcorenciaClone()
		.getIndSituacao().equals(situacaoAtual) : Boolean.TRUE) && 
		(this.getParecerOcorenciaClone().getIndSituacao() == null ? situacaoAtual.equals(DominioSituacao.A) : Boolean.TRUE);
	}
	
	
	public String validarItensPendentes() {
		Boolean isValidaPendente = Boolean.FALSE;
				
		isValidaPendente = !(compararParecerOcorrencia()
				&& compararResponsavelDescricao()
				&& compararLoteSituacao());		
				
		this.setPendenteItens(isValidaPendente);
				
		if (!isValidaPendente){
			return this.cancelar();
		}
		else
		{
			return "";
		}				   
	}   
    
	// Metódo para Suggestion Box de Servidor
	public List<RapServidores> listaServidores(String objPesquisa) {
		try {
			return this.registroColaboradorFacade
					.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}
	
	public String redirecionaAnexosOcorrencia(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
		
	
	// ### GETs e SETs ###	
	public ScoParecerMaterial getParecerMaterial() {
		return parecerMaterial;
	}

	public void setParecerMaterial(ScoParecerMaterial parecerMaterial) {
		this.parecerMaterial = parecerMaterial;
	}

	public ScoParecerOcorrencia getParecerOcorencia() {
		return parecerOcorencia;
	}


	public void setParecerOcorencia(ScoParecerOcorrencia parecerOcorencia) {
		this.parecerOcorencia = parecerOcorencia;
	}


	public ScoParecerOcorrencia getParecerOcorenciaClone() {
		return parecerOcorenciaClone;
	}

	public void setParecerOcorenciaClone(ScoParecerOcorrencia parecerOcorenciaClone) {
		this.parecerOcorenciaClone = parecerOcorenciaClone;
	}

	public List<ScoParecerOcorrencia> getListaParecerOcorrencia() {
		return listaParecerOcorrencia;
	}


	public void setListaParecerOcorrencia(
			List<ScoParecerOcorrencia> listaParecerOcorrencia) {
		this.listaParecerOcorrencia = listaParecerOcorrencia;
	}

	public Boolean getModoEdit() {
		return modoEdit;
	}

	public void setModoEdit(Boolean modoEdit) {
		this.modoEdit = modoEdit;
	}

	public Integer getCodigoParecer() {
		return codigoParecer;
	}

	public void setCodigoParecer(Integer codigoParecer) {
		this.codigoParecer = codigoParecer;
	}


	public Boolean getSituacao() {
		return situacao;
	}


	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public boolean isPendenteItens() {
		return isPendenteItens;
	}

	public void setPendenteItens(Boolean isPendenteItens) {
		this.isPendenteItens = isPendenteItens;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public Boolean getDisabledEdicao() {
		return disabledEdicao;
	}

	public void setDisabledEdicao(Boolean disabledEdicao) {
		this.disabledEdicao = disabledEdicao;
	}

	public Boolean getRenderGravar() {
		return renderGravar;
	}

	public void setRenderGravar(Boolean renderGravar) {
		this.renderGravar = renderGravar;
	}

	public Boolean getParecerAtivo() {
		return parecerAtivo;
	}

	public void setParecerAtivo(Boolean parecerAtivo) {
		this.parecerAtivo = parecerAtivo;
	}
	
}
