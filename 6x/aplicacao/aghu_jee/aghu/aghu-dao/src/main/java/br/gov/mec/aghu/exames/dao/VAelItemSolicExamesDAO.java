package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.exames.questionario.vo.InformacaoComplementarVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelItemSolicExames;
import br.gov.mec.aghu.model.VAelSolicAtends;


public class VAelItemSolicExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelItemSolicExames> {

	private static final long serialVersionUID = -2783626150839151705L;

	public List<InformacaoComplementarVO> pesquisarInformacoesComplementares(Integer pacCodigo, Integer soeSeq, Short seqp, Integer qtnSeq){
		StringBuffer hql = new StringBuffer(5000);
		hql.append("select distinct new br.gov.mec.aghu.exames.questionario.vo.InformacaoComplementarVO(");
		hql.append("'IDENTIFICAÇÃO DO PACIENTE'");
		hql.append(",vise."+VAelItemSolicExames.Fields.SOE_SEQ.toString()+" ");
		hql.append(",cnv."+FatConvenioSaude.Fields.DESCRICAO.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.NOME.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.SEXO.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.NOMEMAE.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.FONE_RESIDENCIAL.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.FONE_RECADO.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.PRONTUARIO.toString()+" ");
		hql.append(",unf1."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()+" ");
		hql.append(",exa."+AelExames.Fields.DESCRICAO.toString()+" ");
		hql.append(",vise."+VAelItemSolicExames.Fields.SEQP.toString()+" ");
		hql.append(",eqe."+AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString()+" ");
		hql.append(",eqe."+AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString()+" ");
		hql.append(",eqe."+AelExamesQuestionario.Fields.QTN_SEQ.toString()+" ");
		hql.append(",qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString()+" ");
		hql.append(",agq."+AelGrupoQuestao.Fields.SEQ.toString()+" ");
		hql.append(",agq."+AelGrupoQuestao.Fields.DESCRICAO.toString()+" ");
		hql.append(",qao."+AelQuestao.Fields.SEQ.toString()+ " ");
		hql.append(",qao."+AelQuestao.Fields.DESCRICAO.toString()+ " ");
		hql.append(",pac."+AipPacientes.Fields.RG.toString()+" ");
		hql.append(",pac."+AipPacientes.Fields.NUMERO_CARTAO_SAUDE.toString()+" ");
		hql.append(",vise."+VAelItemSolicExames.Fields.SERVIDOR_RESPONSAVEL.toString()+"."+RapServidores.Fields.CODIGO_VINCULO.toString());
		hql.append(",vise."+VAelItemSolicExames.Fields.SERVIDOR_RESPONSAVEL.toString()+"."+RapServidores.Fields.MATRICULA.toString());
		hql.append(",vise."+VAelItemSolicExames.Fields.ATD_SEQ.toString()+" ");
		
		hql.append(",cnv."+FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()+" ");
		hql.append(",unf1."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()+" ");
		hql.append(",unf2."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()+" ");
		hql.append(",vise."+VAelItemSolicExames.Fields.INFORMACOES_CLINICAS.toString()+") ");
		
		
		hql.append(" from VAelItemSolicExames vise left join vise.servidorResponsavel");
		hql.append(" ,VAelSolicAtends vas ");
		hql.append(" ,FatConvenioSaude cnv ");
		hql.append(" ,AipPacientes pac ");
		hql.append(" ,AghUnidadesFuncionais unf1 ");
		hql.append(" ,AghUnidadesFuncionais unf2 ");
		hql.append(" ,AelExames exa ");
		hql.append(" ,AelExamesQuestionario eqe ");
		hql.append(" ,AelQuestionarios qtn1 ");
		hql.append(" ,AelQuestionarios qtn ");
		hql.append(" ,AelQuestao qao ");
		hql.append(" ,AelQuestoesQuestionario qqu left join qqu.aelGrupoQuestao agq");
		
		hql.append(" where vas."+VAelSolicAtends.Fields.SEQ.toString()+" = vise."+VAelItemSolicExames.Fields.SOE_SEQ.toString());
		hql.append(" and pac."+AipPacientes.Fields.CODIGO.toString()+" = vas."+VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString());
		hql.append(" and cnv."+FatConvenioSaude.Fields.CODIGO.toString()+" = vas."+VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString());
		hql.append(" and pac."+AipPacientes.Fields.CODIGO.toString()+" = :pacCodigo");
		hql.append(" and unf1."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()+" = vas."+VAelSolicAtends.Fields.ATD_UNF_SEQ.toString());
		hql.append(" and exa."+AelExames.Fields.SIGLA.toString()+" = vise."+VAelItemSolicExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" and unf2."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()+" = vise."+VAelItemSolicExames.Fields.UFE_UNF_SEQ.toString());
		hql.append(" and eqe."+AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString()+" = vise."+VAelItemSolicExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" and eqe."+AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString()+" = vise."+VAelItemSolicExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" and qtn1."+AelQuestionarios.Fields.SEQ.toString()+" = eqe."+AelExamesQuestionario.Fields.QTN_SEQ.toString());
		hql.append(" and qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString()+" = eqe."+AelExamesQuestionario.Fields.QTN_SEQ.toString());
		hql.append(" and qtn."+AelQuestionarios.Fields.SEQ.toString()+" = qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString());
		hql.append(" and qao."+AelQuestao.Fields.SEQ.toString()+" = qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTAO.toString());
		hql.append(" and (vise."+VAelItemSolicExames.Fields.SEQ.toString()+" = :soeSeq");
		hql.append(" and vise."+VAelItemSolicExames.Fields.SEQP.toString()+" = :seqp)");
		hql.append(" and eqe."+AelExamesQuestionario.Fields.QTN_SEQ.toString()+" = :qtnSeq ");
		
		hql.append(" and (qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTIONARIO.toString()+", qqu."+AelQuestoesQuestionario.Fields.SEQ_QUESTAO.toString()+") in");
		hql.append(" (select rqu."+AelRespostaQuestao.Fields.QQU_QTN_SEQ.toString()+","+AelRespostaQuestao.Fields.QQU_QAO_SEQ.toString());
		hql.append(" from AelRespostaQuestao rqu");
		hql.append(" where (rqu."+AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString()+"= :soeSeq");
		hql.append(" and rqu."+AelRespostaQuestao.Fields.ISE_SEQP.toString()+"= :seqp ");
		hql.append(" and rqu."+AelRespostaQuestao.Fields.EQE_EMA_EXA_SIGLA.toString()+"=vise."+VAelItemSolicExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		hql.append(" and rqu."+AelRespostaQuestao.Fields.EQE_EMA_MAN_SEQ.toString()+"=vise."+VAelItemSolicExames.Fields.UFE_EMA_MAN_SEQ.toString());
		hql.append(" and rqu."+AelRespostaQuestao.Fields.EQE_QTN_SEQ.toString()+"= :qtnSeq ))");
		
		hql.append(" order by pac."+AipPacientes.Fields.NOME.toString()+", ");
		hql.append(" eqe."+AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString()+", ");
		hql.append(" eqe."+AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString()+", ");
		hql.append(" eqe."+AelExamesQuestionario.Fields.QTN_SEQ.toString()+", ");
		hql.append(" agq."+AelGrupoQuestao.Fields.SEQ.toString()+", ");
		hql.append(" agq."+AelGrupoQuestao.Fields.DESCRICAO.toString()+", ");
		hql.append(" qao."+AelQuestao.Fields.SEQ.toString()+" ");
		
		
		Query query = createHibernateQuery(hql.toString());
		if (pacCodigo != null) {
			query.setParameter("pacCodigo", pacCodigo);
		}
		if (soeSeq != null) {
			query.setParameter("soeSeq", soeSeq);
		}
		if (seqp != null) {
			query.setParameter("seqp", seqp);
		}
		if (qtnSeq != null) {
			query.setParameter("qtnSeq", qtnSeq);
		}
		List<InformacaoComplementarVO> lista = query.list();
		return lista;
	}

}
