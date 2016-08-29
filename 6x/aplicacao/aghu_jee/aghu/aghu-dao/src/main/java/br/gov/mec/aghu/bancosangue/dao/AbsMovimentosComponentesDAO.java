package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.model.AbsComponenteMovimentado;
import br.gov.mec.aghu.model.AbsDoacoes;
import br.gov.mec.aghu.model.AbsMovimentosComponentes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class AbsMovimentosComponentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsMovimentosComponentes> {

	private static final long serialVersionUID = 8415376587028937423L;

	/**
	 * Lista transfusao com exists sem agrupamento
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param origem
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(a."); 
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(") as dthrMovimento");
		
		hql.append(", a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
		hql.append(" as pacCodigo");

		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where not exists (");
		
			hql.append("select 1 from ");
			hql.append(AghAtendimentos.class.getName()).append(" d");
			//where
			hql.append(" where d.");
			hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
			hql.append(" = a.");
			hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
			hql.append(" and a.");
			hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
			hql.append(" between d.");
			hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString());
			hql.append(" and ");
			hql.append(" case when d.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
			hql.append(" is null then :sysdate else d.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" end");
			hql.append(" and d.");
			hql.append(AghAtendimentos.Fields.ORIGEM.toString());
			hql.append(" = :origem");
			hql.append(" and d.");
			hql.append(AghAtendimentos.Fields.PRONTUARIO.toString());
			hql.append(" is not null");
			
		hql.append(')');
		
		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is not null");
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("sysdate", new Date());
		query.setParameter("origem", origem);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}	
	
	/**
	 * Lista transfusao sem exists sem agrupamento
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(a."); 
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(") as dthrMovimento");
		
		hql.append(", a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
		hql.append(" as pacCodigo");

		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is null");
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}		

	
	
	/**
	 * Lista transfusao com exists com agrupamento
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param origem
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String[] sels = {
				" trunc(a." + AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString() + ")",
				" case when b." + AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " is null then " + matricula + " else b."
						+ AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " end ",
				" case when b." + AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " is null then " + vinCodigo + " else b."
						+ AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " end" };
		
		hql = new StringBuffer(450);
		hql.append(" select ");
		hql.append(sels[0]).append(" as dthrMovimento, count(*) as quantidade,");

		hql.append(sels[1]).append(" as serMatricula, ");
		hql.append(sels[2]).append(" as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where not exists (");
		
			hql.append("select 1 from ");
			hql.append(AghAtendimentos.class.getName()).append(" d");
			//where
			hql.append(" where d.");
			hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
			hql.append(" = a.");
			hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
			hql.append(" and a.");
			hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
			hql.append(" between d.");
			hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString());
			hql.append(" and ");
			hql.append(" case when d.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
			hql.append(" is null then :sysdate else d.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" end");
			hql.append(" and d.");
			hql.append(AghAtendimentos.Fields.ORIGEM.toString());
			hql.append(" = :origem");
			hql.append(" and d.");
			hql.append(AghAtendimentos.Fields.PRONTUARIO.toString());
			hql.append(" is not null");
			
		hql.append(')');
		
		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is not null");

		//group by
		hql.append(" group by ").append(sels[0]);
		hql.append(", ").append(sels[1]);
		hql.append(", ").append(sels[2]);
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("sysdate", new Date());
		query.setParameter("origem", origem);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}
	
	/**
	 * Lista transfusao sem exists com agrupamento
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String[] sels = {
				" trunc(a." + AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString() + ")",
				" case when b." + AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " is null then " + matricula + " else b."
						+ AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " end ",
				" case when b." + AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " is null then " + vinCodigo + " else b."
						+ AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " end" };
		hql = new StringBuffer(300);
		hql.append(" select ");
		hql.append(sels[0]).append(" as dthrMovimento, ");
		
		hql.append("count(*) as quantidade, ");

		hql.append(sels[1]).append(" as serMatricula, ");
		hql.append(sels[2]).append(" as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is null");

		//group by
		hql.append(" group by ").append(sels[0]);
		hql.append(", ").append(sels[1]);
		hql.append(", ").append(sels[2]);
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}

	public List<AbsMovimentosComponentes> listarMovimentosComponentesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsMovimentosComponentes.class);

		criteria.add(Restrictions.eq(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	
	/**
	 * Lista transfusao com exists sem agrupamento com doacao
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param origem
	 * @param tdoCodigo
	 * @param isTdoCodigoIgual
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo, DominioOrigemAtendimento origem, String tdoCodigo, Boolean isTdoCodigoIgual) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(a."); 
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(") as dthrMovimento");
		
		hql.append(", a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
		hql.append(" as pacCodigo");

		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsDoacoes.class.getName());
		hql.append(" as d, ");

		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where not exists (");
		
			hql.append("select 1 from ");
			hql.append(AghAtendimentos.class.getName()).append(" g");
			//where
			hql.append(" where g.");
			hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
			hql.append(" = a.");
			hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
			hql.append(" and a.");
			hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
			hql.append(" between g.");
			hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString());
			hql.append(" and ");
			hql.append(" case when g.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
			hql.append(" is null then :sysdate else g.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" end");
			hql.append(" and g.");
			hql.append(AghAtendimentos.Fields.ORIGEM.toString());
			hql.append(" = :origem");
			hql.append(" and g.");
			hql.append(AghAtendimentos.Fields.PRONTUARIO.toString());
			hql.append(" is not null");
			
		hql.append(')');
		
		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is not null");
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_NUMERO.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_NUMERO.toString());
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_BSA_CODIGO.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_BSA_CODIGO.toString());
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_DATA.toString());

		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.TDO_CODIGO.toString());
		if (isTdoCodigoIgual){
			hql.append(" =");
		} else {
			hql.append(" <>");
		}
		hql.append(" :tdoCodigo");
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("sysdate", new Date());
		query.setParameter("origem", origem);
		query.setParameter("tdoCodigo", tdoCodigo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}
	
	/**
	 * Lista transfusao sem exists sem agrupamento com doacao
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param unfSeqs
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param tdoCodigo
	 * @param isTdoCodigoIgual
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo,  String tdoCodigo, Boolean isTdoCodigoIgual) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(a."); 
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(") as dthrMovimento");
		
		hql.append(", a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());
		hql.append(" as pacCodigo");

		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when b.").append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else b.");
		hql.append(AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsDoacoes.class.getName());
		hql.append(" as d, ");

		hql.append(AipPacientes.class.getName());
		hql.append(" as c, ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.UNF_SEQ.toString());
		hql.append(" in (:unfSeqs)");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and c.");
		hql.append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString());

		hql.append(" and c.");
		hql.append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" is null");
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_NUMERO.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_NUMERO.toString());
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_BSA_CODIGO.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_BSA_CODIGO.toString());
		
		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(" = b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_BOL_DATA.toString());

		hql.append(" and d.");
		hql.append(AbsDoacoes.Fields.TDO_CODIGO.toString());
		
		if (isTdoCodigoIgual){
			hql.append(" =");
		} else {
			hql.append(" <>");
		}
		hql.append(" :tdoCodigo");
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameterList("unfSeqs", unfSeqs);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("tdoCodigo", tdoCodigo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}	
	
	/**
	 * Lista movimentos componentes 
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param mcoType
	 * @param indEstorno
	 * @param indIrradiado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, Boolean indIrradiado) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String[] sels = {
				" trunc(a." + AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString() + ")",
				" case when b." + AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " is null then " + matricula + " else b."
						+ AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " end ",
				" case when b." + AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " is null then " + vinCodigo + " else b."
						+ AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " end" };
		hql = new StringBuffer(250);
		hql.append(" select ");
		hql.append(sels[0]).append(" as dthrMovimento, ");
		
		hql.append("count(*) as quantidade, ");

		hql.append(sels[1]).append(" as serMatricula, ");
		hql.append(sels[2]).append(" as serVinCodigo");

		//from
		hql.append(" from ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_IRRADIADO.toString());
		hql.append(" = :indIrradiado");
		
		//group by
		hql.append(" group by ").append(sels[0]);
		hql.append(", ").append(sels[1]);
		hql.append(", ").append(sels[2]);
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("indIrradiado", indIrradiado);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}			

	/**
	 * Lista movimentos componentes 
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param indFiltrado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoFiltrado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		String[] sels = {
				" trunc(a." + AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString() + ")",
				" case when b." + AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " is null then " + matricula + " else b."
						+ AbsComponenteMovimentado.Fields.SER_MATRICULA.toString() + " end ",
				" case when b." + AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " is null then " + vinCodigo + " else b."
						+ AbsComponenteMovimentado.Fields.SER_VIN_CODIGO.toString() + " end" };
		
		hql = new StringBuffer(260);
		hql.append(" select ");
		hql.append(sels[0]).append(" as dthrMovimento, count(*) as quantidade,");

		hql.append(sels[1]).append(" as serMatricula, ");
		hql.append(sels[2]).append(" as serVinCodigo");
		
		//from
		hql.append(" from ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_FILTRADO.toString());
		hql.append(" = :indFiltrado");
		
		//group by
		hql.append(" group by ").append(sels[0]);
		hql.append(", ").append(sels[1]);
		hql.append(", ").append(sels[2]);
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("indFiltrado", indFiltrado);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}			
	
	/**
	 * Lista movimentos componentes 
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param mcoType
	 * @param indEstorno
	 * @param ecoCsaCodigo
	 * @param indFiltrado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado(Date vDtHrInicio, Date vDtHrFim,
			DominioMcoType mcoType, Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String[] sels = {
				"trunc(a." + AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString() + ")",
				" a." + AbsMovimentosComponentes.Fields.COD_PACIENTE.toString(),
				(new StringBuffer(" case")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '00' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '01' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '02' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '03' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '04' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '05' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '06' then 0")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '07' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '08' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '09' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '10' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '11' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '12' then 1")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '13' then 2")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '14' then 2")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '15' then 2")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '16' then 2")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '17' then 2")
						.append(" when to_char(a.").append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString()).append(",'HH24') = '18' then 2")
						.append(" else 3 end ")).toString() };
		
		hql = new StringBuffer(220);
		hql.append(" select ");
		hql.append(sels[0]).append(" as dthrMovimento, ");
		
		hql.append(sels[1]).append(" as pacCodigo ");
		
		//case
//		hql.append(sels[2]).append(" as turno");
		
		//from
		hql.append(" from ");

		hql.append(AbsComponenteMovimentado.class.getName());
		hql.append(" as b, ");
		
		hql.append(AbsMovimentosComponentes.class.getName());
		hql.append(" as a");
		
		//where
		hql.append(" where a.");
		hql.append(AbsMovimentosComponentes.Fields.DTHR_MOVIMENTO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and a.");
		hql.append(AbsMovimentosComponentes.Fields.MCO_TYPE.toString());
		hql.append(" = :mcoType");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_ESTORNO.toString());
		hql.append(" = :indEstorno");

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.MCO_SEQ.toString());
		hql.append(" = a.");
		hql.append(AbsMovimentosComponentes.Fields.SEQ.toString());

		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.ECO_CSA_CODIGO.toString());
		hql.append(" = :ecoCsaCodigo");
		
		hql.append(" and b.");
		hql.append(AbsComponenteMovimentado.Fields.IND_FILTRADO.toString());
		hql.append(" = :indFiltrado");
		
		//group by
		hql.append(" group by ").append(sels[0]);
		hql.append(", ").append(sels[1]);
		hql.append(", ").append(sels[2]);
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("mcoType", mcoType);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("ecoCsaCodigo", ecoCsaCodigo);
		query.setParameter("indFiltrado", indFiltrado);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}
	
	public List<AbsMovimentosComponentes> listarMovimentosComponentes(Integer pacCodigo, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsMovimentosComponentes.class);
		criteria.add(Restrictions.eq(AbsMovimentosComponentes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AbsMovimentosComponentes.Fields.CRIADO_EM.toString(), dthrInicio));
		
		return executeCriteria(criteria);
	}
	
}
