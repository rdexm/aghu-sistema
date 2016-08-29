package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoRarCandidatoPrograma;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RarCandidatoPrograma;
import br.gov.mec.aghu.model.RarCandidatos;
import br.gov.mec.aghu.model.RarPrograma;
import br.gov.mec.aghu.registrocolaborador.vo.ProgramaEspecialidadeVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class RarProgramaDAO extends BaseDao<RarPrograma>{

	private static final long serialVersionUID = -934836975324193585L;
	
	
	public ProgramaEspecialidadeVO obterNomeProgramaNomeEspecialidade(Integer matricula, Short vinculo){
		ProgramaEspecialidadeVO programaEspecialidadeVO = null;
		List<ProgramaEspecialidadeVO> retornoLista;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RarPrograma.class, "PGA");
		
		criteria.createAlias("PGA." + RarPrograma.Fields.AGH_ESPECIALIDADES.toString(),"ESP");
		criteria.createAlias("PGA." + RarPrograma.Fields.RAR_CANDIDATOS_PROGRAMAS_FOR_PGA_SEQ.toString(),"CPM");
		criteria.createAlias("CPM." + RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString(),"CDN");
		criteria.createAlias("CDN." + RarCandidatos.Fields.PESSOA_FISICA.toString(),"PES");
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.SERVIDOR.toString(),"SER");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), ProgramaEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("PGA." + RarPrograma.Fields.NOME.toString()), ProgramaEspecialidadeVO.Fields.NOME_PROGRAMA.toString())
		);
		
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		criteria.add(Restrictions.eq("CPM." + RarCandidatoPrograma.Fields.SITUACAO.toString(), DominioSituacaoRarCandidatoPrograma.A));		
		
		String dataSqlRestriction = "";
		
		if(isOracle()){
			dataSqlRestriction = " sysdate between cpm2_.dt_inicio and nvl(cpm2_.dt_fim, sysdate + 1)";
		}else if(isPostgreSQL()){
			dataSqlRestriction = " now() between cpm2_.dt_inicio and coalesce(cpm2_.dt_fim, now() + interval '1' day ) "; 
		}
		
		criteria.add(Restrictions.sqlRestriction(dataSqlRestriction));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProgramaEspecialidadeVO.class));

		retornoLista = executeCriteria(criteria);
		
		if(retornoLista != null && !retornoLista.isEmpty()){
			programaEspecialidadeVO = retornoLista.get(0);
		}
		
		return programaEspecialidadeVO;

	}
	
}
