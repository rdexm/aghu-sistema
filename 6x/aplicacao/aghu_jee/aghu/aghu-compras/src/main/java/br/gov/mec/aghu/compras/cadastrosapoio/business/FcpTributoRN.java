package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.compras.dao.FcpTributoDAO;
import br.gov.mec.aghu.compras.dao.FcpTributoJnDAO;
import br.gov.mec.aghu.estoque.dao.FcpValorTributosDAO;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoAliquotaJn;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class FcpTributoRN extends BaseBusiness {

	private static final long serialVersionUID = 5363078226437771845L;
	
	@Inject
	private FcpTributoDAO fcpTributoDAO;
	
	@Inject
	private FcpTributoJnDAO fcpTributoJnDAO;
	
	@Inject
	private FcpValorTributosDAO fcpValorTributosDao;
	
	@Inject
	private RapServidoresDAO rapServidoresDao;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	protected Log getLogger() {
		return null;
	}
	
	public List<FcpRetencaoAliquota> obterListaTributo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		Integer codigo = null;
		if(fcpRetencaoTributo != null){
			codigo = fcpRetencaoTributo.getCodigo();
		}
		return this.fcpTributoDAO.obterListaCodigoTributo(firstResult, maxResult, orderProperty, asc, codigo);
	}
	
	public Long obterCountTributo(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		Integer codigo = null;
		if(fcpRetencaoTributo != null){
			codigo = fcpRetencaoTributo.getCodigo();
		}
		return this.fcpTributoDAO.obterCountCodigoTributo(codigo);
	}

	public void inserirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		
		validaRangeData(fcpRetencaoAliquota);
		
		//verifica se já existe alguma aliquota com Codigo de Recolhimento x Imposto x DtInicial já cadastrado, caso haja ele seta numero + 1
		List<FcpRetencaoAliquota> listaRetencaoAliquotas = fcpTributoDAO.pesquisarRetencaoAliquotaPorFriCodigoImpDtInicial(fcpRetencaoAliquota);
		
		if (listaRetencaoAliquotas.size() == 0) {
			
			fcpRetencaoAliquota.getId().setNumero((short) 1);
			
		} else {
			
			fcpRetencaoAliquota.getId().setNumero((short) (listaRetencaoAliquotas.size() + 1));
		}
		
		fcpTributoDAO.persistirRetencaoAliquota(fcpRetencaoAliquota);
	}

	public void atualizarRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		// Atualizar retenção aliquota
		validaRangeData(fcpRetencaoAliquota);	
		this.fcpTributoDAO.atualizarRetencaoAliquota(fcpRetencaoAliquota);
		// Atualizar journal retenção aliquota
		final FcpRetencaoAliquota fcpRetencaoAliquotaOriginal = this.fcpTributoDAO.obterOriginal(fcpRetencaoAliquota);
		this.posAtualizarRetencaoAliquotaJournal(fcpRetencaoAliquota, fcpRetencaoAliquotaOriginal);
	}

	public void excluirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {	
		if (fcpValorTributosDao.verificarExclusao(fcpRetencaoAliquota)) {		
			// Ecluir retenção aliquota
			this.fcpTributoDAO.excluirRetencaoAliquota(fcpRetencaoAliquota);
			// Atualizar journal retenção aliquota
			this.posExcluirRetencaoAliquotaJournal(fcpRetencaoAliquota);
		} else {
			throw new ApplicationBusinessException("Tributo está sendo utilizado no sistema e não pode ser excluído.", br.gov.mec.aghu.core.exception.Severity.ERROR);
		}		
	}
	
	public void validaRangeData(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {
		  
		  if(fcpRetencaoAliquota != null && fcpRetencaoAliquota.getId() != null){
		   Date dataInicio = fcpRetencaoAliquota.getId().getDtInicioValidade();
		   Date dataFim = fcpRetencaoAliquota.getDtFinalValidade();
		   if(dataInicio != null && dataFim != null){
		    if(dataFim.before(dataInicio)){
		     throw new ApplicationBusinessException("Data inicial deve ser menor que a data final.", br.gov.mec.aghu.core.exception.Severity.ERROR);
		    }
		   }
		  }
		  
		 }
	
	public RapServidores obterRapServidor(Usuario usuario) throws BaseException {
		
		return rapServidoresDao.obterServidorAtivoPorUsuario(usuario.getLogin());
	}
	
	/**
	 * Inserir journal após o update.
	 * @param newFcpRetencaoAliquota
	 * @param oldFcpRetencaoAliquota
	 * @throws ApplicationBusinessException
	 */
	public void posAtualizarRetencaoAliquotaJournal(FcpRetencaoAliquota newFcpRetencaoAliquota, FcpRetencaoAliquota oldFcpRetencaoAliquota) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (CoreUtil.modificados(newFcpRetencaoAliquota.getId().getFriCodigo(), oldFcpRetencaoAliquota.getId().getFriCodigo())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getId().getImposto(), oldFcpRetencaoAliquota.getId().getImposto())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getId().getNumero(), oldFcpRetencaoAliquota.getId().getNumero())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getDescricao(), oldFcpRetencaoAliquota.getDescricao())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getAliquota(), oldFcpRetencaoAliquota.getAliquota())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getCriadoEm(), oldFcpRetencaoAliquota.getCriadoEm())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getRapServidores().getServidor().getId().getMatricula(), oldFcpRetencaoAliquota.getRapServidores().getServidor().getId().getMatricula())
				|| CoreUtil.modificados(newFcpRetencaoAliquota.getRapServidores().getServidor().getId().getVinCodigo(), oldFcpRetencaoAliquota.getRapServidores().getServidor().getId().getVinCodigo())
		) {
			
			FcpRetencaoAliquotaJn retencaoAliquotaJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, FcpRetencaoAliquotaJn.class, (servidorLogado != null ? servidorLogado.getUsuario() : null));
			
			retencaoAliquotaJn.setFriCodigo(oldFcpRetencaoAliquota.getId().getFriCodigo());
			retencaoAliquotaJn.setImposto(oldFcpRetencaoAliquota.getId().getImposto());
			retencaoAliquotaJn.setNumero(oldFcpRetencaoAliquota.getId().getNumero());
			retencaoAliquotaJn.setDescricao(oldFcpRetencaoAliquota.getDescricao());
			retencaoAliquotaJn.setAliquota(oldFcpRetencaoAliquota.getAliquota());
			retencaoAliquotaJn.setCriadoEm(oldFcpRetencaoAliquota.getCriadoEm());
			retencaoAliquotaJn.setAlteradoEm(oldFcpRetencaoAliquota.getAlteradoEm());
			retencaoAliquotaJn.setSerMatricula(oldFcpRetencaoAliquota.getRapServidores().getServidor().getId().getMatricula());
			retencaoAliquotaJn.setSerVinCodigo(oldFcpRetencaoAliquota.getRapServidores().getServidor().getId().getVinCodigo());
			
			this.fcpTributoJnDAO.persistir(retencaoAliquotaJn);
		}
	}
	
	/**
	 * Inserir journal após o delete.
	 * @param fcpRetencaoAliquota
	 * @throws ApplicationBusinessException
	 */
	public void posExcluirRetencaoAliquotaJournal(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FcpRetencaoAliquotaJn retencaoAliquotaJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, FcpRetencaoAliquotaJn.class, (servidorLogado != null ? servidorLogado.getUsuario() : null));
		
		retencaoAliquotaJn.setFriCodigo(fcpRetencaoAliquota.getId().getFriCodigo());
		retencaoAliquotaJn.setImposto(fcpRetencaoAliquota.getId().getImposto());
		retencaoAliquotaJn.setNumero(fcpRetencaoAliquota.getId().getNumero());
		retencaoAliquotaJn.setDescricao(fcpRetencaoAliquota.getDescricao());
		retencaoAliquotaJn.setAliquota(fcpRetencaoAliquota.getAliquota());
		retencaoAliquotaJn.setCriadoEm(fcpRetencaoAliquota.getCriadoEm());
		retencaoAliquotaJn.setAlteradoEm(fcpRetencaoAliquota.getAlteradoEm());
		retencaoAliquotaJn.setSerMatricula(fcpRetencaoAliquota.getRapServidores().getServidor().getId().getMatricula());
		retencaoAliquotaJn.setSerVinCodigo(fcpRetencaoAliquota.getRapServidores().getServidor().getId().getVinCodigo());
		
		this.fcpTributoJnDAO.persistir(retencaoAliquotaJn);
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	/**
	 * @return the fcpTributoDAO
	 */
	public FcpTributoDAO getFcpTributoDAO() {
		return fcpTributoDAO;
	}

	/**
	 * @param fcpTributoDAO the fcpTributoDAO to set
	 */
	public void setFcpTributoDAO(FcpTributoDAO fcpTributoDAO) {
		this.fcpTributoDAO = fcpTributoDAO;
	}

	/**
	 * @return the fcpTributoJnDAO
	 */
	public FcpTributoJnDAO getFcpTributoJnDAO() {
		return fcpTributoJnDAO;
	}

	/**
	 * @param fcpTributoJnDAO the fcpTributoJnDAO to set
	 */
	public void setFcpTributoJnDAO(FcpTributoJnDAO fcpTributoJnDAO) {
		this.fcpTributoJnDAO = fcpTributoJnDAO;
	}

}
