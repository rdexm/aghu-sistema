package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceItemTransferenciaRN extends BaseBusiness{

@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;

private static final Log LOG = LogFactory.getLog(SceItemTransferenciaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemTransferenciaDAO sceItemTransferenciaDAO;

@Inject
private SceTransferenciaDAO sceTransferenciaDAO;

@EJB
private IComprasFacade comprasFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4105000401404390000L;

	public enum SceItemTransferenciaRNCode implements BusinessExceptionCode {
		SCE_00490,SCE_00469,SCE_00018,SCE_00292,SCE_00124,MSG_ESTOQUE_NAO_ENCONTRADO_OU_SEM_SALDO, MENSAGEM_ITEM_TRANS_DUPLICADO;
	}


	/*
	 * Métodos para Inserir SceItemTransferencia
	 */

	/**
	 * ORADB TRIGGER SCET_ITR_BRI (INSERT)
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	private void preInserir(SceItemTransferencia itemTransferencia) throws BaseException{

		this.validaItemDuplicado(itemTransferencia);
		this.validarAlmoxarifadosPai(itemTransferencia); // RN1
		this.validarIndicadorBloqueioEntrada(itemTransferencia); // RN2
		this.validarConsignado(itemTransferencia.getEstoqueAlmoxarifadoOrigem(), itemTransferencia.getEstoqueAlmoxarifado()); // RN3


	}

	/**
	 * Inserir SceItemTransferencia
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void inserir(SceItemTransferencia itemTransferencia) throws BaseException{
		this.preInserir(itemTransferencia);
		this.getSceItemTransferenciaDAO().persistir(itemTransferencia);
		this.getSceItemTransferenciaDAO().flush();
	}

	/**
	 * ORADB PROCEDURE BUSCA_ESTQ_ALMOX_ORIG
	 * @param almSeq
	 * @param matCodigo
	 * @param frnNumero
	 * @return
	 *  
	 */
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoOrigem(Short almSeq, Integer matCodigo, Integer frnNumero) throws ApplicationBusinessException {
		
		AghParametros parametroFornecedor = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		if (parametroFornecedor != null) {
			
			frnNumero = parametroFornecedor.getVlrNumerico().intValue();
			
		}
		
		return getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(almSeq, matCodigo, frnNumero);
		
	}
	
	
	/**
	 * Inserir SceItemTransferencia
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void inserirItemTransferenciaEventual(List<ItemPacoteMateriaisVO>  listItemPacoteMateriaisVO, SceTransferencia transferencia) throws BaseException{

		SceItemTransferencia itemTransferencia = null;
		SceEstoqueAlmoxarifado estoqueAlmxOrig = null;
		BigDecimal codigoFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico();//1
		
		for (ItemPacoteMateriaisVO vo : listItemPacoteMateriaisVO) {
			
			estoqueAlmxOrig = obterEstoqueAlmoxarifadoOrigem(transferencia.getAlmoxarifado().getSeq(), vo.getCodigoMaterial(), codigoFornecedor.intValue());
			
			itemTransferencia = new SceItemTransferencia(); 

			itemTransferencia.setEstoqueAlmoxarifado(getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoAtivoPorId(vo.getSeqEstoque()));
			itemTransferencia.setEstoqueAlmoxarifadoOrigem(estoqueAlmxOrig);
			itemTransferencia.setUnidadeMedida(getComprasFacade().obterScoUnidadeMedidaOriginal(vo.getCodigoUnidadeMedida()));
			itemTransferencia.setQuantidade(vo.getQuantidade());
			itemTransferencia.setTransferencia(transferencia);
			
			inserir(itemTransferencia);
		
		}
		
		this.getSceItemTransferenciaDAO().flush();

	}


	/*
	 * Métodos para Remover SceItemTransferencia
	 */

	/**
	 * ORADB TRIGGER SCET_ITR_BRD (DELETE)
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	private void preRemover(SceItemTransferencia itemTransferencia) throws BaseException{	
		this.validarExclusao(itemTransferencia.getTransferencia().getSeq());
	}

	/**
	 * Remover SceItemTransferencia
	 */
	public void remover(SceItemTransferencia itemTransferencia) throws BaseException{
		itemTransferencia = getSceItemTransferenciaDAO().obterPorChavePrimaria(itemTransferencia.getId());
		
		this.preRemover(itemTransferencia);
		this.getSceItemTransferenciaDAO().remover(itemTransferencia);
		this.getSceItemTransferenciaDAO().flush();
	}

	/*
	 * Métodos para Atualizar SceItemTransferencia
	 */

	/**
	 * ORADB TRIGGER "AGH".SCET_ITR_BRU
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void preAtualizar(SceItemTransferencia itemTransferencia, String nomeMicrocomputador) throws BaseException{

		final SceItemTransferencia oldItemTransferencia = this.getSceItemTransferenciaDAO().obterOriginal(itemTransferencia);

		// VERIFICA SITUAÇÕES DE ALTERAÇÃO
		this.validarAlteracao(itemTransferencia);

		// INFORMAÇÕES RELATIVAS TESTES DE ALMOXARIFADOS
		this.validarAlmoxarifadoPai(itemTransferencia);

		// INFORMAÇÕES REFERENTES A ESTOQUE ALMOX
		if (itemTransferencia !=null && !itemTransferencia.getUnidadeMedida().equals(oldItemTransferencia.getUnidadeMedida())
				|| !itemTransferencia.getEstoqueAlmoxarifado().equals(oldItemTransferencia.getEstoqueAlmoxarifado())){

			if (oldItemTransferencia.getQtdeEnviada() != null && !itemTransferencia.getQtdeEnviada().equals(oldItemTransferencia.getQtdeEnviada()) ||
					(oldItemTransferencia.getQtdeEnviada()==null && itemTransferencia.getQtdeEnviada()!=null)){
				this.validarAlmoxarifados(itemTransferencia);
			}
		}

		if (!itemTransferencia.getMatBloqueado().equals(oldItemTransferencia.getMatBloqueado()) && oldItemTransferencia.getMatBloqueado()) {
			/* Desbloqueia material no almox destino */
			this.atualizaMaterialAlmoxDestino(itemTransferencia.getTransferencia(), itemTransferencia.getEstoqueAlmoxarifado(), itemTransferencia.getQtdeEnviada(), nomeMicrocomputador);
		}

	}	

	/**
	 * Atualiza lista de Itens de Transferência automática através de um VO 
	 * @param listaItemTransferenciaAutomaticaVO
	 * @throws BaseException
	 */
	public void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO> listaItemTransferenciaAutomaticaVO, String nomeMicrocomputador) throws BaseException{

		for (ItemTransferenciaAutomaticaVO vo : listaItemTransferenciaAutomaticaVO) {

			final SceItemTransferencia itemTransferenciaOrigem = this.getSceItemTransferenciaDAO().obterItemTransferenciaPorChave(vo.getId().getEalSeq(), vo.getId().getTrfSeq());
			itemTransferenciaOrigem.setQtdeEnviada(vo.getQtdeEnviada());
			this.atualizar(itemTransferenciaOrigem, nomeMicrocomputador);
		}

	}


	/**
	 * Atualizar SceItemTrs
	 */
	public void atualizar(SceItemTransferencia itemTransferencia, String nomeMicrocomputador) throws BaseException{

		this.preAtualizar(itemTransferencia, nomeMicrocomputador);
		this.getSceItemTransferenciaDAO().merge(itemTransferencia);
		this.getSceItemTransferenciaDAO().flush();
	}

	/*
	 * RNs Inserir
	 */

	/**
	 * ORADB PROCEDURE SCEK_ITR_RN.RN_ITRP_VER_ALM_PAI
	 */
	public void validarAlmoxarifadosPai(SceItemTransferencia itemTransferencia) throws ApplicationBusinessException{

		SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem = this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(itemTransferencia.getTransferencia().getAlmoxarifado().getSeq().intValue());
		SceEstoqueAlmoxarifado estoqueAlmoxarifadoDestino = this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(itemTransferencia.getTransferencia().getAlmoxarifadoRecebimento().getSeq().intValue());

		if (estoqueAlmoxarifadoOrigem != null && estoqueAlmoxarifadoDestino != null && !estoqueAlmoxarifadoOrigem.getMaterial().getCodigo().equals(estoqueAlmoxarifadoDestino.getMaterial().getCodigo())
				&& !estoqueAlmoxarifadoOrigem.getFornecedor().getNumero().equals(estoqueAlmoxarifadoDestino.getFornecedor().getNumero())) {
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00469);
		}

	}	


	/**
	 * ORADB PROCEDURE SCEK_ITR_RN.RN_ITRP_ATU_IND_BLOQ
	 * Verifica se o almoxarifado, fornecedor e material são equivalentes em almoxarifado de origem e destino
	 * @param itemTransferencia
	 */
	public void validarIndicadorBloqueioEntrada(SceItemTransferencia itemTransferencia) throws ApplicationBusinessException{

		final Integer seqTransferencia = itemTransferencia.getTransferencia().getSeq();
		final Short seqAlmoxarifado = itemTransferencia.getTransferencia().getAlmoxarifado().getSeq();

		SceTransferencia transferencia = this.getSceTransferenciaDAO().obterTransferenciaPorSeqAlmoxarifado(seqTransferencia, seqAlmoxarifado);

		if (transferencia == null){
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00018);
		} else {
			// Seta o indicador de material bloqueado através do almoxarifado da transferência localizada
			itemTransferencia.setMatBloqueado(transferencia.getAlmoxarifado().getIndBloqEntrTransf());
		}

	}

	/**
	 * ORADB PROCEDURE SCEK_ITR_RN.RN_ITRP_VER_CONSIGN
	 * Verifica se o estoque almoxarifado, fornecedor e material são equivalentes em  estoque almoxarifado de origem e destino
	 * @param itemTransferencia
	 */
	public void validarConsignado(SceEstoqueAlmoxarifado seqEstoqueAlmoxarifadoOrigem, SceEstoqueAlmoxarifado seqEstoqueAlmoxarifadoDestino) throws ApplicationBusinessException{

		if (seqEstoqueAlmoxarifadoOrigem != null) {

			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifadoConsignado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoaConsignadoTransferencia(seqEstoqueAlmoxarifadoOrigem.getSeq(), seqEstoqueAlmoxarifadoDestino.getSeq());

			if (listaEstoqueAlmoxarifadoConsignado == null) {

				throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00292);

			}

		} else {

			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00292);

		}


	}

	/*
	 * RNs Remover
	 */

	/**
	 * ORADB PROCEDURE SCEK_ITR_RN.RN_ITRP_VER_EXCLUSAO
	 * @param seqTransferencia
	 */
	public void validarExclusao(Integer seqTransferencia) throws BaseException{

		final SceTransferencia transferencia = this.getSceTransferenciaDAO().obterTransferenciaPorSeqEfetivada(seqTransferencia);

		// Não é possível excluir itens de uma transferência efetivada
		if (transferencia != null) {
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00490);
		}

	}

	/**
	 * ORADB PROCEDURE RN_ITRP_DESBLQ_MAT
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void atualizaMaterialAlmoxDestino(SceTransferencia transferencia, SceEstoqueAlmoxarifado estoqueAlmoxarifado, Integer quantidade, String nomeMicrocomputador) throws BaseException{

		SceEstoqueAlmoxarifado estoqueAlmx = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoQuantidadeBloqEntradaTransf(estoqueAlmoxarifado.getSeq(), quantidade);

		if (estoqueAlmx == null) {

			//Estoque destino não encontrado ou sem saldo suficiente para desbloqueio
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.MSG_ESTOQUE_NAO_ENCONTRADO_OU_SEM_SALDO);

		} else {

			//Ao ser desbloqueado o material do ítem da transferência é atualizado o estoque para disponível
			estoqueAlmx.setQtdeDisponivel(estoqueAlmx.getQtdeDisponivel() + quantidade);
			estoqueAlmx.setQtdeBloqEntrTransf(estoqueAlmx.getQtdeBloqEntrTransf() - quantidade);

			/**
			 * Atualiza SceEstoqueAlmoxarifado
			 */
			getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmx, nomeMicrocomputador, true);

		}

	}


	/**
	 * ORADB PROCEDURE  RN_ITRP_VER_ESTQ_ALM
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void validarAlmoxarifados(SceItemTransferencia itemTransferencia) throws BaseException{

		if (itemTransferencia !=null && (itemTransferencia.getEstoqueAlmoxarifado() == null || itemTransferencia.getEstoqueAlmoxarifado().getIndSituacao().equals(DominioSituacao.I))) {
			//Estoque Almoxarifado não cadastrado ou Inativo
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00292);
		}

		itemTransferencia.setUnidadeMedida(itemTransferencia.getEstoqueAlmoxarifado().getUnidadeMedida());

	}

	/**
	 * ORADB PROCEDURE scek_itr_rn.rn_itrp_ver_alm_pai
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void validarAlmoxarifadoPai(SceItemTransferencia itemTransferencia) throws BaseException{

		SceTransferencia transferencia = itemTransferencia.getTransferencia();
		SceEstoqueAlmoxarifado almoxarifado1 = this.getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorItemTransferencia(itemTransferencia.getEstoqueAlmoxarifado().getSeq(), transferencia.getAlmoxarifadoRecebimento().getSeq());

		if (almoxarifado1 != null) {

			SceEstoqueAlmoxarifado almoxarifado2 = this.getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(transferencia.getAlmoxarifado().getSeq(), almoxarifado1.getMaterial().getCodigo(), almoxarifado1.getFornecedor().getNumero());

			if (almoxarifado2 == null) {

				throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00469);

			}

		} else {

			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00469);

		}

	}

	/**
	 * ORADB PROCEDURE RN_ITRP_VER_ALTERAC
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void validarAlteracao(SceItemTransferencia itemTransferencia) throws BaseException{

		SceTransferencia transferencia = this.getSceTransferenciaDAO().obterTransferenciaPorSeq(itemTransferencia.getId().getTrfSeq());

		if (transferencia == null) {
			// Não existeTransferência para esta seq
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.SCE_00124);
		}

	}

	/**
	 * Verifica se o item é duplicado
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	private void validaItemDuplicado(SceItemTransferencia itemTransferencia) throws BaseException {
	
		SceItemTransferencia itemTransAux = getSceItemTransferenciaDAO().obterItemTransferencia(itemTransferencia); 
		
		if (itemTransAux != null) {
			
			throw new ApplicationBusinessException(SceItemTransferenciaRNCode.MENSAGEM_ITEM_TRANS_DUPLICADO, itemTransAux.getEstoqueAlmoxarifado().getMaterial().getCodigo(), itemTransAux.getId().getTrfSeq());
		
		}
		
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}



	/**
	 * Getters para RNs e DAOs
	 */

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public SceItemTransferenciaDAO getSceItemTransferenciaDAO(){
		return sceItemTransferenciaDAO;
	}

	public SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}

	public SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
}
