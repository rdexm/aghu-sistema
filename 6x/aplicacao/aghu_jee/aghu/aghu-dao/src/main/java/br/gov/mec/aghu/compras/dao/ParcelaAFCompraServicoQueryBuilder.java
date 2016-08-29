package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ParcelaAFCompraServicoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 736535683647882158L;

	private FiltroProgrGeralEntregaAFVO filtro;
	
	private Boolean isC1;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		basicQuery(criteria);
		
		adicionaNotaFiscal(criteria);
		adicionaComplemento(criteria);
		adicionarAFP(criteria);
		adicionarFornecedor(criteria);
		adicionarServico(criteria);
		adicionarGrupoServico(criteria);
		adicionarMaterial(criteria);
		adicionarGrupoMaterial(criteria);
		adicionarNumeroSolicitacao(criteria);
		adicionarCentroCustos(criteria);
		adicionarCentroCustrosApp(criteria);
		adicionaModalidade(criteria);
		adicionarModalidadeEmpenho(criteria);
		adicionaVencimento(criteria);
		adicionarEmpenho(criteria);
		adicionarEstocavel(criteria);
		adicionarEfetivada(criteria);
		adicionarAssinada(criteria);
		adicionaCurvaABC(criteria);
		adicionarEnvioFornecedor(criteria);
		adicionarPlanejamento(criteria);
		
		if(filtro.getPrevisaoDtInicial() != null && filtro.getPrevisaoDtFinal() != null) {
			criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(),filtro.getPrevisaoDtInicial(), filtro.getPrevisaoDtFinal()));
		}else if(filtro.getPrevisaoDtInicial() != null){
			criteria.add(Restrictions.ge("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(),filtro.getPrevisaoDtInicial()));
		}else if(filtro.getPrevisaoDtFinal() != null){
			criteria.add(Restrictions.le("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(),filtro.getPrevisaoDtFinal()));
		}
		
		criarProjection(criteria);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProgrGeralEntregaAFVO.class));
	}


	private void basicQuery(DetachedCriteria criteria) {
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN."+  ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(),"PROPFORN");
		criteria.createAlias("PROPFORN."+  ScoPropostaFornecedor.Fields.FORNECEDOR.toString(),"FRN");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC1");
		
		if(isC1) {
			criteria.createAlias("FSC1." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		} else {
			criteria.createAlias("FSC1." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
		}
		
		if(isC1) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC2");		
		} else {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.FASES_SOLICITACAO.toString(), "FSC2");
		}
		
		if(isC1) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GM");
		} else {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
			criteria.createAlias("SRV." + ScoServico.Fields.GRUPO_SERVICO.toString(), "GS");
		}
		
		criteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), 
				"AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), 
				"FSC1." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));


		criteria.add(Restrictions.eq("FSC1." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));

		if(isC1){
			criteria.add(Restrictions.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "FSC1." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		} else {
			criteria.add(Restrictions.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), "FSC1." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		} 
		
		criteria.add(Restrictions.isNotNull("FSC2." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
		criteria.add(Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), 
				"FSC1." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), 
				"FSC1." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));

		criteria.add(Restrictions.eqProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), 
				"IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));		

		if(isC1) {
			criteria.add(Restrictions.eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(),"FSC1." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			criteria.add(Restrictions.eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString(), "SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()));
		} else {
			criteria.add(Restrictions.eqProperty("SLS." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(),"FSC1." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			criteria.add(Restrictions.eqProperty("SRV." + ScoServico.Fields.CODIGO.toString(),"SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()));
		}
		
		if(!isC1){
			criteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), 
					"IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
			criteria.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), 
					"IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()));
		}
		
		criteria.add(Restrictions.eqProperty("FRN." + ScoFornecedor.Fields.NUMERO.toString(), 
				"AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
	}

	private void criarProjection(DetachedCriteria criteria) {
		ProjectionList projectionList = Projections.projectionList();
		
		projectionList.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()), "nroAF");
			projectionList.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), "af");
			projectionList.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), "cp");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString()), "afp");
			projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), "itemAF");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "progEntregaItemAFSeq");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "parcela");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()), "iafAfnNumero");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()), "iafNumero");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString()), "previsaoDt");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()), "qtdParcela");
			projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO_FORN.toString()), "umdForn");
			projectionList.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO_FORM.toString()), "fatorConv");
			
			if(isC1) {
				projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), "umdMat");
				projectionList.add(Projections.property("GM." + ScoGrupoMaterial.Fields.CODIGO.toString()), "codGrupoMat");
				projectionList.add(Projections.property("GM." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), "descGrupoMat");
				projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), "matCodigo");
				projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), "matNome");
				projectionList.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), "nroSol");
			} else {
				projectionList.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), "matNome");
				projectionList.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), "matCodigo");
				projectionList.add(Projections.property("GS." + ScoGrupoServico.Fields.CODIGO.toString()), "codGrupoMat");
				projectionList.add(Projections.property("GS." + ScoGrupoServico.Fields.DESCRICAO.toString()), "descGrupoMat");
				projectionList.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()), "nroSol");
			}
			
			projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), "numeroFornecedor");
			projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "razaoSocialFornecedor");
			projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), "cpfFornecedor");
			projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), "cgcFornecedor");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString()), "indEmp");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString()), "indCanc");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString()), "indPlan");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString()), "indAss");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString()), "indEnvioForn");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_AUTOMATICO.toString()), "indRecalculoAutomatico");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_RECALCULO_MANUAL.toString()), "indRecalculoManual");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENTREGA_IMEDIATA.toString()), "indEntregaImediata");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_TRAMITE_INTERNO.toString()), "indTramiteInterno");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.OBSERVACAO.toString()), "obs");
			projectionList.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_ENTREGA.toString()), "dtEntrega");
		criteria.setProjection(projectionList);
	}

	private void adicionarPlanejamento(DetachedCriteria criteria) {
		if(filtro.getPlanejada() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), filtro.getPlanejada().isSim()));
		}
	}

	private void adicionarEnvioFornecedor(DetachedCriteria criteria) {
		if(filtro.getEnvForn() != null ) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), filtro.getEnvForn().isSim()));
		}
	}

	private void adicionarAssinada(DetachedCriteria criteria) {
		if(filtro.getAssinada() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), filtro.getAssinada().isSim()));
		}
	}

	private void adicionarEfetivada(DetachedCriteria criteria) {
		if(filtro.getEfetivada() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EFETIVADA.toString(), filtro.getEfetivada().isSim()));
		}
	}

	private void adicionarEstocavel(DetachedCriteria criteria) {
		if(isC1 && filtro.getEstocavel() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), filtro.getEstocavel().isSim()));
		}
	}

	private void adicionarEmpenho(DetachedCriteria criteria) {
		if(filtro.getEmpenho() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_EMPENHADA.toString(), 
					filtro.getEmpenho()));
		}
	}

	private void adicionarModalidadeEmpenho(DetachedCriteria criteria) {
		if(filtro.getModalidadeEmpenho() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), filtro.getModalidadeEmpenho()));
		}
	}

	private void adicionarCentroCustrosApp(DetachedCriteria criteria) {
		if(filtro.getCentroCustoApp() != null && filtro.getCentroCustoApp().getCodigo() != null) {
			if(isC1) {
				criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(), filtro.getCentroCustoApp().getCodigo()));	
			} else {
				criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CC_APLICADA_CODIGO.toString(), filtro.getCentroCustoApp().getCodigo()));	
			}
		}
	}

	private void adicionarCentroCustos(DetachedCriteria criteria) {
		if(filtro.getCentroCustoSol() != null && filtro.getCentroCustoSol().getCodigo() != null) {
			if(isC1){
				criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(), filtro.getCentroCustoSol().getCodigo()));
			} else {
				criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.CCT_CODIGO.toString(), filtro.getCentroCustoSol().getCodigo()));	
			}
		}
	}

	private void adicionarNumeroSolicitacao(DetachedCriteria criteria) {
		if(filtro.getNroSolicitacao() != null) {
			if(isC1){
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtro.getNroSolicitacao()));
			} else {
				criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtro.getNroSolicitacao()));
			}
		}
	}

	private void adicionarGrupoMaterial(DetachedCriteria criteria) {
		if(isC1 && filtro.getGrupoMaterial() != null && filtro.getGrupoMaterial().getCodigo() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
		}
	}

	private void adicionarMaterial(DetachedCriteria criteria) {
		if(isC1 && filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
		}
	}

	private void adicionarFornecedor(DetachedCriteria criteria) {
		if(filtro.getFornecedor() != null && filtro.getFornecedor().getNumero() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}
	}
	
	private void adicionarServico(DetachedCriteria criteria) {
		if(!isC1 && filtro.getServico() != null) {
			criteria.add(Restrictions.eq("SRV." + ScoServico.Fields.CODIGO.toString(), filtro.getServico().getCodigo()));
		}
	}
	
	private void adicionarGrupoServico(DetachedCriteria criteria) {
		if(!isC1 && filtro.getGrupoServico() != null) {
			criteria.add(Restrictions.eq("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtro.getGrupoServico().getCodigo()));
		}
	}

	private void adicionarAFP(DetachedCriteria criteria) {
		if(filtro.getAfp() != null) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), filtro.getAfp()));
		}
	}

	private void adicionaComplemento(DetachedCriteria criteria) {
		if(filtro.getCp() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getCp()));
		}
	}

	private void adicionaCurvaABC(DetachedCriteria criteria) {
		if(isC1 && filtro.getCurvaABC() != null) {
			DetachedCriteria sceEstoqueAlmoxarifado = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
			sceEstoqueAlmoxarifado.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(),
					"MAT." + ScoMaterial.Fields.CODIGO.toString()));
			sceEstoqueAlmoxarifado.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(),
					"MAT." + ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()));
			sceEstoqueAlmoxarifado.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(),
					filtro.getNumeroFornecedorPadrao()));
			sceEstoqueAlmoxarifado.setProjection(Projections.projectionList()
					.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString())));
			
			DetachedCriteria sceEstoqueGeral = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");
			sceEstoqueGeral.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.MAT_CODIGO.toString(),
					"EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()));
			sceEstoqueGeral.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(),
					"EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));
			sceEstoqueGeral.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), filtro.getDataCompetencia()));
			sceEstoqueGeral.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString(), filtro.getCurvaABC()));
			sceEstoqueGeral.setProjection(Projections.projectionList()
					.add(Projections.property("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString())));
			
			sceEstoqueAlmoxarifado.add(Subqueries.exists(sceEstoqueGeral));
			criteria.add(Subqueries.exists(sceEstoqueAlmoxarifado));
		}
	}

	private void adicionaVencimento(DetachedCriteria criteria) {
		if(filtro.getVencimento() != null) {
			DetachedCriteria scoContratos = DetachedCriteria.forClass(ScoContrato.class, "CTR");
			scoContratos.add(Restrictions.eqProperty("CTR." + ScoContrato.Fields.NR_CONTRATO.toString(), 
					  "AFN." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString()));
			scoContratos.add(Restrictions.le("CTR." + ScoContrato.Fields.DT_FIM_VIGENCIA.toString(), filtro.getVencimento()));
			scoContratos.setProjection(Projections.projectionList()
					.add(Projections.property("CTR." + ScoContrato.Fields.NR_CONTRATO)));
			criteria.add(Subqueries.exists(scoContratos));
		}
	}

	private void adicionaModalidade(DetachedCriteria criteria) {
		if(filtro.getModalidade() != null && filtro.getModalidade().getCodigo() != null) {
			DetachedCriteria scoLicitacoes = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
			scoLicitacoes.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO, "MLC");
			scoLicitacoes.add(Restrictions.eqProperty("LCT." + ScoLicitacao.Fields.NUMERO.toString(), 
							  "AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()));
			scoLicitacoes.add(Restrictions.eq("MLC." + ScoModalidadeLicitacao.Fields.CODIGO.toString(), filtro.getModalidade().getCodigo()));
			scoLicitacoes.setProjection(Projections.projectionList()
								.add(Projections.property("MLC." + ScoModalidadeLicitacao.Fields.CODIGO.toString())));
			
			criteria.add(Subqueries.exists(scoLicitacoes));
		}
	}

	private void adicionaNotaFiscal(DetachedCriteria criteria) {
		if(filtro.getNroAF() == null) { // Caso não Informado Nro. AF
			criteria.add(Restrictions
					.sqlRestriction(" (coalesce ({alias}.qtde_entregue,0) < " +
							" {alias}. " + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()+ ")" ));
			criteria.add(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString() , 0));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString() , Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString() , Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString() , Boolean.FALSE));
		} else { // Se Informado Nro. AF
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNroAF()));
		}
	}

	public FiltroProgrGeralEntregaAFVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroProgrGeralEntregaAFVO filtro) {
		this.filtro = filtro;
	}

	public Boolean getIsC1() {
		return isC1;
	}

	public void setIsC1(Boolean isC1) {
		this.isC1 = isC1;
	}

}