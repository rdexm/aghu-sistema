package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDasDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocumentoDAO;
import br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceDocumentoValidadeID;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceItemDasId;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class DevolucaoAlmoxarifadoON extends BaseBusiness {
	
	@Inject
	private SceItemDasDAO sceItemDasDAO;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDao;
	
	@EJB
	private SceDocumentoValidadeRN sceDocumentoValidadeRN;
	@EJB
	private ItemDevolucaoAlmoxarifadoRN itemDevolucaoAlmoxarifadoRN;
	@EJB
	private DevolucaoAlmoxarifadoRN devolucaoAlmoxarifadoRN;
	@EJB
	private SceLoteDocumentoRN sceLoteDocumentoRN;
	
	@Inject
	private SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO;

	
	private static final Log LOG = LogFactory.getLog(DevolucaoAlmoxarifadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceLoteDocumentoDAO sceLoteDocumentoDAO;
	
	@Inject
	private SceDocumentoValidadeDAO sceDocumentoValidadeDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1685898681093216844L;
	
	public enum DevolucaoAlmoxarifadoONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_DEVOLUCAO_ALMOXARIFADO, ERRO_DEVOLUCAO_ALMOXARIFADO_QTDE_LOTE_MAIOR_QUE_QTDE_VALIDADE, 
		SCE_00619, SCE_00697, ERRO_DEVOLUCAO_ALMOXARIFADO_VALIDADE_ITEM_DUPLICADA, 
		ERRO_DEVOLUCAO_ALMOXARIFADO_SEM_ITENS, ERRO_DEVOLUCAO_ALMOXARIFADO_ITEM_DUPLICADO;
	}

	public void estornarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoAnterior = this.clonarDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
		devolucaoAlmoxarifado.setEstorno(true);
		devolucaoAlmoxarifado.setDtEstorno(new Date());
		devolucaoAlmoxarifado.setServidor(servidorLogado);
		
		try {
			this.getDevolucaoAlmoxarifadoRN().atualizarDevolucaoAlmoxarifado(devolucaoAlmoxarifadoAnterior, devolucaoAlmoxarifado, nomeMicrocomputador);	
		} catch(BaseException exception) {
			//Neste trecho está sendo forçaada a excecao com rollback devido ao uso de flush na estoria #12310 da equipe Samurais
			ApplicationBusinessException rollback = new ApplicationBusinessException(exception);
			throw rollback;
		}
	}
	
	public SceDevolucaoAlmoxarifado clonarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException {
		SceDevolucaoAlmoxarifado cloneDevolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
		
		try {
			// clona o objeto (faz um shalow clone), trazendo os objetos do 1º nível
			cloneDevolucaoAlmoxarifado = (SceDevolucaoAlmoxarifado) BeanUtils.cloneBean(devolucaoAlmoxarifado);
			//BeanUtils.copyProperties(cloneAtendimento, atendimento);
			
			// Atribui as demais propriedades ao objeto clonado
			if (devolucaoAlmoxarifado.getAlmoxarifado() != null) {
				SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
				almoxarifado.setSeq(devolucaoAlmoxarifado.getAlmoxarifado().getSeq());
				cloneDevolucaoAlmoxarifado.setAlmoxarifado(almoxarifado);
			}

			if (devolucaoAlmoxarifado.getCentroCusto() != null) {
				FccCentroCustos centroCusto = new FccCentroCustos();
				centroCusto.setCodigo(devolucaoAlmoxarifado.getCentroCusto().getCodigo());
				cloneDevolucaoAlmoxarifado.setCentroCusto(centroCusto);
			}
			
			if (devolucaoAlmoxarifado.getSceItemDas() != null) {
				cloneDevolucaoAlmoxarifado.setSceItemDas(devolucaoAlmoxarifado.getSceItemDas());
			}
			
			if (devolucaoAlmoxarifado.getServidor() != null) {
				RapServidores servidor = new RapServidores();
				servidor.setId(devolucaoAlmoxarifado.getServidor().getId());
				cloneDevolucaoAlmoxarifado.setServidor(servidor);
			}
			
			if (devolucaoAlmoxarifado.getServidorEstornado() != null) {
				RapServidores servidorEstornado = new RapServidores();
				servidorEstornado.setId(devolucaoAlmoxarifado.getServidorEstornado().getId());
				cloneDevolucaoAlmoxarifado.setServidorEstornado(servidorEstornado);
			}
			
			if (devolucaoAlmoxarifado.getTipoMovimento() != null) {
				SceTipoMovimento tipoMovimento = new SceTipoMovimento();
				tipoMovimento.setId(devolucaoAlmoxarifado.getTipoMovimento().getId());
				cloneDevolucaoAlmoxarifado.setTipoMovimento(tipoMovimento);
			}
			
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoONExceptionCode.ERRO_CLONE_DEVOLUCAO_ALMOXARIFADO);
		}
		return cloneDevolucaoAlmoxarifado;
	}
	
	public List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialPorDevolucaoAlmoxarifado(Integer numeroDaSelecionado){
		List<SceItemDas> listaItens = this.getSceItemDasDAO().pesquisarItensComMaterialPorDevolucaoAlmoxarifado(numeroDaSelecionado);
		List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifado = new ArrayList<ItemDevolucaoAlmoxarifadoVO>();
		for(SceItemDas item: listaItens){
			ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO = new ItemDevolucaoAlmoxarifadoVO();
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
			if(estoqueAlmoxarifado!=null && estoqueAlmoxarifado.getMaterial()!=null){
				itemDevolucaoAlmoxarifadoVO.setCodigoMaterial(estoqueAlmoxarifado.getMaterial().getCodigo());
				itemDevolucaoAlmoxarifadoVO.setNomeMaterial(estoqueAlmoxarifado.getMaterial().getNome());
			}
			if(estoqueAlmoxarifado!=null && estoqueAlmoxarifado.getFornecedor()!=null){
				itemDevolucaoAlmoxarifadoVO.setNroFornecedor(estoqueAlmoxarifado.getFornecedor().getNumero());
				itemDevolucaoAlmoxarifadoVO.setNomeFornecedor(estoqueAlmoxarifado.getFornecedor().getNomeFantasia());
			}
			itemDevolucaoAlmoxarifadoVO.setQuantidade(item.getQuantidade());
			ScoUnidadeMedida unidadeMedida = item.getUnidadeMedida();
			if(unidadeMedida!=null){
				itemDevolucaoAlmoxarifadoVO.setUnidadeMedida(unidadeMedida.getCodigo());		
			}
			itemDevolucaoAlmoxarifadoVO.setDalSeq(item.getId().getDalSeq());
			itemDevolucaoAlmoxarifadoVO.setEalSeq(item.getId().getEalSeq());
			List<SceDocumentoValidade> listaDocumentoValidade = this.getSceDocumentoValidadeDAO().pesquisarDocumentoValidadePorItemDa(item.getId());
			itemDevolucaoAlmoxarifadoVO.setListaValidades(listaDocumentoValidade);
			listaItemDevolucaoAlmoxarifado.add(itemDevolucaoAlmoxarifadoVO);
		}
		return listaItemDevolucaoAlmoxarifado;
	}
	
	public List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialLoteDocumentosPorDalSeq(Integer dalSeq) {
		List<SceItemDas> listaItens = this.getSceItemDasDAO().
				pesquisarItensComMaterialPorDevolucaoAlmoxarifado(dalSeq);
		List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifado = new ArrayList<ItemDevolucaoAlmoxarifadoVO>();
		for(SceItemDas item: listaItens){
			ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO = new ItemDevolucaoAlmoxarifadoVO();
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
			if(estoqueAlmoxarifado!=null && estoqueAlmoxarifado.getMaterial()!=null){
				itemDevolucaoAlmoxarifadoVO.setCodigoMaterial(estoqueAlmoxarifado.getMaterial().getCodigo());
				itemDevolucaoAlmoxarifadoVO.setNomeMaterial(estoqueAlmoxarifado.getMaterial().getNome());
			}
			if(estoqueAlmoxarifado!=null && estoqueAlmoxarifado.getFornecedor()!=null){
				itemDevolucaoAlmoxarifadoVO.setNroFornecedor(estoqueAlmoxarifado.getFornecedor().getNumero());
				itemDevolucaoAlmoxarifadoVO.setNomeFornecedor(estoqueAlmoxarifado.getFornecedor().getNomeFantasia());
			}
			itemDevolucaoAlmoxarifadoVO.setQuantidade(item.getQuantidade());
			ScoUnidadeMedida unidadeMedida = item.getUnidadeMedida();
			if(unidadeMedida!=null){
				itemDevolucaoAlmoxarifadoVO.setUnidadeMedida(unidadeMedida.getCodigo());		
			}
			itemDevolucaoAlmoxarifadoVO.setDalSeq(item.getId().getDalSeq());
			itemDevolucaoAlmoxarifadoVO.setEalSeq(item.getId().getEalSeq());
			itemDevolucaoAlmoxarifadoVO.setId(item.getId());
			itemDevolucaoAlmoxarifadoVO.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
			
			List<SceDocumentoValidade> listaDocumentoValidade = this.getSceDocumentoValidadeDAO()
					.pesquisarDocumentoValidadePorItemDa(item.getId());
			itemDevolucaoAlmoxarifadoVO.setListaValidades(listaDocumentoValidade);
			
			List<SceLoteDocumento> listaLoteDocumento = this.getSceLoteDocumentoDAO()
					.pesquisarLoteDocumentoPorDalSeqEEalSeq(dalSeq, item.getId().getEalSeq());
			itemDevolucaoAlmoxarifadoVO.setListaLoteDocumento(listaLoteDocumento);
			
			listaItemDevolucaoAlmoxarifado.add(itemDevolucaoAlmoxarifadoVO);
		}		
		return listaItemDevolucaoAlmoxarifado;
	}

	/**
	 * Método negocial para gravar uma DA (devolução ao almoxarifado). Após gravar
	 * a DA são chamadas as RNs de inserção para os itens da DA e para cada
	 * validade e documento que estiver associado ao item.
	 * 
	 * @param listaItensDa
	 * @param listaDocumentoValidadeItemDa
	 * @param listaLoteDocumentoItemDa
	 */
	public void persistirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoNew, 
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO, String nomeMicrocomputador) throws BaseException {
		
		if (listaItemDevolucaoAlmoxarifadoVO.isEmpty()) {
			throw new ApplicationBusinessException(
					DevolucaoAlmoxarifadoONExceptionCode.ERRO_DEVOLUCAO_ALMOXARIFADO_SEM_ITENS);
		}
		
		/*	Na controller é setado -1 como id para permitir a seleção 
			do item por radio button quando é uma nova DA	*/
		if (devolucaoAlmoxarifadoNew.getSeq() == -1) {
			devolucaoAlmoxarifadoNew.setSeq(null);
			getDevolucaoAlmoxarifadoRN().persistirDevolucaoAlmoxarifado(devolucaoAlmoxarifadoNew);
		}
		
		persistirItensDevolucaoAlmoxarifado(listaItemDevolucaoAlmoxarifadoVO, devolucaoAlmoxarifadoNew, nomeMicrocomputador);
	}
	
	private void persistirItensDevolucaoAlmoxarifado(List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO,
			SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException {
		
		for (ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO : listaItemDevolucaoAlmoxarifadoVO) {
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = itemDevolucaoAlmoxarifadoVO.getEstoqueAlmoxarifado();
			SceTipoMovimento tipoMovimento = devolucaoAlmoxarifado.getTipoMovimento();
			SceItemDas itemDa = null;
			
			try {
				if (itemDevolucaoAlmoxarifadoVO.getId() == null) {
					SceItemDasId itemDaId = new SceItemDasId();
					itemDaId.setDalSeq(devolucaoAlmoxarifado.getSeq());
					itemDaId.setEalSeq(estoqueAlmoxarifado.getSeq());
					itemDa = new SceItemDas();
					itemDa.setId(itemDaId);
					itemDa.setQuantidade(itemDevolucaoAlmoxarifadoVO.getQuantidade());
					itemDa.setDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
					estoqueAlmoxarifado = getSceEstoqueAlmoxarifadoDao().obterPorChavePrimaria(estoqueAlmoxarifado.getSeq());
					itemDa.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
					itemDa.setUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
					getItemDevolucaoAlmoxarifadoRN().persistirItemDevolucaoAlmoxarifado(itemDa, nomeMicrocomputador);
				}
			} catch(ApplicationBusinessException exception) {
				//Neste trecho está sendo forçada a excecao com rollback devido ao uso de flush na estoria #12310
				ApplicationBusinessException rollback = new ApplicationBusinessException(exception);
				throw rollback;
			}
			
			for (SceDocumentoValidade docValidade : itemDevolucaoAlmoxarifadoVO.getListaValidades()) {
				/*	Ao cadastrar uma validade na tela é setada a data. Como data é 
					um atributo do id é necessária a validação abaixo para determinar
					se a validade já foi persistida.	*/
				if (!verificarValidadeItemDaPersistido(docValidade)) {
					SceDocumentoValidadeID docValidadeId = docValidade.getId();
					docValidadeId.setEalSeq(estoqueAlmoxarifado.getSeq());
					docValidadeId.setNroDocumento(devolucaoAlmoxarifado.getSeq());
					docValidadeId.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
					docValidadeId.setTmvSeq(tipoMovimento.getId().getSeq().intValue());
					docValidade.setId(docValidadeId);
					docValidade.setTipoMovimento(tipoMovimento);
					getSceDocumentoValidadeRN().inserir(docValidade);
				}
			}
			
			for (SceLoteDocumento loteDocumento : itemDevolucaoAlmoxarifadoVO.getListaLoteDocumento()) {
				if (loteDocumento.getSeq() == null) {
					loteDocumento.setIdaDalSeq(devolucaoAlmoxarifado.getSeq());
					loteDocumento.setIdaEalSeq(estoqueAlmoxarifado.getSeq());
					loteDocumento.setTipoMovimento(tipoMovimento);
					getSceLoteDocumentoRN().inserir(loteDocumento);
				}
			}
		}
	}
	
	public Boolean verificarValidadeItemDaPersistido(SceDocumentoValidade docValidade) {
		SceDocumentoValidadeID docValidadeId = docValidade.getId();
		if (docValidadeId != null && docValidadeId.getTmvSeq() == null 
				&& docValidadeId.getTmvComplemento() == null
				&& docValidadeId.getEalSeq() == null && docValidadeId.getNroDocumento() == null
				&& docValidadeId.getData() != null) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	

	/**
	 * Verifica se a soma das quantidades de documento validade é 
	 * igual a quantidade do item de DA, caso contrário é lançada
	 * uma exceção de negócio.
	 * 
	 * @param listaDocumentoValidade
	 * @param itemDa
	 * @throws ApplicationBusinessException
	 */
	public void verificarQuantidadeDocumentoValidade(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO, 
			SceDocumentoValidade novaValidade) throws ApplicationBusinessException {
		
		if (itemDevolucaoAlmoxarifadoVO.getEstoqueAlmoxarifado().getIndControleValidade()) {
			Integer totalQuantidadeDocValidade = 0;
			
			for (SceDocumentoValidade documentoValidade : itemDevolucaoAlmoxarifadoVO.getListaValidades()) {
				totalQuantidadeDocValidade += documentoValidade.getQuantidade();
			}
			
			totalQuantidadeDocValidade += novaValidade.getQuantidade();
			
			if (totalQuantidadeDocValidade < itemDevolucaoAlmoxarifadoVO.getQuantidade() 
					|| totalQuantidadeDocValidade > itemDevolucaoAlmoxarifadoVO.getQuantidade()) {
				throw new ApplicationBusinessException(DevolucaoAlmoxarifadoONExceptionCode.SCE_00619);
			}
		}
	}
	
	public void verificarQuantidadeValidadesContraItemDa(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO,
			SceDocumentoValidade novaValidade) throws ApplicationBusinessException {
		Integer totalQuantidadeDocValidade = 0;
		
		for (SceDocumentoValidade documentoValidade : itemDevolucaoAlmoxarifadoVO.getListaValidades()) {
			totalQuantidadeDocValidade += documentoValidade.getQuantidade();
		}
		
		totalQuantidadeDocValidade += novaValidade.getQuantidade();
		
		if (totalQuantidadeDocValidade > itemDevolucaoAlmoxarifadoVO.getQuantidade()) {
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoONExceptionCode.SCE_00697);
		}
	}
	
	public void verificarQuantidadeLoteContraQuantidadeValidade(SceLoteDocumento lote, SceDocumentoValidade validade)
			throws ApplicationBusinessException {
		if (lote.getQuantidade() > validade.getQuantidade()) {
			throw new ApplicationBusinessException(
					DevolucaoAlmoxarifadoONExceptionCode.ERRO_DEVOLUCAO_ALMOXARIFADO_QTDE_LOTE_MAIOR_QUE_QTDE_VALIDADE);
		}
	}
	
	public void verificarValidadeDataDuplicada(List<SceDocumentoValidade> listaDocumentoValidade,
			SceDocumentoValidade novaValidade) throws ApplicationBusinessException {
		for (SceDocumentoValidade validade : listaDocumentoValidade) {
			if (validade.getId().getData().equals(novaValidade.getId().getData())) {
				throw new ApplicationBusinessException(
						DevolucaoAlmoxarifadoONExceptionCode.ERRO_DEVOLUCAO_ALMOXARIFADO_VALIDADE_ITEM_DUPLICADA);
			}
		}
	}
	
	public void verificarItemDaDuplicado(ItemDevolucaoAlmoxarifadoVO novoItemDevolucaoAlmoxarifadoVO, 
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO) throws ApplicationBusinessException {
		for (ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO : listaItemDevolucaoAlmoxarifadoVO) {
			if (itemDevolucaoAlmoxarifadoVO.getDalSeq().equals(novoItemDevolucaoAlmoxarifadoVO.getDalSeq()) 
					&& itemDevolucaoAlmoxarifadoVO.getEalSeq().equals(novoItemDevolucaoAlmoxarifadoVO.getEalSeq())) {
				throw new ApplicationBusinessException(
						DevolucaoAlmoxarifadoONExceptionCode.ERRO_DEVOLUCAO_ALMOXARIFADO_ITEM_DUPLICADO);
			}
		}
	}
	
	private SceDocumentoValidadeDAO getSceDocumentoValidadeDAO() {
		return sceDocumentoValidadeDAO;
	}
	
	protected SceLoteDocumentoDAO getSceLoteDocumentoDAO() {
		return sceLoteDocumentoDAO;
	}
	
	private SceItemDasDAO getSceItemDasDAO() {
		return sceItemDasDAO;
	}

	private DevolucaoAlmoxarifadoRN getDevolucaoAlmoxarifadoRN() {
		return devolucaoAlmoxarifadoRN;
	}
	
	private ItemDevolucaoAlmoxarifadoRN getItemDevolucaoAlmoxarifadoRN() {
		return itemDevolucaoAlmoxarifadoRN;
	}
	
	private SceDocumentoValidadeRN getSceDocumentoValidadeRN() {
		return sceDocumentoValidadeRN;
	}
	
	private SceLoteDocumentoRN getSceLoteDocumentoRN() {
		return sceLoteDocumentoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDao() {
		return sceEstoqueAlmoxarifadoDao;
	}

	public void setSceEstoqueAlmoxarifadoDao(SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDao) {
		this.sceEstoqueAlmoxarifadoDao = sceEstoqueAlmoxarifadoDao;
	}

	public void setSceLoteDocumentoDAO(SceLoteDocumentoDAO sceLoteDocumentoDAO) {
		this.sceLoteDocumentoDAO = sceLoteDocumentoDAO;
	}

	public void setSceItemDasDAO(SceItemDasDAO sceItemDasDAO) {
		this.sceItemDasDAO = sceItemDasDAO;
	}

	public SceDevolucaoAlmoxarifadoDAO getSceDevolucaoAlmoxarifadoDAO() {
		return sceDevolucaoAlmoxarifadoDAO;
	}

	public void setSceDevolucaoAlmoxarifadoDAO(SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO) {
		this.sceDevolucaoAlmoxarifadoDAO = sceDevolucaoAlmoxarifadoDAO;
	}
	
}
