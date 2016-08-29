package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoLicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ProcessoAdmComprasController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ProcessoAdmComprasController.class);
	
	private static final long serialVersionUID = 9041577601091390580L;

	private static final String PAGE_ASSOCIA_SC_SS_PAC = "associaScSsPac";
	private static final String PAGE_ITEM_PAC_LIST = "itemPacList";
	private static final String PAGE_DUPLICAR_PAC = "duplicarPAC";
	private static final String PAGE_CONDICOES_PAGAMENTO_PAC_LIST = "condicoesPagamentoPacList";
	private static final String PAGE_ANDAMENTO_PAC_LIST = "andamentoPacList";
	private static final String PAGE_ENCAMINHAR_LICITACOES_LIBERAR = "encaminharLicitacoesLiberar";	
	private static final String PAGE_RELATORIO_ESPELHO_PAC = "compras-relatorioEspelhoPAC";
	private static final String PAGE_ANEXAR_DOC_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";
	private static final String PAGE_GERAR_LOTES_PAC = "gerarLotesDoPAC";
	private static final String PAGE_ENCAMINHAR_JULGAMENTO_PAC = "consultarEncaminharPacAguardoJulgamento";	
	private static final String PAGE_PREGAO_BB = "licitacoesPregaoEletronicoBBList";
	private static final String PAGE_PROCESSO_ADM_PAC = "compras-processoAdmCompraCRUD";
	private static final String PAGE_GER_AUT_FORNECIMENTO_LIST = "compras-gerAutFornecimentoList";

	/*
	 * private static final String PAGE_IMPRIMIR_TERMO_ABERTURA_PAC =
	 * "imprimirTermoAberturaPac";
	 */

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private SecurityController securityController;
		
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private RelatorioTermoAberturaPacController relatorioTermoAberturaPacController;

	private Integer numeroPac; // numero da licitacao

	private ScoLicitacao licitacao;

	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;

	private String gravarVoltarUrl;

	private boolean exigeVarMaxima;

	private boolean focoFreqCompras;
	private boolean redirecionaItensPac;
	private boolean desabilitaItensPac;	

	private ScoLicitacao scoLicitacaoClone;

	private DominioOrigemSolicitacaoSuprimento origem = DominioOrigemSolicitacaoSuprimento.PC;
	
	private boolean requiredExclusao;
	private String remetenteTermoAbertura;

	public enum ProcessoAdmComprasControllerExceptionCode implements BusinessExceptionCode {
		LICITACAO_INCLUIDA_COM_SUCESSO, LICITACAO_ALTERADA_COM_SUCESSO;
	}

	public static enum ControlaCheck {
		URGENTE, PRIORITARIO
	};

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() throws ApplicationBusinessException {
	 

	 


		Boolean isUpdate = numeroPac != null;

		if (isUpdate) {

			licitacao = pacFacade.obterLicitacao(numeroPac);
			licitacao.setItensLicitacao(pacFacade.pesquisarItemLicitacaoPorNumeroPac(null, null, null, false, numeroPac));
			
			requiredExclusao = licitacao.getExclusao();
			try {
				this.scoLicitacaoClone = (ScoLicitacao) licitacao.clone();

			} catch (CloneNotSupportedException e) {
				LOG.error("A classe scoLicitacaoClone " + "não implementa a interface Cloneable.", e);
			}
			
			if ((this.licitacao != null && this.licitacao.getItensLicitacao() != null && this.licitacao.getItensLicitacao().size() > 0)
					|| !securityController.usuarioTemPermissao("cadastrarPAC", "gravar")) {
				redirecionaItensPac = Boolean.TRUE;
			}

			comprasCadastrosBasicosFacade.obterScoModalidadeLicitacaoPorChavePrimaria(licitacao.getModalidadeLicitacao()
					.getCodigo());
			try {
				AghParametros paramTermoAbertura = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_REMETENTE_TERMO_ABERTURA);
				this.setRemetenteTermoAbertura(paramTermoAbertura.getVlrTexto());
			} catch (ApplicationBusinessException e) {
				this.setRemetenteTermoAbertura(this.getBundle().getString("MGS_TERMO_ABERTURA_REMETENTE_DEFAULT"));
			}
		}

		else {
			licitacao = new ScoLicitacao();
		}		
	
	}
	

	/**
	 * Indica se licitação possui um ou mais itens.
	 * 
	 * @return Flag
	 */
	public boolean hasItensLicitacao() {
		if (licitacao.getNumero() != null) {
			Long count = pacFacade.countItemLicitacaoPorNumeroPac(licitacao.getNumero());

			return count > 0;
		} else {
			return false;
		}
	}

	public String gravar() {

		try {
			boolean edicao = false;
			if (licitacao.getNumero() != null) {
				edicao = true;
				if (this.licitacao.getExclusao() &&
					StringUtils.isBlank(this.licitacao.getMotivoExclusao())){
					
					this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_MOTIVO_EXCLUSAO"));
					return null;
				}
				this.licitacao.setExclusao(this.licitacao.getMotivoExclusao() != null);
			}

			pacFacade.persistirPac(licitacao, scoLicitacaoClone);

			if (!edicao) {			
				this.setNumeroPac(licitacao.getNumero());
				this.apresentarMsgNegocio(Severity.INFO,
						ProcessoAdmComprasControllerExceptionCode.LICITACAO_INCLUIDA_COM_SUCESSO.toString());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						ProcessoAdmComprasControllerExceptionCode.LICITACAO_ALTERADA_COM_SUCESSO.toString());
			}

			return null;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String voltar() {

		return voltarParaUrl;
	}

	/**
	 * SuggestionBox Modalidade
	 * 
	 * @param paramPesquisa
	 * @return
	 */

	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String modalidade) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(modalidade, DominioSituacao.A);
	}

	// Suggestion Servidor Gestor
	public List<RapServidores> pesquisarGestorPac(String parametro) {
		return this.registroColaboradorFacade.pesquisarResponsaveis(parametro);
	}

	public void validaFrequenciaCompras() {
		try {
			focoFreqCompras = false;
			this.pacFacade.validaFrequenciaCompras(licitacao);
		} catch (ApplicationBusinessException e) {
			focoFreqCompras = true;
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean bloquearItens() {
		return (this.voltarParaUrl != null && this.voltarParaUrl.equalsIgnoreCase(PAGE_ITEM_PAC_LIST));
	}

	public void validaTipoPac() {

		exigeVarMaxima = false;

		if (this.licitacao.getTipo().equals(DominioTipoLicitacao.RP)) {
			exigeVarMaxima = true;

		}

	}

	public Boolean verificarUtilizaParecerTecnico() {
		try {
			return this.pacFacade.verificarUtilizaParecerTecnico();
		} catch (ApplicationBusinessException e) {
			return false;
		}
	}
	
	public String imprimirTermoAberturaRemetente() throws BaseException, JRException, SystemException, IOException, DocumentException{
		relatorioTermoAberturaPacController.setRemetente(this.getRemetenteTermoAbertura());
		relatorioTermoAberturaPacController.setVoltarParaUrl(PAGE_PROCESSO_ADM_PAC);
		return relatorioTermoAberturaPacController.print(this.licitacao.getNumero());
	}
		
	public void limparMotivoExclusao(){
		requiredExclusao = this.licitacao.getExclusao();
		if (!this.licitacao.getExclusao()){
			this.licitacao.setMotivoExclusao(null);
		}
	}
	
	public void marcaIndExclusao(){
		this.licitacao.setExclusao(this.licitacao.getMotivoExclusao() != null);
		requiredExclusao = (this.licitacao.getMotivoExclusao() != null);
	}
	
	public void limparDocumentoEditalAno(){
		this.licitacao.setAnoComplemento(null);
		this.licitacao.setNumDocLicit(null);
		this.licitacao.setNumEdital(null);
	}

	public String redirecionaAssociarScSsPac() {

		/*if (this.numeroPac != null) {
			licitacao = pacFacade.obterLicitacao(numeroPac);
			licitacao.setItensLicitacao(pacFacade.pesquisarItemLicitacaoPorNumeroPac(null, null, null, false, numeroPac));
		}

		if ((this.licitacao != null && this.licitacao.getItensLicitacao() != null && this.licitacao.getItensLicitacao().size() > 0)
				|| !securityController.usuarioTemPermissao("cadastrarPAC", "gravar")) {
			return PAGE_ITEM_PAC_LIST;
		}*/

		return PAGE_ASSOCIA_SC_SS_PAC;
	}
	
	public String redirecionaItemPacList(){
		return PAGE_ITEM_PAC_LIST;
	}
	
	public String redirecionaCadastroProposta() {
		return PAGE_ENCAMINHAR_LICITACOES_LIBERAR;
	}

	public String redirecionaDuplicarPAC() {
		return PAGE_DUPLICAR_PAC;
	}

	public String redirecionaCondicoesPagamentoPAC() {
		return PAGE_CONDICOES_PAGAMENTO_PAC_LIST;
	}

	public String redirecionaAndamentoPAC() {
		return PAGE_ANDAMENTO_PAC_LIST;
	}

	public String redirecionaAnexosPAC() {
		return PAGE_ANEXAR_DOC_SOLICITACAO_COMPRA;
	}

	

	public String relatorioEspelhoPAC() {
		return PAGE_RELATORIO_ESPELHO_PAC;
	}
	
	public String redirecionaGerarLotes(){
		return PAGE_GERAR_LOTES_PAC;
	}
	
	public String redirecionaJulgamento(){
		return PAGE_ENCAMINHAR_JULGAMENTO_PAC;
	}	
	
		
	public String redirecionaGerarAF(){
		return PAGE_GER_AUT_FORNECIMENTO_LIST;
	}
	
	public String redirecionaPregaoBB(){
		return PAGE_PREGAO_BB;
	}
	
	
	
	

	/** Indica se licitação possui uma ou mais propostas. */
	public boolean hasPropostas() {
		return pacFacade.contarPropostas(this.licitacao, null) > 0;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public String getGravarVoltarUrl() {
		return gravarVoltarUrl;
	}

	public void setGravarVoltarUrl(String gravarVoltarUrl) {
		this.gravarVoltarUrl = gravarVoltarUrl;
	}

	public boolean isExigeVarMaxima() {
		return exigeVarMaxima;
	}

	public void setExigeVarMaxima(boolean exigeVarMaxima) {
		this.exigeVarMaxima = exigeVarMaxima;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public boolean isFocoFreqCompras() {
		return focoFreqCompras;
	}

	public void setFocoFreqCompras(boolean focoFreqCompras) {
		this.focoFreqCompras = focoFreqCompras;
	}

	public ScoLicitacao getScoLicitacaoClone() {
		return scoLicitacaoClone;
	}

	public void setScoLicitacaoClone(ScoLicitacao scoLicitacaoClone) {
		this.scoLicitacaoClone = scoLicitacaoClone;
	}

	public DominioOrigemSolicitacaoSuprimento getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemSolicitacaoSuprimento origem) {
		this.origem = origem;
	}

	public boolean isRedirecionaItensPac() {
		return redirecionaItensPac;
	}

	public void setRedirecionaItensPac(boolean redirecionaItensPac) {
		this.redirecionaItensPac = redirecionaItensPac;
	}
	
	public boolean isDesabilitaItensPac() {
		return desabilitaItensPac;
	}

	public boolean isRequiredExclusao() {
		return requiredExclusao;
	}

	public void setRequiredExclusao(boolean requiredExclusao) {
		this.requiredExclusao = requiredExclusao;
	}

	public void setDesabilitaItensPac(boolean desabilitaItensPac) {
		this.desabilitaItensPac = desabilitaItensPac;
	}

	public String getRemetenteTermoAbertura() {
		return remetenteTermoAbertura;
	}

	public void setRemetenteTermoAbertura(String remetenteTermoAbertura) {
		this.remetenteTermoAbertura = remetenteTermoAbertura;
	}

}