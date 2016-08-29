package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AnexarDocumentoLaudoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AnexarDocumentoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelDocResultadoExameDAO aelDocResultadoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3377310635293142363L;

	
	/**
	 * Anexa Documento Laudo
	 * @param doc
	 * @param unidadeExecutora
	 * @throws BaseException
	 */
	public void anexarDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Seta a anulacao do documento para falso
		doc.setIndAnulacaoDoc(false);
		
		// Atualiza o servidor que esta criando de acordo com o usuario logado
		doc.setServidor(servidorLogado);
		
		// Seta "criado em" com a data e hora atual
		doc.setCriadoEm(new Date());
		
		// Inserir no banco o documento de laudo
		this.getAelDocResultadoExameDAO().persistir(doc);
		
	}

	/**
	 * Remove Documento Laudo
	 * @param doc
	 * @param unidadeExecutora
	 * @throws BaseException
	 */
	public void removerDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// Seta o indicador de anulacao de documento para verdadeiro 
		doc.setIndAnulacaoDoc(Boolean.TRUE);
		
		// Atualiza o servidor anulacao de acordo com o usuario logado
		doc.setServidorAnulacao(servidorLogado);
		
		// Seta "data e hora" da anulacao com a data e hora atual
		doc.setDthrAnulacaoDoc(new Date());
		
		// Atualiza (delete) documento de laudo no banco
		this.getAelDocResultadoExameDAO().atualizar(doc);
	}
	
	/*
	 * Dependências
	 */
	
	protected AelDocResultadoExameDAO getAelDocResultadoExameDAO(){
		return aelDocResultadoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
