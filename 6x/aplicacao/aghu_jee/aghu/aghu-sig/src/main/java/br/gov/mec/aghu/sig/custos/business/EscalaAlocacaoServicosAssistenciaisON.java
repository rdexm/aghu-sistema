package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigEscalaPessoa;
import br.gov.mec.aghu.sig.dao.SigEscalaPessoaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EscalaAlocacaoServicosAssistenciaisON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EscalaAlocacaoServicosAssistenciaisON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigEscalaPessoaDAO sigEscalaPessoaDAO;

	private static final long serialVersionUID = 3709061791627610083L;
	
	public enum EscalaAlocacaoServicosAssistenciaisONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_ERRO_SOMA_PERCENTUAIS_ESCALAS;
	}

	public boolean pesquisarEscalaPessoasPorCentroDeCusto(FccCentroCustos centroCustos) {
		List<SigEscalaPessoa> listaEscalaPessoas = this.getSigEscalaPessoaDAO().pesquisarEscalaPessoasPorCentroDeCusto(centroCustos);

		Integer percentual = 0;
		for (SigEscalaPessoa escalaPessoa : listaEscalaPessoas) {
			percentual = percentual + escalaPessoa.getPercentual();
		}

		if (percentual == 100) {
			return true;
		}
		return false;
	}
	
	public void verificarEscalaPessoasPorCentroDeCusto(FccCentroCustos centroCustos, SigEscalaPessoa escala) throws ApplicationBusinessException {
		List<SigEscalaPessoa> listaEscalaPessoas = this.getSigEscalaPessoaDAO().pesquisarEscalaPessoasPorCentroDeCusto(centroCustos);

		Integer percentual = 0;
		Integer percentualAtual = 0;
		for (SigEscalaPessoa escalaPessoa : listaEscalaPessoas) {
			percentualAtual = percentualAtual + escalaPessoa.getPercentual();
			if(escala.getSeq()!=null && escala.getSeq().equals(escalaPessoa.getSeq())){
				percentual = percentual + escala.getPercentual();
			} else {
				percentual = percentual + escalaPessoa.getPercentual();
			}
		}
		if(escala.getSeq()==null){
			percentual = percentual + escala.getPercentual();
		}

		if (percentual > 100) {
			throw new ApplicationBusinessException(EscalaAlocacaoServicosAssistenciaisONExceptionCode.MENSAGEM_ERRO_SOMA_PERCENTUAIS_ESCALAS,centroCustos.getNomeReduzido(), percentualAtual);
		}
	}


	protected SigEscalaPessoaDAO getSigEscalaPessoaDAO() {
		return sigEscalaPessoaDAO;
	}

}
