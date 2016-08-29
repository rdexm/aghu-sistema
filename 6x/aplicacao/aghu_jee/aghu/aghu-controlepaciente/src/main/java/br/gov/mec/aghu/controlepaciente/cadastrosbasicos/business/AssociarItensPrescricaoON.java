package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoEnfDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoMedicoDAO;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoEnf;
import br.gov.mec.aghu.model.EcpItemCtrlCuidadoMedico;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AssociarItensPrescricaoON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AssociarItensPrescricaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EcpItemCtrlCuidadoMedicoDAO ecpItemCtrlCuidadoMedicoDAO;
	
	@Inject
	private EcpItemControleDAO ecpItemControleDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EcpItemCtrlCuidadoEnfDAO ecpItemCtrlCuidadoEnfDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8277383432862292139L;

	public enum AssociarItensPrescricaoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ITEM_CONTROLE_INVALIDO;
	}

	/**
	 * Carregar o item de controle a partir da seq
	 * @param fetchArgsInnerJoin 
	 * @param fetchArgsLeftJoin 
	 */
	public EcpItemControle obterItemControle(Short seqItemControle, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return getItemControleDAO().obterPorChavePrimaria(seqItemControle, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	/**
	 * Salvar a associação dos cuidados de enfermagem com o item de controle.
	 * 
	 * @param itemControle
	 * @param listaCuidadosIncluir
	 * @param listaCuidadosExcluir
	 */
	public void salvarAssociacaoCuidadosEnfermagem(EcpItemControle itemControle,
			List<EpeCuidados> listaCuidadosIncluir,
			List<EpeCuidados> listaCuidadosExcluir) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (itemControle == null) {
			throw new IllegalArgumentException("Parâmetros inválidos");
		}

		EcpItemCtrlCuidadoEnf itemCtrlCuidadoEnf = null;
		for (EpeCuidados cuidado : listaCuidadosIncluir) {
			itemCtrlCuidadoEnf = new EcpItemCtrlCuidadoEnf();
			itemCtrlCuidadoEnf.setCriadoEm(Calendar.getInstance().getTime());
			itemCtrlCuidadoEnf.setCuidado(cuidado);
			itemCtrlCuidadoEnf.setItemControle(itemControle);
			itemCtrlCuidadoEnf.setServidor(servidorLogado);

			this.getItemCtrlCuidadoEnfDAO().persistir(itemCtrlCuidadoEnf);
			this.getItemCtrlCuidadoEnfDAO().flush();
		}

		for (EpeCuidados cuidado : listaCuidadosExcluir) {
			itemCtrlCuidadoEnf = this.getItemCtrlCuidadoEnfDAO()
					.obterCuidadoEnfPorItemControleECuidado(itemControle,
							cuidado);
			if (itemCtrlCuidadoEnf != null) {
				this.getItemCtrlCuidadoEnfDAO().remover(itemCtrlCuidadoEnf);
				this.getItemCtrlCuidadoEnfDAO().flush();
			}
		}
	}

	
	/**
	 * Salvar a associação dos cuidados de enfermagem com o item de controle.
	 * 
	 * @param itemControle
	 * @param listaCuidadosIncluir
	 * @param listaCuidadosExcluir
	 */
	public void salvarAssociacaoCuidadosMedicos(EcpItemControle itemControle,
			List<MpmCuidadoUsual> listaCuidadosIncluir,
			List<MpmCuidadoUsual> listaCuidadosExcluir) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (itemControle == null) {
			throw new IllegalArgumentException("Parâmetros inválidos");
		}

		EcpItemCtrlCuidadoMedico itemCtrlCuidadoMedico = null;
		for (MpmCuidadoUsual cuidado : listaCuidadosIncluir) {
			
			itemCtrlCuidadoMedico = new EcpItemCtrlCuidadoMedico();
			itemCtrlCuidadoMedico.setCriadoEm(Calendar.getInstance().getTime());
			itemCtrlCuidadoMedico.setCuidadoUsual(cuidado);
			itemCtrlCuidadoMedico.setItemControle(itemControle);
			itemCtrlCuidadoMedico.setServidor(servidorLogado);			
			
			this.getItemCtrlCuidadoMedicoDAO().persistir(itemCtrlCuidadoMedico);
			this.getItemCtrlCuidadoMedicoDAO().flush();
		}

		for (MpmCuidadoUsual cuidado : listaCuidadosExcluir) {
			itemCtrlCuidadoMedico = this.getItemCtrlCuidadoMedicoDAO()
					.obterCuidadoMedicoPorItemControleECuidado(itemControle,
							cuidado);
			
			if (itemCtrlCuidadoMedico != null) {
				this.getItemCtrlCuidadoMedicoDAO().remover(itemCtrlCuidadoMedico);
				this.getItemCtrlCuidadoMedicoDAO().flush();
			}
		}
	}
	
	protected EcpItemControleDAO getItemControleDAO(){
		return ecpItemControleDAO;
	}
	
	protected EcpItemCtrlCuidadoEnfDAO getItemCtrlCuidadoEnfDAO(){
		return ecpItemCtrlCuidadoEnfDAO;
	}
	
	protected EcpItemCtrlCuidadoMedicoDAO getItemCtrlCuidadoMedicoDAO(){
		return ecpItemCtrlCuidadoMedicoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
