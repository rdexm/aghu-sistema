package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.model.AelDifAgendaPaciente;

/**
 * Created by renan_boni on 03/03/15.
 */
public class AelDifAgendaPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDifAgendaPaciente> {

    private static final long serialVersionUID = 2480356656615592590L;

    /**
     * Pesquisa por horarios que ja tenham sido utilizados mas permitam agendamente de exame pertencente ao mesmo grupo
     * do exame ocupante da grade.
     * */
    @SuppressWarnings("unchecked")
    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosDisponiveisAgendamentoConcorrente(Integer codigoPaciente,
                                                                                            String siglaExame,
                                                                                            Integer seqMaterial) {
        StringBuffer hql = new StringBuffer(1200);
        hql.append(" select new  br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO ( ")
                .append("  difAgendaPaciente.id.seqp ")
                .append("  difAgendaPaciente.id.dataHoraAgenda")
                .append("  difAgendaPaciente.id.codigoPaciente ")
                .append("  difAgendaPaciente.id.unidadeFuncionalSeq ")
                .append("  where ")
                .append("  from AelDifAgendaPaciente difAgendaPaciente ")
                .append("  where ")
                .append("     ( ")
                .append("       difAgendaPaciente.id.codigoPaciente = :codigoPaciente ")
                .append("       and exists ")
                .append("       ( ")
                .append("         select 1 from ")
                .append("          VAelHrGradeDisp gradeHorariosDisponiveis ")
                .append("         where ")
                .append("           gradeHorariosDisponiveis.id.grade = difAgendaPaciente.id.unidadeFuncionalSeq ")
                .append("           and gradeHorariosDisponiveis.id.seqGrade =  difAgendaPaciente.id.seqp ")
                .append("           and gradeHorariosDisponiveis.id.dthrAgenda = difAgendaPaciente.id.dataHoraAgenda ")
                .append("           and gradeHorariosDisponiveis.id.indAgendaExMesmoHor = 'S' ")
                .append("           and gradeHorariosDisponiveis.id.situacaoHorario = :paramEnumSituacaoHorario ")
                .append("           and gradeHorariosDisponiveis.id.siglaExame = :sigla ")
                .append("           and gradeHorariosDisponiveis.id.matExame = :matExame ")
                .append("           and gradeHorariosDisponiveis.id.unfExame = :unidadeFuncional ")
                .append("      ) ")
                .append("     ) ") ;

        Query query = createHibernateQuery(hql.toString());
        query.setParameter("codigoPaciente", codigoPaciente);
        query.setParameter("paramEnumSituacaoHorario", DominioSituacaoHorario.M);
        query.setParameter("sigla", siglaExame);
        query.setParameter("matExame", seqMaterial);

        return  query.list();
    }
}
