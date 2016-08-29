package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;

public class MbcAgendaHemoterapiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaHemoterapia> {

	private static final String _AS_ = " as ";
	private static final long serialVersionUID = 1935861822554322614L;

	public List<PlanejamentoCirurgicoVO> pesquisarRelatorioPlanejamentoCirurgico(
			Date dataCirurgia, Short seqEspecialidade, Integer pacCodigo,
			Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucIndFuncaoProf) {
		
		StringBuilder hql = new StringBuilder(1000);
		
		hql.append("SELECT \n ") 
		.append("agd." ).append( MbcAgendas.Fields.SEQ.toString() 					).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.AGD_SEQ.toString() ).append( " , \n") 
		.append("agd." ).append( MbcAgendas.Fields.DT_AGENDA.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.DT_AGENDA_DATE.toString() ).append( " , \n")
		.append("agd." ).append( MbcAgendas.Fields.TEMPO_SALA.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.TEMPO_SALA_DATE.toString() ).append( " , \n")
		.append("agd." ).append( MbcAgendas.Fields.PUC_SER_VIN_CODIGO.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.PUC_SER_VIN_CODIGO.toString() ).append( " , \n")//Equipe e CrmEquipe
		.append("agd." ).append( MbcAgendas.Fields.PUC_SER_MATRICULA.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.PUC_SER_MATRICULA.toString() ).append( " , \n")//Equipe e CrmEquipe
		.append("esp." ).append( AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.ESPECIALIDADE.toString() ).append( " , \n")
		.append("agd." ).append( MbcAgendas.Fields.ALTERADO_EM.toString()			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.ALTERADO_EM_MBC_AGENDA.toString() ).append( " , \n")//DtRegistro e resp_agd
		.append("agd." ).append( MbcAgendas.Fields.DTHR_INCLUSAO.toString() 		).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.DTHR_INCLUSAO.toString() ).append( " , \n")//DtRegistro e resp_agd
		.append("agd." ).append( MbcAgendas.Fields.SER_VIN_CODIGO.toString() 		).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.SER_VIN_CODIGO.toString() ).append( " , \n")//resp_agd
		.append("agd." ).append( MbcAgendas.Fields.SER_MATRICULA.toString()			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.SER_MATRICULA.toString() ).append( " , \n")//resp_agd
		.append("agd." ).append( MbcAgendas.Fields.SER_VIN_CODIGO_ALTERADO_POR.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.SER_VIN_CODIGO_ALTERADO_POR.toString() ).append( " , \n")//resp_agd
		.append("agd." ).append( MbcAgendas.Fields.SER_MATRICULA_ALTERADO_POR.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.SER_MATRICULA_ALTERADO_POR.toString() ).append( " , \n")//resp_agd
		.append("agd." ).append( MbcAgendas.Fields.COMENTARIO.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.COMENTARIO.toString()).append( " , \n")
		.append("pac." ).append( AipPacientes.Fields.CODIGO.toString()  			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.PAC_CODIGO.toString() ).append( " , \n")
		.append("pac." ).append( AipPacientes.Fields.NOME.toString() 				).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.NOME_PACIENTE.toString() ).append( " , \n")
		.append("pac." ).append( AipPacientes.Fields.SEXO.toString() 				).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.SEXO_PAC_DOMINIO.toString() ).append( " , \n")
		.append("pac." ).append( AipPacientes.Fields.PRONTUARIO.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.PRONTUARIO_INTEGER.toString() ).append( " , \n")
		.append("pci." ).append( MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString() ).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.PROCEDIMENTO.toString() ).append( " , \n")
		.append("csa." ).append( AbsComponenteSanguineo.Fields.DESCRICAO.toString() ).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.DESC_HEMO.toString() ).append( " , \n")
		.append("ahe." ).append( MbcAgendaHemoterapia.Fields.QTDE_UNIDADE.toString()).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.QTDE_HEMO_SHORT.toString() ).append( " , \n")
		.append("ahe." ).append( MbcAgendaHemoterapia.Fields.QTDE_UNIDADE_ADIC.toString()  	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.QTDE_HEMO_ADIC_SHORT.toString() ).append( " , \n")
		.append("pac." ).append( AipPacientes.Fields.LTO_LTO_ID.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.LTO_LTO_ID.toString() ).append( " , \n")//Local
		.append("pac." ).append( AipPacientes.Fields.QRT_NUMERO.toString() 			).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.QRT_NUMERO.toString() ).append( " , \n")//Local
		.append("agd." ).append( MbcAgendas.Fields.UNF_SEQ.toString() 				).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.UNF_SEQ.toString() ).append( " , \n")//Local
		.append("agd." ).append( MbcAgendas.Fields.REGIME.toString() 				).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.REGIME_DOMINIO.toString()).append( " , \n")
		.append("agd." ).append( MbcAgendas.Fields.LADO_CIRURGIA.toString() 		).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.LADO_CIRURGIA_DOMINIO.toString() ).append( " , \n")
		.append("agd." ).append( MbcAgendas.Fields.MATERIAL_ESPECIAL.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.MATERIAL_ESPECIAL.toString() ).append( " , \n")
		.append("unf." ).append( AghUnidadesFuncionais.Fields.DESCRICAO.toString() 	).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.UNIDADE_FUNCIONAL.toString()).append( " , \n")
		.append("cid." ).append( AghCid.Fields.CODIGO.toString() 					).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.CID_CODIGO ).append( " , \n")//DIAGNOSTICO
		.append("cid." ).append( AghCid.Fields.DESCRICAO.toString() 				).append(_AS_ ).append( PlanejamentoCirurgicoVO.Fields.CID_DESCRICAO.toString() ).append( " \n")//DIAGNOSTICO
			
		.append("from \n")
		.append("MbcAgendas agd \n")
		.append("join agd.paciente pac \n")
		.append("join agd.procedimentoCirurgico pci \n")
		.append("join agd.especialidade esp \n")
		.append("join agd.unidadeFuncional unf \n")
		.append("left join agd.agendasHemoterapias ahe \n")
		.append("left join agd.agendasDiagnosticos adi \n")
		.append("left join ahe.absComponenteSanguineo csa \n")
		.append("left join adi.aghCid cid \n")
		
		.append("WHERE \n")
		
		.append("agd." ).append( MbcAgendas.Fields.IND_SITUACAO.toString() ).append( " IN (:indSituacoes) \n")
		
		.append("and agd." ).append( MbcAgendas.Fields.IND_EXCLUSAO.toString() ).append( " =  :indExclusao \n")
		
		.append("and pac.").append(AipPacientes.Fields.CODIGO.toString() ).append( " = :codigoPaciente \n")
		.append("and esp.").append(AghEspecialidades.Fields.SEQ.toString()	 ).append( " = :seqEspecialidade \n")
		.append("and agd.").append(MbcAgendas.Fields.DT_AGENDA.toString()).append( " = :dtAgendaCirurgia \n")
		
		.append("and agd.").append(MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString() ).append( '.' ).append( MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString() ).append( " = :pucSerMatricula \n")
		.append("and agd.").append(MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString() ).append( '.' ).append( MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString() ).append( " = :pucSerVinCodigo \n")
		.append("and agd.").append(MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString() ).append( '.' ).append( MbcProfAtuaUnidCirgs.Fields.UNF_SEQ.toString() ).append( " = :pucUnfSeq \n")
		.append("and agd.").append(MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS.toString() ).append( '.' ).append( MbcProfAtuaUnidCirgs.Fields.IND_FUNCAO_PROF.toString() ).append( " = :pucIndFuncaoProf \n")
		
		.append("and agd.").append(MbcAgendas.Fields.UNF_SEQ.toString()).append( " = :unfSeq \n");
		
		Query q = createHibernateQuery(hql.toString());
		q.setParameter("indExclusao", Boolean.FALSE);
		q.setParameterList("indSituacoes", Arrays.asList(DominioSituacaoAgendas.ES,DominioSituacaoAgendas.AG ));
		q.setInteger("codigoPaciente", pacCodigo);
		q.setShort("seqEspecialidade", seqEspecialidade);
		q.setDate("dtAgendaCirurgia", dataCirurgia);
		
		q.setInteger("pucSerMatricula", pucSerMatricula);
		q.setShort("pucSerVinCodigo", pucSerVinCodigo);
		q.setShort("pucUnfSeq", pucUnfSeq);
		q.setParameter("pucIndFuncaoProf", pucIndFuncaoProf);

		q.setShort("unfSeq", pucUnfSeq);
		
		q.setResultTransformer(Transformers.aliasToBean(PlanejamentoCirurgicoVO.class));
		
		return q.list();
	}
	
	public Set<MbcAgendaHemoterapia> listarAgendasHemoterapiaPorAgendaSeq(final Integer agendaSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaHemoterapia.class, "agh");
		criteria.createAlias("agh." + MbcAgendaHemoterapia.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "abs", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MbcAgendaHemoterapia.Fields.AGD_SEQ.toString(), agendaSeq));
		List<MbcAgendaHemoterapia> resultado = executeCriteria(criteria);
		return new HashSet<MbcAgendaHemoterapia>(resultado);
	}
}
