package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaApacVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaHistoricoVO;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaOutraVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FatAtendimentoApacProcHospDAO extends BaseDao<FatAtendimentoApacProcHosp> {

	private static final long serialVersionUID = -2517218093672337699L;

	/**
	 * Realiza a busca de Atendimentos com indicador de prioridade Principal, por código e data de atendimento.
	 * 
	 * @param seqAtendimento - Código do Atendimento
	 * @param dataAtendimento - Data do Atendimento
	 * @return Lista de Atendimentos com prioridade Principal
	 */
	public List<FatAtendimentoApacProcHosp> pesquisarAtendimentoPrincipalPorDataCodigo(Integer seqAtendimento, Date dataAtendimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatAtendimentoApacProcHosp.class, "AAP");

		criteria.createAlias("AAP." + FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString(), "ATM");
		criteria.createAlias("ATM." + AacAtendimentoApacs.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.eq("AAP." + FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString(), DominioPrioridadeCid.P));
		criteria.add(Restrictions.le("ATM." + AacAtendimentoApacs.Fields.DT_INICIO.toString(), dataAtendimento));
		criteria.add(Restrictions.or(
				Restrictions.ge("ATM." + AacAtendimentoApacs.Fields.DT_FIM.toString(), dataAtendimento),
				Restrictions.isNull("ATM." + AacAtendimentoApacs.Fields.DT_FIM.toString())));

		return executeCriteria(criteria);
	}

	/**
	 * Verifica se existe ao menos um registro com os códigos informados.
	 * 
	 * @param phiSeq - Código do Procedimento Interno
	 * @param codigoPaciente - Código do Paciente
	 * @return Flag indicando a existência do registro solicitado
	 */
	public DominioSimNao verificarExistenciaProcedimentoAtendimento(Integer phiSeq, Integer codigoPaciente) {

		DominioSimNao retorno = DominioSimNao.S;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAtendimentoApacProcHosp.class, "AAP");

		criteria.add(Restrictions.eq("AAP." + FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString(), DominioPrioridadeCid.P));
		criteria.add(Restrictions.eq("AAP." + FatAtendimentoApacProcHosp.Fields.PHI_SEQ.toString(), phiSeq));

		DetachedCriteria criteriaCap = DetachedCriteria.forClass(FatContaApac.class, "CAP");
		
		criteriaCap.setProjection(Projections.property("CAP." + FatContaApac.Fields.ATM_CODIGO.toString()));

		criteriaCap.createAlias("CAP." + FatContaApac.Fields.PACIENTE.toString(), "PAC");
		
		criteriaCap.add(Restrictions.eq("CAP." + FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.A));
		criteriaCap.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		
		DetachedCriteria criteriaCpe = DetachedCriteria.forClass(FatCompetencia.class, "CPE");

		ProjectionList projecaoCpe = Projections.projectionList();
		projecaoCpe.add(Projections.property("CPE." + FatCompetencia.Fields.MODULO.toString()));
		projecaoCpe.add(Projections.property("CPE." + FatCompetencia.Fields.MES.toString()));
		projecaoCpe.add(Projections.property("CPE." + FatCompetencia.Fields.ANO.toString()));
		criteriaCpe.setProjection(projecaoCpe);
		
		criteriaCpe.add(Restrictions.eq("CPE." + FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));
		criteriaCpe.add(Restrictions.eq("CPE." + FatCompetencia.Fields.MODULO.toString(), DominioModuloCompetencia.APAT));
		
		criteriaCap.add(Subqueries.propertiesEq(new String[] {"CAP." + FatContaApac.Fields.CPE_MODULO.toString(),
				"CAP." + FatContaApac.Fields.CPE_MES.toString(), "CAP." + FatContaApac.Fields.CPE_ANO.toString()}, criteriaCpe));
		
		criteria.add(Subqueries.propertyEq("AAP." + FatAtendimentoApacProcHosp.Fields.ATM_NUMERO.toString(), criteriaCap));
		
		if (executeCriteriaExists(criteria)) {
			retorno = DominioSimNao.N;
		}
		
		return retorno;
	}
	
	/**
	 * Método para obter CursorBuscaOutraVO
	 * #42229 P4 - Cursor c_busca_outra
	 * @param cAtmNumero
	 * @param cDtInicioValidade
	 * @param cCapType
	 * @param cPacCodigo
	 * @param cPhiSeq
	 * @return
	 */
	public List<CursorBuscaOutraVO> obterListaCursorCBuscaOutra(Long cAtmNumero, Date cDtInicioValidade, 
			String cCapType, Integer cPacCodigo, Integer cPhiSeq){
		
		CursorCBuscaOutraQueryBuilder builder = new CursorCBuscaOutraQueryBuilder();
		return executeCriteria(builder.build(cAtmNumero, cDtInicioValidade, cCapType, cPacCodigo, cPhiSeq));
	}
	
	/**
	 * Método para obter CursorBuscaOutraVO
	 * #42229 P4 - Cursor c_busca_historico
	 * @param cAtmNumero
	 * @param cDtInicioValidade
	 * @param cCapType
	 * @param cPacCodigo
	 * @param cPhiSeq
	 * @return
	 */
	public List<CursorBuscaHistoricoVO> obterListaCursorBuscaHistorico(Long cAtmNumero, Date cDtInicioValidade, 
			String cCapType, Integer cPacCodigo, Integer cPhiSeq){
		
		CursorCBuscaHistoricoQueryBuilder builder = new CursorCBuscaHistoricoQueryBuilder();
		return executeCriteria(builder.build(cAtmNumero, cDtInicioValidade, cCapType, cPacCodigo, cPhiSeq));
	}
	
	/**
	 * Método para obter CursorBuscaOutraVO
	 * #42229 P4 - Cursor c_busca_apac
	 * @param cPacCodigo
	 * @param indTipoTratamento
	 * @return
	 */
	public List<CursorBuscaApacVO> obterListaCursorBuscaApacVOs(Integer cPacCodigo, Byte indTipoTratamento){
		
		CursorCBuscaApacQueryBuilder builder = new CursorCBuscaApacQueryBuilder();
		return executeCriteria(builder.build(cPacCodigo, indTipoTratamento));
	}
}
