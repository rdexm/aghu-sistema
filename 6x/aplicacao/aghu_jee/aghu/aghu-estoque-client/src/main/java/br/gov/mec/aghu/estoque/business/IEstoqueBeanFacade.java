package br.gov.mec.aghu.estoque.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

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
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IEstoqueBeanFacade extends Serializable {

	void gravarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;
	
	void gravarItensRequisicaoMaterial(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException;
	
	void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;
	
	void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException;

	void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriaisEstornar, String nomeMicrocomputador) throws BaseException;
	
	void manterMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException;
	
	void manterMaterialEsperanto(ScoMaterial material, String nomeMicrocomputador) throws BaseException;

	void gravarSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador)throws BaseException;

	void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada)throws BaseException;
	
	void removerDocumentoFiscalEntrada(Integer seq) throws BaseException;

	void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador)throws BaseException;
	
	void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador)throws BaseException;
	
	void atualizarImpressaoNotaRecebimento(final Integer seq, String nomeMicrocomputador) throws BaseException;
	
	void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException;
	
	void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException;
	
	void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException;
	
	void gravarMovimentoMaterial(SceMovimentoMaterial movimento, String nomeMicrocomputador) throws BaseException;
	
	void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO>  listaItemTransferenciaAutomaticaVO, String nomeMicrocomputador) throws BaseException;
	
	void efetivarTransferenciaAutoAlmoxarifados(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException;
	
	void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;
	
	void removerTransferenciaAutoAlmoxarifado(Integer seqTransferencia) throws BaseException;
	
	void removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5) throws BaseException;
	
	void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;
	
	void removerItemTransferenciaAutoAlmoxarifado(final Integer ealSeq, final Integer trfSeq) throws BaseException;
	
	void estornarTransferenciaAutoAlmoxarifados(Integer seqTransferencia, String nomeMicrocomputador) throws BaseException;

	void atualizarTransferenciaAutoAlmoxarifados(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException;

	void gravarTransferenciaEventualMaterial(SceTransferencia transferencia) throws BaseException;
	
	
}
