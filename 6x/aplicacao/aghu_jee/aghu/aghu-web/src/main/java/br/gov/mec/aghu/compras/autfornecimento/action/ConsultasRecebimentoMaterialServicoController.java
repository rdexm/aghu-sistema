package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.dominio.DominioTipoItemNotaRecebimento;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Controller responsável pelas consultas de recebimento de material/serviço.
 * 
 * @see RecebeMaterialServicoController
 * @author matheus
 */

public class ConsultasRecebimentoMaterialServicoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ConsultasRecebimentoMaterialServicoController.class);
	private static final long serialVersionUID = -7090547944595333884L;

	@Inject
	private RecebeMaterialServicoController recebeMaterialServicoController;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
		
	private DominioTipoItemNotaRecebimento filtroMaterialServico;
	private boolean mostrarSBMaterial = false;
	private boolean mostrarSBServico = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public List<ScoAutorizacaoForn> pesquisarAFNumeroComplementoFornecedor(String numeroAf) {
		List<ScoAutorizacaoForn> listaAF = null;

		Short complementoAF = recebeMaterialServicoController.getComplementoAF();
		Integer fornecedorId = recebeMaterialServicoController.getNumeroFornecedor();
		
		listaAF = autFornecimentoFacade.pesquisarAFNumComplementoFornecedor(
				numeroAf, complementoAF, fornecedorId, getMaterial(), getServico());

		return listaAF;
	}

	public List<ScoAutorizacaoForn> pesquisarComplementoNumeroAfNroComplementoFornecedor(String numComplementoAf) {
		Integer numeroAF = recebeMaterialServicoController.getNumeroAF();
		Integer fornecedorId = recebeMaterialServicoController.getNumeroFornecedor();
		
		List<ScoAutorizacaoForn> listaNroComplementoAF = autFornecimentoFacade
				.pesquisarComplementoNumAFNumComplementoFornecedor(numeroAF, numComplementoAf, fornecedorId, getMaterial(), getServico());

		return listaNroComplementoAF;
	}

	public List<ScoFornecedor> pesquisarFornecedorNumeroAfNroComplementoFornecedor(String fornFilter) {
		ScoFornecedor fornecedor = recebeMaterialServicoController.getFornecedor();
		Integer numeroAF = recebeMaterialServicoController.getNumeroAF();
		Short complementoAF = recebeMaterialServicoController.getComplementoAF();
		
		if (fornecedor != null) {
			fornFilter = fornecedor.getNumero().toString();
		}

		List<ScoFornecedor> listaFornecedor = autFornecimentoFacade
				.pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAF, complementoAF, fornFilter, getMaterial(), getServico(), recebeMaterialServicoController.getDocumentoFiscalEntrada());

		return listaFornecedor;
	}
	
	public List<ScoMaterial> pesquisarMaterialaReceber(String param) {
		Integer numeroFornecedor = null;
		Integer numeroAF = recebeMaterialServicoController.getNumeroAF();
		Short complementoAF = recebeMaterialServicoController.getComplementoAF();
		
		if (recebeMaterialServicoController.getFornecedor() != null) {
			numeroFornecedor = recebeMaterialServicoController.getFornecedor().getNumero();
		}
		
		return this.autFornecimentoFacade.pesquisarMaterialaReceber(numeroAF, complementoAF, numeroFornecedor, param);	
	}
	
	public List<ScoServico> pesquisarServicoaReceber(String param) {
		Integer numeroFornecedor = null;
		Integer numeroAF = recebeMaterialServicoController.getNumeroAF();
		Short complementoAF = recebeMaterialServicoController.getComplementoAF();
		
		if (recebeMaterialServicoController.getFornecedor() != null) {
			numeroFornecedor = recebeMaterialServicoController.getFornecedor().getNumero();
		}
		
		return this.autFornecimentoFacade.pesquisarServicoaReceber(numeroAF, complementoAF, numeroFornecedor, param);		
	}
	
	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntrada(String strPesquisa) {
		ScoFornecedor fornecedor = recebeMaterialServicoController.getFornecedor();
		List<SceDocumentoFiscalEntrada> listaNotaFiscalEntrada = null;

		listaNotaFiscalEntrada = estoqueFacade
				.pesquisarNotafiscalEntradaNumeroOuFornecedor(strPesquisa,
						fornecedor);

		return listaNotaFiscalEntrada;
	}
	
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return recebeMaterialServicoController.getDocumentoFiscalEntrada();
	}

	public void setDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		recebeMaterialServicoController.setDocumentoFiscalEntrada(documentoFiscalEntrada);
	}
	
	public void inicio() {
		LOG.info("Iniciou action controller: ConsultasRecebimentoMaterialServicoController");
		recebeMaterialServicoController.inicio();
	}
	
	public void verificarFiltroMaterialServico() {
		if (filtroMaterialServico == DominioTipoItemNotaRecebimento.M) {
			mostrarSBMaterial = true;
			mostrarSBServico = false;
			setServico(null);
		}
		
		if (filtroMaterialServico == DominioTipoItemNotaRecebimento.S) {
			mostrarSBMaterial = false;
			mostrarSBServico = true;
			setMaterial(null);
		}
		
		if (filtroMaterialServico != DominioTipoItemNotaRecebimento.M && filtroMaterialServico != DominioTipoItemNotaRecebimento.S) {
			mostrarSBMaterial = false;
			mostrarSBServico = false;
			setServico(null);
			setMaterial(null);
		}
	}
	
	public void limparNotaFiscal() {
		recebeMaterialServicoController.limparNotaFiscal();
	}

	public ScoAutorizacaoForn getAutorizacaoForn() {
		return recebeMaterialServicoController.getAutorizacaoForn();
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		recebeMaterialServicoController.setAutorizacaoForn(autorizacaoForn);
	}
	
	public void selecionarAF() {
		recebeMaterialServicoController.selecionarAF();
	}

	public ScoAutorizacaoForn getNroComplementoAF() {
		return recebeMaterialServicoController.getNroComplementoAF();
	}

	public void setNroComplementoAF(ScoAutorizacaoForn nroComplementoAF) {
		recebeMaterialServicoController.setNroComplementoAF(nroComplementoAF);
	}
	
	public void selecionarComplemento() {
		recebeMaterialServicoController.selecionarComplemento();
	}
	
	public void selecionarMaterial() {
		recebeMaterialServicoController.selecionarMaterial();
	}

	public void selecionarServico(){
		recebeMaterialServicoController.selecionarServico();
	}
	
	public ScoFornecedor getFornecedor() {
		return recebeMaterialServicoController.getFornecedor();
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		recebeMaterialServicoController.setFornecedor(fornecedor);
	}

	public void selecionarFornecedor() {
		recebeMaterialServicoController.selecionarFornecedor();
	}
	
	
	public ScoMaterial getMaterial() {
		return recebeMaterialServicoController.getMaterial();
	}

	public void setMaterial(ScoMaterial material) {
		recebeMaterialServicoController.setMaterial(material);
	}

	public ScoServico getServico() {
		return recebeMaterialServicoController.getServico();
	}
	
	public void setServico(ScoServico servico) {
		recebeMaterialServicoController.setServico(servico);
	}

	public DominioTipoItemNotaRecebimento getFiltroMaterialServico() {
		return filtroMaterialServico;
	}

	public void setFiltroMaterialServico(DominioTipoItemNotaRecebimento filtroMaterialServico) {
		this.filtroMaterialServico = filtroMaterialServico;		
	}

	public boolean isMostrarSBServico() {
		return mostrarSBServico;
	}

	public void setMostrarSBServico(boolean mostrarSBServico) {
		this.mostrarSBServico = mostrarSBServico;
	}

	public boolean isMostrarSBMaterial() {
		return mostrarSBMaterial;
	}

	public void setMostrarSBMaterial(boolean mostrarSBMaterial) {
		this.mostrarSBMaterial = mostrarSBMaterial;
	}
}