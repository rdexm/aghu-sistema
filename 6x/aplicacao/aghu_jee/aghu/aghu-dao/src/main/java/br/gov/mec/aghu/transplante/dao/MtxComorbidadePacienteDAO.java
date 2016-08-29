package br.gov.mec.aghu.transplante.dao;

import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxComorbidadePaciente;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadeDAO.TipoCriteria;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class MtxComorbidadePacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxComorbidadePaciente> {
	
	private static final long serialVersionUID = -8683838968580496879L;

	public void excluirComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente) throws ApplicationBusinessException, BaseException{
		remover(pesquisarMtxComorbidadePaciente(mtxComorbidadePaciente.getPacCodigo(), mtxComorbidadePaciente.getCmbSeq()));
		flush();
	}
	
	public void gravarComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente) throws ApplicationBusinessException, BaseException{
		persistir(mtxComorbidadePaciente);
		flush();
	}
	
	public MtxComorbidadePaciente pesquisarMtxComorbidadePaciente(AipPacientes aipPacientes, MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidadePaciente.class);
		TipoCriteria.EQ.addRestriction(criteria, MtxComorbidadePaciente.Fields.AIP_PACIENTE.toString(), aipPacientes);
		TipoCriteria.EQ.addRestriction(criteria, MtxComorbidadePaciente.Fields.CMB_SEQ.toString(), mtxComorbidade);
		return (MtxComorbidadePaciente) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MtxComorbidadePaciente> carregarComorbidadesPaciente(DominioTipoTransplante tipo, AipPacientes aipPacientes){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidadePaciente.class, "CP");
		criteria.createAlias("CP."+MtxComorbidadePaciente.Fields.CMB_SEQ.toString(), "CMB", JoinType.INNER_JOIN);
		criteria.createAlias("CP."+MtxComorbidadePaciente.Fields.AIP_PACIENTE.toString(), "P", JoinType.INNER_JOIN);
		criteria.createAlias("CMB."+MtxComorbidade.Fields.CID_SEQ.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.or(Restrictions.eq("CMB."+MtxComorbidade.Fields.TIPO.toString(), tipo),
				Restrictions.eq("CMB."+MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.X)));
		
		TipoCriteria.EQ.addRestriction(criteria, "CMB."+MtxComorbidade.Fields.SITUACAO.toString(), DominioSituacao.A);
		TipoCriteria.EQ.addRestriction(criteria, "CP."+MtxComorbidadePaciente.Fields.AIP_PACIENTE.toString(), aipPacientes);
		criteria.addOrder(Order.asc("CMB."+MtxComorbidade.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("CID."+AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
}
