package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatValorDiariaInternacaoDAO;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class FatValorDiariaInternacaoRN extends BaseBusiness implements	Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6587440068475280629L;

	private static final Log LOG = LogFactory.getLog(FatValorDiariaInternacaoRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatValorDiariaInternacaoDAO fatValorDiariainternacaoDAO;
	
	/**
	 * 
	 * @param fatValorDiariaInternacao
	 * @throws ApplicationBusinessException
	 * persistir valor de diarias
	 */
	
	public void persistirValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException{	
		
		fatValorDiariaInternacao.setAlteradoEm(new Date());
		fatValorDiariaInternacao.setAlteradoPor(this.obterLoginUsuarioLogado());
		fatValorDiariaInternacao.setCriadoEm(new Date());
		fatValorDiariaInternacao.setCriadoPor(this.obterLoginUsuarioLogado());
		
		/**
		 * lançar exceção caso já exista valor de diaria cadastrado com mesma data inicial
		 */
		if(fatValorDiariainternacaoDAO.obterPorChavePrimaria(fatValorDiariaInternacao.getId()) != null){
			throw new ApplicationBusinessException("ERRO_DIARIA_JA_CADASTRADA",Severity.ERROR);
		}
		/**
		 * Lançar exceção caso não exista nenhuma diaria cadastrada
		 */
				
		if(fatValorDiariaInternacao.getFatDiariaInternacao().getSeq() == null){
			throw new ApplicationBusinessException("ERROR_ADICIONAR_VALOR_DIARIA",Severity.ERROR);
		}
		
		/**
		 * Verifica se a data inical é menor que a data final
		 */
		if(fatValorDiariaInternacao.getDataFimValidade() != null){
			int result = fatValorDiariaInternacao.getId().getDataInicioValidade().compareTo(fatValorDiariaInternacao.getDataFimValidade());
			if(result > 0){
				throw new ApplicationBusinessException("ERRO_DATA_INICIO_MAIOR_DATA_FIM",Severity.ERROR);
			}
		}
		
		if(fatValorDiariaInternacao.getId().getDinSeq() != null){
			this.fatValorDiariainternacaoDAO.persistir(fatValorDiariaInternacao);
		}
	}

	public void alterarValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) throws ApplicationBusinessException{	
		
		fatValorDiariaInternacao.setAlteradoEm(new Date());
		fatValorDiariaInternacao.setAlteradoPor(this.obterLoginUsuarioLogado());
		
		/**
		 * Lançar exceção caso não exista nenhuma diaria cadastrada
		 */
		if(fatValorDiariaInternacao.getFatDiariaInternacao().getSeq() == null){
			throw new ApplicationBusinessException("CADASTRO_DIARIA_ERROR_DATA",Severity.ERROR);
		}
		
		if(fatValorDiariaInternacao.getDataFimValidade() != null){
			int result = fatValorDiariaInternacao.getId().getDataInicioValidade().compareTo(fatValorDiariaInternacao.getDataFimValidade());
			if(result > 0){
				throw new ApplicationBusinessException("ERRO_DATA_INICIO_MAIOR_DATA_FIM",Severity.ERROR);
			}
		}
			
		this.fatValorDiariainternacaoDAO.merge(fatValorDiariaInternacao);
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
