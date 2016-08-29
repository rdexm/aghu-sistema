package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptAgendaSessao;
import br.gov.mec.aghu.model.MptItemPrcrModalidade;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptTipoModalidade;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.TratamentoTerapeuticoVO;

public class MptTratamentoTerapeuticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptTratamentoTerapeutico> {

	private static final long serialVersionUID = 8022531093665513417L;

	private static final String TRP = "TRP";
	private static final String TRP_DOT = "TRP.";
	private static final String ATD = "ATD";
	private static final String ATD_DOT = "ATD.";
	private static final String PTE = "PTE";
	private static final String PTE_DOT = "PTE.";
	private static final String FCS = "FCS";

	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTratamentoTerapeutico.class);

		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<MptTratamentoTerapeutico> pesquisarTratamentosTerapeuticosPorPaciente(
			AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarTratamentosTerapeuticosPorPaciente(paciente);
		criteria.addOrder(Order.desc(MptTratamentoTerapeutico.Fields.DTHR_INICIO.toString()));

		
		criteria.setFetchMode(MptTratamentoTerapeutico.Fields.SERVIDOR_RESPONSAVEL.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(MptTratamentoTerapeutico.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(MptTratamentoTerapeutico.Fields.ESPECIALIDADE.toString()+"."+AghEspecialidades.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(MptTratamentoTerapeutico.Fields.TIPO_TRATAMENTO.toString(), FetchMode.JOIN);
		
		List<MptTratamentoTerapeutico> listaVolta = executeCriteria(criteria);

		return listaVolta;
	}
	
	public Long pesquisarTratamentosTerapeuticosPorPacienteCount(
			AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarTratamentosTerapeuticosPorPaciente(paciente);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarTratamentosTerapeuticosPorPaciente(
			AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTratamentoTerapeutico.class);
		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString(),
				paciente.getCodigo()));
		return criteria;
	}
	
	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticos(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTratamentoTerapeutico.class);
		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.ATENDIMENTO.toString(), atendimento));
		
		return executeCriteria(criteria);
	}
	
	public List<MptTratamentoTerapeutico> listarTratamentosTerapeuticosPorPacienteTptSeq(Integer pacCodigo, Integer tptSeq ) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTratamentoTerapeutico.class);
		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MptTratamentoTerapeutico.Fields.TPT_SEQ.toString(), tptSeq));
		criteria.addOrder(Order.desc(MptTratamentoTerapeutico.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.desc(MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<MptTratamentoTerapeutico> listarSessoesFisioterapia(Integer pacCodigo, Integer tptSeq ) {
		StringBuilder hql = new StringBuilder(300);
		hql.append("select distinct trp.").append( MptTratamentoTerapeutico.Fields.DTHR_INICIO.toString() ).append(", ")
		.append("trp.").append( MptTratamentoTerapeutico.Fields.DTHR_FIM.toString() ).append(", ")
		.append("trp.").append( MptTratamentoTerapeutico.Fields.SEQ.toString() ).append(' ')
		.append("from ").append(MptTratamentoTerapeutico.class.getName()).append(" trp, ")
		.append(MptAgendaSessao.class.getName() ).append( " age, ")
		.append(MptTipoModalidade.class.getName() ).append( " tmd, ")
		.append(MptItemPrcrModalidade.class.getName() ).append( " itm, ")
		.append(AghAtendimentos.class.getName() ).append( " atd ")
		.append("where atd.").append( AghAtendimentos.Fields.SEQ.toString() ).append(
				" = itm.").append( MptItemPrcrModalidade.Fields.ID_PTE_ATD_SEQ.toString() ).append(' ')
		.append("and itm.").append( MptItemPrcrModalidade.Fields.TIPO_MODALIDADE_CODIGO.toString() 
				).append(" = tmd.").append( MptTipoModalidade.Fields.CODIGO.toString() ).append(' ')
		.append("and age.").append( MptAgendaSessao.Fields.ITEM_PRCR_MODALIDADE_ID_PTE_ATD_SEQ.toString() ).append(" = itm."
				).append( MptItemPrcrModalidade.Fields.ID_PTE_ATD_SEQ.toString() ).append(' ')
		.append("and age.").append( MptAgendaSessao.Fields.ITEM_PRCR_MODALIDADE_ID_PTE_SEQ.toString() ).append(" = itm."
				).append( MptItemPrcrModalidade.Fields.ID_PTE_SEQ.toString() ).append(' ')
		.append("and age.").append( MptAgendaSessao.Fields.ITEM_PRCR_MODALIDADE_ID_SEQP.toString() ).append(" = itm."
				).append( MptItemPrcrModalidade.Fields.ID_SEQP.toString() ).append(' ')
		.append("and trp.").append( MptTratamentoTerapeutico.Fields.SEQ.toString() 
				).append(" = atd.trpSeq ")
		.append("and trp.").append( MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString() ).append(" = :pacCodigo ")
		.append("and trp.").append( MptTratamentoTerapeutico.Fields.TPT_SEQ.toString() ).append(" = :tptSeq ")
		.append("order by trp.").append( MptTratamentoTerapeutico.Fields.DTHR_INICIO.toString() 
				).append(" desc , trp.").append( MptTratamentoTerapeutico.Fields.DTHR_FIM.toString() ).append(' ');
		
		Query query = createQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("tptSeq", tptSeq);
		
		List<Object[]> valores = query.getResultList();
		List<MptTratamentoTerapeutico> lstRetorno = new ArrayList<MptTratamentoTerapeutico>();

		if(valores != null && valores.size() > 0){
			for (Object[] objects : valores) {
				MptTratamentoTerapeutico tratamentoTerapeutico = new MptTratamentoTerapeutico();
				tratamentoTerapeutico.setDthrInicio((Date) objects[0]);
				tratamentoTerapeutico.setDthrFim((Date) objects[1]);
				tratamentoTerapeutico.setSeq(Integer.parseInt(objects[2].toString()));
				
				lstRetorno.add(tratamentoTerapeutico);
			}
		}
		
		return lstRetorno;
	}

	public MptTratamentoTerapeutico obterConvenioPacienteAgendamento(Integer pacCodigo, Integer pteSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MptTratamentoTerapeutico.class, TRP);

		criteria.createAlias(TRP_DOT + MptTratamentoTerapeutico.Fields.LISTA_AGH_ATENDIMENTOS.toString(), ATD);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.MPT_PRESCRICAO_PACIENTES.toString(), PTE);
		criteria.createAlias(TRP_DOT + MptTratamentoTerapeutico.Fields.FAT_CONVENIO_SAUDE.toString(), FCS);

		criteria.add(Restrictions.eq(ATD_DOT + AghAtendimentos.Fields.PAC_COD.toString(), pacCodigo));
		criteria.add(Restrictions.eq(PTE_DOT + MptPrescricaoPaciente.Fields.SEQ.toString(), pteSeq));

		List<MptTratamentoTerapeutico> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}

	public TratamentoTerapeuticoVO buscarTratamentoTerapeuticoPorSeq(Integer trpSeq) {
		StringBuffer sql = new StringBuffer(" select ")
		.append("  ").append("mtt.").append(MptTratamentoTerapeutico.Fields.ESP_SEQ.toString()).append(" as espSeq, ")
		.append("  ").append("mtt.").append(MptTratamentoTerapeutico.Fields.SERVIDOR_CODIGO.toString()).append(" as serVinCodigo, ")
		.append("  ").append("mtt.").append(MptTratamentoTerapeutico.Fields.SERVIDOR_MATRICULA.toString()).append(" as serMatricula, ")
		.append("  ").append("mtt.").append(MptTratamentoTerapeutico.Fields.UNF_SEQ.toString()).append(" as unfSeq, ")
		.append("  ").append("unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()).append(" as cctCodigo ")
		.append(" from ")
		.append(' ').append(MptTratamentoTerapeutico.class.getSimpleName()).append(" mtt, ")
		.append(' ').append(AghUnidadesFuncionais.class.getSimpleName()).append(" unf ")
		.append(" where ")
		.append("  ").append("mtt.").append(MptTratamentoTerapeutico.Fields.UNF_SEQ.toString()).append(" = ").append("unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		.append("  and ").append("mtt.").append(MptTratamentoTerapeutico.Fields.SEQ.toString()).append(" = ").append(" :trpSeq ");
		
		final org.hibernate.Query query = this.createHibernateQuery(sql.toString());
		
		query.setParameter("trpSeq", trpSeq);
		
		query.setResultTransformer(Transformers.aliasToBean(TratamentoTerapeuticoVO.class));

		List<TratamentoTerapeuticoVO> listaVo = query.list();
		if(listaVo.size() > 0){
			return listaVo.get(0);
		}
		return null;
	}
	
}
