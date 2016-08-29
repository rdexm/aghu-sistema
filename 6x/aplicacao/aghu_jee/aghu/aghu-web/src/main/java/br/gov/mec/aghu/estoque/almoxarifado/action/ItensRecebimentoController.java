package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ItensRecebimentoController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(ItensRecebimentoController.class);

	private static final long serialVersionUID = 7121562107848170654L;
	
	private static final String PAGE_CONFIG_COMPRAS_PRIORIZARENTREGASOLICITACAOMATERIALSERVICO = "compras-priorizarEntregaSolicitacaoMaterialServico";
	//estoque-itensRecebimento=/estoque/almoxarifado/itensRecebimento.xhtml
	
	private static final String PAGE_CONFIG_ESTOQUE_IMPRIMIRNOTARECEBIMENTOPDF = "estoque-imprimirNotaRecebimentoPdf";

	
	
	
	private Integer numeroSeq;
		
	private Long cpfFornecedor;
	private Long cnpjFornecedor;

	private BigDecimal valorNotaFiscal = BigDecimal.ZERO;
	private Boolean indFornecedor = false;
	
	private Double valorAux2;
	private Date dtGeracao;
	
	private String nomeFornecedor;
	private String voltarParaUrl;
	
	private SceNotaRecebProvisorio notaRecebProv = new SceNotaRecebProvisorio();
	
	List<ItemRecebimentoProvisorioVO> listaItensReceb = new ArrayList<ItemRecebimentoProvisorioVO>();
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	private boolean isNotaRecebProvisorioServico = false;
	
	@Inject @SelectionQualifier
    private RecebimentoDetalhaVO recebimentoDetalhaVO;
	
	

	@PostConstruct
	protected void init() {
		this.begin(conversation);
	}
	
	public void inicializar() {
	 

		if (this.recebimentoDetalhaVO.isPreenchido()) {
			this.numeroSeq = this.recebimentoDetalhaVO.getNotaRecebimentoProvisorioSeq();
			this.indFornecedor = this.recebimentoDetalhaVO.getIndFornecedor();
		}
		
		if (this.numeroSeq != null) {
			notaRecebProv = estoqueFacade.obterNotaRecebProvisorio(this.numeroSeq);
			isNotaRecebProvisorioServico = estoqueFacade.isNotaRecebProvisorioServico(notaRecebProv);
		}
		
		if (notaRecebProv == null) {
			apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_RECEB_NAO_ENCONTRADO", this.numeroSeq);
		} else {
			try {
				dtGeracao = notaRecebProv.getDtGeracao();
			
				if (this.indFornecedor) {
					if (notaRecebProv.getScoAfPedido() != null && notaRecebProv.getScoAfPedido() != null
							&& notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor() != null
							&& notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor() != null) {
						if (notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getCgc() != null) {
							cnpjFornecedor = notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getCgc();
						} else {
							cpfFornecedor = notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getCpf();
						}
						nomeFornecedor = notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getRazaoSocial();
					} else {
						cnpjFornecedor = cpfFornecedor = null;
						nomeFornecedor = null;
					}
				}
				listaItensReceb = estoqueFacade.pesquisarItensNotaRecebimentoProvisorio(this.numeroSeq, false);
				
				atualizarVlrTotalNota();
				
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
				apresentarExcecaoNegocio(e);
			}
		}
	
	}
	
	public void atualizarVlrTotalNota() {
		
		if (listaItensReceb != null) {
			for (ItemRecebimentoProvisorioVO item: listaItensReceb){
				valorAux2 = item.getValor();
				BigDecimal valorAux = new BigDecimal(valorAux2);
				this.setValorNotaFiscal(this.getValorNotaFiscal().add(valorAux));
			}		
		}
	}
	
	public String priorizarEntregaSolicitacaoMaterialServico() {
		//view="/compras/autfornecimento/priorizarEntregaSolicitacaoMaterialServico.xhtml"
		return PAGE_CONFIG_COMPRAS_PRIORIZARENTREGASOLICITACAOMATERIALSERVICO;
	}
	
	public String imprimirNotaRecebimentoPdf() {
		//view="/estoque/relatorios/imprimirNotaRecebimentoPdf.xhtml"
		return PAGE_CONFIG_ESTOQUE_IMPRIMIRNOTARECEBIMENTOPDF;
	}
	
	public String geracaoDevolucao() {
		return "geracaoDevolucao";
	}
	
	/**
	 * Indica nota de recebimento provisório é de serviço.
	 * 
	 * @return Flag
	 */
	public boolean isNotaRecebProvisorioServico() {
		return isNotaRecebProvisorioServico;
	}
	
	//botões
	public String voltar() {
		String returnValue = "voltar";
		
		if (this.getVoltarParaUrl() != null) {
			returnValue = this.getVoltarParaUrl();
		}
		
		return returnValue;
	}
	
	//gets and sets
	public Integer getNumeroSeq() {
		return numeroSeq;
	}

	public void setNumeroSeq(Integer numeroSeq) {
		this.numeroSeq = numeroSeq;
	}
	
		
	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}

	public void setCpfFornecedor(Long cpfFornecedor) {
		this.cpfFornecedor = cpfFornecedor;
	}
	
	public Long getCnpjFornecedor() {
		return cnpjFornecedor;
	}

	public void setCnpjFornecedor(Long cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}
	
	public BigDecimal getValorNotaFiscal() {
		return valorNotaFiscal;
	}

	public void setValorNotaFiscal(BigDecimal valorNotaFiscal) {
		this.valorNotaFiscal = valorNotaFiscal;
	}
	
	public Boolean getIndFornecedor() {
		return indFornecedor;
	}

	public void setIndFornecedor(Boolean indFornecedor) {
		this.indFornecedor = indFornecedor;
	}
	
	public Double getValorAux2() {
		return valorAux2;
	}

	public void setValorAux2(Double valorAux2) {
		this.valorAux2 = valorAux2;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	
	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public SceNotaRecebProvisorio getNotaRecebProv() {
		return notaRecebProv;
	}

	public void setNotaRecebProv(SceNotaRecebProvisorio notaRecebProv) {
		this.notaRecebProv = notaRecebProv;
	}
	
	public List<ItemRecebimentoProvisorioVO> getListaItensReceb() {
		return listaItensReceb;
	}

	public void setListaItensReceb(List<ItemRecebimentoProvisorioVO> listaItensReceb) {
		this.listaItensReceb = listaItensReceb;
	}
}