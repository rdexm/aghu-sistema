package br.gov.mec.aghu.farmacia.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.vo.TipoUsoMedicamentoVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaDiluentes;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.PdtMedicUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

@SuppressWarnings({ "PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse","PMD.ExcessiveClassLength"})
public class AfaMedicamentoDAO extends BaseDao<AfaMedicamento> {
	
	private static final long serialVersionUID = -6238821290810935731L;
	private static final String TIPO = "TIPO.";

	public String obtemTipoUsoMedicamentoComDuracSol(Integer matCodigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaMedicamento.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property("UMM."
						+ MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()))
				.add(
						Projections.property(AfaMedicamento.Fields.DESCRICAO
								.toString())).add(
						Projections.property(AfaMedicamento.Fields.CONCENTRACAO
								.toString())));

		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS
				.toString(), "UMM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO
				.toString(), "TUM", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO
				.toString(), matCodigo));
		criteria.add(Restrictions.eq(
				"TUM."
						+ AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA
								.toString(), true));

		List<Object[]> lista = this.executeCriteria(criteria);

		if ((lista != null) && (lista.size() > 0)) {
			for (Object[] obj : lista) {
				if ((obj[0] != null) && (obj[2] != null)) {
					return ((String) obj[1]).trim()
							+ ((BigDecimal) obj[2]).toString() + obj[0];
				} else {
					return ((String) obj[1]).trim();
				}
			}
		}
		return null;
	}
	
	public List<MedicamentoVO> obterMedicamentosVO(Object strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial, Boolean listaMedicamentosAux) {
		DetachedCriteria cri = obterCriteriaMedicamentosVO(strPesquisa, listaMedicamentos, situacoes, somenteMdtoDeUsoAmbulatorial, listaMedicamentosAux);
		
		cri.setProjection(Projections.distinct(
				Projections.projectionList()
						.add(Projections.property(VAfaDescrMdto.Fields.MAT_CODIGO.toString()),		MedicamentoVO.Fields.MAT_CODIGO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.MEDICAMENTO.toString()),		MedicamentoVO.Fields.MEDICAMENTO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()),MedicamentoVO.Fields.DESCRICAO_EDITADA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()),	MedicamentoVO.Fields.DESCRICAO_MAT.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.SINONIMO.toString()),		MedicamentoVO.Fields.SINONIMO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.CONCENTRACAO.toString()),	MedicamentoVO.Fields.CONCENTRACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.UMM_SEQ.toString()),			MedicamentoVO.Fields.UMM_SEQ.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TPR_SIGLA.toString()),		MedicamentoVO.Fields.TPR_SIGLA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()),MedicamentoVO.Fields.IND_PADRONIZACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO.toString()),MedicamentoVO.Fields.IND_EXIGE_OBSERVACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.FREQUENCIA_USUAL.toString()),MedicamentoVO.Fields.FREQUENCIA_USUAL.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TUM_SIGLA.toString()),		MedicamentoVO.Fields.TUM_SIGLA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_SITUACAO.toString()),	MedicamentoVO.Fields.IND_SITUACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TFQ_SEQ.toString()),			MedicamentoVO.Fields.TFQ_SEQ.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString()
								+ "."+ MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()),			MedicamentoVO.Fields.DESCRICAO_UNIDADE_MEDICA.toString())
						.add(Projections.property("TUM."
							+ AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA.toString()),	MedicamentoVO.Fields.IND_EXIGE_JUSTIFICATIVA.toString())
						.add(Projections.property("TUM."
								+ AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString()),MedicamentoVO.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString())
						.add(Projections.property("TUM."
								+ AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString()),		MedicamentoVO.Fields.IND_ANTIMICROBIANO.toString())
						.add(Projections.property("TUM."
								+ AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString()))
				));
		
		cri.addOrder(Order.desc(VAfaDescrMdto.Fields.IND_PADRONIZACAO
				.toString()));
		cri.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));

		cri.setResultTransformer(Transformers.aliasToBean(MedicamentoVO.class));
		
		return executeCriteria(cri, 0, 100, null, true);
	}
	
	public List<MedicamentoVO> obterMedicamentosEnfermeiroObstetra(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial) {
		DetachedCriteria cri = obterCriteriaMedicamentosVO(strPesquisa, listaMedicamentos, situacoes, somenteMdtoDeUsoAmbulatorial);

		cri.setProjection(Projections.distinct(
				Projections.projectionList()
						.add(Projections.property(VAfaDescrMdto.Fields.MAT_CODIGO.toString()),		MedicamentoVO.Fields.MAT_CODIGO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.MEDICAMENTO.toString()),		MedicamentoVO.Fields.MEDICAMENTO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()),MedicamentoVO.Fields.DESCRICAO_EDITADA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()),	MedicamentoVO.Fields.DESCRICAO_MAT.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.SINONIMO.toString()),		MedicamentoVO.Fields.SINONIMO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.CONCENTRACAO.toString()),	MedicamentoVO.Fields.CONCENTRACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.UMM_SEQ.toString()),			MedicamentoVO.Fields.UMM_SEQ.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TPR_SIGLA.toString()),		MedicamentoVO.Fields.TPR_SIGLA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()),MedicamentoVO.Fields.IND_PADRONIZACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO.toString()),MedicamentoVO.Fields.IND_EXIGE_OBSERVACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.FREQUENCIA_USUAL.toString()),MedicamentoVO.Fields.FREQUENCIA_USUAL.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TUM_SIGLA.toString()),		MedicamentoVO.Fields.TUM_SIGLA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.IND_SITUACAO.toString()),	MedicamentoVO.Fields.IND_SITUACAO.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TFQ_SEQ.toString()),			MedicamentoVO.Fields.TFQ_SEQ.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString()
								+ "."+ MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()),			MedicamentoVO.Fields.DESCRICAO_UNIDADE_MEDICA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString()
								+ "."+ AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA.toString()),	MedicamentoVO.Fields.IND_EXIGE_JUSTIFICATIVA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString()
								+ "."+ AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString()),MedicamentoVO.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString())
						.add(Projections.property(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString()
								+ "."+ AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString()),		MedicamentoVO.Fields.IND_ANTIMICROBIANO.toString()
				)));

		cri.addOrder(Order.desc(VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()));
		cri.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.PRESCRICAO_ENFERMEIRO_OBSTETRA.toString(),  true));
		cri.setResultTransformer(Transformers.aliasToBean(MedicamentoVO.class));

		return executeCriteria(cri, 0, 100, null, true);

	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private DetachedCriteria obterCriteriaMedicamentosVO(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class, "vAfa");

		cri.createAlias(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(),
				VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(),
				JoinType.INNER_JOIN);

		if (situacoes != null && situacoes.length > 0) {
			cri.add(Restrictions.in(VAfaDescrMdto.Fields.IND_SITUACAO
					.toString(), situacoes));
		} else {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO
					.toString(), DominioSituacao.A));
		}
		
		if (listaMedicamentos != null) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_PADRONIZACAO
					.toString(), listaMedicamentos));
		}
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT
					.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		if(somenteMdtoDeUsoAmbulatorial != null){
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.PRESCRICAO_AMBULATORIAL.toString(), somenteMdtoDeUsoAmbulatorial));
		}


		return cri;
	}
	
	
	/**
	 * 
	 * @param strPesquisa
	 * @param listaMedicamentos
	 * @param situacoes
	 * @param somenteMdtoDeUsoAmbulatorial 
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private DetachedCriteria obterCriteriaMedicamentosVO(Object strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial , Boolean listaMedicamentosAux) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class, "vAfa");

		cri.createAlias(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(),
				"TUM",
				JoinType.INNER_JOIN);

		if (situacoes != null && situacoes.length > 0) {
			cri.add(Restrictions.in(VAfaDescrMdto.Fields.IND_SITUACAO
					.toString(), situacoes));
		} else {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO
					.toString(), DominioSituacao.A));
		}
		
		if ((listaMedicamentos != null)&& (listaMedicamentosAux == false)){
			if(listaMedicamentos == true) {
				cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_PADRONIZACAO
						.toString(), true));
				cri.add(Restrictions.eq("TUM."+AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString(), false));
			}else if(listaMedicamentos == false){
				cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_PADRONIZACAO
						.toString(), false));
				cri.add(Restrictions.eq("TUM."+AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString(), false));
			}
		}
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}

		if(somenteMdtoDeUsoAmbulatorial != null){
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.PRESCRICAO_AMBULATORIAL.toString(), somenteMdtoDeUsoAmbulatorial));
		}
		if(listaMedicamentosAux == true){
			cri.add(Restrictions.eq("TUM."+AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString(), true));
			cri.add(Restrictions.in(VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString(), new Object[]{Boolean.TRUE,Boolean.FALSE}));
		}
		
		return cri;
	}
	
	public Long obterMedicamentosVOCount(Object strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes, Boolean somenteMdtoDeUsoAmbulatorial, Boolean listaMedicamentosAux) {
		
		DetachedCriteria cri = obterCriteriaMedicamentosVO(strPesquisa, listaMedicamentos, situacoes,somenteMdtoDeUsoAmbulatorial, listaMedicamentosAux);
		return this.executeCriteriaCount(cri);
	}

	/**
	 * Lista de Medicamentos Ativos, Padronizados, Que não exigem Observação e
	 * não são Antimicrobianos
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<MedicamentoVO> obterMedicamentosModeloBasicoVO(
			Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class);
		cri
				.setProjection(Projections
						.distinct(Projections
								.projectionList()
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.MAT_CODIGO
														.toString()),
										MedicamentoVO.Fields.MAT_CODIGO
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.MEDICAMENTO
														.toString()),
										MedicamentoVO.Fields.MEDICAMENTO
												.toString())

								.add(
										Projections
												.property(VAfaDescrMdto.Fields.DESCRICAO_EDITADA
														.toString()),
										MedicamentoVO.Fields.DESCRICAO_EDITADA
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.DESCRICAO_MAT
														.toString()),
										MedicamentoVO.Fields.DESCRICAO_MAT
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.CONCENTRACAO
														.toString()),
										MedicamentoVO.Fields.CONCENTRACAO
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.UMM_SEQ
														.toString()),
										MedicamentoVO.Fields.UMM_SEQ.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.TPR_SIGLA
														.toString()),
										MedicamentoVO.Fields.TPR_SIGLA
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.IND_PADRONIZACAO
														.toString()),
										MedicamentoVO.Fields.IND_PADRONIZACAO
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO
														.toString()),
										MedicamentoVO.Fields.IND_EXIGE_OBSERVACAO
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.FREQUENCIA_USUAL
														.toString()),
										MedicamentoVO.Fields.FREQUENCIA_USUAL
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.TUM_SIGLA
														.toString()),
										MedicamentoVO.Fields.TUM_SIGLA
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.IND_SITUACAO
														.toString()),
										MedicamentoVO.Fields.IND_SITUACAO
												.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.TFQ_SEQ
														.toString()),
										MedicamentoVO.Fields.TFQ_SEQ.toString())
								.add(
										Projections
												.property(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS
														.toString()
														+ "."
														+ MpmUnidadeMedidaMedica.Fields.DESCRICAO
																.toString()),
										MedicamentoVO.Fields.DESCRICAO_UNIDADE_MEDICA
												.toString())
								.add(
										Projections
												.property(TIPO
														+ AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA
																.toString()),
										MedicamentoVO.Fields.IND_EXIGE_JUSTIFICATIVA
												.toString())
								.add(
										Projections
												.property(TIPO
														+ AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA
																.toString()),
										MedicamentoVO.Fields.IND_EXIGE_DURACAO_SOLICITADA
												.toString())
								.add(
										Projections
												.property(TIPO
														+ AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO
																.toString()),
										MedicamentoVO.Fields.IND_ANTIMICROBIANO
												.toString())));

		cri.createAlias(VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(), "TIPO",
				JoinType.INNER_JOIN);

		// ativos
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		// padronizados
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_PADRONIZACAO
				.toString(), true));
		// não exige observação
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO
				.toString(), false));
		// não anti-microbianos
		cri.add(Restrictions.eq(TIPO
				+ AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString(), false));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}

		cri.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));

		cri.setResultTransformer(Transformers.aliasToBean(MedicamentoVO.class));

		List<MedicamentoVO> lista = this.executeCriteria(cri);

		return lista;
	}

	public Long obterMedicamentosModeloBasicoVOCount(Object strPesquisa) {

		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class);
		cri.createAlias(VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(), "TIPO",
				JoinType.INNER_JOIN);

		// ativos
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		// padronizados
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_PADRONIZACAO
				.toString(), true));
		// não exige observação
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO
				.toString(), false));
		// não anti-microbianos
		cri.add(Restrictions.eq(TIPO
				+ AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString(), false));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}

		return this.executeCriteriaCount(cri);
	}

	/**
	 * 
	 * @param strPesquisa
	 * @param listaMedicamentos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AfaMedicamento> obterMedicamentos(Object strPesquisa,
			Boolean listaMedicamentos) {
		List<AfaMedicamento> lista = new ArrayList<AfaMedicamento>();

		lista.addAll(this.obterMedicamentosUnion1(strPesquisa,
				listaMedicamentos));
		lista.addAll(this.obterMedicamentosUnion2(strPesquisa,
				listaMedicamentos));

		final ComparatorChain ordenacao = new ComparatorChain();
		// final BeanComparator ordenarIndPadronizacao = new
		// BeanComparator(AfaMedicamento.Fields.IND_PADRONIZACAO.toString(), new
		// ReverseComparator());
		final BeanComparator ordenarDescricao = new BeanComparator(
				AfaMedicamento.Fields.DESCRICAO.toString(), new NullComparator(
						false));
		// ordenacao.addComparator(ordenarIndPadronizacao);
		ordenacao.addComparator(ordenarDescricao);

		Collections.sort(lista, ordenacao);

		return lista;
	}

	/**
	 * 
	 * @param strPesquisa
	 * @param listaMedicamentos
	 * @return
	 */
	public Integer obterMedicamentosCount(Object strPesquisa,
			Boolean listaMedicamentos) {
		List<AfaMedicamento> lista = new ArrayList<AfaMedicamento>();

		lista.addAll(this.obterMedicamentosUnion1(strPesquisa,
				listaMedicamentos));
		lista.addAll(this.obterMedicamentosUnion2(strPesquisa,
				listaMedicamentos));

		return lista.size();
	}

	/**
	 * 
	 * @param strPesquisa
	 * @param listaMedicamentos
	 * @return
	 */
	private List<AfaMedicamento> obterMedicamentosUnion1(Object strPesquisa,
			Boolean listaMedicamentos) {

		DetachedCriteria cri = DetachedCriteria.forClass(AfaMedicamento.class);

		cri.createAlias(
				AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(),
				AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(),
				JoinType.LEFT_OUTER_JOIN);

		cri.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(),
				DominioSituacaoMedicamento.A));
		cri.add(Restrictions.eq(AfaMedicamento.Fields.IND_PADRONIZACAO
				.toString(), listaMedicamentos));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(
					AfaMedicamento.Fields.MAT_CODIGO.toString(), Integer.valueOf(
							(String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}

		// cri.addOrder(Order.desc(AfaMedicamento.Fields.IND_PADRONIZACAO.toString()));
		// cri.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));

		// cri.setResultTransformer(Transformers.aliasToBean(MedicamentoVO.class));

		List<AfaMedicamento> lista = this.executeCriteria(cri);

		return lista;
	}

	/**
	 * 
	 * @param strPesquisa
	 * @param listaMedicamentos
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private List<AfaMedicamento> obterMedicamentosUnion2(Object strPesquisa,
			Boolean listaMedicamentos) {

		DetachedCriteria cri = DetachedCriteria.forClass(AfaMedicamento.class);

		cri
				.setProjection(Projections
						.distinct(Projections
								.projectionList()
								.add(
										Projections
												.property(AfaMedicamento.Fields.MAT_CODIGO
														.toString()))
								.add(
										Projections
												.property(AfaMedicamento.Fields.SINONIMOS_MEDICAMENTO
														.toString()
														+ "."
														+ AfaSinonimoMedicamento.Fields.DESCRICAO
																.toString()),
										AfaMedicamento.Fields.DESCRICAO
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.IND_SITUACAO
														.toString()))
								.add(
										Projections
												.property(AfaMedicamento.Fields.HRIO_INICIO_ADM_SUGERIDO
														.toString()),
										AfaMedicamento.Fields.HRIO_INICIO_ADM_SUGERIDO
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.FREQUENCIA_USUAL
														.toString()),
										AfaMedicamento.Fields.FREQUENCIA_USUAL
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.IND_PADRONIZACAO
														.toString()))
								.add(
										Projections
												.property(AfaMedicamento.Fields.CONCENTRACAO
														.toString()),
										AfaMedicamento.Fields.CONCENTRACAO
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.IND_EXIGE_OBSERVACAO
														.toString()),
										AfaMedicamento.Fields.IND_EXIGE_OBSERVACAO
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS
														.toString()),
										AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.TPR_SIGLA
														.toString()),
										AfaMedicamento.Fields.TPR_SIGLA
												.toString())
								.add(
										Projections
												.property(AfaMedicamento.Fields.IND_PERMITE_DOSE_FRACIONADA
														.toString()),
										AfaMedicamento.Fields.IND_PERMITE_DOSE_FRACIONADA
												.toString())

						));

		cri.createAlias(AfaMedicamento.Fields.SINONIMOS_MEDICAMENTO.toString(),
				AfaMedicamento.Fields.SINONIMOS_MEDICAMENTO.toString(),
				JoinType.INNER_JOIN);
		cri.createAlias(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(),
				AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(),
				JoinType.INNER_JOIN);
		cri.createAlias(
				AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);

		cri.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(),
				DominioSituacaoMedicamento.A));
		cri.add(Restrictions.eq(AfaMedicamento.Fields.IND_PADRONIZACAO
				.toString(), listaMedicamentos));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(
					AfaMedicamento.Fields.MAT_CODIGO.toString(), Integer.valueOf(
							(String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(
					AfaMedicamento.Fields.SINONIMOS_MEDICAMENTO.toString()
							+ "."
							+ AfaSinonimoMedicamento.Fields.DESCRICAO
									.toString(), (String) strPesquisa,
					MatchMode.ANYWHERE));
		}

		// cri.addOrder(Order.desc(AfaMedicamento.Fields.IND_PADRONIZACAO.toString()));
		// cri.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));

		cri
				.setResultTransformer(Transformers
						.aliasToBean(AfaMedicamento.class));

		List<AfaMedicamento> lista = this.executeCriteria(cri);

		return lista;
	}

	public List<AfaMedicamento> pesquisarMedicamentos(String strObject) {
		if (CoreUtil.isNumeroInteger(strObject)) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(AfaMedicamento.class);

			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO
					.toString(), Integer.valueOf(strObject)));

			List<AfaMedicamento> medicamentos = this.executeCriteria(criteria);
			if (medicamentos != null && !medicamentos.isEmpty()) {
				return medicamentos;
			}
		}
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaMedicamento.class);
		
		if (StringUtils.isNotBlank(strObject)) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO
					.toString(), strObject, MatchMode.ANYWHERE));
		}
		
		return this.executeCriteria(criteria, 0, 20, null, false);
	}

	public Long pesquisarMedicamentosCount(String strObject) {
		if (CoreUtil.isNumeroInteger(strObject)) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(AfaMedicamento.class);

			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO
					.toString(), Integer.valueOf(strObject)));

			Long count = this.executeCriteriaCount(criteria);
			if (count != 0) {
				return count;
			}
		}
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaMedicamento.class);
		
		if (StringUtils.isNotBlank(strObject)) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO
					.toString(), strObject, MatchMode.ANYWHERE));
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = this.getMedicamentoCriteria(medicamento);		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarMedicamentosCount(AfaMedicamento medicamento) {
			
		DetachedCriteria criteria = this.getMedicamentoCriteria(medicamento);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getMedicamentoCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria
			.forClass(AfaMedicamento.class);
		
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "mpmUnidadeMedidaMedicas"	, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento"				, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "afaTipoUsoMdtos"			, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaMedicamento.Fields.RAP_SERVIDORES.toString(), "rapServidores"					, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rapServidores."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica"		, JoinType.LEFT_OUTER_JOIN);

		if (medicamento.getMatCodigo() != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO
					.toString(), medicamento.getMatCodigo()));
		} 
		
		if (medicamento.getDescricao() != null && !"".equals(medicamento.getDescricao())) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO
					.toString(), medicamento.getDescricao().trim(), MatchMode.ANYWHERE));
		}

		if (medicamento.getAfaTipoUsoMdtos() != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO
					.toString(), medicamento.getAfaTipoUsoMdtos()));
		}
		
		if (medicamento.getIndRevisaoCadastro() != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_REVISAO_CADASTRO
					.toString(), medicamento.getIndRevisaoCadastro()));
		}
		
		if (medicamento.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO
					.toString(), medicamento.getIndSituacao()));
		}
		
		return criteria;
	}
	
	/**
	 * Lista de Medicamentos
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(Object strPesquisa) {

		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class);

		// ativos
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(
					VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(),
					(String) strPesquisa, MatchMode.ANYWHERE));
		}

		cri.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));

		List<VAfaDescrMdto> lista = this.executeCriteria(cri);

		return lista;
	}

	public List<VAfaDescrMdto> obterMedicamentosEnfermeiroObstetraReceitaVO(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class);

		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(
					VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(),
					(String) strPesquisa, MatchMode.ANYWHERE));
		}

		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.PRESCRICAO_ENFERMEIRO_OBSTETRA.toString(),  true));
		cri.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));
		List<VAfaDescrMdto> lista = this.executeCriteria(cri);

		return lista;

	}
	
	public Long obterMedicamentosReceitaVOCount(Object strPesquisa) {

		DetachedCriteria cri = DetachedCriteria.forClass(VAfaDescrMdto.class);

		// ativos
		cri.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(),
					Integer.valueOf((String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(
					VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(),
					(String) strPesquisa, MatchMode.ANYWHERE));
		}

		return this.executeCriteriaCount(cri);
	}
	
	/**
	 * Pesquisa Medicamentos associados a um determinado tipo de apresentação
	 * @param sigla
	 * @return
	 */
	public List<AfaMedicamento> pesquisarMedicamentosPorTipoApresentacao(String sigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.TPR_SIGLA.toString(), sigla));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Número de medicamentos ativos de determinado tipo de apresentação.
	 * 
	 * @param tprSigla sigla do tipo de apresentação
	 * @return
	 */
	public Long obterMedicamentosAtivosPorTipoApresentacaoCount(String tprSigla) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);

		// ativos
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		// sigla
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.TPR_SIGLA.toString(), tprSigla));
		
		Long result = this.executeCriteriaCount(criteria);

		return result != null ? result : 0l;
	}
	
	public List<AfaMedicamento> pesquisarTodosMedicamentos(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "umm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaMedicamento.Fields.TPR.toString(), "tpr", JoinType.LEFT_OUTER_JOIN);
		processarCriteriaPesquisarTodosMedicamentos(criteria, strPesquisa);
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}
	
	/** Obtém criteria para consulta de ScoMaterial para Suggestion Box.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link List<AfaMedicamento>} */
	public List<AfaMedicamento> pesquisarMedicamentosSB1(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "umm", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		processarCriteriaPesquisarTodosMedicamentos(criteria, strPesquisa);
		return this.executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}
	
	/** Obtém criteria para consulta de ScoMaterial para Suggestion Box.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link Long} */
	public Long pesquisarMedicamentosSB1Count(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "umm", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		processarCriteriaPesquisarTodosMedicamentos(criteria, strPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	private void processarCriteriaPesquisarTodosMedicamentos(DetachedCriteria cri,
			Object strPesquisa) {
		
		Disjunction or = Restrictions.disjunction();
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			or.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			or.add(Restrictions.or(Restrictions.eq(
					AfaMedicamento.Fields.MAT_CODIGO.toString(), Integer.valueOf(
							(String) strPesquisa))));
		}
		cri.add(or);
	}

	public Long pesquisarTodosMedicamentosCount(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		processarCriteriaPesquisarTodosMedicamentos(criteria, strPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	
	
	public List<AfaMedicamento> pesquisarMedicamentosAtivos(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		processarCriteriaPesquisarTodosMedicamentos(criteria, objPesquisa);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarMedicamentosAtivosCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		processarCriteriaPesquisarTodosMedicamentos(criteria, objPesquisa);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteriaPesquisaMedicamentos(String parametro) {
		DetachedCriteria criteriaMedicamento = DetachedCriteria
				.forClass(AfaMedicamento.class);

		criteriaMedicamento.add(Restrictions.eq(
				AfaMedicamento.Fields.IND_PADRONIZACAO.toString(), true));

		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				Integer integerPesquisa = Integer.valueOf((String) parametro);
				criteriaMedicamento.add(Restrictions.eq(
						AfaMedicamento.Fields.MAT_CODIGO.toString(),
						integerPesquisa));
			} else {
				criteriaMedicamento.add(Restrictions.like(
						AfaMedicamento.Fields.DESCRICAO.toString(), parametro,
						MatchMode.ANYWHERE));
			}
		}

//		criteriaMedicamento.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO
//				.toString()));
		return criteriaMedicamento;
	}
	
	public List<AfaMedicamento> pesquisarMedicamentosPorCodigoDescricao(
			String parametro) {
		DetachedCriteria criteriaMedicamento = criarCriteriaPesquisaMedicamentos(parametro);

		return executeCriteria(criteriaMedicamento);

	}

	public Long pesquisarMedicamentosCountPorCodigoDescricao(String parametro) {
		DetachedCriteria criteriaMedicamento = criarCriteriaPesquisaMedicamentos(parametro);

		return executeCriteriaCount(criteriaMedicamento);

	}
	
	//#5388
	public List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica) {
		
		DetachedCriteria criteria = obterCriteriaPesqMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica);
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		List<AfaMedicamento> lista = executeCriteria(criteria);
		
		return lista;
		
	}
	
	// 5388
	public List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa) {
		DetachedCriteria criteria = getCriteriaPesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica, strPesquisa);
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		List<AfaMedicamento> lista = executeCriteria(criteria);
		
		return lista;
	}
	
	public Long pesquisarMdtosParaDispensacaoPorItemPrescritoCount(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa) {
		DetachedCriteria criteria = getCriteriaPesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica, strPesquisa);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaPesquisarMdtosParaDispensacaoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa) {
		DetachedCriteria criteria = obterCriteriaPesqMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica);
			if(strPesquisa != null && !String.valueOf(strPesquisa).isEmpty()){
				if(CoreUtil.isNumeroInteger(StringUtils.trimToNull(strPesquisa.toString()))){
					criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), Integer.valueOf(StringUtils.trimToNull(strPesquisa.toString()))));
				}else{
					criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
				}
			}
		return criteria;
	}

	//#5388
	/**
	 * Retorna criteria para consulta por itemPrescrito em PrescricaoMedica
	 * Utilizado alem desta DAO, tambem em AfaDispensacaoMdtoDAO como Subquery
	 */
	public DetachedCriteria obterCriteriaPesqMdtosParaDispensacaoPorItemPrescrito(MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class, "MED");
		
		//***SUBQUERIES***
		//MedicamentoEquivalente
		DetachedCriteria criteriaMedEq = DetachedCriteria.forClass(AfaMedicamentoEquivalente.class, "MEQ");
		criteriaMedEq.setProjection(Projections.projectionList().add(Projections.property(AfaMedicamentoEquivalente.Fields.MED_MAT_CODIGO_EQUIVALENTE.toString())));
		criteriaMedEq.add(Restrictions.eq(AfaMedicamentoEquivalente.Fields.MED_MAT_CODIGO.toString(), itemPrescrito.getMedicamento().getMatCodigo()));
		criteriaMedEq.add(Restrictions.eq(AfaMedicamentoEquivalente.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteriaMedEq.createAlias(AfaMedicamentoEquivalente.Fields.MEDICAMENTO.toString(), "MED1");
		criteriaMedEq.add(Restrictions.eq("MED1" + "." + AfaMedicamento.Fields.IND_SITUACAO.toString() , DominioSituacaoMedicamento.A));
		
		//AfaDispensacaoMdtos
		DetachedCriteria criteriaDispMed = DetachedCriteria.forClass(AfaDispensacaoMdtos.class , "DSM1");
		criteriaDispMed.setProjection(Projections.projectionList().add(Projections.property(AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString())));
		criteriaDispMed.add(Restrictions.conjunction()
				.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString(), itemPrescrito))
				.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), prescricaoMedica)));
	
		//CRITERIA PRINCIPAL
//		criteria.setProjection(Projections.projectionList()
//				.add(Projections.property(VMpmMdtosDescr.Fields.MAT_CODIGO.toString()))
//				.add(Projections.property(VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()))
//				.add(Projections.property(VMpmMdtosDescr.Fields.TPR_SIGLA.toString()))
//		);
		Criterion criterionVMpmMdtosDescr1 = Subqueries.propertyIn(AfaMedicamento.Fields.MAT_CODIGO.toString(), criteriaMedEq);
		Criterion criterionVMpmMdtosDescr2 = Subqueries.propertyIn(AfaMedicamento.Fields.MAT_CODIGO.toString(), criteriaDispMed);
		Criterion criterionVMpmMdtosDescr3 = Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), itemPrescrito.getMedicamento().getMatCodigo());
	
		criteria
			.add(Restrictions.disjunction()
					.add(Restrictions.disjunction().add(criterionVMpmMdtosDescr1).add(criterionVMpmMdtosDescr2))
					.add(criterionVMpmMdtosDescr3));
		
		
	
		return criteria;
	}
	
	/**
	 * Lista dos os medicamento da tabela na sua forma resumida para geração do relatório
	 * @return lista
	 */
	//Estória # 5697
	public List<MedicamentoVO> pesquisarTodosMedicamentos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria
		.setProjection(Projections
				.distinct(Projections
						.projectionList()
						.add(
								Projections
										.property(AfaMedicamento.Fields.MAT_CODIGO
												.toString()),
								MedicamentoVO.Fields.MAT_CODIGO
										.toString())
						.add(
								Projections
										.property(AfaMedicamento.Fields.DESCRICAO
												.toString()),
								MedicamentoVO.Fields.DESCRICAO
										.toString())
						
						.add(
								Projections
										.property(AfaMedicamento.Fields.CONCENTRACAO
												.toString()),
								MedicamentoVO.Fields.CONCENTRACAO
										.toString())
						.add(
								Projections
										.property(AfaMedicamento.Fields.TPR_SIGLA
												.toString()),
								MedicamentoVO.Fields.TPR_SIGLA
										.toString())
						.add(
								Projections
										.property(AfaMedicamento.Fields.TUM_SIGLA
												.toString()),
								MedicamentoVO.Fields.TUM_SIGLA
										.toString())
						.add(
								Projections
										.property(AfaMedicamento.Fields.IND_SITUACAO
												.toString()),
								MedicamentoVO.Fields.IND_SITUACAO
										.toString())
						.add(
								Projections
										.property(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS
												.toString()
												+ "."
												+ MpmUnidadeMedidaMedica.Fields.DESCRICAO
														.toString()),
								MedicamentoVO.Fields.DESCRICAO_UNIDADE_MEDICA
										.toString())
						
						));

	criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
			AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
			JoinType.LEFT_OUTER_JOIN);
	
	
	criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
	
	criteria.setResultTransformer(Transformers.aliasToBean(MedicamentoVO.class));
	
	List<MedicamentoVO> lista = this.executeCriteria(criteria);

	return lista;
	}
	
	/**
	 * Lista os tipos de uso de medicamentos na forma resumida para uso como legenda na geração do relatório
	 * @return lista
	 */
	//Estória # 5697
	public List<TipoUsoMedicamentoVO> pesquisarTipoUsoMedicamento() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoUsoMdto.class);
		criteria
		.setProjection(Projections
				.distinct(Projections
						.projectionList()
						.add(
								Projections
										.property(AfaTipoUsoMdto.Fields.SIGLA
												.toString()),
								TipoUsoMedicamentoVO.Fields.SIGLA
										.toString())
						.add(
								Projections
										.property(AfaTipoUsoMdto.Fields.DESCRICAO
												.toString()),
								TipoUsoMedicamentoVO.Fields.DESCRICAO
										.toString())
						
						.add(
								Projections
										.property(AfaTipoUsoMdto.Fields.IND_SITUACAO
												.toString()),
								TipoUsoMedicamentoVO.Fields.IND_SITUACAO
										.toString())
								
						));


	criteria.addOrder(Order.asc(AfaTipoUsoMdto.Fields.SIGLA.toString()));
	
	criteria.setResultTransformer(Transformers.aliasToBean(TipoUsoMedicamentoVO.class));
	
	List<TipoUsoMedicamentoVO> lista = this.executeCriteria(criteria);

	return lista;
	}

	
	public List<AfaMedicamento> pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(final Short unfSeq, final String filtro, boolean firstQuery) {
		final DetachedCriteria criteria = createCriteriaPesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(unfSeq, filtro, firstQuery);
		return executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}
	
	public Long pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(final Short unfSeq, final String filtro, boolean firstQuery) {
		final DetachedCriteria criteria = createCriteriaPesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(unfSeq, filtro, firstQuery);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createCriteriaPesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(
			final Short unfSeq, final String filtro, boolean firstQuery) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class,"LMED");
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("LMED."+AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));

		if(firstQuery){
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtMedicUsual.class,"PMU");
			subCriteria.setProjection(Projections.property("PMU."+PdtMedicUsual.Fields.ID_UNF_SEQ.toString()));
			subCriteria.add(Restrictions.eq("PMU."+PdtMedicUsual.Fields.ID_UNF_SEQ.toString(), unfSeq));
			subCriteria.add(Restrictions.eqProperty("PMU."+PdtMedicUsual.Fields.ID_MED_MAT_CODIGO.toString(), "LMED."+AfaMedicamento.Fields.MAT_CODIGO.toString()));
	
			criteria.add(Subqueries.exists(subCriteria));
		}		
		
		if(filtro != null){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq("LMED."+AfaMedicamento.Fields.UMM_SEQ.toString(), Integer.valueOf(filtro)));
				
			} else {
				criteria.add(Restrictions.ilike("LMED."+AfaMedicamento.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	// #4961 (Manter medicamentos x cuidados)
	// C1
	public List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaMatCodigoOuDescricao(parametro);
		return executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}

	// #4961 (Manter medicamentos x cuidados)
	// C1 - Count
	public Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaMatCodigoOuDescricao(parametro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaParaMatCodigoOuDescricao(String parametro) {
		String matCodigoOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		if (StringUtils.isNotEmpty(matCodigoOuDescricao)) {
			Integer matCodigo = -1;
			if (CoreUtil.isNumeroInteger(matCodigoOuDescricao)) {
				matCodigo = Integer.parseInt(matCodigoOuDescricao);
			}
			if (matCodigo != -1) {
				criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
			} else {
				criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), matCodigoOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	// #4961 (Manter medicamentos x cuidados)
	// C2
	public List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaParaListagemMedicamentosCuidados(matCodigo, indSituacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	// #4961 (Manter medicamentos x cuidados)
	// C2 - Count
	public Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento indSituacao) {
		DetachedCriteria criteria = montarCriteriaParaListagemMedicamentosCuidados(matCodigo, indSituacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		if (matCodigo != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}

	public AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.createAlias(AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "tum", JoinType.LEFT_OUTER_JOIN);
		if (matCodigo != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		return (AfaMedicamento) executeCriteriaUniqueResult(criteria);
	}
	
	public AfaMedicamento obterMedicamentoDiluentePorCodigoMedicamento(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		if (matCodigo != null) {
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		return (AfaMedicamento) executeCriteriaUniqueResult(criteria);
	}

	public AfaMedicamento obterMedicamentoComUnidadeMedidaMedica(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "umm", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), codigo));
		return (AfaMedicamento) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Criteria para listar os medicamentos ativos
	 * 
	 * Web Service #36672
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaMedicamentoAtivoPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Listar os medicamentos ativos
	 * 
	 * Web Service #36672
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	public List<AfaMedicamento> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro, Integer maxResults) {
		DetachedCriteria criteria = this.obterCriteriaMedicamentoAtivoPorCodigoOuDescricao(parametro);
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Count de medicamentos ativos
	 * 
	 * Web Service #36672
	 * 
	 * @param parametro
	 * @return
	 */
	public Long pesquisarMedicamentoAtivoPorCodigoOuDescricaoCount(String parametro) {
		DetachedCriteria criteria = this.obterCriteriaMedicamentoAtivoPorCodigoOuDescricao(parametro);
		return super.executeCriteriaCount(criteria);
	}

	/**
	 * Listar os medicamentos por código
	 * 
	 * Web Service #36675
	 * 
	 * @param matCodigos
	 * @return
	 */
	public List<AfaMedicamento> pesquisarMedicamentoPorCodigos(List<Integer> matCodigos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		criteria.add(Restrictions.in(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigos));
		return super.executeCriteria(criteria);
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento, DominioSituacao situacaoTipoApresentacaoMedicamento,
			String ... orders) {
		DetachedCriteria criteria = getCriteriaPesquisarMedicamentos(
				objPesquisa, situacaoMedicamento,
				joinTipoApresentacaoMedicamento,
				situacaoTipoApresentacaoMedicamento);
		
		if(orders != null){
			for(String order:orders){
				criteria.addOrder(Order.asc(order));
			}
		}
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarMedicamentosCount(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento, DominioSituacao situacaoTipoApresentacaoMedicamento) {
		DetachedCriteria criteria = getCriteriaPesquisarMedicamentos(
				objPesquisa, situacaoMedicamento,
				joinTipoApresentacaoMedicamento,
				situacaoTipoApresentacaoMedicamento);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarMedicamentos(
			Object objPesquisa, DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento,
			DominioSituacao situacaoTipoApresentacaoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		processarCriteriaPesquisarTodosMedicamentos(criteria, objPesquisa);
		
		if(situacaoMedicamento != null){
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), situacaoMedicamento));
		}
		
		if(joinTipoApresentacaoMedicamento){
			criteria.createAlias(AfaMedicamento.Fields.TPR.toString(), "tipoApresentMdto");
			if(situacaoTipoApresentacaoMedicamento != null){
				criteria.add(
						Restrictions.eq
						("tipoApresentMdto."+AfaTipoApresentacaoMedicamento.Fields.SITUACAO.toString()
								, situacaoTipoApresentacaoMedicamento));
			}
		}
		return criteria;
	}	
	
	public Long listarMedicamentosSuggestionCount(Object objPesquisa){
		String strPesquisa = (String) objPesquisa;	
		return executeCriteriaCount(obterCriteriaMedicamentoAtivoPorCodigoOuDescricao(strPesquisa));
	}
	
	public List<AfaMedicamento> listarMedicamentosSuggestion(Object objPesquisa){
		String strPesquisa = (String) objPesquisa;	
		return executeCriteria(obterCriteriaMedicamentoAtivoPorCodigoOuDescricao(strPesquisa), 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #44181 
	 * @author marcelo.deus
	 */
	public List<AfaMedicamento> obterListaMedicamentoTubercolostaticos(){

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class, "MED");
		criteria.createAlias("MED." + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "TUM");
		criteria.add(Restrictions.eq("MED." + AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A))
				.add(Restrictions.eq("TUM." + AfaTipoUsoMdto.Fields.SIGLA.toString(), "T"));
		return executeCriteria(criteria);
	}
	
	public List<AfaMedicamento> obterMedicamentosParaInclusaoLocalDispensacao(List<Integer> listaMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class, "med");
		
		if(listaMateriais != null && !listaMateriais.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in("med."+AfaMedicamento.Fields.MAT_CODIGO.toString(),
					listaMateriais)));
		}
		
		return executeCriteria(criteria);
	}
	
	public Long obterMedicamentosAtivosCount(String objPesquisa) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);		
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		
		if (objPesquisa != null) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE));
		}
		
		return executeCriteriaCount(criteria);			
	}
		
	public List<AfaMedicamento> obterMedicamentosAtivos(String objPesquisa) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);		
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		
		if (objPesquisa != null) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE));
		}
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(AfaMedicamento.Fields.MAT_CODIGO.toString()), AfaMedicamento.Fields.MAT_CODIGO.toString())
				.add(Projections.property(AfaMedicamento.Fields.DESCRICAO.toString()), AfaMedicamento.Fields.DESCRICAO.toString());
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(AfaMedicamento.class));
		
		return executeCriteria(criteria, 0, 100, AfaMedicamento.Fields.DESCRICAO.toString(), true);	
	}
	
	
	private DetachedCriteria pesquisaMedicamentos(String objPesquisa, Boolean  padronizacao, Integer matCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamento.class);
		
		criteria.createAlias(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM" , JoinType.LEFT_OUTER_JOIN);
		
		if (objPesquisa != null) {
			criteria.add(Restrictions.ilike(AfaMedicamento.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE));
		}
		
		if(matCodigo != null){
			criteria.add(Restrictions.eq(AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AfaMedicamento.Fields.IND_PADRONIZACAO.toString(), padronizacao));
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(AfaMedicamento.Fields.MAT_CODIGO.toString()), MedicamentosVO.Fields.MED_MAT_CODIGO.toString())
				.add(Projections.property(AfaMedicamento.Fields.DESCRICAO.toString()), MedicamentosVO.Fields.MEDICAMENTO_DESCRICAO.toString())
				.add(Projections.property(AfaMedicamento.Fields.IND_EXIGE_OBSERVACAO.toString()), MedicamentosVO.Fields.IND_EXIGE_OBSERVACAO.toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.SEQ.toString()), MedicamentosVO.Fields.MEDICAMENTO_UMM_SEQ.toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), MedicamentosVO.Fields.UMM_DESCRICAO.toString());
		
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(MedicamentosVO.class));
		
		return criteria;
	}
	
	public List<MedicamentosVO> pesquisarMedicamento(String objPesquisa, Boolean  padronizacao){
		DetachedCriteria criteria = pesquisaMedicamentos(objPesquisa, padronizacao, null);
		criteria.addOrder(Order.asc(AfaMedicamento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarMedicamentoCount(String objPesquisa, Boolean padronizacao){
		DetachedCriteria criteria = pesquisaMedicamentos(objPesquisa, padronizacao, null);
		return executeCriteriaCount(criteria);
	}
	
	public MedicamentosVO obterMedicamento(String objPesquisa, Boolean  padronizacao, Integer matCodigo){
		DetachedCriteria criteria = pesquisaMedicamentos(objPesquisa, padronizacao, matCodigo);
		return (MedicamentosVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AfaMedicamento> pesquisaDiluente(Integer medMatCodigo) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDiluentes.class);		
		
		criteria.createAlias(AfaDiluentes.Fields.MED_MAT_CODIGO.toString(), "MED");
		criteria.add(Restrictions.eq(AfaDiluentes.Fields.MED_MAT_CODIGO_DILUIDO.toString(), medMatCodigo));
		criteria.add(Restrictions.eq(AfaDiluentes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("MED." + AfaMedicamento.Fields.MAT_CODIGO.toString()), AfaMedicamento.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MED." + AfaMedicamento.Fields.DESCRICAO.toString()), AfaMedicamento.Fields.DESCRICAO.toString())));
		
		criteria.addOrder(Order.desc("MED." + AfaMedicamento.Fields.MAT_CODIGO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaMedicamento.class));
			
		return executeCriteria(criteria);	
	}
		
}