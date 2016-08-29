package br.gov.mec.aghu.transplante.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AgendaPacienteTransplanteQueryBuilder extends QueryBuilder<DetachedCriteria>{

	private static final String ESP = "ESP.";

	private static final String RET = "RET.";

	private static final String TRP = "TRP.";

	private static final String CON = "CON.";

	private static final String PAC = "PAC.";

	private static final long serialVersionUID = -5951840331051545730L;
	
	private DetachedCriteria criteria;
	private Integer codPaciente;
	private List<AghEspecialidades> listaEspecialidade;
	private DominioTipoRetorno tipoRetorno;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MtxTransplantes.class, "TRP");	
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFrom();
		setWhere();
		setProjecao();
	}
	
	private void setProjecao() {
		String contato = "(SELECT CONT.NOME || ' [-] ' || CONT.TELEFONE " 
		+"         FROM AGH.MTX_CONTATO_PACIENTES CONT "
		+"         WHERE CONT.pac_codigo = pac1_.codigo "
		+"           and CONT.seq = ( SELECT MIN(AUX.seq) "
		+"        		   			FROM AGH.MTX_CONTATO_PACIENTES AUX "
		+"		   					WHERE AUX.pac_codigo = CONT.pac_codigo)) AS contato ";
		
		String ultimaOcorrenciaTransp = "(SELECT ext.DATA_OCORRENCIA "
		+"         FROM AGH.MTX_EXTRATO_TRANSPLANTES ext "
		+"         WHERE ext.TRP_SEQ = this_.SEQ "
		+"         	AND ext.SEQ = (SELECT MAX(aux.SEQ) "
		+"                          FROM AGH.MTX_EXTRATO_TRANSPLANTES aux "
		+"                          WHERE aux.TRP_SEQ = ext.TRP_SEQ "
		+"                            AND aux.SITUACAO_TRANSPLANTE = '"+ DominioSituacaoTransplante.T.toString() +"')) AS dataOcorrenciaTransplante ";
		
            
		ProjectionList projection = Projections.projectionList();
		projection
			.add(Projections.property(PAC+AipPacientes.Fields.CODIGO.toString()), AgendaTransplanteRetornoVO.Fields.COD_PACIENTE.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.PRONTUARIO.toString()), AgendaTransplanteRetornoVO.Fields.PRONTUARIO.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.NOME.toString()), AgendaTransplanteRetornoVO.Fields.NOME_PACIENTE.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.DDD_FONE_RESIDENCIAL.toString()), AgendaTransplanteRetornoVO.Fields.DDD_FONE_RESIDENCIAL.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.FONE_RESIDENCIAL.toString()), AgendaTransplanteRetornoVO.Fields.FONE_RESIDENCIAL.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.DDD_FONE_RECADO.toString()), AgendaTransplanteRetornoVO.Fields.DDD_FONE_RECADO.toString())
			.add(Projections.property(PAC+AipPacientes.Fields.FONE_RECADO.toString()), AgendaTransplanteRetornoVO.Fields.FONE_RECADO.toString())
			.add(Projections.property(CON+AacConsultas.Fields.DATA_CONSULTA.toString()), AgendaTransplanteRetornoVO.Fields.DATA_CONSULTA.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.DATA_INGRESSO.toString()), AgendaTransplanteRetornoVO.Fields.DATA_INGRESSO.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.DATA_ULTIMO_TRANSPLANTE.toString()), AgendaTransplanteRetornoVO.Fields.DATA_ULTIMO_TRANSPLANTE.toString())
			.add(Projections.property(RET+AacRetornos.Fields.DESCRICAO.toString()), AgendaTransplanteRetornoVO.Fields.DESCRICAO_RETORNO.toString())
			.add(Projections.property(RET+AacRetornos.Fields.IND_AUSENTE_AMBU.toString()), AgendaTransplanteRetornoVO.Fields.IND_AUSENTE.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.TIPO_TMO.toString()), AgendaTransplanteRetornoVO.Fields.TIPO_TMO.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.TIPO_ALOGENICO.toString()), AgendaTransplanteRetornoVO.Fields.TIPO_ALOGENICO.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.TIPO_ORGAO.toString()), AgendaTransplanteRetornoVO.Fields.TIPO_ORGAO.toString())
			.add(Projections.property(ESP+AghEspecialidades.Fields.SEQ.toString()), AgendaTransplanteRetornoVO.Fields.SEQ_ESPECIALIDADE.toString())
			.add(Projections.property(ESP+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AgendaTransplanteRetornoVO.Fields.NOME_ESPECIALIDADE.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.SEQ.toString()), AgendaTransplanteRetornoVO.Fields.SEQ_TRANSPLANTE.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.OBSERVACOES.toString()), AgendaTransplanteRetornoVO.Fields.OBSERVACAO_TRANSPLANTE.toString())
			.add(Projections.property(TRP+MtxTransplantes.Fields.SITUACAO.toString()), AgendaTransplanteRetornoVO.Fields.SITUACAO.toString())
			.add(Projections.sqlProjection(contato, new String [] {AgendaTransplanteRetornoVO.Fields.CONTATO.toString()}, new Type[] {new StringType()}))
			.add(Projections.sqlProjection(ultimaOcorrenciaTransp, new String [] {AgendaTransplanteRetornoVO.Fields.DATA_OCORRENCIA_TRANSPLANTE.toString()}, new Type[] {new DateType()}));
			
		this.criteria.setProjection(Projections.distinct(projection));
		
		this.criteria.setResultTransformer(Transformers.aliasToBean(AgendaTransplanteRetornoVO.class));
	}

	private void setWhere() {
		this.criteria.add(Restrictions.eq(RET+AacRetornos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		this.criteria.add(Restrictions.eq("GAC."+AacGradeAgendamenConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (this.codPaciente != null){
			this.criteria.add(Restrictions.eq(PAC+AipPacientes.Fields.CODIGO.toString(), this.codPaciente));
		}
		
		if (this.listaEspecialidade != null && this.listaEspecialidade.size() > 0){
			List<Short> listaId = new ArrayList<Short>();

			for (AghEspecialidades aghEspecialidades : listaEspecialidade) {
				listaId.add(aghEspecialidades.getSeq());
			}

			this.criteria.add(Restrictions.in(ESP+AghEspecialidades.Fields.SEQ.toString(), listaId.toArray()));
		}
		
		if (this.tipoRetorno == DominioTipoRetorno.A){
			this.criteria.add(Restrictions.eq(TRP + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.E));
		} else if (this.tipoRetorno == DominioTipoRetorno.D){
			this.criteria.add(Restrictions.eq(TRP + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.T));
		} else  if (this.tipoRetorno == DominioTipoRetorno.X){
			this.criteria.add(Restrictions.in(TRP + MtxTransplantes.Fields.SITUACAO.toString(), new Object[] { DominioSituacaoTransplante.E, DominioSituacaoTransplante.T }));
		}
		
		this.criteria.addOrder(Order.asc(CON + AacConsultas.Fields.DATA_CONSULTA.toString()));
		this.criteria.addOrder(Order.asc(PAC + AipPacientes.Fields.NOME.toString()));
	}

	private void setFrom() {
		this.criteria.createAlias(TRP + MtxTransplantes.Fields.RECEPTOR.toString(), "PAC");
		this.criteria.createAlias(PAC + AipPacientes.Fields.AAC_CONSULTAS.toString(), "CON");
		this.criteria.createAlias(CON + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC");
		this.criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		this.criteria.createAlias(CON + AacConsultas.Fields.RETORNO.toString(), "RET");
	}

	public DetachedCriteria build(Integer codPaciente, List<AghEspecialidades> listaEspecialidade, DominioTipoRetorno tipoRetorno) {
		this.codPaciente = codPaciente;
		this.listaEspecialidade = listaEspecialidade;
		this.tipoRetorno = tipoRetorno;

		return super.build();
	}
}
