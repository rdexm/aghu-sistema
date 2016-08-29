package br.gov.mec.aghu.sig.custos.action;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;


public class ManterDirecionadorAtividadeController extends ActionController {

	private static final String PESQUISAR_DIRECIONADOR_ATIVIDADE = "pesquisarDirecionadorAtividade";

	private static final Log LOG = LogFactory.getLog(ManterDirecionadorAtividadeController.class);

	private static final long serialVersionUID = -1502017164335662570L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	private SigDirecionadores sigDirecionador;
	private Integer seqDirecionador;
	
	private boolean exibirBotaoNovo;
	private boolean recarregarLista = false;
	
	private boolean mostrarBotaoVoltar = true;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 
		Map<String, String[]> mapParams =   FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		if(mapParams.containsKey("seqDirecionador")){
			this.seqDirecionador = Integer.parseInt(mapParams.get("seqDirecionador")[0]);
			this.mostrarBotaoVoltar = false;
		}

		if (seqDirecionador == null) {
			setSigDirecionador(new SigDirecionadores());
			getSigDirecionador().setIndSituacao(DominioSituacao.A);
			getSigDirecionador().setIndNroExecucoes(false);
			getSigDirecionador().setIndColetaSistema(false);
			
		} else {
			setSigDirecionador(this.custosSigCadastrosBasicosFacade.obterDirecionador(seqDirecionador));
		}
	
	}

	public String gravar(){
		
		try{
			this.custosSigCadastrosBasicosFacade.persistirDirecionador(getSigDirecionador(), registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			if (seqDirecionador == null) {
				seqDirecionador = getSigDirecionador().getSeq();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIRECIONADOR_GRAVADO_SUCESSO", this.getSigDirecionador().getNome());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIRECIONADOR_ALTERADO_SUCESSO", this.getSigDirecionador().getNome());
			}
			seqDirecionador = null;
			setSigDirecionador(null);
			this.setRecarregarLista(true);
			return PESQUISAR_DIRECIONADOR_ATIVIDADE;
			
		}
		catch(ApplicationBusinessException exception){
			this.apresentarExcecaoNegocio(exception);
			return null;
		}	
	}
	
	public String voltar() {
		seqDirecionador = null;
		return PESQUISAR_DIRECIONADOR_ATIVIDADE;
	}

	public boolean verificarTipoCalculoHabilitado() {		
		if(getSigDirecionador() != null){
			if(getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.AT || getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.RT){
				return true;
			}
		}
		return false;
	}

	public boolean verificarOperacaoHabilitado() {
		return getSigDirecionador() != null && getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.AT;
	}

	public boolean verificarDirecionadorTempoHabilitado() {
		return getSigDirecionador() != null && getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.RC;
	}

	public boolean verificarInformaExecucoesHabilitado() {
		if(getSigDirecionador() != null && getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.AT){
			return true;
		}
		else{
			if(getSigDirecionador() != null){
				this.getSigDirecionador().setIndNroExecucoes(false);
			}
			return false;
		}
	}
	
	public boolean verificarColetadoViaSistemaHabilitado(){
		if(getSigDirecionador() != null && getSigDirecionador().getIndTipo() == DominioTipoDirecionadorCustos.RT  && getSigDirecionador().getIndTipoCalculo() == DominioTipoCalculoObjeto.PE){
			return true;
		}
		else{
			if(getSigDirecionador() != null ){
				this.getSigDirecionador().setIndColetaSistema(false);
			}
			return false;
		}
	}

	public List<DominioTipoCalculoObjeto> carregarListaTipoCalculoObjeto(){
		if(this.getSigDirecionador()!=null){
			return this.custosSigCadastrosBasicosFacade.listarTiposCalculoObjeto(this.getSigDirecionador().getIndTipo());
		}
		return null;
	}
	
	public void limparCamposColetaViaSistema(){
		this.sigDirecionador.setNomeView(null);
		this.sigDirecionador.setFiltro(null);
	}
	
	// Getters and Setters

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getSeqDirecionador() {
		return seqDirecionador;
	}

	public void setSeqDirecionador(Integer seqDirecionador) {
		this.seqDirecionador = seqDirecionador;
	}

	public boolean isRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}

	public SigDirecionadores getSigDirecionador() {
		return sigDirecionador;
	}

	public void setSigDirecionador(SigDirecionadores sigDirecionador) {
		this.sigDirecionador = sigDirecionador;
	}

	public boolean isMostrarBotaoVoltar() {
		return mostrarBotaoVoltar;
	}

	public void setMostrarBotaoVoltar(boolean mostrarBotaoVoltar) {
		this.mostrarBotaoVoltar = mostrarBotaoVoltar;
	}
}
