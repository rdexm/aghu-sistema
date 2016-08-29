package br.gov.mec.aghu.internacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MovimentosInternacaoON extends BaseBusiness {


@EJB
private MovimentoInternacaoRN movimentoInternacaoRN;

private static final Log LOG = LogFactory.getLog(MovimentosInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973695704002333770L;

	public void persistirMovimentoInternacao(
			AinMovimentosInternacao movimentoInternacao)
			throws ApplicationBusinessException {
		if (movimentoInternacao.getSeq() == null) {
			incluirMovimentoInternacao(movimentoInternacao);
		} else {
			alterarMovimentoInternacao(movimentoInternacao);
		}
	}

	
	private void incluirMovimentoInternacao(
			AinMovimentosInternacao movimentoInternacao)
			throws ApplicationBusinessException {
		this.getMovimentoInternacaoRN().validarMovimentoInternacao(movimentoInternacao);

		AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = this.getAinMovimentoInternacaoDAO();
		ainMovimentoInternacaoDAO.persistir(movimentoInternacao);
		ainMovimentoInternacaoDAO.flush();
	}

	
	private void alterarMovimentoInternacao(
			AinMovimentosInternacao movimentoInternacao)
			throws ApplicationBusinessException {
		AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = this.getAinMovimentoInternacaoDAO();
		ainMovimentoInternacaoDAO.atualizar(movimentoInternacao);
		ainMovimentoInternacaoDAO.flush();
	}

	protected MovimentoInternacaoRN getMovimentoInternacaoRN(){
		return movimentoInternacaoRN;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO(){
		return ainMovimentoInternacaoDAO;
	}
	
}
