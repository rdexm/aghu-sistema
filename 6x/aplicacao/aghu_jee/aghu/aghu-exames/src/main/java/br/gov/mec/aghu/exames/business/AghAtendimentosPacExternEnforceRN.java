package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Enforce da entidade AghAtendimentosPacExtern.<br>
 * 
 * ORADB PROCEDURE AGHP_ENFORCE_APE_RULES.<br>
 * 
 * @author rcorvalao
 *
 */
@Stateless
public class AghAtendimentosPacExternEnforceRN extends BaseBusiness {

	@EJB
	private AghAtendimentosPacExternRN aghAtendimentosPacExternRN;
	
	private static final Log LOG = LogFactory.getLog(AghAtendimentosPacExternEnforceRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1149735311885890020L;

	/**
	 * if p_event = 'INSERT' then.<br>
	 * 
	 * @param oldEntity
	 * @param newEntity
	 */
	public void insert(AghAtendimentosPacExtern newEntity, String nomeMicrocomputador) throws BaseException {
		/* -- RN_APE004 Inserir atendimento
	    aghk_atd_rn.rn_atdp_atu_ins_atd (l_ape_row_new.pac_codigo,
	                                         l_ape_row_new.criado_em,
	                                         null, null, null, null
	                                         , v_numero, v_numero,
	                                         v_unf_seq,
	                                         null,
	                                         l_ape_row_new.ser_matricula,
	                                         l_ape_row_new.ser_vin_codigo,
	                                         null,
	                                         l_ape_row_new.ser_matricula_digitado,
	                                         l_ape_row_new.ser_vin_codigo_digitado,
	                                         null, null, null,
	                                         l_ape_row_new.seq,
	                                         null, null); */
		InclusaoAtendimentoVO inclusaoAtendimento =
		this.getPacienteFacade().incluirAtendimento(newEntity.getPaciente().getCodigo()
				, newEntity.getCriadoEm()
				, null, null, null, null
				, null, null
				, newEntity.getUnidadeFuncional().getSeq()
				, null
				, newEntity.getServidor()
				, null
				, newEntity.getServidorDigitado()
				, null, null, null
				, newEntity.getSeq()
				, null, null, nomeMicrocomputador);
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(inclusaoAtendimento.getAtendimentoSeq());
		newEntity.getAtendimentos().add(atendimento);
	    // Nao migrado, pois estava comentado no codigo legado.
	    //-- RN_APE004 Inserir  conta hospitalar e conta atendimento
	    /* aghk_ape_rn.rn_apep_atu_conta (l_ape_row_new.criado_em,
	                                         l_ape_row_new.csp_cnv_codigo,
	                                         l_ape_row_new.csp_seq,
	                                         l_ape_row_new.ser_matricula_digitado,
	                                         l_ape_row_new.ser_vin_codigo_digitado,
	                                         l_ape_row_new.ser_matricula,
	                                         l_ape_row_new.ser_vin_codigo,
	                                         l_ape_row_new.seq);
	    */
	}
	
	/**
	 * if p_event = 'UPDATE' then.<br>
	 * 
	 * @param oldEntity
	 * @param newEntity
	 * @throws BaseException 
	 */
	public void update(AghAtendimentosPacExtern oldEntity, AghAtendimentosPacExtern newEntity, String nomeMicrocomputador) throws BaseException {
		if (CoreUtil.modificados(oldEntity.getConvenioSaudePlano(), newEntity.getConvenioSaudePlano())) {
			// Convênio deve estar ativo.
			this.getAghAtendimentosPacExternRN().verificarConvenioAtivo(newEntity.getConvenioSaudePlano());
			
			// Ao atualizar convênio, atualizar a solicitação de exames.
			this.getAghAtendimentosPacExternRN().atualizarConvenio(newEntity.getConvenioSaudePlano(), newEntity, nomeMicrocomputador);		
		}
		
		if (CoreUtil.modificados(oldEntity.getServidor(), newEntity.getServidor())) {
	        // O servidor responsável, quando houver, deve pertencer
	        // a um conselho profissional que permita solicitar exame.
			this.getAghAtendimentosPacExternRN().verificarConselhoProfissional(newEntity.getServidor());
		}
		
		if (CoreUtil.modificados(oldEntity.getPaciente(), newEntity.getPaciente())
				|| CoreUtil.modificados(oldEntity.getUnidadeFuncional(), newEntity.getUnidadeFuncional())
				|| CoreUtil.modificados(oldEntity.getServidor(), newEntity.getServidor()) ) {
			// Ao atualizar atendimento externo, atualizar o atendimento.
			this.getAghAtendimentosPacExternRN().atualizarAtendimento(newEntity, nomeMicrocomputador);
		}
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected AghAtendimentosPacExternRN getAghAtendimentosPacExternRN() {
		return aghAtendimentosPacExternRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
