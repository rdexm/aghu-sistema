package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * 
 * @modulo prescricaomedica.cadastrosbasicos
 *
 */
public class MpmUnidadeMedidaMedicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmUnidadeMedidaMedica> {

	private static final long serialVersionUID = 3981333225598493944L;

	public enum UnidadeMedidaMedicaCRUDExceptionCode implements
			BusinessExceptionCode {
		MPM_00775, ERRO_REMOVER_UNIDADE_MEDIDA_MEDICA, ERRO_REMOVER_UNIDADE_MEDIDA_MEDICA1, AFA_MED_UMM_FK1, AFA_FOR_DOS_FK1, ANU_TIP_ITE_DIE_FK1, MPM_ITE_PRE_MDTO_FK1, MPM_ITE_PRE_NPTS_FK1, MPM_TIP_MOD_USO_PROC_FK1, MPT_ITE_PRE_MDTO_FK1,
		ERRO_REMOVER_REGISTROS_ASSOCIADOS_UNIDADE_MEDIDA;;
	}

	public MpmUnidadeMedidaMedica obterUnidadesMedidaMedicaPeloId(
			Integer seqUnidadeMedica) {
		return this.obterPorChavePrimaria(seqUnidadeMedica);
	}

	public Long pesquisarUnidadesMedidaMedicaCount(Integer codigoPlano,
			String descricaoPlano, DominioSituacao situacaoPlano) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmUnidadeMedidaMedica.class);
		if (codigoPlano != null) {
			criteria.add(Restrictions.eq(
					MpmUnidadeMedidaMedica.Fields.SEQ.toString(), codigoPlano));
		}
		if (situacaoPlano != null) {
			criteria.add(Restrictions.eq(
					MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
					situacaoPlano));
		}
		if (descricaoPlano != null && StringUtils.isNotBlank(descricaoPlano)) {
			criteria.add(Restrictions.ilike(
					MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(),
					descricaoPlano.toUpperCase(), MatchMode.ANYWHERE));
		}

		return this.executeCriteriaCount(criteria);
	}

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigoMedida, String descricaoMedida,
			DominioSituacao situacaoUnidadeMedidaMedica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.createAlias(MpmUnidadeMedidaMedica.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		if (codigoMedida != null) {
			criteria.add(Restrictions.eq(
					MpmUnidadeMedidaMedica.Fields.SEQ.toString(), codigoMedida));
		}
		if (situacaoUnidadeMedidaMedica != null) {
			criteria.add(Restrictions.eq(
					MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
					situacaoUnidadeMedidaMedica));
		}
		if (descricaoMedida != null && StringUtils.isNotBlank(descricaoMedida)) {
			criteria.add(Restrictions.ilike(
					MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(),
					descricaoMedida.toUpperCase(), MatchMode.ANYWHERE));
		}
		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Object idOuDescricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmUnidadeMedidaMedica.class);

		if (idOuDescricao != null) {
			String strParametro = (String) idOuDescricao;
			Integer seqUnidadeMedida = null;

			if (CoreUtil.isNumeroInteger(strParametro)){
				seqUnidadeMedida = Integer.valueOf(strParametro);
			}

			if (seqUnidadeMedida != null) {
				criteria.add(Restrictions.eq(
						MpmUnidadeMedidaMedica.Fields.SEQ.toString(),
						seqUnidadeMedida));
			} else {
				if (StringUtils.isNotBlank(strParametro)) {
					criteria.add(Restrictions.ilike(
							MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(),
							strParametro.toUpperCase(), MatchMode.ANYWHERE));
				}
			}
		}

		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_USO_NUTRICAO.toString(),
				DominioSimNao.S));

		return this.executeCriteria(criteria);
	}

	/**
	 * Método responsável pela remoção de uma Unidade de Medida Medica.
	 * 
	 * @dbtables MPM_UNIDADE_MEDIDA_MEDICAS delete
	 * 
	 * @param unidade
	 * @throws ApplicationBusinessException
	 */
	
	public void removerUnidade(MpmUnidadeMedidaMedica unidade)
			throws ApplicationBusinessException {
		try {
			this.remover(unidade);
			this.flush();
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException(
					UnidadeMedidaMedicaCRUDExceptionCode.ERRO_REMOVER_UNIDADE_MEDIDA_MEDICA1,
					e, new Object[0]);
		}
	}

	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(Integer matCodigo) {

		List<MpmUnidadeMedidaMedica> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		subCriteria.setProjection(Projections.property(AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString()+"."+MpmUnidadeMedidaMedica.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(AfaFormaDosagem.Fields.MEDICAMENTOS.toString()+"."+AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		
		criteria.add(Subqueries.propertyIn(MpmUnidadeMedidaMedica.Fields.SEQ.toString(), subCriteria));
		
		criteria.addOrder(Order.asc(MpmUnidadeMedidaMedica.Fields.DESCRICAO
				.toString()));
		result = this.executeCriteria(criteria);

		return result;
	}

	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivas(
			Object parametroConsulta) {

		List<MpmUnidadeMedidaMedica> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		if (parametroConsulta != null) {
			final String parametro = (String) parametroConsulta;

			if (CoreUtil.isNumeroInteger(parametro)) {

				Integer seq = Integer.valueOf(parametro);
				criteria.add(Restrictions.eq(
						MpmUnidadeMedidaMedica.Fields.SEQ.toString(), seq));

			} else if (StringUtils.isNotEmpty(parametro)) {

				criteria.add(Restrictions.ilike(
						MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(),
						parametro, MatchMode.ANYWHERE));
			}

			criteria.add(Restrictions.eq(
					AghEspecialidades.Fields.INDSITUACAO.toString(),
					DominioSituacao.A));

		}

		criteria.addOrder(Order.asc(MpmUnidadeMedidaMedica.Fields.DESCRICAO
				.toString()));
		result = this.executeCriteria(criteria, false);

		return result;
	}

	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivas() {

		List<MpmUnidadeMedidaMedica> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.addOrder(Order.asc(MpmUnidadeMedidaMedica.Fields.DESCRICAO
				.toString()));
		result = this.executeCriteria(criteria, false);

		return result;
	}

	/**
	 * Pesquisa as unidades de medidas médicas ativas para concentracao
	 * 
	 * @param idOuDescricao
	 * @return
	 */
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedicaConcentracao(
			Object idOuDescricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmUnidadeMedidaMedica.class);

		if (idOuDescricao != null
				&& StringUtils.isNotBlank((String) idOuDescricao)) {

			if (CoreUtil.isNumeroInteger(idOuDescricao)) {
				criteria.add(Restrictions.eq(
						MpmUnidadeMedidaMedica.Fields.SEQ.toString(),
						Integer.valueOf((String) idOuDescricao)));
			} else {
				criteria.add(Restrictions.ilike(
						MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(),
						(String) idOuDescricao, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq(
				MpmUnidadeMedidaMedica.Fields.IND_CONCETRACAO.toString(),
				DominioSimNao.S));
		criteria.addOrder(Order.asc(MpmUnidadeMedidaMedica.Fields.DESCRICAO
				.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Verificar a existência de registros de tipo item de dieta em outras
	 * entidades
	 * 
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeRelacionamento(MpmUnidadeMedidaMedica unidade,
			Class class1, Enum field) {

		if (unidade == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(), unidade));

		return (executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * @return
	 */
	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivasPrescreve() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.IND_PRESCRICAO_DOSE.toString(), DominioSimNao.S));
		criteria.addOrder(Order.asc(MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, false);
	}
	
	
	public List<MpmUnidadeMedidaMedica> obterSuggestionUnidade(String strPesquisa) {
		List<MpmUnidadeMedidaMedica> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.IND_SITUACAO.toString(),DominioSituacao.A));

		if(StringUtils.isNotBlank(strPesquisa)){
			if(StringUtils.isNumeric(strPesquisa)){
				criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			}else{
				criteria.add(Restrictions.or(
						Restrictions.eq(MpmUnidadeMedidaMedica.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)),
						Restrictions.eq(MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(), strPesquisa)));
			}
		}
		
		result = this.executeCriteria(criteria,0,100,null,false);
		return result;
	}
	
	public MpmUnidadeMedidaMedica obterPorSeqDosagemSeqMedicamento(Integer seqDosagem, Integer seqMedicamento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		criteria.createAlias(MpmUnidadeMedidaMedica.Fields.FORMAS_DOSAGENS.toString(), "FDS");
		criteria.add(Restrictions.eq("FDS." + AfaFormaDosagem.Fields.SEQ.toString(), seqDosagem));
		criteria.add(Restrictions.eq("FDS." + AfaFormaDosagem.Fields.MEDICAMENTOS_MAT_CODIGO.toString(), seqMedicamento));
		return (MpmUnidadeMedidaMedica) executeCriteriaUniqueResult(criteria);
	}

	public MpmUnidadeMedidaMedica obterUnidadesMedidaMedicaPelaSigla(String ummDescricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeMedidaMedica.class);
		
		criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString(), ummDescricao));
		criteria.add(Restrictions.eq(MpmUnidadeMedidaMedica.Fields.IND_MONIT_HEMODINAMICA.toString(), DominioSimNao.S));
		
		return (MpmUnidadeMedidaMedica) executeCriteriaUniqueResult(criteria);
	}
}