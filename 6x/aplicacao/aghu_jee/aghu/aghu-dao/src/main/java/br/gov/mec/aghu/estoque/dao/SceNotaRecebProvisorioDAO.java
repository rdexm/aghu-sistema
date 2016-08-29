package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentosProvisoriosVO;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioSituacaoDevolucao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.vo.ConfirmacaoRecebimentoFiltroVO;
import br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SceNotaRecebProvisorioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceNotaRecebProvisorio> {

	private static final long serialVersionUID = 5517932964887937134L;

	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorioConfirmacao(final ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		if (indConfirmado != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), indConfirmado));
		}
		if (indEstorno != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), indEstorno));
		}

		if (filtroVO != null) {
			if (filtroVO.getLctNumero() != null || filtroVO.getNumeroFornecedor() != null) {
				criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.AF_PEDIDO.toString(), "AFP", JoinType.LEFT_OUTER_JOIN);
				criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AF",
						JoinType.LEFT_OUTER_JOIN);
				if (filtroVO.getLctNumero() != null) {
					criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtroVO.getLctNumero()));
				}
				if (filtroVO.getNroComplemento() != null) {
					criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroVO.getNroComplemento()));
				}
				if (filtroVO.getNumeroFornecedor() != null) {
					criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(),
							filtroVO.getNumeroFornecedor()));
				}
			}
			if (filtroVO.getNrpSeq() != null) {
				criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), filtroVO.getNrpSeq()));
			}
			if (filtroVO.getSeqNota() != null) {
				criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.DFE_SEQ.toString(), filtroVO.getSeqNota()));
			}
		}
		criteria.addOrder(Order.desc(SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()));
		return executeCriteria(criteria);
	}

	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc) {
		final DetachedCriteria criteria = this.criarCriteriaNotasRecebimentoProvisorio(filtroVO);
		criteria.addOrder(Order.desc("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long contarNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO) {
		return executeCriteriaCount(this.criarCriteriaNotasRecebimentoProvisorio(filtroVO));
	}

	private DetachedCriteria criarCriteriaNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO) {
		final DetachedCriteria criteria = this.criarAlias(filtroVO);
		this.adicionarFiltrosNRP(criteria, filtroVO);
		this.adicionarDemaisFiltros(criteria, filtroVO);
		if (filtroVO.getCodigoMaterial() != null) {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
			subCriteria.setProjection(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
			subCriteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA", JoinType.LEFT_OUTER_JOIN);// ScoProgEntregaItemAutorizacaoFornecimento
			subCriteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF",
					JoinType.LEFT_OUTER_JOIN);
			subCriteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN);
			subCriteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
			subCriteria
					.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString(), filtroVO.getCodigoMaterial()));

			criteria.add(Subqueries.propertyIn("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), subCriteria));
		}
		if (filtroVO.getCodigoServico() != null) {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
			subCriteria.setProjection(Projections.property("IRP." + SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
			subCriteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA", JoinType.LEFT_OUTER_JOIN);// ScoProgEntregaItemAutorizacaoFornecimento
			subCriteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF",
					JoinType.LEFT_OUTER_JOIN);
			subCriteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN);
			subCriteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
			subCriteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString(), filtroVO.getCodigoServico()));

			criteria.add(Subqueries.propertyIn("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), subCriteria));
		}
		return criteria;
	}

	private DetachedCriteria criarAlias(final RecebimentoFiltroVO filtroVO) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.AF_PEDIDO.toString(), "AFP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		if (filtroVO.getTipoDevolucao() != null) {
			criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.NOTA_RECEBIMENTO.toString(), "NR", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("NR." + SceNotaRecebimento.Fields.BOLETIM_OCORRENCIAS.toString(), "BOC", JoinType.INNER_JOIN);
		}
		return criteria;
	}

	private void adicionarFiltrosNRP(final DetachedCriteria criteria, final RecebimentoFiltroVO filtroVO) {
		if (filtroVO.getNrpSeq() != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), filtroVO.getNrpSeq()));
		}
		if (filtroVO.getIndConfirmado() != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), filtroVO.getIndConfirmado()));
		}
		if (filtroVO.getIndEstorno() != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), filtroVO.getIndEstorno()));
		}
		if (filtroVO.getEslSeq() != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.ESL_SEQ.toString(), filtroVO.getEslSeq()));
		}
		if (filtroVO.getNrSeq() != null) {
			// Mapeamento OneToOne não realizado pois é possível ter duas ou
			// mais ocorrências em banco, o que não deveria acontecer
			// negocialmente.
			//
			// Sendo assim, ao buscar no banco, validamos a ON e lançamos
			// mensagem de não conformidade. Ver estória #28709
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NR");
			subCriteria.setProjection(Projections.property("NR." + SceNotaRecebimento.Fields.NRP_SEQ.toString()));
			subCriteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), filtroVO.getNrSeq()));
			criteria.add(Subqueries.propertyIn("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), subCriteria));
		}
		this.adicionarFiltroDataGeracao(criteria, filtroVO);
	}

	private void adicionarFiltroDataGeracao(final DetachedCriteria criteria, final RecebimentoFiltroVO filtroVO) {
		if (filtroVO.getDtGeracaoIni() != null || filtroVO.getDtGeracaoFim() != null) {
			if (filtroVO.getDtGeracaoIni() != null && filtroVO.getDtGeracaoFim() != null) {
				criteria.add(Restrictions.between("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString(),
						DateUtil.obterDataComHoraInical(filtroVO.getDtGeracaoIni()),
						DateUtil.obterDataComHoraFinal(filtroVO.getDtGeracaoFim())));
			} else {
				if (filtroVO.getDtGeracaoIni() == null) {
					criteria.add(Restrictions.lt("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString(),
							DateUtil.obterDataComHoraFinal(filtroVO.getDtGeracaoFim())));
				} else {
					criteria.add(Restrictions.between("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString(),
							DateUtil.obterDataComHoraInical(filtroVO.getDtGeracaoIni()), DateUtil.obterDataComHoraFinal(new Date())));
				}
			}
		}
	}

	private void adicionarDemaisFiltros(final DetachedCriteria criteria, final RecebimentoFiltroVO filtroVO) {
		if (filtroVO.getNumeroNota() != null) {
			criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), filtroVO.getNumeroNota()));
		}
		if (filtroVO.getNumeroFornecedor() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), filtroVO.getNumeroFornecedor()));
		}
		if (filtroVO.getLctNumero() != null) {
			criteria.add(Restrictions.eq("PFR." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), filtroVO.getLctNumero()));
		}
		if (filtroVO.getNroComplemento() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroVO.getNroComplemento()));
		}
		if (filtroVO.getTipoDevolucao() != null && filtroVO.getSituacaoDevolucao() != null) {
			if (DominioSituacaoDevolucao.E.equals(filtroVO.getSituacaoDevolucao())) {
				criteria.add(Restrictions.eq("BOC." + SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.E));
			} else if (DominioSituacaoDevolucao.P.equals(filtroVO.getSituacaoDevolucao())) {
				criteria.add(Restrictions.in("BOC." + SceBoletimOcorrencias.Fields.SITUACAO.toString(), new Object[] {
						DominioBoletimOcorrencias.C, DominioBoletimOcorrencias.G }));
			}
		}
	}

	public Integer obterProximoxSeq() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class);

		criteria.setProjection(Projections.max(SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()));

		Integer maxSeq = (Integer) executeCriteriaUniqueResult(criteria);
		if (maxSeq == null) {
			maxSeq = 1;
		}

		maxSeq++;

		return maxSeq;
	}

	/**
	 * Obtem o numero da AF a partir do seq da nrp
	 * 
	 * @param seqNrp
	 * @return Integer
	 */
	public Integer obterNumeroAfPorNumeroRecebProvisorio(Integer seqNrp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		criteria.setProjection(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.AF_PEDIDO_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), seqNrp));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	public DominioTipoFaseSolicitacao obterTipoNotaRecebimento(Integer seqNrp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.createAlias("IRP." + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), "PEA");
		criteria.createAlias("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS");
		criteria.setProjection(Projections.property("FS." + ScoFaseSolicitacao.Fields.TIPO.toString()));
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), seqNrp));
		return (DominioTipoFaseSolicitacao) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Conta itens de nota de recebimento provisório relacionados a serviço.
	 * 
	 * @param nrp
	 *            Nota de Recebimento Provisório
	 * @param t
	 *            Tipo
	 * @return
	 */
	public int contarItensByTipo(SceNotaRecebProvisorio nrp, DominioTipoFaseSolicitacao t) {
		final String IRP = "irp", PEA = "pea", IAF = "iaf", FSL = "fsl";
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, IRP);
		criteria.createAlias(IRP + '.' + SceItemRecebProvisorio.Fields.PROG_ENTREGA_IAF.toString(), PEA);
		criteria.createAlias(PEA + '.' + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), IAF);
		criteria.createAlias(IAF + '.' + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSL);

		criteria.add(Restrictions.eq(IRP + '.' + SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), nrp));

		criteria.add(Restrictions.eq(FSL + '.' + ScoFaseSolicitacao.Fields.TIPO.toString(), t));

		return executeCriteriaCount(criteria).intValue();
	}

	public Long consultarCampoObservacaoParcelaPendente(final Integer iafAfnNumero, final Integer iafNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class);
		criteria.createAlias(SceNotaRecebProvisorio.Fields.ITENS_NOTA_RECEB_PROV.toString(), "IRP");
		criteria.createAlias("IRP.progEntregaItemAf", "PEA");
		criteria.add(Restrictions.eq("PEA.id.iafAfnNumero", iafAfnNumero));
		criteria.add(Restrictions.eq("PEA.id.iafNumero", iafNumero));
		criteria.add(Restrictions.eq(SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), false));
		criteria.add(Restrictions.eq(SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), false));
		criteria.setProjection(Projections.sum("IRP.quantidade"));
		Long result = (Long) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = 0L;
		}
		return result;
	}

	/**
	 * Lista recebimentos provisorios C1 de #29051
	 * 
	 * @param filtroVO
	 * @param indConfirmado
	 * @param indEstorno
	 * @return
	 */
	public List<RecebimentosProvisoriosVO> pesquisarRecProvisorio(final ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.AF_PEDIDO.toString(), "AFP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFP." + ScoAutorizacaoFornecedorPedido.Fields.SCO_AUTORIZACAO_FORN.toString(), "AF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORN", JoinType.LEFT_OUTER_JOIN);

		if (indConfirmado != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), indConfirmado));
		}

		if (indEstorno != null) {
			criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), indEstorno));
		}

		if (filtroVO != null) {
			if (filtroVO.getLctNumero() != null) {
				criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtroVO.getLctNumero()));
			}

			if (filtroVO.getNroComplemento() != null) {
				criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroVO.getNroComplemento()));
			}

			if (filtroVO.getNumeroFornecedor() != null) {
				criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtroVO.getNumeroFornecedor()));
			}

			if (filtroVO.getNrpSeq() != null) {
				criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), filtroVO.getNrpSeq()));
			}

			if (filtroVO.getSeqNota() != null) {
				criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.DFE_SEQ.toString(), filtroVO.getSeqNota()));

			}

			if (filtroVO.getEslSeq() != null) {
				criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.ESL_SEQ.toString(), filtroVO.getEslSeq()));
			}
		}

		criteria.addOrder(Order.desc(SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString()),
				RecebimentosProvisoriosVO.Fields.SEQ.toString());
		p.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.DT_GERACAO.toString()),
				RecebimentosProvisoriosVO.Fields.DT_GERACAO.toString());
		p.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.AF_PEDIDO_AFN_NUMERO.toString()),
				RecebimentosProvisoriosVO.Fields.AFE_NUMERO.toString());
		p.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.AFN_NUMERO.toString()),
				RecebimentosProvisoriosVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property("NRP." + SceNotaRecebProvisorio.Fields.ESL_SEQ.toString()),
				RecebimentosProvisoriosVO.Fields.ESL_SEQ.toString());
		p.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),
				RecebimentosProvisoriosVO.Fields.PRF_LCT_NUMERO.toString());
		p.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),
				RecebimentosProvisoriosVO.Fields.NRO_COMPLEMENTO.toString());
		p.add(Projections.property("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString()),
				RecebimentosProvisoriosVO.Fields.SITUACAO.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.CGC.toString()), RecebimentosProvisoriosVO.Fields.CGC.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.CPF.toString()), RecebimentosProvisoriosVO.Fields.CPF.toString());
		p.add(Projections.property("FORN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
				RecebimentosProvisoriosVO.Fields.RAZAO_SOCIAL.toString());
		p.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString()),
				RecebimentosProvisoriosVO.Fields.DOC_FISCAL_ENTRADA_SEQ.toString());
		p.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()),
				RecebimentosProvisoriosVO.Fields.DOC_FISCAL_ENTRADA_NUMERO.toString());
		p.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()),
				RecebimentosProvisoriosVO.Fields.DT_EMISSAO.toString());
		p.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_AUTORIZADA.toString()),
				RecebimentosProvisoriosVO.Fields.DT_AUTORIZACAO.toString());
		p.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.VALOR_TOTAL_NF.toString()),
				RecebimentosProvisoriosVO.Fields.VALOR_RECEBIDO.toString());

		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(RecebimentosProvisoriosVO.class));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * C2 de #43475, C2 de #46083
	 * 
	 * @param numRecebimento
	 * @return numeroNF
	 */
	public Long obterNotaPorNumeroRecebimento(Integer numRecebimento){
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "SNRP");
		criteria.createAlias("SNRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString())));
		
		criteria.add(Restrictions.eq("SNRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), numRecebimento));
		return (Long) executeCriteriaUniqueResult(criteria);
	}

	public Integer pesquisarNumeroNotaFiscal(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebProvisorio.class, "NRP");
		
		criteria.setProjection((Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString())));
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("NRP." + SceNotaRecebProvisorio.Fields.NRP_SEQ.toString() , numero));
		Long numeroNota = (Long) executeCriteriaUniqueResult(criteria);
//		return (Integer) executeCriteriaUniqueResult(criteria);
		return Integer.valueOf(numeroNota.toString());
	}

}