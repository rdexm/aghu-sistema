package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class GeracaoDevolucaoController 
		extends ActionController {

	private static final Log LOG = LogFactory.getLog(GeracaoDevolucaoController.class);
	
	private static final long serialVersionUID = -729441123582120797L;
	
	public enum GeracaoDevolucaoControllerExceptionCode implements BusinessExceptionCode {
		MSG_GERACAO_DEVOLUCAO_SUCESSO}
	
	private static final String CONFIRMACAO_DEVOLUCAO = "confirmacaoDevolucao";
	
	@EJB
	IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	IEstoqueFacade estoqueFacade;
	

	// parametros
	private Integer seqNotaRecebProvisorio;	
	private String voltarParaUrl;
	
	// campos tela	
	private Integer seqNotaRecebimento;
	
	private Long numeroDfe;
	private String serieDfe;
	private DominioTipoDocumentoEntrada tipoDfe;
	private Date dataEmissaoDfe;
	private Date dataEntradaDfe;
	private Double valorTotalNfDfe;	
	private String descricaoProblema;
	private SceMotivoProblema sceMotivoProblema;
	private PendenciasDevolucaoVO itemAtual = null;
	private SceNotaRecebimento notaRecebimento = null;
	private SceDocumentoFiscalEntrada dfe = null;
		
	private List<PendenciasDevolucaoVO> listaPendencias;	
		
	// campos auxiliares
	private Boolean ativo;

	/** Indica se devolução foi gerada. */
	private boolean geracaoOk;
	
		

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	public void limpar() {
		this.geracaoOk = false;
		this.setNotaRecebimento(null);
		this.setDfe(null);
		this.setDescricaoProblema(null);		
	}
	
	
	// metodos auxilio
	
	public void iniciar() {
	 
	
		this.limpar();
		this.popularCamposNotaRecebimento();
		this.pesquisar();
	
	}
	
	public void pesquisar() {
		this.ativo = Boolean.TRUE;		
		this.listaPendencias = this.estoqueFacade.pesquisarGeracaoPendenciasDevolucao(this.seqNotaRecebimento);
		
	}
	
	private void popularCamposNotaRecebimento() {
		this.setNotaRecebimento(this.estoqueFacade.obterNotaRecebimentoPorNotaRecebimentoProvisorio(this.seqNotaRecebProvisorio));
		
		if (this.getNotaRecebimento() != null) {
			this.seqNotaRecebimento = this.getNotaRecebimento().getSeq();
			this.popularCamposDocumentoFiscalEntrada(this.getNotaRecebimento());	
		}
	}
	
	private void popularCamposDocumentoFiscalEntrada(SceNotaRecebimento nr) {
		this.setDfe(this.estoqueFacade.obterDocumentoFiscalEntradaPorSeq(nr.getDocumentoFiscalEntrada().getSeq()));
		
		if (this.getDfe() != null) {
			this.numeroDfe = this.getDfe().getNumero();
			this.serieDfe = this.getDfe().getSerie();
			this.tipoDfe = this.getDfe().getTipo();
			this.dataEmissaoDfe = this.getDfe().getDtEmissao();
			this.dataEntradaDfe = this.getDfe().getDtEntrada();
			this.valorTotalNfDfe = this.getDfe().getValorTotalNf();
		}
	}
	
	public void inicializarModalProblema(PendenciasDevolucaoVO item){
		this.setSceMotivoProblema(item.getMotivoProblema());
		this.setDescricaoProblema(item.getDescricao());
		this.setItemAtual(item);		
		
	}
	
	public void limparItem(PendenciasDevolucaoVO item) {
		item.setQtdeSaida(null);
		item.setDescricao(null);
	}
	
	public void alterarProblema(){
		if (this.getItemAtual() != null){
			this.getItemAtual().setDescricao(this.getDescricaoProblema());	
			this.getItemAtual().setMotivoProblema(this.getSceMotivoProblema());
		}
	}
	
	public void refreshMotivoDevolucao() {
		if (this.getSceMotivoProblema() != null){
			this.setDescricaoProblema(this.getSceMotivoProblema().getDescricao());
		}
		else {
			this.setDescricaoProblema(null);
		}
		
	}
		
	public void atualizarValorTotalItemNr(PendenciasDevolucaoVO item){
		if (item.getQtdeSaida() != null){			
			item.setValorTotalItemNr( item.getQtdeSaida().doubleValue() * item.getValorUnitarioItemCalculado());
		}
	}
	public Boolean isHabilitarBotaoConfirmarDevolucao(){		
		return this.estoqueFacade.verificaExisteBoletimOcorrencia(this.seqNotaRecebimento);		
	}	
	
	public void gerarDevolucao(){
		try {
			
			String nomeMicrocomputador = null;
			
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}	
			
			this.estoqueFacade.gerarDevolucao(this.getListaPendencias(), this.getNotaRecebimento(), this.getDfe(), nomeMicrocomputador);
			geracaoOk = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String confirmarDevolucao() {
		return CONFIRMACAO_DEVOLUCAO;
	}
	
	public String confirmarPendenciaDevolucao() {
		return voltarParaUrl;
	}
	
	
	public List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String paramPesq){
		return this.estoqueFacade.pesquisaMotivosProblemasPorSeqDescricao(paramPesq, DominioSituacao.A);
	}
	
	
	
	// Getters/Setters
	
	public List<PendenciasDevolucaoVO> getListaPendencias() {
		return listaPendencias;
	}

	public void setListaPriorizacao(List<PendenciasDevolucaoVO> listaPendencias) {
		this.listaPendencias = listaPendencias;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isGeracaoOk() {
		return geracaoOk;
	}

	public Integer getSeqNotaRecebProvisorio() {
		return seqNotaRecebProvisorio;
	}

	public void setSeqNotaRecebProvisorio(Integer seqNotaRecebProvisorio) {
		this.seqNotaRecebProvisorio = seqNotaRecebProvisorio;
	}

	public Integer getSeqNotaRecebimento() {
		return seqNotaRecebimento;
	}

	public void setSeqNotaRecebimento(Integer seqNotaRecebimento) {
		this.seqNotaRecebimento = seqNotaRecebimento;
	}

	public Long getNumeroDfe() {
		return numeroDfe;
	}

	public void setNumeroDfe(Long numeroDfe) {
		this.numeroDfe = numeroDfe;
	}

	public String getSerieDfe() {
		return serieDfe;
	}

	public void setSerieDfe(String serieDfe) {
		this.serieDfe = serieDfe;
	}

	public DominioTipoDocumentoEntrada getTipoDfe() {
		return tipoDfe;
	}

	public void setTipoDfe(DominioTipoDocumentoEntrada tipoDfe) {
		this.tipoDfe = tipoDfe;
	}

	public Date getDataEmissaoDfe() {
		return dataEmissaoDfe;
	}

	public void setDataEmissaoDfe(Date dataEmissaoDfe) {
		this.dataEmissaoDfe = dataEmissaoDfe;
	}

	public Date getDataEntradaDfe() {
		return dataEntradaDfe;
	}

	public void setDataEntradaDfe(Date dataEntradaDfe) {
		this.dataEntradaDfe = dataEntradaDfe;
	}

	public Double getValorTotalNfDfe() {
		return valorTotalNfDfe;
	}

	public void setValorTotalNfDfe(Double valorTotalNfDfe) {
		this.valorTotalNfDfe = valorTotalNfDfe;
	}

	public String getDescricaoProblema() {
		return descricaoProblema;
	}

	public void setDescricaoProblema(String descricaoProblema) {
		this.descricaoProblema = descricaoProblema;
	}
	
	public SceMotivoProblema getSceMotivoProblema() {
		return sceMotivoProblema;
	}

	public void setSceMotivoProblema(SceMotivoProblema sceMotivoProblema) {
		this.sceMotivoProblema = sceMotivoProblema;
	}

	public PendenciasDevolucaoVO getItemAtual() {
		return itemAtual;
	}

	public void setItemAtual(PendenciasDevolucaoVO itemAtual) {
		this.itemAtual = itemAtual;
	}	

	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public SceDocumentoFiscalEntrada getDfe() {
		return dfe;
	}

	public void setDfe(SceDocumentoFiscalEntrada dfe) {
		this.dfe = dfe;
	}

	
}