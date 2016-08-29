package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCnesUfDAO;
import br.gov.mec.aghu.model.FatCnesUf;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FatCnesUfRN extends BaseBusiness {

	private static final long serialVersionUID = -5845734082065479022L;

	private static final Log LOG = LogFactory.getLog(FatCnesUfRN.class);
	
	@Inject
	private FatCnesUfDAO fatCnesUfDAO;
	
	@EJB	
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	// I1
	public void inserirFatCnesUf(FatCnesUf entity) {
		entity.setServidor(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		fatCnesUfDAO.persistir(entity);
	}

	// E1
	public void deletarFatCnesUf(Short unfSeq, Integer fcsSeq, Short cnesSeq) {
		List<FatCnesUf> itens = fatCnesUfDAO.obterFatCnesUfPorUnfSeqFcsSeq(unfSeq, fcsSeq, cnesSeq);
		for (FatCnesUf item : itens) {
			fatCnesUfDAO.remover(item);
		}
	}
}
