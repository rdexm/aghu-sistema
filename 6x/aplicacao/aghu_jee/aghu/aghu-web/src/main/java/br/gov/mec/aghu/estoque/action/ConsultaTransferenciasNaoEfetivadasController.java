package br.gov.mec.aghu.estoque.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Controller da estoria 5638
 * 
 * @author guilherme.finotti - Squadra
 *
 */

public class ConsultaTransferenciasNaoEfetivadasController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 271457186281820658L;
	
	private static final Log LOG = LogFactory.getLog(ConsultaTransferenciasNaoEfetivadasController.class);	
	private static final String EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO = "estoque-efetivarTransferenciaAutoAlmoxarifado";

	@Inject @Paginator
	private DynamicDataModel<SceTransferencia> dataModel;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private RelatorioTransferenciaMaterialController relatorioTransferenciaMaterialController;
	
	private SceTransferencia transferencia;
	
	private DominioSimNao indTransfAutomatica;
	
	private Date dataGeracao;
	
	private Integer seqTransferencia;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando convesation");
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		if(getTransferencia()==null){
			setTransferencia(new SceTransferencia());
			getTransferencia().setClassifMatNiv5(new ScoClassifMatNiv5());
			getTransferencia().setServidor(new RapServidores());
			getTransferencia().getServidor().setPessoaFisica(new RapPessoasFisicas());
		}
	
	}
	
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Limpa os filtros da pesquisa
	 */
	public void limparPesquisa() {
		setDataGeracao(null);
		transferencia = new SceTransferencia();
		transferencia.setClassifMatNiv5(new ScoClassifMatNiv5());
		transferencia.setServidor(new RapServidores());
		transferencia.getServidor().setPessoaFisica(new RapPessoasFisicas());
		setIndTransfAutomatica(null);
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	/**
	 * Utilizado pelo suggestionbox para pesquisar o almoxarifado
	 * 
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCodigoDescricao(String parametro) {
		return estoqueFacade.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}
	
	@Override
	public List<SceTransferencia> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {		
		if (this.indTransfAutomatica != null) {
			transferencia.setTransferenciaAutomatica(indTransfAutomatica.isSim());
		}
		Short almoxarifadoSeq = null;
		if(transferencia.getAlmoxarifado() != null) {
			almoxarifadoSeq = transferencia.getAlmoxarifado().getSeq();
		}
		Short almoxarifadoRecebimentoSeq = null;
		if(transferencia.getAlmoxarifadoRecebimento() != null) {
			almoxarifadoRecebimentoSeq = transferencia.getAlmoxarifadoRecebimento().getSeq();
		}
		return estoqueFacade.pesquisarTransferenciaAutomatica(firstResult,maxResult,orderProperty,asc,
				transferencia.getSeq(),
				dataGeracao,
				transferencia.getTransferenciaAutomatica(),
				almoxarifadoSeq,
				almoxarifadoRecebimentoSeq,
				transferencia.getClassifMatNiv5().getNumero(),
				transferencia.getClassifMatNiv5().getDescricao(),
				transferencia.getServidor().getPessoaFisica().getNome(),
				Boolean.FALSE);
	}
	
	@Override
	public Long recuperarCount() {
		if(DominioSimNao.S.equals(getIndTransfAutomatica())) {
			transferencia.setTransferenciaAutomatica(Boolean.TRUE);
		} else {
			transferencia.setTransferenciaAutomatica(Boolean.FALSE);
		}
		Short almoxarifadoSeq = null;
		if(transferencia.getAlmoxarifado() != null) {
			almoxarifadoSeq = transferencia.getAlmoxarifado().getSeq();
		}
		Short almoxarifadoRecebimentoSeq = null;
		if(transferencia.getAlmoxarifadoRecebimento() != null) {
			almoxarifadoRecebimentoSeq = transferencia.getAlmoxarifadoRecebimento().getSeq();
		}
		return estoqueFacade.pesquisarTransferenciaAutomaticaCount(
				transferencia.getSeq(),
				dataGeracao,
				transferencia.getTransferenciaAutomatica(),
				almoxarifadoSeq,
				almoxarifadoRecebimentoSeq,
				transferencia.getClassifMatNiv5().getNumero(),
				transferencia.getClassifMatNiv5().getDescricao(),
				transferencia.getServidor().getPessoaFisica().getNome(),
				Boolean.FALSE);
	}
	
	public void imprimirTransferencia(){
		relatorioTransferenciaMaterialController.setNumTransferenciaMaterial(this.getSeqTransferencia());
		relatorioTransferenciaMaterialController.impressaoDireta();
	}
	
	public String efetivarTransferenciaAutoAlmoxarifado(){
		return EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO;
	}
	/**
     * abrevia string
     * para apresentação na tela
     * @param str
     * @param maxWidth
     * @return
     */
    public String abreviar(String str, int maxWidth){
    	String abreviado = null;
        if(str != null) {
        	abreviado = " " + StringUtils.abbreviate(str, maxWidth);
        }
        return abreviado;
    }

	public SceTransferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(SceTransferencia transferencia) {
		this.transferencia = transferencia;
	}

	public DominioSimNao getIndTransfAutomatica() {
		return indTransfAutomatica;
	}

	public void setIndTransfAutomatica(DominioSimNao indTransfAutomatica) {
		this.indTransfAutomatica = indTransfAutomatica;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
	public Integer getSeqTransferencia() {
		return seqTransferencia;
	}

	public void setSeqTransferencia(Integer seqTransferencia) {
		this.seqTransferencia = seqTransferencia;
	}
	
	
	public String obterCodigoDescricaoClassificacaoMaterialPorNumero(Long numero) {
		StringBuilder codigoDescricao = new StringBuilder();
		if (numero != null) {
			VScoClasMaterial vScoClasMaterial = comprasFacade.obterVScoClasMaterialPorNumero(numero);
			if (vScoClasMaterial != null) {
				codigoDescricao.append(vScoClasMaterial.getId().getNumero());
				codigoDescricao.append(" - ");
				codigoDescricao.append(vScoClasMaterial.getId().getDescricao());
			}
		}
		return codigoDescricao.toString();
	}
 


	public DynamicDataModel<SceTransferencia> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceTransferencia> dataModel) {
	 this.dataModel = dataModel;
	}
}
