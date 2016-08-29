package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.MbcAgendaHistorico;
import br.gov.mec.aghu.model.MbcAgendaHistoricoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class MbcAgendaHistoricoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaHistoricoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	
	@Inject
	private MbcAgendaHistoricoDAO mbcAgendaHistoricoDAO;



	private static final long serialVersionUID = -8551269382277162985L;


	/**
	 * @ORADB MBCK_AHI_RN.RN_AHIP_INCLUI
	 * @param agdSeq
	 * @param situacaoAgenda
	 * @param origem
	 * @param descricaoHistorico
	 * @param operacaoHistorico
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserir(Integer agdSeq, DominioSituacaoAgendas situacaoAgenda, DominioOrigem origem,
			String descricaoHistorico, DominioOperacaoAgenda operacaoHistorico) throws BaseException {
		MbcAgendaHistorico historico = new MbcAgendaHistorico();
		MbcAgendaHistoricoId id = new MbcAgendaHistoricoId();
		id.setAgdSeq(agdSeq);
		
		// TODO arquitetura: quando resolver o lance do contexto ejb, tem que voltar esse trecho
		//Double seq = (Double) obterDoContexto(Double.class, "AGENDA_SEQP_HIST");
		
		Double seq = null;
		//if (seq != null) {
		//	seq++;
		//	super.atribuirContextoConversacao("AGENDA_SEQP_HIST", seq);
		//} else {
			seq = getMbcAgendaHistoricoDAO().buscarProximoSeqp(agdSeq);
			seq++;
		//}
		
		id.setSeqp(seq);
		historico.setIndSitAgenda(situacaoAgenda);
		historico.setOrigem(origem);
		historico.setId(id);
		historico.setDescricao(descricaoHistorico);
		historico.setOperacao(operacaoHistorico);
		
		preInserir(historico);
		this.getMbcAgendaHistoricoDAO().persistir(historico);
	}
	
	
	/**
	 * @ORADB MBCT_AHI_BRI
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MbcAgendaHistorico mbcAgendaHistorico) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mbcAgendaHistorico.setCriadoEm(new Date());
		mbcAgendaHistorico.setRapServidores(servidorLogado);
	}
	
	
	protected MbcAgendaHistoricoDAO getMbcAgendaHistoricoDAO() {
		return mbcAgendaHistoricoDAO;
	}
	
}