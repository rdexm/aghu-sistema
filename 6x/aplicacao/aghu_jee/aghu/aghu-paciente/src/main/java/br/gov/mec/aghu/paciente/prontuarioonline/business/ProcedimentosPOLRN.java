package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ProcedimentosPOLRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ProcedimentosPOLRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	
	private static final long serialVersionUID = 7548907974573797034L;

	/**
	 * RN12 
	 * 
	 * ORADB: C_VER_DESC_CIRURGIA
	 */
	public Boolean verDescCirurgica(Integer crgSeq){
		DominioSituacaoDescricao[] situacao = {DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF};
		Long pdtDescricao = blocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgiaSituacaoCount(crgSeq, situacao);
		
		if(pdtDescricao == 0L){
			
			Long mbcDescricaoCirurgica = blocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgiaSituacaoCount(crgSeq, DominioSituacaoDescricaoCirurgia.CON);
			
			if(mbcDescricaoCirurgica == 0L){
				return false;
			}else{
				return true;
			}
		} else{
			return true;
		}
	}

	/**
	 * RN9 - Verifica se tem imagem
	 * ORADB: FUNCTION c_verifica_imagem
	 * @author Angela Gallassini
	 * @param GLOBAL CG$DDT_SEQ (pDdtSeq)
	 *  passar a variável de sessão "MBCC_VERIFICA_IMAGEM_DDT_SEQ" para o método
	 */
	public Boolean verificarSeTemImagem(Integer ddtSeq) {
		Boolean temImagem = false;

		if(ddtSeq == null){
			return temImagem;
		} else {
			
			Long count = getBlocoCirurgicoProcDiagTerapFacade().verificarSeTemImagem(ddtSeq);
			if (count > 0){
				temImagem = true;
			}
			return temImagem;
		}
	}

	/** RN1
	 * Verifica se escala do portal de agendamento tem cirurgia
	 * 
	 * ORADB: Package MBCC_VER_CIRG_PORTAL
	 * @author Angela Gallassini
	 * @param pAgdSeq, pDtAgenda
	 *  
	*/
	public Boolean verificarSeEscalaPortalAgendamentoTemCirurgia(Integer pAgdSeq, Date pDtAgenda) {
		boolean temCirurgia = false;
		
		Long count = getBlocoCirurgicoFacade().verificarSeEscalaPortalAgendamentoTemCirurgia(pAgdSeq, pDtAgenda);
		
		if (count > 0){
			temCirurgia = true;
		}
		return temCirurgia;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}
	
}