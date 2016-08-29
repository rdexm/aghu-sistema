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
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;



/**
 * Classe responsável pela integração do controle de estoque 
 * com a dispensação de medicamentos.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class DispensacaoMedicamentoON extends BaseBusiness {


@EJB
private SceItemRmsRN sceItemRmsRN;

@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;

@EJB
private GerarRequisicaoMaterialON gerarRequisicaoMaterialON;

private static final Log LOG = LogFactory.getLog(DispensacaoMedicamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceItemRmsDAO sceItemRmsDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9004433603982817625L;
	
	public enum DispensacaoMedicamentoONExceptionCode implements BusinessExceptionCode {
		ERRO_DISPENSACAO_MEDICAMENTO_QUANT_INDISPONIVEL_ESTOQUE, ERRO_DISPENSACAO_MEDICAMENTO_QUANT_SEM_ESTOQUE
	}
	
	/**
	 * Verifica se a unidade funcional da dispensacao
	 * permite controle de estoque.
	 * 
	 * @param dispMdtoNew
	 * @return
	 */
	public Boolean verificarUnidadeFuncionalPermiteControleEstoque(AfaDispensacaoMdtos dispMdtoNew) {
		return dispMdtoNew.getUnidadeFuncional().getControleEstoque();
	}

	/**
	 * Trata a Requisição de Material:
	 * se existe, edita o item da RM alterando sua quantidade,
	 * caso não exista, inclui o novo item.
	 * 
	 * @param dispMdtoNew
	 */
	private void atualizarRequisicaoMaterial(AfaDispensacaoMdtos dispMdtoNew, 
			SceEstoqueAlmoxarifado estoqueAlmoxarifado, Integer qtdeDispensada, String nomeMicrocomputador) throws BaseException {
		SceReqMaterial reqMaterial = getGerarRequisicaoMaterialON().retornarReqMaterial(
				dispMdtoNew.getUnidadeFuncional(), dispMdtoNew.getAtendimento());
		SceItemRmsRN sceItemRmsRN = getSceItemRmsRN();
		
		// consulta C3
		List<SceItemRms> listaItemRms = getSceItemRmsDAO().pesquisarItemDispensadoDataCorrente(
				dispMdtoNew.getMedicamento().getMatCodigo(), 
				dispMdtoNew.getUnidadeFuncional().getSeq(), 
				reqMaterial.getSeq(), estoqueAlmoxarifado.getSeq());
		
		SceItemRms itemRms = null;
		
		if (!listaItemRms.isEmpty()) {
			itemRms = listaItemRms.get(0);
			Integer qtdeRequisitada = itemRms.getQtdeRequisitada() + qtdeDispensada;
			itemRms.setQtdeRequisitada(qtdeRequisitada);
			getSceItemRmsRN().atualizar(itemRms);
		}
		else {
			ScoMaterial material = new ScoMaterial();
			material = getComprasFacade().obterScoMaterialPorChavePrimaria(dispMdtoNew.getMedicamento().getMatCodigo());
			SceItemRms novoItemRms = new SceItemRms();
			SceItemRmsId itemRmsId = new SceItemRmsId(reqMaterial.getSeq(), estoqueAlmoxarifado.getSeq());
			novoItemRms.setId(itemRmsId);
			novoItemRms.setSceReqMateriais(reqMaterial);
			novoItemRms.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
			novoItemRms.setScoUnidadeMedida(material.getUnidadeMedida());
			novoItemRms.setQtdeRequisitada(qtdeDispensada);
			novoItemRms.setIndTemEstoque(Boolean.TRUE);
			sceItemRmsRN.inserir(novoItemRms, nomeMicrocomputador);
		}
	}
	
	/**
	 * Atualiza no estoque a quantidade bloqueada e disponivel
	 * do medicamento a ser dispensado. A atualizacao é feita 
	 * primeiramente no fornecedor padrão e depois nos outros fornecedores.
	 * 
	 * @param dispMdtoNew
	 * @param estoqueAlmoxarifado
	 * @throws ApplicationBusinessException
	 */
	private void executarAtualizacaoMedicamentoEstoque(AfaDispensacaoMdtos dispMdtoNew, String etiqueta, String nomeMicrocomputador) throws BaseException {
		
		// consulta C2
		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = 
				getSceEstoqueAlmoxarifadoDAO().pesquisarDisponibilidadeMaterialEstoque(
						dispMdtoNew.getMedicamento().getMatCodigo(), dispMdtoNew.getUnidadeFuncional().getSeq());		
		
		Integer qtdeDispensada = obterQuantidadeDispensada(dispMdtoNew, etiqueta);
		CalculoDispensacao calculoDispensacao = new CalculoDispensacao(qtdeDispensada);
						
		if (!listaEstoqueAlmoxarifado.isEmpty()) {
			
			// Soma o totalQtdeDisponivel considerando a qtdeDisponivel de todos os fornecedores do medicamento
			Integer totalQtdeDisponivel = 0;
			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifado) {
				totalQtdeDisponivel += estoqueAlmoxarifado.getQtdeDisponivel();
			}
			
			if (totalQtdeDisponivel > 0 && totalQtdeDisponivel >= qtdeDispensada) {
				if (listaEstoqueAlmoxarifado.size() == 1) {
					SceEstoqueAlmoxarifado estoqueAlmoxarifado = listaEstoqueAlmoxarifado.get(0);
					atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.FALSE, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);
					atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);
				} else if (listaEstoqueAlmoxarifado.size() > 1) {
					AghParametros paramFornecedorPadrao = 
							getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
					Integer numeroFornecedorPadrao = paramFornecedorPadrao.getVlrNumerico().intValue();
					
					/*
					 * Atualiza qtdeDisponivel primeiramente do fornecedor padrão
					 */
					for (Iterator<SceEstoqueAlmoxarifado> iterator = listaEstoqueAlmoxarifado.iterator(); iterator.hasNext();) {
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = (SceEstoqueAlmoxarifado) iterator.next();
						Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
						Integer qtdeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel();
						// É o fornecedor padrão e estoque possui quantidade disponivel
						if (numeroFornecedor.intValue() == numeroFornecedorPadrao.intValue() && qtdeDisponivel > 0) {
							if (etiqueta != null) {
								atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.TRUE, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);
								atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);	
							} else {
								Integer novaQtdeDispensada = obterQuantidadeDispensacaoContraDisponivel(qtdeDisponivel, qtdeDispensada);
								atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.TRUE, estoqueAlmoxarifado, novaQtdeDispensada, nomeMicrocomputador);
								atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, novaQtdeDispensada, nomeMicrocomputador);
							}
							// Remove da lista o estoque relacionado ao fornecedor padrão
							iterator.remove();
							break;
						}						
					}

					/*
					 * Atualiza qtdeDisponivel dos outros fornecedores
					 */
					for (Iterator<SceEstoqueAlmoxarifado> iterator = listaEstoqueAlmoxarifado.iterator(); iterator.hasNext();) {
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = (SceEstoqueAlmoxarifado) iterator.next();
						Integer qtdeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel();
						Integer qtdeRestanteDispensar = calculoDispensacao.getQtdeDispensacao();
						// Estoque possui quantidade disponivel e ainda possui quantidade a dispensar
						if (qtdeDisponivel > 0 	&& qtdeRestanteDispensar > 0) {
							if (etiqueta != null) {
								atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.TRUE, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);
								atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, qtdeDispensada, nomeMicrocomputador);
							} else if (iterator.hasNext()) {
								Integer novaQtdeDispensada = obterQuantidadeDispensacaoContraDisponivel(qtdeDisponivel, qtdeDispensada);
								atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.TRUE, estoqueAlmoxarifado, novaQtdeDispensada, nomeMicrocomputador);
								atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, novaQtdeDispensada, nomeMicrocomputador);
							} else if (!iterator.hasNext()) {
								// Caso exista qtde a dispensar e 
								// chegou no ultimo fornecedor encontrado
								atualizarQuantidadeMedicamentoEstoque(calculoDispensacao, Boolean.TRUE, estoqueAlmoxarifado, qtdeRestanteDispensar, nomeMicrocomputador);
								atualizarRequisicaoMaterial(dispMdtoNew, estoqueAlmoxarifado, qtdeRestanteDispensar, nomeMicrocomputador);
							}
						}
					}
				}
			} else {
				throw new ApplicationBusinessException(
						DispensacaoMedicamentoONExceptionCode.ERRO_DISPENSACAO_MEDICAMENTO_QUANT_INDISPONIVEL_ESTOQUE);
			}
		}
		else {
			throw new ApplicationBusinessException(
					DispensacaoMedicamentoONExceptionCode.ERRO_DISPENSACAO_MEDICAMENTO_QUANT_SEM_ESTOQUE);
		}			
	}
	
	private Integer obterQuantidadeDispensada(AfaDispensacaoMdtos dispMdtoNew, String etiqueta) {
		// Caso dispensacao seja por etiqueta (cod. de barras) 
		// e' dispensado um por vez
		if (etiqueta != null) {
			return 1;
		} else {
			return dispMdtoNew.getQtdeDispensada().intValue();
		}
	}
	
	private Integer obterQuantidadeDispensacaoContraDisponivel(Integer qtdeDisponivel, Integer qtdeDispensada) {
		if (qtdeDisponivel >= qtdeDispensada) {
			return qtdeDispensada;
		} else {
			return qtdeDisponivel;
		}
	}
	
	private void atualizarQuantidadeMedicamentoEstoque(CalculoDispensacao calculoDispensacaoFornecedores,
			Boolean possuiOutrosFornecedores, SceEstoqueAlmoxarifado estoqueAlmoxarifado, Integer qtdeParaDispensar, String nomeMicrocomputador) throws BaseException {
		
		Integer qtdeDispensacao = calculoDispensacaoFornecedores.getQtdeDispensacao();
		Integer qtdeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel();
		Integer qtdeBloqDispensacao = estoqueAlmoxarifado.getQtdeBloqDispensacao();
		Boolean possuiQtdeDisponivelMaiorQueDispensacao = Boolean.FALSE;
		
		if (qtdeBloqDispensacao == null) {
			qtdeBloqDispensacao = 0;
		}
		
		if (qtdeDisponivel >= qtdeDispensacao) {
			qtdeDisponivel -= qtdeDispensacao;
			possuiQtdeDisponivelMaiorQueDispensacao = Boolean.TRUE;
		} else if (possuiOutrosFornecedores) {
			// Desconta qtdeDisponivel de qtdeDispensacao e zera qtdeDisponivel
			qtdeDispensacao -= qtdeDisponivel;
			qtdeDisponivel = 0; 
		}
		
		qtdeBloqDispensacao += qtdeParaDispensar;
		
		if (possuiQtdeDisponivelMaiorQueDispensacao) {
			qtdeDispensacao -= qtdeParaDispensar;	
		}
		
		estoqueAlmoxarifado.setQtdeBloqDispensacao(qtdeBloqDispensacao);
		estoqueAlmoxarifado.setQtdeDisponivel(qtdeDisponivel);
		getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
		
		calculoDispensacaoFornecedores.setQtdeDispensacao(qtdeDispensacao);
	}
	
	/**
	 * Classe utilizada para armazenar temporariamente
	 * a quantidade atual para a dispensacao.
	 * 
	 * @author diego.pacheco
	 *
	 */
	private class CalculoDispensacao {
		
		// Quantidade a dispensar 
		private Integer qtdeDispensacao;
		
		public CalculoDispensacao(Integer qtdeDispensacao) {
			this.qtdeDispensacao = qtdeDispensacao;
		}
		
		public Integer getQtdeDispensacao() {
			return qtdeDispensacao;
		}

		public void setQtdeDispensacao(Integer qtdeDispensacao) {
			this.qtdeDispensacao = qtdeDispensacao;
		}

	}
	
	/**
	 * Método responsável por agrupar as regras de negócio
	 * necessárias para realizar as atualizações no estoque 
	 * para a dispensação do medicamento.
	 * 
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void tratarDispensacaoMedicamentoEstoque(AfaDispensacaoMdtos dispMdtoNew, String etiqueta, String nomeMicrocomputador) throws BaseException {
		if (verificarUnidadeFuncionalPermiteControleEstoque(dispMdtoNew)) {
			executarAtualizacaoMedicamentoEstoque(dispMdtoNew, etiqueta, nomeMicrocomputador);
		}
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceItemRmsDAO getSceItemRmsDAO() {
		return sceItemRmsDAO;
	}	
	
	protected GerarRequisicaoMaterialON getGerarRequisicaoMaterialON() {
		return gerarRequisicaoMaterialON;
	}
	
	protected SceItemRmsRN getSceItemRmsRN() {
		return sceItemRmsRN;
	}
	
	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}	

}
