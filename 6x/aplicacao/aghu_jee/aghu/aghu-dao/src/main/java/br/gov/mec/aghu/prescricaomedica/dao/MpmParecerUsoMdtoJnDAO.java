package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.MpmParecerUsoMdtoJn;
import br.gov.mec.aghu.model.MpmTipoParecerUsoMdto;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.prescricaomedica.vo.HistoricoParecerMedicamentosJnVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MpmParecerUsoMdtoJnDAO extends BaseDao<MpmParecerUsoMdtoJn>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868681898818133028L;
	private static final String PJN = "PJN";
	private static final String PJN_PONTO = "PJN.";
	
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
	 * #45271 - HISTORICO PARECER MEDICAMENTOS
	 * 
	 */
	public List<HistoricoParecerMedicamentosJnVO> obterHistoricoParecerMedicamentos(BigDecimal parecerSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmParecerUsoMdtoJn.class,PJN);
		criteria.createAlias(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.MPM_PARECER_USO_MDTO.toString(), "PUM");
		criteria.createAlias(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.MPM_TIPO_PARECER_USO_MDTO.toString(), "TPM");
		obterCriteriaVinculosServidor(PJN_PONTO, criteria);
		criteria.createAlias(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.USUARIO.toString(), "Servidor1");
		criteria.createAlias("Servidor1." + RapServidores.Fields.PESSOA_FISICA.toString(), "Pesssoa1");
		criteria.add(Restrictions.eq("PUM." + MpmParecerUsoMdto.Fields.SEQ.toString(), parecerSeq));
		criteria.addOrder(Order.desc(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.JN_DATE_TIME.toString()));
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.JN_DATE_TIME.toString()), HistoricoParecerMedicamentosJnVO.Fields.DATA_ATUALIZACAO.toString());
		projection.add(Projections.property("Pesssoa1." + RapPessoasFisicas.Fields.NOME.toString()), HistoricoParecerMedicamentosJnVO.Fields.RESPONSAVEL.toString());
		projection.add(Projections.property(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.DTHR_PARECER.toString()), HistoricoParecerMedicamentosJnVO.Fields.DATA_PARECER.toString());
		projection.add(Projections.property("TPM." + MpmTipoParecerUsoMdto.Fields.DESCRICAO.toString()), HistoricoParecerMedicamentosJnVO.Fields.PARECER.toString());
		projection.add(Projections.property(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.OBSERVACAO.toString()), HistoricoParecerMedicamentosJnVO.Fields.OBSERVACAO.toString());
		projection.add(Projections.property("RQA." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()), HistoricoParecerMedicamentosJnVO.Fields.NUMERO_REGISTRO.toString());
		projection.add(Projections.property("RPF." + RapPessoasFisicas.Fields.NOME.toString()), HistoricoParecerMedicamentosJnVO.Fields.NOME_USUARIO.toString());
		projection.add(Projections.property("RCP." + RapConselhosProfissionais.Fields.SIGLA.toString()), HistoricoParecerMedicamentosJnVO.Fields.SIGLA_REGISTRO.toString());
		projection.add(Projections.property(PJN_PONTO + MpmParecerUsoMdtoJn.Fields.JN_OPERATION.toString()), HistoricoParecerMedicamentosJnVO.Fields.OPERACAO.toString());
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(HistoricoParecerMedicamentosJnVO.class));
		
		return executeCriteria(criteria);
	}

}
