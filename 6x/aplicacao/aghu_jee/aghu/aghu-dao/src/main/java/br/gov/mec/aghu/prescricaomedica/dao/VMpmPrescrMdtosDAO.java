package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdtoId;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdtoJn;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmTipoParecerUsoMdto;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.VMpmPrescrMdtos;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensa2VO;
import br.gov.mec.aghu.prescricaomedica.vo.DetalhesParecerMedicamentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;

public class VMpmPrescrMdtosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmPrescrMdtos> {
	
	private static final long serialVersionUID = 8751927075976120829L;

	/**
	 * @param imePmdAtdSeq 	afa_dispensacao_mdtos.ime_pmd_atd_seq
	 * @param vPmdSeq		afa_dispensacao_mdtos.ime_pmd_seq
	 * @param imeMedMatCodigo		afa_dispensacao_mdtos.ime_med_mat_codigo
	 * @param imeSeqp			afa_dispensacao_mdtos.ime_seqp
	 * @return
	 */
	public Object[] obtemPrescMdto(Integer imePmdAtdSeq, Long vPmdSeq,
			Integer imeMedMatCodigo, Short imeSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class);
		
		ProjectionList projection = Projections.projectionList().add(Projections.property(VMpmPrescrMdtos.Fields.IND_ITEM_RECOMENDADO_ALTA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.ATD_SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.TP_FREQUENCIA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.TP_VELOCID.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_SE_NECESSARIO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_PENDENTE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.FREQUENCIA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.HR_INICIO_ADM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DTHR_INICIO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DTHR_FIM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.GOTEJO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DURACAO_TRAT_APROV.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DTHR_INICIO_TRATAMENTO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.QTDE_CORRER.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.OBSERVACAO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.MAT_CODIGO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.SEQ_ITEM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DOSE_CALCULADA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_USO_MDTO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DESCR_COMPLEMENTAR.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.FDS_DOSE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.DOSE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.QTDE_CALC24H.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_SOLUCAO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.UNID_DOSE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.FREQ_EDIT.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.JUM_SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_MDTO_AGUARDA_ENTREGA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_USO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.QTDE_MGKG.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.QTDE_MG_SUPERF_CORPORAL.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.TIPO_CALC_DOSE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.OBSERVACAO_ITEM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.CRIADO_EM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_CONTROLADO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_ORIGEM_JUSTIF.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.PMD_ATD_SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.PMD_SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.ALTERADO_EM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.SER_MATRIVULA_VALIDA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.SER_VIN_CODIGO_VALIDA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_ANTIMICROBIANO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_QUIMIOTERAPICO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.MED_PRCR_DESC_COMPLETA.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.UNID_HORAS_CORRER.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.VOLUME_DILUENTE_ML.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.MED_MAT_CODIGO_DILUENTE.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.QTDE_PARAM_CALCULO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.BASE_PARAM_CALCULO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.UMM_SEQ_CALCULO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.SURACAO_PARAM_CALCULO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.UNID_DURACAO_CALCULO.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.PCA_ATD_SEQ.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.PCA_CRIADO_EM.toString()))
									.add(Projections.property(VMpmPrescrMdtos.Fields.IND_BOMBA_INFUSAO.toString()))
									;
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq(VMpmPrescrMdtos.Fields.ATD_SEQ.toString(), imePmdAtdSeq));
		criteria.add(Restrictions.eq(VMpmPrescrMdtos.Fields.SEQ.toString(), vPmdSeq));
		criteria.add(Restrictions.eq(VMpmPrescrMdtos.Fields.MAT_CODIGO.toString(), imeMedMatCodigo));
		criteria.add(Restrictions.eq(VMpmPrescrMdtos.Fields.SEQ_ITEM.toString(), imeSeqp));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca por todos os Pareceres pendentes relacionados ao código de Atendimento informado.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * 
	 * @return Lista de Pareceres pendentes
	 */
	public List<ParecerPendenteVO> listarPareceresPendentesPorCodigoAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class, "VPM");

		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MED_PRCR_DESC_COMPLETA), ParecerPendenteVO.Fields.MEDICAMENTO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DOSE), ParecerPendenteVO.Fields.DOSE.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.FREQ_EDIT), ParecerPendenteVO.Fields.APRAZAMENTO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO), ParecerPendenteVO.Fields.VIA_ADMINISTRACAO.toString());
		projection.add(Projections.property("TPM." + MpmTipoParecerUsoMdto.Fields.DESCRICAO), ParecerPendenteVO.Fields.DESCRICAO.toString());
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.SEQ), ParecerPendenteVO.Fields.SEQ.toString());

		criteria.setProjection(projection);
		
		criteria.createAlias("VPM." + VMpmPrescrMdtos.Fields.ITEM_PRESC_PARECER_MDTO.toString(), "IPP");
		criteria.createAlias("IPP." + MpmItemPrescParecerMdto.Fields.MPM_PARECER_USO_MDTOS.toString(), "PUM");
		criteria.createAlias("PUM." + MpmParecerUsoMdto.Fields.MPM_TIPO_PARECER_USO_MDTOS.toString(), "TPM");
		criteria.createAlias("VPM." + VMpmPrescrMdtos.Fields.PRESCRICAO_MDTO.toString(), "PRM");
		criteria.createAlias("PRM." + MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS.toString(), "IPM");

		criteria.add(Restrictions.eq("PUM." + MpmParecerUsoMdto.Fields.IND_PARECER_VERIFICADO.toString(), DominioSimNao.N));
		criteria.add(Restrictions.eq("TPM." + MpmTipoParecerUsoMdto.Fields.IND_MOSTRA_PARECER_AUTOMATICO.toString(), DominioSimNao.S.toString()));
		criteria.add(Restrictions.eq("IPM." + MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeq));

		criteria.addOrder(Order.desc("PUM." + MpmParecerUsoMdto.Fields.DTHR_PARECER.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ParecerPendenteVO.class));

		return executeCriteria(criteria);
	}
	
	public List<LocalDispensa2VO> listarPrescricaoMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento) {
		DetachedCriteria subquery1 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "B");
		subquery1.setProjection(Projections.projectionList()
							.add(Projections.property("B." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString()))
							.add(Projections.property("B." + MpmPrescricaoMdto.Fields.SEQ.toString())));
		subquery1.add(Restrictions.eq("B." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		subquery1.add(Restrictions.eqProperty("B." + MpmPrescricaoMdto.Fields.SEQ.toString(), "VPM." + VMpmPrescrMdtos.Fields.PMD_SEQ.toString()));
		subquery1.add(Restrictions.eq("B." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.FALSE));
		
		DetachedCriteria subquery2 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "A");
		subquery2.setProjection(Projections.projectionList()
							.add(Projections.property("A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString()))
							.add(Projections.property("A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString())));
		subquery2.add(Restrictions.eq("A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString(), atdSeq));
		subquery2.add(Restrictions.eqProperty("A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString(), "VPM." + MpmPrescricaoMdto.Fields.SEQ.toString()));
		subquery2.add(Restrictions.eq("A." + MpmPrescricaoMdto.Fields.PENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		subquery2.add(Restrictions.eq("A." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.FALSE));
		subquery2.add(
			Restrictions.or(
				Restrictions.ge("A." + MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), dthrMovimento),
				Restrictions.ge("A." + MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), dthrMovimento)
			)	
		);
		subquery2.add(
			Restrictions.lt("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrFim)	
		);
		subquery2.add(
			Restrictions.or(
				Restrictions.isNull("A." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.gt("A." + MpmPrescricaoMdto.Fields.DTHR_FIM, dthrInicio),
				Restrictions.and(
				    Restrictions.eqProperty("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), "A." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
					Restrictions.gt("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrInicio)
				)
			)	
		);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class, "VPM");
		criteria.setProjection(Projections.projectionList()
						.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.ATD_SEQ.toString()), LocalDispensa2VO.Fields.ATD_SEQ.toString())
						.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MAT_CODIGO.toString()), LocalDispensa2VO.Fields.MED_MAT_CODIGO.toString())
						.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DOSE.toString()), LocalDispensa2VO.Fields.DOSE.toString())
						.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.FDS_DOSE.toString()), LocalDispensa2VO.Fields.FDSSEQ.toString())
		);
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.IND_PENDENTE.toString(), DominioSimNao.N.toString()));
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.IND_SOLUCAO.toString(), DominioSimNao.N.toString()));
		criteria.add(
				Restrictions.or(
						Restrictions.ge("VPM." + VMpmPrescrMdtos.Fields.CRIADO_EM.toString(), dthrMovimento),
						Restrictions.ge("VPM." + VMpmPrescrMdtos.Fields.ALTERADO_EM.toString(), dthrMovimento)
				)
		);
		criteria.add(Restrictions.lt("VPM." + VMpmPrescrMdtos.Fields.DTHR_INICIO.toString(), dthrFim));
		criteria.add(
				Restrictions.or(
						Restrictions.isNull("VPM." + VMpmPrescrMdtos.Fields.DTHR_FIM.toString()),
						Restrictions.gt("VPM." + VMpmPrescrMdtos.Fields.DTHR_FIM.toString(), dthrInicio),
						Restrictions.or(
								Restrictions.and(
										Restrictions.eqProperty("VPM." + VMpmPrescrMdtos.Fields.DTHR_INICIO.toString(), "VPM." + VMpmPrescrMdtos.Fields.DTHR_FIM.toString()),
										Restrictions.ge("VPM." + VMpmPrescrMdtos.Fields.DTHR_INICIO.toString(), dthrInicio)
								)
						)
				)
		);
		criteria.add(
			Restrictions.or(
				Restrictions.and(
					Restrictions.isNull("VPM." + VMpmPrescrMdtos.Fields.PMD_ATD_SEQ.toString()),
					Restrictions.isNull("VPM." + VMpmPrescrMdtos.Fields.PMD_SEQ.toString())
				),
				Restrictions.and(
					Restrictions.isNotNull("VPM." + VMpmPrescrMdtos.Fields.PMD_ATD_SEQ.toString()),
					Restrictions.isNotNull("VPM." + VMpmPrescrMdtos.Fields.PMD_SEQ.toString()),
					Subqueries.propertiesIn(new String[] {
							"VPM." + VMpmPrescrMdtos.Fields.PMD_ATD_SEQ.toString(), 
							"VPM." + VMpmPrescrMdtos.Fields.PMD_SEQ.toString()}, subquery1)
				)
			)
		);
		criteria.add( 
			Subqueries.propertiesNotIn(new String[] {
				"VPM." + VMpmPrescrMdtos.Fields.ATD_SEQ.toString(), 
				"VPM." + VMpmPrescrMdtos.Fields.SEQ.toString()}, subquery2)
		);
		criteria.setResultTransformer(Transformers.aliasToBean(LocalDispensa2VO.class));
		return executeCriteria(criteria);
	}		

	public Object[] obterComplementoAvaliacaoMedicamento(Integer pMedMatCodigo, Integer atdSeq, Integer jumSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class, "VPM");

		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.ATD_SEQ.toString()), VMpmPrescrMdtos.Fields.ATD_SEQ.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.SEQ.toString()), VMpmPrescrMdtos.Fields.SEQ.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MAT_CODIGO.toString()), VMpmPrescrMdtos.Fields.MAT_CODIGO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.SEQ_ITEM.toString()), VMpmPrescrMdtos.Fields.SEQ_ITEM.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString()), VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.OBSERVACAO_ITEM.toString()), VMpmPrescrMdtos.Fields.OBSERVACAO_ITEM.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString()), VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString()), VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.FREQ_EDIT.toString()), VMpmPrescrMdtos.Fields.FREQ_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.IND_USO_MDTO.toString()), VMpmPrescrMdtos.Fields.IND_USO_MDTO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DOSE_CALCULADA.toString()), VMpmPrescrMdtos.Fields.DOSE_CALCULADA.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString()), VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DURACAO_TRAT_APROV.toString()), VMpmPrescrMdtos.Fields.DURACAO_TRAT_APROV.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DTHR_INICIO_TRATAMENTO.toString()), VMpmPrescrMdtos.Fields.DTHR_INICIO_TRATAMENTO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.JUM_SEQ.toString()), VMpmPrescrMdtos.Fields.JUM_SEQ.toString());
		
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.OBSERVACAO.toString()), MpmParecerUsoMdto.Fields.OBSERVACAO.toString());
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.SER_MATRICULA.toString()), MpmParecerUsoMdto.Fields.SER_MATRICULA.toString());
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.SER_VIN_CODIGO.toString()), MpmParecerUsoMdto.Fields.SER_VIN_CODIGO.toString());
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.TIPO_PARECER_MDTO_SEQ.toString()), MpmParecerUsoMdto.Fields.TIPO_PARECER_MDTO_SEQ.toString());
		projection.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.DTHR_PARECER.toString()), MpmParecerUsoMdto.Fields.DTHR_PARECER.toString());
		
		projection.add(Projections.property("TPM." + MpmTipoParecerUsoMdto.Fields.DESCRICAO.toString()), MpmTipoParecerUsoMdto.Fields.DESCRICAO.toString());
		
		projection.add(Projections.property("IPR." + MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString()), MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString());
		projection.add(Projections.property("IPR." + MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString()), MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString());
		projection.add(Projections.property("IPR." + MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString()), MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString());
		projection.add(Projections.property("IPR." + MpmItemPrescParecerMdto.Fields.IME_SEQP.toString()), MpmItemPrescParecerMdto.Fields.IME_SEQP.toString());

		criteria.setProjection(projection);
		
		criteria.createAlias("VPM." + VMpmPrescrMdtos.Fields.ITEM_PRESC_PARECER_MDTO.toString(), "IPR");
		criteria.createAlias("IPR." + MpmItemPrescParecerMdto.Fields.MPM_PARECER_USO_MDTOS.toString(), "PUM");
		criteria.createAlias("PUM." + MpmParecerUsoMdto.Fields.MPM_TIPO_PARECER_USO_MDTOS.toString(), "TPM");

		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.IND_ORIGEM_JUSTIF.toString(), DominioSimNao.S.toString()));
		criteria.add(Restrictions.in("VPM." + VMpmPrescrMdtos.Fields.IND_PENDENTE.toString(), new Object[] { "N", "A", "E", "X" }));
		criteria.add(Restrictions.eq("IPR." + MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString(), pMedMatCodigo));
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.JUM_SEQ.toString(), jumSeq));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * obter servidor/pessoa-fisica/qualificacao/tipo-qualificacao/conselho-profissional
	 */
	
	public DetachedCriteria obterCriteriaVinculosServidor(String alias, DetachedCriteria criteria){
		criteria.createAlias(alias + MpmParecerUsoMdtoJn.Fields.SERVIDOR_MATRICULA.toString(), "RSE");
		criteria.createAlias("RSE." + RapServidores.Fields.PESSOA_FISICA.toString(), "RPF");
		criteria.createAlias("RPF." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "RQA");
		criteria.createAlias("RQA." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "RTQ");
		criteria.createAlias("RTQ." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "RCP");
		
		return criteria;
	}
	
	/**
	 * #45270 - obter os detalhes do parecer de medicamentos
	 * @param parecerSeq
	 * @return
	 */
	public DetalhesParecerMedicamentosVO obterDetalhesParecerMedicamentos(BigDecimal parecerSeq,MpmItemPrescParecerMdtoId mpmItemPrescParecerMdtoId){
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class, "VPM");
		criteria.createAlias("VPM." + VMpmPrescrMdtos.Fields.ITEM_PRESC_PARECER_MDTO.toString(), "IPP");
		criteria.createAlias("IPP." + MpmItemPrescParecerMdto.Fields.MPM_PARECER_USO_MDTOS.toString(), "PUM");
		criteria.createAlias("PUM." + MpmParecerUsoMdto.Fields.MPM_TIPO_PARECER_USO_MDTOS.toString(), "TPM");
		obterCriteriaVinculosServidor("PUM.", criteria);

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("PUM." + MpmParecerUsoMdto.Fields.OBSERVACAO.toString()), DetalhesParecerMedicamentosVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property("TPM." + MpmTipoParecerUsoMdto.Fields.DESCRICAO.toString()), DetalhesParecerMedicamentosVO.Fields.PARECER.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString()), DetalhesParecerMedicamentosVO.Fields.MEDICAMENTO.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString()), DetalhesParecerMedicamentosVO.Fields.DOSE.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.FREQ_EDIT.toString()), DetalhesParecerMedicamentosVO.Fields.APRAZAMENTO.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DTHR_INICIO_TRATAMENTO.toString()), DetalhesParecerMedicamentosVO.Fields.DATA_INICIO_TRATAMENTO.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString()), DetalhesParecerMedicamentosVO.Fields.DURACAO_SOLICITADA.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DURACAO_TRAT_APROV.toString()), DetalhesParecerMedicamentosVO.Fields.DURACAO_APROVADA.toString())
				.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString()), DetalhesParecerMedicamentosVO.Fields.VIA_ADM.toString())
				.add(Projections.property("RQA." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), DetalhesParecerMedicamentosVO.Fields.NRO_REG.toString())
				.add(Projections.property("RPF." + RapPessoasFisicas.Fields.NOME.toString()), DetalhesParecerMedicamentosVO.Fields.AVALIADOR.toString())
				.add(Projections.property("RCP." + RapConselhosProfissionais.Fields.SIGLA.toString()), DetalhesParecerMedicamentosVO.Fields.SIGLA.toString())
				.add(Projections.property("RQA." + RapQualificacao.Fields.PESSOA_CODIGO.toString()), DetalhesParecerMedicamentosVO.Fields.PES_CODIGO.toString())));
		
		criteria.add(Restrictions.eq("RCP." + RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("RTQ." + RapTipoQualificacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PUM." + MpmParecerUsoMdto.Fields.SEQ.toString(), parecerSeq));
		
//		if(mpmItemPrescParecerMdtoId !=null){
//			criteria.add(Restrictions.eq("IPP." + MpmItemPrescParecerMdto.Fields.IME_MED_MAT_CODIGO.toString(), mpmItemPrescParecerMdtoId.getImeMedMatCodigo()));
//			criteria.add(Restrictions.eq("IPP." + MpmItemPrescParecerMdto.Fields.IME_PMD_ATD_SEQ.toString(), mpmItemPrescParecerMdtoId.getImePmdAtdSeq()));
//			criteria.add(Restrictions.eq("IPP." + MpmItemPrescParecerMdto.Fields.IME_PMD_SEQ.toString(), mpmItemPrescParecerMdtoId.getImePmdSeq()));
//			criteria.add(Restrictions.eq("IPP." + MpmItemPrescParecerMdto.Fields.IME_SEQP.toString(), mpmItemPrescParecerMdtoId.getImeSeqp()));
//		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(DetalhesParecerMedicamentosVO.class));

		return (DetalhesParecerMedicamentosVO) executeCriteria(criteria).get(0);
		
	}
	
	/**
	 * Consulta C2 da estória #45250.
	 * @param jumSeq
	 * @return
	 */
	public List<Object[]> obterDadosAvaliacaoMedicamento(Integer jumSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmPrescrMdtos.class, "VPM");

		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MAT_CODIGO.toString()), VMpmPrescrMdtos.Fields.MAT_CODIGO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString()), VMpmPrescrMdtos.Fields.MED_DESCRICAO_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.OBSERVACAO_ITEM.toString()), VMpmPrescrMdtos.Fields.OBSERVACAO_ITEM.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString()), VMpmPrescrMdtos.Fields.UNID_DOSE_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString()), VMpmPrescrMdtos.Fields.VIA_ADMINISTRACAO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.FREQ_EDIT.toString()), VMpmPrescrMdtos.Fields.FREQ_EDIT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString()), VMpmPrescrMdtos.Fields.DURACAO_TRAT.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.IND_USO_MDTO.toString()), VMpmPrescrMdtos.Fields.IND_USO_MDTO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.DOSE_CALCULADA.toString()), VMpmPrescrMdtos.Fields.DOSE_CALCULADA.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.JUM_SEQ.toString()), VMpmPrescrMdtos.Fields.JUM_SEQ.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.IND_ANTIMICROBIANO.toString()), VMpmPrescrMdtos.Fields.IND_ANTIMICROBIANO.toString());
		projection.add(Projections.property("VPM." + VMpmPrescrMdtos.Fields.IND_QUIMIOTERAPICO.toString()), VMpmPrescrMdtos.Fields.IND_QUIMIOTERAPICO.toString());
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.IND_ORIGEM_JUSTIF.toString(), DominioSimNao.S.toString()));
		criteria.add(Restrictions.in("VPM." + VMpmPrescrMdtos.Fields.IND_PENDENTE.toString(), new Object[] { "N", "A", "E", "X" }));
		criteria.add(Restrictions.eq("VPM." + VMpmPrescrMdtos.Fields.JUM_SEQ.toString(), jumSeq));
		
		return executeCriteria(criteria);
	}
	
}
