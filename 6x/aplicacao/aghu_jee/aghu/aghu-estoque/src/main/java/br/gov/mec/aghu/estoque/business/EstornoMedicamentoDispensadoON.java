package br.gov.mec.aghu.estoque.business;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceDocumentoValidadeID;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceItemDasId;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pela integração do controle de estoque 
 * com o estorno de medicamentos.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class EstornoMedicamentoDispensadoON extends BaseBusiness {

	@EJB
	private SceDocumentoValidadeRN sceDocumentoValidadeRN;
	
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	
	@EJB
	private GerarRequisicaoMaterialON gerarRequisicaoMaterialON;
	
	@EJB
	private DispensacaoMedicamentoON dispensacaoMedicamentoON;
	
	private static final Log LOG = LogFactory.getLog(EstornoMedicamentoDispensadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IEstoqueFacade IestoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemRmsDAO sceItemRmsDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private SceDocumentoValidadeDAO sceDocumentoValidadeDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3888716475894822739L;
	
	public enum EstornoMedicamentoDispensadoONExceptionCode implements BusinessExceptionCode {
		ERRO_ESTORNO_MEDICAMENTO_DISPENSADO_TIPO_MOVIMENTO_NAO_ENCONTRADO;
	}

	/**
	 * Implementacao de regra referente a estória #14504.
	 * 
	 * @param dispensacaoMdto
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.SignatureDeclareThrowsException","PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void estornarMedicamentoRequisicaoMaterial(AfaDispensacaoMdtos dispensacaoMdto, 
			AfaDispensacaoMdtos dispensacaoMdtoOld, String etiqueta, String nomeMicrocomputador) throws BaseException {
		
		if (getDispensacaoMedicamentoON().verificarUnidadeFuncionalPermiteControleEstoque(dispensacaoMdto)) {
			Integer matCodigo = null;
			if(dispensacaoMdto.getMedicamento()!=null){
				matCodigo = dispensacaoMdto.getMedicamento().getMatCodigo();
			}
			Short unfSeq = null;
			if(dispensacaoMdto.getUnidadeFuncional()!=null){
				unfSeq = dispensacaoMdto.getUnidadeFuncional().getSeq();
			}
			
			Integer qtdeEstornada = 0; 
					
			if (etiqueta != null) {
				// Estorno de etiqueta é apenas um por vez
				qtdeEstornada = 1;
			} else if (dispensacaoMdto.getQtdeEstornada() != null) {
				// Estorno sem etiqueta (quantidade setada na dispensacao)
				qtdeEstornada = dispensacaoMdto.getQtdeEstornada().intValue();
			}
			
			Boolean atualizouReqMaterialGerada = Boolean.FALSE;
			
			AghParametros paramFornecedorPadrao = 
					getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			Integer numeroFornecedorPadrao = paramFornecedorPadrao.getVlrNumerico().intValue();
			
			Integer totalQtdEstornadaItemRmsGeradas = 0;
			List<SceItemRms> listaItensGerados = this.getSceItemRmsDAO().pesquisarRequisicaoMaterial(matCodigo, unfSeq, 
					/*dispensacaoMdto.getDthrDispensacao()*/null, DominioSituacaoRequisicaoMaterial.G, dispensacaoMdto.getAtendimento().getSeq());
			if(listaItensGerados!=null && !listaItensGerados.isEmpty()){
				
				// Estorno por etiqueta
				if (etiqueta != null) {
					SceItemRms itemRmEstorno = null;
					SceItemRms itemRmFornecPadrao = null;
					
					for (SceItemRms item : listaItensGerados) {
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
						Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
						
						// Caso seja o fornecedor padrão
						if (verificarFornecedorPadrao(numeroFornecedorPadrao, numeroFornecedor)) {
							itemRmFornecPadrao = item;
							break;
						}
					}
					
					// Quantidade em estoque do fornecedor padrão deve ser estornada primeiro
					if (itemRmFornecPadrao != null) {
						itemRmEstorno = itemRmFornecPadrao;
					} else {
						// Busca outros estoques, exceto aquele que for do fornecedor padrão
						for (SceItemRms item : listaItensGerados) {
							SceEstoqueAlmoxarifado estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
							Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
							
							if (!verificarFornecedorPadrao(numeroFornecedorPadrao, numeroFornecedor)) {
								itemRmEstorno = item;
								break;
							}
						}
					}
					
					// Realiza estorno das quantidades (disponivel e bloqueada para dispensacao) em SceEstoqueAlmoxarifado
					SceEstoqueAlmoxarifado estoqueAlmoxarifado = itemRmEstorno.getEstoqueAlmoxarifado();
					executarEstornoEstoqueAlmoxarifado(null, estoqueAlmoxarifado, qtdeEstornada, nomeMicrocomputador);
					atualizouReqMaterialGerada = executarEstornoItemRm(itemRmEstorno, qtdeEstornada, nomeMicrocomputador);
					
				// Estorno sem etiqueta (quantidade setada na dispensacao)
				} else if (qtdeEstornada != null) {
					CalculoEstorno calculoEstorno = new CalculoEstorno(qtdeEstornada);
					
					// Primeiro estorna do fornecedor padrão
					for (Iterator<SceItemRms> iterator = listaItensGerados.iterator(); iterator.hasNext();) {
						SceItemRms itemRm =  iterator.next();
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = itemRm.getEstoqueAlmoxarifado();
						Integer qtdeBloqueadaDispensacao = estoqueAlmoxarifado.getQtdeBloqDispensacao();
						Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
						if (verificarFornecedorPadrao(numeroFornecedorPadrao, numeroFornecedor)) {
							Integer qtdeEstornar = obterQuantidadeEstornoContraBloqueadaDispensacao(qtdeBloqueadaDispensacao, qtdeEstornada);
							totalQtdEstornadaItemRmsGeradas+=qtdeEstornar;
							executarEstornoEstoqueAlmoxarifado(calculoEstorno, estoqueAlmoxarifado, qtdeEstornar, nomeMicrocomputador);
							atualizouReqMaterialGerada = executarEstornoItemRm(itemRm, qtdeEstornar, nomeMicrocomputador);
							iterator.remove();
							break;
						}
					}
					
					/*
					 * Estorna de outros fornecedores, caso ainda
					 * exista quantidade a estornar 
					 */
					for (Iterator<SceItemRms> iterator = listaItensGerados.iterator(); iterator.hasNext();) {
						SceItemRms itemRm =  iterator.next();
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = itemRm.getEstoqueAlmoxarifado();
						Integer qtdeBloqueadaDispensacao = estoqueAlmoxarifado.getQtdeBloqDispensacao();
						Integer qtdeRestanteEstornar = calculoEstorno.getQtdeEstorno();
						// Caso ainda possua quantidade a estornar
						if (qtdeRestanteEstornar > 0) {
							totalQtdEstornadaItemRmsGeradas+=qtdeRestanteEstornar;
							if (iterator.hasNext()) {
								Integer qtdeEstornar = obterQuantidadeEstornoContraBloqueadaDispensacao(
										qtdeBloqueadaDispensacao, qtdeRestanteEstornar);
								executarEstornoEstoqueAlmoxarifado(calculoEstorno, estoqueAlmoxarifado, qtdeEstornar, nomeMicrocomputador);
								atualizouReqMaterialGerada = executarEstornoItemRm(itemRm, qtdeEstornar, nomeMicrocomputador);
							} else if (!iterator.hasNext()) {
								// Caso exista qtde a estornar e 
								// chegou no ultimo fornecedor encontrado
								executarEstornoEstoqueAlmoxarifado(calculoEstorno, estoqueAlmoxarifado, qtdeRestanteEstornar, nomeMicrocomputador);
								atualizouReqMaterialGerada = executarEstornoItemRm(itemRm, qtdeRestanteEstornar, nomeMicrocomputador);
							}
						}
					}
				}
			}
			//#37264
			Integer qtdeRestanteAEstornarDasEfetivadas = getQtdeRestanteAEstornarDasEfetivadas(atualizouReqMaterialGerada, qtdeEstornada, totalQtdEstornadaItemRmsGeradas);  
			if(qtdeRestanteAEstornarDasEfetivadas != qtdeEstornada){
				qtdeEstornada = qtdeRestanteAEstornarDasEfetivadas;
				atualizouReqMaterialGerada = Boolean.FALSE;
			}
			if (!atualizouReqMaterialGerada) {
				List<SceItemRms> listaItensEfetivados = this.getSceItemRmsDAO().pesquisarRequisicaoMaterial(matCodigo, unfSeq, 
						/*dispensacaoMdto.getDthrDispensacao()*/ null, DominioSituacaoRequisicaoMaterial.E, dispensacaoMdto.getAtendimento().getSeq());
				if(listaItensEfetivados!=null && !listaItensEfetivados.isEmpty()){
					SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
					SceItemRms item = listaItensEfetivados.get(0);
					SceEstoqueAlmoxarifado estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
					SceReqMaterial reqMaterial = item.getSceReqMateriais();
					IEstoqueFacade estoqueFacade = this.getEstoqueFacade();
					
					SceAlmoxarifado almoxarifado = null;
					FccCentroCustos centroCusto = null;
					if(reqMaterial!=null){
						almoxarifado = reqMaterial.getAlmoxarifado();
						centroCusto = reqMaterial.getCentroCusto();
					}
					devolucaoAlmoxarifado.setAlmoxarifado(almoxarifado);
					devolucaoAlmoxarifado.setCentroCusto(centroCusto);
					
					// Tipo de Movimento para DA (devolução almoxarifado)
					AghParametros paramMovimentoDevolucaoAlmox = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TMV_DOC_DA);
					Short tmvSeq = paramMovimentoDevolucaoAlmox.getVlrNumerico().shortValue();
					SceTipoMovimento tipoMovimento = null;
					
					SceTipoMovimentoId id = new SceTipoMovimentoId();
					if (tmvSeq != null) {
						id.setSeq(tmvSeq);
						id.setComplemento(Byte.valueOf("2"));
						tipoMovimento = this.getSceTipoMovimentosDAO().obterPorChavePrimaria(id);
					}
					
					if (tipoMovimento == null) {
						throw new ApplicationBusinessException(
								EstornoMedicamentoDispensadoONExceptionCode
								.ERRO_ESTORNO_MEDICAMENTO_DISPENSADO_TIPO_MOVIMENTO_NAO_ENCONTRADO);
					}
					
					devolucaoAlmoxarifado.setTipoMovimento(tipoMovimento);
					// Implementação para Estória do Usuário #17135 : campo observação com origem da RM
					devolucaoAlmoxarifado.setObservacao("Origem RM: " + item.getId().getRmsSeq());
					estoqueFacade.inserirDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
					
					SceItemDas itemDas = new SceItemDas();
					SceItemDasId itemDasId = new SceItemDasId();
					itemDasId.setDalSeq(devolucaoAlmoxarifado.getSeq());
					itemDasId.setEalSeq(estoqueAlmoxarifado.getSeq());
					itemDas.setId(itemDasId);
					itemDas.setDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
					itemDas.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
					itemDas.setQuantidade(qtdeEstornada);	
					itemDas.setUnidadeMedida(item.getScoUnidadeMedida());
					estoqueFacade.inserirItemDevolucaoAlmoxarifado(itemDas, nomeMicrocomputador);
					
					SceDocumentoValidadeDAO documentoValidadeDAO = getSceDocumentoValidadeDAO();
					
					// Pesquisa as validades do item da RM
					List<SceDocumentoValidade> listaDocumentoValidade = documentoValidadeDAO
							.pesquisarDocumentoValidadePorEalSeqENroDocRequisicaoMaterial(
									estoqueAlmoxarifado.getSeq(), item.getId().getRmsSeq());
					
					// Insere as validades encontradas na DA
					for (SceDocumentoValidade documentoValidade : listaDocumentoValidade) {
						SceDocumentoValidade novoDocumentoValidade = documentoValidadeDAO.obterOriginal(documentoValidade);
						documentoValidadeDAO.desatachar(documentoValidade);
						SceDocumentoValidadeID documentoValidadeId = novoDocumentoValidade.getId();
						documentoValidadeId.setNroDocumento(devolucaoAlmoxarifado.getSeq()); // seta o numero da DA
						documentoValidadeId.setTmvSeq(tipoMovimento.getId().getSeq().intValue());
						documentoValidadeId.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
						novoDocumentoValidade.setId(documentoValidadeId);
						novoDocumentoValidade.setTipoMovimento(tipoMovimento);
						novoDocumentoValidade.setQuantidade(qtdeEstornada);
						novoDocumentoValidade.setVersion(0);
						getSceDocumentoValidadeRN().inserir(novoDocumentoValidade);
					}
				}
			}
		}
		getFarmaciaFacade().atualizaAfaDispMdto(dispensacaoMdto, dispensacaoMdtoOld, nomeMicrocomputador);
	}
	
	private Integer getQtdeRestanteAEstornarDasEfetivadas(
			Boolean atualizouReqMaterialGerada, Integer qtdeEstornada,
			Integer totalQtdEstornadaItemRmsGeradas) {
		//Caso a qtdeEstornada não pode ser totalmente "retirada" dos itensRms gerados, deve remover dos itensRms Efetivados (Gerando Devolução)
		Boolean possuiQtdeParaEstornarItemRmsEfetivada = qtdeEstornada > totalQtdEstornadaItemRmsGeradas;
		if(atualizouReqMaterialGerada && possuiQtdeParaEstornarItemRmsEfetivada){
			return qtdeEstornada - totalQtdEstornadaItemRmsGeradas;
		}
		return qtdeEstornada;
	}
	
	private Boolean verificarFornecedorPadrao(Integer numeroFornecedorPadrao, Integer numeroFornecedor) {
		if (numeroFornecedor.intValue() == numeroFornecedorPadrao.intValue()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	private Integer obterQuantidadeEstornoContraBloqueadaDispensacao(Integer qtdeBloqueadaDispensacao, Integer qtdeEstorno) {
		if (qtdeBloqueadaDispensacao >= qtdeEstorno) {
			return qtdeEstorno;
		} else {
			return qtdeBloqueadaDispensacao;
		}
	}
	
	private void executarEstornoEstoqueAlmoxarifado(CalculoEstorno calculoEstorno, SceEstoqueAlmoxarifado estoqueAlmoxarifado, 
			Integer qtdeParaEstorno, String nomeMicrocomputador) throws BaseException {
		
		Integer qtdeEstorno = null; 
				
		if (calculoEstorno != null) {
			qtdeEstorno = calculoEstorno.getQtdeEstorno();
		} else {
			qtdeEstorno = qtdeParaEstorno;
		}
				
		Integer qtdeBloqDispensacaoEstoque = estoqueAlmoxarifado.getQtdeBloqDispensacao();
		Integer copiaQtdeBloqDispensacaoEstoque = qtdeBloqDispensacaoEstoque.intValue();
		Boolean possuiQtdeBloqDispensacaoSuficiente = Boolean.FALSE;
		
		if (qtdeBloqDispensacaoEstoque != null) {
			if (qtdeBloqDispensacaoEstoque >= qtdeEstorno) {
				qtdeBloqDispensacaoEstoque -= qtdeEstorno;
				possuiQtdeBloqDispensacaoSuficiente = Boolean.TRUE;
			} else {
				// Desconta qtdeBloqDispensacaoEstoque de qtdeEstorno e zera qtdeBloqDispensacaoEstoque
				qtdeEstorno -= qtdeBloqDispensacaoEstoque;
				qtdeBloqDispensacaoEstoque = 0; 
			}
			
			if (possuiQtdeBloqDispensacaoSuficiente) {
				estoqueAlmoxarifado.setQtdeDisponivel(estoqueAlmoxarifado.getQtdeDisponivel() + qtdeEstorno);	
			} else {
				estoqueAlmoxarifado.setQtdeDisponivel(estoqueAlmoxarifado.getQtdeDisponivel() + copiaQtdeBloqDispensacaoEstoque);	
			}
			
			estoqueAlmoxarifado.setQtdeBloqDispensacao(qtdeBloqDispensacaoEstoque);
			this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
		}
		
		// Atualiza com a quantidade ainda a estornar
		if (possuiQtdeBloqDispensacaoSuficiente) {
			qtdeEstorno -= qtdeParaEstorno;	
		}
		
		if (calculoEstorno != null) {
			calculoEstorno.setQtdeEstorno(qtdeEstorno);	
		}
	}
	
	private Boolean executarEstornoItemRm(SceItemRms itemRm, Integer qtdeEstornada, String nomeMicrocomputador) throws BaseException {
		Boolean atualizouReqMaterial = Boolean.FALSE;
		
		// Realiza estorno da quantidade requisitada na RM
		Integer qtdeRequisitada = itemRm.getQtdeRequisitada() - qtdeEstornada;
		if (qtdeRequisitada <= 0) {
			getGerarRequisicaoMaterialON().excluirItemRequisicaoMaterial(itemRm, 0, Boolean.TRUE);
			atualizouReqMaterial = Boolean.TRUE;
		} else {
			itemRm.setQtdeRequisitada(qtdeRequisitada);	
			getGerarRequisicaoMaterialON().persistirItensRequisicaoMaterial(itemRm, Boolean.TRUE, nomeMicrocomputador);
			atualizouReqMaterial = Boolean.TRUE;
		}
		
		return atualizouReqMaterial;
	}
	
	/**
	 * Classe Wrapper utilizada para armazenar temporariamente
	 * a quantidade atual para o estorno.
	 * 
	 * @author diego.pacheco
	 *
	 */
	private class CalculoEstorno {
		// Quantidade a estornar
		private Integer qtdeEstorno;

		public CalculoEstorno(Integer qtdeEstorno) {
			this.qtdeEstorno = qtdeEstorno;
		}

		public Integer getQtdeEstorno() {
			return qtdeEstorno;
		}

		public void setQtdeEstorno(Integer qtdeEstorno) {
			this.qtdeEstorno = qtdeEstorno;
		}
	}
	
	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}
	
	protected SceItemRmsDAO getSceItemRmsDAO() {
		return sceItemRmsDAO;
	}
	
	protected SceDocumentoValidadeDAO getSceDocumentoValidadeDAO() {
		return sceDocumentoValidadeDAO;
	}
	
	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}
	
	protected SceDocumentoValidadeRN getSceDocumentoValidadeRN() {
		return sceDocumentoValidadeRN;
	}	
	
	protected DispensacaoMedicamentoON getDispensacaoMedicamentoON() {
		return dispensacaoMedicamentoON;
	}
	
	protected GerarRequisicaoMaterialON getGerarRequisicaoMaterialON() {
		return gerarRequisicaoMaterialON;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.IestoqueFacade;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
}
