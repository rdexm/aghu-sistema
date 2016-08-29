package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.estoque.ejb.AtualizaTransferenciaAutoAlmoxarifadoBeanLocal;
import br.gov.mec.aghu.estoque.ejb.EfetivarRequisicaoMaterialBeanLocal;
import br.gov.mec.aghu.estoque.ejb.EstornarNotaRecebimentoBeanLocal;
import br.gov.mec.aghu.estoque.ejb.EstornarRequisicaoMaterialBeanLocal;
import br.gov.mec.aghu.estoque.ejb.GerarItemNotaRecebimentoBeanLocal;
import br.gov.mec.aghu.estoque.ejb.GerarListaTransferenciaAutoAlmoxarifadoBeanLocal;
import br.gov.mec.aghu.estoque.ejb.GerarNotaRecebimentoBeanLocal;
import br.gov.mec.aghu.estoque.ejb.GerarRequisicaoMaterialBeanLocal;
import br.gov.mec.aghu.estoque.ejb.ManterDocumentoFiscalEntradaBeanLocal;
import br.gov.mec.aghu.estoque.ejb.ManterMaterialBeanLocal;
import br.gov.mec.aghu.estoque.ejb.MovimentoMaterialBeanLocal;
import br.gov.mec.aghu.estoque.ejb.TransferenciaEventualMaterialBeanLocal;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;

@Stateless
public class EstoqueBeanFacade extends BaseFacade implements IEstoqueBeanFacade {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2678976200637266420L;

	@EJB
	private GerarRequisicaoMaterialBeanLocal gerarRequisicaoMaterialBeanLocal;
	
	@EJB
	private TransferenciaEventualMaterialBeanLocal transferenciaEventualMaterialBeanLocal;
	
	@EJB
	private EfetivarRequisicaoMaterialBeanLocal efetivarRequisicaoMaterialBeanLocal;
	
	@EJB
	private EstornarRequisicaoMaterialBeanLocal estornarRequisicaoMaterialBeanLocal;
	
	@EJB
	private ManterMaterialBeanLocal manterMaterialBeanLocal;
	
	@EJB
	private GerarNotaRecebimentoBeanLocal gerarNotaRecebimentoBeanLocal;
	
	@EJB
	private GerarItemNotaRecebimentoBeanLocal gerarItemNotaRecebimentoBeanLocal;
	
	@EJB
	private ManterDocumentoFiscalEntradaBeanLocal manterDocumentoFiscalEntradaBeanLocal;
	
	@EJB
	private EstornarNotaRecebimentoBeanLocal estornarNotaRecebimentoBeanLocal;
	
	@EJB
	private AtualizaTransferenciaAutoAlmoxarifadoBeanLocal atualizaTransferenciaAutoAlmoxarifadoBeanLocal;
	
	@EJB
	private MovimentoMaterialBeanLocal movimentoMaterialBeanLocal;
	
	@EJB
	private GerarListaTransferenciaAutoAlmoxarifadoBeanLocal gerarListaTransferenciaAutoAlmoxarifadoBeanLocal;
	

	@Override
	public void gravarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		gerarRequisicaoMaterialBeanLocal.gravarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}
	
	@Override
	public void gravarItensRequisicaoMaterial(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException{
		gerarRequisicaoMaterialBeanLocal.gravarItensRequisicaoMaterial(sceItemRms, nomeMicrocomputador);
	}
	
	@Override
	public void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		efetivarRequisicaoMaterialBeanLocal.efetivarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}
	
	@Override
	public void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException {
		gerarRequisicaoMaterialBeanLocal.excluirItemRequisicaoMaterial(sceItemRms,countItensLista, estorno);
	}

	@Override
	public void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriaisEstornar, String nomeMicrocomputador) throws BaseException {
		estornarRequisicaoMaterialBeanLocal.estornarRequisicaoMaterial(sceReqMateriaisEstornar, nomeMicrocomputador);
	}
	
	@Override
	public void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		estornarNotaRecebimentoBeanLocal.estornarNotaRecebimento(seqNotaRecebimento, nomeMicrocomputador);
	}
	
	@Override
	public void manterMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		manterMaterialBeanLocal.manterMaterial(material, nomeMicrocomputador);
		
	}
	
	@Override
	public void manterMaterialEsperanto(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		manterMaterialBeanLocal.manterMaterialSemFlush(material, nomeMicrocomputador);
		
	}

	@Override
	public void gravarSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador)throws BaseException {
		gerarRequisicaoMaterialBeanLocal.gravarSceEstoqueAlmoxarifado(estoqueAlmox, nomeMicrocomputador);
	}

	@Override
	public void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		gerarNotaRecebimentoBeanLocal.gerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);	
	}
	
	@Override
	public void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		gerarNotaRecebimentoBeanLocal.gerarNotaRecebimentoSolicitacaoCompraAutomatica(notaRecebimento, nomeMicrocomputador);	
	}
	
	@Override
	public void atualizarImpressaoNotaRecebimento(final Integer seq, String nomeMicrocomputador) throws BaseException {
		gerarNotaRecebimentoBeanLocal.atualizarImpressaoNotaRecebimento(seq, nomeMicrocomputador);
	}
	
	@Override
	public void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		gerarItemNotaRecebimentoBeanLocal.gerarItemNotaRecebimento(itemNotaRecebimento, nomeMicrocomputador);
	}
	
	@Override
	public void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento,  Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException {
		gerarItemNotaRecebimentoBeanLocal.gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(itemNotaRecebimento, numeroItem, marcaComercial, fatorConversao, nomeMicrocomputador);
	}
	
	@Override
	public void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada)throws BaseException {
		manterDocumentoFiscalEntradaBeanLocal.persistirDocumentoFiscalEntrada(documentoFiscalEntrada);
	}
	
	@Override
	public void removerDocumentoFiscalEntrada(Integer seq) throws BaseException {
		manterDocumentoFiscalEntradaBeanLocal.removerDocumentoFiscalEntrada(seq);
	}
	
	@Override
	public void gravarMovimentoMaterial(SceMovimentoMaterial movimento, String nomeMicrocomputador) throws BaseException{
		movimentoMaterialBeanLocal.gravarMovimentoMaterial(movimento, nomeMicrocomputador);
	}
	
	@Override
	public void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO>  listaItemTransferenciaAutomaticaVO, String nomeMicrocomputador) throws BaseException {
		atualizaTransferenciaAutoAlmoxarifadoBeanLocal.atualizarItensTransfAutoAlmoxarifados(listaItemTransferenciaAutomaticaVO, nomeMicrocomputador);
	}
	
	@Override
	public void efetivarTransferenciaAutoAlmoxarifados(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException {
		atualizaTransferenciaAutoAlmoxarifadoBeanLocal.efetivarTransferenciaAutoAlmoxarifado(transferencia, nomeMicrocomputador);
	}
	
	@Override
	public void removerTransferenciaAutoAlmoxarifado(Integer seqTransferencia) throws BaseException {
		gerarListaTransferenciaAutoAlmoxarifadoBeanLocal.removerTransferenciaAutoAlmoxarifado(seqTransferencia);
	}
	
	@Override
	public void removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5) throws BaseException{
		gerarListaTransferenciaAutoAlmoxarifadoBeanLocal.removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
	}


	@Override
	public void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		gerarListaTransferenciaAutoAlmoxarifadoBeanLocal.persistirTransferenciaAutoAlmoxarifado(transferencia);
	}

	@Override
	public void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		gerarListaTransferenciaAutoAlmoxarifadoBeanLocal.gerarListaTransferenciaAutoAlmoxarifado(transferencia);
	}
	
	@Override
	public void removerItemTransferenciaAutoAlmoxarifado(final Integer ealSeq, final Integer trfSeq) throws BaseException {
		gerarListaTransferenciaAutoAlmoxarifadoBeanLocal.removerItemTransferenciaAutoAlmoxarifado(ealSeq, trfSeq);
	}
	
	@Override
	public void estornarTransferenciaAutoAlmoxarifados(Integer seqTransferencia, String nomeMicrocomputador) throws BaseException {
		atualizaTransferenciaAutoAlmoxarifadoBeanLocal.estornarTransferenciaAutoAlmoxarifado(seqTransferencia, nomeMicrocomputador);
	}
	
	@Override
	public void atualizarTransferenciaAutoAlmoxarifados(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException {
		atualizaTransferenciaAutoAlmoxarifadoBeanLocal.atualizarTransferenciaAutoAlmoxarifado(transferencia, nomeMicrocomputador);
	}
	
	@Override
	public void gravarTransferenciaEventualMaterial(SceTransferencia transferencia) throws BaseException {
		transferenciaEventualMaterialBeanLocal.gravarTransferenciaEventualMaterial(transferencia);
	}
	

}
