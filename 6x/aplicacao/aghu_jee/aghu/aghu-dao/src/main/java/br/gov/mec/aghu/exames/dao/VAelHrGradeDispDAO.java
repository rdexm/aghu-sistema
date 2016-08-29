package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelHrGradeDisp;


public class VAelHrGradeDispDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelHrGradeDisp> {

	private static final long serialVersionUID = -1180462057454042994L;

	private StringBuffer gerarHqlObrigatorio(final StringBuffer hql, final Date dataReativacao, final Date dataAtual){
		hql.append(" vhg.")
			.append(VAelHrGradeDisp.Fields.SIGLA.toString())
			.append(" = :sigla and ")
			.append("vhg.")
			.append(VAelHrGradeDisp.Fields.MAT_EXAME.toString())
			.append(" = :matExame and ")
			.append("vhg.")
			.append(VAelHrGradeDisp.Fields.UNF_EXAME.toString())
			.append(" = :unfExame and ")
			.append("vhg.")
			.append(VAelHrGradeDisp.Fields.SITUACAO_HORARIO.toString())
			.append(" = :situacaoHorario and ")
			.append("(vhg.")
			.append(VAelHrGradeDisp.Fields.TIPO.toString())
			.append(" = :tipoMarcacao or ")
			.append(" vhg.")
			.append(VAelHrGradeDisp.Fields.TIPO.toString())
			.append(" = :tipoMarcacaoDiv) ");
		if(dataReativacao != null && dataReativacao.after(dataAtual)) {
			hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString())
				.append(" >= :dataReativacao ");
		} else {
			hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString())
				.append(" >= :dataAtual ");
		}
		return hql;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String montarHqlHorariosLivres(String sigla, Integer matExame, Short unfExame, Date dataReativacao, Short tipoMarcacao, Short tipoMarcacaoDiv, Boolean verificaColeta, List<AelExameHorarioColeta> listaExamesHorarioColeta, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor) {
		Date dataAtual = new Date();
		StringBuffer hql = new StringBuffer(440);
		hql.append("select new br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO(")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.HR_EXTRA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.DT_AGENDA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.HR_AGENDA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.GRADE.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.SEQ_GRADE.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.GRUPO_EXAME.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.UNF_EXAME.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.SALA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.SALA_SEQ.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.NUM_SALA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.MATRICULA.toString()).append(", ")
			.append("	vhg.").append(VAelHrGradeDisp.Fields.VIN_CODIGO.toString())
			.append(") from VAelHrGradeDisp vhg")
			.append(" where ");
		this.gerarHqlObrigatorio(hql, dataReativacao, dataAtual);
		if(listaExamesHorarioColeta != null && !listaExamesHorarioColeta.isEmpty()){
			hql.append("and (");
			int tamanhoLista = listaExamesHorarioColeta.size();
			for(int i=0; i< tamanhoLista;i++){
				hql.append("(vhg.")
					.append(VAelHrGradeDisp.Fields.DIA_SEMANA.toString())
					.append(" =:dia_").append(i)
					.append(" and CAST(EXTRACT (minute from vhg.")
					.append(VAelHrGradeDisp.Fields.DTHR_AGENDA)
					.append(") + ")
					.append("EXTRACT (hour from vhg.")
					.append(VAelHrGradeDisp.Fields.DTHR_AGENDA)
					.append(" ) * 60  as int) ")
					.append(" between :horaminutosi_").append(i)
					.append(" and ")
					.append(" :horaminutosf_").append(i).append(" )");				
			    if(i != (tamanhoLista - 1)){
					hql.append(" or ");		
			    }
	    	}
			hql.append(" ) ");
	    }
        if(data != null) {
			hql.append(" and vhg.");
			hql.append(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString());
			hql.append(" >= :data ");
		} 
        if(hora!=null){
        	hql.append(" and ( CAST(EXTRACT (minute from vhg.")
					.append(VAelHrGradeDisp.Fields.DTHR_AGENDA)
					.append(") + ")
					.append("EXTRACT (hour from vhg.")
					.append(VAelHrGradeDisp.Fields.DTHR_AGENDA)
					.append(" ) * 60  as int) ")
					.append(" between :horaMinutosPesquisa")
					.append(" and ")
					.append(" :horaMinutosPesquisa").append(" )");
		}
        if(grade != null) {
			hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.SEQ_GRADE.toString())
				.append(" = :grade ");
		} 
        if(grupoExame!=null){
        	hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.GRUPO_EXAME.toString())
				.append(" = :seqGrupoExame ");
		}
        if(salaExecutoraExame!=null){
        	hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.SALA_SEQ.toString())
				.append(" = :salaSeq ");
		}
    	if(servidor!=null){
    		hql.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.VIN_CODIGO.toString())
				.append(" = :vinCodigo ")
				.append(" and vhg.")
				.append(VAelHrGradeDisp.Fields.MATRICULA.toString())
				.append(" = :matricula ");
		}
    	hql.append(" order by vhg.").append(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString());
        return hql.toString();
	}
	
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<VAelHrGradeDispVO> pesquisarHorariosLivresParaExameVO(String sigla, Integer matExame, Short unfExame, Date dataReativacao, Short tipoMarcacao, Short tipoMarcacaoDiv, Boolean verificaColeta, List<AelExameHorarioColeta> listaExamesHorarioColeta, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor) {
		Date dataAtual = new Date();
		String hql = montarHqlHorariosLivres(sigla, matExame, unfExame, dataReativacao, tipoMarcacao, tipoMarcacaoDiv, verificaColeta, listaExamesHorarioColeta, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
		Query query = createHibernateQuery(hql);
//		if (!verificaColeta || listaExamesHorarioColeta == null || (listaExamesHorarioColeta != null && !listaExamesHorarioColeta.isEmpty())) {
			query.setParameter("sigla", sigla);
			query.setParameter("matExame", matExame);
			query.setParameter("unfExame", unfExame);
			query.setParameter("situacaoHorario", DominioSituacaoHorario.L);
			query.setParameter("tipoMarcacao", tipoMarcacao);
			query.setParameter("tipoMarcacaoDiv", tipoMarcacaoDiv);
			if(dataReativacao!=null && dataReativacao.after(dataAtual)) {
				query.setParameter("dataReativacao", dataReativacao);
			} else {
				query.setParameter("dataAtual", new Date());
			}
//		}
		Calendar calendario = Calendar.getInstance();
		if(verificaColeta && listaExamesHorarioColeta!=null ){
			for(int i=0; i<listaExamesHorarioColeta.size();i++){
    			AelExameHorarioColeta exameHorarioColeta = listaExamesHorarioColeta.get(i);
    			
    			calendario.setTime(exameHorarioColeta.getHorarioInicial());
    			Integer horaConvertidaInicial = converterEmMinutos(calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE));
    			
    			calendario.setTime(exameHorarioColeta.getHorarioFinal());
    			Integer horaConvertidaFim = converterEmMinutos(calendario.get(Calendar.HOUR_OF_DAY),calendario.get(Calendar.MINUTE));
    			
    			query.setParameter("horaminutosi_"+i, horaConvertidaInicial);
    			query.setParameter("horaminutosf_"+i, horaConvertidaFim);
    			query.setParameter("dia_"+i, exameHorarioColeta.getDiaSemana());
    		}
    	}
		
		if(data!=null){
			query.setParameter("data", DateUtil.truncaData(data));
		}
		if(hora!=null){
			calendario.setTime(hora);
			Integer horaMinutosPesquisa = converterEmMinutos(calendario.get(Calendar.HOUR_OF_DAY),calendario.get(Calendar.MINUTE));
			query.setParameter("horaMinutosPesquisa", horaMinutosPesquisa);
		}
		if(grade!=null){
			query.setParameter("grade", grade);
		}
		if(grupoExame!=null){
			query.setParameter("seqGrupoExame", grupoExame.getSeq());
		}
		if(salaExecutoraExame!=null){
			query.setParameter("salaSeq", salaExecutoraExame.getId().getSeqp());
		}
		if(servidor!=null){
			query.setParameter("vinCodigo", servidor.getId().getVinCodigo());
			query.setParameter("matricula", servidor.getId().getMatricula());
		}
		query.setMaxResults(600);
		return query.list();
	}
	
	private Integer converterEmMinutos(Integer hora,Integer minutos) {
		return (hora * 60) + minutos;
	}
	
	public List<VAelHrGradeDispVO> pesquisarHorariosLivresAteFinalDia(Short grade, Integer seqGrade, Short tipoMarcacao, 
			Short tipoMarcacaoDiv, Date dthrAgenda) {
		Date horarioFinalDia = DateUtil.obterDataComHoraFinal(dthrAgenda);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelHrGradeDisp.class);
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.groupProperty(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString()))
					.add(Projections.groupProperty(VAelHrGradeDisp.Fields.SITUACAO_HORARIO.toString()))
					.add(Projections.groupProperty(VAelHrGradeDisp.Fields.HR_EXTRA.toString())));
		
		criteria.add(Restrictions.eq(VAelHrGradeDisp.Fields.GRADE.toString(), grade));
		criteria.add(Restrictions.eq(VAelHrGradeDisp.Fields.SEQ_GRADE.toString(), seqGrade));
		
		List<Short> listaTipoMarcacao = new ArrayList<Short>();
		listaTipoMarcacao.add(tipoMarcacao);
		listaTipoMarcacao.add(tipoMarcacaoDiv);
		
		criteria.add(Restrictions.in(VAelHrGradeDisp.Fields.TIPO.toString(), listaTipoMarcacao));
		criteria.add(Restrictions.between(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString(), dthrAgenda, horarioFinalDia));
		
		criteria.addOrder(Order.asc(VAelHrGradeDisp.Fields.DTHR_AGENDA.toString()));

		List<VAelHrGradeDispVO> listaVAelHrGradeDispVO = new ArrayList<VAelHrGradeDispVO>();
		
		List<Object[]> listaArrayObject = executeCriteria(criteria);

		for (Object[] arrayObject : listaArrayObject) {
			VAelHrGradeDispVO vAelHrGradeDispVO = new VAelHrGradeDispVO();
			vAelHrGradeDispVO.setDthrAgenda((Date) arrayObject[0]);
			vAelHrGradeDispVO.setSituacaoHorario((DominioSituacaoHorario) arrayObject[1]);
			vAelHrGradeDispVO.setHrExtra((Boolean) arrayObject[2]);
			listaVAelHrGradeDispVO.add(vAelHrGradeDispVO);
		}
		
		return listaVAelHrGradeDispVO;
    }

}