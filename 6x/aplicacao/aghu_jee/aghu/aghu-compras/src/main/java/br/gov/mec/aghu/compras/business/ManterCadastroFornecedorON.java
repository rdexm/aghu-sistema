package br.gov.mec.aghu.compras.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ManterCadastroFornecedorON extends BaseBusiness implements Serializable{

	@EJB
	private ManterFornecedorRN manterFornecedorRN;
	
	private static final Log LOG = LogFactory.getLog(ManterCadastroFornecedorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	private static final long serialVersionUID = -2035774151279595685L;

	public enum ManterCadastroFornecedorONExceptionCode implements BusinessExceptionCode {
		SCO_00099, 
		SCO_00098,
		SCO_00097,
	    SCO_00096,
	    SCO_00256,
	    ERRO_CRC_SOMENTE_FORNECEDOR_NACIONAL,
	    ERRO_CLASSIFICACAO_ECONOMICA_SOMENTE_FORN_NACIONAL;
	}
	
	/**
	 * Pesquisa fornecedor por vários campos de fornecedor que estejam preenchidos.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fornecedor
	 * @return
	 * @author bruno.mourao
	 * @since 08/03/2012
	 */
	public List<ScoFornecedor> pesquisarFornecedorCompleta(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc, ScoFornecedor fornecedor, String cpfCnpj){
		return getScoFornecedorDAO().pesquisarFornecedorCompleta(firstResult, maxResult, orderProperty, asc, fornecedor, cpfCnpj);
	}


	protected ScoFornecedorDAO getScoFornecedorDAO() {		
		return scoFornecedorDAO;
	}
	
	public Long pesquisarFornecedorCompletaCount(ScoFornecedor fornecedor, String cpfCnpj){
		return getScoFornecedorDAO().pesquisarFornecedorCompletaCount(fornecedor, cpfCnpj);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void persistirFornecedor(ScoFornecedor fornecedor) throws ApplicationBusinessException, BaseListException{
		
		// armazena msgs de erro
		BaseListException listaExcept = new BaseListException();
		
		//RN8
		if(fornecedor.getCrc() != null || fornecedor.getDtValidadeCrc() != null){
			if(!(fornecedor.getCrc() != null && fornecedor.getDtValidadeCrc() != null) || fornecedor.getCrc()  == null){				
				listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.SCO_00096, new Object[]{}));
			}
		}
		
		//RN9
		if(!(fornecedor.getCrc() == null || fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA))){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.ERRO_CRC_SOMENTE_FORNECEDOR_NACIONAL, new Object[]{}));
		}
		
		//RN10
		if(!(fornecedor.getClassificacaoEconomica() == null || fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA))){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.ERRO_CLASSIFICACAO_ECONOMICA_SOMENTE_FORN_NACIONAL, new Object[]{}));
		}
		
		//RN11
		if(!((fornecedor.getInscricaoEstadual() == null || StringUtils.isEmpty(fornecedor.getInscricaoEstadual())) || fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA))){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.SCO_00098, new Object[]{}));
		}
		else if(fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNE)){
			fornecedor.setInscricaoEstadual(null);
		}
		
		//RN12 
		if(fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA) && fornecedor.getCnpjCpf() == null){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.SCO_00256, new Object[]{}));
		}

		//RN14
		if(!fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA) && fornecedor.getCpf() != null){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.SCO_00099, new Object[]{}));
		}
		
		//RN15
		if(!fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNA) && fornecedor.getCgc() != null){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorONExceptionCode.SCO_00097, new Object[]{}));
		}
		
		if(listaExcept.hasException()){
			throw listaExcept;
		}
		
		//registrar Servidor que fez inclusão/alteração
		registrarServidor(fornecedor);
		
		getManterFornecedorRN().persistirScoFornecedor(fornecedor, true);
		
	}


	private void registrarServidor(ScoFornecedor fornecedor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(fornecedor.getNumero() == null){
			//inserção
			fornecedor.setServidor(servidorLogado);
			fornecedor.setDtCadastramento(new Date());
		}
		else{
			//Alteração
			fornecedor.setServidorAtuCadastro(servidorLogado);
			fornecedor.setDtAlteracao(new Date());
		}
	}

	protected ManterFornecedorRN getManterFornecedorRN() {
		return manterFornecedorRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
