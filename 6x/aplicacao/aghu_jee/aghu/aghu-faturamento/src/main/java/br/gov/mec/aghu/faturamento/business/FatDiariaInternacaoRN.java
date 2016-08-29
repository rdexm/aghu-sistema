package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatDiariaInternacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorDiariaInternacaoDAO;
import br.gov.mec.aghu.model.FatDiariaInternacao;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class FatDiariaInternacaoRN extends BaseBusiness implements	Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8804519230905677209L;

	private static final Log LOG = LogFactory.getLog(FatDiariaInternacaoRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatDiariaInternacaoDAO fatDiariainternacaoDAO;
	
	@Inject
	private FatValorDiariaInternacaoDAO fatValorDiariainternacaoDAO;
	
	private List<FatValorDiariaInternacao> listaValoresDiarias;
	
	public void persistirDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{	
		
		fatDiariaInternacao.setAlteradoEm(new Date());
		fatDiariaInternacao.setAlteradoPor(this.obterLoginUsuarioLogado());
		fatDiariaInternacao.setCriadoEm(new Date());
		fatDiariaInternacao.setCriadoPor(this.obterLoginUsuarioLogado());
		
		this.fatDiariainternacaoDAO.persistir(fatDiariaInternacao);
		
	}
	public void alterarDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{	
		
		fatDiariaInternacao.setAlteradoEm(new Date());
		fatDiariaInternacao.setAlteradoPor(this.obterLoginUsuarioLogado());
		
		this.fatDiariainternacaoDAO.merge(fatDiariaInternacao);
	}
	
	/**
	 * 
	 * @param fatDiariaInternacao
	 * @throws ApplicationBusinessException
	 * remover a diaria caso não exista valores de diarias atribuidos a ele
	 */
	public void removerDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) throws ApplicationBusinessException{
		listaValoresDiarias = new ArrayList<FatValorDiariaInternacao>();
		listaValoresDiarias = fatValorDiariainternacaoDAO.obterListaValorDiaria(fatDiariaInternacao.getSeq());
		
		if(!listaValoresDiarias.isEmpty()){
			throw new ApplicationBusinessException("CADASTRO_DIARIA_ERROR_EXCLUSAO",Severity.ERROR);
		}
		
		fatDiariainternacaoDAO.removerDiariaInternacao(fatDiariaInternacao);
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	public FatValorDiariaInternacaoDAO getFatValorDiariainternacaoDAO() {
		return fatValorDiariainternacaoDAO;
	}
	public void setFatValorDiariainternacaoDAO(
			FatValorDiariaInternacaoDAO fatValorDiariainternacaoDAO) {
		this.fatValorDiariainternacaoDAO = fatValorDiariainternacaoDAO;
	}
	
}
