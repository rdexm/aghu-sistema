package br.gov.mec.aghu.compras.solicitacaomaterial.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.core.action.ActionController;

public class ValidacaoNotaFiscalController extends ActionController {

	private static final long serialVersionUID = 4731533190708355034L;
	
	private final String PAGE_VISUALIZAR_CONSULTA = "validacaoNotaFiscalList";
	
	@EJB
	private IComprasFacade comprasFacade;
	private Integer numeroNotaFiscal;
	private Date dataNotaFiscal;
	private List<ScoPedItensMatExpediente> lista;
	private List<ScoPedItensMatExpediente> listaItensNotaFiscal;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.setLista(this.getComprasFacade().pesquisarItensNotaFiscalByNumeroNotaFiscal(numeroNotaFiscal));
	}
	
	public void pesquisarItens(final ScoPedItensMatExpediente item) {
		this.setListaItensNotaFiscal(this.getComprasFacade().pesquisarItensPedidoByNotaFiscalCodigoMaterial(numeroNotaFiscal, item.getMaterial().getCodigo()));
	}

	public Integer getNumeroNotaFiscal (){
		return numeroNotaFiscal;
	}
	
	public String voltar() {
		return PAGE_VISUALIZAR_CONSULTA;
	}
	
	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public List<ScoPedItensMatExpediente> getLista() {
		return lista;
	}

	public void setLista(List<ScoPedItensMatExpediente> lista) {
		this.lista = lista;
	}
	
	public List<ScoPedItensMatExpediente> getListaItensNotaFiscal() {
		return listaItensNotaFiscal;
	}
	
	public void setListaItensNotaFiscal(
			List<ScoPedItensMatExpediente> listaItensNotaFiscal) {
		this.listaItensNotaFiscal = listaItensNotaFiscal;
	}

	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	public Date getDataNotaFiscal() {
		return dataNotaFiscal;
	}

	public void setDataNotaFiscal(Date dataNotaFiscal) {
		this.dataNotaFiscal = dataNotaFiscal;
	}
}
