package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConfirmacaoDevolucaoController 
		extends ActionController {

	private static final Log LOG = LogFactory.getLog(ConfirmacaoDevolucaoController.class);
	
	private static final long serialVersionUID = -729441123582120797L;
	
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
	
	private Long numeroDfs;
	private String serieDfs;
	private Date dataDfs;
		
	private List<PendenciasDevolucaoVO> listaPendencias;	
		
	// campos auxiliares
	private Boolean ativo;
	private Integer seqDfe;
	
	
	// botoes
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

		
	public String cancelarDevolucao() {
		String ret = "";
		try {
			List<String> listaRetorno;
			
			listaRetorno = this.estoqueFacade.cancelarDevolucaoFornecedor(this.listaPendencias, this.numeroDfs, this.serieDfs, this.dataDfs,
						this.obterNomeComputador(), this.seqNotaRecebProvisorio);
			
			for (String msg : listaRetorno) {
				apresentarMsgNegocio(
						Severity.INFO, msg);	
			}
			
			ret = voltarParaUrl;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return ret;
	}
	
	public String confirmarDevolucao() {
		String ret = "";
		try {
			List<String> listaRetorno = this.estoqueFacade.confirmarDevolucaoFornecedor(this.listaPendencias, this.seqDfe,
					this.seqNotaRecebimento, this.numeroDfs, this.serieDfs, this.dataDfs, this.obterNomeComputador());
			
			for (String msg : listaRetorno) {
				apresentarMsgNegocio(
						Severity.INFO, msg);	
			}
			ret = voltarParaUrl;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return ret;
	}
	
	public void limpar() {
		this.numeroDfs = null;
		this.serieDfs = null;
		this.dataDfs = null;
	}
	
	private String obterNomeComputador() {
		String nomeComputador = null;
		
		try {
			nomeComputador = this.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Impossivel obter o nome do computador.");
		}
		
		return nomeComputador;
	}
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public void iniciar() {	
		this.limpar();
		this.popularCamposNotaRecebimento();
		this.pesquisar();			
	}
	
	public void pesquisar() {
		this.ativo = Boolean.TRUE;
		this.listaPendencias = this.estoqueFacade.pesquisarPendenciasDevolucao(this.seqNotaRecebimento);
	}
	
	private void popularCamposNotaRecebimento() {
		SceNotaRecebimento notaRecebimento = this.estoqueFacade.obterNotaRecebimentoPorNotaRecebimentoProvisorio(this.seqNotaRecebProvisorio);
		
		if (notaRecebimento != null) {
			this.seqNotaRecebimento = notaRecebimento.getSeq();
			this.popularCamposDocumentoFiscalEntrada(notaRecebimento);	
		}
	}
	
	private void popularCamposDocumentoFiscalEntrada(SceNotaRecebimento nr) {
		SceDocumentoFiscalEntrada dfe = this.estoqueFacade.obterDocumentoFiscalEntradaPorSeq(nr.getDocumentoFiscalEntrada().getSeq());
		
		if (dfe != null) {
			this.seqDfe = dfe.getSeq();
			this.numeroDfe = dfe.getNumero();
			this.serieDfe = dfe.getSerie();
			this.tipoDfe = dfe.getTipo();
			this.dataEmissaoDfe = dfe.getDtEmissao();
			this.dataEntradaDfe = dfe.getDtEntrada();
			this.valorTotalNfDfe = dfe.getValorTotalNf();
		}
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

	public Long getNumeroDfs() {
		return numeroDfs;
	}

	public void setNumeroDfs(Long numeroDfs) {
		this.numeroDfs = numeroDfs;
	}

	public String getSerieDfs() {
		return serieDfs;
	}

	public void setSerieDfs(String serieDfs) {
		this.serieDfs = serieDfs;
	}

	public Date getDataDfs() {
		return dataDfs;
	}

	public void setDataDfs(Date dataDfs) {
		this.dataDfs = dataDfs;
	}

	public Integer getSeqDfe() {
		return seqDfe;
	}

	public void setSeqDfe(Integer seqDfe) {
		this.seqDfe = seqDfe;
	}
}