package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiaAnotacaoDAO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgiaAnotacao;
import br.gov.mec.aghu.model.MbcCirurgiaAnotacaoId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade
 * MbcCirurgiaAnotacao.
 * 
 * @author ihaas
 * 
 */
@Stateless
public class MbcCirurgiaAnotacaoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCirurgiaAnotacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiaAnotacaoDAO mbcCirurgiaAnotacaoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private ICentroCustoFacade iCentroCustoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -639550022022971803L;

	/**
	 * Insere instância de MbcCirurgiaAnotacao.
	 * 
	 * @param anotacao
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void persistirCirurgiaAnotacao(MbcCirurgiaAnotacao anotacao,
			Integer mbcCirurgiaCodigo)
			throws ApplicationBusinessException {

		executarAntesDeInserir(anotacao);
		
		// Efetua load de MbcCirurgias através do código recebido por parâmetro via page.
		MbcCirurgias cirurgia = this.getBlocoCirurgicoFacade().obterCirurgiaPorSeq(mbcCirurgiaCodigo);
		anotacao.setMbcCirurgias(cirurgia);
		
		MbcCirurgiaAnotacaoId id = new MbcCirurgiaAnotacaoId();
		id.setCrgSeq(anotacao.getMbcCirurgias().getSeq());
		
		Double seqp = getMbcCirurgiaAnotacaoDAO().obterCirurgiaAnotacaoMaxSeqp(anotacao.getMbcCirurgias().getSeq());
		
		if (seqp == null) {
			
			seqp = Double.valueOf(1);
			id.setSeqp(seqp);
			
		} else {
			
			id.setSeqp(seqp + 1);
			
		}
		
		anotacao.setId(id);
		getMbcCirurgiaAnotacaoDAO().persistir(anotacao);
		
	}

	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_CRA_BRI
	 * 
	 * @param anestesiaDescricao
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesDeInserir(MbcCirurgiaAnotacao anotacao)
			throws ApplicationBusinessException {

		anotacao.setCriadoEm(new Date());
		anotacao.setRapServidores(getRegistroColaboradorFacade()
				.obterServidorPorUsuario(servidorLogadoFacade.obterServidorLogado().getUsuario()));
		
		atualizarFccCentroCustos(anotacao, servidorLogadoFacade.obterServidorLogado().getUsuario());

	}

	private MbcCirurgiaAnotacaoDAO getMbcCirurgiaAnotacaoDAO() {
		return mbcCirurgiaAnotacaoDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.iCentroCustoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return this.iBlocoCirurgicoFacade;
	}

	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_CRA_RN.RN_CRAP_ATU_CCUSTO
	 * 
	 * @param anotacao
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarFccCentroCustos(MbcCirurgiaAnotacao anotacao,
			String obterLoginUsuarioLogado) throws ApplicationBusinessException  {

		List<FccCentroCustos> listResult = new ArrayList<FccCentroCustos>();
		listResult = this.getCentroCustoFacade().pesquisarCentroCustosServidor(null);
		
		if(listResult != null) {
			anotacao.setFccCentroCustos(listResult.get(0));
		}
	}
}
