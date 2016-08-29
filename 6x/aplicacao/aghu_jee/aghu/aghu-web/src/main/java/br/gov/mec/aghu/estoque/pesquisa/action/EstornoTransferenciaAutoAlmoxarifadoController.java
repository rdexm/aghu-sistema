package br.gov.mec.aghu.estoque.pesquisa.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.TransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EstornoTransferenciaAutoAlmoxarifadoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(EstornoTransferenciaAutoAlmoxarifadoController.class);

	private static final long serialVersionUID = 4615586305675520815L;

	private static final String PESQUISAR_ESTORNO_TRANSFERENCIA_AUTO_ALMOXARIFADO = "estoque-pesquisarEstornoTransferenciaAutoAlmoxarifado";
	

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	/**
	 * Número da transferência recebido por parâmetro
	 */
	private Integer numero;
	
	/**
	 * Transferencia a ser estornada
	 */
	private SceTransferencia transferencia;
	
	private TransferenciaAutomaticaVO vo;
	private List<SceItemTransferencia> itensTransferencia;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	/**
	 * Método que inicia a controller
	 */
	public void iniciar() {
	 

		
		if (numero != null) {
			transferencia = estoqueFacade.obterTransferenciaPorId(numero);
			
			if (transferencia != null) {
				
				vo = new TransferenciaAutomaticaVO();
				vo.setAlmoxarifadoOrigem(transferencia.getAlmoxarifado());
				vo.setAlmoxarifadoDestino(transferencia.getAlmoxarifadoRecebimento());
				vo.setClassificacaoMaterial(transferencia.getClassifMatNiv5());
				
				// Popula a descricao da classificação do material conforme a view
				if (transferencia.getClassifMatNiv5() != null) {
					final VScoClasMaterial clasMaterial = this.comprasFacade.obterVScoClasMaterialPorNumero(transferencia.getClassifMatNiv5().getNumero());
					vo.setDescricaoClassificacaoMaterial(clasMaterial != null ? clasMaterial.getId().getDescricao() : null);
				}
				
				vo.setDtGeracao(transferencia.getDtGeracao());
				vo.setServidor(transferencia.getServidor());
				vo.setDtEfetivacao(transferencia.getDtEfetivacao());
				vo.setServidorEfetivado(transferencia.getServidorEfetivado());
				itensTransferencia = estoqueFacade.pesquisarListaItensTransferenciaPorTransferencia(numero);
			}
		}
	
	}
	
	/**
	 * Estorna a transferência automática
	 */
	public void estornar() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
			estoqueBeanFacade.estornarTransferenciaAutoAlmoxarifados(transferencia.getSeq(), nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNO_TRANSF_AUTO");
				
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		return PESQUISAR_ESTORNO_TRANSFERENCIA_AUTO_ALMOXARIFADO;
	}
	
	
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public SceTransferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(SceTransferencia transferencia) {
		this.transferencia = transferencia;
	}

	public List<SceItemTransferencia> getItensTransferencia() {
		return itensTransferencia;
	}

	public void setItensTransferencia(
			List<SceItemTransferencia> itensTransferencia) {
		this.itensTransferencia = itensTransferencia;
	}

	public TransferenciaAutomaticaVO getVo() {
		return vo;
	}

	public void setVo(TransferenciaAutomaticaVO vo) {
		this.vo = vo;
	}
}